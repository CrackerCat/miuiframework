package android.hardware.camera2.impl;

import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.impl.CameraCaptureSessionImpl.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraCaptureSessionImpl$1$uPVvNnGFdZcxxscdYQ5erNgaRWA implements Runnable {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ CaptureCallback f$1;
    private final /* synthetic */ CaptureRequest f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ long f$4;

    public /* synthetic */ -$$Lambda$CameraCaptureSessionImpl$1$uPVvNnGFdZcxxscdYQ5erNgaRWA(AnonymousClass1 anonymousClass1, CaptureCallback captureCallback, CaptureRequest captureRequest, long j, long j2) {
        this.f$0 = anonymousClass1;
        this.f$1 = captureCallback;
        this.f$2 = captureRequest;
        this.f$3 = j;
        this.f$4 = j2;
    }

    public final void run() {
        this.f$0.lambda$onCaptureStarted$0$CameraCaptureSessionImpl$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
