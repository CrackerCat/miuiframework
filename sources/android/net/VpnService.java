package android.net;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.system.OsConstants;
import com.android.internal.net.VpnConfig;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VpnService extends Service {
    public static final String SERVICE_INTERFACE = "android.net.VpnService";
    public static final String SERVICE_META_DATA_SUPPORTS_ALWAYS_ON = "android.net.VpnService.SUPPORTS_ALWAYS_ON";

    public class Builder {
        @UnsupportedAppUsage
        private final List<LinkAddress> mAddresses = new ArrayList();
        private final VpnConfig mConfig = new VpnConfig();
        @UnsupportedAppUsage
        private final List<RouteInfo> mRoutes = new ArrayList();

        public Builder() {
            this.mConfig.user = VpnService.this.getClass().getName();
        }

        public Builder setSession(String session) {
            this.mConfig.session = session;
            return this;
        }

        public Builder setConfigureIntent(PendingIntent intent) {
            this.mConfig.configureIntent = intent;
            return this;
        }

        public Builder setMtu(int mtu) {
            if (mtu > 0) {
                this.mConfig.mtu = mtu;
                return this;
            }
            throw new IllegalArgumentException("Bad mtu");
        }

        public Builder setHttpProxy(ProxyInfo proxyInfo) {
            this.mConfig.proxyInfo = proxyInfo;
            return this;
        }

        public Builder addAddress(InetAddress address, int prefixLength) {
            VpnService.check(address, prefixLength);
            if (address.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            this.mAddresses.add(new LinkAddress(address, prefixLength));
            this.mConfig.updateAllowedFamilies(address);
            return this;
        }

        public Builder addAddress(String address, int prefixLength) {
            return addAddress(InetAddress.parseNumericAddress(address), prefixLength);
        }

        public Builder addRoute(InetAddress address, int prefixLength) {
            VpnService.check(address, prefixLength);
            int offset = prefixLength / 8;
            byte[] bytes = address.getAddress();
            if (offset < bytes.length) {
                bytes[offset] = (byte) (bytes[offset] << (prefixLength % 8));
                while (offset < bytes.length) {
                    if (bytes[offset] == (byte) 0) {
                        offset++;
                    } else {
                        throw new IllegalArgumentException("Bad address");
                    }
                }
            }
            this.mRoutes.add(new RouteInfo(new IpPrefix(address, prefixLength), null));
            this.mConfig.updateAllowedFamilies(address);
            return this;
        }

        public Builder addRoute(String address, int prefixLength) {
            return addRoute(InetAddress.parseNumericAddress(address), prefixLength);
        }

        public Builder addDnsServer(InetAddress address) {
            if (address.isLoopbackAddress() || address.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            if (this.mConfig.dnsServers == null) {
                this.mConfig.dnsServers = new ArrayList();
            }
            this.mConfig.dnsServers.add(address.getHostAddress());
            return this;
        }

        public Builder addDnsServer(String address) {
            return addDnsServer(InetAddress.parseNumericAddress(address));
        }

        public Builder addSearchDomain(String domain) {
            if (this.mConfig.searchDomains == null) {
                this.mConfig.searchDomains = new ArrayList();
            }
            this.mConfig.searchDomains.add(domain);
            return this;
        }

        public Builder allowFamily(int family) {
            if (family == OsConstants.AF_INET) {
                this.mConfig.allowIPv4 = true;
            } else if (family == OsConstants.AF_INET6) {
                this.mConfig.allowIPv6 = true;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(family);
                stringBuilder.append(" is neither ");
                stringBuilder.append(OsConstants.AF_INET);
                stringBuilder.append(" nor ");
                stringBuilder.append(OsConstants.AF_INET6);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            return this;
        }

        private void verifyApp(String packageName) throws NameNotFoundException {
            try {
                Stub.asInterface(ServiceManager.getService("package")).getApplicationInfo(packageName, 0, UserHandle.getCallingUserId());
            } catch (RemoteException e) {
                throw new IllegalStateException(e);
            }
        }

        public Builder addAllowedApplication(String packageName) throws NameNotFoundException {
            if (this.mConfig.disallowedApplications == null) {
                verifyApp(packageName);
                if (this.mConfig.allowedApplications == null) {
                    this.mConfig.allowedApplications = new ArrayList();
                }
                this.mConfig.allowedApplications.add(packageName);
                return this;
            }
            throw new UnsupportedOperationException("addDisallowedApplication already called");
        }

        public Builder addDisallowedApplication(String packageName) throws NameNotFoundException {
            if (this.mConfig.allowedApplications == null) {
                verifyApp(packageName);
                if (this.mConfig.disallowedApplications == null) {
                    this.mConfig.disallowedApplications = new ArrayList();
                }
                this.mConfig.disallowedApplications.add(packageName);
                return this;
            }
            throw new UnsupportedOperationException("addAllowedApplication already called");
        }

        public Builder allowBypass() {
            this.mConfig.allowBypass = true;
            return this;
        }

        public Builder setBlocking(boolean blocking) {
            this.mConfig.blocking = blocking;
            return this;
        }

        public Builder setUnderlyingNetworks(Network[] networks) {
            this.mConfig.underlyingNetworks = networks != null ? (Network[]) networks.clone() : null;
            return this;
        }

        public Builder setMetered(boolean isMetered) {
            this.mConfig.isMetered = isMetered;
            return this;
        }

        public ParcelFileDescriptor establish() {
            VpnConfig vpnConfig = this.mConfig;
            vpnConfig.addresses = this.mAddresses;
            vpnConfig.routes = this.mRoutes;
            try {
                return VpnService.getService().establishVpn(this.mConfig);
            } catch (RemoteException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private class Callback extends Binder {
        private Callback() {
        }

        /* Access modifiers changed, original: protected */
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            if (code != 16777215) {
                return false;
            }
            VpnService.this.onRevoke();
            return true;
        }
    }

    private static IConnectivityManager getService() {
        return IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
    }

    public static Intent prepare(Context context) {
        try {
            if (getService().prepareVpn(context.getPackageName(), null, context.getUserId())) {
                return null;
            }
        } catch (RemoteException e) {
        }
        return VpnConfig.getIntentForConfirmation();
    }

    @SystemApi
    public static void prepareAndAuthorize(Context context) {
        IConnectivityManager cm = getService();
        String packageName = context.getPackageName();
        try {
            int userId = context.getUserId();
            if (!cm.prepareVpn(packageName, null, userId)) {
                cm.prepareVpn(null, packageName, userId);
            }
            cm.setVpnPackageAuthorization(packageName, userId, true);
        } catch (RemoteException e) {
        }
    }

    public boolean protect(int socket) {
        return NetworkUtils.protectFromVpn(socket);
    }

    public boolean protect(Socket socket) {
        return protect(socket.getFileDescriptor$().getInt$());
    }

    public boolean protect(DatagramSocket socket) {
        return protect(socket.getFileDescriptor$().getInt$());
    }

    public boolean addAddress(InetAddress address, int prefixLength) {
        check(address, prefixLength);
        try {
            return getService().addVpnAddress(address.getHostAddress(), prefixLength);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean removeAddress(InetAddress address, int prefixLength) {
        check(address, prefixLength);
        try {
            return getService().removeVpnAddress(address.getHostAddress(), prefixLength);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean setUnderlyingNetworks(Network[] networks) {
        try {
            return getService().setUnderlyingNetworksForVpn(networks);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public final boolean isAlwaysOn() {
        try {
            return getService().isCallerCurrentAlwaysOnVpnApp();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final boolean isLockdownEnabled() {
        try {
            return getService().isCallerCurrentAlwaysOnVpnLockdownApp();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IBinder onBind(Intent intent) {
        if (intent != null) {
            if ("android.net.VpnService".equals(intent.getAction())) {
                return new Callback();
            }
        }
        return null;
    }

    public void onRevoke() {
        stopSelf();
    }

    private static void check(InetAddress address, int prefixLength) {
        if (address.isLoopbackAddress()) {
            throw new IllegalArgumentException("Bad address");
        }
        String str = "Bad prefixLength";
        if (address instanceof Inet4Address) {
            if (prefixLength < 0 || prefixLength > 32) {
                throw new IllegalArgumentException(str);
            }
        } else if (!(address instanceof Inet6Address)) {
            throw new IllegalArgumentException("Unsupported family");
        } else if (prefixLength < 0 || prefixLength > 128) {
            throw new IllegalArgumentException(str);
        }
    }
}
