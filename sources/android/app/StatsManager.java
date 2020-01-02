package android.app;

import android.annotation.SystemApi;
import android.content.Context;
import android.os.IBinder.DeathRecipient;
import android.os.IStatsManager;
import android.os.IStatsManager.Stub;
import android.os.IStatsPullerCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.AndroidException;
import android.util.Slog;

@SystemApi
public final class StatsManager {
    public static final String ACTION_STATSD_STARTED = "android.app.action.STATSD_STARTED";
    private static final boolean DEBUG = false;
    public static final String EXTRA_STATS_ACTIVE_CONFIG_KEYS = "android.app.extra.STATS_ACTIVE_CONFIG_KEYS";
    public static final String EXTRA_STATS_BROADCAST_SUBSCRIBER_COOKIES = "android.app.extra.STATS_BROADCAST_SUBSCRIBER_COOKIES";
    public static final String EXTRA_STATS_CONFIG_KEY = "android.app.extra.STATS_CONFIG_KEY";
    public static final String EXTRA_STATS_CONFIG_UID = "android.app.extra.STATS_CONFIG_UID";
    public static final String EXTRA_STATS_DIMENSIONS_VALUE = "android.app.extra.STATS_DIMENSIONS_VALUE";
    public static final String EXTRA_STATS_SUBSCRIPTION_ID = "android.app.extra.STATS_SUBSCRIPTION_ID";
    public static final String EXTRA_STATS_SUBSCRIPTION_RULE_ID = "android.app.extra.STATS_SUBSCRIPTION_RULE_ID";
    private static final String TAG = "StatsManager";
    private final Context mContext;
    private IStatsManager mService;

    public static class StatsUnavailableException extends AndroidException {
        public StatsUnavailableException(String reason) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to connect to statsd: ");
            stringBuilder.append(reason);
            super(stringBuilder.toString());
        }

