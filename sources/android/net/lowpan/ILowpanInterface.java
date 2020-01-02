package android.net.lowpan;

import android.net.IpPrefix;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.Map;

public interface ILowpanInterface extends IInterface {
    public static final int ERROR_ALREADY = 9;
    public static final int ERROR_BUSY = 8;
    public static final int ERROR_CANCELED = 10;
    public static final int ERROR_DISABLED = 3;
    public static final int ERROR_FEATURE_NOT_SUPPORTED = 11;
    public static final int ERROR_FORM_FAILED_AT_SCAN = 15;
    public static final int ERROR_INVALID_ARGUMENT = 2;
    public static final int ERROR_IO_FAILURE = 6;
    public static final int ERROR_JOIN_FAILED_AT_AUTH = 14;
    public static final int ERROR_JOIN_FAILED_AT_SCAN = 13;
    public static final int ERROR_JOIN_FAILED_UNKNOWN = 12;
    public static final int ERROR_NCP_PROBLEM = 7;
    public static final int ERROR_TIMEOUT = 5;
    public static final int ERROR_UNSPECIFIED = 1;
    public static final int ERROR_WRONG_STATE = 4;
    public static final String KEY_CHANNEL_MASK = "android.net.lowpan.property.CHANNEL_MASK";
    public static final String KEY_MAX_TX_POWER = "android.net.lowpan.property.MAX_TX_POWER";
    public static final String NETWORK_TYPE_THREAD_V1 = "org.threadgroup.thread.v1";
    public static final String NETWORK_TYPE_UNKNOWN = "unknown";
    public static final String PERM_ACCESS_LOWPAN_STATE = "android.permission.ACCESS_LOWPAN_STATE";
    public static final String PERM_CHANGE_LOWPAN_STATE = "android.permission.CHANGE_LOWPAN_STATE";
    public static final String PERM_READ_LOWPAN_CREDENTIAL = "android.permission.READ_LOWPAN_CREDENTIAL";
    public static final String ROLE_COORDINATOR = "coordinator";
    public static final String ROLE_DETACHED = "detached";
    public static final String ROLE_END_DEVICE = "end-device";
    public static final String ROLE_LEADER = "leader";
    public static final String ROLE_ROUTER = "router";
    public static final String ROLE_SLEEPY_END_DEVICE = "sleepy-end-device";
    public static final String ROLE_SLEEPY_ROUTER = "sleepy-router";
    public static final String STATE_ATTACHED = "attached";
    public static final String STATE_ATTACHING = "attaching";
    public static final String STATE_COMMISSIONING = "commissioning";
    public static final String STATE_FAULT = "fault";
    public static final String STATE_OFFLINE = "offline";

    public static class Default implements ILowpanInterface {
        public String getName() throws RemoteException {
            return null;
        }

        public String getNcpVersion() throws RemoteException {
            return null;
        }

        public String getDriverVersion() throws RemoteException {
            return null;
        }

        public LowpanChannelInfo[] getSupportedChannels() throws RemoteException {
            return null;
        }

        public String[] getSupportedNetworkTypes() throws RemoteException {
            return null;
        }

        public byte[] getMacAddress() throws RemoteException {
            return null;
        }

        public boolean isEnabled() throws RemoteException {
            return false;
        }

        public void setEnabled(boolean enabled) throws RemoteException {
        }

        public boolean isUp() throws RemoteException {
            return false;
        }

        public boolean isCommissioned() throws RemoteException {
            return false;
        }

        public boolean isConnected() throws RemoteException {
            return false;
        }

        public String getState() throws RemoteException {
            return null;
        }

        public String getRole() throws RemoteException {
            return null;
        }

        public String getPartitionId() throws RemoteException {
            return null;
        }

        public byte[] getExtendedAddress() throws RemoteException {
            return null;
        }

        public LowpanIdentity getLowpanIdentity() throws RemoteException {
            return null;
        }

        public LowpanCredential getLowpanCredential() throws RemoteException {
            return null;
        }

        public String[] getLinkAddresses() throws RemoteException {
            return null;
        }

