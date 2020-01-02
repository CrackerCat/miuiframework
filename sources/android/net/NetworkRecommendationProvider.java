package android.net;

import android.Manifest.permission;
import android.annotation.SystemApi;
import android.content.Context;
import android.net.INetworkRecommendationProvider.Stub;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.concurrent.Executor;

@SystemApi
public abstract class NetworkRecommendationProvider {
    private static final String TAG = "NetworkRecProvider";
    private static final boolean VERBOSE;
    private final IBinder mService;

    private final class ServiceWrapper extends Stub {
        private final Context mContext;
        private final Executor mExecutor;
        private final Handler mHandler = null;

        ServiceWrapper(Context context, Executor executor) {
            this.mContext = context;
            this.mExecutor = executor;
        }

        public void requestScores(final NetworkKey[] networks) throws RemoteException {
            enforceCallingPermission();
            if (networks != null && networks.length > 0) {
                execute(new Runnable() {
                    public void run() {
                        NetworkRecommendationProvider.this.onRequestScores(networks);
                    }
                });
            }
        }

        private void execute(Runnable command) {
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(command);
            } else {
                this.mHandler.post(command);
            }
        }

        private void enforceCallingPermission() {
            Context context = this.mContext;
            if (context != null) {
                context.enforceCallingOrSelfPermission(permission.REQUEST_NETWORK_SCORES, "Permission denied.");
            }
        }
    }

    public abstract void onRequestScores(NetworkKey[] networkKeyArr);

    static {
        boolean z = Build.IS_DEBUGGABLE && Log.isLoggable(TAG, 2);
        VERBOSE = z;
    }

    public NetworkRecommendationProvider(Context context, Executor executor) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(executor);
        this.mService = new ServiceWrapper(context, executor);
    }

    public final IBinder getBinder() {
        return this.mService;
    }
}
