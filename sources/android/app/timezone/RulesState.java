package android.app.timezone;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.format.DateFormat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RulesState implements Parcelable {
    private static final byte BYTE_FALSE = (byte) 0;
    private static final byte BYTE_TRUE = (byte) 1;
    public static final Creator<RulesState> CREATOR = new Creator<RulesState>() {
        public RulesState createFromParcel(Parcel in) {
            return RulesState.createFromParcel(in);
        }

        public RulesState[] newArray(int size) {
            return new RulesState[size];
        }
    };
    public static final int DISTRO_STATUS_INSTALLED = 2;
    public static final int DISTRO_STATUS_NONE = 1;
    public static final int DISTRO_STATUS_UNKNOWN = 0;
    public static final int STAGED_OPERATION_INSTALL = 3;
    public static final int STAGED_OPERATION_NONE = 1;
    public static final int STAGED_OPERATION_UNINSTALL = 2;
    public static final int STAGED_OPERATION_UNKNOWN = 0;
    private final String mBaseRulesVersion;
    private final DistroFormatVersion mDistroFormatVersionSupported;
    private final int mDistroStatus;
    private final DistroRulesVersion mInstalledDistroRulesVersion;
    private final boolean mOperationInProgress;
    private final DistroRulesVersion mStagedDistroRulesVersion;
    private final int mStagedOperationType;

    @Retention(RetentionPolicy.SOURCE)
    private @interface DistroStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface StagedOperationType {
    }

    public RulesState(String baseRulesVersion, DistroFormatVersion distroFormatVersionSupported, boolean operationInProgress, int stagedOperationType, DistroRulesVersion stagedDistroRulesVersion, int distroStatus, DistroRulesVersion installedDistroRulesVersion) {
        this.mBaseRulesVersion = Utils.validateRulesVersion("baseRulesVersion", baseRulesVersion);
        this.mDistroFormatVersionSupported = (DistroFormatVersion) Utils.validateNotNull("distroFormatVersionSupported", distroFormatVersionSupported);
        this.mOperationInProgress = operationInProgress;
        if (!operationInProgress || stagedOperationType == 0) {
            this.mStagedOperationType = validateStagedOperation(stagedOperationType);
            boolean z = true;
            this.mStagedDistroRulesVersion = (DistroRulesVersion) Utils.validateConditionalNull(this.mStagedOperationType == 3, "stagedDistroRulesVersion", stagedDistroRulesVersion);
            this.mDistroStatus = validateDistroStatus(distroStatus);
            if (this.mDistroStatus != 2) {
                z = false;
            }
            this.mInstalledDistroRulesVersion = (DistroRulesVersion) Utils.validateConditionalNull(z, "installedDistroRulesVersion", installedDistroRulesVersion);
            return;
        }
        throw new IllegalArgumentException("stagedOperationType != STAGED_OPERATION_UNKNOWN");
    }

    public String getBaseRulesVersion() {
        return this.mBaseRulesVersion;
    }

    public boolean isOperationInProgress() {
        return this.mOperationInProgress;
    }

    public int getStagedOperationType() {
        return this.mStagedOperationType;
    }

    public DistroRulesVersion getStagedDistroRulesVersion() {
        return this.mStagedDistroRulesVersion;
    }

    public int getDistroStatus() {
        return this.mDistroStatus;
    }

    public DistroRulesVersion getInstalledDistroRulesVersion() {
        return this.mInstalledDistroRulesVersion;
    }

    public boolean isDistroFormatVersionSupported(DistroFormatVersion distroFormatVersion) {
        return this.mDistroFormatVersionSupported.supports(distroFormatVersion);
    }

    public boolean isBaseVersionNewerThan(DistroRulesVersion distroRulesVersion) {
        return this.mBaseRulesVersion.compareTo(distroRulesVersion.getRulesVersion()) > 0;
    }

    private static RulesState createFromParcel(Parcel in) {
        return new RulesState(in.readString(), (DistroFormatVersion) in.readParcelable(null), in.readByte() == (byte) 1, in.readByte(), (DistroRulesVersion) in.readParcelable(null), in.readByte(), (DistroRulesVersion) in.readParcelable(null));
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mBaseRulesVersion);
        out.writeParcelable(this.mDistroFormatVersionSupported, 0);
        out.writeByte(this.mOperationInProgress);
        out.writeByte((byte) this.mStagedOperationType);
        out.writeParcelable(this.mStagedDistroRulesVersion, 0);
        out.writeByte((byte) this.mDistroStatus);
        out.writeParcelable(this.mInstalledDistroRulesVersion, 0);
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RulesState that = (RulesState) o;
        if (this.mOperationInProgress != that.mOperationInProgress || this.mStagedOperationType != that.mStagedOperationType || this.mDistroStatus != that.mDistroStatus || !this.mBaseRulesVersion.equals(that.mBaseRulesVersion) || !this.mDistroFormatVersionSupported.equals(that.mDistroFormatVersionSupported)) {
            return false;
        }
        DistroRulesVersion distroRulesVersion = this.mStagedDistroRulesVersion;
        if (!distroRulesVersion == null ? distroRulesVersion.equals(that.mStagedDistroRulesVersion) : that.mStagedDistroRulesVersion == null) {
            return false;
        }
        distroRulesVersion = this.mInstalledDistroRulesVersion;
        if (distroRulesVersion != null) {
            z = distroRulesVersion.equals(that.mInstalledDistroRulesVersion);
        } else if (that.mInstalledDistroRulesVersion != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int hashCode;
        int hashCode2 = ((((((this.mBaseRulesVersion.hashCode() * 31) + this.mDistroFormatVersionSupported.hashCode()) * 31) + this.mOperationInProgress) * 31) + this.mStagedOperationType) * 31;
        DistroRulesVersion distroRulesVersion = this.mStagedDistroRulesVersion;
        int i = 0;
        if (distroRulesVersion != null) {
            hashCode = distroRulesVersion.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode2 = (((hashCode2 + hashCode) * 31) + this.mDistroStatus) * 31;
        distroRulesVersion = this.mInstalledDistroRulesVersion;
        if (distroRulesVersion != null) {
            i = distroRulesVersion.hashCode();
        }
        return hashCode2 + i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RulesState{mBaseRulesVersion='");
        stringBuilder.append(this.mBaseRulesVersion);
        stringBuilder.append(DateFormat.QUOTE);
        stringBuilder.append(", mDistroFormatVersionSupported=");
        stringBuilder.append(this.mDistroFormatVersionSupported);
        stringBuilder.append(", mOperationInProgress=");
        stringBuilder.append(this.mOperationInProgress);
        stringBuilder.append(", mStagedOperationType=");
        stringBuilder.append(this.mStagedOperationType);
        stringBuilder.append(", mStagedDistroRulesVersion=");
        stringBuilder.append(this.mStagedDistroRulesVersion);
        stringBuilder.append(", mDistroStatus=");
        stringBuilder.append(this.mDistroStatus);
        stringBuilder.append(", mInstalledDistroRulesVersion=");
        stringBuilder.append(this.mInstalledDistroRulesVersion);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    private static int validateStagedOperation(int stagedOperationType) {
        if (stagedOperationType >= 0 && stagedOperationType <= 3) {
            return stagedOperationType;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown operation type=");
        stringBuilder.append(stagedOperationType);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private static int validateDistroStatus(int distroStatus) {
        if (distroStatus >= 0 && distroStatus <= 2) {
            return distroStatus;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown distro status=");
        stringBuilder.append(distroStatus);
        throw new IllegalArgumentException(stringBuilder.toString());
    }
}
