package android.app.admin;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class SystemUpdatePolicy implements Parcelable {
    public static final Creator<SystemUpdatePolicy> CREATOR = new Creator<SystemUpdatePolicy>() {
        public SystemUpdatePolicy createFromParcel(Parcel source) {
            SystemUpdatePolicy policy = new SystemUpdatePolicy();
            policy.mPolicyType = source.readInt();
            policy.mMaintenanceWindowStart = source.readInt();
            policy.mMaintenanceWindowEnd = source.readInt();
            int freezeCount = source.readInt();
            policy.mFreezePeriods.ensureCapacity(freezeCount);
            for (int i = 0; i < freezeCount; i++) {
                policy.mFreezePeriods.add(new FreezePeriod(MonthDay.of(source.readInt(), source.readInt()), MonthDay.of(source.readInt(), source.readInt())));
            }
            return policy;
        }

        public SystemUpdatePolicy[] newArray(int size) {
            return new SystemUpdatePolicy[size];
        }
    };
    static final int FREEZE_PERIOD_MAX_LENGTH = 90;
    static final int FREEZE_PERIOD_MIN_SEPARATION = 60;
    private static final String KEY_FREEZE_END = "end";
    private static final String KEY_FREEZE_START = "start";
    private static final String KEY_FREEZE_TAG = "freeze";
    private static final String KEY_INSTALL_WINDOW_END = "install_window_end";
    private static final String KEY_INSTALL_WINDOW_START = "install_window_start";
    private static final String KEY_POLICY_TYPE = "policy_type";
    private static final String TAG = "SystemUpdatePolicy";
    public static final int TYPE_INSTALL_AUTOMATIC = 1;
    public static final int TYPE_INSTALL_WINDOWED = 2;
    @SystemApi
    public static final int TYPE_PAUSE = 4;
    public static final int TYPE_POSTPONE = 3;
    private static final int TYPE_UNKNOWN = -1;
    private static final int WINDOW_BOUNDARY = 1440;
    private final ArrayList<FreezePeriod> mFreezePeriods;
    private int mMaintenanceWindowEnd;
    private int mMaintenanceWindowStart;
    private int mPolicyType;

    @SystemApi
    public static class InstallationOption {
        private long mEffectiveTime;
        private final int mType;

        @Retention(RetentionPolicy.SOURCE)
        @interface InstallationOptionType {
        }

        InstallationOption(int type, long effectiveTime) {
            this.mType = type;
            this.mEffectiveTime = effectiveTime;
        }

        public int getType() {
            return this.mType;
        }

        public long getEffectiveTime() {
            return this.mEffectiveTime;
        }

        /* Access modifiers changed, original: protected */
        public void limitEffectiveTime(long otherTime) {
            this.mEffectiveTime = Long.min(this.mEffectiveTime, otherTime);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface SystemUpdatePolicyType {
    }

    public static final class ValidationFailedException extends IllegalArgumentException implements Parcelable {
        public static final Creator<ValidationFailedException> CREATOR = new Creator<ValidationFailedException>() {
            public ValidationFailedException createFromParcel(Parcel source) {
                return new ValidationFailedException(source.readInt(), source.readString(), null);
            }

            public ValidationFailedException[] newArray(int size) {
                return new ValidationFailedException[size];
            }
        };
        public static final int ERROR_COMBINED_FREEZE_PERIOD_TOO_CLOSE = 6;
        public static final int ERROR_COMBINED_FREEZE_PERIOD_TOO_LONG = 5;
        public static final int ERROR_DUPLICATE_OR_OVERLAP = 2;
        public static final int ERROR_NEW_FREEZE_PERIOD_TOO_CLOSE = 4;
        public static final int ERROR_NEW_FREEZE_PERIOD_TOO_LONG = 3;
        public static final int ERROR_NONE = 0;
        public static final int ERROR_UNKNOWN = 1;
        private final int mErrorCode;

        @Retention(RetentionPolicy.SOURCE)
        @interface ValidationFailureType {
        }

        /* synthetic */ ValidationFailedException(int x0, String x1, AnonymousClass1 x2) {
            this(x0, x1);
        }

        private ValidationFailedException(int errorCode, String message) {
            super(message);
            this.mErrorCode = errorCode;
        }

        public int getErrorCode() {
            return this.mErrorCode;
        }

        public static ValidationFailedException duplicateOrOverlapPeriods() {
            return new ValidationFailedException(2, "Found duplicate or overlapping periods");
        }

        public static ValidationFailedException freezePeriodTooLong(String message) {
            return new ValidationFailedException(3, message);
        }

        public static ValidationFailedException freezePeriodTooClose(String message) {
            return new ValidationFailedException(4, message);
        }

        public static ValidationFailedException combinedPeriodTooLong(String message) {
            return new ValidationFailedException(5, message);
        }

        public static ValidationFailedException combinedPeriodTooClose(String message) {
            return new ValidationFailedException(6, message);
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mErrorCode);
            dest.writeString(getMessage());
        }
    }

    /* synthetic */ SystemUpdatePolicy(AnonymousClass1 x0) {
        this();
    }

    private SystemUpdatePolicy() {
        this.mPolicyType = -1;
        this.mFreezePeriods = new ArrayList();
    }

    public static SystemUpdatePolicy createAutomaticInstallPolicy() {
        SystemUpdatePolicy policy = new SystemUpdatePolicy();
        policy.mPolicyType = 1;
        return policy;
    }

    public static SystemUpdatePolicy createWindowedInstallPolicy(int startTime, int endTime) {
        if (startTime < 0 || startTime >= 1440 || endTime < 0 || endTime >= 1440) {
            throw new IllegalArgumentException("startTime and endTime must be inside [0, 1440)");
        }
        SystemUpdatePolicy policy = new SystemUpdatePolicy();
        policy.mPolicyType = 2;
        policy.mMaintenanceWindowStart = startTime;
        policy.mMaintenanceWindowEnd = endTime;
        return policy;
    }

    public static SystemUpdatePolicy createPostponeInstallPolicy() {
        SystemUpdatePolicy policy = new SystemUpdatePolicy();
        policy.mPolicyType = 3;
        return policy;
    }

    public int getPolicyType() {
        return this.mPolicyType;
    }

    public int getInstallWindowStart() {
        if (this.mPolicyType == 2) {
            return this.mMaintenanceWindowStart;
        }
        return -1;
    }

    public int getInstallWindowEnd() {
        if (this.mPolicyType == 2) {
            return this.mMaintenanceWindowEnd;
        }
        return -1;
    }

    public boolean isValid() {
        try {
            validateType();
            validateFreezePeriods();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void validateType() {
        int i = this.mPolicyType;
        if (i != 1 && i != 3) {
            if (i == 2) {
                i = this.mMaintenanceWindowStart;
                if (i >= 0 && i < 1440) {
                    i = this.mMaintenanceWindowEnd;
                    if (i >= 0 && i < 1440) {
                        return;
                    }
                }
                throw new IllegalArgumentException("Invalid maintenance window");
            }
            throw new IllegalArgumentException("Invalid system update policy type.");
        }
    }

    public SystemUpdatePolicy setFreezePeriods(List<FreezePeriod> freezePeriods) {
        FreezePeriod.validatePeriods(freezePeriods);
        this.mFreezePeriods.clear();
        this.mFreezePeriods.addAll(freezePeriods);
        return this;
    }

    public List<FreezePeriod> getFreezePeriods() {
        return Collections.unmodifiableList(this.mFreezePeriods);
    }

    public Pair<LocalDate, LocalDate> getCurrentFreezePeriod(LocalDate now) {
        Iterator it = this.mFreezePeriods.iterator();
        while (it.hasNext()) {
            FreezePeriod interval = (FreezePeriod) it.next();
            if (interval.contains(now)) {
                return interval.toCurrentOrFutureRealDates(now);
            }
        }
        return null;
    }

    private long timeUntilNextFreezePeriod(long now) {
        List<FreezePeriod> sortedPeriods = FreezePeriod.canonicalizePeriods(this.mFreezePeriods);
        LocalDate nowDate = millisToDate(now);
        LocalDate nextFreezeStart = null;
        for (FreezePeriod interval : sortedPeriods) {
            if (interval.after(nowDate)) {
                nextFreezeStart = interval.toCurrentOrFutureRealDates(nowDate).first;
                break;
            } else if (interval.contains(nowDate)) {
                throw new IllegalArgumentException("Given date is inside a freeze period");
            }
        }
        if (nextFreezeStart == null) {
            nextFreezeStart = ((FreezePeriod) sortedPeriods.get(0)).toCurrentOrFutureRealDates(nowDate).first;
        }
        return dateToMillis(nextFreezeStart) - now;
    }

    public void validateFreezePeriods() {
        FreezePeriod.validatePeriods(this.mFreezePeriods);
    }

    public void validateAgainstPreviousFreezePeriod(LocalDate prevPeriodStart, LocalDate prevPeriodEnd, LocalDate now) {
        FreezePeriod.validateAgainstPreviousFreezePeriod(this.mFreezePeriods, prevPeriodStart, prevPeriodEnd, now);
    }

    @SystemApi
    public InstallationOption getInstallationOptionAt(long when) {
        Pair<LocalDate, LocalDate> current = getCurrentFreezePeriod(millisToDate(when));
        if (current != null) {
            return new InstallationOption(4, dateToMillis(roundUpLeapDay((LocalDate) current.second).plusDays(1)) - when);
        }
        InstallationOption option = getInstallationOptionRegardlessFreezeAt(when);
        if (this.mFreezePeriods.size() > 0) {
            option.limitEffectiveTime(timeUntilNextFreezePeriod(when));
        }
        return option;
    }

    private InstallationOption getInstallationOptionRegardlessFreezeAt(long when) {
        int i = this.mPolicyType;
        if (i == 1 || i == 3) {
            return new InstallationOption(this.mPolicyType, Long.MAX_VALUE);
        }
        if (i == 2) {
            Calendar query = Calendar.getInstance();
            query.setTimeInMillis(when);
            long whenMillis = ((TimeUnit.HOURS.toMillis((long) query.get(11)) + TimeUnit.MINUTES.toMillis((long) query.get(12))) + TimeUnit.SECONDS.toMillis((long) query.get(13))) + ((long) query.get(14));
            long windowStartMillis = TimeUnit.MINUTES.toMillis((long) this.mMaintenanceWindowStart);
            long windowEndMillis = TimeUnit.MINUTES.toMillis((long) this.mMaintenanceWindowEnd);
            long dayInMillis = TimeUnit.DAYS.toMillis(1);
            if ((windowStartMillis > whenMillis || whenMillis > windowEndMillis) && (windowStartMillis <= windowEndMillis || (windowStartMillis > whenMillis && whenMillis > windowEndMillis))) {
                return new InstallationOption(4, ((windowStartMillis - whenMillis) + dayInMillis) % dayInMillis);
            }
            return new InstallationOption(1, ((windowEndMillis - whenMillis) + dayInMillis) % dayInMillis);
        }
        throw new RuntimeException("Unknown policy type");
    }

    private static LocalDate roundUpLeapDay(LocalDate date) {
        if (date.isLeapYear() && date.getMonthValue() == 2 && date.getDayOfMonth() == 28) {
            return date.plusDays(1);
        }
        return date;
    }

    private static LocalDate millisToDate(long when) {
        return Instant.ofEpochMilli(when).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static long dateToMillis(LocalDate when) {
        return LocalDateTime.of(when, LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public String toString() {
        return String.format("SystemUpdatePolicy (type: %d, windowStart: %d, windowEnd: %d, freezes: [%s])", new Object[]{Integer.valueOf(this.mPolicyType), Integer.valueOf(this.mMaintenanceWindowStart), Integer.valueOf(this.mMaintenanceWindowEnd), this.mFreezePeriods.stream().map(-$$Lambda$SystemUpdatePolicy$cfrSWvZcAu30PIPvKA2LGQbmTew.INSTANCE).collect(Collectors.joining(","))});
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPolicyType);
        dest.writeInt(this.mMaintenanceWindowStart);
        dest.writeInt(this.mMaintenanceWindowEnd);
        int freezeCount = this.mFreezePeriods.size();
        dest.writeInt(freezeCount);
        for (int i = 0; i < freezeCount; i++) {
            FreezePeriod interval = (FreezePeriod) this.mFreezePeriods.get(i);
            dest.writeInt(interval.getStart().getMonthValue());
            dest.writeInt(interval.getStart().getDayOfMonth());
            dest.writeInt(interval.getEnd().getMonthValue());
            dest.writeInt(interval.getEnd().getDayOfMonth());
        }
    }

    public static SystemUpdatePolicy restoreFromXml(XmlPullParser parser) {
        try {
            SystemUpdatePolicy policy = new SystemUpdatePolicy();
            String value = parser.getAttributeValue(null, KEY_POLICY_TYPE);
            if (value != null) {
                policy.mPolicyType = Integer.parseInt(value);
                value = parser.getAttributeValue(null, KEY_INSTALL_WINDOW_START);
                if (value != null) {
                    policy.mMaintenanceWindowStart = Integer.parseInt(value);
                }
                value = parser.getAttributeValue(null, KEY_INSTALL_WINDOW_END);
                if (value != null) {
                    policy.mMaintenanceWindowEnd = Integer.parseInt(value);
                }
                int outerDepth = parser.getDepth();
                while (true) {
                    int next = parser.next();
                    int type = next;
                    if (next == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                        return policy;
                    }
                    if (type != 3) {
                        if (type != 4) {
                            if (parser.getName().equals(KEY_FREEZE_TAG)) {
                                policy.mFreezePeriods.add(new FreezePeriod(MonthDay.parse(parser.getAttributeValue(null, "start")), MonthDay.parse(parser.getAttributeValue(null, "end"))));
                            }
                        }
                    }
                }
                return policy;
            }
        } catch (IOException | NumberFormatException | XmlPullParserException e) {
            Log.w(TAG, "Load xml failed", e);
        }
        return null;
    }

    public void saveToXml(XmlSerializer out) throws IOException {
        out.attribute(null, KEY_POLICY_TYPE, Integer.toString(this.mPolicyType));
        out.attribute(null, KEY_INSTALL_WINDOW_START, Integer.toString(this.mMaintenanceWindowStart));
        out.attribute(null, KEY_INSTALL_WINDOW_END, Integer.toString(this.mMaintenanceWindowEnd));
        for (int i = 0; i < this.mFreezePeriods.size(); i++) {
            FreezePeriod interval = (FreezePeriod) this.mFreezePeriods.get(i);
            String str = KEY_FREEZE_TAG;
            out.startTag(null, str);
            out.attribute(null, "start", interval.getStart().toString());
            out.attribute(null, "end", interval.getEnd().toString());
            out.endTag(null, str);
        }
    }
}
