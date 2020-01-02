package android.net;

import android.content.Context;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telecom.Logging.Session;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import java.util.Objects;

public class NetworkIdentity implements Comparable<NetworkIdentity> {
    @Deprecated
    public static final boolean COMBINE_SUBTYPE_ENABLED = true;
    public static final int SUBTYPE_COMBINED = -1;
    private static final String TAG = "NetworkIdentity";
    final boolean mDefaultNetwork;
    final boolean mMetered;
    final String mNetworkId;
    final boolean mRoaming;
    final int mSubType = -1;
    final String mSubscriberId;
    final int mType;

    public NetworkIdentity(int type, int subType, String subscriberId, String networkId, boolean roaming, boolean metered, boolean defaultNetwork) {
        this.mType = type;
        this.mSubscriberId = subscriberId;
        this.mNetworkId = networkId;
        this.mRoaming = roaming;
        this.mMetered = metered;
        this.mDefaultNetwork = defaultNetwork;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.mType), Integer.valueOf(this.mSubType), this.mSubscriberId, this.mNetworkId, Boolean.valueOf(this.mRoaming), Boolean.valueOf(this.mMetered), Boolean.valueOf(this.mDefaultNetwork)});
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof NetworkIdentity)) {
            return false;
        }
        NetworkIdentity ident = (NetworkIdentity) obj;
        if (this.mType == ident.mType && this.mSubType == ident.mSubType && this.mRoaming == ident.mRoaming && Objects.equals(this.mSubscriberId, ident.mSubscriberId) && Objects.equals(this.mNetworkId, ident.mNetworkId) && this.mMetered == ident.mMetered && this.mDefaultNetwork == ident.mDefaultNetwork) {
            z = true;
        }
        return z;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        builder.append("type=");
        builder.append(ConnectivityManager.getNetworkTypeName(this.mType));
        builder.append(", subType=");
        builder.append("COMBINED");
        if (this.mSubscriberId != null) {
            builder.append(", subscriberId=");
            builder.append(scrubSubscriberId(this.mSubscriberId));
        }
        if (this.mNetworkId != null) {
            builder.append(", networkId=");
            builder.append(this.mNetworkId);
        }
        if (this.mRoaming) {
            builder.append(", ROAMING");
        }
        builder.append(", metered=");
        builder.append(this.mMetered);
        builder.append(", defaultNetwork=");
        builder.append(this.mDefaultNetwork);
        builder.append("}");
        return builder.toString();
    }

    public void writeToProto(ProtoOutputStream proto, long tag) {
        long start = proto.start(tag);
        proto.write(1120986464257L, this.mType);
        String str = this.mSubscriberId;
        if (str != null) {
            proto.write(1138166333442L, scrubSubscriberId(str));
        }
        proto.write(1138166333443L, this.mNetworkId);
        proto.write(1133871366148L, this.mRoaming);
        proto.write(1133871366149L, this.mMetered);
        proto.write(1133871366150L, this.mDefaultNetwork);
        proto.end(start);
    }

    public int getType() {
        return this.mType;
    }

    public int getSubType() {
        return this.mSubType;
    }

    public String getSubscriberId() {
        return this.mSubscriberId;
    }

    public String getNetworkId() {
        return this.mNetworkId;
    }

    public boolean getRoaming() {
        return this.mRoaming;
    }

    public boolean getMetered() {
        return this.mMetered;
    }

    public boolean getDefaultNetwork() {
        return this.mDefaultNetwork;
    }

    public static String scrubSubscriberId(String subscriberId) {
        if (Build.IS_ENG) {
            return subscriberId;
        }
        if (subscriberId == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(subscriberId.substring(0, Math.min(6, subscriberId.length())));
        stringBuilder.append(Session.TRUNCATE_STRING);
        return stringBuilder.toString();
    }

    public static String[] scrubSubscriberId(String[] subscriberId) {
        if (subscriberId == null) {
            return null;
        }
        String[] res = new String[subscriberId.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = scrubSubscriberId(subscriberId[i]);
        }
        return res;
    }

    public static NetworkIdentity buildNetworkIdentity(Context context, NetworkState state, boolean defaultNetwork) {
        String subscriberId;
        String networkId;
        int type = state.networkInfo.getType();
        int subType = state.networkInfo.getSubtype();
        boolean roaming = state.networkCapabilities.hasCapability(18) ^ 1;
        boolean metered = state.networkCapabilities.hasCapability(11) ^ 1;
        if (ConnectivityManager.isNetworkTypeMobile(type)) {
            if (!(state.subscriberId != null || state.networkInfo.getState() == State.DISCONNECTED || state.networkInfo.getState() == State.UNKNOWN)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Active mobile network without subscriber! ni = ");
                stringBuilder.append(state.networkInfo);
                Slog.w(TAG, stringBuilder.toString());
            }
            subscriberId = state.subscriberId;
            networkId = null;
        } else if (type != 1) {
            subscriberId = null;
            networkId = null;
        } else if (state.networkId != null) {
            subscriberId = null;
            networkId = state.networkId;
        } else {
            WifiInfo info = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
            subscriberId = null;
            networkId = info != null ? info.getSSID() : null;
        }
        return new NetworkIdentity(type, subType, subscriberId, networkId, roaming, metered, defaultNetwork);
    }

    public int compareTo(NetworkIdentity another) {
        String str;
        String str2;
        int res = Integer.compare(this.mType, another.mType);
        if (res == 0) {
            res = Integer.compare(this.mSubType, another.mSubType);
        }
        if (res == 0) {
            str = this.mSubscriberId;
            if (str != null) {
                str2 = another.mSubscriberId;
                if (str2 != null) {
                    res = str.compareTo(str2);
                }
            }
        }
        if (res == 0) {
            str = this.mNetworkId;
            if (str != null) {
                str2 = another.mNetworkId;
                if (str2 != null) {
                    res = str.compareTo(str2);
                }
            }
        }
        if (res == 0) {
            res = Boolean.compare(this.mRoaming, another.mRoaming);
        }
        if (res == 0) {
            res = Boolean.compare(this.mMetered, another.mMetered);
        }
        if (res == 0) {
            return Boolean.compare(this.mDefaultNetwork, another.mDefaultNetwork);
        }
        return res;
    }
}
