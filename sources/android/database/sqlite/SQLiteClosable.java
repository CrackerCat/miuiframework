package android.database.sqlite;

import android.annotation.UnsupportedAppUsage;
import java.io.Closeable;

public abstract class SQLiteClosable implements Closeable {
    @UnsupportedAppUsage
    private int mReferenceCount = 1;

    public abstract void onAllReferencesReleased();

    /* Access modifiers changed, original: protected */
    @Deprecated
    public void onAllReferencesReleasedFromContainer() {
        onAllReferencesReleased();
    }

    public void acquireReference() {
        synchronized (this) {
            if (this.mReferenceCount > 0) {
                this.mReferenceCount++;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("attempt to re-open an already-closed object: ");
                stringBuilder.append(this);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
    }

    public void releaseReference() {
        boolean refCountIsZero;
        synchronized (this) {
            boolean z = true;
            int i = this.mReferenceCount - 1;
            this.mReferenceCount = i;
            if (i != 0) {
                z = false;
            }
            refCountIsZero = z;
        }
        if (refCountIsZero) {
            onAllReferencesReleased();
        }
    }

    @Deprecated
    public void releaseReferenceFromContainer() {
        boolean refCountIsZero;
        synchronized (this) {
            boolean z = true;
            int i = this.mReferenceCount - 1;
            this.mReferenceCount = i;
            if (i != 0) {
                z = false;
            }
            refCountIsZero = z;
        }
        if (refCountIsZero) {
            onAllReferencesReleasedFromContainer();
        }
    }

    public void close() {
        releaseReference();
    }
}
