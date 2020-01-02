package android.os.image;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelableException;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Slog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

@SystemApi
public class DynamicSystemClient {
    public static final String ACTION_NOTIFY_IF_IN_USE = "android.os.image.action.NOTIFY_IF_IN_USE";
    public static final String ACTION_START_INSTALL = "android.os.image.action.START_INSTALL";
    public static final int CAUSE_ERROR_EXCEPTION = 6;
    public static final int CAUSE_ERROR_INVALID_URL = 4;
    public static final int CAUSE_ERROR_IO = 3;
    public static final int CAUSE_ERROR_IPC = 5;
    public static final int CAUSE_INSTALL_CANCELLED = 2;
    public static final int CAUSE_INSTALL_COMPLETED = 1;
    public static final int CAUSE_NOT_SPECIFIED = 0;
    private static final long DEFAULT_USERDATA_SIZE = 10737418240L;
    public static final String KEY_EXCEPTION_DETAIL = "KEY_EXCEPTION_DETAIL";
    public static final String KEY_INSTALLED_SIZE = "KEY_INSTALLED_SIZE";
    public static final String KEY_SYSTEM_SIZE = "KEY_SYSTEM_SIZE";
    public static final String KEY_USERDATA_SIZE = "KEY_USERDATA_SIZE";
    public static final int MSG_POST_STATUS = 3;
    public static final int MSG_REGISTER_LISTENER = 1;
    public static final int MSG_UNREGISTER_LISTENER = 2;
    public static final int STATUS_IN_PROGRESS = 2;
    public static final int STATUS_IN_USE = 4;
    public static final int STATUS_NOT_STARTED = 1;
    public static final int STATUS_READY = 3;
    public static final int STATUS_UNKNOWN = 0;
    private static final String TAG = "DynSystemClient";
    private boolean mBound;
    private final DynSystemServiceConnection mConnection = new DynSystemServiceConnection();
    private final Context mContext;
    private Executor mExecutor;
    private OnStatusChangedListener mListener;
    private final Messenger mMessenger = new Messenger(new IncomingHandler(this));
    private Messenger mService;

    private class DynSystemServiceConnection implements ServiceConnection {
        private DynSystemServiceConnection() {
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            String str = DynamicSystemClient.TAG;
            Slog.v(str, "DynSystemService connected");
            DynamicSystemClient.this.mService = new Messenger(service);
            try {
                Message msg = Message.obtain((Handler) null, 1);
                msg.replyTo = DynamicSystemClient.this.mMessenger;
                DynamicSystemClient.this.mService.send(msg);
            } catch (RemoteException e) {
                Slog.e(str, "Unable to get status from installation service");
                DynamicSystemClient.this.mExecutor.execute(new -$$Lambda$DynamicSystemClient$DynSystemServiceConnection$Q-VWaYUew87mkpsE47b33p5XLa8(this, e));
            }
        }

        public /* synthetic */ void lambda$onServiceConnected$0$DynamicSystemClient$DynSystemServiceConnection(RemoteException e) {
            DynamicSystemClient.this.mListener.onStatusChanged(0, 5, 0, e);
        }

        public void onServiceDisconnected(ComponentName className) {
            Slog.v(DynamicSystemClient.TAG, "DynSystemService disconnected");
            DynamicSystemClient.this.mService = null;
        }
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<DynamicSystemClient> mWeakClient;

        IncomingHandler(DynamicSystemClient service) {
            super(Looper.getMainLooper());
            this.mWeakClient = new WeakReference(service);
        }

        public void handleMessage(Message msg) {
            DynamicSystemClient service = (DynamicSystemClient) this.mWeakClient.get();
            if (service != null) {
                service.handleMessage(msg);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface InstallationStatus {
    }

    public interface OnStatusChangedListener {
        void onStatusChanged(int i, int i2, long j, Throwable th);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusChangedCause {
    }

    @SystemApi
    public DynamicSystemClient(Context context) {
        this.mContext = context;
    }

    public void setOnStatusChangedListener(Executor executor, OnStatusChangedListener listener) {
        this.mListener = listener;
        this.mExecutor = executor;
    }

    public void setOnStatusChangedListener(OnStatusChangedListener listener) {
        this.mListener = listener;
        this.mExecutor = null;
    }

    @SystemApi
    public void bind() {
        if (featureFlagEnabled()) {
            Intent intent = new Intent();
            intent.setClassName("com.android.dynsystem", "com.android.dynsystem.DynamicSystemInstallationService");
            this.mContext.bindService(intent, this.mConnection, 1);
            this.mBound = true;
            return;
        }
        Slog.w(TAG, "settings_dynamic_system not enabled; bind() aborted.");
    }

    @SystemApi
    public void unbind() {
        if (this.mBound) {
            if (this.mService != null) {
                try {
                    Message msg = Message.obtain((Handler) null, 2);
                    msg.replyTo = this.mMessenger;
                    this.mService.send(msg);
                } catch (RemoteException e) {
                    Slog.e(TAG, "Unable to unregister from installation service");
                }
            }
            this.mContext.unbindService(this.mConnection);
            this.mBound = false;
        }
    }

    @SystemApi
    public void start(Uri systemUrl, long systemSize) {
        start(systemUrl, systemSize, DEFAULT_USERDATA_SIZE);
    }

    public void start(Uri systemUrl, long systemSize, long userdataSize) {
        if (featureFlagEnabled()) {
            Intent intent = new Intent();
            intent.setClassName("com.android.dynsystem", "com.android.dynsystem.VerificationActivity");
            intent.setData(systemUrl);
            intent.setAction(ACTION_START_INSTALL);
            intent.putExtra(KEY_SYSTEM_SIZE, systemSize);
            intent.putExtra(KEY_USERDATA_SIZE, userdataSize);
            this.mContext.startActivity(intent);
            return;
        }
        Slog.w(TAG, "settings_dynamic_system not enabled; start() aborted.");
    }

    private boolean featureFlagEnabled() {
        return SystemProperties.getBoolean("persist.sys.fflag.override.settings_dynamic_system", false);
    }

    private void handleMessage(Message msg) {
        Message message = msg;
        if (message.what == 3) {
            int status = message.arg1;
            int cause = message.arg2;
            Bundle bundle = message.obj;
            long progress = bundle.getLong(KEY_INSTALLED_SIZE);
            ParcelableException t = (ParcelableException) bundle.getSerializable(KEY_EXCEPTION_DETAIL);
            Throwable detail = t == null ? null : t.getCause();
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(new -$$Lambda$DynamicSystemClient$j9BjPR3q6kOr-cwQrk0KAsVFWNQ(this, status, cause, progress, detail));
                return;
            }
            this.mListener.onStatusChanged(status, cause, progress, detail);
        }
    }

    public /* synthetic */ void lambda$handleMessage$0$DynamicSystemClient(int status, int cause, long progress, Throwable detail) {
        this.mListener.onStatusChanged(status, cause, progress, detail);
    }
}
