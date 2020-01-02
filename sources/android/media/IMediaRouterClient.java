package android.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMediaRouterClient extends IInterface {

    public static class Default implements IMediaRouterClient {
        public void onStateChanged() throws RemoteException {
        }

        public void onRestoreRoute() throws RemoteException {
        }

        public void onSelectedRouteChanged(String routeId) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMediaRouterClient {
        private static final String DESCRIPTOR = "android.media.IMediaRouterClient";
        static final int TRANSACTION_onRestoreRoute = 2;
        static final int TRANSACTION_onSelectedRouteChanged = 3;
        static final int TRANSACTION_onStateChanged = 1;

        private static class Proxy implements IMediaRouterClient {
            public static IMediaRouterClient sDefaultImpl;
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

            public void onStateChanged() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onStateChanged();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onRestoreRoute() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onRestoreRoute();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onSelectedRouteChanged(String routeId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(routeId);
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onSelectedRouteChanged(routeId);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaRouterClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMediaRouterClient)) {
                return new Proxy(obj);
            }
            return (IMediaRouterClient) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "onStateChanged";
            }
            if (transactionCode == 2) {
                return "onRestoreRoute";
            }
            if (transactionCode != 3) {
                return null;
            }
            return "onSelectedRouteChanged";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code == 1) {
                data.enforceInterface(descriptor);
                onStateChanged();
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                onRestoreRoute();
                return true;
            } else if (code == 3) {
                data.enforceInterface(descriptor);
                onSelectedRouteChanged(data.readString());
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IMediaRouterClient impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMediaRouterClient getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void onRestoreRoute() throws RemoteException;

    void onSelectedRouteChanged(String str) throws RemoteException;

    void onStateChanged() throws RemoteException;
}
