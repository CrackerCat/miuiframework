package android.opengl;

public abstract class EGLObjectHandle {
    private final long mHandle;

    @Deprecated
    protected EGLObjectHandle(int handle) {
        this.mHandle = (long) handle;
    }

    protected EGLObjectHandle(long handle) {
        this.mHandle = handle;
    }

    @Deprecated
    public int getHandle() {
        long j = this.mHandle;
        if ((4294967295L & j) == j) {
            return (int) j;
        }
        throw new UnsupportedOperationException();
    }

    public long getNativeHandle() {
        return this.mHandle;
    }

    public int hashCode() {
        int result = 17 * 31;
        long j = this.mHandle;
        return result + ((int) (j ^ (j >>> 32)));
    }
}
