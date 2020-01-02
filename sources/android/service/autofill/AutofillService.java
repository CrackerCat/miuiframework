package android.service.autofill;

import android.app.Service;
import android.content.Intent;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.Looper;
import android.os.RemoteException;
import android.service.autofill.IAutoFillService.Stub;
import android.util.Log;
import android.view.autofill.AutofillManager;
import com.android.internal.util.function.pooled.PooledLambda;

public abstract class AutofillService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.autofill.AutofillService";
    public static final String SERVICE_META_DATA = "android.autofill";
    private static final String TAG = "AutofillService";
    private Handler mHandler;
    private final IAutoFillService mInterface = new Stub() {
        public void onConnectedStateChanged(boolean connected) {
            AutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(connected ? -$$Lambda$amIBeR2CTPTUHkT8htLcarZmUYc.INSTANCE : -$$Lambda$eWz26esczusoIA84WEwFlxQuDGQ.INSTANCE, AutofillService.this));
        }

        public void onFillRequest(FillRequest request, IFillCallback callback) {
            ICancellationSignal transport = CancellationSignal.createTransport();
            try {
                callback.onCancellable(transport);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
            AutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(-$$Lambda$I0gCKFrBTO70VZfSZTq2fj-wyG8.INSTANCE, AutofillService.this, request, CancellationSignal.fromTransport(transport), new FillCallback(callback, request.getId())));
        }

        public void onSaveRequest(SaveRequest request, ISaveCallback callback) {
            AutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(-$$Lambda$KrOZIsyY-3lh3prHWFldsWopHBw.INSTANCE, AutofillService.this, request, new SaveCallback(callback)));
        }
    };

    public abstract void onFillRequest(FillRequest fillRequest, CancellationSignal cancellationSignal, FillCallback fillCallback);

    public abstract void onSaveRequest(SaveRequest saveRequest, SaveCallback saveCallback);

    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
    }

    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mInterface.asBinder();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Tried to bind to wrong intent (should be android.service.autofill.AutofillService: ");
        stringBuilder.append(intent);
        Log.w(TAG, stringBuilder.toString());
        return null;
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public final FillEventHistory getFillEventHistory() {
        AutofillManager afm = (AutofillManager) getSystemService(AutofillManager.class);
        if (afm == null) {
            return null;
        }
        return afm.getFillEventHistory();
    }
}
