package android.net.metrics;

import android.os.SystemClock;
import android.util.SparseIntArray;
import com.miui.mishare.RemoteDevice;
import java.util.StringJoiner;

public class WakeupStats {
    private static final int NO_UID = -1;
    public long applicationWakeups = 0;
    public final long creationTimeMs = SystemClock.elapsedRealtime();
    public long durationSec = 0;
    public final SparseIntArray ethertypes = new SparseIntArray();
    public final String iface;
    public final SparseIntArray ipNextHeaders = new SparseIntArray();
    public long l2BroadcastCount = 0;
    public long l2MulticastCount = 0;
    public long l2UnicastCount = 0;
    public long noUidWakeups = 0;
    public long nonApplicationWakeups = 0;
    public long rootWakeups = 0;
    public long systemWakeups = 0;
    public long totalWakeups = 0;

    public WakeupStats(String iface) {
        this.iface = iface;
    }

    public void updateDuration() {
        this.durationSec = (SystemClock.elapsedRealtime() - this.creationTimeMs) / 1000;
    }

    public void countEvent(WakeupEvent ev) {
        this.totalWakeups++;
        int i = ev.uid;
        if (i == -1) {
            this.noUidWakeups++;
        } else if (i == 0) {
            this.rootWakeups++;
        } else if (i == 1000) {
            this.systemWakeups++;
        } else if (ev.uid >= 10000) {
            this.applicationWakeups++;
        } else {
            this.nonApplicationWakeups++;
        }
        i = ev.dstHwAddr.getAddressType();
        if (i == 1) {
            this.l2UnicastCount++;
        } else if (i == 2) {
            this.l2MulticastCount++;
        } else if (i == 3) {
            this.l2BroadcastCount++;
        }
        increment(this.ethertypes, ev.ethertype);
        if (ev.ipNextHeader >= 0) {
            increment(this.ipNextHeaders, ev.ipNextHeader);
        }
    }

    public String toString() {
        int i;
        int eth;
        int count;
        updateDuration();
        StringJoiner j = new StringJoiner(", ", "WakeupStats(", ")");
        j.add(this.iface);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(this.durationSec);
        stringBuilder.append(RemoteDevice.KEY_STATUS);
        j.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("total: ");
        stringBuilder.append(this.totalWakeups);
        j.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("root: ");
        stringBuilder.append(this.rootWakeups);
        j.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("system: ");
        stringBuilder.append(this.systemWakeups);
        j.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("apps: ");
        stringBuilder.append(this.applicationWakeups);
        j.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("non-apps: ");
        stringBuilder.append(this.nonApplicationWakeups);
        j.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("no uid: ");
        stringBuilder.append(this.noUidWakeups);
        j.add(stringBuilder.toString());
        j.add(String.format("l2 unicast/multicast/broadcast: %d/%d/%d", new Object[]{Long.valueOf(this.l2UnicastCount), Long.valueOf(this.l2MulticastCount), Long.valueOf(this.l2BroadcastCount)}));
        for (i = 0; i < this.ethertypes.size(); i++) {
            eth = this.ethertypes.keyAt(i);
            count = this.ethertypes.valueAt(i);
            j.add(String.format("ethertype 0x%x: %d", new Object[]{Integer.valueOf(eth), Integer.valueOf(count)}));
        }
        for (i = 0; i < this.ipNextHeaders.size(); i++) {
            eth = this.ipNextHeaders.keyAt(i);
            count = this.ipNextHeaders.valueAt(i);
            j.add(String.format("ipNxtHdr %d: %d", new Object[]{Integer.valueOf(eth), Integer.valueOf(count)}));
        }
        return j.toString();
    }

    private static void increment(SparseIntArray counters, int key) {
        counters.put(key, counters.get(key, 0) + 1);
    }
}
