package android.hardware.radio;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TunerCallbackAdapter$jl29exheqPoYrltfLs9fLsjsI1A implements Runnable {
    private final /* synthetic */ TunerCallbackAdapter f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$TunerCallbackAdapter$jl29exheqPoYrltfLs9fLsjsI1A(TunerCallbackAdapter tunerCallbackAdapter, int i) {
        this.f$0 = tunerCallbackAdapter;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onError$2$TunerCallbackAdapter(this.f$1);
    }
}
