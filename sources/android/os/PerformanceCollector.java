package android.os;

import android.annotation.UnsupportedAppUsage;
import android.os.Debug.MemoryInfo;
import java.util.ArrayList;

public class PerformanceCollector {
    public static final String METRIC_KEY_CPU_TIME = "cpu_time";
    public static final String METRIC_KEY_EXECUTION_TIME = "execution_time";
    public static final String METRIC_KEY_GC_INVOCATION_COUNT = "gc_invocation_count";
    public static final String METRIC_KEY_GLOBAL_ALLOC_COUNT = "global_alloc_count";
    public static final String METRIC_KEY_GLOBAL_ALLOC_SIZE = "global_alloc_size";
    public static final String METRIC_KEY_GLOBAL_FREED_COUNT = "global_freed_count";
    public static final String METRIC_KEY_GLOBAL_FREED_SIZE = "global_freed_size";
    public static final String METRIC_KEY_ITERATIONS = "iterations";
    public static final String METRIC_KEY_JAVA_ALLOCATED = "java_allocated";
    public static final String METRIC_KEY_JAVA_FREE = "java_free";
    public static final String METRIC_KEY_JAVA_PRIVATE_DIRTY = "java_private_dirty";
    public static final String METRIC_KEY_JAVA_PSS = "java_pss";
    public static final String METRIC_KEY_JAVA_SHARED_DIRTY = "java_shared_dirty";
    public static final String METRIC_KEY_JAVA_SIZE = "java_size";
    public static final String METRIC_KEY_LABEL = "label";
    public static final String METRIC_KEY_NATIVE_ALLOCATED = "native_allocated";
    public static final String METRIC_KEY_NATIVE_FREE = "native_free";
    public static final String METRIC_KEY_NATIVE_PRIVATE_DIRTY = "native_private_dirty";
    public static final String METRIC_KEY_NATIVE_PSS = "native_pss";
    public static final String METRIC_KEY_NATIVE_SHARED_DIRTY = "native_shared_dirty";
    public static final String METRIC_KEY_NATIVE_SIZE = "native_size";
    public static final String METRIC_KEY_OTHER_PRIVATE_DIRTY = "other_private_dirty";
    public static final String METRIC_KEY_OTHER_PSS = "other_pss";
    public static final String METRIC_KEY_OTHER_SHARED_DIRTY = "other_shared_dirty";
    public static final String METRIC_KEY_PRE_RECEIVED_TRANSACTIONS = "pre_received_transactions";
    public static final String METRIC_KEY_PRE_SENT_TRANSACTIONS = "pre_sent_transactions";
    public static final String METRIC_KEY_RECEIVED_TRANSACTIONS = "received_transactions";
    public static final String METRIC_KEY_SENT_TRANSACTIONS = "sent_transactions";
    private long mCpuTime;
    private long mExecTime;
    private Bundle mPerfMeasurement;
    private Bundle mPerfSnapshot;
    private PerformanceResultsWriter mPerfWriter;
    private long mSnapshotCpuTime;
    private long mSnapshotExecTime;

    public interface PerformanceResultsWriter {
        void writeBeginSnapshot(String str);

        void writeEndSnapshot(Bundle bundle);

        void writeMeasurement(String str, float f);

        void writeMeasurement(String str, long j);

        void writeMeasurement(String str, String str2);

        void writeStartTiming(String str);

        void writeStopTiming(Bundle bundle);
    }

    public PerformanceCollector(PerformanceResultsWriter writer) {
        setPerformanceResultsWriter(writer);
    }

    public void setPerformanceResultsWriter(PerformanceResultsWriter writer) {
        this.mPerfWriter = writer;
    }

    @UnsupportedAppUsage
    public void beginSnapshot(String label) {
        PerformanceResultsWriter performanceResultsWriter = this.mPerfWriter;
        if (performanceResultsWriter != null) {
            performanceResultsWriter.writeBeginSnapshot(label);
        }
        startPerformanceSnapshot();
    }

    @UnsupportedAppUsage
    public Bundle endSnapshot() {
        endPerformanceSnapshot();
        PerformanceResultsWriter performanceResultsWriter = this.mPerfWriter;
        if (performanceResultsWriter != null) {
            performanceResultsWriter.writeEndSnapshot(this.mPerfSnapshot);
        }
        return this.mPerfSnapshot;
    }

