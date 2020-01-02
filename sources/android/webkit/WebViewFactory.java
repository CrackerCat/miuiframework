package android.webkit;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Trace;
import android.util.AndroidRuntimeException;
import android.util.ArraySet;
import android.util.Log;
import android.webkit.IWebViewUpdateService.Stub;
import java.io.File;
import java.lang.reflect.Method;

@SystemApi
public final class WebViewFactory {
    private static final String CHROMIUM_WEBVIEW_FACTORY = "com.android.webview.chromium.WebViewChromiumFactoryProviderForQ";
    private static final String CHROMIUM_WEBVIEW_FACTORY_METHOD = "create";
    private static final boolean DEBUG = false;
    public static final int LIBLOAD_ADDRESS_SPACE_NOT_RESERVED = 2;
    public static final int LIBLOAD_FAILED_JNI_CALL = 7;
    public static final int LIBLOAD_FAILED_LISTING_WEBVIEW_PACKAGES = 4;
    public static final int LIBLOAD_FAILED_TO_FIND_NAMESPACE = 10;
    public static final int LIBLOAD_FAILED_TO_LOAD_LIBRARY = 6;
    public static final int LIBLOAD_FAILED_TO_OPEN_RELRO_FILE = 5;
    public static final int LIBLOAD_FAILED_WAITING_FOR_RELRO = 3;
    public static final int LIBLOAD_FAILED_WAITING_FOR_WEBVIEW_REASON_UNKNOWN = 8;
    public static final int LIBLOAD_SUCCESS = 0;
    public static final int LIBLOAD_WRONG_PACKAGE_NAME = 1;
    private static final String LOGTAG = "WebViewFactory";
    private static String WEBVIEW_UPDATE_SERVICE_NAME = "webviewupdate";
    private static String sDataDirectorySuffix;
    @UnsupportedAppUsage
    private static PackageInfo sPackageInfo;
    @UnsupportedAppUsage
    private static WebViewFactoryProvider sProviderInstance;
    private static final Object sProviderLock = new Object();
    private static boolean sWebViewDisabled;
    private static Boolean sWebViewSupported;

    static class MissingWebViewPackageException extends Exception {
        public MissingWebViewPackageException(String message) {
            super(message);
        }

        public MissingWebViewPackageException(Exception e) {
            super(e);
        }
    }

    private static String getWebViewPreparationErrorReason(int error) {
        if (error == 3) {
            return "Time out waiting for Relro files being created";
        }
        if (error == 4) {
            return "No WebView installed";
        }
        if (error != 8) {
            return "Unknown";
        }
        return "Crashed for unknown reason";
    }

