package android.net;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Pair;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;

public final class IpPrefix implements Parcelable {
    public static final Creator<IpPrefix> CREATOR = new Creator<IpPrefix>() {
        public IpPrefix createFromParcel(Parcel in) {
            return new IpPrefix(in.createByteArray(), in.readInt());
        }

        public IpPrefix[] newArray(int size) {
            return new IpPrefix[size];
        }
    };
    private final byte[] address;
    private final int prefixLength;

    private void checkAndMaskAddressAndPrefixLength() {
        byte[] bArr = this.address;
        if (bArr.length == 4 || bArr.length == 16) {
            NetworkUtils.maskRawAddress(this.address, this.prefixLength);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IpPrefix has ");
        stringBuilder.append(this.address.length);
        stringBuilder.append(" bytes which is neither 4 nor 16");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public IpPrefix(byte[] address, int prefixLength) {
        this.address = (byte[]) address.clone();
        this.prefixLength = prefixLength;
        checkAndMaskAddressAndPrefixLength();
    }

    @SystemApi
    public IpPrefix(InetAddress address, int prefixLength) {
        this.address = address.getAddress();
        this.prefixLength = prefixLength;
        checkAndMaskAddressAndPrefixLength();
    }

    @SystemApi
    public IpPrefix(String prefix) {
        Pair<InetAddress, Integer> ipAndMask = NetworkUtils.parseIpAndMask(prefix);
        this.address = ((InetAddress) ipAndMask.first).getAddress();
        this.prefixLength = ((Integer) ipAndMask.second).intValue();
        checkAndMaskAddressAndPrefixLength();
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof IpPrefix)) {
            return false;
        }
        IpPrefix that = (IpPrefix) obj;
        if (Arrays.equals(this.address, that.address) && this.prefixLength == that.prefixLength) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return Arrays.hashCode(this.address) + (this.prefixLength * 11);
    }

    public InetAddress getAddress() {
        try {
            return InetAddress.getByAddress(this.address);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Address is invalid");
        }
    }

    public byte[] getRawAddress() {
        return (byte[]) this.address.clone();
    }

    public int getPrefixLength() {
        return this.prefixLength;
    }

    public boolean contains(InetAddress address) {
        byte[] addrBytes = address.getAddress();
        if (addrBytes == null || addrBytes.length != this.address.length) {
            return false;
        }
        NetworkUtils.maskRawAddress(addrBytes, this.prefixLength);
        return Arrays.equals(this.address, addrBytes);
    }

    public boolean containsPrefix(IpPrefix otherPrefix) {
        if (otherPrefix.getPrefixLength() < this.prefixLength) {
            return false;
        }
        byte[] otherAddress = otherPrefix.getRawAddress();
        NetworkUtils.maskRawAddress(otherAddress, this.prefixLength);
        return Arrays.equals(otherAddress, this.address);
    }

    public boolean isIPv6() {
        return getAddress() instanceof Inet6Address;
    }

    public boolean isIPv4() {
        return getAddress() instanceof Inet4Address;
    }

    public String toString() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(InetAddress.getByAddress(this.address).getHostAddress());
            stringBuilder.append("/");
            stringBuilder.append(this.prefixLength);
            return stringBuilder.toString();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("IpPrefix with invalid address! Shouldn't happen.", e);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.address);
        dest.writeInt(this.prefixLength);
    }

    public static Comparator<IpPrefix> lengthComparator() {
        return new Comparator<IpPrefix>() {
            public int compare(IpPrefix prefix1, IpPrefix prefix2) {
                if (prefix1.isIPv4()) {
                    if (prefix2.isIPv6()) {
                        return -1;
                    }
                } else if (prefix2.isIPv4()) {
                    return 1;
                }
                int p1len = prefix1.getPrefixLength();
                int p2len = prefix2.getPrefixLength();
                if (p1len < p2len) {
                    return -1;
                }
                if (p2len < p1len) {
                    return 1;
                }
                byte[] a1 = prefix1.address;
                byte[] a2 = prefix2.address;
                int len = a1.length < a2.length ? a1.length : a2.length;
                for (int i = 0; i < len; i++) {
                    if (a1[i] < a2[i]) {
                        return -1;
                    }
                    if (a1[i] > a2[i]) {
                        return 1;
                    }
                }
                if (a2.length < len) {
                    return 1;
                }
                if (a1.length < len) {
                    return -1;
                }
                return 0;
            }
        };
    }
}
