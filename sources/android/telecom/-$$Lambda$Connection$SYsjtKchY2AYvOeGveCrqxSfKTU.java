package android.telecom;

import android.telecom.Connection.Listener;
import java.util.function.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Connection$SYsjtKchY2AYvOeGveCrqxSfKTU implements Consumer {
    private final /* synthetic */ Connection f$0;

    public /* synthetic */ -$$Lambda$Connection$SYsjtKchY2AYvOeGveCrqxSfKTU(Connection connection) {
        this.f$0 = connection;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$sendRttSessionRemotelyTerminated$2$Connection((Listener) obj);
    }
}