        public IpPrefix[] getLinkNetworks() throws RemoteException {
            return null;
        }

        public void join(LowpanProvision provision) throws RemoteException {
        }

        public void form(LowpanProvision provision) throws RemoteException {
        }

        public void attach(LowpanProvision provision) throws RemoteException {
        }

        public void leave() throws RemoteException {
        }

        public void reset() throws RemoteException {
        }

        public void startCommissioningSession(LowpanBeaconInfo beaconInfo) throws RemoteException {
        }

        public void closeCommissioningSession() throws RemoteException {
        }

        public void sendToCommissioner(byte[] packet) throws RemoteException {
        }

        public void beginLowPower() throws RemoteException {
        }

        public void pollForData() throws RemoteException {
        }

        public void onHostWake() throws RemoteException {
        }

        public void addListener(ILowpanInterfaceListener listener) throws RemoteException {
        }

        public void removeListener(ILowpanInterfaceListener listener) throws RemoteException {
        }

        public void startNetScan(Map properties, ILowpanNetScanCallback listener) throws RemoteException {
        }

        public void stopNetScan() throws RemoteException {
        }

        public void startEnergyScan(Map properties, ILowpanEnergyScanCallback listener) throws RemoteException {
        }

        public void stopEnergyScan() throws RemoteException {
        }

        public void addOnMeshPrefix(IpPrefix prefix, int flags) throws RemoteException {
        }

        public void removeOnMeshPrefix(IpPrefix prefix) throws RemoteException {
        }

        public void addExternalRoute(IpPrefix prefix, int flags) throws RemoteException {
        }

