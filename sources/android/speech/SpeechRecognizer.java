package android.speech;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.speech.IRecognitionService.Stub;
import android.text.TextUtils;
import android.util.Log;
import android.util.SeempLog;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpeechRecognizer {
    public static final String CONFIDENCE_SCORES = "confidence_scores";
    private static final boolean DBG = false;
    public static final int ERROR_AUDIO = 3;
    public static final int ERROR_CLIENT = 5;
    public static final int ERROR_INSUFFICIENT_PERMISSIONS = 9;
    public static final int ERROR_NETWORK = 2;
    public static final int ERROR_NETWORK_TIMEOUT = 1;
    public static final int ERROR_NO_MATCH = 7;
    public static final int ERROR_RECOGNIZER_BUSY = 8;
    public static final int ERROR_SERVER = 4;
    public static final int ERROR_SPEECH_TIMEOUT = 6;
    private static final int MSG_CANCEL = 3;
    private static final int MSG_CHANGE_LISTENER = 4;
    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    public static final String RESULTS_RECOGNITION = "results_recognition";
    private static final String TAG = "SpeechRecognizer";
    private Connection mConnection;
    private final Context mContext;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                SpeechRecognizer.this.handleStartListening((Intent) msg.obj);
            } else if (i == 2) {
                SpeechRecognizer.this.handleStopMessage();
            } else if (i == 3) {
                SpeechRecognizer.this.handleCancelMessage();
            } else if (i == 4) {
                SpeechRecognizer.this.handleChangeListener((RecognitionListener) msg.obj);
            }
        }
    };
    private final InternalListener mListener = new InternalListener();
    private final Queue<Message> mPendingTasks = new LinkedList();
    private IRecognitionService mService;
    private final ComponentName mServiceComponent;

    private class Connection implements ServiceConnection {
        private Connection() {
        }

        /* synthetic */ Connection(SpeechRecognizer x0, AnonymousClass1 x1) {
            this();
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            SpeechRecognizer.this.mService = Stub.asInterface(service);
            while (!SpeechRecognizer.this.mPendingTasks.isEmpty()) {
                SpeechRecognizer.this.mHandler.sendMessage((Message) SpeechRecognizer.this.mPendingTasks.poll());
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            SpeechRecognizer.this.mService = null;
            SpeechRecognizer.this.mConnection = null;
            SpeechRecognizer.this.mPendingTasks.clear();
        }
    }

    private static class InternalListener extends IRecognitionListener.Stub {
        private static final int MSG_BEGINNING_OF_SPEECH = 1;
        private static final int MSG_BUFFER_RECEIVED = 2;
        private static final int MSG_END_OF_SPEECH = 3;
        private static final int MSG_ERROR = 4;
        private static final int MSG_ON_EVENT = 9;
        private static final int MSG_PARTIAL_RESULTS = 7;
        private static final int MSG_READY_FOR_SPEECH = 5;
        private static final int MSG_RESULTS = 6;
        private static final int MSG_RMS_CHANGED = 8;
        private final Handler mInternalHandler;
        private RecognitionListener mInternalListener;

        private InternalListener() {
            this.mInternalHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (InternalListener.this.mInternalListener != null) {
                        switch (msg.what) {
                            case 1:
                                InternalListener.this.mInternalListener.onBeginningOfSpeech();
                                break;
                            case 2:
                                InternalListener.this.mInternalListener.onBufferReceived((byte[]) msg.obj);
                                break;
                            case 3:
                                InternalListener.this.mInternalListener.onEndOfSpeech();
                                break;
                            case 4:
                                InternalListener.this.mInternalListener.onError(((Integer) msg.obj).intValue());
                                break;
                            case 5:
                                InternalListener.this.mInternalListener.onReadyForSpeech((Bundle) msg.obj);
                                break;
                            case 6:
                                InternalListener.this.mInternalListener.onResults((Bundle) msg.obj);
                                break;
                            case 7:
                                InternalListener.this.mInternalListener.onPartialResults((Bundle) msg.obj);
                                break;
                            case 8:
                                InternalListener.this.mInternalListener.onRmsChanged(((Float) msg.obj).floatValue());
                                break;
                            case 9:
                                InternalListener.this.mInternalListener.onEvent(msg.arg1, (Bundle) msg.obj);
                                break;
                        }
                    }
                }
            };
        }

        /* synthetic */ InternalListener(AnonymousClass1 x0) {
            this();
        }

        public void onBeginningOfSpeech() {
            Message.obtain(this.mInternalHandler, 1).sendToTarget();
        }

        public void onBufferReceived(byte[] buffer) {
            Message.obtain(this.mInternalHandler, 2, buffer).sendToTarget();
        }

        public void onEndOfSpeech() {
            Message.obtain(this.mInternalHandler, 3).sendToTarget();
        }

        public void onError(int error) {
            Message.obtain(this.mInternalHandler, 4, Integer.valueOf(error)).sendToTarget();
        }

        public void onReadyForSpeech(Bundle noiseParams) {
            Message.obtain(this.mInternalHandler, 5, noiseParams).sendToTarget();
        }

        public void onResults(Bundle results) {
            Message.obtain(this.mInternalHandler, 6, results).sendToTarget();
        }

        public void onPartialResults(Bundle results) {
            Message.obtain(this.mInternalHandler, 7, results).sendToTarget();
        }

        public void onRmsChanged(float rmsdB) {
            Message.obtain(this.mInternalHandler, 8, Float.valueOf(rmsdB)).sendToTarget();
        }

        public void onEvent(int eventType, Bundle params) {
            Message.obtain(this.mInternalHandler, 9, eventType, eventType, params).sendToTarget();
        }
    }

    private SpeechRecognizer(Context context, ComponentName serviceComponent) {
        this.mContext = context;
        this.mServiceComponent = serviceComponent;
    }

    public static boolean isRecognitionAvailable(Context context) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentServices(new Intent(RecognitionService.SERVICE_INTERFACE), 0);
        if (list == null || list.size() == 0) {
            return false;
        }
        return true;
    }

    public static SpeechRecognizer createSpeechRecognizer(Context context) {
        return createSpeechRecognizer(context, null);
    }

    public static SpeechRecognizer createSpeechRecognizer(Context context, ComponentName serviceComponent) {
        if (context != null) {
            checkIsCalledFromMainThread();
            return new SpeechRecognizer(context, serviceComponent);
        }
        throw new IllegalArgumentException("Context cannot be null)");
    }

    public void setRecognitionListener(RecognitionListener listener) {
        checkIsCalledFromMainThread();
        putMessage(Message.obtain(this.mHandler, 4, listener));
    }

    public void startListening(Intent recognizerIntent) {
        SeempLog.record(72);
        if (recognizerIntent != null) {
            checkIsCalledFromMainThread();
            if (this.mConnection == null) {
                this.mConnection = new Connection(this, null);
                Intent serviceIntent = new Intent(RecognitionService.SERVICE_INTERFACE);
                ComponentName componentName = this.mServiceComponent;
                String str = TAG;
                if (componentName == null) {
                    String serviceComponent = Secure.getString(this.mContext.getContentResolver(), Secure.VOICE_RECOGNITION_SERVICE);
                    if (TextUtils.isEmpty(serviceComponent)) {
                        Log.e(str, "no selected voice recognition service");
                        this.mListener.onError(5);
                        return;
                    }
                    serviceIntent.setComponent(ComponentName.unflattenFromString(serviceComponent));
                } else {
                    serviceIntent.setComponent(componentName);
                }
                if (!this.mContext.bindService(serviceIntent, this.mConnection, 1)) {
                    Log.e(str, "bind to recognition service failed");
                    this.mConnection = null;
                    this.mService = null;
                    this.mListener.onError(5);
                    return;
                }
            }
            putMessage(Message.obtain(this.mHandler, 1, recognizerIntent));
            return;
        }
        throw new IllegalArgumentException("intent must not be null");
    }

    public void stopListening() {
        checkIsCalledFromMainThread();
        putMessage(Message.obtain(this.mHandler, 2));
    }

    public void cancel() {
        checkIsCalledFromMainThread();
        putMessage(Message.obtain(this.mHandler, 3));
    }

    private static void checkIsCalledFromMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("SpeechRecognizer should be used only from the application's main thread");
        }
    }

    private void putMessage(Message msg) {
        if (this.mService == null) {
            this.mPendingTasks.offer(msg);
        } else {
            this.mHandler.sendMessage(msg);
        }
    }

    private void handleStartListening(Intent recognizerIntent) {
        if (checkOpenConnection()) {
            try {
                this.mService.startListening(recognizerIntent, this.mListener);
            } catch (RemoteException e) {
                Log.e(TAG, "startListening() failed", e);
                this.mListener.onError(5);
            }
        }
    }

    private void handleStopMessage() {
        if (checkOpenConnection()) {
            try {
                this.mService.stopListening(this.mListener);
            } catch (RemoteException e) {
                Log.e(TAG, "stopListening() failed", e);
                this.mListener.onError(5);
            }
        }
    }

    private void handleCancelMessage() {
        if (checkOpenConnection()) {
            try {
                this.mService.cancel(this.mListener);
            } catch (RemoteException e) {
                Log.e(TAG, "cancel() failed", e);
                this.mListener.onError(5);
            }
        }
    }

    private boolean checkOpenConnection() {
        if (this.mService != null) {
            return true;
        }
        this.mListener.onError(5);
        Log.e(TAG, "not connected to the recognition service");
        return false;
    }

    private void handleChangeListener(RecognitionListener listener) {
        this.mListener.mInternalListener = listener;
    }

    public void destroy() {
        IRecognitionService iRecognitionService = this.mService;
        if (iRecognitionService != null) {
            try {
                iRecognitionService.cancel(this.mListener);
            } catch (RemoteException e) {
            }
        }
        Connection connection = this.mConnection;
        if (connection != null) {
            this.mContext.unbindService(connection);
        }
        this.mPendingTasks.clear();
        this.mService = null;
        this.mConnection = null;
        this.mListener.mInternalListener = null;
    }
}
