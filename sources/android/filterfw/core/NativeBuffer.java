package android.filterfw.core;

public class NativeBuffer {
    private Frame mAttachedFrame;
    private long mDataPointer = 0;
    private boolean mOwnsData = false;
    private int mRefCount = 1;
    private int mSize = 0;

    private native boolean allocate(int i);

    private native boolean deallocate(boolean z);

    private native boolean nativeCopyTo(NativeBuffer nativeBuffer);

    public NativeBuffer(int count) {
        allocate(getElementSize() * count);
        this.mOwnsData = true;
    }

    public NativeBuffer mutableCopy() {
        try {
            NativeBuffer result = (NativeBuffer) getClass().newInstance();
            if (this.mSize <= 0 || nativeCopyTo(result)) {
                return result;
            }
            throw new RuntimeException("Failed to copy NativeBuffer to mutable instance!");
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to allocate a copy of ");
            stringBuilder.append(getClass());
            stringBuilder.append("! Make sure the class has a default constructor!");
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    public int size() {
        return this.mSize;
    }

    public int count() {
        return this.mDataPointer != 0 ? this.mSize / getElementSize() : 0;
    }

    public int getElementSize() {
        return 1;
    }

    public NativeBuffer retain() {
        Frame frame = this.mAttachedFrame;
        if (frame != null) {
            frame.retain();
        } else if (this.mOwnsData) {
            this.mRefCount++;
        }
        return this;
    }

    public NativeBuffer release() {
        boolean doDealloc = false;
        Frame frame = this.mAttachedFrame;
        boolean z = false;
        if (frame != null) {
            if (frame.release() == null) {
                z = true;
            }
            doDealloc = z;
        } else if (this.mOwnsData) {
            this.mRefCount--;
            if (this.mRefCount == 0) {
                z = true;
            }
            doDealloc = z;
        }
        if (!doDealloc) {
            return this;
        }
        deallocate(this.mOwnsData);
        return null;
    }

    public boolean isReadOnly() {
        Frame frame = this.mAttachedFrame;
        return frame != null ? frame.isReadOnly() : false;
    }

    static {
        System.loadLibrary("filterfw");
    }

    /* Access modifiers changed, original: 0000 */
    public void attachToFrame(Frame frame) {
        this.mAttachedFrame = frame;
    }

    /* Access modifiers changed, original: protected */
    public void assertReadable() {
        if (!(this.mDataPointer == 0 || this.mSize == 0)) {
            Frame frame = this.mAttachedFrame;
            if (frame == null || frame.hasNativeAllocation()) {
                return;
            }
        }
        throw new NullPointerException("Attempting to read from null data frame!");
    }

    /* Access modifiers changed, original: protected */
    public void assertWritable() {
        if (isReadOnly()) {
            throw new RuntimeException("Attempting to modify read-only native (structured) data!");
        }
    }
}
