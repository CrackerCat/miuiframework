package android.net;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.Objects;

@SystemApi
public class RssiCurve implements Parcelable {
    public static final Creator<RssiCurve> CREATOR = new Creator<RssiCurve>() {
        public RssiCurve createFromParcel(Parcel in) {
            return new RssiCurve(in, null);
        }

        public RssiCurve[] newArray(int size) {
            return new RssiCurve[size];
        }
    };
    private static final int DEFAULT_ACTIVE_NETWORK_RSSI_BOOST = 25;
    public final int activeNetworkRssiBoost;
    public final int bucketWidth;
    public final byte[] rssiBuckets;
    public final int start;

    /* synthetic */ RssiCurve(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public RssiCurve(int start, int bucketWidth, byte[] rssiBuckets) {
        this(start, bucketWidth, rssiBuckets, 25);
    }

    public RssiCurve(int start, int bucketWidth, byte[] rssiBuckets, int activeNetworkRssiBoost) {
        this.start = start;
        this.bucketWidth = bucketWidth;
        if (rssiBuckets == null || rssiBuckets.length == 0) {
            throw new IllegalArgumentException("rssiBuckets must be at least one element large.");
        }
        this.rssiBuckets = rssiBuckets;
        this.activeNetworkRssiBoost = activeNetworkRssiBoost;
    }

    private RssiCurve(Parcel in) {
        this.start = in.readInt();
        this.bucketWidth = in.readInt();
        this.rssiBuckets = new byte[in.readInt()];
        in.readByteArray(this.rssiBuckets);
        this.activeNetworkRssiBoost = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.start);
        out.writeInt(this.bucketWidth);
        out.writeInt(this.rssiBuckets.length);
        out.writeByteArray(this.rssiBuckets);
        out.writeInt(this.activeNetworkRssiBoost);
    }

    public byte lookupScore(int rssi) {
        return lookupScore(rssi, false);
    }

    public byte lookupScore(int rssi, boolean isActiveNetwork) {
        if (isActiveNetwork) {
            rssi += this.activeNetworkRssiBoost;
        }
        int index = (rssi - this.start) / this.bucketWidth;
        if (index < 0) {
            index = 0;
        } else {
            byte[] bArr = this.rssiBuckets;
            if (index > bArr.length - 1) {
                index = bArr.length - 1;
            }
        }
        return this.rssiBuckets[index];
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RssiCurve rssiCurve = (RssiCurve) o;
        if (!(this.start == rssiCurve.start && this.bucketWidth == rssiCurve.bucketWidth && Arrays.equals(this.rssiBuckets, rssiCurve.rssiBuckets) && this.activeNetworkRssiBoost == rssiCurve.activeNetworkRssiBoost)) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.start), Integer.valueOf(this.bucketWidth), Integer.valueOf(this.activeNetworkRssiBoost)}) ^ Arrays.hashCode(this.rssiBuckets);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RssiCurve[start=");
        sb.append(this.start);
        sb.append(",bucketWidth=");
        sb.append(this.bucketWidth);
        sb.append(",activeNetworkRssiBoost=");
        sb.append(this.activeNetworkRssiBoost);
        sb.append(",buckets=");
        int i = 0;
        while (true) {
            byte[] bArr = this.rssiBuckets;
            if (i < bArr.length) {
                sb.append(bArr[i]);
                if (i < this.rssiBuckets.length - 1) {
                    sb.append(",");
                }
                i++;
            } else {
                sb.append("]");
                return sb.toString();
            }
        }
    }
}
