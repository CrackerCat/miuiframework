package android.net;

import com.android.okhttp.internalandroidapi.Dns;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Network$KD6DxaMRJIcajhj36TU1K7lJnHQ implements Dns {
    private final /* synthetic */ Network f$0;

    public /* synthetic */ -$$Lambda$Network$KD6DxaMRJIcajhj36TU1K7lJnHQ(Network network) {
        this.f$0 = network;
    }

    public final List lookup(String str) {
        return this.f$0.lambda$maybeInitUrlConnectionFactory$0$Network(str);
    }
}
