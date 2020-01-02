package com.android.internal.policy;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IKeyguardStateCallback extends IInterface {

    public static class Default implements IKeyguardStateCallback {
        public void onShowingStateChanged(boolean showing) throws RemoteException {
        }

        public void onSimSecureStateChanged(boolean simSecure) throws RemoteException {
        }

        public void onInputRestrictedStateChanged(boolean inputRestricted) throws RemoteException {
        }

        public void onTrustedChanged(boolean trusted) throws RemoteException {
        }

        public void onHasLockscreenWallpaperChanged(boolean hasLockscreenWallpaper) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IKeyguardStateCallback {
        private static final String DESCRIPTOR = "com.android.internal.policy.IKeyguardStateCallback";
        static final int TRANSACTION_onHasLockscreenWallpaperChanged = 5;
        static final int TRANSACTION_onInputRestrictedStateChanged = 3;
        static final int TRANSACTION_onShowingStateChanged = 1;
        static final int TRANSACTION_onSimSecureStateChanged = 2;
        static final int TRANSACTION_onTrustedChanged = 4;

        private static class Proxy implements IKeyguardStateCallback {
            public static IKeyguardStateCallback sDefaultImpl;
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

            public void onShowingStateChanged(boolean showing) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showing ? 1 : 0);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onShowingStateChanged(showing);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onSimSecureStateChanged(boolean simSecure) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(simSecure ? 1 : 0);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onSimSecureStateChanged(simSecure);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onInputRestrictedStateChanged(boolean inputRestricted) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(inputRestricted ? 1 : 0);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onInputRestrictedStateChanged(inputRestricted);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onTrustedChanged(boolean trusted) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(trusted ? 1 : 0);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onTrustedChanged(trusted);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onHasLockscreenWallpaperChanged(boolean hasLockscreenWallpaper) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(hasLockscreenWallpaper ? 1 : 0);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onHasLockscreenWallpaperChanged(hasLockscreenWallpaper);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyguardStateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IKeyguardStateCallback)) {
                return new Proxy(obj);
            }
            return (IKeyguardStateCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "onShowingStateChanged";
            }
            if (transactionCode == 2) {
                return "onSimSecureStateChanged";
            }
            if (transactionCode == 3) {
                return "onInputRestrictedStateChanged";
            }
            if (transactionCode == 4) {
                return "onTrustedChanged";
            }
            if (transactionCode != 5) {
                return null;
            }
            return "onHasLockscreenWallpaperChanged";
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
                onShowingStateChanged(_arg0);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onSimSecureStateChanged(_arg0);
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onInputRestrictedStateChanged(_arg0);
                reply.writeNoException();
                return true;
            } else if (code == 4) {
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onTrustedChanged(_arg0);
                reply.writeNoException();
                return true;
            } else if (code == 5) {
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = true;
                }
                onHasLockscreenWallpaperChanged(_arg0);
                reply.writeNoException();
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IKeyguardStateCallback impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IKeyguardStateCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void onHasLockscreenWallpaperChanged(boolean z) throws RemoteException;

    void onInputRestrictedStateChanged(boolean z) throws RemoteException;

    void onShowingStateChanged(boolean z) throws RemoteException;

    void onSimSecureStateChanged(boolean z) throws RemoteException;

    void onTrustedChanged(boolean z) throws RemoteException;
}
