package android.content.pm;

import android.annotation.SystemApi;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PermissionGroupInfo extends PackageItemInfo implements Parcelable {
    public static final Creator<PermissionGroupInfo> CREATOR = new Creator<PermissionGroupInfo>() {
        public PermissionGroupInfo createFromParcel(Parcel source) {
            return new PermissionGroupInfo(source, null);
        }

        public PermissionGroupInfo[] newArray(int size) {
            return new PermissionGroupInfo[size];
        }
    };
    public static final int FLAG_PERSONAL_INFO = 1;
    @SystemApi
    public final int backgroundRequestDetailResourceId;
    @SystemApi
    public final int backgroundRequestResourceId;
    public int descriptionRes;
    public int flags;
    public CharSequence nonLocalizedDescription;
    public int priority;
    @SystemApi
    public final int requestDetailResourceId;
    @SystemApi
    public int requestRes;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    /* synthetic */ PermissionGroupInfo(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public PermissionGroupInfo(int requestDetailResourceId, int backgroundRequestResourceId, int backgroundRequestDetailResourceId) {
        this.requestDetailResourceId = requestDetailResourceId;
        this.backgroundRequestResourceId = backgroundRequestResourceId;
        this.backgroundRequestDetailResourceId = backgroundRequestDetailResourceId;
    }

    @Deprecated
    public PermissionGroupInfo() {
        this(0, 0, 0);
    }

    @Deprecated
    public PermissionGroupInfo(PermissionGroupInfo orig) {
        super((PackageItemInfo) orig);
        this.descriptionRes = orig.descriptionRes;
        this.requestRes = orig.requestRes;
        this.requestDetailResourceId = orig.requestDetailResourceId;
        this.backgroundRequestResourceId = orig.backgroundRequestResourceId;
        this.backgroundRequestDetailResourceId = orig.backgroundRequestDetailResourceId;
        this.nonLocalizedDescription = orig.nonLocalizedDescription;
        this.flags = orig.flags;
        this.priority = orig.priority;
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

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PermissionGroupInfo{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        stringBuilder.append(this.name);
        stringBuilder.append(" flgs=0x");
        stringBuilder.append(Integer.toHexString(this.flags));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeInt(this.descriptionRes);
        dest.writeInt(this.requestRes);
        dest.writeInt(this.requestDetailResourceId);
        dest.writeInt(this.backgroundRequestResourceId);
        dest.writeInt(this.backgroundRequestDetailResourceId);
        TextUtils.writeToParcel(this.nonLocalizedDescription, dest, parcelableFlags);
        dest.writeInt(this.flags);
        dest.writeInt(this.priority);
    }

    private PermissionGroupInfo(Parcel source) {
        super(source);
        this.descriptionRes = source.readInt();
        this.requestRes = source.readInt();
        this.requestDetailResourceId = source.readInt();
        this.backgroundRequestResourceId = source.readInt();
        this.backgroundRequestDetailResourceId = source.readInt();
        this.nonLocalizedDescription = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.flags = source.readInt();
        this.priority = source.readInt();
    }
}