        public void removeExternalRoute(IpPrefix prefix) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements ILowpanInterface {
        private static final String DESCRIPTOR = "android.net.lowpan.ILowpanInterface";
        static final int TRANSACTION_addExternalRoute = 39;
        static final int TRANSACTION_addListener = 31;
        static final int TRANSACTION_addOnMeshPrefix = 37;
        static final int TRANSACTION_attach = 22;
        static final int TRANSACTION_beginLowPower = 28;
        static final int TRANSACTION_closeCommissioningSession = 26;
        static final int TRANSACTION_form = 21;
        static final int TRANSACTION_getDriverVersion = 3;
        static final int TRANSACTION_getExtendedAddress = 15;
        static final int TRANSACTION_getLinkAddresses = 18;
        static final int TRANSACTION_getLinkNetworks = 19;
        static final int TRANSACTION_getLowpanCredential = 17;
        static final int TRANSACTION_getLowpanIdentity = 16;
        static final int TRANSACTION_getMacAddress = 6;
        static final int TRANSACTION_getName = 1;
        static final int TRANSACTION_getNcpVersion = 2;
        static final int TRANSACTION_getPartitionId = 14;
        static final int TRANSACTION_getRole = 13;
        static final int TRANSACTION_getState = 12;
        static final int TRANSACTION_getSupportedChannels = 4;
        static final int TRANSACTION_getSupportedNetworkTypes = 5;
        static final int TRANSACTION_isCommissioned = 10;
        static final int TRANSACTION_isConnected = 11;
        static final int TRANSACTION_isEnabled = 7;
        static final int TRANSACTION_isUp = 9;
        static final int TRANSACTION_join = 20;
        static final int TRANSACTION_leave = 23;
        static final int TRANSACTION_onHostWake = 30;
        static final int TRANSACTION_pollForData = 29;
        static final int TRANSACTION_removeExternalRoute = 40;
        static final int TRANSACTION_removeListener = 32;
        static final int TRANSACTION_removeOnMeshPrefix = 38;
        static final int TRANSACTION_reset = 24;
        static final int TRANSACTION_sendToCommissioner = 27;
        static final int TRANSACTION_setEnabled = 8;
        static final int TRANSACTION_startCommissioningSession = 25;
        static final int TRANSACTION_startEnergyScan = 35;
        static final int TRANSACTION_startNetScan = 33;
        static final int TRANSACTION_stopEnergyScan = 36;
        static final int TRANSACTION_stopNetScan = 34;

        private static class Proxy implements ILowpanInterface {
            public static ILowpanInterface sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public String getName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    String str = true;
                    if (!this.mRemote.transact(1, _data, _reply, 0)) {
                        str = Stub.getDefaultImpl();
                        if (str != 0) {
                            str = Stub.getDefaultImpl().getName();
                            return str;
                        }
                    }
                    _reply.readException();
                    str = _reply.readString();
                    String _result = str;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getNcpVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    String str = 2;
                    if (!this.mRemote.transact(2, _data, _reply, 0)) {
                        str = Stub.getDefaultImpl();
                        if (str != 0) {
                            str = Stub.getDefaultImpl().getNcpVersion();
                            return str;
                        }
                    }
                    _reply.readException();
                    str = _reply.readString();
                    String _result = str;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getDriverVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    String str = 3;
                    if (!this.mRemote.transact(3, _data, _reply, 0)) {
                        str = Stub.getDefaultImpl();
                        if (str != 0) {
                            str = Stub.getDefaultImpl().getDriverVersion();
                            return str;
                        }
                    }
                    _reply.readException();
                    str = _reply.readString();
                    String _result = str;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public LowpanChannelInfo[] getSupportedChannels() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    LowpanChannelInfo[] lowpanChannelInfoArr = 4;
                    if (!this.mRemote.transact(4, _data, _reply, 0)) {
                        lowpanChannelInfoArr = Stub.getDefaultImpl();
                        if (lowpanChannelInfoArr != 0) {
                            lowpanChannelInfoArr = Stub.getDefaultImpl().getSupportedChannels();
                            return lowpanChannelInfoArr;
                        }
                    }
                    _reply.readException();
                    LowpanChannelInfo[] _result = (LowpanChannelInfo[]) _reply.createTypedArray(LowpanChannelInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getSupportedNetworkTypes() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    String[] strArr = 5;
                    if (!this.mRemote.transact(5, _data, _reply, 0)) {
                        strArr = Stub.getDefaultImpl();
                        if (strArr != 0) {
                            strArr = Stub.getDefaultImpl().getSupportedNetworkTypes();
                            return strArr;
                        }
                    }
                    _reply.readException();
                    strArr = _reply.createStringArray();
                    String[] _result = strArr;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getMacAddress() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    byte[] bArr = 6;
                    if (!this.mRemote.transact(6, _data, _reply, 0)) {
                        bArr = Stub.getDefaultImpl();
                        if (bArr != 0) {
                            bArr = Stub.getDefaultImpl().getMacAddress();
                            return bArr;
                        }
                    }
                    _reply.readException();
                    bArr = _reply.createByteArray();
                    byte[] _result = bArr;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(7, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().isEnabled();
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setEnabled(enabled);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isUp() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(9, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().isUp();
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isCommissioned() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(10, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().isCommissioned();
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isConnected() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(11, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().isConnected();
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    String str = 12;
                    if (!this.mRemote.transact(12, _data, _reply, 0)) {
                        str = Stub.getDefaultImpl();
                        if (str != 0) {
                            str = Stub.getDefaultImpl().getState();
                            return str;
                        }
                    }
                    _reply.readException();
                    str = _reply.readString();
                    String _result = str;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getRole() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    String str = 13;
                    if (!this.mRemote.transact(13, _data, _reply, 0)) {
                        str = Stub.getDefaultImpl();
                        if (str != 0) {
                            str = Stub.getDefaultImpl().getRole();
                            return str;
                        }
                    }
                    _reply.readException();
                    str = _reply.readString();
                    String _result = str;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getPartitionId() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    String str = 14;
                    if (!this.mRemote.transact(14, _data, _reply, 0)) {
                        str = Stub.getDefaultImpl();
                        if (str != 0) {
                            str = Stub.getDefaultImpl().getPartitionId();
                            return str;
                        }
                    }
                    _reply.readException();
                    str = _reply.readString();
                    String _result = str;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getExtendedAddress() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    byte[] bArr = 15;
                    if (!this.mRemote.transact(15, _data, _reply, 0)) {
                        bArr = Stub.getDefaultImpl();
                        if (bArr != 0) {
                            bArr = Stub.getDefaultImpl().getExtendedAddress();
                            return bArr;
                        }
                    }
                    _reply.readException();
                    bArr = _reply.createByteArray();
                    byte[] _result = bArr;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public LowpanIdentity getLowpanIdentity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    LowpanIdentity lowpanIdentity = 16;
                    if (!this.mRemote.transact(16, _data, _reply, 0)) {
                        lowpanIdentity = Stub.getDefaultImpl();
                        if (lowpanIdentity != 0) {
                            lowpanIdentity = Stub.getDefaultImpl().getLowpanIdentity();
                            return lowpanIdentity;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        lowpanIdentity = (LowpanIdentity) LowpanIdentity.CREATOR.createFromParcel(_reply);
                    } else {
                        lowpanIdentity = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return lowpanIdentity;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public LowpanCredential getLowpanCredential() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    LowpanCredential lowpanCredential = 17;
                    if (!this.mRemote.transact(17, _data, _reply, 0)) {
                        lowpanCredential = Stub.getDefaultImpl();
                        if (lowpanCredential != 0) {
                            lowpanCredential = Stub.getDefaultImpl().getLowpanCredential();
                            return lowpanCredential;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        lowpanCredential = (LowpanCredential) LowpanCredential.CREATOR.createFromParcel(_reply);
                    } else {
                        lowpanCredential = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return lowpanCredential;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getLinkAddresses() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    String[] strArr = 18;
                    if (!this.mRemote.transact(18, _data, _reply, 0)) {
                        strArr = Stub.getDefaultImpl();
                        if (strArr != 0) {
                            strArr = Stub.getDefaultImpl().getLinkAddresses();
                            return strArr;
                        }
                    }
                    _reply.readException();
                    strArr = _reply.createStringArray();
                    String[] _result = strArr;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public IpPrefix[] getLinkNetworks() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    IpPrefix[] ipPrefixArr = 19;
                    if (!this.mRemote.transact(19, _data, _reply, 0)) {
                        ipPrefixArr = Stub.getDefaultImpl();
                        if (ipPrefixArr != 0) {
                            ipPrefixArr = Stub.getDefaultImpl().getLinkNetworks();
                            return ipPrefixArr;
                        }
                    }
                    _reply.readException();
                    IpPrefix[] _result = (IpPrefix[]) _reply.createTypedArray(IpPrefix.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void join(LowpanProvision provision) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (provision != null) {
                        _data.writeInt(1);
                        provision.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(20, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().join(provision);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void form(LowpanProvision provision) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (provision != null) {
                        _data.writeInt(1);
                        provision.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(21, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().form(provision);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void attach(LowpanProvision provision) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (provision != null) {
                        _data.writeInt(1);
                        provision.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(22, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().attach(provision);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void leave() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(23, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().leave();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reset() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(24, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().reset();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void startCommissioningSession(LowpanBeaconInfo beaconInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (beaconInfo != null) {
                        _data.writeInt(1);
                        beaconInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(25, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startCommissioningSession(beaconInfo);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void closeCommissioningSession() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(26, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().closeCommissioningSession();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendToCommissioner(byte[] packet) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(packet);
                    if (this.mRemote.transact(27, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().sendToCommissioner(packet);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void beginLowPower() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(28, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().beginLowPower();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void pollForData() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(29, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().pollForData();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onHostWake() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(30, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onHostWake();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void addListener(ILowpanInterfaceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(31, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().addListener(listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeListener(ILowpanInterfaceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(32, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().removeListener(listener);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void startNetScan(Map properties, ILowpanNetScanCallback listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeMap(properties);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(33, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startNetScan(properties, listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void stopNetScan() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(34, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().stopNetScan();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void startEnergyScan(Map properties, ILowpanEnergyScanCallback listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeMap(properties);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(35, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startEnergyScan(properties, listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void stopEnergyScan() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(36, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().stopEnergyScan();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void addOnMeshPrefix(IpPrefix prefix, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (prefix != null) {
                        _data.writeInt(1);
                        prefix.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    if (this.mRemote.transact(37, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().addOnMeshPrefix(prefix, flags);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeOnMeshPrefix(IpPrefix prefix) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (prefix != null) {
                        _data.writeInt(1);
                        prefix.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(38, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().removeOnMeshPrefix(prefix);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void addExternalRoute(IpPrefix prefix, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (prefix != null) {
                        _data.writeInt(1);
                        prefix.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    if (this.mRemote.transact(39, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().addExternalRoute(prefix, flags);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeExternalRoute(IpPrefix prefix) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (prefix != null) {
                        _data.writeInt(1);
                        prefix.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(40, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().removeExternalRoute(prefix);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILowpanInterface asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ILowpanInterface)) {
                return new Proxy(obj);
            }
            return (ILowpanInterface) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getName";
                case 2:
                    return "getNcpVersion";
                case 3:
                    return "getDriverVersion";
                case 4:
                    return "getSupportedChannels";
                case 5:
                    return "getSupportedNetworkTypes";
                case 6:
                    return "getMacAddress";
                case 7:
                    return "isEnabled";
                case 8:
                    return "setEnabled";
                case 9:
                    return "isUp";
                case 10:
                    return "isCommissioned";
                case 11:
                    return "isConnected";
                case 12:
                    return "getState";
                case 13:
                    return "getRole";
                case 14:
                    return "getPartitionId";
                case 15:
                    return "getExtendedAddress";
                case 16:
                    return "getLowpanIdentity";
                case 17:
                    return "getLowpanCredential";
                case 18:
                    return "getLinkAddresses";
                case 19:
                    return "getLinkNetworks";
                case 20:
                    return "join";
                case 21:
                    return "form";
                case 22:
                    return "attach";
                case 23:
                    return "leave";
                case 24:
                    return "reset";
                case 25:
                    return "startCommissioningSession";
                case 26:
                    return "closeCommissioningSession";
                case 27:
                    return "sendToCommissioner";
                case 28:
                    return "beginLowPower";
                case 29:
                    return "pollForData";
                case 30:
                    return "onHostWake";
                case 31:
                    return "addListener";
                case 32:
                    return "removeListener";
                case 33:
                    return "startNetScan";
                case 34:
                    return "stopNetScan";
                case 35:
                    return "startEnergyScan";
                case 36:
                    return "stopEnergyScan";
                case 37:
                    return "addOnMeshPrefix";
                case 38:
                    return "removeOnMeshPrefix";
                case 39:
                    return "addExternalRoute";
                case 40:
                    return "removeExternalRoute";
                default:
                    return null;
            }
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code != IBinder.INTERFACE_TRANSACTION) {
                boolean _arg0 = false;
                String _result;
                String[] _result2;
                byte[] _result3;
                LowpanProvision _arg02;
                IpPrefix _arg03;
                switch (code) {
                    case 1:
                        data.enforceInterface(descriptor);
                        _result = getName();
                        reply.writeNoException();
                        reply.writeString(_result);
                        return true;
                    case 2:
                        data.enforceInterface(descriptor);
                        _result = getNcpVersion();
                        reply.writeNoException();
                        reply.writeString(_result);
                        return true;
                    case 3:
                        data.enforceInterface(descriptor);
                        _result = getDriverVersion();
                        reply.writeNoException();
                        reply.writeString(_result);
                        return true;
                    case 4:
                        data.enforceInterface(descriptor);
                        LowpanChannelInfo[] _result4 = getSupportedChannels();
                        reply.writeNoException();
                        reply.writeTypedArray(_result4, 1);
                        return true;
                    case 5:
                        data.enforceInterface(descriptor);
                        _result2 = getSupportedNetworkTypes();
                        reply.writeNoException();
                        reply.writeStringArray(_result2);
                        return true;
                    case 6:
                        data.enforceInterface(descriptor);
                        _result3 = getMacAddress();
                        reply.writeNoException();
                        reply.writeByteArray(_result3);
                        return true;
                    case 7:
                        data.enforceInterface(descriptor);
                        _arg0 = isEnabled();
                        reply.writeNoException();
                        reply.writeInt(_arg0);
                        return true;
                    case 8:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = true;
                        }
                        setEnabled(_arg0);
                        reply.writeNoException();
                        return true;
                    case 9:
                        data.enforceInterface(descriptor);
                        _arg0 = isUp();
                        reply.writeNoException();
                        reply.writeInt(_arg0);
                        return true;
                    case 10:
                        data.enforceInterface(descriptor);
                        _arg0 = isCommissioned();
                        reply.writeNoException();
                        reply.writeInt(_arg0);
                        return true;
                    case 11:
                        data.enforceInterface(descriptor);
                        _arg0 = isConnected();
                        reply.writeNoException();
                        reply.writeInt(_arg0);
                        return true;
                    case 12:
                        data.enforceInterface(descriptor);
                        _result = getState();
                        reply.writeNoException();
                        reply.writeString(_result);
                        return true;
                    case 13:
                        data.enforceInterface(descriptor);
                        _result = getRole();
                        reply.writeNoException();
                        reply.writeString(_result);
                        return true;
                    case 14:
                        data.enforceInterface(descriptor);
                        _result = getPartitionId();
                        reply.writeNoException();
                        reply.writeString(_result);
                        return true;
                    case 15:
                        data.enforceInterface(descriptor);
                        _result3 = getExtendedAddress();
                        reply.writeNoException();
                        reply.writeByteArray(_result3);
                        return true;
                    case 16:
                        data.enforceInterface(descriptor);
                        LowpanIdentity _result5 = getLowpanIdentity();
                        reply.writeNoException();
                        if (_result5 != null) {
                            reply.writeInt(1);
                            _result5.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 17:
                        data.enforceInterface(descriptor);
                        LowpanCredential _result6 = getLowpanCredential();
                        reply.writeNoException();
                        if (_result6 != null) {
                            reply.writeInt(1);
                            _result6.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 18:
                        data.enforceInterface(descriptor);
                        _result2 = getLinkAddresses();
                        reply.writeNoException();
                        reply.writeStringArray(_result2);
                        return true;
                    case 19:
                        data.enforceInterface(descriptor);
                        IpPrefix[] _result7 = getLinkNetworks();
                        reply.writeNoException();
                        reply.writeTypedArray(_result7, 1);
                        return true;
                    case 20:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (LowpanProvision) LowpanProvision.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        join(_arg02);
                        reply.writeNoException();
                        return true;
                    case 21:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (LowpanProvision) LowpanProvision.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        form(_arg02);
                        reply.writeNoException();
                        return true;
                    case 22:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (LowpanProvision) LowpanProvision.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        attach(_arg02);
                        reply.writeNoException();
                        return true;
                    case 23:
                        data.enforceInterface(descriptor);
                        leave();
                        reply.writeNoException();
                        return true;
                    case 24:
                        data.enforceInterface(descriptor);
                        reset();
                        reply.writeNoException();
                        return true;
                    case 25:
                        LowpanBeaconInfo _arg04;
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg04 = (LowpanBeaconInfo) LowpanBeaconInfo.CREATOR.createFromParcel(data);
                        } else {
                            _arg04 = null;
                        }
                        startCommissioningSession(_arg04);
                        reply.writeNoException();
                        return true;
                    case 26:
                        data.enforceInterface(descriptor);
                        closeCommissioningSession();
                        reply.writeNoException();
                        return true;
                    case 27:
                        data.enforceInterface(descriptor);
                        sendToCommissioner(data.createByteArray());
                        return true;
                    case 28:
                        data.enforceInterface(descriptor);
                        beginLowPower();
                        reply.writeNoException();
                        return true;
                    case 29:
                        data.enforceInterface(descriptor);
                        pollForData();
                        return true;
                    case 30:
                        data.enforceInterface(descriptor);
                        onHostWake();
                        return true;
                    case 31:
                        data.enforceInterface(descriptor);
                        addListener(android.net.lowpan.ILowpanInterfaceListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 32:
                        data.enforceInterface(descriptor);
                        removeListener(android.net.lowpan.ILowpanInterfaceListener.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 33:
                        data.enforceInterface(descriptor);
                        startNetScan(data.readHashMap(getClass().getClassLoader()), android.net.lowpan.ILowpanNetScanCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 34:
                        data.enforceInterface(descriptor);
                        stopNetScan();
                        return true;
                    case 35:
                        data.enforceInterface(descriptor);
                        startEnergyScan(data.readHashMap(getClass().getClassLoader()), android.net.lowpan.ILowpanEnergyScanCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 36:
                        data.enforceInterface(descriptor);
                        stopEnergyScan();
                        return true;
                    case 37:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg03 = (IpPrefix) IpPrefix.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        addOnMeshPrefix(_arg03, data.readInt());
                        reply.writeNoException();
                        return true;
                    case 38:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg03 = (IpPrefix) IpPrefix.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        removeOnMeshPrefix(_arg03);
                        return true;
                    case 39:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg03 = (IpPrefix) IpPrefix.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        addExternalRoute(_arg03, data.readInt());
                        reply.writeNoException();
                        return true;
                    case 40:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg03 = (IpPrefix) IpPrefix.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        removeExternalRoute(_arg03);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(ILowpanInterface impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static ILowpanInterface getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void addExternalRoute(IpPrefix ipPrefix, int i) throws RemoteException;

    void addListener(ILowpanInterfaceListener iLowpanInterfaceListener) throws RemoteException;

    void addOnMeshPrefix(IpPrefix ipPrefix, int i) throws RemoteException;

    void attach(LowpanProvision lowpanProvision) throws RemoteException;

    void beginLowPower() throws RemoteException;

    void closeCommissioningSession() throws RemoteException;

    void form(LowpanProvision lowpanProvision) throws RemoteException;

    String getDriverVersion() throws RemoteException;

    byte[] getExtendedAddress() throws RemoteException;

    String[] getLinkAddresses() throws RemoteException;

    IpPrefix[] getLinkNetworks() throws RemoteException;

    LowpanCredential getLowpanCredential() throws RemoteException;

    LowpanIdentity getLowpanIdentity() throws RemoteException;

    byte[] getMacAddress() throws RemoteException;

    String getName() throws RemoteException;

    String getNcpVersion() throws RemoteException;

    String getPartitionId() throws RemoteException;

    String getRole() throws RemoteException;

    String getState() throws RemoteException;

    LowpanChannelInfo[] getSupportedChannels() throws RemoteException;

    String[] getSupportedNetworkTypes() throws RemoteException;

    boolean isCommissioned() throws RemoteException;

    boolean isConnected() throws RemoteException;

    boolean isEnabled() throws RemoteException;

    boolean isUp() throws RemoteException;

    void join(LowpanProvision lowpanProvision) throws RemoteException;

    void leave() throws RemoteException;

    void onHostWake() throws RemoteException;

    void pollForData() throws RemoteException;

    void removeExternalRoute(IpPrefix ipPrefix) throws RemoteException;

    void removeListener(ILowpanInterfaceListener iLowpanInterfaceListener) throws RemoteException;

    void removeOnMeshPrefix(IpPrefix ipPrefix) throws RemoteException;

    void reset() throws RemoteException;

    void sendToCommissioner(byte[] bArr) throws RemoteException;

    void setEnabled(boolean z) throws RemoteException;

    void startCommissioningSession(LowpanBeaconInfo lowpanBeaconInfo) throws RemoteException;

    void startEnergyScan(Map map, ILowpanEnergyScanCallback iLowpanEnergyScanCallback) throws RemoteException;

    void startNetScan(Map map, ILowpanNetScanCallback iLowpanNetScanCallback) throws RemoteException;

    void stopEnergyScan() throws RemoteException;

    void stopNetScan() throws RemoteException;
}
