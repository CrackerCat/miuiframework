package android.media;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Objects;

public final class VolumeShaper implements AutoCloseable {
    private int mId;
    private final WeakReference<PlayerBase> mWeakPlayerBase;

    public static final class Configuration implements Parcelable {
        public static final Creator<Configuration> CREATOR = new Creator<Configuration>() {
            public Configuration createFromParcel(Parcel p) {
                int type = p.readInt();
                int id = p.readInt();
                if (type == 0) {
                    return new Configuration(id);
                }
                int optionFlags = p.readInt();
                double durationMs = p.readDouble();
                int interpolatorType = p.readInt();
                float firstSlope = p.readFloat();
                float lastSlope = p.readFloat();
                int length = p.readInt();
                float[] times = new float[length];
                float[] volumes = new float[length];
                for (int i = 0; i < length; i++) {
                    times[i] = p.readFloat();
                    volumes[i] = p.readFloat();
                }
                return new Configuration(type, id, optionFlags, durationMs, interpolatorType, times, volumes);
            }

            public Configuration[] newArray(int size) {
                return new Configuration[size];
            }
        };
        public static final Configuration CUBIC_RAMP = new Builder().setInterpolatorType(2).setCurve(new float[]{0.0f, 1.0f}, new float[]{0.0f, 1.0f}).setDuration(1000).build();
        public static final int INTERPOLATOR_TYPE_CUBIC = 2;
        public static final int INTERPOLATOR_TYPE_CUBIC_MONOTONIC = 3;
        public static final int INTERPOLATOR_TYPE_LINEAR = 1;
        public static final int INTERPOLATOR_TYPE_STEP = 0;
        public static final Configuration LINEAR_RAMP = new Builder().setInterpolatorType(1).setCurve(new float[]{0.0f, 1.0f}, new float[]{0.0f, 1.0f}).setDuration(1000).build();
        private static final int MAXIMUM_CURVE_POINTS = 16;
        public static final int OPTION_FLAG_CLOCK_TIME = 2;
        private static final int OPTION_FLAG_PUBLIC_ALL = 3;
        public static final int OPTION_FLAG_VOLUME_IN_DBFS = 1;
        public static final Configuration SCURVE_RAMP;
        public static final Configuration SINE_RAMP;
        static final int TYPE_ID = 0;
        static final int TYPE_SCALE = 1;
        @UnsupportedAppUsage
        private final double mDurationMs;
        @UnsupportedAppUsage
        private final int mId;
        @UnsupportedAppUsage
        private final int mInterpolatorType;
        @UnsupportedAppUsage
        private final int mOptionFlags;
        @UnsupportedAppUsage
        private final float[] mTimes;
        @UnsupportedAppUsage
        private final int mType;
        @UnsupportedAppUsage
        private final float[] mVolumes;

        public static final class Builder {
            private double mDurationMs = 1000.0d;
            private int mId = -1;
            private int mInterpolatorType = 2;
            private int mOptionFlags = 2;
            private float[] mTimes = null;
            private int mType = 1;
            private float[] mVolumes = null;

            public Builder(Configuration configuration) {
                this.mType = configuration.getType();
                this.mId = configuration.getId();
                this.mOptionFlags = configuration.getAllOptionFlags();
                this.mInterpolatorType = configuration.getInterpolatorType();
                this.mDurationMs = (double) configuration.getDuration();
                this.mTimes = (float[]) configuration.getTimes().clone();
                this.mVolumes = (float[]) configuration.getVolumes().clone();
            }

