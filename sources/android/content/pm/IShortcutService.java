package android.content.pm;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import java.util.List;

public interface IShortcutService extends IInterface {

    public static class Default implements IShortcutService {
        public boolean setDynamicShortcuts(String packageName, ParceledListSlice shortcutInfoList, int userId) throws RemoteException {
            return false;
        }

        public ParceledListSlice getDynamicShortcuts(String packageName, int userId) throws RemoteException {
            return null;
        }

        public ParceledListSlice getManifestShortcuts(String packageName, int userId) throws RemoteException {
            return null;
        }

        public boolean addDynamicShortcuts(String packageName, ParceledListSlice shortcutInfoList, int userId) throws RemoteException {
            return false;
        }

        public void removeDynamicShortcuts(String packageName, List shortcutIds, int userId) throws RemoteException {
        }

        public void removeAllDynamicShortcuts(String packageName, int userId) throws RemoteException {
        }

        public ParceledListSlice getPinnedShortcuts(String packageName, int userId) throws RemoteException {
            return null;
        }

        public boolean updateShortcuts(String packageName, ParceledListSlice shortcuts, int userId) throws RemoteException {
            return false;
        }

        public boolean requestPinShortcut(String packageName, ShortcutInfo shortcut, IntentSender resultIntent, int userId) throws RemoteException {
            return false;
        }

        public Intent createShortcutResultIntent(String packageName, ShortcutInfo shortcut, int userId) throws RemoteException {
            return null;
        }

        public void disableShortcuts(String packageName, List shortcutIds, CharSequence disabledMessage, int disabledMessageResId, int userId) throws RemoteException {
        }

        public void enableShortcuts(String packageName, List shortcutIds, int userId) throws RemoteException {
        }

        public int getMaxShortcutCountPerActivity(String packageName, int userId) throws RemoteException {
            return 0;
        }

        public int getRemainingCallCount(String packageName, int userId) throws RemoteException {
            return 0;
        }

        public long getRateLimitResetTime(String packageName, int userId) throws RemoteException {
            return 0;
        }

        public int getIconMaxDimensions(String packageName, int userId) throws RemoteException {
            return 0;
        }

        public void reportShortcutUsed(String packageName, String shortcutId, int userId) throws RemoteException {
        }

        public void resetThrottling() throws RemoteException {
        }

        public void onApplicationActive(String packageName, int userId) throws RemoteException {
        }

        public byte[] getBackupPayload(int user) throws RemoteException {
            return null;
        }

        public void applyRestore(byte[] payload, int user) throws RemoteException {
        }

        public boolean isRequestPinItemSupported(int user, int requestType) throws RemoteException {
            return false;
        }

        public ParceledListSlice getShareTargets(String packageName, IntentFilter filter, int userId) throws RemoteException {
            return null;
        }

