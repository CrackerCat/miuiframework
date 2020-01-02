package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.InvalidParameterException;

public final class GnssNavigationMessage implements Parcelable {
    public static final Creator<GnssNavigationMessage> CREATOR = new Creator<GnssNavigationMessage>() {
        public GnssNavigationMessage createFromParcel(Parcel parcel) {
            GnssNavigationMessage navigationMessage = new GnssNavigationMessage();
            navigationMessage.setType(parcel.readInt());
            navigationMessage.setSvid(parcel.readInt());
            navigationMessage.setMessageId(parcel.readInt());
            navigationMessage.setSubmessageId(parcel.readInt());
            byte[] data = new byte[parcel.readInt()];
            parcel.readByteArray(data);
            navigationMessage.setData(data);
            navigationMessage.setStatus(parcel.readInt());
            return navigationMessage;
        }

        public GnssNavigationMessage[] newArray(int size) {
            return new GnssNavigationMessage[size];
        }
    };
    private static final byte[] EMPTY_ARRAY = new byte[0];
    public static final int STATUS_PARITY_PASSED = 1;
    public static final int STATUS_PARITY_REBUILT = 2;
    public static final int STATUS_UNKNOWN = 0;
    public static final int TYPE_BDS_D1 = 1281;
    public static final int TYPE_BDS_D2 = 1282;
    public static final int TYPE_GAL_F = 1538;
    public static final int TYPE_GAL_I = 1537;
    public static final int TYPE_GLO_L1CA = 769;
    public static final int TYPE_GPS_CNAV2 = 260;
    public static final int TYPE_GPS_L1CA = 257;
    public static final int TYPE_GPS_L2CNAV = 258;
    public static final int TYPE_GPS_L5CNAV = 259;
    public static final int TYPE_UNKNOWN = 0;
    private byte[] mData;
    private int mMessageId;
    private int mStatus;
    private int mSubmessageId;
    private int mSvid;
    private int mType;

    public static abstract class Callback {
        public static final int STATUS_LOCATION_DISABLED = 2;
        public static final int STATUS_NOT_SUPPORTED = 0;
        public static final int STATUS_READY = 1;

        @Retention(RetentionPolicy.SOURCE)
        public @interface GnssNavigationMessageStatus {
        }

        public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
        }

        public void onStatusChanged(int status) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface GnssNavigationMessageType {
    }

    public GnssNavigationMessage() {
        initialize();
    }

    public void set(GnssNavigationMessage navigationMessage) {
        this.mType = navigationMessage.mType;
        this.mSvid = navigationMessage.mSvid;
        this.mMessageId = navigationMessage.mMessageId;
        this.mSubmessageId = navigationMessage.mSubmessageId;
        this.mData = navigationMessage.mData;
        this.mStatus = navigationMessage.mStatus;
    }

    public void reset() {
        initialize();
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int value) {
        this.mType = value;
    }

    private String getTypeString() {
        int i = this.mType;
        if (i == 0) {
            return "Unknown";
        }
        if (i == 769) {
            return "Glonass L1 C/A";
        }
        if (i == 1281) {
            return "Beidou D1";
        }
        if (i == 1282) {
            return "Beidou D2";
        }
        if (i == 1537) {
            return "Galileo I";
        }
        if (i == 1538) {
            return "Galileo F";
        }
        switch (i) {
            case 257:
                return "GPS L1 C/A";
            case 258:
                return "GPS L2-CNAV";
            case 259:
                return "GPS L5-CNAV";
            case 260:
                return "GPS CNAV2";
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<Invalid:");
                stringBuilder.append(this.mType);
                stringBuilder.append(">");
                return stringBuilder.toString();
        }
    }

    public int getSvid() {
        return this.mSvid;
    }

    public void setSvid(int value) {
        this.mSvid = value;
    }

    public int getMessageId() {
        return this.mMessageId;
    }

    public void setMessageId(int value) {
        this.mMessageId = value;
    }

    public int getSubmessageId() {
        return this.mSubmessageId;
    }

    public void setSubmessageId(int value) {
        this.mSubmessageId = value;
    }

    public byte[] getData() {
        return this.mData;
    }

    public void setData(byte[] value) {
        if (value != null) {
            this.mData = value;
            return;
        }
        throw new InvalidParameterException("Data must be a non-null array");
    }

    public int getStatus() {
        return this.mStatus;
    }

    public void setStatus(int value) {
        this.mStatus = value;
    }

    private String getStatusString() {
        int i = this.mStatus;
        if (i == 0) {
            return "Unknown";
        }
        if (i == 1) {
            return "ParityPassed";
        }
        if (i == 2) {
            return "ParityRebuilt";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<Invalid:");
        stringBuilder.append(this.mStatus);
        stringBuilder.append(">");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mSvid);
        parcel.writeInt(this.mMessageId);
        parcel.writeInt(this.mSubmessageId);
        parcel.writeInt(this.mData.length);
        parcel.writeByteArray(this.mData);
        parcel.writeInt(this.mStatus);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        String format = "   %-15s = %s\n";
        StringBuilder builder = new StringBuilder("GnssNavigationMessage:\n");
        r3 = new Object[2];
        int i = 0;
        r3[0] = "Type";
        r3[1] = getTypeString();
        String str = "   %-15s = %s\n";
        builder.append(String.format(str, r3));
        builder.append(String.format(str, new Object[]{"Svid", Integer.valueOf(this.mSvid)}));
        builder.append(String.format(str, new Object[]{"Status", getStatusString()}));
        builder.append(String.format(str, new Object[]{"MessageId", Integer.valueOf(this.mMessageId)}));
        builder.append(String.format(str, new Object[]{"SubmessageId", Integer.valueOf(this.mSubmessageId)}));
        builder.append(String.format(str, new Object[]{"Data", "{"}));
        String prefix = "        ";
        byte[] bArr = this.mData;
        int length = bArr.length;
        while (i < length) {
            byte value = bArr[i];
            builder.append(prefix);
            builder.append(value);
            prefix = ", ";
            i++;
        }
        builder.append(" }");
        return builder.toString();
    }

    private void initialize() {
        this.mType = 0;
        this.mSvid = 0;
        this.mMessageId = -1;
        this.mSubmessageId = -1;
        this.mData = EMPTY_ARRAY;
        this.mStatus = 0;
    }
}