            public Builder setId(int id) {
                if (id >= -1) {
                    this.mId = id;
                    return this;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("invalid id: ");
                stringBuilder.append(id);
                throw new IllegalArgumentException(stringBuilder.toString());
            }

            public Builder setInterpolatorType(int interpolatorType) {
                if (interpolatorType == 0 || interpolatorType == 1 || interpolatorType == 2 || interpolatorType == 3) {
                    this.mInterpolatorType = interpolatorType;
                    return this;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("invalid interpolatorType: ");
                stringBuilder.append(interpolatorType);
                throw new IllegalArgumentException(stringBuilder.toString());
            }

            public Builder setOptionFlags(int optionFlags) {
                if ((optionFlags & -4) == 0) {
                    this.mOptionFlags = (this.mOptionFlags & -4) | optionFlags;
                    return this;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("invalid bits in flag: ");
                stringBuilder.append(optionFlags);
                throw new IllegalArgumentException(stringBuilder.toString());
            }

            public Builder setDuration(long durationMillis) {
                if (durationMillis > 0) {
                    this.mDurationMs = (double) durationMillis;
                    return this;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("duration: ");
                stringBuilder.append(durationMillis);
                stringBuilder.append(" not positive");
                throw new IllegalArgumentException(stringBuilder.toString());
            }

            public Builder setCurve(float[] times, float[] volumes) {
                boolean z = true;
                if ((this.mOptionFlags & 1) == 0) {
                    z = false;
                }
                Configuration.checkCurveForErrorsAndThrowException(times, volumes, z, false);
                this.mTimes = (float[]) times.clone();
                this.mVolumes = (float[]) volumes.clone();
                return this;
            }

            public Builder reflectTimes() {
                float[] fArr;
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, (this.mOptionFlags & 1) != 0, true);
                int i = 0;
                while (true) {
                    fArr = this.mTimes;
                    if (i >= fArr.length / 2) {
                        break;
                    }
                    float temp = fArr[i];
                    fArr[i] = 1.0f - fArr[(fArr.length - 1) - i];
                    fArr[(fArr.length - 1) - i] = 1.0f - temp;
                    fArr = this.mVolumes;
                    temp = fArr[i];
                    fArr[i] = fArr[(fArr.length - 1) - i];
                    fArr[(fArr.length - 1) - i] = temp;
                    i++;
                }
                if ((1 & fArr.length) != 0) {
                    fArr[i] = 1.0f - fArr[i];
                }
                return this;
            }

            public Builder invertVolumes() {
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, (this.mOptionFlags & 1) != 0, true);
                float max = this.mVolumes;
                float min = max[0];
                max = max[0];
                int i = 1;
                while (true) {
                    float[] fArr = this.mVolumes;
                    if (i >= fArr.length) {
                        break;
                    }
                    if (fArr[i] < min) {
                        min = fArr[i];
                    } else if (fArr[i] > max) {
                        max = fArr[i];
                    }
                    i++;
                }
                float maxmin = max + min;
                int i2 = 0;
                while (true) {
                    float[] fArr2 = this.mVolumes;
                    if (i2 >= fArr2.length) {
                        return this;
                    }
                    fArr2[i2] = maxmin - fArr2[i2];
                    i2++;
                }
            }

            public Builder scaleToEndVolume(float volume) {
                boolean log = (this.mOptionFlags & 1) != 0;
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, log, true);
                Configuration.checkValidVolumeAndThrowException(volume, log);
                float[] fArr = this.mVolumes;
                float startVolume = fArr[0];
                float endVolume = fArr[fArr.length - 1];
                float scale;
                int i;
                float[] fArr2;
                if (endVolume != startVolume) {
                    scale = (volume - startVolume) / (endVolume - startVolume);
                    i = 0;
                    while (true) {
                        fArr2 = this.mVolumes;
                        if (i >= fArr2.length) {
                            break;
                        }
                        fArr2[i] = ((fArr2[i] - startVolume) * scale) + startVolume;
                        i++;
                    }
                } else {
                    scale = volume - startVolume;
                    i = 0;
                    while (true) {
                        fArr2 = this.mVolumes;
                        if (i >= fArr2.length) {
                            break;
                        }
                        fArr2[i] = fArr2[i] + (this.mTimes[i] * scale);
                        i++;
                    }
                }
                Configuration.clampVolume(this.mVolumes, log);
                return this;
            }

            public Builder scaleToStartVolume(float volume) {
                boolean log = (this.mOptionFlags & 1) != 0;
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, log, true);
                Configuration.checkValidVolumeAndThrowException(volume, log);
                float[] fArr = this.mVolumes;
                float startVolume = fArr[0];
                float endVolume = fArr[fArr.length - 1];
                float scale;
                int i;
                float[] fArr2;
                if (endVolume != startVolume) {
                    scale = (volume - endVolume) / (startVolume - endVolume);
                    i = 0;
                    while (true) {
                        fArr2 = this.mVolumes;
                        if (i >= fArr2.length) {
                            break;
                        }
                        fArr2[i] = ((fArr2[i] - endVolume) * scale) + endVolume;
                        i++;
                    }
                } else {
                    scale = volume - startVolume;
                    i = 0;
                    while (true) {
                        fArr2 = this.mVolumes;
                        if (i >= fArr2.length) {
                            break;
                        }
                        fArr2[i] = fArr2[i] + ((1.0f - this.mTimes[i]) * scale);
                        i++;
                    }
                }
                Configuration.clampVolume(this.mVolumes, log);
                return this;
            }

