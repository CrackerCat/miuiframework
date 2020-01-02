package android.os;

public interface IUpdateEngineCallback extends IInterface {

    public static class Default implements IUpdateEngineCallback {
        public void onStatusUpdate(int status_code, float percentage) throws RemoteException {
        }

        public void onPayloadApplicationComplete(int error_code) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IUpdateEngineCallback {
        private static final String DESCRIPTOR = "android.os.IUpdateEngineCallback";
        static final int TRANSACTION_onPayloadApplicationComplete = 2;
        static final int TRANSACTION_onStatusUpdate = 1;

        private static class Proxy implements IUpdateEngineCallback {
            public static IUpdateEngineCallback sDefaultImpl;
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

            public void onStatusUpdate(int status_code, float percentage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status_code);
                    _data.writeFloat(percentage);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onStatusUpdate(status_code, percentage);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onPayloadApplicationComplete(int error_code) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(error_code);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onPayloadApplicationComplete(error_code);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUpdateEngineCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IUpdateEngineCallback)) {
                return new Proxy(obj);
            }
            return (IUpdateEngineCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "onStatusUpdate";
            }
            if (transactionCode != 2) {
                return null;
            }
            return "onPayloadApplicationComplete";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code == 1) {
                data.enforceInterface(descriptor);
                onStatusUpdate(data.readInt(), data.readFloat());
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                onPayloadApplicationComplete(data.readInt());
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IUpdateEngineCallback impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IUpdateEngineCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void onPayloadApplicationComplete(int i) throws RemoteException;

    void onStatusUpdate(int i, float f) throws RemoteException;
}
