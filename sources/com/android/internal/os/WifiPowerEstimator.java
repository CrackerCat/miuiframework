package com.android.internal.os;

import android.os.BatteryStats;
import android.os.BatteryStats.Uid;

public class WifiPowerEstimator extends PowerCalculator {
    private static final boolean DEBUG = false;
    private static final String TAG = "WifiPowerEstimator";
    private long mTotalAppWifiRunningTimeMs = 0;
    private final double mWifiPowerBatchScan;
    private final double mWifiPowerOn;
    private final double mWifiPowerPerPacket;
    private final double mWifiPowerScan;

    public WifiPowerEstimator(PowerProfile profile) {
        this.mWifiPowerPerPacket = getWifiPowerPerPacket(profile);
        this.mWifiPowerOn = profile.getAveragePower(PowerProfile.POWER_WIFI_ON);
        this.mWifiPowerScan = profile.getAveragePower(PowerProfile.POWER_WIFI_SCAN);
        this.mWifiPowerBatchScan = profile.getAveragePower(PowerProfile.POWER_WIFI_BATCHED_SCAN);
    }

    private static double getWifiPowerPerPacket(PowerProfile profile) {
        return (profile.getAveragePower(PowerProfile.POWER_WIFI_ACTIVE) / 3600.0d) / 61.03515625d;
    }

    public void calculateApp(BatterySipper app, Uid u, long rawRealtimeUs, long rawUptimeUs, int statsType) {
        BatterySipper batterySipper = app;
        Uid uid = u;
        long j = rawRealtimeUs;
        int i = statsType;
        batterySipper.wifiRxPackets = uid.getNetworkActivityPackets(2, i);
        batterySipper.wifiTxPackets = uid.getNetworkActivityPackets(3, i);
        batterySipper.wifiRxBytes = uid.getNetworkActivityBytes(2, i);
        batterySipper.wifiTxBytes = uid.getNetworkActivityBytes(3, i);
        double wifiPacketPower = ((double) (batterySipper.wifiRxPackets + batterySipper.wifiTxPackets)) * this.mWifiPowerPerPacket;
        batterySipper.wifiRunningTimeMs = uid.getWifiRunningTime(j, i) / 1000;
        this.mTotalAppWifiRunningTimeMs += batterySipper.wifiRunningTimeMs;
        double wifiLockPower = (((double) batterySipper.wifiRunningTimeMs) * this.mWifiPowerOn) / 3600000.0d;
        long wifiScanTimeMs = uid.getWifiScanTime(j, i) / 1000;
        double wifiScanPower = (((double) wifiScanTimeMs) * this.mWifiPowerScan) / 3600000.0d;
        int bin = 0;
        double wifiBatchScanPower = 0.0d;
        while (bin < 5) {
            wifiBatchScanPower += (((double) (uid.getWifiBatchedScanTime(bin, j, i) / 1000)) * this.mWifiPowerBatchScan) / 3600000.0d;
            bin++;
            uid = u;
            double d = 3600000.0d;
            j = rawRealtimeUs;
            i = statsType;
        }
        batterySipper.wifiPowerMah = ((wifiPacketPower + wifiLockPower) + wifiScanPower) + wifiBatchScanPower;
    }

    public void calculateRemaining(BatterySipper app, BatteryStats stats, long rawRealtimeUs, long rawUptimeUs, int statsType) {
        long totalRunningTimeMs = stats.getGlobalWifiRunningTime(rawRealtimeUs, statsType) / 1000;
        double powerDrain = (((double) (totalRunningTimeMs - this.mTotalAppWifiRunningTimeMs)) * this.mWifiPowerOn) / 3600000.0d;
        app.wifiRunningTimeMs = totalRunningTimeMs;
        app.wifiPowerMah = Math.max(0.0d, powerDrain);
    }

    public void reset() {
        this.mTotalAppWifiRunningTimeMs = 0;
    }
}
