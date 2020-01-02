package android.hardware.hdmi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IHdmiVendorCommandListener extends IInterface {

    public static abstract class Stub extends Binder implements IHdmiVendorCommandListener {
        private static final String DESCRIPTOR = "android.hardware.hdmi.IHdmiVendorCommandListener";
        static final int TRANSACTION_onControlStateChanged = 2;
        static final int TRANSACTION_onReceived = 1;

        private static class Proxy implements IHdmiVendorCommandListener {
            public static IHdmiVendorCommandListener sDefaultImpl;
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

            public void onReceived(int logicalAddress, int destAddress, byte[] operands, boolean hasVendorId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(logicalAddress);
                    _data.writeInt(destAddress);
                    _data.writeByteArray(operands);
                    _data.writeInt(hasVendorId ? 1 : 0);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onReceived(logicalAddress, destAddress, operands, hasVendorId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onControlStateChanged(boolean enabled, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    _data.writeInt(reason);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onControlStateChanged(enabled, reason);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IHdmiVendorCommandListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IHdmiVendorCommandListener)) {
                return new Proxy(obj);
            }
            return (IHdmiVendorCommandListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "onReceived";
            }
            if (transactionCode != 2) {
                return null;
            }
            return "onControlStateChanged";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            boolean _arg0 = false;
            if (code == 1) {
                data.enforceInterface(descriptor);
                int _arg02 = data.readInt();
                int _arg1 = data.readInt();
                byte[] _arg2 = data.createByteArray();
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onReceived(_arg02, _arg1, _arg2, _arg0);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onControlStateChanged(_arg0, data.readInt());
                reply.writeNoException();
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IHdmiVendorCommandListener impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IHdmiVendorCommandListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    public static class Default implements IHdmiVendorCommandListener {
        public void onReceived(int logicalAddress, int destAddress, byte[] operands, boolean hasVendorId) throws RemoteException {
        }

        public void onControlStateChanged(boolean enabled, int reason) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    void onControlStateChanged(boolean z, int i) throws RemoteException;

    void onReceived(int i, int i2, byte[] bArr, boolean z) throws RemoteException;
}
