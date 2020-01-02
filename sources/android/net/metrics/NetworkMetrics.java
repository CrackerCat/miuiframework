package android.net.metrics;

import android.net.NetworkCapabilities;
import com.android.internal.util.BitUtils;
import com.android.internal.util.TokenBucket;
import java.util.StringJoiner;

public class NetworkMetrics {
    private static final int CONNECT_LATENCY_MAXIMUM_RECORDS = 20000;
    private static final int INITIAL_DNS_BATCH_SIZE = 100;
    public final ConnectStats connectMetrics;
    public final DnsEvent dnsMetrics;
    public final int netId;
    public Summary pendingSummary;
    public final Summary summary;
    public final long transports;

    static class Metrics {
        public int count;
        public double max = Double.MIN_VALUE;
        public double sum;

        Metrics() {
        }

        /* Access modifiers changed, original: 0000 */
        public void merge(Metrics that) {
            this.count += that.count;
            this.sum += that.sum;
            this.max = Math.max(this.max, that.max);
        }

        /* Access modifiers changed, original: 0000 */
        public void count(double value) {
            count(value, 1);
        }

        /* Access modifiers changed, original: 0000 */
        public void count(double value, int subcount) {
            this.count += subcount;
            this.sum += value;
            this.max = Math.max(this.max, value);
        }

        /* Access modifiers changed, original: 0000 */
        public double average() {
            double a = this.sum / ((double) this.count);
            if (Double.isNaN(a)) {
                return 0.0d;
            }
            return a;
        }
    }

    public static class Summary {
        public final Metrics connectErrorRate = new Metrics();
        public final Metrics connectLatencies = new Metrics();
        public final Metrics dnsErrorRate = new Metrics();
        public final Metrics dnsLatencies = new Metrics();
        public final int netId;
        public final Metrics roundTripTimeUs = new Metrics();
        public final Metrics sentAckTimeDiffenceMs = new Metrics();
        public final Metrics tcpLossRate = new Metrics();
        public final long transports;

        public Summary(int netId, long transports) {
            this.netId = netId;
            this.transports = transports;
        }

        /* Access modifiers changed, original: 0000 */
        public void merge(Summary that) {
            this.dnsLatencies.merge(that.dnsLatencies);
            this.dnsErrorRate.merge(that.dnsErrorRate);
            this.connectLatencies.merge(that.connectLatencies);
            this.connectErrorRate.merge(that.connectErrorRate);
            this.tcpLossRate.merge(that.tcpLossRate);
        }

        public String toString() {
            StringJoiner j = new StringJoiner(", ", "{", "}");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("netId=");
            stringBuilder.append(this.netId);
            j.add(stringBuilder.toString());
            for (int t : BitUtils.unpackBits(this.transports)) {
                j.add(NetworkCapabilities.transportNameOf(t));
            }
            j.add(String.format("dns avg=%dms max=%dms err=%.1f%% tot=%d", new Object[]{Integer.valueOf((int) this.dnsLatencies.average()), Integer.valueOf((int) this.dnsLatencies.max), Double.valueOf(this.dnsErrorRate.average() * 100.0d), Integer.valueOf(this.dnsErrorRate.count)}));
            j.add(String.format("connect avg=%dms max=%dms err=%.1f%% tot=%d", new Object[]{Integer.valueOf((int) this.connectLatencies.average()), Integer.valueOf((int) this.connectLatencies.max), Double.valueOf(this.connectErrorRate.average() * 100.0d), Integer.valueOf(this.connectErrorRate.count)}));
            j.add(String.format("tcp avg_loss=%.1f%% total_sent=%d total_lost=%d", new Object[]{Double.valueOf(this.tcpLossRate.average() * 100.0d), Integer.valueOf(this.tcpLossRate.count), Integer.valueOf((int) this.tcpLossRate.sum)}));
            j.add(String.format("tcp rtt=%dms", new Object[]{Integer.valueOf((int) (this.roundTripTimeUs.average() / 1000.0d))}));
            j.add(String.format("tcp sent-ack_diff=%dms", new Object[]{Integer.valueOf((int) this.sentAckTimeDiffenceMs.average())}));
            return j.toString();
        }
    }

    public NetworkMetrics(int netId, long transports, TokenBucket tb) {
        this.netId = netId;
        this.transports = transports;
        this.connectMetrics = new ConnectStats(netId, transports, tb, 20000);
        this.dnsMetrics = new DnsEvent(netId, transports, 100);
        this.summary = new Summary(netId, transports);
    }

    public Summary getPendingStats() {
        Summary s = this.pendingSummary;
        this.pendingSummary = null;
        if (s != null) {
            this.summary.merge(s);
        }
        return s;
    }

    public void addDnsResult(int eventType, int returnCode, int latencyMs) {
        if (this.pendingSummary == null) {
            this.pendingSummary = new Summary(this.netId, this.transports);
        }
        boolean isSuccess = this.dnsMetrics.addResult((byte) eventType, (byte) returnCode, latencyMs);
        this.pendingSummary.dnsLatencies.count((double) latencyMs);
        this.pendingSummary.dnsErrorRate.count(isSuccess ? 0.0d : 1.0d);
    }

    public void addConnectResult(int error, int latencyMs, String ipAddr) {
        if (this.pendingSummary == null) {
            this.pendingSummary = new Summary(this.netId, this.transports);
        }
        this.pendingSummary.connectErrorRate.count(this.connectMetrics.addEvent(error, latencyMs, ipAddr) ? 0.0d : 1.0d);
        if (ConnectStats.isNonBlocking(error)) {
            this.pendingSummary.connectLatencies.count((double) latencyMs);
        }
    }

    public void addTcpStatsResult(int sent, int lost, int rttUs, int sentAckDiffMs) {
        if (this.pendingSummary == null) {
            this.pendingSummary = new Summary(this.netId, this.transports);
        }
        this.pendingSummary.tcpLossRate.count((double) lost, sent);
        this.pendingSummary.roundTripTimeUs.count((double) rttUs);
        this.pendingSummary.sentAckTimeDiffenceMs.count((double) sentAckDiffMs);
    }
}
