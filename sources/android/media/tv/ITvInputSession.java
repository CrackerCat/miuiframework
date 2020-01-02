package android.media.tv;

import android.graphics.Rect;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.Surface;

public interface ITvInputSession extends IInterface {

    public static class Default implements ITvInputSession {
        public void release() throws RemoteException {
        }

        public void setMain(boolean isMain) throws RemoteException {
        }

        public void setSurface(Surface surface) throws RemoteException {
        }

        public void dispatchSurfaceChanged(int format, int width, int height) throws RemoteException {
        }

        public void setVolume(float volume) throws RemoteException {
        }

        public void tune(Uri channelUri, Bundle params) throws RemoteException {
        }

        public void setCaptionEnabled(boolean enabled) throws RemoteException {
        }

        public void selectTrack(int type, String trackId) throws RemoteException {
        }

        public void appPrivateCommand(String action, Bundle data) throws RemoteException {
        }

        public void createOverlayView(IBinder windowToken, Rect frame) throws RemoteException {
        }

        public void relayoutOverlayView(Rect frame) throws RemoteException {
        }

        public void removeOverlayView() throws RemoteException {
        }

        public void unblockContent(String unblockedRating) throws RemoteException {
        }

        public void timeShiftPlay(Uri recordedProgramUri) throws RemoteException {
        }

        public void timeShiftPause() throws RemoteException {
        }

        public void timeShiftResume() throws RemoteException {
        }

        public void timeShiftSeekTo(long timeMs) throws RemoteException {
        }

        public void timeShiftSetPlaybackParams(PlaybackParams params) throws RemoteException {
        }

        public void timeShiftEnablePositionTracking(boolean enable) throws RemoteException {
        }

        public void startRecording(Uri programUri) throws RemoteException {
        }

