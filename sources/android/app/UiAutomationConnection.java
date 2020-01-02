package android.app;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.app.IUiAutomationConnection.Stub;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.IWindowManager;
import android.view.InputEvent;
import android.view.SurfaceControl;
import android.view.WindowAnimationFrameStats;
import android.view.WindowContentFrameStats;
import android.view.accessibility.IAccessibilityManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import libcore.io.IoUtils;

public final class UiAutomationConnection extends Stub {
    private static final int INITIAL_FROZEN_ROTATION_UNSPECIFIED = -1;
    private static final String TAG = "UiAutomationConnection";
    private final IAccessibilityManager mAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
    private final IActivityManager mActivityManager = IActivityManager.Stub.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE));
    private IAccessibilityServiceClient mClient;
    private int mInitialFrozenRotation = -1;
    private boolean mIsShutdown;
    private final Object mLock = new Object();
    private int mOwningUid;
    private final IPackageManager mPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    private final Binder mToken = new Binder();
    private final IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));

    public class Repeater implements Runnable {
        private final InputStream readFrom;
        private final OutputStream writeTo;

        public Repeater(InputStream readFrom, OutputStream writeTo) {
            this.readFrom = readFrom;
            this.writeTo = writeTo;
        }

        public void run() {
            try {
                byte[] buffer = new byte[8192];
                while (true) {
                    int readByteCount = this.readFrom.read(buffer);
                    if (readByteCount < 0) {
                        break;
                    }
                    this.writeTo.write(buffer, 0, readByteCount);
                    this.writeTo.flush();
                }
            } catch (IOException e) {
                Log.w(UiAutomationConnection.TAG, "Error while reading/writing to streams");
            } catch (Throwable th) {
                IoUtils.closeQuietly(this.readFrom);
                IoUtils.closeQuietly(this.writeTo);
            }
            IoUtils.closeQuietly(this.readFrom);
            IoUtils.closeQuietly(this.writeTo);
        }
    }

    public void connect(IAccessibilityServiceClient client, int flags) {
        if (client != null) {
            synchronized (this.mLock) {
                throwIfShutdownLocked();
                if (isConnectedLocked()) {
                    throw new IllegalStateException("Already connected.");
                }
                this.mOwningUid = Binder.getCallingUid();
                registerUiTestAutomationServiceLocked(client, flags);
                storeRotationStateLocked();
            }
            return;
        }
        throw new IllegalArgumentException("Client cannot be null!");
    }

    public void disconnect() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            if (isConnectedLocked()) {
                this.mOwningUid = -1;
                unregisterUiTestAutomationServiceLocked();
                restoreRotationStateLocked();
            } else {
                throw new IllegalStateException("Already disconnected.");
            }
        }
    }

    public boolean injectInputEvent(InputEvent event, boolean sync) {
        int mode;
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        boolean z = false;
        if (sync) {
            mode = 2;
        } else {
            mode = 0;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            z = this.mWindowManager.injectInputAfterTransactionsApplied(event, mode);
            return z;
        } catch (RemoteException e) {
            return z;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void syncInputTransactions() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        try {
            this.mWindowManager.syncInputTransactions();
        } catch (RemoteException e) {
        }
    }

    public boolean setRotation(int rotation) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        if (rotation == -2) {
            try {
                this.mWindowManager.thawRotation();
            } catch (RemoteException e) {
                Binder.restoreCallingIdentity(identity);
                return false;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
            }
        } else {
            this.mWindowManager.freezeRotation(rotation);
        }
        Binder.restoreCallingIdentity(identity);
        return true;
    }

    public Bitmap takeScreenshot(Rect crop, int rotation) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            Bitmap screenshot = SurfaceControl.screenshot(crop, crop.width(), crop.height(), rotation);
            return screenshot;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public boolean clearWindowContentFrameStats(int windowId) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        int callingUserId = UserHandle.getCallingUserId();
        long identity = Binder.clearCallingIdentity();
        try {
            IBinder token = this.mAccessibilityManager.getWindowToken(windowId, callingUserId);
            boolean z;
            if (token == null) {
                z = false;
                return z;
            }
            z = this.mWindowManager.clearWindowContentFrameStats(token);
            Binder.restoreCallingIdentity(identity);
            return z;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public WindowContentFrameStats getWindowContentFrameStats(int windowId) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        int callingUserId = UserHandle.getCallingUserId();
        long identity = Binder.clearCallingIdentity();
        try {
            IBinder token = this.mAccessibilityManager.getWindowToken(windowId, callingUserId);
            WindowContentFrameStats windowContentFrameStats;
            if (token == null) {
                windowContentFrameStats = null;
                return windowContentFrameStats;
            }
            windowContentFrameStats = this.mWindowManager.getWindowContentFrameStats(token);
            Binder.restoreCallingIdentity(identity);
            return windowContentFrameStats;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void clearWindowAnimationFrameStats() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            SurfaceControl.clearAnimationFrameStats();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public WindowAnimationFrameStats getWindowAnimationFrameStats() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            WindowAnimationFrameStats stats = new WindowAnimationFrameStats();
            SurfaceControl.getAnimationFrameStats(stats);
            return stats;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void grantRuntimePermission(String packageName, String permission, int userId) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mPackageManager.grantRuntimePermission(packageName, permission, userId);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void revokeRuntimePermission(String packageName, String permission, int userId) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mPackageManager.revokeRuntimePermission(packageName, permission, userId);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void adoptShellPermissionIdentity(int uid, String[] permissions) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mActivityManager.startDelegateShellPermissionIdentity(uid, permissions);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void dropShellPermissionIdentity() throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mActivityManager.stopDelegateShellPermissionIdentity();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void executeShellCommand(String command, ParcelFileDescriptor sink, ParcelFileDescriptor source) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        try {
            Thread readFromProcess;
            Thread writeToProcess;
            Thread writeToProcess2;
            final Process process = Runtime.getRuntime().exec(command);
            if (sink != null) {
                Thread readFromProcess2 = new Thread(new Repeater(process.getInputStream(), new FileOutputStream(sink.getFileDescriptor())));
                readFromProcess2.start();
                readFromProcess = readFromProcess2;
            } else {
                readFromProcess = null;
            }
            if (source != null) {
                writeToProcess = new Thread(new Repeater(new FileInputStream(source.getFileDescriptor()), process.getOutputStream()));
                writeToProcess.start();
                writeToProcess2 = writeToProcess;
            } else {
                writeToProcess2 = null;
            }
            writeToProcess = writeToProcess2;
            final Thread thread = readFromProcess;
            final ParcelFileDescriptor parcelFileDescriptor = sink;
            final ParcelFileDescriptor parcelFileDescriptor2 = source;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if (writeToProcess != null) {
                            writeToProcess.join();
                        }
                        if (thread != null) {
                            thread.join();
                        }
                    } catch (InterruptedException e) {
                        Log.e(UiAutomationConnection.TAG, "At least one of the threads was interrupted");
                    }
                    IoUtils.closeQuietly(parcelFileDescriptor);
                    IoUtils.closeQuietly(parcelFileDescriptor2);
                    process.destroy();
                }
            }).start();
        } catch (IOException exc) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error running shell command '");
            stringBuilder.append(command);
            stringBuilder.append("'");
            throw new RuntimeException(stringBuilder.toString(), exc);
        }
    }

    public void shutdown() {
        synchronized (this.mLock) {
            if (isConnectedLocked()) {
                throwIfCalledByNotTrustedUidLocked();
            }
            throwIfShutdownLocked();
            this.mIsShutdown = true;
            if (isConnectedLocked()) {
                disconnect();
            }
        }
    }

    private void registerUiTestAutomationServiceLocked(IAccessibilityServiceClient client, int flags) {
        IAccessibilityManager manager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = -1;
        info.feedbackType = 16;
        info.flags |= 65554;
        info.setCapabilities(15);
        try {
            manager.registerUiTestAutomationService(this.mToken, client, info, flags);
            this.mClient = client;
        } catch (RemoteException re) {
            throw new IllegalStateException("Error while registering UiTestAutomationService.", re);
        }
    }

    private void unregisterUiTestAutomationServiceLocked() {
        try {
            IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE)).unregisterUiTestAutomationService(this.mClient);
            this.mClient = null;
        } catch (RemoteException re) {
            throw new IllegalStateException("Error while unregistering UiTestAutomationService", re);
        }
    }

    private void storeRotationStateLocked() {
        try {
            if (this.mWindowManager.isRotationFrozen()) {
                this.mInitialFrozenRotation = this.mWindowManager.getDefaultDisplayRotation();
            }
        } catch (RemoteException e) {
        }
    }

    private void restoreRotationStateLocked() {
        try {
            if (this.mInitialFrozenRotation != -1) {
                this.mWindowManager.freezeRotation(this.mInitialFrozenRotation);
            } else {
                this.mWindowManager.thawRotation();
            }
        } catch (RemoteException e) {
        }
    }

    private boolean isConnectedLocked() {
        return this.mClient != null;
    }

    private void throwIfShutdownLocked() {
        if (this.mIsShutdown) {
            throw new IllegalStateException("Connection shutdown!");
        }
    }

    private void throwIfNotConnectedLocked() {
        if (!isConnectedLocked()) {
            throw new IllegalStateException("Not connected!");
        }
    }

    private void throwIfCalledByNotTrustedUidLocked() {
        int callingUid = Binder.getCallingUid();
        int i = this.mOwningUid;
        if (callingUid != i && i != 1000 && callingUid != 0) {
            throw new SecurityException("Calling from not trusted UID!");
        }
    }
}
