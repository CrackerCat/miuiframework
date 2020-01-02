package com.android.ims.internal.uce.options;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.ims.internal.uce.common.CapInfo;

public class OptionsCapInfo implements Parcelable {
    public static final Creator<OptionsCapInfo> CREATOR = new Creator<OptionsCapInfo>() {
        public OptionsCapInfo createFromParcel(Parcel source) {
            return new OptionsCapInfo(source, null);
        }

        public OptionsCapInfo[] newArray(int size) {
            return new OptionsCapInfo[size];
        }
    };
    private CapInfo mCapInfo;
    private String mSdp;

    /* synthetic */ OptionsCapInfo(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public static OptionsCapInfo getOptionsCapInfoInstance() {
        return new OptionsCapInfo();
    }

    @UnsupportedAppUsage
    public String getSdp() {
        return this.mSdp;
    }

    @UnsupportedAppUsage
    public void setSdp(String sdp) {
        this.mSdp = sdp;
    }

    @UnsupportedAppUsage
    public OptionsCapInfo() {
        this.mSdp = "";
        this.mCapInfo = new CapInfo();
    }

    @UnsupportedAppUsage
    public CapInfo getCapInfo() {
        return this.mCapInfo;
    }

    @UnsupportedAppUsage
    public void setCapInfo(CapInfo capInfo) {
        this.mCapInfo = capInfo;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSdp);
        dest.writeParcelable(this.mCapInfo, flags);
    }

    private OptionsCapInfo(Parcel source) {
        this.mSdp = "";
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mSdp = source.readString();
        this.mCapInfo = (CapInfo) source.readParcelable(CapInfo.class.getClassLoader());
    }
}