    @UnsupportedAppUsage
    public void startTiming(String label) {
        PerformanceResultsWriter performanceResultsWriter = this.mPerfWriter;
        if (performanceResultsWriter != null) {
            performanceResultsWriter.writeStartTiming(label);
        }
        this.mPerfMeasurement = new Bundle();
        this.mPerfMeasurement.putParcelableArrayList(METRIC_KEY_ITERATIONS, new ArrayList());
        this.mExecTime = SystemClock.uptimeMillis();
        this.mCpuTime = Process.getElapsedCpuTime();
    }

    public Bundle addIteration(String label) {
        this.mCpuTime = Process.getElapsedCpuTime() - this.mCpuTime;
        this.mExecTime = SystemClock.uptimeMillis() - this.mExecTime;
        Bundle iteration = new Bundle();
        iteration.putString("label", label);
        iteration.putLong(METRIC_KEY_EXECUTION_TIME, this.mExecTime);
        iteration.putLong(METRIC_KEY_CPU_TIME, this.mCpuTime);
        this.mPerfMeasurement.getParcelableArrayList(METRIC_KEY_ITERATIONS).add(iteration);
        this.mExecTime = SystemClock.uptimeMillis();
        this.mCpuTime = Process.getElapsedCpuTime();
        return iteration;
    }

    @UnsupportedAppUsage
    public Bundle stopTiming(String label) {
        addIteration(label);
        PerformanceResultsWriter performanceResultsWriter = this.mPerfWriter;
        if (performanceResultsWriter != null) {
            performanceResultsWriter.writeStopTiming(this.mPerfMeasurement);
        }
        return this.mPerfMeasurement;
    }

    public void addMeasurement(String label, long value) {
        PerformanceResultsWriter performanceResultsWriter = this.mPerfWriter;
        if (performanceResultsWriter != null) {
            performanceResultsWriter.writeMeasurement(label, value);
        }
    }

    public void addMeasurement(String label, float value) {
        PerformanceResultsWriter performanceResultsWriter = this.mPerfWriter;
        if (performanceResultsWriter != null) {
            performanceResultsWriter.writeMeasurement(label, value);
        }
    }

    public void addMeasurement(String label, String value) {
        PerformanceResultsWriter performanceResultsWriter = this.mPerfWriter;
        if (performanceResultsWriter != null) {
            performanceResultsWriter.writeMeasurement(label, value);
        }
    }

    private void startPerformanceSnapshot() {
        this.mPerfSnapshot = new Bundle();
        Bundle binderCounts = getBinderCounts();
        for (String key : binderCounts.keySet()) {
            Bundle bundle = this.mPerfSnapshot;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("pre_");
            stringBuilder.append(key);
            bundle.putLong(stringBuilder.toString(), binderCounts.getLong(key));
        }
        startAllocCounting();
        this.mSnapshotExecTime = SystemClock.uptimeMillis();
        this.mSnapshotCpuTime = Process.getElapsedCpuTime();
    }

