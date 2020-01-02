package com.android.ims.internal.uce.options;

import android.annotation.UnsupportedAppUsage;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.ims.internal.uce.common.StatusCode;

public interface IOptionsListener extends IInterface {

    public static class Default implements IOptionsListener {
        public void getVersionCb(String version) throws RemoteException {
        }

        public void serviceAvailable(StatusCode statusCode) throws RemoteException {
        }

        public void serviceUnavailable(StatusCode statusCode) throws RemoteException {
        }

        public void sipResponseReceived(String uri, OptionsSipResponse sipResponse, OptionsCapInfo capInfo) throws RemoteException {
        }

        public void cmdStatus(OptionsCmdStatus cmdStatus) throws RemoteException {
        }

        public void incomingOptions(String uri, OptionsCapInfo capInfo, int tID) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IOptionsListener {
        private static final String DESCRIPTOR = "com.android.ims.internal.uce.options.IOptionsListener";
        static final int TRANSACTION_cmdStatus = 5;
        static final int TRANSACTION_getVersionCb = 1;
        static final int TRANSACTION_incomingOptions = 6;
        static final int TRANSACTION_serviceAvailable = 2;
        static final int TRANSACTION_serviceUnavailable = 3;
        static final int TRANSACTION_sipResponseReceived = 4;

        private static class Proxy implements IOptionsListener {
            public static IOptionsListener sDefaultImpl;
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

            public void getVersionCb(String version) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(version);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().getVersionCb(version);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void serviceAvailable(StatusCode statusCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (statusCode != null) {
                        _data.writeInt(1);
                        statusCode.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().serviceAvailable(statusCode);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void serviceUnavailable(StatusCode statusCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (statusCode != null) {
                        _data.writeInt(1);
                        statusCode.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().serviceUnavailable(statusCode);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sipResponseReceived(String uri, OptionsSipResponse sipResponse, OptionsCapInfo capInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    if (sipResponse != null) {
                        _data.writeInt(1);
                        sipResponse.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (capInfo != null) {
                        _data.writeInt(1);
                        capInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sipResponseReceived(uri, sipResponse, capInfo);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void cmdStatus(OptionsCmdStatus cmdStatus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (cmdStatus != null) {
                        _data.writeInt(1);
                        cmdStatus.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().cmdStatus(cmdStatus);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void incomingOptions(String uri, OptionsCapInfo capInfo, int tID) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    if (capInfo != null) {
                        _data.writeInt(1);
                        capInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(tID);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().incomingOptions(uri, capInfo, tID);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOptionsListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IOptionsListener)) {
                return new Proxy(obj);
            }
            return (IOptionsListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getVersionCb";
                case 2:
                    return "serviceAvailable";
                case 3:
                    return "serviceUnavailable";
                case 4:
                    return "sipResponseReceived";
                case 5:
                    return "cmdStatus";
                case 6:
                    return "incomingOptions";
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
                StatusCode _arg0;
                String _arg02;
                switch (code) {
                    case 1:
                        data.enforceInterface(descriptor);
                        getVersionCb(data.readString());
                        reply.writeNoException();
                        return true;
                    case 2:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = (StatusCode) StatusCode.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        serviceAvailable(_arg0);
                        reply.writeNoException();
                        return true;
                    case 3:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = (StatusCode) StatusCode.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        serviceUnavailable(_arg0);
                        reply.writeNoException();
                        return true;
                    case 4:
                        OptionsSipResponse _arg1;
                        OptionsCapInfo _arg2;
                        data.enforceInterface(descriptor);
                        _arg02 = data.readString();
                        if (data.readInt() != 0) {
                            _arg1 = (OptionsSipResponse) OptionsSipResponse.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        if (data.readInt() != 0) {
                            _arg2 = (OptionsCapInfo) OptionsCapInfo.CREATOR.createFromParcel(data);
                        } else {
                            _arg2 = null;
                        }
                        sipResponseReceived(_arg02, _arg1, _arg2);
                        reply.writeNoException();
                        return true;
                    case 5:
                        OptionsCmdStatus _arg03;
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg03 = (OptionsCmdStatus) OptionsCmdStatus.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        cmdStatus(_arg03);
                        reply.writeNoException();
                        return true;
                    case 6:
                        OptionsCapInfo _arg12;
                        data.enforceInterface(descriptor);
                        _arg02 = data.readString();
                        if (data.readInt() != 0) {
                            _arg12 = (OptionsCapInfo) OptionsCapInfo.CREATOR.createFromParcel(data);
                        } else {
                            _arg12 = null;
                        }
                        incomingOptions(_arg02, _arg12, data.readInt());
                        reply.writeNoException();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IOptionsListener impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IOptionsListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    @UnsupportedAppUsage
    void cmdStatus(OptionsCmdStatus optionsCmdStatus) throws RemoteException;

    @UnsupportedAppUsage
    void getVersionCb(String str) throws RemoteException;

    @UnsupportedAppUsage
    void incomingOptions(String str, OptionsCapInfo optionsCapInfo, int i) throws RemoteException;

    @UnsupportedAppUsage
    void serviceAvailable(StatusCode statusCode) throws RemoteException;

    @UnsupportedAppUsage
    void serviceUnavailable(StatusCode statusCode) throws RemoteException;

    @UnsupportedAppUsage
    void sipResponseReceived(String str, OptionsSipResponse optionsSipResponse, OptionsCapInfo optionsCapInfo) throws RemoteException;
}