        public void stopRecording() throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements ITvInputSession {
        private static final String DESCRIPTOR = "android.media.tv.ITvInputSession";
        static final int TRANSACTION_appPrivateCommand = 9;
        static final int TRANSACTION_createOverlayView = 10;
        static final int TRANSACTION_dispatchSurfaceChanged = 4;
        static final int TRANSACTION_relayoutOverlayView = 11;
        static final int TRANSACTION_release = 1;
        static final int TRANSACTION_removeOverlayView = 12;
        static final int TRANSACTION_selectTrack = 8;
        static final int TRANSACTION_setCaptionEnabled = 7;
        static final int TRANSACTION_setMain = 2;
        static final int TRANSACTION_setSurface = 3;
        static final int TRANSACTION_setVolume = 5;
        static final int TRANSACTION_startRecording = 20;
        static final int TRANSACTION_stopRecording = 21;
        static final int TRANSACTION_timeShiftEnablePositionTracking = 19;
        static final int TRANSACTION_timeShiftPause = 15;
        static final int TRANSACTION_timeShiftPlay = 14;
        static final int TRANSACTION_timeShiftResume = 16;
        static final int TRANSACTION_timeShiftSeekTo = 17;
        static final int TRANSACTION_timeShiftSetPlaybackParams = 18;
        static final int TRANSACTION_tune = 6;
        static final int TRANSACTION_unblockContent = 13;

        private static class Proxy implements ITvInputSession {
            public static ITvInputSession sDefaultImpl;
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

            public void release() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().release();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setMain(boolean isMain) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(isMain ? 1 : 0);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setMain(isMain);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setSurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (surface != null) {
                        _data.writeInt(1);
                        surface.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setSurface(surface);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void dispatchSurfaceChanged(int format, int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(format);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    if (this.mRemote.transact(4, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().dispatchSurfaceChanged(format, width, height);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setVolume(float volume) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(volume);
                    if (this.mRemote.transact(5, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setVolume(volume);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void tune(Uri channelUri, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (channelUri != null) {
                        _data.writeInt(1);
                        channelUri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (params != null) {
                        _data.writeInt(1);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(6, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().tune(channelUri, params);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setCaptionEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    if (this.mRemote.transact(7, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setCaptionEnabled(enabled);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void selectTrack(int type, String trackId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(trackId);
                    if (this.mRemote.transact(8, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().selectTrack(type, trackId);
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
                    if (this.mRemote.transact(9, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().appPrivateCommand(action, data);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void createOverlayView(IBinder windowToken, Rect frame) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    if (frame != null) {
                        _data.writeInt(1);
                        frame.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(10, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().createOverlayView(windowToken, frame);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void relayoutOverlayView(Rect frame) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (frame != null) {
                        _data.writeInt(1);
                        frame.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(11, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().relayoutOverlayView(frame);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void removeOverlayView() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(12, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().removeOverlayView();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void unblockContent(String unblockedRating) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(unblockedRating);
                    if (this.mRemote.transact(13, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().unblockContent(unblockedRating);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void timeShiftPlay(Uri recordedProgramUri) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (recordedProgramUri != null) {
                        _data.writeInt(1);
                        recordedProgramUri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(14, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().timeShiftPlay(recordedProgramUri);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void timeShiftPause() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(15, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().timeShiftPause();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void timeShiftResume() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(16, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().timeShiftResume();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void timeShiftSeekTo(long timeMs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(timeMs);
                    if (this.mRemote.transact(17, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().timeShiftSeekTo(timeMs);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void timeShiftSetPlaybackParams(PlaybackParams params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(1);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(18, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().timeShiftSetPlaybackParams(params);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void timeShiftEnablePositionTracking(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    if (this.mRemote.transact(19, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().timeShiftEnablePositionTracking(enable);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void startRecording(Uri programUri) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (programUri != null) {
                        _data.writeInt(1);
                        programUri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(20, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().startRecording(programUri);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void stopRecording() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(21, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().stopRecording();
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITvInputSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ITvInputSession)) {
                return new Proxy(obj);
            }
            return (ITvInputSession) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "release";
                case 2:
                    return "setMain";
                case 3:
                    return "setSurface";
                case 4:
                    return "dispatchSurfaceChanged";
                case 5:
                    return "setVolume";
                case 6:
                    return "tune";
                case 7:
                    return "setCaptionEnabled";
                case 8:
                    return "selectTrack";
                case 9:
                    return "appPrivateCommand";
                case 10:
                    return "createOverlayView";
                case 11:
                    return "relayoutOverlayView";
                case 12:
                    return "removeOverlayView";
                case 13:
                    return "unblockContent";
                case 14:
                    return "timeShiftPlay";
                case 15:
                    return "timeShiftPause";
                case 16:
                    return "timeShiftResume";
                case 17:
                    return "timeShiftSeekTo";
                case 18:
                    return "timeShiftSetPlaybackParams";
                case 19:
                    return "timeShiftEnablePositionTracking";
                case 20:
                    return "startRecording";
                case 21:
                    return "stopRecording";
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
                boolean _arg0 = false;
                Uri _arg02;
                Bundle _arg1;
                switch (code) {
                    case 1:
                        data.enforceInterface(descriptor);
                        release();
                        return true;
                    case 2:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = true;
                        }
                        setMain(_arg0);
                        return true;
                    case 3:
                        Surface _arg03;
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg03 = (Surface) Surface.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        setSurface(_arg03);
                        return true;
                    case 4:
                        data.enforceInterface(descriptor);
                        dispatchSurfaceChanged(data.readInt(), data.readInt(), data.readInt());
                        return true;
                    case 5:
                        data.enforceInterface(descriptor);
                        setVolume(data.readFloat());
                        return true;
                    case 6:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (Uri) Uri.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        if (data.readInt() != 0) {
                            _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        tune(_arg02, _arg1);
                        return true;
                    case 7:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = true;
                        }
                        setCaptionEnabled(_arg0);
                        return true;
                    case 8:
                        data.enforceInterface(descriptor);
                        selectTrack(data.readInt(), data.readString());
                        return true;
                    case 9:
                        data.enforceInterface(descriptor);
                        String _arg04 = data.readString();
                        if (data.readInt() != 0) {
                            _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        appPrivateCommand(_arg04, _arg1);
                        return true;
                    case 10:
                        Rect _arg12;
                        data.enforceInterface(descriptor);
                        IBinder _arg05 = data.readStrongBinder();
                        if (data.readInt() != 0) {
                            _arg12 = (Rect) Rect.CREATOR.createFromParcel(data);
                        } else {
                            _arg12 = null;
                        }
                        createOverlayView(_arg05, _arg12);
                        return true;
                    case 11:
                        Rect _arg06;
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg06 = (Rect) Rect.CREATOR.createFromParcel(data);
                        } else {
                            _arg06 = null;
                        }
                        relayoutOverlayView(_arg06);
                        return true;
                    case 12:
                        data.enforceInterface(descriptor);
                        removeOverlayView();
                        return true;
                    case 13:
                        data.enforceInterface(descriptor);
                        unblockContent(data.readString());
                        return true;
                    case 14:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (Uri) Uri.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        timeShiftPlay(_arg02);
                        return true;
                    case 15:
                        data.enforceInterface(descriptor);
                        timeShiftPause();
                        return true;
                    case 16:
                        data.enforceInterface(descriptor);
                        timeShiftResume();
                        return true;
                    case 17:
                        data.enforceInterface(descriptor);
                        timeShiftSeekTo(data.readLong());
                        return true;
                    case 18:
                        PlaybackParams _arg07;
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg07 = (PlaybackParams) PlaybackParams.CREATOR.createFromParcel(data);
                        } else {
                            _arg07 = null;
                        }
                        timeShiftSetPlaybackParams(_arg07);
                        return true;
                    case 19:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = true;
                        }
                        timeShiftEnablePositionTracking(_arg0);
                        return true;
                    case 20:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (Uri) Uri.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        startRecording(_arg02);
                        return true;
                    case 21:
                        data.enforceInterface(descriptor);
                        stopRecording();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(ITvInputSession impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static ITvInputSession getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void appPrivateCommand(String str, Bundle bundle) throws RemoteException;

    void createOverlayView(IBinder iBinder, Rect rect) throws RemoteException;

    void dispatchSurfaceChanged(int i, int i2, int i3) throws RemoteException;

    void relayoutOverlayView(Rect rect) throws RemoteException;

    void release() throws RemoteException;

    void removeOverlayView() throws RemoteException;

    void selectTrack(int i, String str) throws RemoteException;

    void setCaptionEnabled(boolean z) throws RemoteException;

    void setMain(boolean z) throws RemoteException;

    void setSurface(Surface surface) throws RemoteException;

    void setVolume(float f) throws RemoteException;

    void startRecording(Uri uri) throws RemoteException;

    void stopRecording() throws RemoteException;

    void timeShiftEnablePositionTracking(boolean z) throws RemoteException;

    void timeShiftPause() throws RemoteException;

    void timeShiftPlay(Uri uri) throws RemoteException;

    void timeShiftResume() throws RemoteException;

    void timeShiftSeekTo(long j) throws RemoteException;

    void timeShiftSetPlaybackParams(PlaybackParams playbackParams) throws RemoteException;

    void tune(Uri uri, Bundle bundle) throws RemoteException;

    void unblockContent(String str) throws RemoteException;
}
