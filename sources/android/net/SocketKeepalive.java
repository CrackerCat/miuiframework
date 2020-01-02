package android.net;

import android.net.ISocketKeepaliveCallback.Stub;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;

public abstract class SocketKeepalive implements AutoCloseable {
    public static final int BINDER_DIED = -10;
    public static final int DATA_RECEIVED = -2;
    public static final int ERROR_HARDWARE_ERROR = -31;
    public static final int ERROR_HARDWARE_UNSUPPORTED = -30;
    public static final int ERROR_INSUFFICIENT_RESOURCES = -32;
    public static final int ERROR_INVALID_INTERVAL = -24;
    public static final int ERROR_INVALID_IP_ADDRESS = -21;
    public static final int ERROR_INVALID_LENGTH = -23;
    public static final int ERROR_INVALID_NETWORK = -20;
    public static final int ERROR_INVALID_PORT = -22;
    public static final int ERROR_INVALID_SOCKET = -25;
    public static final int ERROR_SOCKET_NOT_IDLE = -26;
    public static final int ERROR_UNSUPPORTED = -30;
    public static final int MAX_INTERVAL_SEC = 3600;
    public static final int MIN_INTERVAL_SEC = 10;
    public static final int NO_KEEPALIVE = -1;
    public static final int SUCCESS = 0;
    static final String TAG = "SocketKeepalive";
    final ISocketKeepaliveCallback mCallback;
    final Executor mExecutor;
    final Network mNetwork;
    final ParcelFileDescriptor mPfd;
    final IConnectivityManager mService;
    Integer mSlot;

    public static class Callback {
        public void onStarted() {
        }

        public void onStopped() {
        }

        public void onError(int error) {
        }

        public void onDataReceived() {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorCode {
    }

    public static class ErrorCodeException extends Exception {
        public final int error;

        public ErrorCodeException(int error, Throwable e) {
            super(e);
            this.error = error;
        }

        public ErrorCodeException(int error) {
            this.error = error;
        }
    }

    public static class InvalidPacketException extends ErrorCodeException {
        public InvalidPacketException(int error) {
            super(error);
        }
    }

    public static class InvalidSocketException extends ErrorCodeException {
        public InvalidSocketException(int error, Throwable e) {
            super(error, e);
        }

        public InvalidSocketException(int error) {
            super(error);
        }
    }

    public abstract void startImpl(int i);

    public abstract void stopImpl();

    SocketKeepalive(IConnectivityManager service, Network network, ParcelFileDescriptor pfd, final Executor executor, final Callback callback) {
        this.mService = service;
        this.mNetwork = network;
        this.mPfd = pfd;
        this.mExecutor = executor;
        this.mCallback = new Stub() {
            public void onStarted(int slot) {
                Binder.withCleanCallingIdentity(new -$$Lambda$SocketKeepalive$1$m-VPtyb2YaC8aWd5gXQYgFGhVbM(this, slot, callback));
            }

            public /* synthetic */ void lambda$onStarted$1$SocketKeepalive$1(int slot, Callback callback) throws Exception {
                SocketKeepalive.this.mExecutor.execute(new -$$Lambda$SocketKeepalive$1$nDWCSiqzvu6z8lptsLq-qY42hTk(this, slot, callback));
            }

            public /* synthetic */ void lambda$onStarted$0$SocketKeepalive$1(int slot, Callback callback) {
                SocketKeepalive.this.mSlot = Integer.valueOf(slot);
                callback.onStarted();
            }

            public void onStopped() {
                Binder.withCleanCallingIdentity(new -$$Lambda$SocketKeepalive$1$GQbcC2yhPzv5xknkQV01K3_QTNA(this, executor, callback));
            }

            public /* synthetic */ void lambda$onStopped$3$SocketKeepalive$1(Executor executor, Callback callback) throws Exception {
                executor.execute(new -$$Lambda$SocketKeepalive$1$Ghy-awbQuJd8C-GZAjeZCXMiaUw(this, callback));
            }

            public /* synthetic */ void lambda$onStopped$2$SocketKeepalive$1(Callback callback) {
                SocketKeepalive.this.mSlot = null;
                callback.onStopped();
            }

            public void onError(int error) {
                Binder.withCleanCallingIdentity(new -$$Lambda$SocketKeepalive$1$0jK7H49vYYFjBANIXTac00ocnSo(this, executor, callback, error));
            }

            public /* synthetic */ void lambda$onError$5$SocketKeepalive$1(Executor executor, Callback callback, int error) throws Exception {
                executor.execute(new -$$Lambda$SocketKeepalive$1$xxwNi85oVXVQ_ILhrZNWwo4ppA8(this, callback, error));
            }

            public /* synthetic */ void lambda$onError$4$SocketKeepalive$1(Callback callback, int error) {
                SocketKeepalive.this.mSlot = null;
                callback.onError(error);
            }

            public void onDataReceived() {
                Binder.withCleanCallingIdentity(new -$$Lambda$SocketKeepalive$1$nPQMIWzmX3WEJCjp1qnz_O7qaxs(this, executor, callback));
            }

            public /* synthetic */ void lambda$onDataReceived$7$SocketKeepalive$1(Executor executor, Callback callback) throws Exception {
                executor.execute(new -$$Lambda$SocketKeepalive$1$yVvEaumPDc_celEzvlSEH2FU0nc(this, callback));
            }

            public /* synthetic */ void lambda$onDataReceived$6$SocketKeepalive$1(Callback callback) {
                SocketKeepalive.this.mSlot = null;
                callback.onDataReceived();
            }
        };
    }

    public final void start(int intervalSec) {
        startImpl(intervalSec);
    }

    public final void stop() {
        stopImpl();
    }

    public final void close() {
        stop();
        try {
            this.mPfd.close();
        } catch (IOException e) {
        }
    }
}
