package android.net;

import android.annotation.UnsupportedAppUsage;
import android.net.wifi.WifiInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.BackupUtils;
import android.util.BackupUtils.BadVersionException;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class NetworkTemplate implements Parcelable {
    private static final int BACKUP_VERSION = 1;
    @UnsupportedAppUsage
    public static final Creator<NetworkTemplate> CREATOR = new Creator<NetworkTemplate>() {
        public NetworkTemplate createFromParcel(Parcel in) {
            return new NetworkTemplate(in, null);
        }

        public NetworkTemplate[] newArray(int size) {
            return new NetworkTemplate[size];
        }
    };
    public static final int MATCH_BLUETOOTH = 8;
    public static final int MATCH_ETHERNET = 5;
    public static final int MATCH_MOBILE = 1;
    public static final int MATCH_MOBILE_WILDCARD = 6;
    public static final int MATCH_PROXY = 9;
    public static final int MATCH_WIFI = 4;
    public static final int MATCH_WIFI_WILDCARD = 7;
    private static final String TAG = "NetworkTemplate";
    private static boolean sForceAllNetworkTypes = false;
    private final int mDefaultNetwork;
    private final int mMatchRule;
    private final String[] mMatchSubscriberIds;
    private final int mMetered;
    private final String mNetworkId;
    private final int mRoaming;
    private final String mSubscriberId;

    /* synthetic */ NetworkTemplate(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    private static boolean isKnownMatchRule(int rule) {
        if (rule != 1) {
            switch (rule) {
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    @VisibleForTesting
    public static void forceAllNetworkTypes() {
        sForceAllNetworkTypes = true;
    }

    @VisibleForTesting
    public static void resetForceAllNetworkTypes() {
        sForceAllNetworkTypes = false;
    }

    @UnsupportedAppUsage
    public static NetworkTemplate buildTemplateMobileAll(String subscriberId) {
        return new NetworkTemplate(1, subscriberId, null);
    }

    @UnsupportedAppUsage
    public static NetworkTemplate buildTemplateMobileWildcard() {
        return new NetworkTemplate(6, null, null);
    }

    @UnsupportedAppUsage
    public static NetworkTemplate buildTemplateWifiWildcard() {
        return new NetworkTemplate(7, null, null);
    }

    @Deprecated
    @UnsupportedAppUsage
    public static NetworkTemplate buildTemplateWifi() {
        return buildTemplateWifiWildcard();
    }

    public static NetworkTemplate buildTemplateWifi(String networkId) {
        return new NetworkTemplate(4, null, networkId);
    }

    @UnsupportedAppUsage
    public static NetworkTemplate buildTemplateEthernet() {
        return new NetworkTemplate(5, null, null);
    }

    public static NetworkTemplate buildTemplateBluetooth() {
        return new NetworkTemplate(8, null, null);
    }

    public static NetworkTemplate buildTemplateProxy() {
        return new NetworkTemplate(9, null, null);
    }

    @UnsupportedAppUsage
    public NetworkTemplate(int matchRule, String subscriberId, String networkId) {
        this(matchRule, subscriberId, new String[]{subscriberId}, networkId);
    }

    public NetworkTemplate(int matchRule, String subscriberId, String[] matchSubscriberIds, String networkId) {
        this(matchRule, subscriberId, matchSubscriberIds, networkId, -1, -1, -1);
    }

    public NetworkTemplate(int matchRule, String subscriberId, String[] matchSubscriberIds, String networkId, int metered, int roaming, int defaultNetwork) {
        this.mMatchRule = matchRule;
        this.mSubscriberId = subscriberId;
        this.mMatchSubscriberIds = matchSubscriberIds;
        this.mNetworkId = networkId;
        this.mMetered = metered;
        this.mRoaming = roaming;
        this.mDefaultNetwork = defaultNetwork;
        if (!isKnownMatchRule(matchRule)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown network template rule ");
            stringBuilder.append(matchRule);
            stringBuilder.append(" will not match any identity.");
            Log.e(TAG, stringBuilder.toString());
        }
    }

    private NetworkTemplate(Parcel in) {
        this.mMatchRule = in.readInt();
        this.mSubscriberId = in.readString();
        this.mMatchSubscriberIds = in.createStringArray();
        this.mNetworkId = in.readString();
        this.mMetered = in.readInt();
        this.mRoaming = in.readInt();
        this.mDefaultNetwork = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMatchRule);
        dest.writeString(this.mSubscriberId);
        dest.writeStringArray(this.mMatchSubscriberIds);
        dest.writeString(this.mNetworkId);
        dest.writeInt(this.mMetered);
        dest.writeInt(this.mRoaming);
        dest.writeInt(this.mDefaultNetwork);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("NetworkTemplate: ");
        builder.append("matchRule=");
        builder.append(getMatchRuleName(this.mMatchRule));
        if (this.mSubscriberId != null) {
            builder.append(", subscriberId=");
            builder.append(NetworkIdentity.scrubSubscriberId(this.mSubscriberId));
        }
        if (this.mMatchSubscriberIds != null) {
            builder.append(", matchSubscriberIds=");
            builder.append(Arrays.toString(NetworkIdentity.scrubSubscriberId(this.mMatchSubscriberIds)));
        }
        if (this.mNetworkId != null) {
            builder.append(", networkId=");
            builder.append(this.mNetworkId);
        }
        if (this.mMetered != -1) {
            builder.append(", metered=");
            builder.append(NetworkStats.meteredToString(this.mMetered));
        }
        if (this.mRoaming != -1) {
            builder.append(", roaming=");
            builder.append(NetworkStats.roamingToString(this.mRoaming));
        }
        if (this.mDefaultNetwork != -1) {
            builder.append(", defaultNetwork=");
            builder.append(NetworkStats.defaultNetworkToString(this.mDefaultNetwork));
        }
        return builder.toString();
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.mMatchRule), this.mSubscriberId, this.mNetworkId, Integer.valueOf(this.mMetered), Integer.valueOf(this.mRoaming), Integer.valueOf(this.mDefaultNetwork)});
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof NetworkTemplate)) {
            return false;
        }
        NetworkTemplate other = (NetworkTemplate) obj;
        if (this.mMatchRule == other.mMatchRule && Objects.equals(this.mSubscriberId, other.mSubscriberId) && Objects.equals(this.mNetworkId, other.mNetworkId) && this.mMetered == other.mMetered && this.mRoaming == other.mRoaming && this.mDefaultNetwork == other.mDefaultNetwork) {
            z = true;
        }
        return z;
    }

    public boolean isMatchRuleMobile() {
        int i = this.mMatchRule;
        if (i == 1 || i == 6) {
            return true;
        }
        return false;
    }

    public boolean isPersistable() {
        int i = this.mMatchRule;
        if (i == 6 || i == 7) {
            return false;
        }
        return true;
    }

    @UnsupportedAppUsage
    public int getMatchRule() {
        return this.mMatchRule;
    }

    @UnsupportedAppUsage
    public String getSubscriberId() {
        return this.mSubscriberId;
    }

    public String getNetworkId() {
        return this.mNetworkId;
    }

    public boolean matches(NetworkIdentity ident) {
        if (!matchesMetered(ident) || !matchesRoaming(ident) || !matchesDefaultNetwork(ident)) {
            return false;
        }
        int i = this.mMatchRule;
        if (i == 1) {
            return matchesMobile(ident);
        }
        switch (i) {
            case 4:
                return matchesWifi(ident);
            case 5:
                return matchesEthernet(ident);
            case 6:
                return matchesMobileWildcard(ident);
            case 7:
                return matchesWifiWildcard(ident);
            case 8:
                return matchesBluetooth(ident);
            case 9:
                return matchesProxy(ident);
            default:
                return false;
        }
    }

    private boolean matchesMetered(NetworkIdentity ident) {
        int i = this.mMetered;
        if (i == -1) {
            return true;
        }
        if (i == 1 && ident.mMetered) {
            return true;
        }
        return this.mMetered == 0 && !ident.mMetered;
    }

    private boolean matchesRoaming(NetworkIdentity ident) {
        int i = this.mRoaming;
        if (i == -1) {
            return true;
        }
        if (i == 1 && ident.mRoaming) {
            return true;
        }
        return this.mRoaming == 0 && !ident.mRoaming;
    }

    private boolean matchesDefaultNetwork(NetworkIdentity ident) {
        int i = this.mDefaultNetwork;
        if (i == -1) {
            return true;
        }
        if (i == 1 && ident.mDefaultNetwork) {
            return true;
        }
        return this.mDefaultNetwork == 0 && !ident.mDefaultNetwork;
    }

    public boolean matchesSubscriberId(String subscriberId) {
        return ArrayUtils.contains(this.mMatchSubscriberIds, (Object) subscriberId);
    }

    private boolean matchesMobile(NetworkIdentity ident) {
        boolean z = true;
        if (ident.mType == 6) {
            return true;
        }
        if (!((sForceAllNetworkTypes || (ident.mType == 0 && ident.mMetered)) && !ArrayUtils.isEmpty(this.mMatchSubscriberIds) && ArrayUtils.contains(this.mMatchSubscriberIds, ident.mSubscriberId))) {
            z = false;
        }
        return z;
    }

    private boolean matchesWifi(NetworkIdentity ident) {
        if (ident.mType != 1) {
            return false;
        }
        return Objects.equals(WifiInfo.removeDoubleQuotes(this.mNetworkId), WifiInfo.removeDoubleQuotes(ident.mNetworkId));
    }

    private boolean matchesEthernet(NetworkIdentity ident) {
        if (ident.mType == 9) {
            return true;
        }
        return false;
    }

    private boolean matchesMobileWildcard(NetworkIdentity ident) {
        boolean z = true;
        if (ident.mType == 6) {
            return true;
        }
        if (!(sForceAllNetworkTypes || (ident.mType == 0 && ident.mMetered))) {
            z = false;
        }
        return z;
    }

    private boolean matchesWifiWildcard(NetworkIdentity ident) {
        int i = ident.mType;
        if (i == 1 || i == 13) {
            return true;
        }
        return false;
    }

    private boolean matchesBluetooth(NetworkIdentity ident) {
        if (ident.mType == 7) {
            return true;
        }
        return false;
    }

    private boolean matchesProxy(NetworkIdentity ident) {
        return ident.mType == 16;
    }

    private static String getMatchRuleName(int matchRule) {
        if (matchRule == 1) {
            return "MOBILE";
        }
        switch (matchRule) {
            case 4:
                return "WIFI";
            case 5:
                return "ETHERNET";
            case 6:
                return "MOBILE_WILDCARD";
            case 7:
                return "WIFI_WILDCARD";
            case 8:
                return "BLUETOOTH";
            case 9:
                return "PROXY";
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("UNKNOWN(");
                stringBuilder.append(matchRule);
                stringBuilder.append(")");
                return stringBuilder.toString();
        }
    }

    @UnsupportedAppUsage
    public static NetworkTemplate normalize(NetworkTemplate template, String[] merged) {
        if (template.isMatchRuleMobile() && ArrayUtils.contains((Object[]) merged, template.mSubscriberId)) {
            return new NetworkTemplate(template.mMatchRule, merged[0], merged, template.mNetworkId);
        }
        return template;
    }

    public byte[] getBytesForBackup() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeInt(1);
        out.writeInt(this.mMatchRule);
        BackupUtils.writeString(out, this.mSubscriberId);
        BackupUtils.writeString(out, this.mNetworkId);
        return baos.toByteArray();
    }

    public static NetworkTemplate getNetworkTemplateFromBackup(DataInputStream in) throws IOException, BadVersionException {
        int version = in.readInt();
        if (version < 1 || version > 1) {
            throw new BadVersionException("Unknown Backup Serialization Version");
        }
        int matchRule = in.readInt();
        String subscriberId = BackupUtils.readString(in);
        String networkId = BackupUtils.readString(in);
        if (isKnownMatchRule(matchRule)) {
            return new NetworkTemplate(matchRule, subscriberId, networkId);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Restored network template contains unknown match rule ");
        stringBuilder.append(matchRule);
        throw new BadVersionException(stringBuilder.toString());
    }
}
