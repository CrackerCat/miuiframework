package android.app;

import android.annotation.UnsupportedAppUsage;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public abstract class IntentService extends Service {
    private String mName;
    private boolean mRedelivery;
    @UnsupportedAppUsage
    private volatile ServiceHandler mServiceHandler;
    private volatile Looper mServiceLooper;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            IntentService.this.onHandleIntent((Intent) msg.obj);
            IntentService.this.stopSelf(msg.arg1);
        }
    }

    public abstract void onHandleIntent(Intent intent);

    public IntentService(String name) {
        this.mName = name;
    }

    public void setIntentRedelivery(boolean enabled) {
        this.mRedelivery = enabled;
    }

    public void onCreate() {
        super.onCreate();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IntentService[");
        stringBuilder.append(this.mName);
        stringBuilder.append("]");
        HandlerThread thread = new HandlerThread(stringBuilder.toString());
        thread.start();
        this.mServiceLooper = thread.getLooper();
        this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
    }

    public void onStart(Intent intent, int startId) {
        Message msg = this.mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        this.mServiceHandler.sendMessage(msg);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return this.mRedelivery ? 3 : 2;
    }

    public void onDestroy() {
        this.mServiceLooper.quit();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
