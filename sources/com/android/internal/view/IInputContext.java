package com.android.internal.view;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputContentInfo;

public interface IInputContext extends IInterface {

    public static abstract class Stub extends Binder implements IInputContext {
        private static final String DESCRIPTOR = "com.android.internal.view.IInputContext";
        static final int TRANSACTION_beginBatchEdit = 15;
        static final int TRANSACTION_clearMetaKeyStates = 18;
        static final int TRANSACTION_commitCompletion = 10;
        static final int TRANSACTION_commitContent = 23;
        static final int TRANSACTION_commitCorrection = 11;
        static final int TRANSACTION_commitText = 9;
        static final int TRANSACTION_deleteSurroundingText = 5;
        static final int TRANSACTION_deleteSurroundingTextInCodePoints = 6;
        static final int TRANSACTION_endBatchEdit = 16;
        static final int TRANSACTION_finishComposingText = 8;
        static final int TRANSACTION_getCursorCapsMode = 3;
        static final int TRANSACTION_getExtractedText = 4;
        static final int TRANSACTION_getSelectedText = 21;
        static final int TRANSACTION_getTextAfterCursor = 2;
        static final int TRANSACTION_getTextBeforeCursor = 1;
        static final int TRANSACTION_performContextMenuAction = 14;
        static final int TRANSACTION_performEditorAction = 13;
        static final int TRANSACTION_performPrivateCommand = 19;
        static final int TRANSACTION_requestUpdateCursorAnchorInfo = 22;
        static final int TRANSACTION_sendKeyEvent = 17;
        static final int TRANSACTION_setComposingRegion = 20;
        static final int TRANSACTION_setComposingText = 7;
        static final int TRANSACTION_setSelection = 12;

        private static class Proxy implements IInputContext {
            public static IInputContext sDefaultImpl;
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

