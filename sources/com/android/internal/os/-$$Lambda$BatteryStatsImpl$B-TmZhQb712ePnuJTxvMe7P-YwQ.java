package com.android.internal.os;

import com.android.internal.os.KernelCpuUidTimeReader.Callback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BatteryStatsImpl$B-TmZhQb712ePnuJTxvMe7P-YwQ implements Callback {
    private final /* synthetic */ BatteryStatsImpl f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$BatteryStatsImpl$B-TmZhQb712ePnuJTxvMe7P-YwQ(BatteryStatsImpl batteryStatsImpl, boolean z, boolean z2, boolean z3, int i, int i2) {
        this.f$0 = batteryStatsImpl;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = z3;
        this.f$4 = i;
        this.f$5 = i2;
    }

    public final void onUidCpuTime(int i, Object obj) {
        this.f$0.lambda$readKernelUidCpuFreqTimesLocked$1$BatteryStatsImpl(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, i, (long[]) obj);
    }
}
