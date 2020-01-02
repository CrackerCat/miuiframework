package android.speech;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IRecognitionService extends IInterface {

    public static class Default implements IRecognitionService {
        public void startListening(Intent recognizerIntent, IRecognitionListener listener) throws RemoteException {
        }

        public void stopListening(IRecognitionListener listener) throws RemoteException {
        }

        public void cancel(IRecognitionListener listener) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IRecognitionService {
        private static final String DESCRIPTOR = "android.speech.IRecognitionService";
        static final int TRANSACTION_cancel = 3;
        static final int TRANSACTION_startListening = 1;
        static final int TRANSACTION_stopListening = 2;

        private static class Proxy implements IRecognitionService {
            public static IRecognitionService sDefaultImpl;
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

            public void startListening(Intent recognizerIntent, IRecognitionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (recognizerIntent != null) {
                        _data.writeInt(1);
                        recognizerIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().startListening(recognizerIntent, listener);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void stopListening(IRecognitionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().stopListening(listener);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void cancel(IRecognitionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().cancel(listener);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRecognitionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IRecognitionService)) {
                return new Proxy(obj);
            }
            return (IRecognitionService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "startListening";
            }
            if (transactionCode == 2) {
                return "stopListening";
            }
            if (transactionCode != 3) {
                return null;
            }
            return "cancel";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code == 1) {
                Intent _arg0;
                data.enforceInterface(descriptor);
                if (data.readInt() != 0) {
                    _arg0 = (Intent) Intent.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                startListening(_arg0, android.speech.IRecognitionListener.Stub.asInterface(data.readStrongBinder()));
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                stopListening(android.speech.IRecognitionListener.Stub.asInterface(data.readStrongBinder()));
                return true;
            } else if (code == 3) {
                data.enforceInterface(descriptor);
                cancel(android.speech.IRecognitionListener.Stub.asInterface(data.readStrongBinder()));
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IRecognitionService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IRecognitionService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void cancel(IRecognitionListener iRecognitionListener) throws RemoteException;

    void startListening(Intent intent, IRecognitionListener iRecognitionListener) throws RemoteException;

    void stopListening(IRecognitionListener iRecognitionListener) throws RemoteException;
}
