package android.hardware.camera2.params;

import android.graphics.PointF;
import android.hardware.camera2.utils.HashCodeHelpers;
import com.android.internal.util.Preconditions;
import java.util.Arrays;

public final class TonemapCurve {
    public static final int CHANNEL_BLUE = 2;
    public static final int CHANNEL_GREEN = 1;
    public static final int CHANNEL_RED = 0;
    public static final float LEVEL_BLACK = 0.0f;
    public static final float LEVEL_WHITE = 1.0f;
    private static final int MIN_CURVE_LENGTH = 4;
    private static final int OFFSET_POINT_IN = 0;
    private static final int OFFSET_POINT_OUT = 1;
    public static final int POINT_SIZE = 2;
    private static final int TONEMAP_MIN_CURVE_POINTS = 2;
    private final float[] mBlue;
    private final float[] mGreen;
    private boolean mHashCalculated = false;
    private int mHashCode;
    private final float[] mRed;

    public TonemapCurve(float[] red, float[] green, float[] blue) {
        Preconditions.checkNotNull(red, "red must not be null");
        Preconditions.checkNotNull(green, "green must not be null");
        Preconditions.checkNotNull(blue, "blue must not be null");
        String str = "red";
        checkArgumentArrayLengthDivisibleBy(red, 2, str);
        String str2 = "green";
        checkArgumentArrayLengthDivisibleBy(green, 2, str2);
        String str3 = "blue";
        checkArgumentArrayLengthDivisibleBy(blue, 2, str3);
        checkArgumentArrayLengthNoLessThan(red, 4, str);
        checkArgumentArrayLengthNoLessThan(green, 4, str2);
        checkArgumentArrayLengthNoLessThan(blue, 4, str3);
        Preconditions.checkArrayElementsInRange(red, 0.0f, 1.0f, str);
        Preconditions.checkArrayElementsInRange(green, 0.0f, 1.0f, str2);
        Preconditions.checkArrayElementsInRange(blue, 0.0f, 1.0f, str3);
        this.mRed = Arrays.copyOf(red, red.length);
        this.mGreen = Arrays.copyOf(green, green.length);
        this.mBlue = Arrays.copyOf(blue, blue.length);
    }

    private static void checkArgumentArrayLengthDivisibleBy(float[] array, int divisible, String arrayName) {
        if (array.length % divisible != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(arrayName);
            stringBuilder.append(" size must be divisible by ");
            stringBuilder.append(divisible);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private static int checkArgumentColorChannel(int colorChannel) {
        if (colorChannel == 0 || colorChannel == 1 || colorChannel == 2) {
            return colorChannel;
        }
        throw new IllegalArgumentException("colorChannel out of range");
    }

    private static void checkArgumentArrayLengthNoLessThan(float[] array, int minLength, String arrayName) {
        if (array.length < minLength) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(arrayName);
            stringBuilder.append(" size must be at least ");
            stringBuilder.append(minLength);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public int getPointCount(int colorChannel) {
        checkArgumentColorChannel(colorChannel);
        return getCurve(colorChannel).length / 2;
    }

    public PointF getPoint(int colorChannel, int index) {
        checkArgumentColorChannel(colorChannel);
        if (index < 0 || index >= getPointCount(colorChannel)) {
            throw new IllegalArgumentException("index out of range");
        }
        float[] curve = getCurve(colorChannel);
        return new PointF(curve[(index * 2) + 0], curve[(index * 2) + 1]);
    }

    public void copyColorCurve(int colorChannel, float[] destination, int offset) {
        Preconditions.checkArgumentNonnegative(offset, "offset must not be negative");
        Preconditions.checkNotNull(destination, "destination must not be null");
        if (destination.length + offset >= getPointCount(colorChannel) * 2) {
            float[] curve = getCurve(colorChannel);
            System.arraycopy(curve, 0, destination, offset, curve.length);
            return;
        }
        throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TonemapCurve)) {
            return false;
        }
        TonemapCurve other = (TonemapCurve) obj;
        if (Arrays.equals(this.mRed, other.mRed) && Arrays.equals(this.mGreen, other.mGreen) && Arrays.equals(this.mBlue, other.mBlue)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        if (this.mHashCalculated) {
            return this.mHashCode;
        }
        this.mHashCode = HashCodeHelpers.hashCodeGeneric(this.mRed, this.mGreen, this.mBlue);
        this.mHashCalculated = true;
        return this.mHashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("TonemapCurve{");
        sb.append("R:");
        sb.append(curveToString(0));
        sb.append(", G:");
        sb.append(curveToString(1));
        sb.append(", B:");
        sb.append(curveToString(2));
        sb.append("}");
        return sb.toString();
    }

    private String curveToString(int colorChannel) {
        checkArgumentColorChannel(colorChannel);
        StringBuilder sb = new StringBuilder("[");
        float[] curve = getCurve(colorChannel);
        int pointCount = curve.length / 2;
        int i = 0;
        int j = 0;
        while (i < pointCount) {
            sb.append("(");
            sb.append(curve[j]);
            sb.append(", ");
            sb.append(curve[j + 1]);
            sb.append("), ");
            i++;
            j += 2;
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    private float[] getCurve(int colorChannel) {
        if (colorChannel == 0) {
            return this.mRed;
        }
        if (colorChannel == 1) {
            return this.mGreen;
        }
        if (colorChannel == 2) {
            return this.mBlue;
        }
        throw new AssertionError("colorChannel out of range");
    }
}
