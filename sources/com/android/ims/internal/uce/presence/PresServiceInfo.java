package com.android.ims.internal.uce.presence;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PresServiceInfo implements Parcelable {
    public static final Creator<PresServiceInfo> CREATOR = new Creator<PresServiceInfo>() {
        public PresServiceInfo createFromParcel(Parcel source) {
            return new PresServiceInfo(source, null);
        }

        public PresServiceInfo[] newArray(int size) {
            return new PresServiceInfo[size];
        }
    };
    public static final int UCE_PRES_MEDIA_CAP_FULL_AUDIO_AND_VIDEO = 2;
    public static final int UCE_PRES_MEDIA_CAP_FULL_AUDIO_ONLY = 1;
    public static final int UCE_PRES_MEDIA_CAP_NONE = 0;
    public static final int UCE_PRES_MEDIA_CAP_UNKNOWN = 3;
    private int mMediaCap;
    private String mServiceDesc;
    private String mServiceID;
    private String mServiceVer;

    /* synthetic */ PresServiceInfo(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    @UnsupportedAppUsage
    public int getMediaType() {
        return this.mMediaCap;
    }

    public void setMediaType(int nMediaCap) {
        this.mMediaCap = nMediaCap;
    }

    @UnsupportedAppUsage
    public String getServiceId() {
        return this.mServiceID;
    }

    public void setServiceId(String serviceID) {
        this.mServiceID = serviceID;
    }

    @UnsupportedAppUsage
    public String getServiceDesc() {
        return this.mServiceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.mServiceDesc = serviceDesc;
    }

    @UnsupportedAppUsage
    public String getServiceVer() {
        return this.mServiceVer;
    }

    public void setServiceVer(String serviceVer) {
        this.mServiceVer = serviceVer;
    }

    public PresServiceInfo() {
        this.mMediaCap = 0;
        String str = "";
        this.mServiceID = str;
        this.mServiceDesc = str;
        this.mServiceVer = str;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mServiceID);
        dest.writeString(this.mServiceDesc);
        dest.writeString(this.mServiceVer);
        dest.writeInt(this.mMediaCap);
    }

    private PresServiceInfo(Parcel source) {
        this.mMediaCap = 0;
        String str = "";
        this.mServiceID = str;
        this.mServiceDesc = str;
        this.mServiceVer = str;
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mServiceID = source.readString();
        this.mServiceDesc = source.readString();
        this.mServiceVer = source.readString();
        this.mMediaCap = source.readInt();
    }
}
