package android.telephony.mbms;

import android.annotation.SystemApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.MbmsDownloadSession;
import android.telephony.mbms.vendor.VendorUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MbmsDownloadReceiver extends BroadcastReceiver {
    public static final String DOWNLOAD_TOKEN_SUFFIX = ".download_token";
    private static final String EMBMS_INTENT_PERMISSION = "android.permission.SEND_EMBMS_INTENTS";
    private static final String LOG_TAG = "MbmsDownloadReceiver";
    private static final int MAX_TEMP_FILE_RETRIES = 5;
    public static final String MBMS_FILE_PROVIDER_META_DATA_KEY = "mbms-file-provider-authority";
    @SystemApi
    public static final int RESULT_APP_NOTIFICATION_ERROR = 6;
    @SystemApi
    public static final int RESULT_BAD_TEMP_FILE_ROOT = 3;
    @SystemApi
    public static final int RESULT_DOWNLOAD_FINALIZATION_ERROR = 4;
    @SystemApi
    public static final int RESULT_INVALID_ACTION = 1;
    @SystemApi
    public static final int RESULT_MALFORMED_INTENT = 2;
    @SystemApi
    public static final int RESULT_OK = 0;
    @SystemApi
    public static final int RESULT_TEMP_FILE_GENERATION_ERROR = 5;
    private static final String TEMP_FILE_STAGING_LOCATION = "staged_completed_files";
    private static final String TEMP_FILE_SUFFIX = ".embms.temp";
    private String mFileProviderAuthorityCache = null;
    private String mMiddlewarePackageNameCache = null;

    public void onReceive(Context context, Intent intent) {
        verifyPermissionIntegrity(context);
        if (!verifyIntentContents(context, intent)) {
            setResultCode(2);
        } else if (Objects.equals(intent.getStringExtra(VendorUtils.EXTRA_TEMP_FILE_ROOT), MbmsTempFileProvider.getEmbmsTempFileDir(context).getPath())) {
            if (VendorUtils.ACTION_DOWNLOAD_RESULT_INTERNAL.equals(intent.getAction())) {
                moveDownloadedFile(context, intent);
                cleanupPostMove(context, intent);
            } else {
                if (VendorUtils.ACTION_FILE_DESCRIPTOR_REQUEST.equals(intent.getAction())) {
                    generateTempFiles(context, intent);
                } else {
                    if (VendorUtils.ACTION_CLEANUP.equals(intent.getAction())) {
                        cleanupTempFiles(context, intent);
                    } else {
                        setResultCode(1);
                    }
                }
            }
        } else {
            setResultCode(3);
        }
    }

    private boolean verifyIntentContents(Context context, Intent intent) {
        boolean equals = VendorUtils.ACTION_DOWNLOAD_RESULT_INTERNAL.equals(intent.getAction());
        String str = "Download result did not include the temp file root. Ignoring.";
        String str2 = VendorUtils.EXTRA_TEMP_FILE_ROOT;
        String str3 = LOG_TAG;
        String str4;
        if (equals) {
            String str5 = MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_RESULT;
            if (intent.hasExtra(str5)) {
                str4 = MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_REQUEST;
                if (!intent.hasExtra(str4)) {
                    Log.w(str3, "Download result did not include the associated request. Ignoring.");
                    return false;
                } else if (1 != intent.getIntExtra(str5, 2)) {
                    return true;
                } else {
                    if (!intent.hasExtra(str2)) {
                        Log.w(str3, str);
                        return false;
                    } else if (!intent.hasExtra(MbmsDownloadSession.EXTRA_MBMS_FILE_INFO)) {
                        Log.w(str3, "Download result did not include the associated file info. Ignoring.");
                        return false;
                    } else if (intent.hasExtra(VendorUtils.EXTRA_FINAL_URI)) {
                        DownloadRequest request = (DownloadRequest) intent.getParcelableExtra(str4);
                        str = new StringBuilder();
                        str.append(request.getHash());
                        str.append(DOWNLOAD_TOKEN_SUFFIX);
                        File expectedTokenFile = new File(MbmsUtils.getEmbmsTempFileDirForService(context, request.getFileServiceId()), str.toString());
                        if (!expectedTokenFile.exists()) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Supplied download request does not match a token that we have. Expected ");
                            stringBuilder.append(expectedTokenFile);
                            Log.w(str3, stringBuilder.toString());
                            return false;
                        }
                    } else {
                        Log.w(str3, "Download result did not include the path to the final temp file. Ignoring.");
                        return false;
                    }
                }
            }
            Log.w(str3, "Download result did not include a result code. Ignoring.");
            return false;
        }
        equals = VendorUtils.ACTION_FILE_DESCRIPTOR_REQUEST.equals(intent.getAction());
        str4 = VendorUtils.EXTRA_SERVICE_ID;
        if (!equals) {
            if (VendorUtils.ACTION_CLEANUP.equals(intent.getAction())) {
                if (!intent.hasExtra(str4)) {
                    Log.w(str3, "Cleanup request did not include the associated service id. Ignoring.");
                    return false;
                } else if (!intent.hasExtra(str2)) {
                    Log.w(str3, "Cleanup request did not include the temp file root. Ignoring.");
                    return false;
                } else if (!intent.hasExtra(VendorUtils.EXTRA_TEMP_FILES_IN_USE)) {
                    Log.w(str3, "Cleanup request did not include the list of temp files in use. Ignoring.");
                    return false;
                }
            }
        } else if (!intent.hasExtra(str4)) {
            Log.w(str3, "Temp file request did not include the associated service id. Ignoring.");
            return false;
        } else if (!intent.hasExtra(str2)) {
            Log.w(str3, str);
            return false;
        }
        return true;
    }

    private void moveDownloadedFile(Context context, Intent intent) {
        String str = MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_REQUEST;
        Parcelable request = (DownloadRequest) intent.getParcelableExtra(str);
        Intent intentForApp = request.getIntentForApp();
        String finalLocation = LOG_TAG;
        if (intentForApp == null) {
            Log.i(finalLocation, "Malformed app notification intent");
            setResultCode(6);
            return;
        }
        String str2 = MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_RESULT;
        int result = intent.getIntExtra(str2, 2);
        intentForApp.putExtra(str2, result);
        intentForApp.putExtra(str, request);
        if (result != 1) {
            Log.i(finalLocation, "Download request indicated a failed download. Aborting.");
            context.sendBroadcast(intentForApp);
            setResultCode(0);
            return;
        }
        Uri finalTempFile = (Uri) intent.getParcelableExtra(VendorUtils.EXTRA_FINAL_URI);
        if (verifyTempFilePath(context, request.getFileServiceId(), finalTempFile)) {
            String str3 = MbmsDownloadSession.EXTRA_MBMS_FILE_INFO;
            Parcelable completedFileInfo = (FileInfo) intent.getParcelableExtra(str3);
            try {
                finalLocation = moveToFinalLocation(finalTempFile, FileSystems.getDefault().getPath(request.getDestinationUri().getPath(), new String[0]), getFileRelativePath(request.getSourceUri().getPath(), completedFileInfo.getUri().getPath()));
                intentForApp.putExtra(MbmsDownloadSession.EXTRA_MBMS_COMPLETED_FILE_URI, (Parcelable) finalLocation);
                intentForApp.putExtra(str3, completedFileInfo);
                context.sendBroadcast(intentForApp);
                setResultCode(0);
                return;
            } catch (IOException e) {
                Log.w(finalLocation, "Failed to move temp file to final destination");
                setResultCode(4);
                return;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Download result specified an invalid temp file ");
        stringBuilder.append(finalTempFile);
        Log.w(finalLocation, stringBuilder.toString());
        setResultCode(4);
    }

    private void cleanupPostMove(Context context, Intent intent) {
        DownloadRequest request = (DownloadRequest) intent.getParcelableExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_REQUEST);
        String str = LOG_TAG;
        if (request == null) {
            Log.w(str, "Intent does not include a DownloadRequest. Ignoring.");
            return;
        }
        List<Uri> tempFiles = intent.getParcelableArrayListExtra(VendorUtils.EXTRA_TEMP_LIST);
        if (tempFiles != null) {
            for (Uri tempFileUri : tempFiles) {
                if (verifyTempFilePath(context, request.getFileServiceId(), tempFileUri)) {
                    File tempFile = new File(tempFileUri.getSchemeSpecificPart());
                    if (!tempFile.delete()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed to delete temp file at ");
                        stringBuilder.append(tempFile.getPath());
                        Log.w(str, stringBuilder.toString());
                    }
                }
            }
        }
    }

    private void generateTempFiles(Context context, Intent intent) {
        String serviceId = intent.getStringExtra(VendorUtils.EXTRA_SERVICE_ID);
        String str = LOG_TAG;
        if (serviceId == null) {
            Log.w(str, "Temp file request did not include the associated service id. Ignoring.");
            setResultCode(2);
            return;
        }
        int fdCount = intent.getIntExtra(VendorUtils.EXTRA_FD_COUNT, 0);
        List<Uri> pausedList = intent.getParcelableArrayListExtra(VendorUtils.EXTRA_PAUSED_LIST);
        if (fdCount == 0 && (pausedList == null || pausedList.size() == 0)) {
            Log.i(str, "No temp files actually requested. Ending.");
            setResultCode(0);
            setResultExtras(Bundle.EMPTY);
            return;
        }
        ArrayList<UriPathPair> freshTempFiles = generateFreshTempFiles(context, serviceId, fdCount);
        ArrayList<UriPathPair> pausedFiles = generateUrisForPausedFiles(context, serviceId, pausedList);
        Bundle result = new Bundle();
        result.putParcelableArrayList(VendorUtils.EXTRA_FREE_URI_LIST, freshTempFiles);
        result.putParcelableArrayList(VendorUtils.EXTRA_PAUSED_URI_LIST, pausedFiles);
        setResultCode(0);
        setResultExtras(result);
    }

    private ArrayList<UriPathPair> generateFreshTempFiles(Context context, String serviceId, int freshFdCount) {
        File tempFileDir = MbmsUtils.getEmbmsTempFileDirForService(context, serviceId);
        if (!tempFileDir.exists()) {
            tempFileDir.mkdirs();
        }
        ArrayList<UriPathPair> result = new ArrayList(freshFdCount);
        for (int i = 0; i < freshFdCount; i++) {
            File tempFile = generateSingleTempFile(tempFileDir);
            if (tempFile == null) {
                setResultCode(5);
                Log.w(LOG_TAG, "Failed to generate a temp file. Moving on.");
            } else {
                Uri fileUri = Uri.fromFile(tempFile);
                Uri contentUri = MbmsTempFileProvider.getUriForFile(context, getFileProviderAuthorityCached(context), tempFile);
                context.grantUriPermission(getMiddlewarePackageCached(context), contentUri, 3);
                result.add(new UriPathPair(fileUri, contentUri));
            }
        }
        return result;
    }

    private static File generateSingleTempFile(File tempFileDir) {
        int numTries = 0;
        while (numTries < 5) {
            numTries++;
            String fileName = new StringBuilder();
            fileName.append(UUID.randomUUID());
            fileName.append(TEMP_FILE_SUFFIX);
            File tempFile = new File(tempFileDir, fileName.toString());
            try {
                if (tempFile.createNewFile()) {
                    return tempFile.getCanonicalFile();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    private ArrayList<UriPathPair> generateUrisForPausedFiles(Context context, String serviceId, List<Uri> pausedFiles) {
        if (pausedFiles == null) {
            return new ArrayList(0);
        }
        ArrayList<UriPathPair> result = new ArrayList(pausedFiles.size());
        for (Uri fileUri : pausedFiles) {
            boolean verifyTempFilePath = verifyTempFilePath(context, serviceId, fileUri);
            String str = "Supplied file ";
            String str2 = LOG_TAG;
            if (verifyTempFilePath) {
                File tempFile = new File(fileUri.getSchemeSpecificPart());
                if (tempFile.exists()) {
                    Uri contentUri = MbmsTempFileProvider.getUriForFile(context, getFileProviderAuthorityCached(context), tempFile);
                    context.grantUriPermission(getMiddlewarePackageCached(context), contentUri, 3);
                    result.add(new UriPathPair(fileUri, contentUri));
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(fileUri);
                    stringBuilder.append(" does not exist.");
                    Log.w(str2, stringBuilder.toString());
                    setResultCode(5);
                }
            } else {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(fileUri);
                stringBuilder2.append(" is not a valid temp file to resume");
                Log.w(str2, stringBuilder2.toString());
                setResultCode(5);
            }
        }
        return result;
    }

    private void cleanupTempFiles(Context context, Intent intent) {
        File tempFileDir = MbmsUtils.getEmbmsTempFileDirForService(context, intent.getStringExtra(VendorUtils.EXTRA_SERVICE_ID));
        final List<Uri> filesInUse = intent.getParcelableArrayListExtra(VendorUtils.EXTRA_TEMP_FILES_IN_USE);
        for (File fileToDelete : tempFileDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                try {
                    File canonicalFile = file.getCanonicalFile();
                    if (!canonicalFile.getName().endsWith(MbmsDownloadReceiver.TEMP_FILE_SUFFIX)) {
                        return false;
                    }
                    return filesInUse.contains(Uri.fromFile(canonicalFile)) ^ 1;
                } catch (IOException e) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Got IOException canonicalizing ");
                    stringBuilder.append(file);
                    stringBuilder.append(", not deleting.");
                    Log.w(MbmsDownloadReceiver.LOG_TAG, stringBuilder.toString());
                    return false;
                }
            }
        })) {
            fileToDelete.delete();
        }
    }

    private static Uri moveToFinalLocation(Uri fromPath, Path appSpecifiedPath, String relativeLocation) throws IOException {
        if (ContentResolver.SCHEME_FILE.equals(fromPath.getScheme())) {
            Path fromFile = FileSystems.getDefault().getPath(fromPath.getPath(), new String[0]);
            Path toFile = appSpecifiedPath.resolve(relativeLocation);
            if (!Files.isDirectory(toFile.getParent(), new LinkOption[0])) {
                Files.createDirectories(toFile.getParent(), new FileAttribute[0]);
            }
            return Uri.fromFile(Files.move(fromFile, toFile, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE}).toFile());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Downloaded file location uri ");
        stringBuilder.append(fromPath);
        stringBuilder.append(" does not have a file scheme");
        Log.w(LOG_TAG, stringBuilder.toString());
        return null;
    }

    @VisibleForTesting
    public static String getFileRelativePath(String sourceUriPath, String fileInfoPath) {
        if (sourceUriPath.endsWith("*")) {
            sourceUriPath = sourceUriPath.substring(0, sourceUriPath.lastIndexOf(47));
        }
        if (!fileInfoPath.startsWith(sourceUriPath)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("File location specified in FileInfo does not match the source URI. source: ");
            stringBuilder.append(sourceUriPath);
            stringBuilder.append(" fileinfo path: ");
            stringBuilder.append(fileInfoPath);
            Log.e(LOG_TAG, stringBuilder.toString());
            return null;
        } else if (fileInfoPath.length() == sourceUriPath.length()) {
            return sourceUriPath.substring(sourceUriPath.lastIndexOf(47) + 1);
        } else {
            String prefixOmittedPath = fileInfoPath.substring(sourceUriPath.length());
            if (prefixOmittedPath.startsWith("/")) {
                prefixOmittedPath = prefixOmittedPath.substring(1);
            }
            return prefixOmittedPath;
        }
    }

    private static boolean verifyTempFilePath(Context context, String serviceId, Uri filePath) {
        boolean equals = ContentResolver.SCHEME_FILE.equals(filePath.getScheme());
        String str = LOG_TAG;
        if (equals) {
            String path = filePath.getSchemeSpecificPart();
            File tempFile = new File(path);
            String str2 = "File at ";
            StringBuilder stringBuilder;
            if (!tempFile.exists()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str2);
                stringBuilder.append(path);
                stringBuilder.append(" does not exist.");
                Log.w(str, stringBuilder.toString());
                return false;
            } else if (MbmsUtils.isContainedIn(MbmsUtils.getEmbmsTempFileDirForService(context, serviceId), tempFile)) {
                return true;
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str2);
                stringBuilder.append(path);
                stringBuilder.append(" is not contained in the temp file root, which is ");
                stringBuilder.append(MbmsUtils.getEmbmsTempFileDirForService(context, serviceId));
                Log.w(str, stringBuilder.toString());
                return false;
            }
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Uri ");
        stringBuilder2.append(filePath);
        stringBuilder2.append(" does not have a file scheme");
        Log.w(str, stringBuilder2.toString());
        return false;
    }

    private String getFileProviderAuthorityCached(Context context) {
        String str = this.mFileProviderAuthorityCache;
        if (str != null) {
            return str;
        }
        this.mFileProviderAuthorityCache = getFileProviderAuthority(context);
        return this.mFileProviderAuthorityCache;
    }

    private static String getFileProviderAuthority(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            String str = "App must declare the file provider authority as metadata in the manifest.";
            if (appInfo.metaData != null) {
                String authority = appInfo.metaData.getString(MBMS_FILE_PROVIDER_META_DATA_KEY);
                if (authority != null) {
                    return authority;
                }
                throw new RuntimeException(str);
            }
            throw new RuntimeException(str);
        } catch (NameNotFoundException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Package manager couldn't find ");
            stringBuilder.append(context.getPackageName());
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    private String getMiddlewarePackageCached(Context context) {
        if (this.mMiddlewarePackageNameCache == null) {
            this.mMiddlewarePackageNameCache = MbmsUtils.getMiddlewareServiceInfo(context, MbmsDownloadSession.MBMS_DOWNLOAD_SERVICE_ACTION).packageName;
        }
        return this.mMiddlewarePackageNameCache;
    }

    private void verifyPermissionIntegrity(Context context) {
        List<ResolveInfo> infos = context.getPackageManager().queryBroadcastReceivers(new Intent(context, MbmsDownloadReceiver.class), 0);
        if (infos.size() == 1) {
            ActivityInfo selfInfo = ((ResolveInfo) infos.get(0)).activityInfo;
            if (selfInfo == null) {
                throw new IllegalStateException("Queried ResolveInfo does not contain a receiver");
            } else if (MbmsUtils.getOverrideServiceName(context, MbmsDownloadSession.MBMS_DOWNLOAD_SERVICE_ACTION) == null) {
                if (!Objects.equals("android.permission.SEND_EMBMS_INTENTS", selfInfo.permission)) {
                    throw new IllegalStateException("MbmsDownloadReceiver must require the SEND_EMBMS_INTENTS permission.");
                }
                return;
            } else if (selfInfo.permission == null) {
                throw new IllegalStateException("MbmsDownloadReceiver must require some permission");
            } else {
                return;
            }
        }
        throw new IllegalStateException("Non-unique download receiver in your app");
    }
}
