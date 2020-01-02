package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

public interface IIpSecService extends IInterface {

    public static class Default implements IIpSecService {
        public IpSecSpiResponse allocateSecurityParameterIndex(String destinationAddress, int requestedSpi, IBinder binder) throws RemoteException {
            return null;
        }

        public void releaseSecurityParameterIndex(int resourceId) throws RemoteException {
        }

        public IpSecUdpEncapResponse openUdpEncapsulationSocket(int port, IBinder binder) throws RemoteException {
            return null;
        }

        public void closeUdpEncapsulationSocket(int resourceId) throws RemoteException {
        }

        public IpSecTunnelInterfaceResponse createTunnelInterface(String localAddr, String remoteAddr, Network underlyingNetwork, IBinder binder, String callingPackage) throws RemoteException {
            return null;
        }

        public void addAddressToTunnelInterface(int tunnelResourceId, LinkAddress localAddr, String callingPackage) throws RemoteException {
        }

        public void removeAddressFromTunnelInterface(int tunnelResourceId, LinkAddress localAddr, String callingPackage) throws RemoteException {
        }

        public void deleteTunnelInterface(int resourceId, String callingPackage) throws RemoteException {
        }

        public IpSecTransformResponse createTransform(IpSecConfig c, IBinder binder, String callingPackage) throws RemoteException {
            return null;
        }

        public void deleteTransform(int transformId) throws RemoteException {
        }

        public void applyTransportModeTransform(ParcelFileDescriptor socket, int direction, int transformId) throws RemoteException {
        }

        public void applyTunnelModeTransform(int tunnelResourceId, int direction, int transformResourceId, String callingPackage) throws RemoteException {
        }

