package android.telephony.euicc;

import android.telephony.euicc.EuiccCardManager.ResultCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EuiccCardManager$15$_sAstfKdFkraAjC1faT-C0t_PgM implements Runnable {
    private final /* synthetic */ ResultCallback f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ byte[] f$2;

    public /* synthetic */ -$$Lambda$EuiccCardManager$15$_sAstfKdFkraAjC1faT-C0t_PgM(ResultCallback resultCallback, int i, byte[] bArr) {
        this.f$0 = resultCallback;
        this.f$1 = i;
        this.f$2 = bArr;
    }

    public final void run() {
        this.f$0.onComplete(this.f$1, this.f$2);
    }
}