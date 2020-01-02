package android.net.wifi.aware;

import android.annotation.SystemApi;
import android.net.NetworkSpecifier;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

public class DiscoverySession implements AutoCloseable {
    private static final boolean DBG = false;
    private static final int MAX_SEND_RETRY_COUNT = 5;
    private static final String TAG = "DiscoverySession";
    private static final boolean VDBG = false;
    protected final int mClientId;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    protected WeakReference<WifiAwareManager> mMgr;
    protected final int mSessionId;
    protected boolean mTerminated = false;

    public static int getMaxSendRetryCount() {
        return 5;
    }

    public DiscoverySession(WifiAwareManager manager, int clientId, int sessionId) {
        this.mMgr = new WeakReference(manager);
        this.mClientId = clientId;
        this.mSessionId = sessionId;
        this.mCloseGuard.open("close");
    }

    public void close() {
        WifiAwareManager mgr = (WifiAwareManager) this.mMgr.get();
        if (mgr == null) {
            Log.w(TAG, "destroy: called post GC on WifiAwareManager");
            return;
        }
        mgr.terminateSession(this.mClientId, this.mSessionId);
        this.mTerminated = true;
        this.mMgr.clear();
        this.mCloseGuard.close();
    }

    public void setTerminated() {
        if (this.mTerminated) {
            Log.w(TAG, "terminate: already terminated.");
            return;
        }
        this.mTerminated = true;
        this.mMgr.clear();
        this.mCloseGuard.close();
    }

    /* Access modifiers changed, original: protected */
    public void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            if (!this.mTerminated) {
                close();
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    @VisibleForTesting
    public int getClientId() {
        return this.mClientId;
    }

    @VisibleForTesting
    public int getSessionId() {
        return this.mSessionId;
    }

    public void sendMessage(PeerHandle peerHandle, int messageId, byte[] message, int retryCount) {
        boolean z = this.mTerminated;
        String str = TAG;
        if (z) {
            Log.w(str, "sendMessage: called on terminated session");
            return;
        }
        WifiAwareManager mgr = (WifiAwareManager) this.mMgr.get();
        if (mgr == null) {
            Log.w(str, "sendMessage: called post GC on WifiAwareManager");
            return;
        }
        mgr.sendMessage(this.mClientId, this.mSessionId, peerHandle, message, messageId, retryCount);
    }

    public void sendMessage(PeerHandle peerHandle, int messageId, byte[] message) {
        sendMessage(peerHandle, messageId, message, 0);
    }

    @Deprecated
    public NetworkSpecifier createNetworkSpecifierOpen(PeerHandle peerHandle) {
        boolean z = this.mTerminated;
        String str = TAG;
        if (z) {
            Log.w(str, "createNetworkSpecifierOpen: called on terminated session");
            return null;
        }
        WifiAwareManager mgr = (WifiAwareManager) this.mMgr.get();
        if (mgr == null) {
            Log.w(str, "createNetworkSpecifierOpen: called post GC on WifiAwareManager");
            return null;
        }
        int role;
        if (this instanceof SubscribeDiscoverySession) {
            role = 0;
        } else {
            role = 1;
        }
        return mgr.createNetworkSpecifier(this.mClientId, role, this.mSessionId, peerHandle, null, null);
    }

    @Deprecated
    public NetworkSpecifier createNetworkSpecifierPassphrase(PeerHandle peerHandle, String passphrase) {
        if (WifiAwareUtils.validatePassphrase(passphrase)) {
            boolean z = this.mTerminated;
            String str = TAG;
            if (z) {
                Log.w(str, "createNetworkSpecifierPassphrase: called on terminated session");
                return null;
            }
            WifiAwareManager mgr = (WifiAwareManager) this.mMgr.get();
            if (mgr == null) {
                Log.w(str, "createNetworkSpecifierPassphrase: called post GC on WifiAwareManager");
                return null;
            }
            int role;
            if (this instanceof SubscribeDiscoverySession) {
                role = 0;
            } else {
                role = 1;
            }
            return mgr.createNetworkSpecifier(this.mClientId, role, this.mSessionId, peerHandle, null, passphrase);
        }
        throw new IllegalArgumentException("Passphrase must meet length requirements");
    }

    @SystemApi
    @Deprecated
    public NetworkSpecifier createNetworkSpecifierPmk(PeerHandle peerHandle, byte[] pmk) {
        if (WifiAwareUtils.validatePmk(pmk)) {
            boolean z = this.mTerminated;
            String str = TAG;
            if (z) {
                Log.w(str, "createNetworkSpecifierPmk: called on terminated session");
                return null;
            }
            WifiAwareManager mgr = (WifiAwareManager) this.mMgr.get();
            if (mgr == null) {
                Log.w(str, "createNetworkSpecifierPmk: called post GC on WifiAwareManager");
                return null;
            }
            int role;
            if (this instanceof SubscribeDiscoverySession) {
                role = 0;
            } else {
                role = 1;
            }
            return mgr.createNetworkSpecifier(this.mClientId, role, this.mSessionId, peerHandle, pmk, null);
        }
        throw new IllegalArgumentException("PMK must 32 bytes");
    }
}
