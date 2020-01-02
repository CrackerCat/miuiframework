package android.hardware.location;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface IContextHubService extends IInterface {

    public static class Default implements IContextHubService {
        public int registerCallback(IContextHubCallback callback) throws RemoteException {
            return 0;
        }

        public int[] getContextHubHandles() throws RemoteException {
            return null;
        }

        public ContextHubInfo getContextHubInfo(int contextHubHandle) throws RemoteException {
            return null;
        }

        public int loadNanoApp(int contextHubHandle, NanoApp nanoApp) throws RemoteException {
            return 0;
        }

        public int unloadNanoApp(int nanoAppHandle) throws RemoteException {
            return 0;
        }

        public NanoAppInstanceInfo getNanoAppInstanceInfo(int nanoAppHandle) throws RemoteException {
            return null;
        }

        public int[] findNanoAppOnHub(int contextHubHandle, NanoAppFilter filter) throws RemoteException {
            return null;
        }

        public int sendMessage(int contextHubHandle, int nanoAppHandle, ContextHubMessage msg) throws RemoteException {
            return 0;
        }

        public IContextHubClient createClient(int contextHubId, IContextHubClientCallback client) throws RemoteException {
            return null;
        }

        public IContextHubClient createPendingIntentClient(int contextHubId, PendingIntent pendingIntent, long nanoAppId) throws RemoteException {
            return null;
        }

        public List<ContextHubInfo> getContextHubs() throws RemoteException {
            return null;
        }

        public void loadNanoAppOnHub(int contextHubId, IContextHubTransactionCallback transactionCallback, NanoAppBinary nanoAppBinary) throws RemoteException {
        }

        public void unloadNanoAppFromHub(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
        }

        public void enableNanoApp(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
        }

        public void disableNanoApp(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
        }

        public void queryNanoApps(int contextHubId, IContextHubTransactionCallback transactionCallback) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IContextHubService {
        private static final String DESCRIPTOR = "android.hardware.location.IContextHubService";
        static final int TRANSACTION_createClient = 9;
        static final int TRANSACTION_createPendingIntentClient = 10;
        static final int TRANSACTION_disableNanoApp = 15;
        static final int TRANSACTION_enableNanoApp = 14;
        static final int TRANSACTION_findNanoAppOnHub = 7;
        static final int TRANSACTION_getContextHubHandles = 2;
        static final int TRANSACTION_getContextHubInfo = 3;
        static final int TRANSACTION_getContextHubs = 11;
        static final int TRANSACTION_getNanoAppInstanceInfo = 6;
        static final int TRANSACTION_loadNanoApp = 4;
        static final int TRANSACTION_loadNanoAppOnHub = 12;
        static final int TRANSACTION_queryNanoApps = 16;
        static final int TRANSACTION_registerCallback = 1;
        static final int TRANSACTION_sendMessage = 8;
        static final int TRANSACTION_unloadNanoApp = 5;
        static final int TRANSACTION_unloadNanoAppFromHub = 13;

        private static class Proxy implements IContextHubService {
            public static IContextHubService sDefaultImpl;
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

            public int registerCallback(IContextHubCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    int i = 1;
                    if (!this.mRemote.transact(1, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != 0) {
                            i = Stub.getDefaultImpl().registerCallback(callback);
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

            public int[] getContextHubHandles() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    int[] iArr = 2;
                    if (!this.mRemote.transact(2, _data, _reply, 0)) {
                        iArr = Stub.getDefaultImpl();
                        if (iArr != 0) {
                            iArr = Stub.getDefaultImpl().getContextHubHandles();
                            return iArr;
                        }
                    }
                    _reply.readException();
                    iArr = _reply.createIntArray();
                    int[] _result = iArr;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ContextHubInfo getContextHubInfo(int contextHubHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubHandle);
                    ContextHubInfo contextHubInfo = 3;
                    if (!this.mRemote.transact(3, _data, _reply, 0)) {
                        contextHubInfo = Stub.getDefaultImpl();
                        if (contextHubInfo != 0) {
                            contextHubInfo = Stub.getDefaultImpl().getContextHubInfo(contextHubHandle);
                            return contextHubInfo;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        contextHubInfo = (ContextHubInfo) ContextHubInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        contextHubInfo = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return contextHubInfo;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int loadNanoApp(int contextHubHandle, NanoApp nanoApp) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubHandle);
                    if (nanoApp != null) {
                        _data.writeInt(1);
                        nanoApp.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    int i = this.mRemote;
                    if (!i.transact(4, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != null) {
                            i = Stub.getDefaultImpl().loadNanoApp(contextHubHandle, nanoApp);
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

            public int unloadNanoApp(int nanoAppHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nanoAppHandle);
                    int i = 5;
                    if (!this.mRemote.transact(5, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != 0) {
                            i = Stub.getDefaultImpl().unloadNanoApp(nanoAppHandle);
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

            public NanoAppInstanceInfo getNanoAppInstanceInfo(int nanoAppHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nanoAppHandle);
                    NanoAppInstanceInfo nanoAppInstanceInfo = 6;
                    if (!this.mRemote.transact(6, _data, _reply, 0)) {
                        nanoAppInstanceInfo = Stub.getDefaultImpl();
                        if (nanoAppInstanceInfo != 0) {
                            nanoAppInstanceInfo = Stub.getDefaultImpl().getNanoAppInstanceInfo(nanoAppHandle);
                            return nanoAppInstanceInfo;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        nanoAppInstanceInfo = (NanoAppInstanceInfo) NanoAppInstanceInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        nanoAppInstanceInfo = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return nanoAppInstanceInfo;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int[] findNanoAppOnHub(int contextHubHandle, NanoAppFilter filter) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubHandle);
                    if (filter != null) {
                        _data.writeInt(1);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    int[] iArr = this.mRemote;
                    if (!iArr.transact(7, _data, _reply, 0)) {
                        iArr = Stub.getDefaultImpl();
                        if (iArr != null) {
                            iArr = Stub.getDefaultImpl().findNanoAppOnHub(contextHubHandle, filter);
                            return iArr;
                        }
                    }
                    _reply.readException();
                    iArr = _reply.createIntArray();
                    int[] _result = iArr;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int sendMessage(int contextHubHandle, int nanoAppHandle, ContextHubMessage msg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubHandle);
                    _data.writeInt(nanoAppHandle);
                    if (msg != null) {
                        _data.writeInt(1);
                        msg.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    int i = this.mRemote;
                    if (!i.transact(8, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != null) {
                            i = Stub.getDefaultImpl().sendMessage(contextHubHandle, nanoAppHandle, msg);
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

            public IContextHubClient createClient(int contextHubId, IContextHubClientCallback client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    IContextHubClient iContextHubClient = 9;
                    if (!this.mRemote.transact(9, _data, _reply, 0)) {
                        iContextHubClient = Stub.getDefaultImpl();
                        if (iContextHubClient != 0) {
                            iContextHubClient = Stub.getDefaultImpl().createClient(contextHubId, client);
                            return iContextHubClient;
                        }
                    }
                    _reply.readException();
                    iContextHubClient = android.hardware.location.IContextHubClient.Stub.asInterface(_reply.readStrongBinder());
                    IContextHubClient _result = iContextHubClient;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public IContextHubClient createPendingIntentClient(int contextHubId, PendingIntent pendingIntent, long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    if (pendingIntent != null) {
                        _data.writeInt(1);
                        pendingIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(nanoAppId);
                    IContextHubClient iContextHubClient = this.mRemote;
                    if (!iContextHubClient.transact(10, _data, _reply, 0)) {
                        iContextHubClient = Stub.getDefaultImpl();
                        if (iContextHubClient != null) {
                            iContextHubClient = Stub.getDefaultImpl().createPendingIntentClient(contextHubId, pendingIntent, nanoAppId);
                            return iContextHubClient;
                        }
                    }
                    _reply.readException();
                    iContextHubClient = android.hardware.location.IContextHubClient.Stub.asInterface(_reply.readStrongBinder());
                    IContextHubClient _result = iContextHubClient;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ContextHubInfo> getContextHubs() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    List<ContextHubInfo> list = 11;
                    if (!this.mRemote.transact(11, _data, _reply, 0)) {
                        list = Stub.getDefaultImpl();
                        if (list != 0) {
                            list = Stub.getDefaultImpl().getContextHubs();
                            return list;
                        }
                    }
                    _reply.readException();
                    list = _reply.createTypedArrayList(ContextHubInfo.CREATOR);
                    List<ContextHubInfo> _result = list;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void loadNanoAppOnHub(int contextHubId, IContextHubTransactionCallback transactionCallback, NanoAppBinary nanoAppBinary) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongBinder(transactionCallback != null ? transactionCallback.asBinder() : null);
                    if (nanoAppBinary != null) {
                        _data.writeInt(1);
                        nanoAppBinary.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(12, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().loadNanoAppOnHub(contextHubId, transactionCallback, nanoAppBinary);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unloadNanoAppFromHub(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongBinder(transactionCallback != null ? transactionCallback.asBinder() : null);
                    _data.writeLong(nanoAppId);
                    if (this.mRemote.transact(13, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unloadNanoAppFromHub(contextHubId, transactionCallback, nanoAppId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void enableNanoApp(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongBinder(transactionCallback != null ? transactionCallback.asBinder() : null);
                    _data.writeLong(nanoAppId);
                    if (this.mRemote.transact(14, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().enableNanoApp(contextHubId, transactionCallback, nanoAppId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void disableNanoApp(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongBinder(transactionCallback != null ? transactionCallback.asBinder() : null);
                    _data.writeLong(nanoAppId);
                    if (this.mRemote.transact(15, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().disableNanoApp(contextHubId, transactionCallback, nanoAppId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void queryNanoApps(int contextHubId, IContextHubTransactionCallback transactionCallback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongBinder(transactionCallback != null ? transactionCallback.asBinder() : null);
                    if (this.mRemote.transact(16, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().queryNanoApps(contextHubId, transactionCallback);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IContextHubService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IContextHubService)) {
                return new Proxy(obj);
            }
            return (IContextHubService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "registerCallback";
                case 2:
                    return "getContextHubHandles";
                case 3:
                    return "getContextHubInfo";
                case 4:
                    return "loadNanoApp";
                case 5:
                    return "unloadNanoApp";
                case 6:
                    return "getNanoAppInstanceInfo";
                case 7:
                    return "findNanoAppOnHub";
                case 8:
                    return "sendMessage";
                case 9:
                    return "createClient";
                case 10:
                    return "createPendingIntentClient";
                case 11:
                    return "getContextHubs";
                case 12:
                    return "loadNanoAppOnHub";
                case 13:
                    return "unloadNanoAppFromHub";
                case 14:
                    return "enableNanoApp";
                case 15:
                    return "disableNanoApp";
                case 16:
                    return "queryNanoApps";
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
                IBinder iBinder = null;
                int _result;
                int _arg0;
                switch (code) {
                    case 1:
                        data.enforceInterface(descriptor);
                        _result = registerCallback(android.hardware.location.IContextHubCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 2:
                        data.enforceInterface(descriptor);
                        int[] _result2 = getContextHubHandles();
                        reply.writeNoException();
                        reply.writeIntArray(_result2);
                        return true;
                    case 3:
                        data.enforceInterface(descriptor);
                        ContextHubInfo _result3 = getContextHubInfo(data.readInt());
                        reply.writeNoException();
                        if (_result3 != null) {
                            reply.writeInt(1);
                            _result3.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 4:
                        NanoApp _arg1;
                        data.enforceInterface(descriptor);
                        _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = (NanoApp) NanoApp.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        int _result4 = loadNanoApp(_arg0, _arg1);
                        reply.writeNoException();
                        reply.writeInt(_result4);
                        return true;
                    case 5:
                        data.enforceInterface(descriptor);
                        _result = unloadNanoApp(data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 6:
                        data.enforceInterface(descriptor);
                        NanoAppInstanceInfo _result5 = getNanoAppInstanceInfo(data.readInt());
                        reply.writeNoException();
                        if (_result5 != null) {
                            reply.writeInt(1);
                            _result5.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 7:
                        NanoAppFilter _arg12;
                        data.enforceInterface(descriptor);
                        _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg12 = (NanoAppFilter) NanoAppFilter.CREATOR.createFromParcel(data);
                        } else {
                            _arg12 = null;
                        }
                        int[] _result6 = findNanoAppOnHub(_arg0, _arg12);
                        reply.writeNoException();
                        reply.writeIntArray(_result6);
                        return true;
                    case 8:
                        ContextHubMessage _arg2;
                        data.enforceInterface(descriptor);
                        _arg0 = data.readInt();
                        _result = data.readInt();
                        if (data.readInt() != 0) {
                            _arg2 = (ContextHubMessage) ContextHubMessage.CREATOR.createFromParcel(data);
                        } else {
                            _arg2 = null;
                        }
                        int _result7 = sendMessage(_arg0, _result, _arg2);
                        reply.writeNoException();
                        reply.writeInt(_result7);
                        return true;
                    case 9:
                        data.enforceInterface(descriptor);
                        IContextHubClient _result8 = createClient(data.readInt(), android.hardware.location.IContextHubClientCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        if (_result8 != null) {
                            iBinder = _result8.asBinder();
                        }
                        reply.writeStrongBinder(iBinder);
                        return true;
                    case 10:
                        PendingIntent _arg13;
                        data.enforceInterface(descriptor);
                        _result = data.readInt();
                        if (data.readInt() != 0) {
                            _arg13 = (PendingIntent) PendingIntent.CREATOR.createFromParcel(data);
                        } else {
                            _arg13 = null;
                        }
                        IContextHubClient _result9 = createPendingIntentClient(_result, _arg13, data.readLong());
                        reply.writeNoException();
                        if (_result9 != null) {
                            iBinder = _result9.asBinder();
                        }
                        reply.writeStrongBinder(iBinder);
                        return true;
                    case 11:
                        data.enforceInterface(descriptor);
                        List<ContextHubInfo> _result10 = getContextHubs();
                        reply.writeNoException();
                        reply.writeTypedList(_result10);
                        return true;
                    case 12:
                        NanoAppBinary _arg22;
                        data.enforceInterface(descriptor);
                        _arg0 = data.readInt();
                        IContextHubTransactionCallback _arg14 = android.hardware.location.IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder());
                        if (data.readInt() != 0) {
                            _arg22 = (NanoAppBinary) NanoAppBinary.CREATOR.createFromParcel(data);
                        } else {
                            _arg22 = null;
                        }
                        loadNanoAppOnHub(_arg0, _arg14, _arg22);
                        reply.writeNoException();
                        return true;
                    case 13:
                        data.enforceInterface(descriptor);
                        unloadNanoAppFromHub(data.readInt(), android.hardware.location.IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder()), data.readLong());
                        reply.writeNoException();
                        return true;
                    case 14:
                        data.enforceInterface(descriptor);
                        enableNanoApp(data.readInt(), android.hardware.location.IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder()), data.readLong());
                        reply.writeNoException();
                        return true;
                    case 15:
                        data.enforceInterface(descriptor);
                        disableNanoApp(data.readInt(), android.hardware.location.IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder()), data.readLong());
                        reply.writeNoException();
                        return true;
                    case 16:
                        data.enforceInterface(descriptor);
                        queryNanoApps(data.readInt(), android.hardware.location.IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IContextHubService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IContextHubService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    IContextHubClient createClient(int i, IContextHubClientCallback iContextHubClientCallback) throws RemoteException;

    IContextHubClient createPendingIntentClient(int i, PendingIntent pendingIntent, long j) throws RemoteException;

    void disableNanoApp(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException;

    void enableNanoApp(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException;

    int[] findNanoAppOnHub(int i, NanoAppFilter nanoAppFilter) throws RemoteException;

    int[] getContextHubHandles() throws RemoteException;

    ContextHubInfo getContextHubInfo(int i) throws RemoteException;

    List<ContextHubInfo> getContextHubs() throws RemoteException;

    NanoAppInstanceInfo getNanoAppInstanceInfo(int i) throws RemoteException;

    int loadNanoApp(int i, NanoApp nanoApp) throws RemoteException;

    void loadNanoAppOnHub(int i, IContextHubTransactionCallback iContextHubTransactionCallback, NanoAppBinary nanoAppBinary) throws RemoteException;

    void queryNanoApps(int i, IContextHubTransactionCallback iContextHubTransactionCallback) throws RemoteException;

    int registerCallback(IContextHubCallback iContextHubCallback) throws RemoteException;

    int sendMessage(int i, int i2, ContextHubMessage contextHubMessage) throws RemoteException;

    int unloadNanoApp(int i) throws RemoteException;

    void unloadNanoAppFromHub(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException;
}