    private void endPerformanceSnapshot() {
        Runtime runtime;
        long dalvikFree;
        long dalvikAllocated;
        this.mSnapshotCpuTime = Process.getElapsedCpuTime() - this.mSnapshotCpuTime;
        this.mSnapshotExecTime = SystemClock.uptimeMillis() - this.mSnapshotExecTime;
        stopAllocCounting();
        long nativeMax = Debug.getNativeHeapSize() / 1024;
        long nativeAllocated = Debug.getNativeHeapAllocatedSize() / 1024;
        long nativeFree = Debug.getNativeHeapFreeSize() / 1024;
        MemoryInfo memInfo = new MemoryInfo();
        Debug.getMemoryInfo(memInfo);
        Runtime runtime2 = Runtime.getRuntime();
        long dalvikMax = runtime2.totalMemory() / 1024;
        long dalvikFree2 = runtime2.freeMemory() / 1024;
        long dalvikAllocated2 = dalvikMax - dalvikFree2;
        Bundle binderCounts = getBinderCounts();
        for (String key : binderCounts.keySet()) {
            runtime = runtime2;
            dalvikFree = dalvikFree2;
            dalvikAllocated = dalvikAllocated2;
            this.mPerfSnapshot.putLong(key, binderCounts.getLong(key));
            runtime2 = runtime;
            dalvikFree2 = dalvikFree;
            dalvikAllocated2 = dalvikAllocated;
        }
        dalvikAllocated = dalvikAllocated2;
        runtime = runtime2;
        dalvikFree = dalvikFree2;
        Bundle allocCounts = getAllocCounts();
        for (String key2 : allocCounts.keySet()) {
            Bundle binderCounts2 = binderCounts;
            this.mPerfSnapshot.putLong(key2, allocCounts.getLong(key2));
            binderCounts = binderCounts2;
        }
        this.mPerfSnapshot.putLong(METRIC_KEY_EXECUTION_TIME, this.mSnapshotExecTime);
        this.mPerfSnapshot.putLong(METRIC_KEY_CPU_TIME, this.mSnapshotCpuTime);
        this.mPerfSnapshot.putLong(METRIC_KEY_NATIVE_SIZE, nativeMax);
        this.mPerfSnapshot.putLong(METRIC_KEY_NATIVE_ALLOCATED, nativeAllocated);
        this.mPerfSnapshot.putLong(METRIC_KEY_NATIVE_FREE, nativeFree);
        this.mPerfSnapshot.putLong(METRIC_KEY_NATIVE_PSS, (long) memInfo.nativePss);
        this.mPerfSnapshot.putLong(METRIC_KEY_NATIVE_PRIVATE_DIRTY, (long) memInfo.nativePrivateDirty);
        this.mPerfSnapshot.putLong(METRIC_KEY_NATIVE_SHARED_DIRTY, (long) memInfo.nativeSharedDirty);
        this.mPerfSnapshot.putLong(METRIC_KEY_JAVA_SIZE, dalvikMax);
        this.mPerfSnapshot.putLong(METRIC_KEY_JAVA_ALLOCATED, dalvikAllocated);
        this.mPerfSnapshot.putLong(METRIC_KEY_JAVA_FREE, dalvikFree);
        this.mPerfSnapshot.putLong(METRIC_KEY_JAVA_PSS, (long) memInfo.dalvikPss);
        this.mPerfSnapshot.putLong(METRIC_KEY_JAVA_PRIVATE_DIRTY, (long) memInfo.dalvikPrivateDirty);
        this.mPerfSnapshot.putLong(METRIC_KEY_JAVA_SHARED_DIRTY, (long) memInfo.dalvikSharedDirty);
        this.mPerfSnapshot.putLong(METRIC_KEY_OTHER_PSS, (long) memInfo.otherPss);
        this.mPerfSnapshot.putLong(METRIC_KEY_OTHER_PRIVATE_DIRTY, (long) memInfo.otherPrivateDirty);
        this.mPerfSnapshot.putLong(METRIC_KEY_OTHER_SHARED_DIRTY, (long) memInfo.otherSharedDirty);
    }

    private static void startAllocCounting() {
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        Debug.resetAllCounts();
        Debug.startAllocCounting();
    }

    private static void stopAllocCounting() {
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        Debug.stopAllocCounting();
    }

    private static Bundle getAllocCounts() {
        Bundle results = new Bundle();
        results.putLong(METRIC_KEY_GLOBAL_ALLOC_COUNT, (long) Debug.getGlobalAllocCount());
        results.putLong(METRIC_KEY_GLOBAL_ALLOC_SIZE, (long) Debug.getGlobalAllocSize());
        results.putLong(METRIC_KEY_GLOBAL_FREED_COUNT, (long) Debug.getGlobalFreedCount());
        results.putLong(METRIC_KEY_GLOBAL_FREED_SIZE, (long) Debug.getGlobalFreedSize());
        results.putLong(METRIC_KEY_GC_INVOCATION_COUNT, (long) Debug.getGlobalGcInvocationCount());
        return results;
    }

    private static Bundle getBinderCounts() {
        Bundle results = new Bundle();
        results.putLong(METRIC_KEY_SENT_TRANSACTIONS, (long) Debug.getBinderSentTransactions());
        results.putLong(METRIC_KEY_RECEIVED_TRANSACTIONS, (long) Debug.getBinderReceivedTransactions());
        return results;
    }
}
