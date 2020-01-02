package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IDockedStackListener extends IInterface {

    public static class Default implements IDockedStackListener {
        public void onDividerVisibilityChanged(boolean visible) throws RemoteException {
        }

        public void onDockedStackExistsChanged(boolean exists) throws RemoteException {
        }

        public void onDockedStackMinimizedChanged(boolean minimized, long animDuration, boolean isHomeStackResizable) throws RemoteException {
        }

        public void onAdjustedForImeChanged(boolean adjustedForIme, long animDuration) throws RemoteException {
        }

        public void onDockSideChanged(int newDockSide) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IDockedStackListener {
        private static final String DESCRIPTOR = "android.view.IDockedStackListener";
        static final int TRANSACTION_onAdjustedForImeChanged = 4;
        static final int TRANSACTION_onDividerVisibilityChanged = 1;
        static final int TRANSACTION_onDockSideChanged = 5;
        static final int TRANSACTION_onDockedStackExistsChanged = 2;
        static final int TRANSACTION_onDockedStackMinimizedChanged = 3;

        private static class Proxy implements IDockedStackListener {
            public static IDockedStackListener sDefaultImpl;
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

            public void onDividerVisibilityChanged(boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(visible ? 1 : 0);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onDividerVisibilityChanged(visible);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onDockedStackExistsChanged(boolean exists) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(exists ? 1 : 0);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onDockedStackExistsChanged(exists);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onDockedStackMinimizedChanged(boolean minimized, long animDuration, boolean isHomeStackResizable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i = 0;
                    _data.writeInt(minimized ? 1 : 0);
                    _data.writeLong(animDuration);
                    if (isHomeStackResizable) {
                        i = 1;
                    }
                    _data.writeInt(i);
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onDockedStackMinimizedChanged(minimized, animDuration, isHomeStackResizable);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onAdjustedForImeChanged(boolean adjustedForIme, long animDuration) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(adjustedForIme ? 1 : 0);
                    _data.writeLong(animDuration);
                    if (this.mRemote.transact(4, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onAdjustedForImeChanged(adjustedForIme, animDuration);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onDockSideChanged(int newDockSide) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(newDockSide);
                    if (this.mRemote.transact(5, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onDockSideChanged(newDockSide);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDockedStackListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IDockedStackListener)) {
                return new Proxy(obj);
            }
            return (IDockedStackListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "onDividerVisibilityChanged";
            }
            if (transactionCode == 2) {
                return "onDockedStackExistsChanged";
            }
            if (transactionCode == 3) {
                return "onDockedStackMinimizedChanged";
            }
            if (transactionCode == 4) {
                return "onAdjustedForImeChanged";
            }
            if (transactionCode != 5) {
                return null;
            }
            return "onDockSideChanged";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            boolean _arg0 = false;
            if (code == 1) {
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onDividerVisibilityChanged(_arg0);
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onDockedStackExistsChanged(_arg0);
                return true;
            } else if (code == 3) {
                data.enforceInterface(descriptor);
                boolean _arg02 = data.readInt() != 0;
                long _arg1 = data.readLong();
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onDockedStackMinimizedChanged(_arg02, _arg1, _arg0);
                return true;
            } else if (code == 4) {
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onAdjustedForImeChanged(_arg0, data.readLong());
                return true;
            } else if (code == 5) {
                data.enforceInterface(descriptor);
                onDockSideChanged(data.readInt());
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IDockedStackListener impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IDockedStackListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void onAdjustedForImeChanged(boolean z, long j) throws RemoteException;

    void onDividerVisibilityChanged(boolean z) throws RemoteException;

    void onDockSideChanged(int i) throws RemoteException;

    void onDockedStackExistsChanged(boolean z) throws RemoteException;

    void onDockedStackMinimizedChanged(boolean z, long j, boolean z2) throws RemoteException;
}
