package android.hardware.camera2.legacy;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CaptureResultExtras;
import android.hardware.camera2.impl.PhysicalCaptureResultInfo;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.utils.SubmitInfo;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.system.OsConstants;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import java.util.List;

public class CameraDeviceUserShim implements ICameraDeviceUser {
    private static final boolean DEBUG = false;
    private static final int OPEN_CAMERA_TIMEOUT_MS = 5000;
    private static final String TAG = "CameraDeviceUserShim";
    private final CameraCallbackThread mCameraCallbacks;
    private final CameraCharacteristics mCameraCharacteristics;
    private final CameraLooper mCameraInit;
    private final Object mConfigureLock = new Object();
    private boolean mConfiguring;
    private final LegacyCameraDevice mLegacyDevice;
    private int mSurfaceIdCounter;
    private final SparseArray<Surface> mSurfaces;

    private static class CameraCallbackThread implements ICameraDeviceCallbacks {
        private static final int CAMERA_ERROR = 0;
        private static final int CAMERA_IDLE = 1;
        private static final int CAPTURE_STARTED = 2;
        private static final int PREPARED = 4;
        private static final int REPEATING_REQUEST_ERROR = 5;
        private static final int REQUEST_QUEUE_EMPTY = 6;
        private static final int RESULT_RECEIVED = 3;
        private final ICameraDeviceCallbacks mCallbacks;
        private Handler mHandler;
        private final HandlerThread mHandlerThread = new HandlerThread("LegacyCameraCallback");

        private class CallbackHandler extends Handler {
            public CallbackHandler(Looper l) {
                super(l);
            }

            public void handleMessage(Message msg) {
                try {
                    Object[] resultArray;
                    switch (msg.what) {
                        case 0:
                            CameraCallbackThread.this.mCallbacks.onDeviceError(msg.arg1, (CaptureResultExtras) msg.obj);
                            break;
                        case 1:
                            CameraCallbackThread.this.mCallbacks.onDeviceIdle();
                            break;
                        case 2:
                            CaptureResultExtras resultExtras = msg.obj;
                            CameraCallbackThread.this.mCallbacks.onCaptureStarted(resultExtras, ((((long) msg.arg2) & 4294967295L) << 32) | (4294967295L & ((long) msg.arg1)));
                            break;
                        case 3:
                            resultArray = msg.obj;
                            CameraCallbackThread.this.mCallbacks.onResultReceived(resultArray[0], resultArray[1], new PhysicalCaptureResultInfo[0]);
                            break;
                        case 4:
                            CameraCallbackThread.this.mCallbacks.onPrepared(msg.arg1);
                            break;
                        case 5:
                            resultArray = msg.obj;
                            CameraCallbackThread.this.mCallbacks.onRepeatingRequestError(((Long) resultArray[0]).longValue(), ((Integer) resultArray[1]).intValue());
                            break;
                        case 6:
                            CameraCallbackThread.this.mCallbacks.onRequestQueueEmpty();
                            break;
                        default:
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Unknown callback message ");
                            stringBuilder.append(msg.what);
                            throw new IllegalArgumentException(stringBuilder.toString());
                    }
                } catch (RemoteException e) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Received remote exception during camera callback ");
                    stringBuilder2.append(msg.what);
                    throw new IllegalStateException(stringBuilder2.toString(), e);
                }
            }
        }

        public CameraCallbackThread(ICameraDeviceCallbacks callbacks) {
            this.mCallbacks = callbacks;
            this.mHandlerThread.start();
        }

        public void close() {
            this.mHandlerThread.quitSafely();
        }

        public void onDeviceError(int errorCode, CaptureResultExtras resultExtras) {
            getHandler().sendMessage(getHandler().obtainMessage(0, errorCode, 0, resultExtras));
        }

        public void onDeviceIdle() {
            getHandler().sendMessage(getHandler().obtainMessage(1));
        }

        public void onCaptureStarted(CaptureResultExtras resultExtras, long timestamp) {
            getHandler().sendMessage(getHandler().obtainMessage(2, (int) (timestamp & 4294967295L), (int) (4294967295L & (timestamp >> 32)), resultExtras));
        }

        public void onResultReceived(CameraMetadataNative result, CaptureResultExtras resultExtras, PhysicalCaptureResultInfo[] physicalResults) {
            getHandler().sendMessage(getHandler().obtainMessage(3, new Object[]{result, resultExtras}));
        }