        public void removeTransportModeTransforms(ParcelFileDescriptor socket) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IIpSecService {
        private static final String DESCRIPTOR = "android.net.IIpSecService";
        static final int TRANSACTION_addAddressToTunnelInterface = 6;
        static final int TRANSACTION_allocateSecurityParameterIndex = 1;
        static final int TRANSACTION_applyTransportModeTransform = 11;
        static final int TRANSACTION_applyTunnelModeTransform = 12;
        static final int TRANSACTION_closeUdpEncapsulationSocket = 4;
        static final int TRANSACTION_createTransform = 9;
        static final int TRANSACTION_createTunnelInterface = 5;
        static final int TRANSACTION_deleteTransform = 10;
        static final int TRANSACTION_deleteTunnelInterface = 8;
        static final int TRANSACTION_openUdpEncapsulationSocket = 3;
        static final int TRANSACTION_releaseSecurityParameterIndex = 2;
        static final int TRANSACTION_removeAddressFromTunnelInterface = 7;
        static final int TRANSACTION_removeTransportModeTransforms = 13;

        private static class Proxy implements IIpSecService {
            public static IIpSecService sDefaultImpl;
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

            public IpSecSpiResponse allocateSecurityParameterIndex(String destinationAddress, int requestedSpi, IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(destinationAddress);
                    _data.writeInt(requestedSpi);
                    _data.writeStrongBinder(binder);
                    IpSecSpiResponse ipSecSpiResponse = true;
                    if (!this.mRemote.transact(1, _data, _reply, 0)) {
                        ipSecSpiResponse = Stub.getDefaultImpl();
                        if (ipSecSpiResponse != 0) {
                            ipSecSpiResponse = Stub.getDefaultImpl().allocateSecurityParameterIndex(destinationAddress, requestedSpi, binder);
                            return ipSecSpiResponse;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        ipSecSpiResponse = (IpSecSpiResponse) IpSecSpiResponse.CREATOR.createFromParcel(_reply);
                    } else {
                        ipSecSpiResponse = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return ipSecSpiResponse;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void releaseSecurityParameterIndex(int resourceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(resourceId);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().releaseSecurityParameterIndex(resourceId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public IpSecUdpEncapResponse openUdpEncapsulationSocket(int port, IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(port);
                    _data.writeStrongBinder(binder);
                    IpSecUdpEncapResponse ipSecUdpEncapResponse = 3;
                    if (!this.mRemote.transact(3, _data, _reply, 0)) {
                        ipSecUdpEncapResponse = Stub.getDefaultImpl();
                        if (ipSecUdpEncapResponse != 0) {
                            ipSecUdpEncapResponse = Stub.getDefaultImpl().openUdpEncapsulationSocket(port, binder);
                            return ipSecUdpEncapResponse;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        ipSecUdpEncapResponse = (IpSecUdpEncapResponse) IpSecUdpEncapResponse.CREATOR.createFromParcel(_reply);
                    } else {
                        ipSecUdpEncapResponse = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return ipSecUdpEncapResponse;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void closeUdpEncapsulationSocket(int resourceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(resourceId);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().closeUdpEncapsulationSocket(resourceId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public IpSecTunnelInterfaceResponse createTunnelInterface(String localAddr, String remoteAddr, Network underlyingNetwork, IBinder binder, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(localAddr);
                    _data.writeString(remoteAddr);
                    if (underlyingNetwork != null) {
                        _data.writeInt(1);
                        underlyingNetwork.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(binder);
                    _data.writeString(callingPackage);
                    IpSecTunnelInterfaceResponse ipSecTunnelInterfaceResponse = this.mRemote;
                    if (!ipSecTunnelInterfaceResponse.transact(5, _data, _reply, 0)) {
                        ipSecTunnelInterfaceResponse = Stub.getDefaultImpl();
                        if (ipSecTunnelInterfaceResponse != null) {
                            ipSecTunnelInterfaceResponse = Stub.getDefaultImpl().createTunnelInterface(localAddr, remoteAddr, underlyingNetwork, binder, callingPackage);
                            return ipSecTunnelInterfaceResponse;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        ipSecTunnelInterfaceResponse = (IpSecTunnelInterfaceResponse) IpSecTunnelInterfaceResponse.CREATOR.createFromParcel(_reply);
                    } else {
                        ipSecTunnelInterfaceResponse = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return ipSecTunnelInterfaceResponse;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addAddressToTunnelInterface(int tunnelResourceId, LinkAddress localAddr, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(tunnelResourceId);
                    if (localAddr != null) {
                        _data.writeInt(1);
                        localAddr.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(callingPackage);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().addAddressToTunnelInterface(tunnelResourceId, localAddr, callingPackage);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeAddressFromTunnelInterface(int tunnelResourceId, LinkAddress localAddr, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(tunnelResourceId);
                    if (localAddr != null) {
                        _data.writeInt(1);
                        localAddr.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(callingPackage);
                    if (this.mRemote.transact(7, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().removeAddressFromTunnelInterface(tunnelResourceId, localAddr, callingPackage);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void deleteTunnelInterface(int resourceId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(resourceId);
                    _data.writeString(callingPackage);
                    if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().deleteTunnelInterface(resourceId, callingPackage);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public IpSecTransformResponse createTransform(IpSecConfig c, IBinder binder, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (c != null) {
                        _data.writeInt(1);
                        c.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(binder);
                    _data.writeString(callingPackage);
                    IpSecTransformResponse ipSecTransformResponse = this.mRemote;
                    if (!ipSecTransformResponse.transact(9, _data, _reply, 0)) {
                        ipSecTransformResponse = Stub.getDefaultImpl();
                        if (ipSecTransformResponse != null) {
                            ipSecTransformResponse = Stub.getDefaultImpl().createTransform(c, binder, callingPackage);
                            return ipSecTransformResponse;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        ipSecTransformResponse = (IpSecTransformResponse) IpSecTransformResponse.CREATOR.createFromParcel(_reply);
                    } else {
                        ipSecTransformResponse = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return ipSecTransformResponse;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void deleteTransform(int transformId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(transformId);
                    if (this.mRemote.transact(10, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().deleteTransform(transformId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void applyTransportModeTransform(ParcelFileDescriptor socket, int direction, int transformId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (socket != null) {
                        _data.writeInt(1);
                        socket.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(direction);
                    _data.writeInt(transformId);
                    if (this.mRemote.transact(11, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().applyTransportModeTransform(socket, direction, transformId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void applyTunnelModeTransform(int tunnelResourceId, int direction, int transformResourceId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(tunnelResourceId);
                    _data.writeInt(direction);
                    _data.writeInt(transformResourceId);
                    _data.writeString(callingPackage);
                    if (this.mRemote.transact(12, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().applyTunnelModeTransform(tunnelResourceId, direction, transformResourceId, callingPackage);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeTransportModeTransforms(ParcelFileDescriptor socket) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (socket != null) {
                        _data.writeInt(1);
                        socket.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(13, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().removeTransportModeTransforms(socket);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IIpSecService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IIpSecService)) {
                return new Proxy(obj);
            }
            return (IIpSecService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "allocateSecurityParameterIndex";
                case 2:
                    return "releaseSecurityParameterIndex";
                case 3:
                    return "openUdpEncapsulationSocket";
                case 4:
                    return "closeUdpEncapsulationSocket";
                case 5:
                    return "createTunnelInterface";
                case 6:
                    return "addAddressToTunnelInterface";
                case 7:
                    return "removeAddressFromTunnelInterface";
                case 8:
                    return "deleteTunnelInterface";
                case 9:
                    return "createTransform";
                case 10:
                    return "deleteTransform";
                case 11:
                    return "applyTransportModeTransform";
                case 12:
                    return "applyTunnelModeTransform";
                case 13:
                    return "removeTransportModeTransforms";
                default:
                    return null;
            }
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = code;
            Parcel parcel = data;
            Parcel parcel2 = reply;
            String descriptor = DESCRIPTOR;
            if (i != 1598968902) {
                int _arg0;
                LinkAddress _arg1;
                ParcelFileDescriptor _arg02;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(descriptor);
                        IpSecSpiResponse _result = allocateSecurityParameterIndex(data.readString(), data.readInt(), data.readStrongBinder());
                        reply.writeNoException();
                        if (_result != null) {
                            parcel2.writeInt(1);
                            _result.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 2:
                        parcel.enforceInterface(descriptor);
                        releaseSecurityParameterIndex(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 3:
                        parcel.enforceInterface(descriptor);
                        IpSecUdpEncapResponse _result2 = openUdpEncapsulationSocket(data.readInt(), data.readStrongBinder());
                        reply.writeNoException();
                        if (_result2 != null) {
                            parcel2.writeInt(1);
                            _result2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 4:
                        parcel.enforceInterface(descriptor);
                        closeUdpEncapsulationSocket(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 5:
                        Network _arg2;
                        parcel.enforceInterface(descriptor);
                        String _arg03 = data.readString();
                        String _arg12 = data.readString();
                        if (data.readInt() != 0) {
                            _arg2 = (Network) Network.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg2 = null;
                        }
                        IpSecTunnelInterfaceResponse _result3 = createTunnelInterface(_arg03, _arg12, _arg2, data.readStrongBinder(), data.readString());
                        reply.writeNoException();
                        if (_result3 != null) {
                            parcel2.writeInt(1);
                            _result3.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 6:
                        parcel.enforceInterface(descriptor);
                        _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = (LinkAddress) LinkAddress.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg1 = null;
                        }
                        addAddressToTunnelInterface(_arg0, _arg1, data.readString());
                        reply.writeNoException();
                        return true;
                    case 7:
                        parcel.enforceInterface(descriptor);
                        _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = (LinkAddress) LinkAddress.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg1 = null;
                        }
                        removeAddressFromTunnelInterface(_arg0, _arg1, data.readString());
                        reply.writeNoException();
                        return true;
                    case 8:
                        parcel.enforceInterface(descriptor);
                        deleteTunnelInterface(data.readInt(), data.readString());
                        reply.writeNoException();
                        return true;
                    case 9:
                        IpSecConfig _arg04;
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg04 = (IpSecConfig) IpSecConfig.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg04 = null;
                        }
                        IpSecTransformResponse _result4 = createTransform(_arg04, data.readStrongBinder(), data.readString());
                        reply.writeNoException();
                        if (_result4 != null) {
                            parcel2.writeInt(1);
                            _result4.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 10:
                        parcel.enforceInterface(descriptor);
                        deleteTransform(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 11:
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg02 = null;
                        }
                        applyTransportModeTransform(_arg02, data.readInt(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 12:
                        parcel.enforceInterface(descriptor);
                        applyTunnelModeTransform(data.readInt(), data.readInt(), data.readInt(), data.readString());
                        reply.writeNoException();
                        return true;
                    case 13:
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg02 = null;
                        }
                        removeTransportModeTransforms(_arg02);
                        reply.writeNoException();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            parcel2.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IIpSecService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IIpSecService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void addAddressToTunnelInterface(int i, LinkAddress linkAddress, String str) throws RemoteException;

    IpSecSpiResponse allocateSecurityParameterIndex(String str, int i, IBinder iBinder) throws RemoteException;

    void applyTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor, int i, int i2) throws RemoteException;

    void applyTunnelModeTransform(int i, int i2, int i3, String str) throws RemoteException;

    void closeUdpEncapsulationSocket(int i) throws RemoteException;

    IpSecTransformResponse createTransform(IpSecConfig ipSecConfig, IBinder iBinder, String str) throws RemoteException;

    IpSecTunnelInterfaceResponse createTunnelInterface(String str, String str2, Network network, IBinder iBinder, String str3) throws RemoteException;

    void deleteTransform(int i) throws RemoteException;

    void deleteTunnelInterface(int i, String str) throws RemoteException;

    IpSecUdpEncapResponse openUdpEncapsulationSocket(int i, IBinder iBinder) throws RemoteException;

    void releaseSecurityParameterIndex(int i) throws RemoteException;

    void removeAddressFromTunnelInterface(int i, LinkAddress linkAddress, String str) throws RemoteException;

    void removeTransportModeTransforms(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;
}