    private static boolean isWebViewSupported() {
        if (sWebViewSupported == null) {
            sWebViewSupported = Boolean.valueOf(AppGlobals.getInitialApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WEBVIEW));
        }
        return sWebViewSupported.booleanValue();
    }

    static void disableWebView() {
        synchronized (sProviderLock) {
            if (sProviderInstance == null) {
                sWebViewDisabled = true;
            } else {
                throw new IllegalStateException("Can't disable WebView: WebView already initialized");
            }
        }
    }

    static void setDataDirectorySuffix(String suffix) {
        synchronized (sProviderLock) {
            if (sProviderInstance != null) {
                throw new IllegalStateException("Can't set data directory suffix: WebView already initialized");
            } else if (suffix.indexOf(File.separatorChar) < 0) {
                sDataDirectorySuffix = suffix;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Suffix ");
                stringBuilder.append(suffix);
                stringBuilder.append(" contains a path separator");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
    }

    static String getDataDirectorySuffix() {
        String str;
        synchronized (sProviderLock) {
            str = sDataDirectorySuffix;
        }
        return str;
    }

    public static String getWebViewLibrary(ApplicationInfo ai) {
        if (ai.metaData != null) {
            return ai.metaData.getString("com.android.webview.WebViewLibrary");
        }
        return null;
    }

    public static PackageInfo getLoadedPackageInfo() {
        PackageInfo packageInfo;
        synchronized (sProviderLock) {
            packageInfo = sPackageInfo;
        }
        return packageInfo;
    }

    public static Class<WebViewFactoryProvider> getWebViewProviderClass(ClassLoader clazzLoader) throws ClassNotFoundException {
        return Class.forName(CHROMIUM_WEBVIEW_FACTORY, true, clazzLoader);
    }

    public static int loadWebViewNativeLibraryFromPackage(String packageName, ClassLoader clazzLoader) {
        String libraryFileName = LOGTAG;
        if (!isWebViewSupported()) {
            return 1;
        }
        try {
            WebViewProviderResponse response = getUpdateService().waitForAndGetProvider();
            if (response.status != 0 && response.status != 3) {
                return response.status;
            }
            if (!response.packageInfo.packageName.equals(packageName)) {
                return 1;
            }
            try {
                libraryFileName = getWebViewLibrary(AppGlobals.getInitialApplication().getPackageManager().getPackageInfo(packageName, 268435584).applicationInfo);
                int loadNativeRet = WebViewLibraryLoader.loadNativeLibrary(clazzLoader, libraryFileName);
                if (loadNativeRet == 0) {
                    return response.status;
                }
                return loadNativeRet;
            } catch (NameNotFoundException e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Couldn't find package ");
                stringBuilder.append(packageName);
                Log.e(libraryFileName, stringBuilder.toString());
                return 1;
            }
        } catch (RemoteException e2) {
            Log.e(libraryFileName, "error waiting for relro creation", e2);
            return 8;
        }
    }

    @UnsupportedAppUsage
    static WebViewFactoryProvider getProvider() {
        synchronized (sProviderLock) {
            if (sProviderInstance != null) {
                WebViewFactoryProvider webViewFactoryProvider = sProviderInstance;
                return webViewFactoryProvider;
            }
            int uid = Process.myUid();
            if (uid == 0 || uid == 1000 || uid == 1001 || uid == 1027 || uid == 1002) {
                throw new UnsupportedOperationException("For security reasons, WebView is not allowed in privileged processes");
            } else if (!isWebViewSupported()) {
                throw new UnsupportedOperationException();
            } else if (sWebViewDisabled) {
                throw new IllegalStateException("WebView.disableWebView() was called: WebView is disabled");
            } else {
                Trace.traceBegin(16, "WebViewFactory.getProvider()");
                try {
                    Method staticFactory = null;
                    try {
                        staticFactory = getProviderClass().getMethod(CHROMIUM_WEBVIEW_FACTORY_METHOD, new Class[]{WebViewDelegate.class});
                    } catch (Exception e) {
                    }
                    Trace.traceBegin(16, "WebViewFactoryProvider invocation");
                    sProviderInstance = (WebViewFactoryProvider) staticFactory.invoke(null, new Object[]{new WebViewDelegate()});
                    WebViewFactoryProvider webViewFactoryProvider2 = sProviderInstance;
                    Trace.traceEnd(16);
                    Trace.traceEnd(16);
                    return webViewFactoryProvider2;
                } catch (Exception e2) {
                    Log.e(LOGTAG, "error instantiating provider", e2);
                    throw new AndroidRuntimeException(e2);
                } catch (Throwable th) {
                    Trace.traceEnd(16);
                }
            }
        }
    }

    private static boolean signaturesEquals(Signature[] s1, Signature[] s2) {
        int i = 0;
        if (s1 == null) {
            boolean z;
            if (s2 == null) {
                z = true;
            }
            return z;
        } else if (s2 == null) {
            return false;
        } else {
            ArraySet<Signature> set1 = new ArraySet();
            for (Signature signature : s1) {
                set1.add(signature);
            }
            ArraySet<Signature> set2 = new ArraySet();
            int length = s2.length;
            while (i < length) {
                set2.add(s2[i]);
                i++;
            }
            return set1.equals(set2);
        }
    }

    private static void verifyPackageInfo(PackageInfo chosen, PackageInfo toUse) throws MissingWebViewPackageException {
        String str = " actual: ";
        StringBuilder stringBuilder;
        if (!chosen.packageName.equals(toUse.packageName)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to verify WebView provider, packageName mismatch, expected: ");
            stringBuilder.append(chosen.packageName);
            stringBuilder.append(str);
            stringBuilder.append(toUse.packageName);
            throw new MissingWebViewPackageException(stringBuilder.toString());
        } else if (chosen.getLongVersionCode() > toUse.getLongVersionCode()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to verify WebView provider, version code is lower than expected: ");
            stringBuilder.append(chosen.getLongVersionCode());
            stringBuilder.append(str);
            stringBuilder.append(toUse.getLongVersionCode());
            throw new MissingWebViewPackageException(stringBuilder.toString());
        } else if (getWebViewLibrary(toUse.applicationInfo) == null) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Tried to load an invalid WebView provider: ");
            stringBuilder2.append(toUse.packageName);
            throw new MissingWebViewPackageException(stringBuilder2.toString());
        } else if (!signaturesEquals(chosen.signatures, toUse.signatures)) {
            throw new MissingWebViewPackageException("Failed to verify WebView provider, signature mismatch");
        }
    }

    @UnsupportedAppUsage
    private static Context getWebViewContextAndSetProvider() throws MissingWebViewPackageException {
        String str = "Failed to load WebView provider: ";
        Application initialApplication = AppGlobals.getInitialApplication();
        StringBuilder stringBuilder;
        try {
            Trace.traceBegin(16, "WebViewUpdateService.waitForAndGetProvider()");
            WebViewProviderResponse response = getUpdateService().waitForAndGetProvider();
            Trace.traceEnd(16);
            if (response.status != 0) {
                if (response.status != 3) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(getWebViewPreparationErrorReason(response.status));
                    throw new MissingWebViewPackageException(stringBuilder.toString());
                }
            }
            Trace.traceBegin(16, "ActivityManager.addPackageDependency()");
            ActivityManager.getService().addPackageDependency(response.packageInfo.packageName);
            Trace.traceEnd(16);
            PackageManager pm = initialApplication.getPackageManager();
            Trace.traceBegin(16, "PackageManager.getPackageInfo()");
            PackageInfo newPackageInfo = pm.getPackageInfo(response.packageInfo.packageName, 268444864);
            Trace.traceEnd(16);
            verifyPackageInfo(response.packageInfo, newPackageInfo);
            ApplicationInfo ai = newPackageInfo.applicationInfo;
            Trace.traceBegin(16, "initialApplication.createApplicationContext");
            Context webViewContext = initialApplication.createApplicationContext(ai, 3);
            sPackageInfo = newPackageInfo;
            Trace.traceEnd(16);
            return webViewContext;
        } catch (NameNotFoundException | RemoteException e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(e);
            throw new MissingWebViewPackageException(stringBuilder.toString());
        } catch (Throwable th) {
            Trace.traceEnd(16);
        }
    }

    @UnsupportedAppUsage
    private static Class<WebViewFactoryProvider> getProviderClass() {
        String str = LOGTAG;
        Application initialApplication = AppGlobals.getInitialApplication();
        try {
            Trace.traceBegin(16, "WebViewFactory.getWebViewContextAndSetProvider()");
            Context webViewContext = getWebViewContextAndSetProvider();
            Trace.traceEnd(16);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Loading ");
            stringBuilder.append(sPackageInfo.packageName);
            stringBuilder.append(" version ");
            stringBuilder.append(sPackageInfo.versionName);
            stringBuilder.append(" (code ");
            stringBuilder.append(sPackageInfo.getLongVersionCode());
            stringBuilder.append(")");
            Log.i(str, stringBuilder.toString());
            Trace.traceBegin(16, "WebViewFactory.getChromiumProviderClass()");
            try {
                for (String newAssetPath : webViewContext.getApplicationInfo().getAllApkPaths()) {
                    initialApplication.getAssets().addAssetPathAsSharedLibrary(newAssetPath);
                }
                ClassLoader clazzLoader = webViewContext.getClassLoader();
                Trace.traceBegin(16, "WebViewFactory.loadNativeLibrary()");
                WebViewLibraryLoader.loadNativeLibrary(clazzLoader, getWebViewLibrary(sPackageInfo.applicationInfo));
                Trace.traceEnd(16);
                Trace.traceBegin(16, "Class.forName()");
                Class webViewProviderClass = getWebViewProviderClass(clazzLoader);
                Trace.traceEnd(16);
                Trace.traceEnd(16);
                return webViewProviderClass;
            } catch (ClassNotFoundException e) {
                Log.e(str, "error loading provider", e);
                throw new AndroidRuntimeException(e);
            } catch (Throwable th) {
                Trace.traceEnd(16);
            }
        } catch (MissingWebViewPackageException e2) {
            Log.e(str, "Chromium WebView package does not exist", e2);
            throw new AndroidRuntimeException(e2);
        } catch (Throwable th2) {
            Trace.traceEnd(16);
        }
    }

    public static void prepareWebViewInZygote() {
        try {
            WebViewLibraryLoader.reserveAddressSpaceInZygote();
        } catch (Throwable t) {
            Log.e(LOGTAG, "error preparing native loader", t);
        }
    }

    public static int onWebViewProviderChanged(PackageInfo packageInfo) {
        int startedRelroProcesses = 0;
        try {
            startedRelroProcesses = WebViewLibraryLoader.prepareNativeLibraries(packageInfo);
        } catch (Throwable t) {
            Log.e(LOGTAG, "error preparing webview native library", t);
        }
        WebViewZygote.onWebViewProviderChanged(packageInfo);
        return startedRelroProcesses;
    }

    @UnsupportedAppUsage
    public static IWebViewUpdateService getUpdateService() {
        if (isWebViewSupported()) {
            return getUpdateServiceUnchecked();
        }
        return null;
    }

    static IWebViewUpdateService getUpdateServiceUnchecked() {
        return Stub.asInterface(ServiceManager.getService(WEBVIEW_UPDATE_SERVICE_NAME));
    }
}
