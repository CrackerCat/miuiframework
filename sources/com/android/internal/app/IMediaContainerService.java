package com.android.internal.app;

import android.content.pm.PackageInfoLite;
import android.content.res.ObbInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.os.IParcelFileDescriptorFactory;

public interface IMediaContainerService extends IInterface {

    public static class Default implements IMediaContainerService {
        public int copyPackage(String packagePath, IParcelFileDescriptorFactory target) throws RemoteException {
            return 0;
        }

        public PackageInfoLite getMinimalPackageInfo(String packagePath, int flags, String abiOverride) throws RemoteException {
            return null;
        }

        public ObbInfo getObbInfo(String filename) throws RemoteException {
            return null;
        }

        public long calculateInstalledSize(String packagePath, String abiOverride) throws RemoteException {
            return 0;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMediaContainerService {
        private static final String DESCRIPTOR = "com.android.internal.app.IMediaContainerService";
        static final int TRANSACTION_calculateInstalledSize = 4;
        static final int TRANSACTION_copyPackage = 1;
        static final int TRANSACTION_getMinimalPackageInfo = 2;
        static final int TRANSACTION_getObbInfo = 3;

        private static class Proxy implements IMediaContainerService {
            public static IMediaContainerService sDefaultImpl;
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

            public int copyPackage(String packagePath, IParcelFileDescriptorFactory target) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packagePath);
                    _data.writeStrongBinder(target != null ? target.asBinder() : null);
                    int i = 1;
                    if (!this.mRemote.transact(1, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != 0) {
                            i = Stub.getDefaultImpl().copyPackage(packagePath, target);
                            return i;
                        }
                    }
                    _reply.readException();
                    i = _reply.readInt();
                    int _result = i;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public PackageInfoLite getMinimalPackageInfo(String packagePath, int flags, String abiOverride) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packagePath);
                    _data.writeInt(flags);
                    _data.writeString(abiOverride);
                    PackageInfoLite packageInfoLite = 2;
                    if (!this.mRemote.transact(2, _data, _reply, 0)) {
                        packageInfoLite = Stub.getDefaultImpl();
                        if (packageInfoLite != 0) {
                            packageInfoLite = Stub.getDefaultImpl().getMinimalPackageInfo(packagePath, flags, abiOverride);
                            return packageInfoLite;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        packageInfoLite = (PackageInfoLite) PackageInfoLite.CREATOR.createFromParcel(_reply);
                    } else {
                        packageInfoLite = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return packageInfoLite;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ObbInfo getObbInfo(String filename) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(filename);
                    ObbInfo obbInfo = 3;
                    if (!this.mRemote.transact(3, _data, _reply, 0)) {
                        obbInfo = Stub.getDefaultImpl();
                        if (obbInfo != 0) {
                            obbInfo = Stub.getDefaultImpl().getObbInfo(filename);
                            return obbInfo;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        obbInfo = (ObbInfo) ObbInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        obbInfo = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return obbInfo;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long calculateInstalledSize(String packagePath, String abiOverride) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packagePath);
                    _data.writeString(abiOverride);
                    long j = 4;
                    if (!this.mRemote.transact(4, _data, _reply, 0)) {
                        j = Stub.getDefaultImpl();
                        if (j != 0) {
                            j = Stub.getDefaultImpl().calculateInstalledSize(packagePath, abiOverride);
                            return j;
                        }
                    }
                    _reply.readException();
                    j = _reply.readLong();
                    long _result = j;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaContainerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMediaContainerService)) {
                return new Proxy(obj);
            }
            return (IMediaContainerService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode == 1) {
                return "copyPackage";
            }
            if (transactionCode == 2) {
                return "getMinimalPackageInfo";
            }
            if (transactionCode == 3) {
                return "getObbInfo";
            }
            if (transactionCode != 4) {
                return null;
            }
            return "calculateInstalledSize";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code == 1) {
                data.enforceInterface(descriptor);
                int _result = copyPackage(data.readString(), com.android.internal.os.IParcelFileDescriptorFactory.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(_result);
                return true;
            } else if (code == 2) {
                data.enforceInterface(descriptor);
                PackageInfoLite _result2 = getMinimalPackageInfo(data.readString(), data.readInt(), data.readString());
                reply.writeNoException();
                if (_result2 != null) {
                    reply.writeInt(1);
                    _result2.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            } else if (code == 3) {
                data.enforceInterface(descriptor);
                ObbInfo _result3 = getObbInfo(data.readString());
                reply.writeNoException();
                if (_result3 != null) {
                    reply.writeInt(1);
                    _result3.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            } else if (code == 4) {
                data.enforceInterface(descriptor);
                long _result4 = calculateInstalledSize(data.readString(), data.readString());
                reply.writeNoException();
                reply.writeLong(_result4);
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IMediaContainerService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMediaContainerService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    long calculateInstalledSize(String str, String str2) throws RemoteException;

    int copyPackage(String str, IParcelFileDescriptorFactory iParcelFileDescriptorFactory) throws RemoteException;

    PackageInfoLite getMinimalPackageInfo(String str, int i, String str2) throws RemoteException;

    ObbInfo getObbInfo(String str) throws RemoteException;
}
