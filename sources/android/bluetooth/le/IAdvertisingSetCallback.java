package android.bluetooth.le;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAdvertisingSetCallback extends IInterface {

    public static abstract class Stub extends Binder implements IAdvertisingSetCallback {
        private static final String DESCRIPTOR = "android.bluetooth.le.IAdvertisingSetCallback";
        static final int TRANSACTION_onAdvertisingDataSet = 5;
        static final int TRANSACTION_onAdvertisingEnabled = 4;
        static final int TRANSACTION_onAdvertisingParametersUpdated = 7;
        static final int TRANSACTION_onAdvertisingSetStarted = 1;
        static final int TRANSACTION_onAdvertisingSetStopped = 3;
        static final int TRANSACTION_onOwnAddressRead = 2;
        static final int TRANSACTION_onPeriodicAdvertisingDataSet = 9;
        static final int TRANSACTION_onPeriodicAdvertisingEnabled = 10;
        static final int TRANSACTION_onPeriodicAdvertisingParametersUpdated = 8;
        static final int TRANSACTION_onScanResponseDataSet = 6;

        private static class Proxy implements IAdvertisingSetCallback {
            public static IAdvertisingSetCallback sDefaultImpl;
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

            public void onAdvertisingSetStarted(int advertiserId, int tx_power, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(tx_power);
                    _data.writeInt(status);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onAdvertisingSetStarted(advertiserId, tx_power, status);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onOwnAddressRead(int advertiserId, int addressType, String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(addressType);
                    _data.writeString(address);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onOwnAddressRead(advertiserId, addressType, address);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onAdvertisingSetStopped(int advertiserId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onAdvertisingSetStopped(advertiserId);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onAdvertisingEnabled(int advertiserId, boolean enable, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(enable ? 1 : 0);
                    _data.writeInt(status);
                    if (this.mRemote.transact(4, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onAdvertisingEnabled(advertiserId, enable, status);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onAdvertisingDataSet(int advertiserId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(status);
                    if (this.mRemote.transact(5, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onAdvertisingDataSet(advertiserId, status);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onScanResponseDataSet(int advertiserId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(status);
                    if (this.mRemote.transact(6, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onScanResponseDataSet(advertiserId, status);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onAdvertisingParametersUpdated(int advertiserId, int tx_power, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(tx_power);
                    _data.writeInt(status);
                    if (this.mRemote.transact(7, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onAdvertisingParametersUpdated(advertiserId, tx_power, status);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onPeriodicAdvertisingParametersUpdated(int advertiserId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(status);
                    if (this.mRemote.transact(8, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onPeriodicAdvertisingParametersUpdated(advertiserId, status);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onPeriodicAdvertisingDataSet(int advertiserId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(status);
                    if (this.mRemote.transact(9, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onPeriodicAdvertisingDataSet(advertiserId, status);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onPeriodicAdvertisingEnabled(int advertiserId, boolean enable, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(advertiserId);
                    _data.writeInt(enable ? 1 : 0);
                    _data.writeInt(status);
                    if (this.mRemote.transact(10, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onPeriodicAdvertisingEnabled(advertiserId, enable, status);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAdvertisingSetCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IAdvertisingSetCallback)) {
                return new Proxy(obj);
            }
            return (IAdvertisingSetCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "onAdvertisingSetStarted";
                case 2:
                    return "onOwnAddressRead";
                case 3:
                    return "onAdvertisingSetStopped";
                case 4:
                    return "onAdvertisingEnabled";
                case 5:
                    return "onAdvertisingDataSet";
                case 6:
                    return "onScanResponseDataSet";
                case 7:
                    return "onAdvertisingParametersUpdated";
                case 8:
                    return "onPeriodicAdvertisingParametersUpdated";
                case 9:
                    return "onPeriodicAdvertisingDataSet";
                case 10:
                    return "onPeriodicAdvertisingEnabled";
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
                boolean _arg1 = false;
                int _arg0;
                switch (code) {
                    case 1:
                        data.enforceInterface(descriptor);
                        onAdvertisingSetStarted(data.readInt(), data.readInt(), data.readInt());
                        return true;
                    case 2:
                        data.enforceInterface(descriptor);
                        onOwnAddressRead(data.readInt(), data.readInt(), data.readString());
                        return true;
                    case 3:
                        data.enforceInterface(descriptor);
                        onAdvertisingSetStopped(data.readInt());
                        return true;
                    case 4:
                        data.enforceInterface(descriptor);
                        _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = true;
                        }
                        onAdvertisingEnabled(_arg0, _arg1, data.readInt());
                        return true;
                    case 5:
                        data.enforceInterface(descriptor);
                        onAdvertisingDataSet(data.readInt(), data.readInt());
                        return true;
                    case 6:
                        data.enforceInterface(descriptor);
                        onScanResponseDataSet(data.readInt(), data.readInt());
                        return true;
                    case 7:
                        data.enforceInterface(descriptor);
                        onAdvertisingParametersUpdated(data.readInt(), data.readInt(), data.readInt());
                        return true;
                    case 8:
                        data.enforceInterface(descriptor);
                        onPeriodicAdvertisingParametersUpdated(data.readInt(), data.readInt());
                        return true;
                    case 9:
                        data.enforceInterface(descriptor);
                        onPeriodicAdvertisingDataSet(data.readInt(), data.readInt());
                        return true;
                    case 10:
                        data.enforceInterface(descriptor);
                        _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = true;
                        }
                        onPeriodicAdvertisingEnabled(_arg0, _arg1, data.readInt());
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IAdvertisingSetCallback impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IAdvertisingSetCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    public static class Default implements IAdvertisingSetCallback {
        public void onAdvertisingSetStarted(int advertiserId, int tx_power, int status) throws RemoteException {
        }

        public void onOwnAddressRead(int advertiserId, int addressType, String address) throws RemoteException {
        }

        public void onAdvertisingSetStopped(int advertiserId) throws RemoteException {
        }

        public void onAdvertisingEnabled(int advertiserId, boolean enable, int status) throws RemoteException {
        }

        public void onAdvertisingDataSet(int advertiserId, int status) throws RemoteException {
        }

        public void onScanResponseDataSet(int advertiserId, int status) throws RemoteException {
        }

        public void onAdvertisingParametersUpdated(int advertiserId, int tx_power, int status) throws RemoteException {
        }

        public void onPeriodicAdvertisingParametersUpdated(int advertiserId, int status) throws RemoteException {
        }

        public void onPeriodicAdvertisingDataSet(int advertiserId, int status) throws RemoteException {
        }

        public void onPeriodicAdvertisingEnabled(int advertiserId, boolean enable, int status) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    void onAdvertisingDataSet(int i, int i2) throws RemoteException;

    void onAdvertisingEnabled(int i, boolean z, int i2) throws RemoteException;

    void onAdvertisingParametersUpdated(int i, int i2, int i3) throws RemoteException;

    void onAdvertisingSetStarted(int i, int i2, int i3) throws RemoteException;

    void onAdvertisingSetStopped(int i) throws RemoteException;

    void onOwnAddressRead(int i, int i2, String str) throws RemoteException;

    void onPeriodicAdvertisingDataSet(int i, int i2) throws RemoteException;

    void onPeriodicAdvertisingEnabled(int i, boolean z, int i2) throws RemoteException;

    void onPeriodicAdvertisingParametersUpdated(int i, int i2) throws RemoteException;

    void onScanResponseDataSet(int i, int i2) throws RemoteException;
}
