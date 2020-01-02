package android.graphics.text;

import android.graphics.Paint;
import android.graphics.Rect;
import com.android.internal.util.Preconditions;
import libcore.util.NativeAllocationRegistry;

public class MeasuredText {
    private char[] mChars;
    private boolean mComputeHyphenation;
    private boolean mComputeLayout;
    private long mNativePtr;

    public static final class Builder {
        private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(MeasuredText.class.getClassLoader(), MeasuredText.nGetReleaseFunc());
        private boolean mComputeHyphenation = false;
        private boolean mComputeLayout = true;
        private int mCurrentOffset = 0;
        private MeasuredText mHintMt = null;
        private long mNativePtr;
        private final char[] mText;

        private static native void nAddReplacementRun(long j, long j2, int i, int i2, float f);

        private static native void nAddStyleRun(long j, long j2, int i, int i2, boolean z);

        private static native long nBuildMeasuredText(long j, long j2, char[] cArr, boolean z, boolean z2);

        private static native void nFreeBuilder(long j);

        private static native long nInitBuilder();

        public Builder(char[] text) {
            Preconditions.checkNotNull(text);
            this.mText = text;
            this.mNativePtr = nInitBuilder();
        }

        public Builder(MeasuredText text) {
            Preconditions.checkNotNull(text);
            this.mText = text.mChars;
            this.mNativePtr = nInitBuilder();
            if (text.mComputeLayout) {
                this.mComputeHyphenation = text.mComputeHyphenation;
                this.mComputeLayout = text.mComputeLayout;
                this.mHintMt = text;
                return;
            }
            throw new IllegalArgumentException("The input MeasuredText must not be created with setComputeLayout(false).");
        }

        public Builder appendStyleRun(Paint paint, int length, boolean isRtl) {
            Preconditions.checkNotNull(paint);
            boolean z = true;
            Preconditions.checkArgument(length > 0, "length can not be negative");
            int end = this.mCurrentOffset + length;
            if (end > this.mText.length) {
                z = false;
            }
            Preconditions.checkArgument(z, "Style exceeds the text length");
            nAddStyleRun(this.mNativePtr, paint.getNativeInstance(), this.mCurrentOffset, end, isRtl);
            this.mCurrentOffset = end;
            return this;
        }

        public Builder appendReplacementRun(Paint paint, int length, float width) {
            boolean z = true;
            Preconditions.checkArgument(length > 0, "length can not be negative");
            int end = this.mCurrentOffset + length;
            if (end > this.mText.length) {
                z = false;
            }
            Preconditions.checkArgument(z, "Replacement exceeds the text length");
            nAddReplacementRun(this.mNativePtr, paint.getNativeInstance(), this.mCurrentOffset, end, width);
            this.mCurrentOffset = end;
            return this;
        }

        public Builder setComputeHyphenation(boolean computeHyphenation) {
            this.mComputeHyphenation = computeHyphenation;
            return this;
        }

        public Builder setComputeLayout(boolean computeLayout) {
            this.mComputeLayout = computeLayout;
            return this;
        }

        public MeasuredText build() {
            ensureNativePtrNoReuse();
            if (this.mCurrentOffset == this.mText.length) {
                MeasuredText measuredText = this.mHintMt;
                if (measuredText == null || measuredText.mComputeHyphenation == this.mComputeHyphenation) {
                    try {
                        long ptr = nBuildMeasuredText(this.mNativePtr, this.mHintMt == null ? 0 : this.mHintMt.getNativePtr(), this.mText, this.mComputeHyphenation, this.mComputeLayout);
                        MeasuredText measuredText2 = new MeasuredText(ptr, this.mText, this.mComputeHyphenation, this.mComputeLayout);
                        sRegistry.registerNativeAllocation(measuredText2, ptr);
                        return measuredText2;
                    } finally {
                        nFreeBuilder(this.mNativePtr);
                        this.mNativePtr = 0;
                    }
                } else {
                    throw new IllegalArgumentException("The hyphenation configuration is different from given hint MeasuredText");
                }
            }
            throw new IllegalStateException("Style info has not been provided for all text.");
        }

