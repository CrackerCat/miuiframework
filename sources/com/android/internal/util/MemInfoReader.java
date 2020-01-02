package com.android.internal.util;

import android.annotation.UnsupportedAppUsage;
import android.os.Debug;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;

public final class MemInfoReader {
    final long[] mInfos = new long[15];

    @UnsupportedAppUsage
    public void readMemInfo() {
        ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        try {
            Debug.getMemInfo(this.mInfos);
        } finally {
            StrictMode.setThreadPolicy(savedPolicy);
        }
    }

    @UnsupportedAppUsage
    public long getTotalSize() {
        return this.mInfos[0] * 1024;
    }

    @UnsupportedAppUsage
    public long getFreeSize() {
        return this.mInfos[1] * 1024;
    }

    @UnsupportedAppUsage
    public long getCachedSize() {
        return getCachedSizeKb() * 1024;
    }

    public long getKernelUsedSize() {
        return getKernelUsedSizeKb() * 1024;
    }

    public long getTotalSizeKb() {
        return this.mInfos[0];
    }

    public long getFreeSizeKb() {
        return this.mInfos[1];
    }

    public long getCachedSizeKb() {
        long[] jArr = this.mInfos;
        return ((jArr[2] + jArr[6]) + jArr[3]) - jArr[11];
    }

    public long getMoreCachedSizeKb() {
        long[] jArr = this.mInfos;
        return (jArr[2] + jArr[6]) + jArr[3];
    }

    public long getKernelUsedSizeKb() {
        long[] jArr = this.mInfos;
        return (((jArr[4] + jArr[7]) + jArr[12]) + jArr[13]) + jArr[14];
    }

    public long getSwapTotalSizeKb() {
        return this.mInfos[8];
    }

    public long getSwapFreeSizeKb() {
        return this.mInfos[9];
    }

    public long getZramTotalSizeKb() {
        return this.mInfos[10];
    }

    @UnsupportedAppUsage
    public long[] getRawInfo() {
        return this.mInfos;
    }
}
