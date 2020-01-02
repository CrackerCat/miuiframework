package android.content.pm;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PermissionInfo extends PackageItemInfo implements Parcelable {
    public static final Creator<PermissionInfo> CREATOR = new Creator<PermissionInfo>() {
        public PermissionInfo createFromParcel(Parcel source) {
            return new PermissionInfo(source, null);
        }

        public PermissionInfo[] newArray(int size) {
            return new PermissionInfo[size];
        }
    };
    public static final int FLAG_COSTS_MONEY = 1;
    public static final int FLAG_HARD_RESTRICTED = 4;
    public static final int FLAG_IMMUTABLY_RESTRICTED = 16;
    public static final int FLAG_INSTALLED = 1073741824;
    @SystemApi
    public static final int FLAG_REMOVED = 2;
    public static final int FLAG_SOFT_RESTRICTED = 8;
    public static final int PROTECTION_DANGEROUS = 1;
    public static final int PROTECTION_FLAG_APPOP = 64;
    @SystemApi
    public static final int PROTECTION_FLAG_APP_PREDICTOR = 2097152;
    @SystemApi
    public static final int PROTECTION_FLAG_CONFIGURATOR = 524288;
    public static final int PROTECTION_FLAG_DEVELOPMENT = 32;
    @SystemApi
    public static final int PROTECTION_FLAG_DOCUMENTER = 262144;
    @SystemApi
    public static final int PROTECTION_FLAG_INCIDENT_REPORT_APPROVER = 1048576;
    public static final int PROTECTION_FLAG_INSTALLER = 256;
    public static final int PROTECTION_FLAG_INSTANT = 4096;
    @SystemApi
    public static final int PROTECTION_FLAG_OEM = 16384;
    public static final int PROTECTION_FLAG_PRE23 = 128;
    public static final int PROTECTION_FLAG_PREINSTALLED = 1024;
    public static final int PROTECTION_FLAG_PRIVILEGED = 16;
    public static final int PROTECTION_FLAG_RUNTIME_ONLY = 8192;
    public static final int PROTECTION_FLAG_SETUP = 2048;
    @Deprecated
    public static final int PROTECTION_FLAG_SYSTEM = 16;
    @SystemApi
    public static final int PROTECTION_FLAG_SYSTEM_TEXT_CLASSIFIER = 65536;
    public static final int PROTECTION_FLAG_VENDOR_PRIVILEGED = 32768;
    public static final int PROTECTION_FLAG_VERIFIER = 512;
    @SystemApi
    public static final int PROTECTION_FLAG_WELLBEING = 131072;
    @Deprecated
    public static final int PROTECTION_MASK_BASE = 15;
    @Deprecated
    public static final int PROTECTION_MASK_FLAGS = 65520;
    public static final int PROTECTION_NORMAL = 0;
    public static final int PROTECTION_SIGNATURE = 2;
    @Deprecated
    public static final int PROTECTION_SIGNATURE_OR_SYSTEM = 3;
    @SystemApi
    public final String backgroundPermission;
    public int descriptionRes;
    public int flags;
    public String group;
    public CharSequence nonLocalizedDescription;
    @Deprecated
    public int protectionLevel;
    @SystemApi
    public int requestRes;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Protection {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ProtectionFlags {
    }

    /* synthetic */ PermissionInfo(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public static int fixProtectionLevel(int level) {
        if (level == 3) {
            level = 18;
        }
        if ((32768 & level) == 0 || (level & 16) != 0) {
            return level;
        }
        return level & -32769;
    }

    @UnsupportedAppUsage
    public static String protectionToString(int level) {
        StringBuilder stringBuilder;
        String protLevel = "????";
        int i = level & 15;
        if (i == 0) {
            protLevel = "normal";
        } else if (i == 1) {
            protLevel = "dangerous";
        } else if (i == 2) {
            protLevel = "signature";
        } else if (i == 3) {
            protLevel = "signatureOrSystem";
        }
        if ((level & 16) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|privileged");
            protLevel = stringBuilder.toString();
        }
        if ((level & 32) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|development");
            protLevel = stringBuilder.toString();
        }
        if ((level & 64) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|appop");
            protLevel = stringBuilder.toString();
        }
        if ((level & 128) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|pre23");
            protLevel = stringBuilder.toString();
        }
        if ((level & 256) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|installer");
            protLevel = stringBuilder.toString();
        }
        if ((level & 512) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|verifier");
            protLevel = stringBuilder.toString();
        }
        if ((level & 1024) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|preinstalled");
            protLevel = stringBuilder.toString();
        }
        if ((level & 2048) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|setup");
            protLevel = stringBuilder.toString();
        }
        if ((level & 4096) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|instant");
            protLevel = stringBuilder.toString();
        }
        if ((level & 8192) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|runtime");
            protLevel = stringBuilder.toString();
        }
        if ((level & 16384) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|oem");
            protLevel = stringBuilder.toString();
        }
        if ((32768 & level) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|vendorPrivileged");
            protLevel = stringBuilder.toString();
        }
        if ((65536 & level) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|textClassifier");
            protLevel = stringBuilder.toString();
        }
        if ((131072 & level) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|wellbeing");
            protLevel = stringBuilder.toString();
        }
        if ((262144 & level) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|documenter");
            protLevel = stringBuilder.toString();
        }
        if ((524288 & level) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|configurator");
            protLevel = stringBuilder.toString();
        }
        if ((1048576 & level) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(protLevel);
            stringBuilder.append("|incidentReportApprover");
            protLevel = stringBuilder.toString();
        }
        if ((2097152 & level) == 0) {
            return protLevel;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(protLevel);
        stringBuilder.append("|appPredictor");
        return stringBuilder.toString();
    }

    public PermissionInfo(String backgroundPermission) {
        this.backgroundPermission = backgroundPermission;
    }

    @Deprecated
    public PermissionInfo() {
        this((String) null);
    }

    @Deprecated
    public PermissionInfo(PermissionInfo orig) {
        super((PackageItemInfo) orig);
        this.protectionLevel = orig.protectionLevel;
        this.flags = orig.flags;
        this.group = orig.group;
        this.backgroundPermission = orig.backgroundPermission;
        this.descriptionRes = orig.descriptionRes;
        this.requestRes = orig.requestRes;
        this.nonLocalizedDescription = orig.nonLocalizedDescription;
    }

    public CharSequence loadDescription(PackageManager pm) {
        CharSequence charSequence = this.nonLocalizedDescription;
        if (charSequence != null) {
            return charSequence;
        }
        if (this.descriptionRes != 0) {
            charSequence = pm.getText(this.packageName, this.descriptionRes, null);
            if (charSequence != null) {
                return charSequence;
            }
        }
        return null;
    }

    public int getProtection() {
        return this.protectionLevel & 15;
    }

    public int getProtectionFlags() {
        return this.protectionLevel & -16;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PermissionInfo{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        stringBuilder.append(this.name);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeInt(this.protectionLevel);
        dest.writeInt(this.flags);
        dest.writeString(this.group);
        dest.writeString(this.backgroundPermission);
        dest.writeInt(this.descriptionRes);
        dest.writeInt(this.requestRes);
        TextUtils.writeToParcel(this.nonLocalizedDescription, dest, parcelableFlags);
    }

    public int calculateFootprint() {
        int size = this.name.length();
        if (this.nonLocalizedLabel != null) {
            size += this.nonLocalizedLabel.length();
        }
        CharSequence charSequence = this.nonLocalizedDescription;
        if (charSequence != null) {
            return size + charSequence.length();
        }
        return size;
    }

    public boolean isHardRestricted() {
        return (this.flags & 4) != 0;
    }

    public boolean isSoftRestricted() {
        return (this.flags & 8) != 0;
    }

    public boolean isRestricted() {
        return isHardRestricted() || isSoftRestricted();
    }

    public boolean isAppOp() {
        return (this.protectionLevel & 64) != 0;
    }

    public boolean isRuntime() {
        return getProtection() == 1;
    }

    private PermissionInfo(Parcel source) {
        super(source);
        this.protectionLevel = source.readInt();
        this.flags = source.readInt();
        this.group = source.readString();
        this.backgroundPermission = source.readString();
        this.descriptionRes = source.readInt();
        this.requestRes = source.readInt();
        this.nonLocalizedDescription = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
    }
}