        public void onPrepared(int streamId) {
            getHandler().sendMessage(getHandler().obtainMessage(4, streamId, 0));
        }

        public void onRepeatingRequestError(long lastFrameNumber, int repeatingRequestId) {
            getHandler().sendMessage(getHandler().obtainMessage(5, new Object[]{Long.valueOf(lastFrameNumber), Integer.valueOf(repeatingRequestId)}));
        }

        public void onRequestQueueEmpty() {
            getHandler().sendMessage(getHandler().obtainMessage(6, 0, 0));
        }

        public IBinder asBinder() {
            return null;
        }

        private Handler getHandler() {
            if (this.mHandler == null) {
                this.mHandler = new CallbackHandler(this.mHandlerThread.getLooper());
            }
            return this.mHandler;
        }
    }

    private static class CameraLooper implements Runnable, AutoCloseable {
        private final Camera mCamera = Camera.openUninitialized();
        private final int mCameraId;
        private volatile int mInitErrors;
        private Looper mLooper;
        private final ConditionVariable mStartDone = new ConditionVariable();
        private final Thread mThread;

        public CameraLooper(int cameraId) {
            this.mCameraId = cameraId;
            this.mThread = new Thread(this);
            this.mThread.start();
        }

        public Camera getCamera() {
            return this.mCamera;
        }

        public void run() {
            Looper.prepare();
            this.mLooper = Looper.myLooper();
            this.mInitErrors = this.mCamera.cameraInitUnspecified(this.mCameraId);
            this.mStartDone.open();
            Looper.loop();
        }

        public void close() {
            Looper looper = this.mLooper;
            if (looper != null) {
                looper.quitSafely();
                try {
                    this.mThread.join();
                    this.mLooper = null;
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
            }
        }

        public int waitForOpen(int timeoutMs) {
            if (this.mStartDone.block((long) timeoutMs)) {
                return this.mInitErrors;
            }
            String str = CameraDeviceUserShim.TAG;
            Log.e(str, "waitForOpen - Camera failed to open after timeout of 5000 ms");
            try {
                this.mCamera.release();
            } catch (RuntimeException e) {
                Log.e(str, "connectBinderShim - Failed to release camera after timeout ", e);
            }
            throw new ServiceSpecificException(10);
        }
    }

    protected CameraDeviceUserShim(int cameraId, LegacyCameraDevice legacyCamera, CameraCharacteristics characteristics, CameraLooper cameraInit, CameraCallbackThread cameraCallbacks) {
        this.mLegacyDevice = legacyCamera;
        this.mConfiguring = false;
        this.mSurfaces = new SparseArray();
        this.mCameraCharacteristics = characteristics;
        this.mCameraInit = cameraInit;
        this.mCameraCallbacks = cameraCallbacks;
        this.mSurfaceIdCounter = 0;
    }

    private static int translateErrorsFromCamera1(int errorCode) {
        if (errorCode == (-OsConstants.EACCES)) {
            return 1;
        }
        return errorCode;
    }

    public static CameraDeviceUserShim connectBinderShim(ICameraDeviceCallbacks callbacks, int cameraId, Size displaySize) {
        int i = cameraId;
        CameraLooper init = new CameraLooper(i);
        CameraCallbackThread threadCallbacks = new CameraCallbackThread(callbacks);
        int initErrors = init.waitForOpen(5000);
        Camera legacyCamera = init.getCamera();
        LegacyExceptionUtils.throwOnServiceError(initErrors);
        legacyCamera.disableShutterSound();
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(i, info);
        try {
            CameraCharacteristics characteristics = LegacyMetadataMapper.createCharacteristics(legacyCamera.getParameters(), info, i, displaySize);
            return new CameraDeviceUserShim(cameraId, new LegacyCameraDevice(i, legacyCamera, characteristics, threadCallbacks), characteristics, init, threadCallbacks);
        } catch (RuntimeException e) {
            Size size = displaySize;
            RuntimeException e2 = e2;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to get initial parameters: ");
            stringBuilder.append(e2.getMessage());
            throw new ServiceSpecificException(10, stringBuilder.toString());
        }
    }

    public void disconnect() {
        if (this.mLegacyDevice.isClosed()) {
            Log.w(TAG, "Cannot disconnect, device has already been closed.");
        }
        try {
            this.mLegacyDevice.close();
        } finally {
            this.mCameraInit.close();
            this.mCameraCallbacks.close();
        }
    }

    public SubmitInfo submitRequest(CaptureRequest request, boolean streaming) {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot submit request, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        synchronized (this.mConfigureLock) {
            if (this.mConfiguring) {
                String err2 = "Cannot submit request, configuration change in progress.";
                Log.e(TAG, err2);
                throw new ServiceSpecificException(10, err2);
            }
        }
        return this.mLegacyDevice.submitRequest(request, streaming);
    }

    public SubmitInfo submitRequestList(CaptureRequest[] request, boolean streaming) {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot submit request list, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        synchronized (this.mConfigureLock) {
            if (this.mConfiguring) {
                String err2 = "Cannot submit request, configuration change in progress.";
                Log.e(TAG, err2);
                throw new ServiceSpecificException(10, err2);
            }
        }
        return this.mLegacyDevice.submitRequestList(request, streaming);
    }

    public long cancelRequest(int requestId) {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot cancel request, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        synchronized (this.mConfigureLock) {
            if (this.mConfiguring) {
                String err2 = "Cannot cancel request, configuration change in progress.";
                Log.e(TAG, err2);
                throw new ServiceSpecificException(10, err2);
            }
        }
        return this.mLegacyDevice.cancelRequest(requestId);
    }

    public boolean isSessionConfigurationSupported(SessionConfiguration sessionConfig) {
        int sessionType = sessionConfig.getSessionType();
        String str = TAG;
        boolean z = false;
        if (sessionType != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Session type: ");
            stringBuilder.append(sessionConfig.getSessionType());
            stringBuilder.append(" is different from  regular. Legacy devices support only regular session types!");
            Log.e(str, stringBuilder.toString());
            return false;
        } else if (sessionConfig.getInputConfiguration() != null) {
            Log.e(str, "Input configuration present, legacy devices do not support this feature!");
            return false;
        } else {
            List<OutputConfiguration> outputConfigs = sessionConfig.getOutputConfigurations();
            if (outputConfigs.isEmpty()) {
                Log.e(str, "Empty output configuration list!");
                return false;
            }
            SparseArray<Surface> surfaces = new SparseArray(outputConfigs.size());
            int idx = 0;
            for (OutputConfiguration outputConfig : outputConfigs) {
                List<Surface> surfaceList = outputConfig.getSurfaces();
                if (surfaceList.isEmpty() || surfaceList.size() > 1) {
                    Log.e(str, "Legacy devices do not support deferred or shared surfaces!");
                    return false;
                }
                int idx2 = idx + 1;
                surfaces.put(idx, outputConfig.getSurface());
                idx = idx2;
            }
            if (this.mLegacyDevice.configureOutputs(surfaces, true) == 0) {
                z = true;
            }
            return z;
        }
    }

    public void beginConfigure() {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot begin configure, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        synchronized (this.mConfigureLock) {
            if (this.mConfiguring) {
                String err2 = "Cannot begin configure, configuration change already in progress.";
                Log.e(TAG, err2);
                throw new ServiceSpecificException(10, err2);
            }
            this.mConfiguring = true;
        }
    }

    public void endConfigure(int operatingMode, CameraMetadataNative sessionParams) {
        String err;
        if (this.mLegacyDevice.isClosed()) {
            err = "Cannot end configure, device has been closed.";
            Log.e(TAG, err);
            synchronized (this.mConfigureLock) {
                this.mConfiguring = false;
            }
            throw new ServiceSpecificException(4, err);
        } else if (operatingMode == 0) {
            SparseArray<Surface> surfaces = null;
            synchronized (this.mConfigureLock) {
                if (this.mConfiguring) {
                    if (this.mSurfaces != null) {
                        surfaces = this.mSurfaces.clone();
                    }
                    this.mConfiguring = false;
                } else {
                    String err2 = "Cannot end configure, no configuration change in progress.";
                    Log.e(TAG, err2);
                    throw new ServiceSpecificException(10, err2);
                }
            }
            this.mLegacyDevice.configureOutputs(surfaces);
        } else {
            err = "LEGACY devices do not support this operating mode";
            Log.e(TAG, err);
            synchronized (this.mConfigureLock) {
                this.mConfiguring = false;
            }
            throw new ServiceSpecificException(3, err);
        }
    }

    public void deleteStream(int streamId) {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot delete stream, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        synchronized (this.mConfigureLock) {
            if (this.mConfiguring) {
                int index = this.mSurfaces.indexOfKey(streamId);
                if (index >= 0) {
                    this.mSurfaces.removeAt(index);
                } else {
                    String err2 = new StringBuilder();
                    err2.append("Cannot delete stream, stream id ");
                    err2.append(streamId);
                    err2.append(" doesn't exist.");
                    err2 = err2.toString();
                    Log.e(TAG, err2);
                    throw new ServiceSpecificException(3, err2);
                }
            }
            String err3 = "Cannot delete stream, no configuration change in progress.";
            Log.e(TAG, err3);
            throw new ServiceSpecificException(10, err3);
        }
    }

    public int createStream(OutputConfiguration outputConfiguration) {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot create stream, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        int id;
        synchronized (this.mConfigureLock) {
            String err2;
            if (!this.mConfiguring) {
                err2 = "Cannot create stream, beginConfigure hasn't been called yet.";
                Log.e(TAG, err2);
                throw new ServiceSpecificException(10, err2);
            } else if (outputConfiguration.getRotation() == 0) {
                id = this.mSurfaceIdCounter + 1;
                this.mSurfaceIdCounter = id;
                this.mSurfaces.put(id, outputConfiguration.getSurface());
            } else {
                err2 = "Cannot create stream, stream rotation is not supported.";
                Log.e(TAG, err2);
                throw new ServiceSpecificException(3, err2);
            }
        }
        return id;
    }

    public void finalizeOutputConfigurations(int steamId, OutputConfiguration config) {
        String err = "Finalizing output configuration is not supported on legacy devices";
        Log.e(TAG, err);
        throw new ServiceSpecificException(10, err);
    }

    public int createInputStream(int width, int height, int format) {
        String err = "Creating input stream is not supported on legacy devices";
        Log.e(TAG, err);
        throw new ServiceSpecificException(10, err);
    }

    public Surface getInputSurface() {
        String err = "Getting input surface is not supported on legacy devices";
        Log.e(TAG, err);
        throw new ServiceSpecificException(10, err);
    }

    public CameraMetadataNative createDefaultRequest(int templateId) {
        boolean isClosed = this.mLegacyDevice.isClosed();
        String str = TAG;
        if (isClosed) {
            String err = "Cannot create default request, device has been closed.";
            Log.e(str, err);
            throw new ServiceSpecificException(4, err);
        }
        try {
            return LegacyMetadataMapper.createRequestTemplate(this.mCameraCharacteristics, templateId);
        } catch (IllegalArgumentException e) {
            String err2 = "createDefaultRequest - invalid templateId specified";
            Log.e(str, err2);
            throw new ServiceSpecificException(3, err2);
        }
    }

    public CameraMetadataNative getCameraInfo() {
        Log.e(TAG, "getCameraInfo unimplemented.");
        return null;
    }

    public void updateOutputConfiguration(int streamId, OutputConfiguration config) {
    }

    public void waitUntilIdle() throws RemoteException {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot wait until idle, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        synchronized (this.mConfigureLock) {
            if (this.mConfiguring) {
                String err2 = "Cannot wait until idle, configuration change in progress.";
                Log.e(TAG, err2);
                throw new ServiceSpecificException(10, err2);
            }
        }
        this.mLegacyDevice.waitUntilIdle();
    }

    public long flush() {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot flush, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        synchronized (this.mConfigureLock) {
            if (this.mConfiguring) {
                String err2 = "Cannot flush, configuration change in progress.";
                Log.e(TAG, err2);
                throw new ServiceSpecificException(10, err2);
            }
        }
        return this.mLegacyDevice.flush();
    }

    public void prepare(int streamId) {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot prepare stream, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
        this.mCameraCallbacks.onPrepared(streamId);
    }

    public void prepare2(int maxCount, int streamId) {
        prepare(streamId);
    }

    public void tearDown(int streamId) {
        if (this.mLegacyDevice.isClosed()) {
            String err = "Cannot tear down stream, device has been closed.";
            Log.e(TAG, err);
            throw new ServiceSpecificException(4, err);
        }
    }

    public IBinder asBinder() {
        return null;
    }
}
