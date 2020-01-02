package android.net.metrics;

import android.annotation.SystemApi;
import android.net.metrics.IpConnectivityLog.Event;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;

@SystemApi
public final class IpReachabilityEvent implements Event {
    public static final Creator<IpReachabilityEvent> CREATOR = new Creator<IpReachabilityEvent>() {
        public IpReachabilityEvent createFromParcel(Parcel in) {
            return new IpReachabilityEvent(in, null);
        }

        public IpReachabilityEvent[] newArray(int size) {
            return new IpReachabilityEvent[size];
        }
    };
    public static final int NUD_FAILED = 512;
    public static final int NUD_FAILED_ORGANIC = 1024;
    public static final int PROBE = 256;
    public static final int PROVISIONING_LOST = 768;
    public static final int PROVISIONING_LOST_ORGANIC = 1280;
    public final int eventType;

    static final class Decoder {
        static final SparseArray<String> constants = MessageUtils.findMessageNames(new Class[]{IpReachabilityEvent.class}, new String[]{"PROBE", "PROVISIONING_", "NUD_"});

        Decoder() {
        }
    }

    /* synthetic */ IpReachabilityEvent(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public IpReachabilityEvent(int eventType) {
        this.eventType = eventType;
    }

    private IpReachabilityEvent(Parcel in) {
        this.eventType = in.readInt();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.eventType);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        int lo = this.eventType;
        int hi = 65280 & lo;
        lo &= 255;
        return String.format("IpReachabilityEvent(%s:%02x)", new Object[]{(String) Decoder.constants.get(hi), Integer.valueOf(lo)});
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null || !obj.getClass().equals(IpReachabilityEvent.class)) {
            return false;
        }
        if (this.eventType == ((IpReachabilityEvent) obj).eventType) {
            z = true;
        }
        return z;
    }
}