            public Configuration build() {
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, (this.mOptionFlags & 1) != 0, true);
                return new Configuration(this.mType, this.mId, this.mOptionFlags, this.mDurationMs, this.mInterpolatorType, this.mTimes, this.mVolumes);
            }
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface InterpolatorType {
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface OptionFlag {
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface Type {
        }

        public static int getMaximumCurvePoints() {
            return 16;
        }

        static {
            float[] times = new float[16];
            float[] sines = new float[16];
            float[] scurve = new float[16];
            for (int i = 0; i < 16; i++) {
                times[i] = ((float) i) / 15.0f;
                float sine = (float) Math.sin((((double) times[i]) * 3.141592653589793d) / 2.0d);
                sines[i] = sine;
                scurve[i] = sine * sine;
            }
            SINE_RAMP = new Builder().setInterpolatorType(2).setCurve(times, sines).setDuration(1000).build();
            SCURVE_RAMP = new Builder().setInterpolatorType(2).setCurve(times, scurve).setDuration(1000).build();
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("VolumeShaper.Configuration{mType = ");
            stringBuilder.append(this.mType);
            stringBuilder.append(", mId = ");
            stringBuilder.append(this.mId);
            String str = "}";
            if (this.mType != 0) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(", mOptionFlags = 0x");
                stringBuilder2.append(Integer.toHexString(this.mOptionFlags).toUpperCase());
                stringBuilder2.append(", mDurationMs = ");
                stringBuilder2.append(this.mDurationMs);
                stringBuilder2.append(", mInterpolatorType = ");
                stringBuilder2.append(this.mInterpolatorType);
                stringBuilder2.append(", mTimes[] = ");
                stringBuilder2.append(Arrays.toString(this.mTimes));
                stringBuilder2.append(", mVolumes[] = ");
                stringBuilder2.append(Arrays.toString(this.mVolumes));
                stringBuilder2.append(str);
                str = stringBuilder2.toString();
            }
            stringBuilder.append(str);
            return stringBuilder.toString();
        }

        public int hashCode() {
            if (this.mType == 0) {
                return Objects.hash(new Object[]{Integer.valueOf(this.mType), Integer.valueOf(this.mId)});
            }
            return Objects.hash(new Object[]{Integer.valueOf(this.mType), Integer.valueOf(this.mId), Integer.valueOf(this.mOptionFlags), Double.valueOf(this.mDurationMs), Integer.valueOf(this.mInterpolatorType), Integer.valueOf(Arrays.hashCode(this.mTimes)), Integer.valueOf(Arrays.hashCode(this.mVolumes))});
        }

        public boolean equals(Object o) {
            if (!(o instanceof Configuration)) {
                return false;
            }
            boolean z = true;
            if (o == this) {
                return true;
            }
            Configuration other = (Configuration) o;
            int i = this.mType;
            if (!(i == other.mType && this.mId == other.mId && (i == 0 || (this.mOptionFlags == other.mOptionFlags && this.mDurationMs == other.mDurationMs && this.mInterpolatorType == other.mInterpolatorType && Arrays.equals(this.mTimes, other.mTimes) && Arrays.equals(this.mVolumes, other.mVolumes))))) {
                z = false;
            }
            return z;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mType);
            dest.writeInt(this.mId);
            if (this.mType != 0) {
                dest.writeInt(this.mOptionFlags);
                dest.writeDouble(this.mDurationMs);
                dest.writeInt(this.mInterpolatorType);
                dest.writeFloat(0.0f);
                dest.writeFloat(0.0f);
                dest.writeInt(this.mTimes.length);
                int i = 0;
                while (true) {
                    float[] fArr = this.mTimes;
                    if (i < fArr.length) {
                        dest.writeFloat(fArr[i]);
                        dest.writeFloat(this.mVolumes[i]);
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }

        public Configuration(int id) {
            if (id >= 0) {
                this.mType = 0;
                this.mId = id;
                this.mInterpolatorType = 0;
                this.mOptionFlags = 0;
                this.mDurationMs = 0.0d;
                this.mTimes = null;
                this.mVolumes = null;
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("negative id ");
            stringBuilder.append(id);
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        @UnsupportedAppUsage
        private Configuration(int type, int id, int optionFlags, double durationMs, int interpolatorType, float[] times, float[] volumes) {
            this.mType = type;
            this.mId = id;
            this.mOptionFlags = optionFlags;
            this.mDurationMs = durationMs;
            this.mInterpolatorType = interpolatorType;
            this.mTimes = times;
            this.mVolumes = volumes;
        }

        public int getType() {
            return this.mType;
        }

        public int getId() {
            return this.mId;
        }

        public int getInterpolatorType() {
            return this.mInterpolatorType;
        }

        public int getOptionFlags() {
            return this.mOptionFlags & 3;
        }

        /* Access modifiers changed, original: 0000 */
        public int getAllOptionFlags() {
            return this.mOptionFlags;
        }

        public long getDuration() {
            return (long) this.mDurationMs;
        }

        public float[] getTimes() {
            return this.mTimes;
        }

        public float[] getVolumes() {
            return this.mVolumes;
        }

        private static String checkCurveForErrors(float[] times, float[] volumes, boolean log) {
            if (times == null) {
                return "times array must be non-null";
            }
            if (volumes == null) {
                return "volumes array must be non-null";
            }
            if (times.length != volumes.length) {
                return "array length must match";
            }
            if (times.length < 2) {
                return "array length must be at least 2";
            }
            if (times.length > 16) {
                return "array length must be no larger than 16";
            }
            if (times[0] != 0.0f) {
                return "times must start at 0.f";
            }
            if (times[times.length - 1] != 1.0f) {
                return "times must end at 1.f";
            }
            int i;
            StringBuilder stringBuilder;
            for (i = 1; i < times.length; i++) {
                if (times[i] <= times[i - 1]) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("times not monotonic increasing, check index ");
                    stringBuilder.append(i);
                    return stringBuilder.toString();
                }
            }
            if (log) {
                for (i = 0; i < volumes.length; i++) {
                    if (volumes[i] > 0.0f) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("volumes for log scale cannot be positive, check index ");
                        stringBuilder.append(i);
                        return stringBuilder.toString();
                    }
                }
            } else {
                i = 0;
                while (i < volumes.length) {
                    if (volumes[i] < 0.0f || volumes[i] > 1.0f) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("volumes for linear scale must be between 0.f and 1.f, check index ");
                        stringBuilder.append(i);
                        return stringBuilder.toString();
                    }
                    i++;
                }
            }
            return null;
        }

        private static void checkCurveForErrorsAndThrowException(float[] times, float[] volumes, boolean log, boolean ise) {
            String error = checkCurveForErrors(times, volumes, log);
            if (error == null) {
                return;
            }
            if (ise) {
                throw new IllegalStateException(error);
            }
            throw new IllegalArgumentException(error);
        }

        private static void checkValidVolumeAndThrowException(float volume, boolean log) {
            if (log) {
                if (volume > 0.0f) {
                    throw new IllegalArgumentException("dbfs volume must be 0.f or less");
                }
            } else if (volume < 0.0f || volume > 1.0f) {
                throw new IllegalArgumentException("volume must be >= 0.f and <= 1.f");
            }
        }

        private static void clampVolume(float[] volumes, boolean log) {
            int i;
            if (log) {
                for (i = 0; i < volumes.length; i++) {
                    if (volumes[i] > 0.0f) {
                        volumes[i] = 0.0f;
                    }
                }
                return;
            }
            for (i = 0; i < volumes.length; i++) {
                if (volumes[i] < 0.0f) {
                    volumes[i] = 0.0f;
                } else if (volumes[i] > 1.0f) {
                    volumes[i] = 1.0f;
                }
            }
        }
    }

    public static final class Operation implements Parcelable {
        public static final Creator<Operation> CREATOR = new Creator<Operation>() {
            public Operation createFromParcel(Parcel p) {
                return new Operation(p.readInt(), p.readInt(), p.readFloat());
            }

            public Operation[] newArray(int size) {
                return new Operation[size];
            }
        };
        private static final int FLAG_CREATE_IF_NEEDED = 16;
        private static final int FLAG_DEFER = 8;
        private static final int FLAG_JOIN = 4;
        private static final int FLAG_NONE = 0;
        private static final int FLAG_PUBLIC_ALL = 3;
        private static final int FLAG_REVERSE = 1;
        private static final int FLAG_TERMINATE = 2;
        public static final Operation PLAY = new Builder().build();
        public static final Operation REVERSE = new Builder().reverse().build();
        @UnsupportedAppUsage
        private final int mFlags;
        @UnsupportedAppUsage
        private final int mReplaceId;
        @UnsupportedAppUsage
        private final float mXOffset;

        public static final class Builder {
            int mFlags;
            int mReplaceId;
            float mXOffset;

            public Builder() {
                this.mFlags = 0;
                this.mReplaceId = -1;
                this.mXOffset = Float.NaN;
            }

            public Builder(Operation operation) {
                this.mReplaceId = operation.mReplaceId;
                this.mFlags = operation.mFlags;
                this.mXOffset = operation.mXOffset;
            }

            public Builder replace(int id, boolean join) {
                this.mReplaceId = id;
                if (join) {
                    this.mFlags |= 4;
                } else {
                    this.mFlags &= -5;
                }
                return this;
            }

            public Builder defer() {
                this.mFlags |= 8;
                return this;
            }

            public Builder terminate() {
                this.mFlags |= 2;
                return this;
            }

            public Builder reverse() {
                this.mFlags ^= 1;
                return this;
            }

            public Builder createIfNeeded() {
                this.mFlags |= 16;
                return this;
            }

            public Builder setXOffset(float xOffset) {
                if (xOffset < -0.0f) {
                    throw new IllegalArgumentException("Negative xOffset not allowed");
                } else if (xOffset <= 1.0f) {
                    this.mXOffset = xOffset;
                    return this;
                } else {
                    throw new IllegalArgumentException("xOffset > 1.f not allowed");
                }
            }

            private Builder setFlags(int flags) {
                if ((flags & -4) == 0) {
                    this.mFlags = (this.mFlags & -4) | flags;
                    return this;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("flag has unknown bits set: ");
                stringBuilder.append(flags);
                throw new IllegalArgumentException(stringBuilder.toString());
            }

            public Operation build() {
                return new Operation(this.mFlags, this.mReplaceId, this.mXOffset);
            }
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface Flag {
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("VolumeShaper.Operation{mFlags = 0x");
            stringBuilder.append(Integer.toHexString(this.mFlags).toUpperCase());
            stringBuilder.append(", mReplaceId = ");
            stringBuilder.append(this.mReplaceId);
            stringBuilder.append(", mXOffset = ");
            stringBuilder.append(this.mXOffset);
            stringBuilder.append("}");
            return stringBuilder.toString();
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(this.mFlags), Integer.valueOf(this.mReplaceId), Float.valueOf(this.mXOffset)});
        }

        public boolean equals(Object o) {
            if (!(o instanceof Operation)) {
                return false;
            }
            boolean z = true;
            if (o == this) {
                return true;
            }
            Operation other = (Operation) o;
            if (!(this.mFlags == other.mFlags && this.mReplaceId == other.mReplaceId && Float.compare(this.mXOffset, other.mXOffset) == 0)) {
                z = false;
            }
            return z;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mFlags);
            dest.writeInt(this.mReplaceId);
            dest.writeFloat(this.mXOffset);
        }

        @UnsupportedAppUsage
        private Operation(int flags, int replaceId, float xOffset) {
            this.mFlags = flags;
            this.mReplaceId = replaceId;
            this.mXOffset = xOffset;
        }
    }

