package android.service.trust;

import android.Manifest.permission;
import android.annotation.SystemApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.trust.ITrustAgentService.Stub;
import android.util.Log;
import android.util.Slog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@SystemApi
public class TrustAgentService extends Service {
    private static final boolean DEBUG = false;
    private static final String EXTRA_TOKEN = "token";
    private static final String EXTRA_TOKEN_HANDLE = "token_handle";
    private static final String EXTRA_TOKEN_REMOVED_RESULT = "token_removed_result";
    private static final String EXTRA_TOKEN_STATE = "token_state";
    private static final String EXTRA_USER_HANDLE = "user_handle";
    public static final int FLAG_GRANT_TRUST_DISMISS_KEYGUARD = 2;
    public static final int FLAG_GRANT_TRUST_INITIATED_BY_USER = 1;
    private static final int MSG_CONFIGURE = 2;
    private static final int MSG_DEVICE_LOCKED = 4;
    private static final int MSG_DEVICE_UNLOCKED = 5;
    private static final int MSG_ESCROW_TOKEN_ADDED = 7;
    private static final int MSG_ESCROW_TOKEN_REMOVED = 9;
    private static final int MSG_ESCROW_TOKEN_STATE_RECEIVED = 8;
    private static final int MSG_TRUST_TIMEOUT = 3;
    private static final int MSG_UNLOCK_ATTEMPT = 1;
    private static final int MSG_UNLOCK_LOCKOUT = 6;
    public static final String SERVICE_INTERFACE = "android.service.trust.TrustAgentService";
    public static final int TOKEN_STATE_ACTIVE = 1;
    public static final int TOKEN_STATE_INACTIVE = 0;
    public static final String TRUST_AGENT_META_DATA = "android.service.trust.trustagent";
    private final String TAG;
    private ITrustAgentServiceCallback mCallback;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            boolean z = false;
            Bundle data;
            switch (msg.what) {
                case 1:
                    TrustAgentService trustAgentService = TrustAgentService.this;
                    if (msg.arg1 != 0) {
                        z = true;
                    }
                    trustAgentService.onUnlockAttempt(z);
                    return;
                case 2:
                    ConfigurationData data2 = msg.obj;
                    z = TrustAgentService.this.onConfigure(data2.options);
                    if (data2.token != null) {
                        try {
                            synchronized (TrustAgentService.this.mLock) {
                                TrustAgentService.this.mCallback.onConfigureCompleted(z, data2.token);
                            }
                            return;
                        } catch (RemoteException e) {
                            TrustAgentService.this.onError("calling onSetTrustAgentFeaturesEnabledCompleted()");
                            return;
                        }
                    }
                    return;
                case 3:
                    TrustAgentService.this.onTrustTimeout();
                    return;
                case 4:
                    TrustAgentService.this.onDeviceLocked();
                    return;
                case 5:
                    TrustAgentService.this.onDeviceUnlocked();
                    return;
                case 6:
                    TrustAgentService.this.onDeviceUnlockLockout((long) msg.arg1);
                    return;
                case 7:
                    data = msg.getData();
                    TrustAgentService.this.onEscrowTokenAdded(data.getByteArray("token"), data.getLong(TrustAgentService.EXTRA_TOKEN_HANDLE), (UserHandle) data.getParcelable(TrustAgentService.EXTRA_USER_HANDLE));
                    return;
                case 8:
                    data = msg.getData();
                    TrustAgentService.this.onEscrowTokenStateReceived(data.getLong(TrustAgentService.EXTRA_TOKEN_HANDLE), data.getInt(TrustAgentService.EXTRA_TOKEN_STATE, 0));
                    return;
                case 9:
                    data = msg.getData();
                    TrustAgentService.this.onEscrowTokenRemoved(data.getLong(TrustAgentService.EXTRA_TOKEN_HANDLE), data.getBoolean(TrustAgentService.EXTRA_TOKEN_REMOVED_RESULT));
                    return;
                default:
                    return;
            }
        }
    };
    private final Object mLock = new Object();
    private boolean mManagingTrust;
    private Runnable mPendingGrantTrustTask;

    private static final class ConfigurationData {
        final List<PersistableBundle> options;
        final IBinder token;

        ConfigurationData(List<PersistableBundle> opts, IBinder t) {
            this.options = opts;
            this.token = t;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface GrantTrustFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TokenState {
    }

    private final class TrustAgentServiceWrapper extends Stub {
        private TrustAgentServiceWrapper() {
        }

        /* synthetic */ TrustAgentServiceWrapper(TrustAgentService x0, AnonymousClass1 x1) {
            this();
        }

        public void onUnlockAttempt(boolean successful) {
            TrustAgentService.this.mHandler.obtainMessage(1, successful, 0).sendToTarget();
        }

        public void onUnlockLockout(int timeoutMs) {
            TrustAgentService.this.mHandler.obtainMessage(6, timeoutMs, 0).sendToTarget();
        }

        public void onTrustTimeout() {
            TrustAgentService.this.mHandler.sendEmptyMessage(3);
        }

        public void onConfigure(List<PersistableBundle> args, IBinder token) {
            TrustAgentService.this.mHandler.obtainMessage(2, new ConfigurationData(args, token)).sendToTarget();
        }

        public void onDeviceLocked() throws RemoteException {
            TrustAgentService.this.mHandler.obtainMessage(4).sendToTarget();
        }

        public void onDeviceUnlocked() throws RemoteException {
            TrustAgentService.this.mHandler.obtainMessage(5).sendToTarget();
        }

        public void setCallback(ITrustAgentServiceCallback callback) {
            synchronized (TrustAgentService.this.mLock) {
                TrustAgentService.this.mCallback = callback;
                if (TrustAgentService.this.mManagingTrust) {
                    try {
                        TrustAgentService.this.mCallback.setManagingTrust(TrustAgentService.this.mManagingTrust);
                    } catch (RemoteException e) {
                        TrustAgentService.this.onError("calling setManagingTrust()");
                    }
                }
                if (TrustAgentService.this.mPendingGrantTrustTask != null) {
                    TrustAgentService.this.mPendingGrantTrustTask.run();
                    TrustAgentService.this.mPendingGrantTrustTask = null;
                }
            }
        }

        public void onEscrowTokenAdded(byte[] token, long handle, UserHandle user) {
            Message msg = TrustAgentService.this.mHandler.obtainMessage(7);
            msg.getData().putByteArray("token", token);
            msg.getData().putLong(TrustAgentService.EXTRA_TOKEN_HANDLE, handle);
            msg.getData().putParcelable(TrustAgentService.EXTRA_USER_HANDLE, user);
            msg.sendToTarget();
        }

        public void onTokenStateReceived(long handle, int tokenState) {
            Message msg = TrustAgentService.this.mHandler.obtainMessage(8);
            msg.getData().putLong(TrustAgentService.EXTRA_TOKEN_HANDLE, handle);
            msg.getData().putInt(TrustAgentService.EXTRA_TOKEN_STATE, tokenState);
            msg.sendToTarget();
        }

        public void onEscrowTokenRemoved(long handle, boolean successful) {
            Message msg = TrustAgentService.this.mHandler.obtainMessage(9);
            msg.getData().putLong(TrustAgentService.EXTRA_TOKEN_HANDLE, handle);
            msg.getData().putBoolean(TrustAgentService.EXTRA_TOKEN_REMOVED_RESULT, successful);
            msg.sendToTarget();
        }
    }

    public TrustAgentService() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TrustAgentService.class.getSimpleName());
        stringBuilder.append("[");
        stringBuilder.append(getClass().getSimpleName());
        stringBuilder.append("]");
        this.TAG = stringBuilder.toString();
    }

    public void onCreate() {
        String str = permission.BIND_TRUST_AGENT;
        super.onCreate();
        ComponentName component = new ComponentName((Context) this, getClass());
        try {
            if (!str.equals(getPackageManager().getServiceInfo(component, 0).permission)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(component.flattenToShortString());
                stringBuilder.append(" is not declared with the permission \"");
                stringBuilder.append(str);
                stringBuilder.append("\"");
                throw new IllegalStateException(stringBuilder.toString());
            }
        } catch (NameNotFoundException e) {
            String str2 = this.TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Can't get ServiceInfo for ");
            stringBuilder2.append(component.toShortString());
            Log.e(str2, stringBuilder2.toString());
        }
    }

    public void onUnlockAttempt(boolean successful) {
    }

    public void onTrustTimeout() {
    }

    public void onDeviceLocked() {
    }

    public void onDeviceUnlocked() {
    }

    public void onDeviceUnlockLockout(long timeoutMs) {
    }

    public void onEscrowTokenAdded(byte[] token, long handle, UserHandle user) {
    }

    public void onEscrowTokenStateReceived(long handle, int tokenState) {
    }

    public void onEscrowTokenRemoved(long handle, boolean successful) {
    }

    private void onError(String msg) {
        String str = this.TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Remote exception while ");
        stringBuilder.append(msg);
        Slog.v(str, stringBuilder.toString());
    }

    public boolean onConfigure(List<PersistableBundle> list) {
        return false;
    }

    @Deprecated
    public final void grantTrust(CharSequence message, long durationMs, boolean initiatedByUser) {
        grantTrust(message, durationMs, (int) initiatedByUser);
    }

    public final void grantTrust(CharSequence message, long durationMs, int flags) {
        synchronized (this.mLock) {
            if (this.mManagingTrust) {
                if (this.mCallback != null) {
                    try {
                        this.mCallback.grantTrust(message.toString(), durationMs, flags);
                    } catch (RemoteException e) {
                        onError("calling enableTrust()");
                    }
                } else {
                    final CharSequence charSequence = message;
                    final long j = durationMs;
                    final int i = flags;
                    this.mPendingGrantTrustTask = new Runnable() {
                        public void run() {
                            TrustAgentService.this.grantTrust(charSequence, j, i);
                        }
                    };
                }
            } else {
                throw new IllegalStateException("Cannot grant trust if agent is not managing trust. Call setManagingTrust(true) first.");
            }
        }
    }

    public final void revokeTrust() {
        synchronized (this.mLock) {
            if (this.mPendingGrantTrustTask != null) {
                this.mPendingGrantTrustTask = null;
            }
            if (this.mCallback != null) {
                try {
                    this.mCallback.revokeTrust();
                } catch (RemoteException e) {
                    onError("calling revokeTrust()");
                }
            }
        }
    }

    public final void setManagingTrust(boolean managingTrust) {
        synchronized (this.mLock) {
            if (this.mManagingTrust != managingTrust) {
                this.mManagingTrust = managingTrust;
                if (this.mCallback != null) {
                    try {
                        this.mCallback.setManagingTrust(managingTrust);
                    } catch (RemoteException e) {
                        onError("calling setManagingTrust()");
                    }
                }
            }
        }
    }

    public final void addEscrowToken(byte[] token, UserHandle user) {
        synchronized (this.mLock) {
            if (this.mCallback != null) {
                try {
                    this.mCallback.addEscrowToken(token, user.getIdentifier());
                } catch (RemoteException e) {
                    onError("calling addEscrowToken");
                }
            } else {
                Slog.w(this.TAG, "Cannot add escrow token if the agent is not connecting to framework");
                throw new IllegalStateException("Trust agent is not connected");
            }
        }
    }

    public final void isEscrowTokenActive(long handle, UserHandle user) {
        synchronized (this.mLock) {
            if (this.mCallback != null) {
                try {
                    this.mCallback.isEscrowTokenActive(handle, user.getIdentifier());
                } catch (RemoteException e) {
                    onError("calling isEscrowTokenActive");
                }
            } else {
                Slog.w(this.TAG, "Cannot add escrow token if the agent is not connecting to framework");
                throw new IllegalStateException("Trust agent is not connected");
            }
        }
    }

    public final void removeEscrowToken(long handle, UserHandle user) {
        synchronized (this.mLock) {
            if (this.mCallback != null) {
                try {
                    this.mCallback.removeEscrowToken(handle, user.getIdentifier());
                } catch (RemoteException e) {
                    onError("callling removeEscrowToken");
                }
            } else {
                Slog.w(this.TAG, "Cannot add escrow token if the agent is not connecting to framework");
                throw new IllegalStateException("Trust agent is not connected");
            }
        }
    }

    public final void unlockUserWithToken(long handle, byte[] token, UserHandle user) {
        if (((UserManager) getSystemService("user")).isUserUnlocked(user)) {
            Slog.i(this.TAG, "User already unlocked");
            return;
        }
        synchronized (this.mLock) {
            if (this.mCallback != null) {
                try {
                    this.mCallback.unlockUserWithToken(handle, token, user.getIdentifier());
                } catch (RemoteException e) {
                    onError("calling unlockUserWithToken");
                }
            } else {
                Slog.w(this.TAG, "Cannot add escrow token if the agent is not connecting to framework");
                throw new IllegalStateException("Trust agent is not connected");
            }
        }
    }

    public final void showKeyguardErrorMessage(CharSequence message) {
        if (message != null) {
            synchronized (this.mLock) {
                if (this.mCallback != null) {
                    try {
                        this.mCallback.showKeyguardErrorMessage(message);
                    } catch (RemoteException e) {
                        onError("calling showKeyguardErrorMessage");
                    }
                } else {
                    Slog.w(this.TAG, "Cannot show message because service is not connected to framework.");
                    throw new IllegalStateException("Trust agent is not connected");
                }
            }
            return;
        }
        throw new IllegalArgumentException("message cannot be null");
    }

    public final IBinder onBind(Intent intent) {
        return new TrustAgentServiceWrapper(this, null);
    }
}