        private void ensureNativePtrNoReuse() {
            if (this.mNativePtr == 0) {
                throw new IllegalStateException("Builder can not be reused.");
            }
        }
    }

    private static native void nGetBounds(long j, char[] cArr, int i, int i2, Rect rect);

    private static native float nGetCharWidthAt(long j, int i);

    private static native int nGetMemoryUsage(long j);

    private static native long nGetReleaseFunc();

    private static native float nGetWidth(long j, int i, int i2);

    private MeasuredText(long ptr, char[] chars, boolean computeHyphenation, boolean computeLayout) {
        this.mNativePtr = ptr;
        this.mChars = chars;
        this.mComputeHyphenation = computeHyphenation;
        this.mComputeLayout = computeLayout;
    }

    public char[] getChars() {
        return this.mChars;
    }

    public float getWidth(int start, int end) {
        boolean z = true;
        boolean z2 = start >= 0 && start <= this.mChars.length;
        StringBuilder stringBuilder = new StringBuilder();
        String str = "start(";
        stringBuilder.append(str);
        stringBuilder.append(start);
        stringBuilder.append(") must be 0 <= start <= ");
        stringBuilder.append(this.mChars.length);
        Preconditions.checkArgument(z2, stringBuilder.toString());
        z2 = end >= 0 && end <= this.mChars.length;
        stringBuilder = new StringBuilder();
        stringBuilder.append("end(");
        stringBuilder.append(end);
        stringBuilder.append(") must be 0 <= end <= ");
        stringBuilder.append(this.mChars.length);
        Preconditions.checkArgument(z2, stringBuilder.toString());
        if (start > end) {
            z = false;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(start);
        stringBuilder2.append(") is larger than end(");
        stringBuilder2.append(end);
        stringBuilder2.append(")");
        Preconditions.checkArgument(z, stringBuilder2.toString());
        return nGetWidth(this.mNativePtr, start, end);
    }

    public int getMemoryUsage() {
        return nGetMemoryUsage(this.mNativePtr);
    }

    public void getBounds(int start, int end, Rect rect) {
        boolean z = true;
        boolean z2 = start >= 0 && start <= this.mChars.length;
        StringBuilder stringBuilder = new StringBuilder();
        String str = "start(";
        stringBuilder.append(str);
        stringBuilder.append(start);
        stringBuilder.append(") must be 0 <= start <= ");
        stringBuilder.append(this.mChars.length);
        Preconditions.checkArgument(z2, stringBuilder.toString());
        z2 = end >= 0 && end <= this.mChars.length;
        stringBuilder = new StringBuilder();
        stringBuilder.append("end(");
        stringBuilder.append(end);
        stringBuilder.append(") must be 0 <= end <= ");
        stringBuilder.append(this.mChars.length);
        Preconditions.checkArgument(z2, stringBuilder.toString());
        if (start > end) {
            z = false;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(start);
        stringBuilder2.append(") is larger than end(");
        stringBuilder2.append(end);
        stringBuilder2.append(")");
        Preconditions.checkArgument(z, stringBuilder2.toString());
        Preconditions.checkNotNull(rect);
        nGetBounds(this.mNativePtr, this.mChars, start, end, rect);
    }

    public float getCharWidthAt(int offset) {
        boolean z = offset >= 0 && offset < this.mChars.length;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("offset(");
        stringBuilder.append(offset);
        stringBuilder.append(") is larger than text length: ");
        stringBuilder.append(this.mChars.length);
        Preconditions.checkArgument(z, stringBuilder.toString());
        return nGetCharWidthAt(this.mNativePtr, offset);
    }

    public long getNativePtr() {
        return this.mNativePtr;
    }
}
