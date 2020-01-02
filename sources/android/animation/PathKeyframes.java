package android.animation;

import android.animation.Keyframes.FloatKeyframes;
import android.animation.Keyframes.IntKeyframes;
import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;

public class PathKeyframes implements Keyframes {
    private static final ArrayList<Keyframe> EMPTY_KEYFRAMES = new ArrayList();
    private static final int FRACTION_OFFSET = 0;
    private static final int NUM_COMPONENTS = 3;
    private static final int X_OFFSET = 1;
    private static final int Y_OFFSET = 2;
    private float[] mKeyframeData;
    private PointF mTempPointF;

    private static abstract class SimpleKeyframes implements Keyframes {
        private SimpleKeyframes() {
        }

        /* synthetic */ SimpleKeyframes(AnonymousClass1 x0) {
            this();
        }

        public void setEvaluator(TypeEvaluator evaluator) {
        }

        public ArrayList<Keyframe> getKeyframes() {
            return PathKeyframes.EMPTY_KEYFRAMES;
        }

        public Keyframes clone() {
            try {
                return (Keyframes) super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

    static abstract class FloatKeyframesBase extends SimpleKeyframes implements FloatKeyframes {
        FloatKeyframesBase() {
            super();
        }

        public Class getType() {
            return Float.class;
        }

        public Object getValue(float fraction) {
            return Float.valueOf(getFloatValue(fraction));
        }
    }

    static abstract class IntKeyframesBase extends SimpleKeyframes implements IntKeyframes {
        IntKeyframesBase() {
            super();
        }

        public Class getType() {
            return Integer.class;
        }

        public Object getValue(float fraction) {
            return Integer.valueOf(getIntValue(fraction));
        }
    }

    public PathKeyframes(Path path) {
        this(path, 0.5f);
    }

    public PathKeyframes(Path path, float error) {
        this.mTempPointF = new PointF();
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("The path must not be null or empty");
        }
        this.mKeyframeData = path.approximate(error);
    }

    public ArrayList<Keyframe> getKeyframes() {
        return EMPTY_KEYFRAMES;
    }

    public Object getValue(float fraction) {
        int numPoints = this.mKeyframeData.length / 3;
        if (fraction < 0.0f) {
            return interpolateInRange(fraction, 0, 1);
        }
        if (fraction > 1.0f) {
            return interpolateInRange(fraction, numPoints - 2, numPoints - 1);
        }
        if (fraction == 0.0f) {
            return pointForIndex(0);
        }
        if (fraction == 1.0f) {
            return pointForIndex(numPoints - 1);
        }
        int low = 0;
        int high = numPoints - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            float midFraction = this.mKeyframeData[(mid * 3) + 0];
            if (fraction < midFraction) {
                high = mid - 1;
            } else if (fraction <= midFraction) {
                return pointForIndex(mid);
            } else {
                low = mid + 1;
            }
        }
        return interpolateInRange(fraction, high, low);
    }

    private PointF interpolateInRange(float fraction, int startIndex, int endIndex) {
        int startBase = startIndex * 3;
        int endBase = endIndex * 3;
        float endY = this.mKeyframeData;
        float startFraction = endY[startBase + 0];
        float intervalFraction = (fraction - startFraction) / (endY[endBase + 0] - startFraction);
        float startX = endY[startBase + 1];
        float endX = endY[endBase + 1];
        float startY = endY[startBase + 2];
        endY = endY[endBase + 2];
        this.mTempPointF.set(interpolate(intervalFraction, startX, endX), interpolate(intervalFraction, startY, endY));
        return this.mTempPointF;
    }

    public void setEvaluator(TypeEvaluator evaluator) {
    }

    public Class getType() {
        return PointF.class;
    }

    public Keyframes clone() {
        try {
            return (Keyframes) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    private PointF pointForIndex(int index) {
        int base = index * 3;
        int xOffset = base + 1;
        int yOffset = base + 2;
        PointF pointF = this.mTempPointF;
        float[] fArr = this.mKeyframeData;
        pointF.set(fArr[xOffset], fArr[yOffset]);
        return this.mTempPointF;
    }

    private static float interpolate(float fraction, float startValue, float endValue) {
        return ((endValue - startValue) * fraction) + startValue;
    }

    public FloatKeyframes createXFloatKeyframes() {
        return new FloatKeyframesBase() {
            public float getFloatValue(float fraction) {
                return ((PointF) PathKeyframes.this.getValue(fraction)).x;
            }
        };
    }

    public FloatKeyframes createYFloatKeyframes() {
        return new FloatKeyframesBase() {
            public float getFloatValue(float fraction) {
                return ((PointF) PathKeyframes.this.getValue(fraction)).y;
            }
        };
    }

    public IntKeyframes createXIntKeyframes() {
        return new IntKeyframesBase() {
            public int getIntValue(float fraction) {
                return Math.round(((PointF) PathKeyframes.this.getValue(fraction)).x);
            }
        };
    }

    public IntKeyframes createYIntKeyframes() {
        return new IntKeyframesBase() {
            public int getIntValue(float fraction) {
                return Math.round(((PointF) PathKeyframes.this.getValue(fraction)).y);
            }
        };
    }
}