    public static final class State implements Parcelable {
        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel p) {
                return new State(p.readFloat(), p.readFloat());
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
        @UnsupportedAppUsage
        private float mVolume;
        @UnsupportedAppUsage
        private float mXOffset;

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("VolumeShaper.State{mVolume = ");
            stringBuilder.append(this.mVolume);
            stringBuilder.append(", mXOffset = ");
            stringBuilder.append(this.mXOffset);
            stringBuilder.append("}");
            return stringBuilder.toString();
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Float.valueOf(this.mVolume), Float.valueOf(this.mXOffset)});
        }

        public boolean equals(Object o) {
            if (!(o instanceof State)) {
                return false;
            }
            boolean z = true;
            if (o == this) {
                return true;
            }
            State other = (State) o;
            if (!(this.mVolume == other.mVolume && this.mXOffset == other.mXOffset)) {
                z = false;
            }
            return z;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(this.mVolume);
            dest.writeFloat(this.mXOffset);
        }

        @UnsupportedAppUsage
        State(float volume, float xOffset) {
            this.mVolume = volume;
            this.mXOffset = xOffset;
        }

        public float getVolume() {
            return this.mVolume;
        }

        public float getXOffset() {
            return this.mXOffset;
        }
    }

    VolumeShaper(Configuration configuration, PlayerBase playerBase) {
        this.mWeakPlayerBase = new WeakReference(playerBase);
        this.mId = applyPlayer(configuration, new Builder().defer().build());
    }

    /* Access modifiers changed, original: 0000 */
    public int getId() {
        return this.mId;
    }

    public void apply(Operation operation) {
        applyPlayer(new Configuration(this.mId), operation);
    }

    public void replace(Configuration configuration, Operation operation, boolean join) {
        this.mId = applyPlayer(configuration, new Builder(operation).replace(this.mId, join).build());
    }

    public float getVolume() {
        return getStatePlayer(this.mId).getVolume();
    }

    public void close() {
        try {
            applyPlayer(new Configuration(this.mId), new Builder().terminate().build());
        } catch (IllegalStateException e) {
        }
        WeakReference weakReference = this.mWeakPlayerBase;
        if (weakReference != null) {
            weakReference.clear();
        }
    }

    /* Access modifiers changed, original: protected */
    public void finalize() {
        close();
    }

    private int applyPlayer(Configuration configuration, Operation operation) {
        WeakReference weakReference = this.mWeakPlayerBase;
        if (weakReference != null) {
            PlayerBase player = (PlayerBase) weakReference.get();
            if (player != null) {
                player = player.playerApplyVolumeShaper(configuration, operation);
                if (player >= null) {
                    return player;
                }
                if (player == -38) {
                    throw new IllegalStateException("player or VolumeShaper deallocated");
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("invalid configuration or operation: ");
                stringBuilder.append(player);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            throw new IllegalStateException("player deallocated");
        }
        throw new IllegalStateException("uninitialized shaper");
    }

    private State getStatePlayer(int id) {
        WeakReference weakReference = this.mWeakPlayerBase;
        if (weakReference != null) {
            PlayerBase player = (PlayerBase) weakReference.get();
            if (player != null) {
                player = player.playerGetVolumeShaperState(id);
                if (player != null) {
                    return player;
                }
                throw new IllegalStateException("shaper cannot be found");
            }
            throw new IllegalStateException("player deallocated");
        }
        throw new IllegalStateException("uninitialized shaper");
    }
}
