package android.net.wifi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface INetworkRequestUserSelectionCallback extends IInterface {

    public static class Default implements INetworkRequestUserSelectionCallback {
        public void select(WifiConfiguration wificonfiguration) throws RemoteException {
        }

        public void reject() throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements INetworkRequestUserSelectionCallback {
        private static final String DESCRIPTOR = "android.net.wifi.INetworkRequestUserSelectionCallback";
        static final int TRANSACTION_reject = 2;
        static final int TRANSACTION_select = 1;

        private static class Proxy implements INetworkRequestUserSelectionCallback {
            public static INetworkRequestUserSelectionCallback sDefaultImpl;
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

            public void select(WifiConfiguration wificonfiguration) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (wificonfiguration != null) {
                        _data.writeInt(1);
                        wificonfiguration.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().select(wificonfiguration);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void reject() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().reject();
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkRequestUserSelectionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof INetworkRequestUserSelectionCallback)) {
                return new Proxy(obj);
            }
            return (INetworkRequestUserSelectionCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "select";
            }
            if (transactionCode != 2) {
                return null;
            }
            return "reject";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code == 1) {
                WifiConfiguration _arg0;
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = (WifiConfiguration) WifiConfiguration.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                select(_arg0);
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                reject();
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(INetworkRequestUserSelectionCallback impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static INetworkRequestUserSelectionCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void reject() throws RemoteException;

    void select(WifiConfiguration wifiConfiguration) throws RemoteException;
}