            public void getTextBeforeCursor(int length, int flags, int seq, IInputContextCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(length);
                    _data.writeInt(flags);
                    _data.writeInt(seq);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().getTextBeforeCursor(length, flags, seq, callback);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void getTextAfterCursor(int length, int flags, int seq, IInputContextCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(length);
                    _data.writeInt(flags);
                    _data.writeInt(seq);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().getTextAfterCursor(length, flags, seq, callback);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void getCursorCapsMode(int reqModes, int seq, IInputContextCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reqModes);
                    _data.writeInt(seq);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().getCursorCapsMode(reqModes, seq, callback);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void getExtractedText(ExtractedTextRequest request, int flags, int seq, IInputContextCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(seq);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(4, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().getExtractedText(request, flags, seq, callback);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void deleteSurroundingText(int beforeLength, int afterLength) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(beforeLength);
                    _data.writeInt(afterLength);
                    if (this.mRemote.transact(5, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().deleteSurroundingText(beforeLength, afterLength);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(beforeLength);
                    _data.writeInt(afterLength);
                    if (this.mRemote.transact(6, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().deleteSurroundingTextInCodePoints(beforeLength, afterLength);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setComposingText(CharSequence text, int newCursorPosition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    if (this.mRemote.transact(7, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setComposingText(text, newCursorPosition);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void finishComposingText() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(8, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().finishComposingText();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void commitText(CharSequence text, int newCursorPosition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    if (this.mRemote.transact(9, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().commitText(text, newCursorPosition);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void commitCompletion(CompletionInfo completion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (completion != null) {
                        _data.writeInt(1);
                        completion.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(10, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().commitCompletion(completion);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void commitCorrection(CorrectionInfo correction) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (correction != null) {
                        _data.writeInt(1);
                        correction.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(11, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().commitCorrection(correction);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setSelection(int start, int end) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    if (this.mRemote.transact(12, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setSelection(start, end);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void performEditorAction(int actionCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(actionCode);
                    if (this.mRemote.transact(13, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().performEditorAction(actionCode);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void performContextMenuAction(int id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    if (this.mRemote.transact(14, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().performContextMenuAction(id);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void beginBatchEdit() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(15, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().beginBatchEdit();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void endBatchEdit() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(16, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().endBatchEdit();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void sendKeyEvent(KeyEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(17, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().sendKeyEvent(event);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void clearMetaKeyStates(int states) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(states);
                    if (this.mRemote.transact(18, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().clearMetaKeyStates(states);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void performPrivateCommand(String action, Bundle data) throws RemoteException {
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
                    if (this.mRemote.transact(19, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().performPrivateCommand(action, data);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setComposingRegion(int start, int end) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    if (this.mRemote.transact(20, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setComposingRegion(start, end);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void getSelectedText(int flags, int seq, IInputContextCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(seq);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(21, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().getSelectedText(flags, seq, callback);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void requestUpdateCursorAnchorInfo(int cursorUpdateMode, int seq, IInputContextCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cursorUpdateMode);
                    _data.writeInt(seq);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(22, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().requestUpdateCursorAnchorInfo(cursorUpdateMode, seq, callback);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts, int sec, IInputContextCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (inputContentInfo != null) {
                        _data.writeInt(1);
                        inputContentInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    if (opts != null) {
                        _data.writeInt(1);
                        opts.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(sec);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(23, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().commitContent(inputContentInfo, flags, opts, sec, callback);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInputContext asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IInputContext)) {
                return new Proxy(obj);
            }
            return (IInputContext) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getTextBeforeCursor";
                case 2:
                    return "getTextAfterCursor";
                case 3:
                    return "getCursorCapsMode";
                case 4:
                    return "getExtractedText";
                case 5:
                    return "deleteSurroundingText";
                case 6:
                    return "deleteSurroundingTextInCodePoints";
                case 7:
                    return "setComposingText";
                case 8:
                    return "finishComposingText";
                case 9:
                    return "commitText";
                case 10:
                    return "commitCompletion";
                case 11:
                    return "commitCorrection";
                case 12:
                    return "setSelection";
                case 13:
                    return "performEditorAction";
                case 14:
                    return "performContextMenuAction";
                case 15:
                    return "beginBatchEdit";
                case 16:
                    return "endBatchEdit";
                case 17:
                    return "sendKeyEvent";
                case 18:
                    return "clearMetaKeyStates";
                case 19:
                    return "performPrivateCommand";
                case 20:
                    return "setComposingRegion";
                case 21:
                    return "getSelectedText";
                case 22:
                    return "requestUpdateCursorAnchorInfo";
                case 23:
                    return "commitContent";
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
                CharSequence _arg0;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(descriptor);
                        getTextBeforeCursor(data.readInt(), data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 2:
                        parcel.enforceInterface(descriptor);
                        getTextAfterCursor(data.readInt(), data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 3:
                        parcel.enforceInterface(descriptor);
                        getCursorCapsMode(data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 4:
                        ExtractedTextRequest _arg02;
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (ExtractedTextRequest) ExtractedTextRequest.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg02 = null;
                        }
                        getExtractedText(_arg02, data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 5:
                        parcel.enforceInterface(descriptor);
                        deleteSurroundingText(data.readInt(), data.readInt());
                        return true;
                    case 6:
                        parcel.enforceInterface(descriptor);
                        deleteSurroundingTextInCodePoints(data.readInt(), data.readInt());
                        return true;
                    case 7:
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
                        } else {
                            _arg0 = null;
                        }
                        setComposingText(_arg0, data.readInt());
                        return true;
                    case 8:
                        parcel.enforceInterface(descriptor);
                        finishComposingText();
                        return true;
                    case 9:
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
                        } else {
                            _arg0 = null;
                        }
                        commitText(_arg0, data.readInt());
                        return true;
                    case 10:
                        CompletionInfo _arg03;
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg03 = (CompletionInfo) CompletionInfo.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg03 = null;
                        }
                        commitCompletion(_arg03);
                        return true;
                    case 11:
                        CorrectionInfo _arg04;
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg04 = (CorrectionInfo) CorrectionInfo.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg04 = null;
                        }
                        commitCorrection(_arg04);
                        return true;
                    case 12:
                        parcel.enforceInterface(descriptor);
                        setSelection(data.readInt(), data.readInt());
                        return true;
                    case 13:
                        parcel.enforceInterface(descriptor);
                        performEditorAction(data.readInt());
                        return true;
                    case 14:
                        parcel.enforceInterface(descriptor);
                        performContextMenuAction(data.readInt());
                        return true;
                    case 15:
                        parcel.enforceInterface(descriptor);
                        beginBatchEdit();
                        return true;
                    case 16:
                        parcel.enforceInterface(descriptor);
                        endBatchEdit();
                        return true;
                    case 17:
                        KeyEvent _arg05;
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg05 = (KeyEvent) KeyEvent.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg05 = null;
                        }
                        sendKeyEvent(_arg05);
                        return true;
                    case 18:
                        parcel.enforceInterface(descriptor);
                        clearMetaKeyStates(data.readInt());
                        return true;
                    case 19:
                        Bundle _arg1;
                        parcel.enforceInterface(descriptor);
                        String _arg06 = data.readString();
                        if (data.readInt() != 0) {
                            _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg1 = null;
                        }
                        performPrivateCommand(_arg06, _arg1);
                        return true;
                    case 20:
                        parcel.enforceInterface(descriptor);
                        setComposingRegion(data.readInt(), data.readInt());
                        return true;
                    case 21:
                        parcel.enforceInterface(descriptor);
                        getSelectedText(data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 22:
                        parcel.enforceInterface(descriptor);
                        requestUpdateCursorAnchorInfo(data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    case 23:
                        InputContentInfo _arg07;
                        Bundle _arg2;
                        parcel.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg07 = (InputContentInfo) InputContentInfo.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg07 = null;
                        }
                        int _arg12 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg2 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg2 = null;
                        }
                        commitContent(_arg07, _arg12, _arg2, data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IInputContext impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IInputContext getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    public static class Default implements IInputContext {
        public void getTextBeforeCursor(int length, int flags, int seq, IInputContextCallback callback) throws RemoteException {
        }

        public void getTextAfterCursor(int length, int flags, int seq, IInputContextCallback callback) throws RemoteException {
        }

        public void getCursorCapsMode(int reqModes, int seq, IInputContextCallback callback) throws RemoteException {
        }

        public void getExtractedText(ExtractedTextRequest request, int flags, int seq, IInputContextCallback callback) throws RemoteException {
        }

        public void deleteSurroundingText(int beforeLength, int afterLength) throws RemoteException {
        }

        public void deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) throws RemoteException {
        }

        public void setComposingText(CharSequence text, int newCursorPosition) throws RemoteException {
        }

        public void finishComposingText() throws RemoteException {
        }

        public void commitText(CharSequence text, int newCursorPosition) throws RemoteException {
        }

        public void commitCompletion(CompletionInfo completion) throws RemoteException {
        }

        public void commitCorrection(CorrectionInfo correction) throws RemoteException {
        }

        public void setSelection(int start, int end) throws RemoteException {
        }

        public void performEditorAction(int actionCode) throws RemoteException {
        }

        public void performContextMenuAction(int id) throws RemoteException {
        }

        public void beginBatchEdit() throws RemoteException {
        }

        public void endBatchEdit() throws RemoteException {
        }

        public void sendKeyEvent(KeyEvent event) throws RemoteException {
        }

        public void clearMetaKeyStates(int states) throws RemoteException {
        }

        public void performPrivateCommand(String action, Bundle data) throws RemoteException {
        }

        public void setComposingRegion(int start, int end) throws RemoteException {
        }

        public void getSelectedText(int flags, int seq, IInputContextCallback callback) throws RemoteException {
        }

        public void requestUpdateCursorAnchorInfo(int cursorUpdateMode, int seq, IInputContextCallback callback) throws RemoteException {
        }

        public void commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts, int sec, IInputContextCallback callback) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    void beginBatchEdit() throws RemoteException;

    void clearMetaKeyStates(int i) throws RemoteException;

    void commitCompletion(CompletionInfo completionInfo) throws RemoteException;

    void commitContent(InputContentInfo inputContentInfo, int i, Bundle bundle, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void commitCorrection(CorrectionInfo correctionInfo) throws RemoteException;

    void commitText(CharSequence charSequence, int i) throws RemoteException;

    void deleteSurroundingText(int i, int i2) throws RemoteException;

    void deleteSurroundingTextInCodePoints(int i, int i2) throws RemoteException;

    void endBatchEdit() throws RemoteException;

    void finishComposingText() throws RemoteException;

    void getCursorCapsMode(int i, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void getExtractedText(ExtractedTextRequest extractedTextRequest, int i, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void getSelectedText(int i, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void getTextAfterCursor(int i, int i2, int i3, IInputContextCallback iInputContextCallback) throws RemoteException;

    void getTextBeforeCursor(int i, int i2, int i3, IInputContextCallback iInputContextCallback) throws RemoteException;

    void performContextMenuAction(int i) throws RemoteException;

    void performEditorAction(int i) throws RemoteException;

    void performPrivateCommand(String str, Bundle bundle) throws RemoteException;

    void requestUpdateCursorAnchorInfo(int i, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void sendKeyEvent(KeyEvent keyEvent) throws RemoteException;

    void setComposingRegion(int i, int i2) throws RemoteException;

    void setComposingText(CharSequence charSequence, int i) throws RemoteException;

    void setSelection(int i, int i2) throws RemoteException;
}