        public boolean hasShareTargets(String packageName, String packageToCheck, int userId) throws RemoteException {
            return false;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IShortcutService {
        private static final String DESCRIPTOR = "android.content.pm.IShortcutService";
        static final int TRANSACTION_addDynamicShortcuts = 4;
        static final int TRANSACTION_applyRestore = 21;
        static final int TRANSACTION_createShortcutResultIntent = 10;
        static final int TRANSACTION_disableShortcuts = 11;
        static final int TRANSACTION_enableShortcuts = 12;
        static final int TRANSACTION_getBackupPayload = 20;
        static final int TRANSACTION_getDynamicShortcuts = 2;
        static final int TRANSACTION_getIconMaxDimensions = 16;
        static final int TRANSACTION_getManifestShortcuts = 3;
        static final int TRANSACTION_getMaxShortcutCountPerActivity = 13;
        static final int TRANSACTION_getPinnedShortcuts = 7;
        static final int TRANSACTION_getRateLimitResetTime = 15;
        static final int TRANSACTION_getRemainingCallCount = 14;
        static final int TRANSACTION_getShareTargets = 23;
        static final int TRANSACTION_hasShareTargets = 24;
        static final int TRANSACTION_isRequestPinItemSupported = 22;
        static final int TRANSACTION_onApplicationActive = 19;
        static final int TRANSACTION_removeAllDynamicShortcuts = 6;
        static final int TRANSACTION_removeDynamicShortcuts = 5;
        static final int TRANSACTION_reportShortcutUsed = 17;
        static final int TRANSACTION_requestPinShortcut = 9;
        static final int TRANSACTION_resetThrottling = 18;
        static final int TRANSACTION_setDynamicShortcuts = 1;
        static final int TRANSACTION_updateShortcuts = 8;

        private static class Proxy implements IShortcutService {
            public static IShortcutService sDefaultImpl;
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

            public boolean setDynamicShortcuts(String packageName, ParceledListSlice shortcutInfoList, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _result = true;
                    if (shortcutInfoList != null) {
                        _data.writeInt(1);
                        shortcutInfoList.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        if (_reply.readInt() == 0) {
                            _result = false;
                        }
                        _reply.recycle();
                        _data.recycle();
                        return _result;
                    }
                    _result = Stub.getDefaultImpl().setDynamicShortcuts(packageName, shortcutInfoList, userId);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParceledListSlice getDynamicShortcuts(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    ParceledListSlice parceledListSlice = 2;
                    if (!this.mRemote.transact(2, _data, _reply, 0)) {
                        parceledListSlice = Stub.getDefaultImpl();
                        if (parceledListSlice != 0) {
                            parceledListSlice = Stub.getDefaultImpl().getDynamicShortcuts(packageName, userId);
                            return parceledListSlice;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        parceledListSlice = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        parceledListSlice = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return parceledListSlice;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParceledListSlice getManifestShortcuts(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    ParceledListSlice parceledListSlice = 3;
                    if (!this.mRemote.transact(3, _data, _reply, 0)) {
                        parceledListSlice = Stub.getDefaultImpl();
                        if (parceledListSlice != 0) {
                            parceledListSlice = Stub.getDefaultImpl().getManifestShortcuts(packageName, userId);
                            return parceledListSlice;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        parceledListSlice = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        parceledListSlice = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return parceledListSlice;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean addDynamicShortcuts(String packageName, ParceledListSlice shortcutInfoList, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _result = true;
                    if (shortcutInfoList != null) {
                        _data.writeInt(1);
                        shortcutInfoList.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        if (_reply.readInt() == 0) {
                            _result = false;
                        }
                        _reply.recycle();
                        _data.recycle();
                        return _result;
                    }
                    _result = Stub.getDefaultImpl().addDynamicShortcuts(packageName, shortcutInfoList, userId);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeDynamicShortcuts(String packageName, List shortcutIds, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeList(shortcutIds);
                    _data.writeInt(userId);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().removeDynamicShortcuts(packageName, shortcutIds, userId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeAllDynamicShortcuts(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().removeAllDynamicShortcuts(packageName, userId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParceledListSlice getPinnedShortcuts(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    ParceledListSlice parceledListSlice = 7;
                    if (!this.mRemote.transact(7, _data, _reply, 0)) {
                        parceledListSlice = Stub.getDefaultImpl();
                        if (parceledListSlice != 0) {
                            parceledListSlice = Stub.getDefaultImpl().getPinnedShortcuts(packageName, userId);
                            return parceledListSlice;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        parceledListSlice = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        parceledListSlice = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return parceledListSlice;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean updateShortcuts(String packageName, ParceledListSlice shortcuts, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _result = true;
                    if (shortcuts != null) {
                        _data.writeInt(1);
                        shortcuts.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        if (_reply.readInt() == 0) {
                            _result = false;
                        }
                        _reply.recycle();
                        _data.recycle();
                        return _result;
                    }
                    _result = Stub.getDefaultImpl().updateShortcuts(packageName, shortcuts, userId);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean requestPinShortcut(String packageName, ShortcutInfo shortcut, IntentSender resultIntent, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _result = true;
                    if (shortcut != null) {
                        _data.writeInt(1);
                        shortcut.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (resultIntent != null) {
                        _data.writeInt(1);
                        resultIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    if (this.mRemote.transact(9, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        if (_reply.readInt() == 0) {
                            _result = false;
                        }
                        _reply.recycle();
                        _data.recycle();
                        return _result;
                    }
                    _result = Stub.getDefaultImpl().requestPinShortcut(packageName, shortcut, resultIntent, userId);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Intent createShortcutResultIntent(String packageName, ShortcutInfo shortcut, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (shortcut != null) {
                        _data.writeInt(1);
                        shortcut.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    Intent intent = this.mRemote;
                    if (!intent.transact(10, _data, _reply, 0)) {
                        intent = Stub.getDefaultImpl();
                        if (intent != null) {
                            intent = Stub.getDefaultImpl().createShortcutResultIntent(packageName, shortcut, userId);
                            return intent;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        intent = (Intent) Intent.CREATOR.createFromParcel(_reply);
                    } else {
                        intent = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return intent;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void disableShortcuts(String packageName, List shortcutIds, CharSequence disabledMessage, int disabledMessageResId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeList(shortcutIds);
                    if (disabledMessage != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(disabledMessage, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(disabledMessageResId);
                    _data.writeInt(userId);
                    if (this.mRemote.transact(11, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().disableShortcuts(packageName, shortcutIds, disabledMessage, disabledMessageResId, userId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void enableShortcuts(String packageName, List shortcutIds, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeList(shortcutIds);
                    _data.writeInt(userId);
                    if (this.mRemote.transact(12, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().enableShortcuts(packageName, shortcutIds, userId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getMaxShortcutCountPerActivity(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    int i = 13;
                    if (!this.mRemote.transact(13, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != 0) {
                            i = Stub.getDefaultImpl().getMaxShortcutCountPerActivity(packageName, userId);
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

            public int getRemainingCallCount(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    int i = 14;
                    if (!this.mRemote.transact(14, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != 0) {
                            i = Stub.getDefaultImpl().getRemainingCallCount(packageName, userId);
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

            public long getRateLimitResetTime(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    long j = 15;
                    if (!this.mRemote.transact(15, _data, _reply, 0)) {
                        j = Stub.getDefaultImpl();
                        if (j != 0) {
                            j = Stub.getDefaultImpl().getRateLimitResetTime(packageName, userId);
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

            public int getIconMaxDimensions(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    int i = 16;
                    if (!this.mRemote.transact(16, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != 0) {
                            i = Stub.getDefaultImpl().getIconMaxDimensions(packageName, userId);
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

            public void reportShortcutUsed(String packageName, String shortcutId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(shortcutId);
                    _data.writeInt(userId);
                    if (this.mRemote.transact(17, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().reportShortcutUsed(packageName, shortcutId, userId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void resetThrottling() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(18, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().resetThrottling();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onApplicationActive(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    if (this.mRemote.transact(19, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onApplicationActive(packageName, userId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getBackupPayload(int user) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(user);
                    byte[] bArr = 20;
                    if (!this.mRemote.transact(20, _data, _reply, 0)) {
                        bArr = Stub.getDefaultImpl();
                        if (bArr != 0) {
                            bArr = Stub.getDefaultImpl().getBackupPayload(user);
                            return bArr;
                        }
                    }
                    _reply.readException();
                    bArr = _reply.createByteArray();
                    byte[] _result = bArr;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void applyRestore(byte[] payload, int user) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(payload);
                    _data.writeInt(user);
                    if (this.mRemote.transact(21, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().applyRestore(payload, user);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isRequestPinItemSupported(int user, int requestType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(user);
                    _data.writeInt(requestType);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(22, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().isRequestPinItemSupported(user, requestType);
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

            public ParceledListSlice getShareTargets(String packageName, IntentFilter filter, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (filter != null) {
                        _data.writeInt(1);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    ParceledListSlice parceledListSlice = this.mRemote;
                    if (!parceledListSlice.transact(23, _data, _reply, 0)) {
                        parceledListSlice = Stub.getDefaultImpl();
                        if (parceledListSlice != null) {
                            parceledListSlice = Stub.getDefaultImpl().getShareTargets(packageName, filter, userId);
                            return parceledListSlice;
                        }
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        parceledListSlice = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        parceledListSlice = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return parceledListSlice;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean hasShareTargets(String packageName, String packageToCheck, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(packageToCheck);
                    _data.writeInt(userId);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(24, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().hasShareTargets(packageName, packageToCheck, userId);
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
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IShortcutService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IShortcutService)) {
                return new Proxy(obj);
            }
            return (IShortcutService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "setDynamicShortcuts";
                case 2:
                    return "getDynamicShortcuts";
                case 3:
                    return "getManifestShortcuts";
                case 4:
                    return "addDynamicShortcuts";
                case 5:
                    return "removeDynamicShortcuts";
                case 6:
                    return "removeAllDynamicShortcuts";
                case 7:
                    return "getPinnedShortcuts";
                case 8:
                    return "updateShortcuts";
                case 9:
                    return "requestPinShortcut";
                case 10:
                    return "createShortcutResultIntent";
                case 11:
                    return "disableShortcuts";
                case 12:
                    return "enableShortcuts";
                case 13:
                    return "getMaxShortcutCountPerActivity";
                case 14:
                    return "getRemainingCallCount";
                case 15:
                    return "getRateLimitResetTime";
                case 16:
                    return "getIconMaxDimensions";
                case 17:
                    return "reportShortcutUsed";
                case 18:
                    return "resetThrottling";
                case 19:
                    return "onApplicationActive";
                case 20:
                    return "getBackupPayload";
                case 21:
                    return "applyRestore";
                case 22:
                    return "isRequestPinItemSupported";
                case 23:
                    return "getShareTargets";
                case 24:
                    return "hasShareTargets";
                default:
                    return null;
            }
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = code;
            Parcel parcel = data;
            Parcel parcel2 = reply;
            String descriptor = DESCRIPTOR;
            if (i != 1598968902) {
                String _arg0;
                ParceledListSlice _arg1;
                boolean _result;
                ParceledListSlice _result2;
                String _arg02;
                int _result3;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(descriptor);
                        _arg0 = data.readString();
                        if (data.readInt() != 0) {
                            _arg1 = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg1 = null;
                        }
                        _result = setDynamicShortcuts(_arg0, _arg1, data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result);
                        return true;
                    case 2:
                        parcel.enforceInterface(descriptor);
                        _result2 = getDynamicShortcuts(data.readString(), data.readInt());
                        reply.writeNoException();
                        if (_result2 != null) {
                            parcel2.writeInt(1);
                            _result2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 3:
                        parcel.enforceInterface(descriptor);
                        _result2 = getManifestShortcuts(data.readString(), data.readInt());
                        reply.writeNoException();
                        if (_result2 != null) {
                            parcel2.writeInt(1);
                            _result2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 4:
                        parcel.enforceInterface(descriptor);
                        _arg0 = data.readString();
                        if (data.readInt() != 0) {
                            _arg1 = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg1 = null;
                        }
                        _result = addDynamicShortcuts(_arg0, _arg1, data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result);
                        return true;
                    case 5:
                        parcel.enforceInterface(descriptor);
                        removeDynamicShortcuts(data.readString(), parcel.readArrayList(getClass().getClassLoader()), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface(descriptor);
                        removeAllDynamicShortcuts(data.readString(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 7:
                        parcel.enforceInterface(descriptor);
                        _result2 = getPinnedShortcuts(data.readString(), data.readInt());
                        reply.writeNoException();
                        if (_result2 != null) {
                            parcel2.writeInt(1);
                            _result2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 8:
                        parcel.enforceInterface(descriptor);
                        _arg0 = data.readString();
                        if (data.readInt() != 0) {
                            _arg1 = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg1 = null;
                        }
                        _result = updateShortcuts(_arg0, _arg1, data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result);
                        return true;
                    case 9:
                        ShortcutInfo _arg12;
                        IntentSender _arg2;
                        parcel.enforceInterface(descriptor);
                        _arg0 = data.readString();
                        if (data.readInt() != 0) {
                            _arg12 = (ShortcutInfo) ShortcutInfo.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg12 = null;
                        }
                        if (data.readInt() != 0) {
                            _arg2 = (IntentSender) IntentSender.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg2 = null;
                        }
                        boolean _result4 = requestPinShortcut(_arg0, _arg12, _arg2, data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result4);
                        return true;
                    case 10:
                        ShortcutInfo _arg13;
                        parcel.enforceInterface(descriptor);
                        _arg02 = data.readString();
                        if (data.readInt() != 0) {
                            _arg13 = (ShortcutInfo) ShortcutInfo.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg13 = null;
                        }
                        Intent _result5 = createShortcutResultIntent(_arg02, _arg13, data.readInt());
                        reply.writeNoException();
                        if (_result5 != null) {
                            parcel2.writeInt(1);
                            _result5.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 11:
                        CharSequence _arg22;
                        parcel.enforceInterface(descriptor);
                        String _arg03 = data.readString();
                        List _arg14 = parcel.readArrayList(getClass().getClassLoader());
                        if (data.readInt() != 0) {
                            _arg22 = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
                        } else {
                            _arg22 = null;
                        }
                        disableShortcuts(_arg03, _arg14, _arg22, data.readInt(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 12:
                        parcel.enforceInterface(descriptor);
                        enableShortcuts(data.readString(), parcel.readArrayList(getClass().getClassLoader()), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 13:
                        parcel.enforceInterface(descriptor);
                        _result3 = getMaxShortcutCountPerActivity(data.readString(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result3);
                        return true;
                    case 14:
                        parcel.enforceInterface(descriptor);
                        _result3 = getRemainingCallCount(data.readString(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result3);
                        return true;
                    case 15:
                        parcel.enforceInterface(descriptor);
                        long _result6 = getRateLimitResetTime(data.readString(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeLong(_result6);
                        return true;
                    case 16:
                        parcel.enforceInterface(descriptor);
                        _result3 = getIconMaxDimensions(data.readString(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result3);
                        return true;
                    case 17:
                        parcel.enforceInterface(descriptor);
                        reportShortcutUsed(data.readString(), data.readString(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 18:
                        parcel.enforceInterface(descriptor);
                        resetThrottling();
                        reply.writeNoException();
                        return true;
                    case 19:
                        parcel.enforceInterface(descriptor);
                        onApplicationActive(data.readString(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 20:
                        parcel.enforceInterface(descriptor);
                        byte[] _result7 = getBackupPayload(data.readInt());
                        reply.writeNoException();
                        parcel2.writeByteArray(_result7);
                        return true;
                    case 21:
                        parcel.enforceInterface(descriptor);
                        applyRestore(data.createByteArray(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 22:
                        parcel.enforceInterface(descriptor);
                        boolean _result8 = isRequestPinItemSupported(data.readInt(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result8);
                        return true;
                    case 23:
                        IntentFilter _arg15;
                        parcel.enforceInterface(descriptor);
                        _arg02 = data.readString();
                        if (data.readInt() != 0) {
                            _arg15 = (IntentFilter) IntentFilter.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg15 = null;
                        }
                        ParceledListSlice _result9 = getShareTargets(_arg02, _arg15, data.readInt());
                        reply.writeNoException();
                        if (_result9 != null) {
                            parcel2.writeInt(1);
                            _result9.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 24:
                        parcel.enforceInterface(descriptor);
                        _result = hasShareTargets(data.readString(), data.readString(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            parcel2.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IShortcutService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IShortcutService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    boolean addDynamicShortcuts(String str, ParceledListSlice parceledListSlice, int i) throws RemoteException;

    void applyRestore(byte[] bArr, int i) throws RemoteException;

    Intent createShortcutResultIntent(String str, ShortcutInfo shortcutInfo, int i) throws RemoteException;

    void disableShortcuts(String str, List list, CharSequence charSequence, int i, int i2) throws RemoteException;

    void enableShortcuts(String str, List list, int i) throws RemoteException;

    byte[] getBackupPayload(int i) throws RemoteException;

    ParceledListSlice getDynamicShortcuts(String str, int i) throws RemoteException;

    int getIconMaxDimensions(String str, int i) throws RemoteException;

    ParceledListSlice getManifestShortcuts(String str, int i) throws RemoteException;

    int getMaxShortcutCountPerActivity(String str, int i) throws RemoteException;

    ParceledListSlice getPinnedShortcuts(String str, int i) throws RemoteException;

    long getRateLimitResetTime(String str, int i) throws RemoteException;

    int getRemainingCallCount(String str, int i) throws RemoteException;

    ParceledListSlice getShareTargets(String str, IntentFilter intentFilter, int i) throws RemoteException;

    boolean hasShareTargets(String str, String str2, int i) throws RemoteException;

    boolean isRequestPinItemSupported(int i, int i2) throws RemoteException;

    void onApplicationActive(String str, int i) throws RemoteException;

    void removeAllDynamicShortcuts(String str, int i) throws RemoteException;

    void removeDynamicShortcuts(String str, List list, int i) throws RemoteException;

    void reportShortcutUsed(String str, String str2, int i) throws RemoteException;

    boolean requestPinShortcut(String str, ShortcutInfo shortcutInfo, IntentSender intentSender, int i) throws RemoteException;

    void resetThrottling() throws RemoteException;

    boolean setDynamicShortcuts(String str, ParceledListSlice parceledListSlice, int i) throws RemoteException;

    boolean updateShortcuts(String str, ParceledListSlice parceledListSlice, int i) throws RemoteException;
}
