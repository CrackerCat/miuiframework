package android.net.wifi.aware;

public class PeerHandle {
    public int peerId;

    public PeerHandle(int peerId) {
        this.peerId = peerId;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (!(o instanceof PeerHandle)) {
            return false;
        }
        if (this.peerId != ((PeerHandle) o).peerId) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return this.peerId;
    }
}
