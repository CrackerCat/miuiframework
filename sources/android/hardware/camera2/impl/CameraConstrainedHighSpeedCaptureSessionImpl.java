package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.utils.SurfaceUtils;
import android.os.ConditionVariable;
import android.os.Handler;
import android.util.Range;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public class CameraConstrainedHighSpeedCaptureSessionImpl extends CameraConstrainedHighSpeedCaptureSession implements CameraCaptureSessionCore {
    private final CameraCharacteristics mCharacteristics;
    private final ConditionVariable mInitialized = new ConditionVariable();
    private final CameraCaptureSessionImpl mSessionImpl;

    private class WrapperCallback extends StateCallback {
        private final StateCallback mCallback;

        public WrapperCallback(StateCallback callback) {
            this.mCallback = callback;
        }

        public void onConfigured(CameraCaptureSession session) {
            CameraConstrainedHighSpeedCaptureSessionImpl.this.mInitialized.block();
            this.mCallback.onConfigured(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        public void onConfigureFailed(CameraCaptureSession session) {
            CameraConstrainedHighSpeedCaptureSessionImpl.this.mInitialized.block();
            this.mCallback.onConfigureFailed(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        public void onReady(CameraCaptureSession session) {
            this.mCallback.onReady(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        public void onActive(CameraCaptureSession session) {
            this.mCallback.onActive(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        public void onCaptureQueueEmpty(CameraCaptureSession session) {
            this.mCallback.onCaptureQueueEmpty(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        public void onClosed(CameraCaptureSession session) {
            this.mCallback.onClosed(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
            this.mCallback.onSurfacePrepared(CameraConstrainedHighSpeedCaptureSessionImpl.this, surface);
        }
    }

    CameraConstrainedHighSpeedCaptureSessionImpl(int id, StateCallback callback, Executor stateExecutor, CameraDeviceImpl deviceImpl, Executor deviceStateExecutor, boolean configureSuccess, CameraCharacteristics characteristics) {
        this.mCharacteristics = characteristics;
        StateCallback stateCallback = callback;
        this.mSessionImpl = new CameraCaptureSessionImpl(id, null, new WrapperCallback(callback), stateExecutor, deviceImpl, deviceStateExecutor, configureSuccess);
        this.mInitialized.open();
    }

    public List<CaptureRequest> createHighSpeedRequestList(CaptureRequest request) throws CameraAccessException {
        CaptureRequest captureRequest = request;
        if (captureRequest != null) {
            Collection<Surface> outputSurfaces = request.getTargets();
            Range<Integer> fpsRange = (Range) captureRequest.get(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);
            SurfaceUtils.checkConstrainedHighSpeedSurfaces(outputSurfaces, fpsRange, (StreamConfigurationMap) this.mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP));
            int requestListSize = getHighSpeedRequestListSize(fpsRange, outputSurfaces);
            List<CaptureRequest> requestList = new ArrayList();
            CameraMetadataNative requestMetadata = new CameraMetadataNative(request.getNativeCopy());
            Builder singleTargetRequestBuilder = new Builder(requestMetadata, false, -1, request.getLogicalCameraId(), null);
            singleTargetRequestBuilder.setTag(request.getTag());
            Iterator<Surface> iterator = outputSurfaces.iterator();
            Surface firstSurface = (Surface) iterator.next();
            if (outputSurfaces.size() == 1 && SurfaceUtils.isSurfaceForHwVideoEncoder(firstSurface)) {
                singleTargetRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, Integer.valueOf(1));
            } else {
                singleTargetRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, Integer.valueOf(3));
            }
            singleTargetRequestBuilder.setPartOfCHSRequestList(true);
            Builder doubleTargetRequestBuilder = null;
            if (outputSurfaces.size() == 2) {
                doubleTargetRequestBuilder = new Builder(new CameraMetadataNative(request.getNativeCopy()), false, -1, request.getLogicalCameraId(), null);
                doubleTargetRequestBuilder.setTag(request.getTag());
                doubleTargetRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, Integer.valueOf(3));
                doubleTargetRequestBuilder.addTarget(firstSurface);
                Surface secondSurface = (Surface) iterator.next();
                doubleTargetRequestBuilder.addTarget(secondSurface);
                doubleTargetRequestBuilder.setPartOfCHSRequestList(true);
                requestMetadata = firstSurface;
                if (!SurfaceUtils.isSurfaceForHwVideoEncoder(requestMetadata)) {
                    requestMetadata = secondSurface;
                }
                singleTargetRequestBuilder.addTarget(requestMetadata);
            } else {
                singleTargetRequestBuilder.addTarget(firstSurface);
                CameraMetadataNative cameraMetadataNative = requestMetadata;
            }
            for (int i = 0; i < requestListSize; i++) {
                if (i != 0 || doubleTargetRequestBuilder == null) {
                    requestList.add(singleTargetRequestBuilder.build());
                } else {
                    requestList.add(doubleTargetRequestBuilder.build());
                }
            }
            return Collections.unmodifiableList(requestList);
        }
        throw new IllegalArgumentException("Input capture request must not be null");
    }

    private boolean isConstrainedHighSpeedRequestList(List<CaptureRequest> requestList) {
        Preconditions.checkCollectionNotEmpty(requestList, "High speed request list");
        for (CaptureRequest request : requestList) {
            if (!request.isPartOfCRequestList()) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x004b  */
    private int getHighSpeedRequestListSize(android.util.Range<java.lang.Integer> r9, java.util.Collection<android.view.Surface> r10) {
        /*
        r8 = this;
        r0 = 0;
        r1 = r10.iterator();
    L_0x0005:
        r2 = r1.hasNext();
        if (r2 == 0) goto L_0x0049;
    L_0x000b:
        r2 = r1.next();
        r2 = (android.view.Surface) r2;
        r3 = android.hardware.camera2.utils.SurfaceUtils.isSurfaceForHwVideoEncoder(r2);
        if (r3 == 0) goto L_0x0048;
    L_0x0017:
        r1 = android.hardware.camera2.utils.SurfaceUtils.getSurfaceSize(r2);
        r3 = r8.mCharacteristics;
        r4 = android.hardware.camera2.CameraCharacteristics.CONTROL_AVAILABLE_HIGH_SPEED_VIDEO_CONFIGURATIONS;
        r3 = r3.get(r4);
        r3 = (android.hardware.camera2.params.HighSpeedVideoConfiguration[]) r3;
        r4 = r3.length;
        r5 = 0;
    L_0x0027:
        if (r5 >= r4) goto L_0x0047;
    L_0x0029:
        r6 = r3[r5];
        r7 = r6.getSize();
        r7 = r7.equals(r1);
        if (r7 == 0) goto L_0x0044;
    L_0x0035:
        r7 = r6.getFpsRange();
        r7 = r7.equals(r9);
        if (r7 == 0) goto L_0x0044;
    L_0x003f:
        r0 = r6.getBatchSizeMax();
        goto L_0x0047;
    L_0x0044:
        r5 = r5 + 1;
        goto L_0x0027;
    L_0x0047:
        goto L_0x0049;
    L_0x0048:
        goto L_0x0005;
    L_0x0049:
        if (r0 != 0) goto L_0x0057;
    L_0x004b:
        r1 = r9.getUpper();
        r1 = (java.lang.Integer) r1;
        r1 = r1.intValue();
        r0 = r1 / 30;
    L_0x0057:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.camera2.impl.CameraConstrainedHighSpeedCaptureSessionImpl.getHighSpeedRequestListSize(android.util.Range, java.util.Collection):int");
    }

    public CameraDevice getDevice() {
        return this.mSessionImpl.getDevice();
    }

    public void prepare(Surface surface) throws CameraAccessException {
        this.mSessionImpl.prepare(surface);
    }

    public void prepare(int maxCount, Surface surface) throws CameraAccessException {
        this.mSessionImpl.prepare(maxCount, surface);
    }

    public void tearDown(Surface surface) throws CameraAccessException {
        this.mSessionImpl.tearDown(surface);
    }

    public int capture(CaptureRequest request, CaptureCallback listener, Handler handler) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    public int captureSingleRequest(CaptureRequest request, Executor executor, CaptureCallback listener) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    public int captureBurst(List<CaptureRequest> requests, CaptureCallback listener, Handler handler) throws CameraAccessException {
        if (isConstrainedHighSpeedRequestList(requests)) {
            return this.mSessionImpl.captureBurst(requests, listener, handler);
        }
        throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
    }

    public int captureBurstRequests(List<CaptureRequest> requests, Executor executor, CaptureCallback listener) throws CameraAccessException {
        if (isConstrainedHighSpeedRequestList(requests)) {
            return this.mSessionImpl.captureBurstRequests(requests, executor, listener);
        }
        throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
    }

    public int setRepeatingRequest(CaptureRequest request, CaptureCallback listener, Handler handler) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    public int setSingleRepeatingRequest(CaptureRequest request, Executor executor, CaptureCallback listener) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    public int setRepeatingBurst(List<CaptureRequest> requests, CaptureCallback listener, Handler handler) throws CameraAccessException {
        if (isConstrainedHighSpeedRequestList(requests)) {
            return this.mSessionImpl.setRepeatingBurst(requests, listener, handler);
        }
        throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
    }

    public int setRepeatingBurstRequests(List<CaptureRequest> requests, Executor executor, CaptureCallback listener) throws CameraAccessException {
        if (isConstrainedHighSpeedRequestList(requests)) {
            return this.mSessionImpl.setRepeatingBurstRequests(requests, executor, listener);
        }
        throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
    }

    public void stopRepeating() throws CameraAccessException {
        this.mSessionImpl.stopRepeating();
    }

    public void abortCaptures() throws CameraAccessException {
        this.mSessionImpl.abortCaptures();
    }

    public Surface getInputSurface() {
        return null;
    }

    public void updateOutputConfiguration(OutputConfiguration config) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    public void close() {
        this.mSessionImpl.close();
    }

    public boolean isReprocessable() {
        return false;
    }

    public void replaceSessionClose() {
        this.mSessionImpl.replaceSessionClose();
    }

    public StateCallbackKK getDeviceStateCallback() {
        return this.mSessionImpl.getDeviceStateCallback();
    }

    public boolean isAborting() {
        return this.mSessionImpl.isAborting();
    }

    public void finalizeOutputConfigurations(List<OutputConfiguration> deferredOutputConfigs) throws CameraAccessException {
        this.mSessionImpl.finalizeOutputConfigurations(deferredOutputConfigs);
    }
}
