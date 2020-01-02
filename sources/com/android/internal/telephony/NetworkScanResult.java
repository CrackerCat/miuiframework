package com.android.internal.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.telephony.CellInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NetworkScanResult implements Parcelable {
    public static final Creator<NetworkScanResult> CREATOR = new Creator<NetworkScanResult>() {
        public NetworkScanResult createFromParcel(Parcel in) {
            return new NetworkScanResult(in, null);
        }

        public NetworkScanResult[] newArray(int size) {
            return new NetworkScanResult[size];
        }
    };
    public static final int SCAN_STATUS_COMPLETE = 2;
    public static final int SCAN_STATUS_PARTIAL = 1;
    public List<CellInfo> networkInfos;
    public int scanError;
    public int scanStatus;

    /* synthetic */ NetworkScanResult(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public NetworkScanResult(int scanStatus, int scanError, List<CellInfo> networkInfos) {
        this.scanStatus = scanStatus;
        this.scanError = scanError;
        this.networkInfos = networkInfos;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.scanStatus);
        dest.writeInt(this.scanError);
        dest.writeParcelableList(this.networkInfos, flags);
    }

    private NetworkScanResult(Parcel in) {
        this.scanStatus = in.readInt();
        this.scanError = in.readInt();
        List<CellInfo> ni = new ArrayList();
        in.readParcelableList(ni, Object.class.getClassLoader());
        this.networkInfos = ni;
    }

    public boolean equals(Object o) {
        boolean z = false;
        try {
            NetworkScanResult nsr = (NetworkScanResult) o;
            if (o == null) {
                return false;
            }
            if (this.scanStatus == nsr.scanStatus && this.scanError == nsr.scanError && this.networkInfos.equals(nsr.networkInfos)) {
                z = true;
            }
            return z;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("scanStatus=");
        stringBuilder2.append(this.scanStatus);
        stringBuilder.append(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(", scanError=");
        stringBuilder2.append(this.scanError);
        stringBuilder.append(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(", networkInfos=");
        stringBuilder2.append(this.networkInfos);
        stringBuilder.append(stringBuilder2.toString());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public int hashCode() {
        return ((this.scanStatus * 31) + (this.scanError * 23)) + (Objects.hashCode(this.networkInfos) * 37);
    }
}
