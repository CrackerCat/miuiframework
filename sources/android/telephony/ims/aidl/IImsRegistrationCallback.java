package android.telephony.ims.aidl;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.telephony.ims.ImsReasonInfo;

public interface IImsRegistrationCallback extends IInterface {

    public static abstract class Stub extends Binder implements IImsRegistrationCallback {
        private static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsRegistrationCallback";
        static final int TRANSACTION_onDeregistered = 3;
        static final int TRANSACTION_onRegistered = 1;
        static final int TRANSACTION_onRegistering = 2;
        static final int TRANSACTION_onSubscriberAssociatedUriChanged = 5;
        static final int TRANSACTION_onTechnologyChangeFailed = 4;

        private static class Proxy implements IImsRegistrationCallback {
            public static IImsRegistrationCallback sDefaultImpl;
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

            public void onRegistered(int imsRadioTech) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(imsRadioTech);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onRegistered(imsRadioTech);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onRegistering(int imsRadioTech) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(imsRadioTech);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onRegistering(imsRadioTech);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onDeregistered(ImsReasonInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onDeregistered(info);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onTechnologyChangeFailed(int imsRadioTech, ImsReasonInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(imsRadioTech);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(4, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onTechnologyChangeFailed(imsRadioTech, info);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onSubscriberAssociatedUriChanged(Uri[] uris) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(uris, 0);
                    if (this.mRemote.transact(5, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onSubscriberAssociatedUriChanged(uris);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsRegistrationCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IImsRegistrationCallback)) {
                return new Proxy(obj);
            }
            return (IImsRegistrationCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "onRegistered";
            }
            if (transactionCode == 2) {
                return "onRegistering";
            }
            if (transactionCode == 3) {
                return "onDeregistered";
            }
            if (transactionCode == 4) {
                return "onTechnologyChangeFailed";
            }
            if (transactionCode != 5) {
                return null;
            }
            return "onSubscriberAssociatedUriChanged";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code == 1) {
                data.enforceInterface(descriptor);
                onRegistered(data.readInt());
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                onRegistering(data.readInt());
                return true;
            } else if (code == 3) {
                ImsReasonInfo _arg0;
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = (ImsReasonInfo) ImsReasonInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                onDeregistered(_arg0);
                return true;
            } else if (code == 4) {
                ImsReasonInfo _arg1;
                data.enforceInterface(descriptor);
                int _arg02 = data.readInt();
                if (data.readInt() != 0) {
                    _arg1 = (ImsReasonInfo) ImsReasonInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg1 = null;
                }
                onTechnologyChangeFailed(_arg02, _arg1);
                return true;
            } else if (code == 5) {
                data.enforceInterface(descriptor);
                onSubscriberAssociatedUriChanged((Uri[]) data.createTypedArray(Uri.CREATOR));
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IImsRegistrationCallback impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IImsRegistrationCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    public static class Default implements IImsRegistrationCallback {
        public void onRegistered(int imsRadioTech) throws RemoteException {
        }

        public void onRegistering(int imsRadioTech) throws RemoteException {
        }

        public void onDeregistered(ImsReasonInfo info) throws RemoteException {
        }

        public void onTechnologyChangeFailed(int imsRadioTech, ImsReasonInfo info) throws RemoteException {
        }

        public void onSubscriberAssociatedUriChanged(Uri[] uris) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    void onDeregistered(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void onRegistered(int i) throws RemoteException;

    void onRegistering(int i) throws RemoteException;

    void onSubscriberAssociatedUriChanged(Uri[] uriArr) throws RemoteException;

    void onTechnologyChangeFailed(int i, ImsReasonInfo imsReasonInfo) throws RemoteException;
}
