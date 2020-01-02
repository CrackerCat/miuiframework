package android.net;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;

public class InterfaceConfiguration implements Parcelable {
    public static final Creator<InterfaceConfiguration> CREATOR = new Creator<InterfaceConfiguration>() {
        public InterfaceConfiguration createFromParcel(Parcel in) {
            InterfaceConfiguration info = new InterfaceConfiguration();
            info.mHwAddr = in.readString();
            if (in.readByte() == (byte) 1) {
                info.mAddr = (LinkAddress) in.readParcelable(null);
            }
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                info.mFlags.add(in.readString());
            }
            return info;
        }

        public InterfaceConfiguration[] newArray(int size) {
            return new InterfaceConfiguration[size];
        }
    };
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String FLAG_DOWN = "down";
    private static final String FLAG_UP = "up";
    private LinkAddress mAddr;
    private HashSet<String> mFlags = Sets.newHashSet();
    private String mHwAddr;

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("mHwAddr=");
        builder.append(this.mHwAddr);
        builder.append(" mAddr=");
        builder.append(String.valueOf(this.mAddr));
        builder.append(" mFlags=");
        builder.append(getFlags());
        return builder.toString();
    }

    @UnsupportedAppUsage
    public Iterable<String> getFlags() {
        return this.mFlags;
    }

    public boolean hasFlag(String flag) {
        validateFlag(flag);
        return this.mFlags.contains(flag);
    }

    @UnsupportedAppUsage
    public void clearFlag(String flag) {
        validateFlag(flag);
        this.mFlags.remove(flag);
    }

    @UnsupportedAppUsage
    public void setFlag(String flag) {
        validateFlag(flag);
        this.mFlags.add(flag);
    }

    @UnsupportedAppUsage
    public void setInterfaceUp() {
        this.mFlags.remove(FLAG_DOWN);
        this.mFlags.add(FLAG_UP);
    }

    @UnsupportedAppUsage
    public void setInterfaceDown() {
        this.mFlags.remove(FLAG_UP);
        this.mFlags.add(FLAG_DOWN);
    }

    public void ignoreInterfaceUpDownStatus() {
        this.mFlags.remove(FLAG_UP);
        this.mFlags.remove(FLAG_DOWN);
    }

    public LinkAddress getLinkAddress() {
        return this.mAddr;
    }

    @UnsupportedAppUsage
    public void setLinkAddress(LinkAddress addr) {
        this.mAddr = addr;
    }

    public String getHardwareAddress() {
        return this.mHwAddr;
    }

    public void setHardwareAddress(String hwAddr) {
        this.mHwAddr = hwAddr;
    }

    public boolean isActive() {
        try {
            if (isUp()) {
                for (byte b : this.mAddr.getAddress().getAddress()) {
                    if (b != (byte) 0) {
                        return true;
                    }
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean isUp() {
        return hasFlag(FLAG_UP);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mHwAddr);
        if (this.mAddr != null) {
            dest.writeByte((byte) 1);
            dest.writeParcelable(this.mAddr, flags);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeInt(this.mFlags.size());
        Iterator it = this.mFlags.iterator();
        while (it.hasNext()) {
            dest.writeString((String) it.next());
        }
    }

    private static void validateFlag(String flag) {
        if (flag.indexOf(32) >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("flag contains space: ");
            stringBuilder.append(flag);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }
}