        public StatsUnavailableException(String reason, Throwable e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to connect to statsd: ");
            stringBuilder.append(reason);
            super(stringBuilder.toString(), e);
        }
    }

    private class StatsdDeathRecipient implements DeathRecipient {
        private StatsdDeathRecipient() {
        }

        public void binderDied() {
            synchronized (this) {
                StatsManager.this.mService = null;
            }
        }
    }

    public StatsManager(Context context) {
        this.mContext = context;
    }

    public void addConfig(long configKey, byte[] config) throws StatsUnavailableException {
        synchronized (this) {
            try {
                getIStatsManagerLocked().addConfiguration(configKey, config, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to connect to statsd when adding configuration");
                throw new StatsUnavailableException("could not connect", e);
            } catch (SecurityException e2) {
                throw new StatsUnavailableException(e2.getMessage(), e2);
            } catch (Throwable th) {
            }
        }
    }

    @Deprecated
    public boolean addConfiguration(long configKey, byte[] config) {
        try {
            addConfig(configKey, config);
            return true;
        } catch (StatsUnavailableException | IllegalArgumentException e) {
            return false;
        }
    }

    public void removeConfig(long configKey) throws StatsUnavailableException {
        synchronized (this) {
            try {
                getIStatsManagerLocked().removeConfiguration(configKey, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to connect to statsd when removing configuration");
                throw new StatsUnavailableException("could not connect", e);
            } catch (SecurityException e2) {
                throw new StatsUnavailableException(e2.getMessage(), e2);
            } catch (Throwable th) {
            }
        }
    }

    @Deprecated
    public boolean removeConfiguration(long configKey) {
        try {
            removeConfig(configKey);
            return true;
        } catch (StatsUnavailableException e) {
            return false;
        }
    }

    public void setBroadcastSubscriber(PendingIntent pendingIntent, long configKey, long subscriberId) throws StatsUnavailableException {
        synchronized (this) {
            try {
                IStatsManager service = getIStatsManagerLocked();
                if (pendingIntent != null) {
                    service.setBroadcastSubscriber(configKey, subscriberId, pendingIntent.getTarget().asBinder(), this.mContext.getOpPackageName());
                } else {
                    service.unsetBroadcastSubscriber(configKey, subscriberId, this.mContext.getOpPackageName());
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to connect to statsd when adding broadcast subscriber", e);
                throw new StatsUnavailableException("could not connect", e);
            } catch (SecurityException e2) {
                throw new StatsUnavailableException(e2.getMessage(), e2);
            } catch (Throwable th) {
            }
        }
    }

    @Deprecated
    public boolean setBroadcastSubscriber(long configKey, long subscriberId, PendingIntent pendingIntent) {
        try {
            setBroadcastSubscriber(pendingIntent, configKey, subscriberId);
            return true;
        } catch (StatsUnavailableException e) {
            return false;
        }
    }

    public void setFetchReportsOperation(PendingIntent pendingIntent, long configKey) throws StatsUnavailableException {
        synchronized (this) {
            try {
                IStatsManager service = getIStatsManagerLocked();
                if (pendingIntent == null) {
                    service.removeDataFetchOperation(configKey, this.mContext.getOpPackageName());
                } else {
                    service.setDataFetchOperation(configKey, pendingIntent.getTarget().asBinder(), this.mContext.getOpPackageName());
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to connect to statsd when registering data listener.");
                throw new StatsUnavailableException("could not connect", e);
            } catch (SecurityException e2) {
                throw new StatsUnavailableException(e2.getMessage(), e2);
            } catch (Throwable th) {
            }
        }
    }

    public long[] setActiveConfigsChangedOperation(PendingIntent pendingIntent) throws StatsUnavailableException {
        synchronized (this) {
            try {
                IStatsManager service = getIStatsManagerLocked();
                if (pendingIntent == null) {
                    service.removeActiveConfigsChangedOperation(this.mContext.getOpPackageName());
                    long[] jArr = new long[0];
                    return jArr;
                }
                long[] activeConfigsChangedOperation = service.setActiveConfigsChangedOperation(pendingIntent.getTarget().asBinder(), this.mContext.getOpPackageName());
                return activeConfigsChangedOperation;
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to connect to statsd when registering active configs listener.");
                throw new StatsUnavailableException("could not connect", e);
            } catch (SecurityException e2) {
                throw new StatsUnavailableException(e2.getMessage(), e2);
            } catch (Throwable th) {
            }
        }
    }

    @Deprecated
    public boolean setDataFetchOperation(long configKey, PendingIntent pendingIntent) {
        try {
            setFetchReportsOperation(pendingIntent, configKey);
            return true;
        } catch (StatsUnavailableException e) {
            return false;
        }
    }

    public byte[] getReports(long configKey) throws StatsUnavailableException {
        byte[] data;
        synchronized (this) {
            try {
                data = getIStatsManagerLocked().getData(configKey, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to connect to statsd when getting data");
                throw new StatsUnavailableException("could not connect", e);
            } catch (SecurityException e2) {
                throw new StatsUnavailableException(e2.getMessage(), e2);
            } catch (Throwable th) {
            }
        }
        return data;
    }

    @Deprecated
    public byte[] getData(long configKey) {
        try {
            return getReports(configKey);
        } catch (StatsUnavailableException e) {
            return null;
        }
    }

    public byte[] getStatsMetadata() throws StatsUnavailableException {
        byte[] metadata;
        synchronized (this) {
            try {
                metadata = getIStatsManagerLocked().getMetadata(this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to connect to statsd when getting metadata");
                throw new StatsUnavailableException("could not connect", e);
            } catch (SecurityException e2) {
                throw new StatsUnavailableException(e2.getMessage(), e2);
            } catch (Throwable th) {
            }
        }
        return metadata;
    }

    @Deprecated
    public byte[] getMetadata() {
        try {
            return getStatsMetadata();
        } catch (StatsUnavailableException e) {
            return null;
        }
    }

    public long[] getRegisteredExperimentIds() throws StatsUnavailableException {
        synchronized (this) {
            int i = 0;
            try {
                IStatsManager service = getIStatsManagerLocked();
                if (service == null) {
                    long[] jArr = new long[0];
                    return jArr;
                }
                i = service.getRegisteredExperimentIds();
                return i;
            } catch (RemoteException e) {
                return new long[i];
            } catch (Throwable th) {
            }
        }
    }

    public void setPullerCallback(int atomTag, IStatsPullerCallback callback) throws StatsUnavailableException {
        synchronized (this) {
            try {
                IStatsManager service = getIStatsManagerLocked();
                if (callback == null) {
                    service.unregisterPullerCallback(atomTag, this.mContext.getOpPackageName());
                } else {
                    service.registerPullerCallback(atomTag, callback, this.mContext.getOpPackageName());
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to connect to statsd when registering data listener.");
                throw new StatsUnavailableException("could not connect", e);
            } catch (SecurityException e2) {
                throw new StatsUnavailableException(e2.getMessage(), e2);
            } catch (Throwable th) {
            }
        }
    }

    private IStatsManager getIStatsManagerLocked() throws StatsUnavailableException {
        IStatsManager iStatsManager = this.mService;
        if (iStatsManager != null) {
            return iStatsManager;
        }
        this.mService = Stub.asInterface(ServiceManager.getService(Context.STATS_MANAGER));
        iStatsManager = this.mService;
        if (iStatsManager != null) {
            try {
                iStatsManager.asBinder().linkToDeath(new StatsdDeathRecipient(), 0);
                return this.mService;
            } catch (RemoteException e) {
                throw new StatsUnavailableException("could not connect when linkToDeath", e);
            }
        }
        throw new StatsUnavailableException("could not be found");
    }
}
