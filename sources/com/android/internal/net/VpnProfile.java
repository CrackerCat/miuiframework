package com.android.internal.net;

import android.annotation.UnsupportedAppUsage;
import android.net.ProxyInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class VpnProfile implements Cloneable, Parcelable {
    public static final Creator<VpnProfile> CREATOR = new Creator<VpnProfile>() {
        public VpnProfile createFromParcel(Parcel in) {
            return new VpnProfile(in);
        }

        public VpnProfile[] newArray(int size) {
            return new VpnProfile[size];
        }
    };
    public static final int PROXY_MANUAL = 1;
    public static final int PROXY_NONE = 0;
    private static final String TAG = "VpnProfile";
    public static final int TYPE_IPSEC_HYBRID_RSA = 5;
    public static final int TYPE_IPSEC_XAUTH_PSK = 3;
    public static final int TYPE_IPSEC_XAUTH_RSA = 4;
    public static final int TYPE_L2TP_IPSEC_PSK = 1;
    public static final int TYPE_L2TP_IPSEC_RSA = 2;
    public static final int TYPE_MAX = 5;
    public static final int TYPE_PPTP = 0;
    public String dnsServers;
    public String ipsecCaCert;
    public String ipsecIdentifier;
    public String ipsecSecret;
    public String ipsecServerCert;
    public String ipsecUserCert;
    @UnsupportedAppUsage
    public final String key;
    public String l2tpSecret;
    public boolean mppe;
    @UnsupportedAppUsage
    public String name;
    public String password;
    public ProxyInfo proxy;
    public String routes;
    @UnsupportedAppUsage
    public boolean saveLogin;
    public String searchDomains;
    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    public String server;
    @UnsupportedAppUsage
    public int type = 0;
    @UnsupportedAppUsage
    public String username;

    public VpnProfile(String key) {
        String str = "";
        this.name = str;
        this.server = str;
        this.username = str;
        this.password = str;
        this.dnsServers = str;
        this.searchDomains = str;
        this.routes = str;
        this.mppe = true;
        this.l2tpSecret = str;
        this.ipsecIdentifier = str;
        this.ipsecSecret = str;
        this.ipsecUserCert = str;
        this.ipsecCaCert = str;
        this.ipsecServerCert = str;
        this.proxy = null;
        this.saveLogin = false;
        this.key = key;
    }

    @UnsupportedAppUsage
    public VpnProfile(Parcel in) {
        String str = "";
        this.name = str;
        boolean z = false;
        this.server = str;
        this.username = str;
        this.password = str;
        this.dnsServers = str;
        this.searchDomains = str;
        this.routes = str;
        this.mppe = true;
        this.l2tpSecret = str;
        this.ipsecIdentifier = str;
        this.ipsecSecret = str;
        this.ipsecUserCert = str;
        this.ipsecCaCert = str;
        this.ipsecServerCert = str;
        this.proxy = null;
        this.saveLogin = false;
        this.key = in.readString();
        this.name = in.readString();
        this.type = in.readInt();
        this.server = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.dnsServers = in.readString();
        this.searchDomains = in.readString();
        this.routes = in.readString();
        this.mppe = in.readInt() != 0;
        this.l2tpSecret = in.readString();
        this.ipsecIdentifier = in.readString();
        this.ipsecSecret = in.readString();
        this.ipsecUserCert = in.readString();
        this.ipsecCaCert = in.readString();
        this.ipsecServerCert = in.readString();
        if (in.readInt() != 0) {
            z = true;
        }
        this.saveLogin = z;
        this.proxy = (ProxyInfo) in.readParcelable(null);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.key);
        out.writeString(this.name);
        out.writeInt(this.type);
        out.writeString(this.server);
        out.writeString(this.username);
        out.writeString(this.password);
        out.writeString(this.dnsServers);
        out.writeString(this.searchDomains);
        out.writeString(this.routes);
        out.writeInt(this.mppe);
        out.writeString(this.l2tpSecret);
        out.writeString(this.ipsecIdentifier);
        out.writeString(this.ipsecSecret);
        out.writeString(this.ipsecUserCert);
        out.writeString(this.ipsecCaCert);
        out.writeString(this.ipsecServerCert);
        out.writeInt(this.saveLogin);
        out.writeParcelable(this.proxy, flags);
    }

    @UnsupportedAppUsage
    public static VpnProfile decode(String key, byte[] value) {
        if (key == null) {
            return null;
        }
        try {
            String[] values = new String(value, StandardCharsets.UTF_8).split("\u0000", -1);
            if (values.length >= 14) {
                if (values.length <= 19) {
                    VpnProfile profile = new VpnProfile(key);
                    boolean z = false;
                    profile.name = values[0];
                    profile.type = Integer.parseInt(values[1]);
                    if (profile.type >= 0) {
                        if (profile.type <= 5) {
                            profile.server = values[2];
                            profile.username = values[3];
                            profile.password = values[4];
                            profile.dnsServers = values[5];
                            profile.searchDomains = values[6];
                            profile.routes = values[7];
                            profile.mppe = Boolean.parseBoolean(values[8]);
                            profile.l2tpSecret = values[9];
                            profile.ipsecIdentifier = values[10];
                            profile.ipsecSecret = values[11];
                            profile.ipsecUserCert = values[12];
                            profile.ipsecCaCert = values[13];
                            String pacFileUrl = "";
                            profile.ipsecServerCert = values.length > 14 ? values[14] : pacFileUrl;
                            if (values.length > 15) {
                                String host = values.length > 15 ? values[15] : pacFileUrl;
                                String port = values.length > 16 ? values[16] : pacFileUrl;
                                String exclList = values.length > 17 ? values[17] : pacFileUrl;
                                if (values.length > 18) {
                                    pacFileUrl = values[18];
                                }
                                if (pacFileUrl.isEmpty()) {
                                    profile.proxy = new ProxyInfo(host, port.isEmpty() ? 0 : Integer.parseInt(port), exclList);
                                } else {
                                    profile.proxy = new ProxyInfo(pacFileUrl);
                                }
                            }
                            if (!(profile.username.isEmpty() && profile.password.isEmpty())) {
                                z = true;
                            }
                            profile.saveLogin = z;
                            return profile;
                        }
                    }
                    return null;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] encode() {
        StringBuilder builder = new StringBuilder(this.name);
        builder.append(0);
        builder.append(this.type);
        builder.append(0);
        builder.append(this.server);
        builder.append(0);
        String str = "";
        builder.append(this.saveLogin ? this.username : str);
        builder.append(0);
        builder.append(this.saveLogin ? this.password : str);
        builder.append(0);
        builder.append(this.dnsServers);
        builder.append(0);
        builder.append(this.searchDomains);
        builder.append(0);
        builder.append(this.routes);
        builder.append(0);
        builder.append(this.mppe);
        builder.append(0);
        builder.append(this.l2tpSecret);
        builder.append(0);
        builder.append(this.ipsecIdentifier);
        builder.append(0);
        builder.append(this.ipsecSecret);
        builder.append(0);
        builder.append(this.ipsecUserCert);
        builder.append(0);
        builder.append(this.ipsecCaCert);
        builder.append(0);
        builder.append(this.ipsecServerCert);
        if (this.proxy != null) {
            builder.append(0);
            builder.append(this.proxy.getHost() != null ? this.proxy.getHost() : str);
            builder.append(0);
            builder.append(this.proxy.getPort());
            builder.append(0);
            if (this.proxy.getExclusionListAsString() != null) {
                str = this.proxy.getExclusionListAsString();
            }
            builder.append(str);
            builder.append(0);
            builder.append(this.proxy.getPacFileUrl().toString());
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public boolean isValidLockdownProfile() {
        return isTypeValidForLockdown() && isServerAddressNumeric() && hasDns() && areDnsAddressesNumeric();
    }

    public boolean isTypeValidForLockdown() {
        return this.type != 0;
    }

    public boolean isServerAddressNumeric() {
        try {
            InetAddress.parseNumericAddress(this.server);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean hasDns() {
        return TextUtils.isEmpty(this.dnsServers) ^ 1;
    }

    public boolean areDnsAddressesNumeric() {
        try {
            for (String dnsServer : this.dnsServers.split(" +")) {
                InetAddress.parseNumericAddress(dnsServer);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public int describeContents() {
        return 0;
    }
}
