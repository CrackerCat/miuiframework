package android.hardware.location;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import libcore.util.EmptyArray;

@SystemApi
@Deprecated
public class NanoAppInstanceInfo implements Parcelable {
    public static final Creator<NanoAppInstanceInfo> CREATOR = new Creator<NanoAppInstanceInfo>() {
        public NanoAppInstanceInfo createFromParcel(Parcel in) {
            return new NanoAppInstanceInfo(in, null);
        }

        public NanoAppInstanceInfo[] newArray(int size) {
            return new NanoAppInstanceInfo[size];
        }
    };
    private long mAppId;
    private int mAppVersion;
    private int mContexthubId;
    private int mHandle;
    private String mName;
    private int mNeededExecMemBytes;
    private int mNeededReadMemBytes;
    private int[] mNeededSensors;
    private int mNeededWriteMemBytes;
    private int[] mOutputEvents;
    private String mPublisher;

    /* synthetic */ NanoAppInstanceInfo(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public NanoAppInstanceInfo() {
        String str = "Unknown";
        this.mPublisher = str;
        this.mName = str;
        this.mNeededReadMemBytes = 0;
        this.mNeededWriteMemBytes = 0;
        this.mNeededExecMemBytes = 0;
        this.mNeededSensors = EmptyArray.INT;
        this.mOutputEvents = EmptyArray.INT;
    }

    public NanoAppInstanceInfo(int handle, long appId, int appVersion, int contextHubId) {
        String str = "Unknown";
        this.mPublisher = str;
        this.mName = str;
        this.mNeededReadMemBytes = 0;
        this.mNeededWriteMemBytes = 0;
        this.mNeededExecMemBytes = 0;
        this.mNeededSensors = EmptyArray.INT;
        this.mOutputEvents = EmptyArray.INT;
        this.mHandle = handle;
        this.mAppId = appId;
        this.mAppVersion = appVersion;
        this.mContexthubId = contextHubId;
    }

    public String getPublisher() {
        return this.mPublisher;
    }

    public String getName() {
        return this.mName;
    }

    public long getAppId() {
        return this.mAppId;
    }

    public int getAppVersion() {
        return this.mAppVersion;
    }

    public int getNeededReadMemBytes() {
        return this.mNeededReadMemBytes;
    }

    public int getNeededWriteMemBytes() {
        return this.mNeededWriteMemBytes;
    }

    public int getNeededExecMemBytes() {
        return this.mNeededExecMemBytes;
    }

    public int[] getNeededSensors() {
        return this.mNeededSensors;
    }

    public int[] getOutputEvents() {
        return this.mOutputEvents;
    }

    public int getContexthubId() {
        return this.mContexthubId;
    }

    public int getHandle() {
        return this.mHandle;
    }

    private NanoAppInstanceInfo(Parcel in) {
        String str = "Unknown";
        this.mPublisher = str;
        this.mName = str;
        this.mNeededReadMemBytes = 0;
        this.mNeededWriteMemBytes = 0;
        this.mNeededExecMemBytes = 0;
        this.mNeededSensors = EmptyArray.INT;
        this.mOutputEvents = EmptyArray.INT;
        this.mPublisher = in.readString();
        this.mName = in.readString();
        this.mHandle = in.readInt();
        this.mAppId = in.readLong();
        this.mAppVersion = in.readInt();
        this.mContexthubId = in.readInt();
        this.mNeededReadMemBytes = in.readInt();
        this.mNeededWriteMemBytes = in.readInt();
        this.mNeededExecMemBytes = in.readInt();
        this.mNeededSensors = new int[in.readInt()];
        in.readIntArray(this.mNeededSensors);
        this.mOutputEvents = new int[in.readInt()];
        in.readIntArray(this.mOutputEvents);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mPublisher);
        out.writeString(this.mName);
        out.writeInt(this.mHandle);
        out.writeLong(this.mAppId);
        out.writeInt(this.mAppVersion);
        out.writeInt(this.mContexthubId);
        out.writeInt(this.mNeededReadMemBytes);
        out.writeInt(this.mNeededWriteMemBytes);
        out.writeInt(this.mNeededExecMemBytes);
        out.writeInt(this.mNeededSensors.length);
        out.writeIntArray(this.mNeededSensors);
        out.writeInt(this.mOutputEvents.length);
        out.writeIntArray(this.mOutputEvents);
    }

    public String toString() {
        String retVal = new StringBuilder();
        retVal.append("handle : ");
        retVal.append(this.mHandle);
        retVal = retVal.toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(retVal);
        stringBuilder.append(", Id : 0x");
        stringBuilder.append(Long.toHexString(this.mAppId));
        retVal = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append(retVal);
        stringBuilder.append(", Version : 0x");
        stringBuilder.append(Integer.toHexString(this.mAppVersion));
        return stringBuilder.toString();
    }
}
