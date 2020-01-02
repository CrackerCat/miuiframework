package com.android.internal.view;

import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.ExtractedText;

public interface IInputMethodSession extends IInterface {

    public static class Default implements IInputMethodSession {
        public void updateExtractedText(int token, ExtractedText text) throws RemoteException {
        }

        public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) throws RemoteException {
        }

        public void viewClicked(boolean focusChanged) throws RemoteException {
        }

        public void updateCursor(Rect newCursor) throws RemoteException {
        }

        public void displayCompletions(CompletionInfo[] completions) throws RemoteException {
        }

        public void appPrivateCommand(String action, Bundle data) throws RemoteException {
        }

        public void toggleSoftInput(int showFlags, int hideFlags) throws RemoteException {
        }

        public void finishSession() throws RemoteException {
        }

        public void updateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) throws RemoteException {
        }

        public void notifyImeHidden() throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IInputMethodSession {
        private static final String DESCRIPTOR = "com.android.internal.view.IInputMethodSession";
        static final int TRANSACTION_appPrivateCommand = 6;
        static final int TRANSACTION_displayCompletions = 5;
        static final int TRANSACTION_finishSession = 8;
        static final int TRANSACTION_notifyImeHidden = 10;
        static final int TRANSACTION_toggleSoftInput = 7;
        static final int TRANSACTION_updateCursor = 4;
        static final int TRANSACTION_updateCursorAnchorInfo = 9;
        static final int TRANSACTION_updateExtractedText = 1;
        static final int TRANSACTION_updateSelection = 2;
        static final int TRANSACTION_viewClicked = 3;

        private static class Proxy implements IInputMethodSession {
            public static IInputMethodSession sDefaultImpl;
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

            public void updateExtractedText(int token, ExtractedText text) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(token);
                    if (text != null) {
                        _data.writeInt(1);
                        text.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().updateExtractedText(token, text);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) throws RemoteException {
                Throwable th;
                int i;
                int i2;
                int i3;
                int i4;
                int i5;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(oldSelStart);
                    } catch (Throwable th2) {
                        th = th2;
                        i = oldSelEnd;
                        i2 = newSelStart;
                        i3 = newSelEnd;
                        i4 = candidatesStart;
                        i5 = candidatesEnd;
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(oldSelEnd);
                        try {
                            _data.writeInt(newSelStart);
                            try {
                                _data.writeInt(newSelEnd);
                                try {
                                    _data.writeInt(candidatesStart);
                                } catch (Throwable th3) {
                                    th = th3;
                                    i5 = candidatesEnd;
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th4) {
                                th = th4;
                                i4 = candidatesStart;
                                i5 = candidatesEnd;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th5) {
                            th = th5;
                            i3 = newSelEnd;
                            i4 = candidatesStart;
                            i5 = candidatesEnd;
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(candidatesEnd);
                            try {
                                if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                                    _data.recycle();
                                    return;
                                }
                                Stub.getDefaultImpl().updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
                                _data.recycle();
                            } catch (Throwable th6) {
                                th = th6;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th8) {
                        th = th8;
                        i2 = newSelStart;
                        i3 = newSelEnd;
                        i4 = candidatesStart;
                        i5 = candidatesEnd;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th9) {
                    th = th9;
                    int i6 = oldSelStart;
                    i = oldSelEnd;
                    i2 = newSelStart;
                    i3 = newSelEnd;
                    i4 = candidatesStart;
                    i5 = candidatesEnd;
                    _data.recycle();
                    throw th;
                }
            }

            public void viewClicked(boolean focusChanged) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(focusChanged ? 1 : 0);
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().viewClicked(focusChanged);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void updateCursor(Rect newCursor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (newCursor != null) {
                        _data.writeInt(1);
                        newCursor.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(4, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().updateCursor(newCursor);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void displayCompletions(CompletionInfo[] completions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(completions, 0);
                    if (this.mRemote.transact(5, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().displayCompletions(completions);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void appPrivateCommand(String action, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    if (data != null) {
                        _data.writeInt(1);
                        data.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(6, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().appPrivateCommand(action, data);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void toggleSoftInput(int showFlags, int hideFlags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showFlags);
                    _data.writeInt(hideFlags);
                    if (this.mRemote.transact(7, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().toggleSoftInput(showFlags, hideFlags);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void finishSession() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(8, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().finishSession();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void updateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (cursorAnchorInfo != null) {
                        _data.writeInt(1);
                        cursorAnchorInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(9, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().updateCursorAnchorInfo(cursorAnchorInfo);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void notifyImeHidden() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(10, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().notifyImeHidden();
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInputMethodSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IInputMethodSession)) {
                return new Proxy(obj);
            }
            return (IInputMethodSession) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "updateExtractedText";
                case 2:
                    return "updateSelection";
                case 3:
                    return "viewClicked";
                case 4:
                    return "updateCursor";
                case 5:
                    return "displayCompletions";
                case 6:
                    return "appPrivateCommand";
                case 7:
                    return "toggleSoftInput";
                case 8:
                    return "finishSession";
                case 9:
                    return "updateCursorAnchorInfo";
                case 10:
                    return "notifyImeHidden";
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
            String descriptor = DESCRIPTOR;
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        ExtractedText _arg1;
                        parcel.enforceInterface(descriptor);
                        int _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = (ExtractedText) ExtractedText.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg1 = null;
                        }
                        updateExtractedText(_arg0, _arg1);
                        return true;
                    case 2:
                        parcel.enforceInterface(descriptor);
                        updateSelection(data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt());
                        return true;
                    case 3:
                        parcel.enforceInterface(descriptor);
                        viewClicked(data.readInt() != 0);
                        return true;
                    case 4:
                        Rect _arg02;
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (Rect) Rect.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg02 = null;
                        }
                        updateCursor(_arg02);
                        return true;
                    case 5:
                        parcel.enforceInterface(descriptor);
                        displayCompletions((CompletionInfo[]) parcel.createTypedArray(CompletionInfo.CREATOR));
                        return true;
                    case 6:
                        Bundle _arg12;
                        parcel.enforceInterface(descriptor);
                        String _arg03 = data.readString();
                        if (data.readInt() != 0) {
                            _arg12 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg12 = null;
                        }
                        appPrivateCommand(_arg03, _arg12);
                        return true;
                    case 7:
                        parcel.enforceInterface(descriptor);
                        toggleSoftInput(data.readInt(), data.readInt());
                        return true;
                    case 8:
                        parcel.enforceInterface(descriptor);
                        finishSession();
                        return true;
                    case 9:
                        CursorAnchorInfo _arg04;
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg04 = (CursorAnchorInfo) CursorAnchorInfo.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg04 = null;
                        }
                        updateCursorAnchorInfo(_arg04);
                        return true;
                    case 10:
                        parcel.enforceInterface(descriptor);
                        notifyImeHidden();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IInputMethodSession impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IInputMethodSession getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void appPrivateCommand(String str, Bundle bundle) throws RemoteException;

    void displayCompletions(CompletionInfo[] completionInfoArr) throws RemoteException;

    void finishSession() throws RemoteException;

    void notifyImeHidden() throws RemoteException;

    void toggleSoftInput(int i, int i2) throws RemoteException;

    void updateCursor(Rect rect) throws RemoteException;

    void updateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) throws RemoteException;

    void updateExtractedText(int i, ExtractedText extractedText) throws RemoteException;

    void updateSelection(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void viewClicked(boolean z) throws RemoteException;
}
