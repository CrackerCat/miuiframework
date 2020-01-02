package android.app.job;

import android.annotation.UnsupportedAppUsage;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IJobCallback extends IInterface {

    public static class Default implements IJobCallback {
        public void acknowledgeStartMessage(int jobId, boolean ongoing) throws RemoteException {
        }

        public void acknowledgeStopMessage(int jobId, boolean reschedule) throws RemoteException {
        }

        public JobWorkItem dequeueWork(int jobId) throws RemoteException {
            return null;
        }

        public boolean completeWork(int jobId, int workId) throws RemoteException {
            return false;
        }

        public void jobFinished(int jobId, boolean reschedule) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IJobCallback {
        private static final String DESCRIPTOR = "android.app.job.IJobCallback";
        static final int TRANSACTION_acknowledgeStartMessage = 1;
        static final int TRANSACTION_acknowledgeStopMessage = 2;
        static final int TRANSACTION_completeWork = 4;
        static final int TRANSACTION_dequeueWork = 3;
        static final int TRANSACTION_jobFinished = 5;

        private static class Proxy implements IJobCallback {
            public static IJobCallback sDefaultImpl;
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

            public void acknowledgeStartMessage(int jobId, boolean ongoing) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeInt(ongoing ? 1 : 0);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().acknowledgeStartMessage(jobId, ongoing);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void acknowledgeStopMessage(int jobId, boolean reschedule) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeInt(reschedule ? 1 : 0);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().acknowledgeStopMessage(jobId, reschedule);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public JobWorkItem dequeueWork(int jobId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    JobWorkItem jobWorkItem = 3;
                    if (!this.mRemote.transact(3, _data, _reply, 0)) {
                        jobWorkItem = Stub.getDefaultImpl();
                        if (jobWorkItem != 0) {
                            jobWorkItem = Stub.getDefaultImpl().dequeueWork(jobId);
                            return jobWorkItem;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        jobWorkItem = (JobWorkItem) JobWorkItem.CREATOR.createFromParcel(_reply);
                    } else {
                        jobWorkItem = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return jobWorkItem;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean completeWork(int jobId, int workId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeInt(workId);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(4, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().completeWork(jobId, workId);
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void jobFinished(int jobId, boolean reschedule) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeInt(reschedule ? 1 : 0);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().jobFinished(jobId, reschedule);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IJobCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IJobCallback)) {
                return new Proxy(obj);
            }
            return (IJobCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "acknowledgeStartMessage";
            }
            if (transactionCode == 2) {
                return "acknowledgeStopMessage";
            }
            if (transactionCode == 3) {
                return "dequeueWork";
            }
            if (transactionCode == 4) {
                return "completeWork";
            }
            if (transactionCode != 5) {
                return null;
            }
            return "jobFinished";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            boolean _arg1 = false;
            int _arg0;
            if (code == 1) {
                data.enforceInterface(descriptor);
                _arg0 = data.readInt();
                if (data.readInt() != 0) {
                    _arg1 = true;
                }
                acknowledgeStartMessage(_arg0, _arg1);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                _arg0 = data.readInt();
                if (data.readInt() != 0) {
                    _arg1 = true;
                }
                acknowledgeStopMessage(_arg0, _arg1);
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(descriptor);
                JobWorkItem _result = dequeueWork(data.readInt());
                reply.writeNoException();
                if (_result != null) {
                    reply.writeInt(1);
                    _result.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            } else if (code == 4) {
                data.enforceInterface(descriptor);
                boolean _result2 = completeWork(data.readInt(), data.readInt());
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            } else if (code == 5) {
                data.enforceInterface(descriptor);
                _arg0 = data.readInt();
                if (data.readInt() != 0) {
                    _arg1 = true;
                }
                jobFinished(_arg0, _arg1);
                reply.writeNoException();
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IJobCallback impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IJobCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    @UnsupportedAppUsage
    void acknowledgeStartMessage(int i, boolean z) throws RemoteException;

    @UnsupportedAppUsage
    void acknowledgeStopMessage(int i, boolean z) throws RemoteException;

    @UnsupportedAppUsage
    boolean completeWork(int i, int i2) throws RemoteException;

    @UnsupportedAppUsage
    JobWorkItem dequeueWork(int i) throws RemoteException;

    @UnsupportedAppUsage
    void jobFinished(int i, boolean z) throws RemoteException;
}
