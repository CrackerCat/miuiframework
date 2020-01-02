package com.android.server;

import android.os.StrictMode;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import dalvik.system.SocketTagger;
import java.io.FileDescriptor;
import java.net.SocketException;

public final class NetworkManagementSocketTagger extends SocketTagger {
    private static final boolean LOGD = false;
    public static final String PROP_QTAGUID_ENABLED = "net.qtaguid_enabled";
    private static final String TAG = "NetworkManagementSocketTagger";
    private static ThreadLocal<SocketTags> threadSocketTags = new ThreadLocal<SocketTags>() {
        /* Access modifiers changed, original: protected */
        public SocketTags initialValue() {
            return new SocketTags();
        }
    };

    public static class SocketTags {
        public int statsTag = -1;
        public int statsUid = -1;
    }

    private static native int native_deleteTagData(int i, int i2);

    private static native int native_setCounterSet(int i, int i2);

    private static native int native_tagSocketFd(FileDescriptor fileDescriptor, int i, int i2);

    private static native int native_untagSocketFd(FileDescriptor fileDescriptor);

    public static void install() {
        SocketTagger.set(new NetworkManagementSocketTagger());
    }

    public static int setThreadSocketStatsTag(int tag) {
        int old = ((SocketTags) threadSocketTags.get()).statsTag;
        ((SocketTags) threadSocketTags.get()).statsTag = tag;
        return old;
    }

    public static int getThreadSocketStatsTag() {
        return ((SocketTags) threadSocketTags.get()).statsTag;
    }

    public static int setThreadSocketStatsUid(int uid) {
        int old = ((SocketTags) threadSocketTags.get()).statsUid;
        ((SocketTags) threadSocketTags.get()).statsUid = uid;
        return old;
    }

    public static int getThreadSocketStatsUid() {
        return ((SocketTags) threadSocketTags.get()).statsUid;
    }

    public void tag(FileDescriptor fd) throws SocketException {
        SocketTags options = (SocketTags) threadSocketTags.get();
        if (options.statsTag == -1 && StrictMode.vmUntaggedSocketEnabled()) {
            StrictMode.onUntaggedSocket();
        }
        tagSocketFd(fd, options.statsTag, options.statsUid);
    }

    private void tagSocketFd(FileDescriptor fd, int tag, int uid) {
        if (!(tag == -1 && uid == -1) && SystemProperties.getBoolean(PROP_QTAGUID_ENABLED, false)) {
            int errno = native_tagSocketFd(fd, tag, uid);
            if (errno < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("tagSocketFd(");
                stringBuilder.append(fd.getInt$());
                String str = ", ";
                stringBuilder.append(str);
                stringBuilder.append(tag);
                stringBuilder.append(str);
                stringBuilder.append(uid);
                stringBuilder.append(") failed with errno");
                stringBuilder.append(errno);
                Log.i(TAG, stringBuilder.toString());
            }
        }
    }

    public void untag(FileDescriptor fd) throws SocketException {
        unTagSocketFd(fd);
    }

    private void unTagSocketFd(FileDescriptor fd) {
        SocketTags options = (SocketTags) threadSocketTags.get();
        if (!(options.statsTag == -1 && options.statsUid == -1) && SystemProperties.getBoolean(PROP_QTAGUID_ENABLED, false)) {
            int errno = native_untagSocketFd(fd);
            if (errno < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("untagSocket(");
                stringBuilder.append(fd.getInt$());
                stringBuilder.append(") failed with errno ");
                stringBuilder.append(errno);
                Log.w(TAG, stringBuilder.toString());
            }
        }
    }

    public static void setKernelCounterSet(int uid, int counterSet) {
        if (SystemProperties.getBoolean(PROP_QTAGUID_ENABLED, false)) {
            int errno = native_setCounterSet(counterSet, uid);
            if (errno < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("setKernelCountSet(");
                stringBuilder.append(uid);
                stringBuilder.append(", ");
                stringBuilder.append(counterSet);
                stringBuilder.append(") failed with errno ");
                stringBuilder.append(errno);
                Log.w(TAG, stringBuilder.toString());
            }
        }
    }

    public static void resetKernelUidStats(int uid) {
        if (SystemProperties.getBoolean(PROP_QTAGUID_ENABLED, false)) {
            int errno = native_deleteTagData(0, uid);
            if (errno < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("problem clearing counters for uid ");
                stringBuilder.append(uid);
                stringBuilder.append(" : errno ");
                stringBuilder.append(errno);
                Slog.w(TAG, stringBuilder.toString());
            }
        }
    }

    public static int kernelToTag(String string) {
        int length = string.length();
        if (length > 10) {
            return Long.decode(string.substring(0, length - 8)).intValue();
        }
        return 0;
    }
}
