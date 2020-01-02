package com.android.internal.telecom;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ICallScreeningAdapter extends IInterface {

    public static class Default implements ICallScreeningAdapter {
        public void allowCall(String callId) throws RemoteException {
        }

        public void silenceCall(String callId) throws RemoteException {
        }

        public void disallowCall(String callId, boolean shouldReject, boolean shouldAddToCallLog, boolean shouldShowNotification, ComponentName componentName) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements ICallScreeningAdapter {
        private static final String DESCRIPTOR = "com.android.internal.telecom.ICallScreeningAdapter";
        static final int TRANSACTION_allowCall = 1;
        static final int TRANSACTION_disallowCall = 3;
        static final int TRANSACTION_silenceCall = 2;

        private static class Proxy implements ICallScreeningAdapter {
            public static ICallScreeningAdapter sDefaultImpl;
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

            public void allowCall(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().allowCall(callId);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void silenceCall(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().silenceCall(callId);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void disallowCall(String callId, boolean shouldReject, boolean shouldAddToCallLog, boolean shouldShowNotification, ComponentName componentName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(shouldReject ? 1 : 0);
                    _data.writeInt(shouldAddToCallLog ? 1 : 0);
                    _data.writeInt(shouldShowNotification ? 1 : 0);
                    if (componentName != null) {
                        _data.writeInt(1);
                        componentName.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().disallowCall(callId, shouldReject, shouldAddToCallLog, shouldShowNotification, componentName);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICallScreeningAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ICallScreeningAdapter)) {
                return new Proxy(obj);
            }
            return (ICallScreeningAdapter) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "allowCall";
            }
            if (transactionCode == 2) {
                return "silenceCall";
            }
            if (transactionCode != 3) {
                return null;
            }
            return "disallowCall";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code == 1) {
                data.enforceInterface(descriptor);
                allowCall(data.readString());
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                silenceCall(data.readString());
                return true;
            } else if (code == 3) {
                ComponentName _arg4;
                data.enforceInterface(descriptor);
                String _arg0 = data.readString();
                boolean _arg1 = data.readInt() != 0;
                boolean _arg2 = data.readInt() != 0;
                boolean _arg3 = data.readInt() != 0;
                if (data.readInt() != 0) {
                    _arg4 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                } else {
                    _arg4 = null;
                }
                disallowCall(_arg0, _arg1, _arg2, _arg3, _arg4);
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(ICallScreeningAdapter impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static ICallScreeningAdapter getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void allowCall(String str) throws RemoteException;

    void disallowCall(String str, boolean z, boolean z2, boolean z3, ComponentName componentName) throws RemoteException;

    void silenceCall(String str) throws RemoteException;
}
