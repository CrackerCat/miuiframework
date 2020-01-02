package android.graphics.drawable;

import android.annotation.UnsupportedAppUsage;
import android.content.res.ColorStateList;
import android.content.res.ComplexColor;
import android.content.res.GradientColor;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Trace;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.PathParser.PathData;
import android.util.Property;
import com.android.internal.R;
import com.android.internal.util.VirtualRefBasePtr;
import dalvik.system.VMRuntime;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VectorDrawable extends Drawable {
    private static final String LOGTAG = VectorDrawable.class.getSimpleName();
    private static final String SHAPE_CLIP_PATH = "clip-path";
    private static final String SHAPE_GROUP = "group";
    private static final String SHAPE_PATH = "path";
    private static final String SHAPE_VECTOR = "vector";
    private BlendModeColorFilter mBlendModeColorFilter;
    private ColorFilter mColorFilter;
    private boolean mDpiScaledDirty;
    private int mDpiScaledHeight;
    private Insets mDpiScaledInsets;
    private int mDpiScaledWidth;
    private boolean mMutated;
    private int mTargetDensity;
    @UnsupportedAppUsage
    private PorterDuffColorFilter mTintFilter;
    private final Rect mTmpBounds;
    private VectorDrawableState mVectorState;

    static abstract class VObject {
        VirtualRefBasePtr mTreePtr = null;

        public abstract void applyTheme(Theme theme);

        public abstract boolean canApplyTheme();

        public abstract long getNativePtr();

        public abstract int getNativeSize();

        public abstract Property getProperty(String str);

        public abstract boolean hasFocusStateSpecified();

        public abstract void inflate(Resources resources, AttributeSet attributeSet, Theme theme);

        public abstract boolean isStateful();

        public abstract boolean onStateChange(int[] iArr);

        VObject() {
        }

        /* Access modifiers changed, original: 0000 */
        public boolean isTreeValid() {
            VirtualRefBasePtr virtualRefBasePtr = this.mTreePtr;
            return (virtualRefBasePtr == null || virtualRefBasePtr.get() == 0) ? false : true;
        }

        /* Access modifiers changed, original: 0000 */
        public void setTree(VirtualRefBasePtr ptr) {
            this.mTreePtr = ptr;
        }
    }

    static abstract class VPath extends VObject {
        private static final Property<VPath, PathData> PATH_DATA = new Property<VPath, PathData>(PathData.class, "pathData") {
            public void set(VPath object, PathData data) {
                object.setPathData(data);
            }

            public PathData get(VPath object) {
                return object.getPathData();
            }
        };
        int mChangingConfigurations;
        protected PathData mPathData = null;
        String mPathName;

        /* Access modifiers changed, original: 0000 */
        public Property getProperty(String propertyName) {
            if (PATH_DATA.getName().equals(propertyName)) {
                return PATH_DATA;
            }
            return null;
        }

        public VPath(VPath copy) {
            PathData pathData = null;
            this.mPathName = copy.mPathName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            PathData pathData2 = copy.mPathData;
            if (pathData2 != null) {
                pathData = new PathData(pathData2);
            }
            this.mPathData = pathData;
        }

        public String getPathName() {
            return this.mPathName;
        }

        public PathData getPathData() {
            return this.mPathData;
        }

        public void setPathData(PathData pathData) {
            this.mPathData.setPathData(pathData);
            if (isTreeValid()) {
                VectorDrawable.nSetPathData(getNativePtr(), this.mPathData.getNativePtr());
            }
        }
    }

    private static class VClipPath extends VPath {
        private static final int NATIVE_ALLOCATION_SIZE = 120;
        private final long mNativePtr;

        public VClipPath() {
            this.mNativePtr = VectorDrawable.nCreateClipPath();
        }

        public VClipPath(VClipPath copy) {
            super(copy);
            this.mNativePtr = VectorDrawable.nCreateClipPath(copy.mNativePtr);
        }

        public long getNativePtr() {
            return this.mNativePtr;
        }

        public void inflate(Resources r, AttributeSet attrs, Theme theme) {
            TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.VectorDrawableClipPath);
            updateStateFromTypedArray(a);
            a.recycle();
        }

        public boolean canApplyTheme() {
            return false;
        }

        public void applyTheme(Theme theme) {
        }

        public boolean onStateChange(int[] stateSet) {
            return false;
        }

        public boolean isStateful() {
            return false;
        }

        public boolean hasFocusStateSpecified() {
            return false;
        }

        /* Access modifiers changed, original: 0000 */
        public int getNativeSize() {
            return 120;
        }

        private void updateStateFromTypedArray(TypedArray a) {
            this.mChangingConfigurations |= a.getChangingConfigurations();
            String pathName = a.getString(null);
            if (pathName != null) {
                this.mPathName = pathName;
                VectorDrawable.nSetName(this.mNativePtr, this.mPathName);
            }
            String pathDataString = a.getString(1);
            if (pathDataString != null) {
                this.mPathData = new PathData(pathDataString);
                VectorDrawable.nSetPathString(this.mNativePtr, pathDataString, pathDataString.length());
            }
        }
    }

    static class VFullPath extends VPath {
        private static final Property<VFullPath, Float> FILL_ALPHA = new FloatProperty<VFullPath>("fillAlpha") {
            public void setValue(VFullPath object, float value) {
                object.setFillAlpha(value);
            }

            public Float get(VFullPath object) {
                return Float.valueOf(object.getFillAlpha());
            }
        };
        private static final int FILL_ALPHA_INDEX = 4;
        private static final Property<VFullPath, Integer> FILL_COLOR = new IntProperty<VFullPath>("fillColor") {
            public void setValue(VFullPath object, int value) {
                object.setFillColor(value);
            }

            public Integer get(VFullPath object) {
                return Integer.valueOf(object.getFillColor());
            }
        };
        private static final int FILL_COLOR_INDEX = 3;
        private static final int FILL_TYPE_INDEX = 11;
        private static final int NATIVE_ALLOCATION_SIZE = 264;
        private static final Property<VFullPath, Float> STROKE_ALPHA = new FloatProperty<VFullPath>("strokeAlpha") {
            public void setValue(VFullPath object, float value) {
                object.setStrokeAlpha(value);
            }

            public Float get(VFullPath object) {
                return Float.valueOf(object.getStrokeAlpha());
            }
        };
        private static final int STROKE_ALPHA_INDEX = 2;
        private static final Property<VFullPath, Integer> STROKE_COLOR = new IntProperty<VFullPath>("strokeColor") {
            public void setValue(VFullPath object, int value) {
                object.setStrokeColor(value);
            }

            public Integer get(VFullPath object) {
                return Integer.valueOf(object.getStrokeColor());
            }
        };
        private static final int STROKE_COLOR_INDEX = 1;
        private static final int STROKE_LINE_CAP_INDEX = 8;
        private static final int STROKE_LINE_JOIN_INDEX = 9;
        private static final int STROKE_MITER_LIMIT_INDEX = 10;
        private static final Property<VFullPath, Float> STROKE_WIDTH = new FloatProperty<VFullPath>("strokeWidth") {
            public void setValue(VFullPath object, float value) {
                object.setStrokeWidth(value);
            }

            public Float get(VFullPath object) {
                return Float.valueOf(object.getStrokeWidth());
            }
        };
        private static final int STROKE_WIDTH_INDEX = 0;
        private static final int TOTAL_PROPERTY_COUNT = 12;
        private static final Property<VFullPath, Float> TRIM_PATH_END = new FloatProperty<VFullPath>("trimPathEnd") {
            public void setValue(VFullPath object, float value) {
                object.setTrimPathEnd(value);
            }

            public Float get(VFullPath object) {
                return Float.valueOf(object.getTrimPathEnd());
            }
        };
        private static final int TRIM_PATH_END_INDEX = 6;
        private static final Property<VFullPath, Float> TRIM_PATH_OFFSET = new FloatProperty<VFullPath>("trimPathOffset") {
            public void setValue(VFullPath object, float value) {
                object.setTrimPathOffset(value);
            }

            public Float get(VFullPath object) {
                return Float.valueOf(object.getTrimPathOffset());
            }
        };
        private static final int TRIM_PATH_OFFSET_INDEX = 7;
        private static final Property<VFullPath, Float> TRIM_PATH_START = new FloatProperty<VFullPath>("trimPathStart") {
            public void setValue(VFullPath object, float value) {
                object.setTrimPathStart(value);
            }

            public Float get(VFullPath object) {
                return Float.valueOf(object.getTrimPathStart());
            }
        };
        private static final int TRIM_PATH_START_INDEX = 5;
        private static final HashMap<String, Integer> sPropertyIndexMap = new HashMap<String, Integer>() {
            {
                put("strokeWidth", Integer.valueOf(0));
                put("strokeColor", Integer.valueOf(1));
                put("strokeAlpha", Integer.valueOf(2));
                put("fillColor", Integer.valueOf(3));
                put("fillAlpha", Integer.valueOf(4));
                put("trimPathStart", Integer.valueOf(5));
                put("trimPathEnd", Integer.valueOf(6));
                put("trimPathOffset", Integer.valueOf(7));
            }
        };
        private static final HashMap<String, Property> sPropertyMap = new HashMap<String, Property>() {
            {
                put("strokeWidth", VFullPath.STROKE_WIDTH);
                put("strokeColor", VFullPath.STROKE_COLOR);
                put("strokeAlpha", VFullPath.STROKE_ALPHA);
                put("fillColor", VFullPath.FILL_COLOR);
                put("fillAlpha", VFullPath.FILL_ALPHA);
                put("trimPathStart", VFullPath.TRIM_PATH_START);
                put("trimPathEnd", VFullPath.TRIM_PATH_END);
                put("trimPathOffset", VFullPath.TRIM_PATH_OFFSET);
            }
        };
        ComplexColor mFillColors;
        private final long mNativePtr;
        private byte[] mPropertyData;
        ComplexColor mStrokeColors;
        private int[] mThemeAttrs;

        public VFullPath() {
            this.mStrokeColors = null;
            this.mFillColors = null;
            this.mNativePtr = VectorDrawable.nCreateFullPath();
        }

        public VFullPath(VFullPath copy) {
            super(copy);
            this.mStrokeColors = null;
            this.mFillColors = null;
            this.mNativePtr = VectorDrawable.nCreateFullPath(copy.mNativePtr);
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mStrokeColors = copy.mStrokeColors;
            this.mFillColors = copy.mFillColors;
        }

        /* Access modifiers changed, original: 0000 */
        public Property getProperty(String propertyName) {
            Property p = super.getProperty(propertyName);
            if (p != null) {
                return p;
            }
            if (sPropertyMap.containsKey(propertyName)) {
                return (Property) sPropertyMap.get(propertyName);
            }
            return null;
        }

        /* Access modifiers changed, original: 0000 */
        public int getPropertyIndex(String propertyName) {
            if (sPropertyIndexMap.containsKey(propertyName)) {
                return ((Integer) sPropertyIndexMap.get(propertyName)).intValue();
            }
            return -1;
        }

        public boolean onStateChange(int[] stateSet) {
            int oldStrokeColor;
            int newStrokeColor;
            boolean changed = false;
            ComplexColor complexColor = this.mStrokeColors;
            int i = 1;
            if (complexColor != null && (complexColor instanceof ColorStateList)) {
                oldStrokeColor = getStrokeColor();
                newStrokeColor = ((ColorStateList) this.mStrokeColors).getColorForState(stateSet, oldStrokeColor);
                changed = false | (oldStrokeColor != newStrokeColor ? 1 : 0);
                if (oldStrokeColor != newStrokeColor) {
                    VectorDrawable.nSetStrokeColor(this.mNativePtr, newStrokeColor);
                }
            }
            complexColor = this.mFillColors;
            if (complexColor != null && (complexColor instanceof ColorStateList)) {
                oldStrokeColor = getFillColor();
                newStrokeColor = ((ColorStateList) this.mFillColors).getColorForState(stateSet, oldStrokeColor);
                if (oldStrokeColor == newStrokeColor) {
                    i = 0;
                }
                changed |= i;
                if (oldStrokeColor != newStrokeColor) {
                    VectorDrawable.nSetFillColor(this.mNativePtr, newStrokeColor);
                }
            }
            return changed;
        }

        public boolean isStateful() {
            return (this.mStrokeColors == null && this.mFillColors == null) ? false : true;
        }

        public boolean hasFocusStateSpecified() {
            ComplexColor complexColor = this.mStrokeColors;
            if (complexColor != null && (complexColor instanceof ColorStateList) && ((ColorStateList) complexColor).hasFocusStateSpecified()) {
                complexColor = this.mFillColors;
                if (complexColor != null && (complexColor instanceof ColorStateList) && ((ColorStateList) complexColor).hasFocusStateSpecified()) {
                    return true;
                }
            }
            return false;
        }

        /* Access modifiers changed, original: 0000 */
        public int getNativeSize() {
            return 264;
        }

        public long getNativePtr() {
            return this.mNativePtr;
        }

        public void inflate(Resources r, AttributeSet attrs, Theme theme) {
            TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.VectorDrawablePath);
            updateStateFromTypedArray(a);
            a.recycle();
        }

        private void updateStateFromTypedArray(TypedArray a) {
            TypedArray typedArray = a;
            if (this.mPropertyData == null) {
                this.mPropertyData = new byte[48];
            }
            boolean success = VectorDrawable.nGetFullPathProperties(this.mNativePtr, this.mPropertyData, 48);
            int byteCount;
            if (success) {
                int strokeColor;
                float trimPathOffset;
                int fillColor;
                int strokeColor2;
                int strokeColor3;
                int fillType;
                ByteBuffer properties = ByteBuffer.wrap(this.mPropertyData);
                properties.order(ByteOrder.nativeOrder());
                float strokeWidth = properties.getFloat(0);
                int strokeColor4 = properties.getInt(4);
                float strokeAlpha = properties.getFloat(8);
                int fillColor2 = properties.getInt(12);
                float fillAlpha = properties.getFloat(2.24E-44f);
                float trimPathStart = properties.getFloat(2.8E-44f);
                float trimPathEnd = properties.getFloat(3.4E-44f);
                float trimPathOffset2 = properties.getFloat(3.9E-44f);
                int strokeLineCap = properties.getInt(32);
                int strokeLineJoin = properties.getInt(36);
                float strokeMiterLimit = properties.getFloat(5.6E-44f);
                byteCount = 48;
                int byteCount2 = properties.getInt(44);
                Shader fillGradient = null;
                Shader strokeGradient = null;
                this.mChangingConfigurations |= a.getChangingConfigurations();
                this.mThemeAttrs = a.extractThemeAttrs();
                String pathName = typedArray.getString(null);
                if (pathName != null) {
                    this.mPathName = pathName;
                    strokeColor = strokeColor4;
                    VectorDrawable.nSetName(this.mNativePtr, this.mPathName);
                } else {
                    ByteBuffer byteBuffer = properties;
                    strokeColor = strokeColor4;
                }
                pathName = typedArray.getString(2);
                if (pathName != null) {
                    this.mPathData = new PathData(pathName);
                    trimPathOffset = trimPathOffset2;
                    fillColor = fillColor2;
                    VectorDrawable.nSetPathString(this.mNativePtr, pathName, pathName.length());
                } else {
                    trimPathOffset = trimPathOffset2;
                    fillColor = fillColor2;
                }
                ComplexColor fillColors = typedArray.getComplexColor(1);
                if (fillColors != null) {
                    if (fillColors instanceof GradientColor) {
                        this.mFillColors = fillColors;
                        fillGradient = ((GradientColor) fillColors).getShader();
                    } else if (fillColors.isStateful() || fillColors.canApplyTheme()) {
                        this.mFillColors = fillColors;
                    } else {
                        this.mFillColors = null;
                    }
                    fillColor2 = fillColors.getDefaultColor();
                } else {
                    fillColor2 = fillColor;
                }
                ComplexColor strokeColors = typedArray.getComplexColor(3);
                if (strokeColors != null) {
                    if ((strokeColors instanceof GradientColor) != null) {
                        this.mStrokeColors = strokeColors;
                        strokeGradient = ((GradientColor) strokeColors).getShader();
                    } else if (strokeColors.isStateful() == null && strokeColors.canApplyTheme() == null) {
                        this.mStrokeColors = null;
                    } else {
                        this.mStrokeColors = strokeColors;
                    }
                    strokeColor2 = strokeColors.getDefaultColor();
                } else {
                    strokeColor2 = strokeColor;
                }
                long j = this.mNativePtr;
                long j2 = 0;
                if (fillGradient != null) {
                    strokeColor3 = strokeColor2;
                    fillType = byteCount2;
                    byteCount2 = fillGradient.getNativeInstance();
                } else {
                    fillType = byteCount2;
                    strokeColor3 = strokeColor2;
                    byteCount2 = 0;
                }
                VectorDrawable.nUpdateFullPathFillGradient(j, byteCount2);
                byteCount2 = this.mNativePtr;
                if (strokeGradient != null) {
                    j2 = strokeGradient.getNativeInstance();
                }
                VectorDrawable.nUpdateFullPathStrokeGradient(byteCount2, j2);
                byteCount2 = typedArray.getFloat(12, fillAlpha);
                success = typedArray.getInt(true, strokeLineCap);
                int strokeLineJoin2 = typedArray.getInt(9, strokeLineJoin);
                strokeMiterLimit = typedArray.getFloat(10, strokeMiterLimit);
                float strokeAlpha2 = typedArray.getFloat(1.5E-44f, strokeAlpha);
                strokeWidth = typedArray.getFloat(4, strokeWidth);
                float trimPathEnd2 = typedArray.getFloat(8.4E-45f, trimPathEnd);
                trimPathOffset = typedArray.getFloat(7, trimPathOffset);
                VectorDrawable.nUpdateFullPathProperties(this.mNativePtr, strokeWidth, strokeColor3, strokeAlpha2, fillColor2, byteCount2, typedArray.getFloat(7.0E-45f, trimPathStart), trimPathEnd2, trimPathOffset, strokeMiterLimit, success, strokeLineJoin2, typedArray.getInt(13, fillType));
                return;
            }
            byteCount = 48;
            boolean z = success;
            throw new RuntimeException("Error: inconsistent property count");
        }

        public boolean canApplyTheme() {
            if (this.mThemeAttrs != null) {
                return true;
            }
            boolean fillCanApplyTheme = canComplexColorApplyTheme(this.mFillColors);
            boolean strokeCanApplyTheme = canComplexColorApplyTheme(this.mStrokeColors);
            if (fillCanApplyTheme || strokeCanApplyTheme) {
                return true;
            }
            return false;
        }

        public void applyTheme(Theme t) {
            ComplexColor complexColor;
            TypedArray a = this.mThemeAttrs;
            if (a != null) {
                a = t.resolveAttributes(a, R.styleable.VectorDrawablePath);
                updateStateFromTypedArray(a);
                a.recycle();
            }
            boolean fillCanApplyTheme = canComplexColorApplyTheme(this.mFillColors);
            boolean strokeCanApplyTheme = canComplexColorApplyTheme(this.mStrokeColors);
            if (fillCanApplyTheme) {
                this.mFillColors = this.mFillColors.obtainForTheme(t);
                complexColor = this.mFillColors;
                if (complexColor instanceof GradientColor) {
                    VectorDrawable.nUpdateFullPathFillGradient(this.mNativePtr, ((GradientColor) complexColor).getShader().getNativeInstance());
                } else if (complexColor instanceof ColorStateList) {
                    VectorDrawable.nSetFillColor(this.mNativePtr, complexColor.getDefaultColor());
                }
            }
            if (strokeCanApplyTheme) {
                this.mStrokeColors = this.mStrokeColors.obtainForTheme(t);
                complexColor = this.mStrokeColors;
                if (complexColor instanceof GradientColor) {
                    VectorDrawable.nUpdateFullPathStrokeGradient(this.mNativePtr, ((GradientColor) complexColor).getShader().getNativeInstance());
                } else if (complexColor instanceof ColorStateList) {
                    VectorDrawable.nSetStrokeColor(this.mNativePtr, complexColor.getDefaultColor());
                }
            }
        }

        private boolean canComplexColorApplyTheme(ComplexColor complexColor) {
            return complexColor != null && complexColor.canApplyTheme();
        }

        /* Access modifiers changed, original: 0000 */
        public int getStrokeColor() {
            return isTreeValid() ? VectorDrawable.nGetStrokeColor(this.mNativePtr) : 0;
        }

        /* Access modifiers changed, original: 0000 */
        public void setStrokeColor(int strokeColor) {
            this.mStrokeColors = null;
            if (isTreeValid()) {
                VectorDrawable.nSetStrokeColor(this.mNativePtr, strokeColor);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public float getStrokeWidth() {
            return isTreeValid() ? VectorDrawable.nGetStrokeWidth(this.mNativePtr) : 0.0f;
        }

        /* Access modifiers changed, original: 0000 */
        public void setStrokeWidth(float strokeWidth) {
            if (isTreeValid()) {
                VectorDrawable.nSetStrokeWidth(this.mNativePtr, strokeWidth);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public float getStrokeAlpha() {
            return isTreeValid() ? VectorDrawable.nGetStrokeAlpha(this.mNativePtr) : 0.0f;
        }

        /* Access modifiers changed, original: 0000 */
        public void setStrokeAlpha(float strokeAlpha) {
            if (isTreeValid()) {
                VectorDrawable.nSetStrokeAlpha(this.mNativePtr, strokeAlpha);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public int getFillColor() {
            return isTreeValid() ? VectorDrawable.nGetFillColor(this.mNativePtr) : 0;
        }

        /* Access modifiers changed, original: 0000 */
        public void setFillColor(int fillColor) {
            this.mFillColors = null;
            if (isTreeValid()) {
                VectorDrawable.nSetFillColor(this.mNativePtr, fillColor);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public float getFillAlpha() {
            return isTreeValid() ? VectorDrawable.nGetFillAlpha(this.mNativePtr) : 0.0f;
        }

        /* Access modifiers changed, original: 0000 */
        public void setFillAlpha(float fillAlpha) {
            if (isTreeValid()) {
                VectorDrawable.nSetFillAlpha(this.mNativePtr, fillAlpha);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public float getTrimPathStart() {
            return isTreeValid() ? VectorDrawable.nGetTrimPathStart(this.mNativePtr) : 0.0f;
        }

        /* Access modifiers changed, original: 0000 */
        public void setTrimPathStart(float trimPathStart) {
            if (isTreeValid()) {
                VectorDrawable.nSetTrimPathStart(this.mNativePtr, trimPathStart);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public float getTrimPathEnd() {
            return isTreeValid() ? VectorDrawable.nGetTrimPathEnd(this.mNativePtr) : 0.0f;
        }

        /* Access modifiers changed, original: 0000 */
        public void setTrimPathEnd(float trimPathEnd) {
            if (isTreeValid()) {
                VectorDrawable.nSetTrimPathEnd(this.mNativePtr, trimPathEnd);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public float getTrimPathOffset() {
            return isTreeValid() ? VectorDrawable.nGetTrimPathOffset(this.mNativePtr) : 0.0f;
        }

        /* Access modifiers changed, original: 0000 */
        public void setTrimPathOffset(float trimPathOffset) {
            if (isTreeValid()) {
                VectorDrawable.nSetTrimPathOffset(this.mNativePtr, trimPathOffset);
            }
        }
    }

    static class VGroup extends VObject {
        private static final int NATIVE_ALLOCATION_SIZE = 100;
        private static final Property<VGroup, Float> PIVOT_X = new FloatProperty<VGroup>("pivotX") {
            public void setValue(VGroup object, float value) {
                object.setPivotX(value);
            }

            public Float get(VGroup object) {
                return Float.valueOf(object.getPivotX());
            }
        };
        private static final int PIVOT_X_INDEX = 1;
        private static final Property<VGroup, Float> PIVOT_Y = new FloatProperty<VGroup>("pivotY") {
            public void setValue(VGroup object, float value) {
                object.setPivotY(value);
            }

            public Float get(VGroup object) {
                return Float.valueOf(object.getPivotY());
            }
        };
        private static final int PIVOT_Y_INDEX = 2;
        private static final Property<VGroup, Float> ROTATION = new FloatProperty<VGroup>("rotation") {
            public void setValue(VGroup object, float value) {
                object.setRotation(value);
            }

            public Float get(VGroup object) {
                return Float.valueOf(object.getRotation());
            }
        };
        private static final int ROTATION_INDEX = 0;
        private static final Property<VGroup, Float> SCALE_X = new FloatProperty<VGroup>("scaleX") {
            public void setValue(VGroup object, float value) {
                object.setScaleX(value);
            }

            public Float get(VGroup object) {
                return Float.valueOf(object.getScaleX());
            }
        };
        private static final int SCALE_X_INDEX = 3;
        private static final Property<VGroup, Float> SCALE_Y = new FloatProperty<VGroup>("scaleY") {
            public void setValue(VGroup object, float value) {
                object.setScaleY(value);
            }

            public Float get(VGroup object) {
                return Float.valueOf(object.getScaleY());
            }
        };
        private static final int SCALE_Y_INDEX = 4;
        private static final int TRANSFORM_PROPERTY_COUNT = 7;
        private static final Property<VGroup, Float> TRANSLATE_X = new FloatProperty<VGroup>("translateX") {
            public void setValue(VGroup object, float value) {
                object.setTranslateX(value);
            }

            public Float get(VGroup object) {
                return Float.valueOf(object.getTranslateX());
            }
        };
        private static final int TRANSLATE_X_INDEX = 5;
        private static final Property<VGroup, Float> TRANSLATE_Y = new FloatProperty<VGroup>("translateY") {
            public void setValue(VGroup object, float value) {
                object.setTranslateY(value);
            }

            public Float get(VGroup object) {
                return Float.valueOf(object.getTranslateY());
            }
        };
        private static final int TRANSLATE_Y_INDEX = 6;
        private static final HashMap<String, Integer> sPropertyIndexMap = new HashMap<String, Integer>() {
            {
                put("translateX", Integer.valueOf(5));
                put("translateY", Integer.valueOf(6));
                put("scaleX", Integer.valueOf(3));
                put("scaleY", Integer.valueOf(4));
                put("pivotX", Integer.valueOf(1));
                put("pivotY", Integer.valueOf(2));
                put("rotation", Integer.valueOf(0));
            }
        };
        private static final HashMap<String, Property> sPropertyMap = new HashMap<String, Property>() {
            {
                put("translateX", VGroup.TRANSLATE_X);
                put("translateY", VGroup.TRANSLATE_Y);
                put("scaleX", VGroup.SCALE_X);
                put("scaleY", VGroup.SCALE_Y);
                put("pivotX", VGroup.PIVOT_X);
                put("pivotY", VGroup.PIVOT_Y);
                put("rotation", VGroup.ROTATION);
            }
        };
        private int mChangingConfigurations;
        private final ArrayList<VObject> mChildren;
        private String mGroupName;
        private boolean mIsStateful;
        private final long mNativePtr;
        private int[] mThemeAttrs;
        private float[] mTransform;

        static int getPropertyIndex(String propertyName) {
            if (sPropertyIndexMap.containsKey(propertyName)) {
                return ((Integer) sPropertyIndexMap.get(propertyName)).intValue();
            }
            return -1;
        }

        public VGroup(VGroup copy, ArrayMap<String, Object> targetsMap) {
            this.mChildren = new ArrayList();
            this.mGroupName = null;
            this.mIsStateful = copy.mIsStateful;
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mGroupName = copy.mGroupName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            String str = this.mGroupName;
            if (str != null) {
                targetsMap.put(str, this);
            }
            this.mNativePtr = VectorDrawable.nCreateGroup(copy.mNativePtr);
            ArrayList<VObject> children = copy.mChildren;
            for (int i = 0; i < children.size(); i++) {
                VObject copyChild = (VObject) children.get(i);
                if (copyChild instanceof VGroup) {
                    addChild(new VGroup((VGroup) copyChild, targetsMap));
                } else {
                    VPath newPath;
                    if (copyChild instanceof VFullPath) {
                        newPath = new VFullPath((VFullPath) copyChild);
                    } else if (copyChild instanceof VClipPath) {
                        newPath = new VClipPath((VClipPath) copyChild);
                    } else {
                        throw new IllegalStateException("Unknown object in the tree!");
                    }
                    addChild(newPath);
                    if (newPath.mPathName != null) {
                        targetsMap.put(newPath.mPathName, newPath);
                    }
                }
            }
        }

        public VGroup() {
            this.mChildren = new ArrayList();
            this.mGroupName = null;
            this.mNativePtr = VectorDrawable.nCreateGroup();
        }

        /* Access modifiers changed, original: 0000 */
        public Property getProperty(String propertyName) {
            if (sPropertyMap.containsKey(propertyName)) {
                return (Property) sPropertyMap.get(propertyName);
            }
            return null;
        }

        public String getGroupName() {
            return this.mGroupName;
        }

        public void addChild(VObject child) {
            VectorDrawable.nAddChild(this.mNativePtr, child.getNativePtr());
            this.mChildren.add(child);
            this.mIsStateful |= child.isStateful();
        }

        public void setTree(VirtualRefBasePtr treeRoot) {
            super.setTree(treeRoot);
            for (int i = 0; i < this.mChildren.size(); i++) {
                ((VObject) this.mChildren.get(i)).setTree(treeRoot);
            }
        }

        public long getNativePtr() {
            return this.mNativePtr;
        }

        public void inflate(Resources res, AttributeSet attrs, Theme theme) {
            TypedArray a = Drawable.obtainAttributes(res, theme, attrs, R.styleable.VectorDrawableGroup);
            updateStateFromTypedArray(a);
            a.recycle();
        }

        /* Access modifiers changed, original: 0000 */
        public void updateStateFromTypedArray(TypedArray a) {
            TypedArray typedArray = a;
            this.mChangingConfigurations |= a.getChangingConfigurations();
            this.mThemeAttrs = a.extractThemeAttrs();
            if (this.mTransform == null) {
                this.mTransform = new float[7];
            }
            if (VectorDrawable.nGetGroupProperties(this.mNativePtr, this.mTransform, 7)) {
                float rotate = typedArray.getFloat(5, this.mTransform[0]);
                float pivotX = typedArray.getFloat(1, this.mTransform[1]);
                float pivotY = typedArray.getFloat(2, this.mTransform[2]);
                float scaleX = typedArray.getFloat(3, this.mTransform[3]);
                float scaleY = typedArray.getFloat(4, this.mTransform[4]);
                float translateX = typedArray.getFloat(6, this.mTransform[5]);
                float translateY = typedArray.getFloat(7, this.mTransform[6]);
                String groupName = typedArray.getString(0);
                if (groupName != null) {
                    this.mGroupName = groupName;
                    VectorDrawable.nSetName(this.mNativePtr, this.mGroupName);
                }
                VectorDrawable.nUpdateGroupProperties(this.mNativePtr, rotate, pivotX, pivotY, scaleX, scaleY, translateX, translateY);
                return;
            }
            throw new RuntimeException("Error: inconsistent property count");
        }

        public boolean onStateChange(int[] stateSet) {
            boolean changed = false;
            ArrayList<VObject> children = this.mChildren;
            int count = children.size();
            for (int i = 0; i < count; i++) {
                VObject child = (VObject) children.get(i);
                if (child.isStateful()) {
                    changed |= child.onStateChange(stateSet);
                }
            }
            return changed;
        }

        public boolean isStateful() {
            return this.mIsStateful;
        }

        public boolean hasFocusStateSpecified() {
            boolean result = false;
            ArrayList<VObject> children = this.mChildren;
            int count = children.size();
            for (int i = 0; i < count; i++) {
                VObject child = (VObject) children.get(i);
                if (child.isStateful()) {
                    result |= child.hasFocusStateSpecified();
                }
            }
            return result;
        }

        /* Access modifiers changed, original: 0000 */
        public int getNativeSize() {
            int size = 100;
            for (int i = 0; i < this.mChildren.size(); i++) {
                size += ((VObject) this.mChildren.get(i)).getNativeSize();
            }
            return size;
        }

        public boolean canApplyTheme() {
            if (this.mThemeAttrs != null) {
                return true;
            }
            ArrayList<VObject> children = this.mChildren;
            int count = children.size();
            for (int i = 0; i < count; i++) {
                if (((VObject) children.get(i)).canApplyTheme()) {
                    return true;
                }
            }
            return false;
        }

        public void applyTheme(Theme t) {
            TypedArray a = this.mThemeAttrs;
            if (a != null) {
                a = t.resolveAttributes(a, R.styleable.VectorDrawableGroup);
                updateStateFromTypedArray(a);
                a.recycle();
            }
            ArrayList<VObject> children = this.mChildren;
            int count = children.size();
            for (int i = 0; i < count; i++) {
                VObject child = (VObject) children.get(i);
                if (child.canApplyTheme()) {
                    child.applyTheme(t);
                    this.mIsStateful |= child.isStateful();
                }
            }
        }

        public float getRotation() {
            return isTreeValid() ? VectorDrawable.nGetRotation(this.mNativePtr) : 0.0f;
        }

        @UnsupportedAppUsage
        public void setRotation(float rotation) {
            if (isTreeValid()) {
                VectorDrawable.nSetRotation(this.mNativePtr, rotation);
            }
        }

        public float getPivotX() {
            return isTreeValid() ? VectorDrawable.nGetPivotX(this.mNativePtr) : 0.0f;
        }

        @UnsupportedAppUsage
        public void setPivotX(float pivotX) {
            if (isTreeValid()) {
                VectorDrawable.nSetPivotX(this.mNativePtr, pivotX);
            }
        }

        public float getPivotY() {
            return isTreeValid() ? VectorDrawable.nGetPivotY(this.mNativePtr) : 0.0f;
        }

        @UnsupportedAppUsage
        public void setPivotY(float pivotY) {
            if (isTreeValid()) {
                VectorDrawable.nSetPivotY(this.mNativePtr, pivotY);
            }
        }

        public float getScaleX() {
            return isTreeValid() ? VectorDrawable.nGetScaleX(this.mNativePtr) : 0.0f;
        }

        public void setScaleX(float scaleX) {
            if (isTreeValid()) {
                VectorDrawable.nSetScaleX(this.mNativePtr, scaleX);
            }
        }

        public float getScaleY() {
            return isTreeValid() ? VectorDrawable.nGetScaleY(this.mNativePtr) : 0.0f;
        }

        public void setScaleY(float scaleY) {
            if (isTreeValid()) {
                VectorDrawable.nSetScaleY(this.mNativePtr, scaleY);
            }
        }

        public float getTranslateX() {
            return isTreeValid() ? VectorDrawable.nGetTranslateX(this.mNativePtr) : 0.0f;
        }

        @UnsupportedAppUsage
        public void setTranslateX(float translateX) {
            if (isTreeValid()) {
                VectorDrawable.nSetTranslateX(this.mNativePtr, translateX);
            }
        }

        public float getTranslateY() {
            return isTreeValid() ? VectorDrawable.nGetTranslateY(this.mNativePtr) : 0.0f;
        }

        @UnsupportedAppUsage
        public void setTranslateY(float translateY) {
            if (isTreeValid()) {
                VectorDrawable.nSetTranslateY(this.mNativePtr, translateY);
            }
        }
    }

    static class VectorDrawableState extends ConstantState {
        static final Property<VectorDrawableState, Float> ALPHA = new FloatProperty<VectorDrawableState>("alpha") {
            public void setValue(VectorDrawableState state, float value) {
                state.setAlpha(value);
            }

            public Float get(VectorDrawableState state) {
                return Float.valueOf(state.getAlpha());
            }
        };
        private static final int NATIVE_ALLOCATION_SIZE = 316;
        private int mAllocationOfAllNodes = 0;
        boolean mAutoMirrored;
        int mBaseHeight = 0;
        int mBaseWidth = 0;
        BlendMode mBlendMode = Drawable.DEFAULT_BLEND_MODE;
        boolean mCacheDirty;
        boolean mCachedAutoMirrored;
        BlendMode mCachedBlendMode;
        int[] mCachedThemeAttrs;
        ColorStateList mCachedTint;
        int mChangingConfigurations;
        int mDensity = 160;
        int mLastHWCachePixelCount = 0;
        int mLastSWCachePixelCount = 0;
        VirtualRefBasePtr mNativeTree = null;
        Insets mOpticalInsets = Insets.NONE;
        VGroup mRootGroup;
        String mRootName = null;
        int[] mThemeAttrs;
        ColorStateList mTint = null;
        final ArrayMap<String, Object> mVGTargetsMap = new ArrayMap();
        float mViewportHeight = 0.0f;
        float mViewportWidth = 0.0f;

        /* Access modifiers changed, original: 0000 */
        public Property getProperty(String propertyName) {
            if (ALPHA.getName().equals(propertyName)) {
                return ALPHA;
            }
            return null;
        }

        public VectorDrawableState(VectorDrawableState copy) {
            if (copy != null) {
                this.mThemeAttrs = copy.mThemeAttrs;
                this.mChangingConfigurations = copy.mChangingConfigurations;
                this.mTint = copy.mTint;
                this.mBlendMode = copy.mBlendMode;
                this.mAutoMirrored = copy.mAutoMirrored;
                this.mRootGroup = new VGroup(copy.mRootGroup, this.mVGTargetsMap);
                createNativeTreeFromCopy(copy, this.mRootGroup);
                this.mBaseWidth = copy.mBaseWidth;
                this.mBaseHeight = copy.mBaseHeight;
                setViewportSize(copy.mViewportWidth, copy.mViewportHeight);
                this.mOpticalInsets = copy.mOpticalInsets;
                this.mRootName = copy.mRootName;
                this.mDensity = copy.mDensity;
                String str = copy.mRootName;
                if (str != null) {
                    this.mVGTargetsMap.put(str, this);
                }
            } else {
                this.mRootGroup = new VGroup();
                createNativeTree(this.mRootGroup);
            }
            onTreeConstructionFinished();
        }

        private void createNativeTree(VGroup rootGroup) {
            this.mNativeTree = new VirtualRefBasePtr(VectorDrawable.nCreateTree(rootGroup.mNativePtr));
            VMRuntime.getRuntime().registerNativeAllocation(316);
        }

        private void createNativeTreeFromCopy(VectorDrawableState copy, VGroup rootGroup) {
            this.mNativeTree = new VirtualRefBasePtr(VectorDrawable.nCreateTreeFromCopy(copy.mNativeTree.get(), rootGroup.mNativePtr));
            VMRuntime.getRuntime().registerNativeAllocation(316);
        }

        /* Access modifiers changed, original: 0000 */
        public void onTreeConstructionFinished() {
            this.mRootGroup.setTree(this.mNativeTree);
            this.mAllocationOfAllNodes = this.mRootGroup.getNativeSize();
            VMRuntime.getRuntime().registerNativeAllocation(this.mAllocationOfAllNodes);
        }

        /* Access modifiers changed, original: 0000 */
        public long getNativeRenderer() {
            VirtualRefBasePtr virtualRefBasePtr = this.mNativeTree;
            if (virtualRefBasePtr == null) {
                return 0;
            }
            return virtualRefBasePtr.get();
        }

        public boolean canReuseCache() {
            if (!this.mCacheDirty && this.mCachedThemeAttrs == this.mThemeAttrs && this.mCachedTint == this.mTint && this.mCachedBlendMode == this.mBlendMode && this.mCachedAutoMirrored == this.mAutoMirrored) {
                return true;
            }
            updateCacheStates();
            return false;
        }

        public void updateCacheStates() {
            this.mCachedThemeAttrs = this.mThemeAttrs;
            this.mCachedTint = this.mTint;
            this.mCachedBlendMode = this.mBlendMode;
            this.mCachedAutoMirrored = this.mAutoMirrored;
            this.mCacheDirty = false;
        }

        public void applyTheme(Theme t) {
            this.mRootGroup.applyTheme(t);
        }

        public boolean canApplyTheme() {
            if (this.mThemeAttrs == null) {
                VGroup vGroup = this.mRootGroup;
                if (vGroup == null || !vGroup.canApplyTheme()) {
                    ColorStateList colorStateList = this.mTint;
                    if ((colorStateList == null || !colorStateList.canApplyTheme()) && !super.canApplyTheme()) {
                        return false;
                    }
                }
            }
            return true;
        }

        public Drawable newDrawable() {
            return new VectorDrawable(this, null);
        }

        public Drawable newDrawable(Resources res) {
            return new VectorDrawable(this, res);
        }

        public int getChangingConfigurations() {
            int i = this.mChangingConfigurations;
            ColorStateList colorStateList = this.mTint;
            return i | (colorStateList != null ? colorStateList.getChangingConfigurations() : 0);
        }

        public boolean isStateful() {
            ColorStateList colorStateList = this.mTint;
            if (colorStateList == null || !colorStateList.isStateful()) {
                VGroup vGroup = this.mRootGroup;
                if (vGroup == null || !vGroup.isStateful()) {
                    return false;
                }
            }
            return true;
        }

        public boolean hasFocusStateSpecified() {
            ColorStateList colorStateList = this.mTint;
            if (colorStateList == null || !colorStateList.hasFocusStateSpecified()) {
                VGroup vGroup = this.mRootGroup;
                if (vGroup == null || !vGroup.hasFocusStateSpecified()) {
                    return false;
                }
            }
            return true;
        }

        /* Access modifiers changed, original: 0000 */
        public void setViewportSize(float viewportWidth, float viewportHeight) {
            this.mViewportWidth = viewportWidth;
            this.mViewportHeight = viewportHeight;
            VectorDrawable.nSetRendererViewportSize(getNativeRenderer(), viewportWidth, viewportHeight);
        }

        public final boolean setDensity(int targetDensity) {
            if (this.mDensity == targetDensity) {
                return false;
            }
            int sourceDensity = this.mDensity;
            this.mDensity = targetDensity;
            applyDensityScaling(sourceDensity, targetDensity);
            return true;
        }

        private void applyDensityScaling(int sourceDensity, int targetDensity) {
            this.mBaseWidth = Drawable.scaleFromDensity(this.mBaseWidth, sourceDensity, targetDensity, true);
            this.mBaseHeight = Drawable.scaleFromDensity(this.mBaseHeight, sourceDensity, targetDensity, true);
            this.mOpticalInsets = Insets.of(Drawable.scaleFromDensity(this.mOpticalInsets.left, sourceDensity, targetDensity, false), Drawable.scaleFromDensity(this.mOpticalInsets.top, sourceDensity, targetDensity, false), Drawable.scaleFromDensity(this.mOpticalInsets.right, sourceDensity, targetDensity, false), Drawable.scaleFromDensity(this.mOpticalInsets.bottom, sourceDensity, targetDensity, false));
        }

        public boolean onStateChange(int[] stateSet) {
            return this.mRootGroup.onStateChange(stateSet);
        }

        public void finalize() throws Throwable {
            super.finalize();
            VMRuntime.getRuntime().registerNativeFree((this.mAllocationOfAllNodes + 316) + ((this.mLastHWCachePixelCount * 4) + (this.mLastSWCachePixelCount * 4)));
        }

        public boolean setAlpha(float alpha) {
            return VectorDrawable.nSetRootAlpha(this.mNativeTree.get(), alpha);
        }

        public float getAlpha() {
            return VectorDrawable.nGetRootAlpha(this.mNativeTree.get());
        }
    }

    private static native void nAddChild(long j, long j2);

    private static native long nCreateClipPath();

    private static native long nCreateClipPath(long j);

    private static native long nCreateFullPath();

    private static native long nCreateFullPath(long j);

    private static native long nCreateGroup();

    private static native long nCreateGroup(long j);

    private static native long nCreateTree(long j);

    private static native long nCreateTreeFromCopy(long j, long j2);

    private static native int nDraw(long j, long j2, long j3, Rect rect, boolean z, boolean z2);

    private static native float nGetFillAlpha(long j);

    private static native int nGetFillColor(long j);

    private static native boolean nGetFullPathProperties(long j, byte[] bArr, int i);

    private static native boolean nGetGroupProperties(long j, float[] fArr, int i);

    private static native float nGetPivotX(long j);

    private static native float nGetPivotY(long j);

    private static native float nGetRootAlpha(long j);

    private static native float nGetRotation(long j);

    private static native float nGetScaleX(long j);

    private static native float nGetScaleY(long j);

    private static native float nGetStrokeAlpha(long j);

    private static native int nGetStrokeColor(long j);

    private static native float nGetStrokeWidth(long j);

    private static native float nGetTranslateX(long j);

    private static native float nGetTranslateY(long j);

    private static native float nGetTrimPathEnd(long j);

    private static native float nGetTrimPathOffset(long j);

    private static native float nGetTrimPathStart(long j);

    private static native void nSetAllowCaching(long j, boolean z);

    private static native void nSetAntiAlias(long j, boolean z);

    private static native void nSetFillAlpha(long j, float f);

    private static native void nSetFillColor(long j, int i);

    private static native void nSetName(long j, String str);

    private static native void nSetPathData(long j, long j2);

    private static native void nSetPathString(long j, String str, int i);

    private static native void nSetPivotX(long j, float f);

    private static native void nSetPivotY(long j, float f);

    private static native void nSetRendererViewportSize(long j, float f, float f2);

    private static native boolean nSetRootAlpha(long j, float f);

    private static native void nSetRotation(long j, float f);

    private static native void nSetScaleX(long j, float f);

    private static native void nSetScaleY(long j, float f);

    private static native void nSetStrokeAlpha(long j, float f);

    private static native void nSetStrokeColor(long j, int i);

    private static native void nSetStrokeWidth(long j, float f);

    private static native void nSetTranslateX(long j, float f);

    private static native void nSetTranslateY(long j, float f);

    private static native void nSetTrimPathEnd(long j, float f);

    private static native void nSetTrimPathOffset(long j, float f);

    private static native void nSetTrimPathStart(long j, float f);

    private static native void nUpdateFullPathFillGradient(long j, long j2);

    private static native void nUpdateFullPathProperties(long j, float f, int i, float f2, int i2, float f3, float f4, float f5, float f6, float f7, int i3, int i4, int i5);

    private static native void nUpdateFullPathStrokeGradient(long j, long j2);

    private static native void nUpdateGroupProperties(long j, float f, float f2, float f3, float f4, float f5, float f6, float f7);

    public VectorDrawable() {
        this(new VectorDrawableState(null), null);
    }

    private VectorDrawable(VectorDrawableState state, Resources res) {
        this.mDpiScaledWidth = 0;
        this.mDpiScaledHeight = 0;
        this.mDpiScaledInsets = Insets.NONE;
        this.mDpiScaledDirty = true;
        this.mTmpBounds = new Rect();
        this.mVectorState = state;
        updateLocalState(res);
    }

    private void updateLocalState(Resources res) {
        int density = Drawable.resolveDensity(res, this.mVectorState.mDensity);
        if (this.mTargetDensity != density) {
            this.mTargetDensity = density;
            this.mDpiScaledDirty = true;
        }
        updateColorFilters(this.mVectorState.mBlendMode, this.mVectorState.mTint);
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mVectorState = new VectorDrawableState(this.mVectorState);
            this.mMutated = true;
        }
        return this;
    }

    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    /* Access modifiers changed, original: 0000 */
    @UnsupportedAppUsage
    public Object getTargetByName(String name) {
        return this.mVectorState.mVGTargetsMap.get(name);
    }

    public ConstantState getConstantState() {
        this.mVectorState.mChangingConfigurations = getChangingConfigurations();
        return this.mVectorState;
    }

    public void draw(Canvas canvas) {
        copyBounds(this.mTmpBounds);
        if (this.mTmpBounds.width() > 0 && this.mTmpBounds.height() > 0) {
            long colorFilterNativeInstance;
            ColorFilter colorFilter = this.mColorFilter;
            if (colorFilter == null) {
                colorFilter = this.mBlendModeColorFilter;
            }
            if (colorFilter == null) {
                colorFilterNativeInstance = 0;
            } else {
                colorFilterNativeInstance = colorFilter.getNativeInstance();
            }
            int pixelCount = nDraw(this.mVectorState.getNativeRenderer(), canvas.getNativeCanvasWrapper(), colorFilterNativeInstance, this.mTmpBounds, needMirroring(), this.mVectorState.canReuseCache());
            if (pixelCount != 0) {
                int deltaInBytes;
                if (canvas.isHardwareAccelerated()) {
                    deltaInBytes = (pixelCount - this.mVectorState.mLastHWCachePixelCount) * 4;
                    this.mVectorState.mLastHWCachePixelCount = pixelCount;
                } else {
                    deltaInBytes = (pixelCount - this.mVectorState.mLastSWCachePixelCount) * 4;
                    this.mVectorState.mLastSWCachePixelCount = pixelCount;
                }
                if (deltaInBytes > 0) {
                    VMRuntime.getRuntime().registerNativeAllocation(deltaInBytes);
                } else if (deltaInBytes < 0) {
                    VMRuntime.getRuntime().registerNativeFree(-deltaInBytes);
                }
            }
        }
    }

    public int getAlpha() {
        return (int) (this.mVectorState.getAlpha() * 255.0f);
    }

    public void setAlpha(int alpha) {
        if (this.mVectorState.setAlpha(((float) alpha) / 255.0f)) {
            invalidateSelf();
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
        invalidateSelf();
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public void setTintList(ColorStateList tint) {
        VectorDrawableState state = this.mVectorState;
        if (state.mTint != tint) {
            state.mTint = tint;
            updateColorFilters(this.mVectorState.mBlendMode, tint);
            invalidateSelf();
        }
    }

    public void setTintBlendMode(BlendMode blendMode) {
        VectorDrawableState state = this.mVectorState;
        if (state.mBlendMode != blendMode) {
            state.mBlendMode = blendMode;
            updateColorFilters(state.mBlendMode, state.mTint);
            invalidateSelf();
        }
    }

    public boolean isStateful() {
        if (!super.isStateful()) {
            VectorDrawableState vectorDrawableState = this.mVectorState;
            if (vectorDrawableState == null || !vectorDrawableState.isStateful()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasFocusStateSpecified() {
        VectorDrawableState vectorDrawableState = this.mVectorState;
        return vectorDrawableState != null && vectorDrawableState.hasFocusStateSpecified();
    }

    /* Access modifiers changed, original: protected */
    public boolean onStateChange(int[] stateSet) {
        boolean changed = false;
        if (isStateful()) {
            mutate();
        }
        VectorDrawableState state = this.mVectorState;
        if (state.onStateChange(stateSet)) {
            changed = true;
            state.mCacheDirty = true;
        }
        if (state.mTint == null || state.mBlendMode == null) {
            return changed;
        }
        updateColorFilters(state.mBlendMode, state.mTint);
        return true;
    }

    private void updateColorFilters(BlendMode blendMode, ColorStateList tint) {
        this.mTintFilter = updateTintFilter(this.mTintFilter, tint, BlendMode.blendModeToPorterDuffMode(blendMode));
        this.mBlendModeColorFilter = updateBlendModeFilter(this.mBlendModeColorFilter, tint, blendMode);
    }

    public int getOpacity() {
        return getAlpha() == 0 ? -2 : -3;
    }

    public int getIntrinsicWidth() {
        if (this.mDpiScaledDirty) {
            computeVectorSize();
        }
        return this.mDpiScaledWidth;
    }

    public int getIntrinsicHeight() {
        if (this.mDpiScaledDirty) {
            computeVectorSize();
        }
        return this.mDpiScaledHeight;
    }

    public Insets getOpticalInsets() {
        if (this.mDpiScaledDirty) {
            computeVectorSize();
        }
        return this.mDpiScaledInsets;
    }

    /* Access modifiers changed, original: 0000 */
    public void computeVectorSize() {
        Insets opticalInsets = this.mVectorState.mOpticalInsets;
        int sourceDensity = this.mVectorState.mDensity;
        int targetDensity = this.mTargetDensity;
        if (targetDensity != sourceDensity) {
            this.mDpiScaledWidth = Drawable.scaleFromDensity(this.mVectorState.mBaseWidth, sourceDensity, targetDensity, true);
            this.mDpiScaledHeight = Drawable.scaleFromDensity(this.mVectorState.mBaseHeight, sourceDensity, targetDensity, true);
            this.mDpiScaledInsets = Insets.of(Drawable.scaleFromDensity(opticalInsets.left, sourceDensity, targetDensity, false), Drawable.scaleFromDensity(opticalInsets.top, sourceDensity, targetDensity, false), Drawable.scaleFromDensity(opticalInsets.right, sourceDensity, targetDensity, false), Drawable.scaleFromDensity(opticalInsets.bottom, sourceDensity, targetDensity, false));
        } else {
            this.mDpiScaledWidth = this.mVectorState.mBaseWidth;
            this.mDpiScaledHeight = this.mVectorState.mBaseHeight;
            this.mDpiScaledInsets = opticalInsets;
        }
        this.mDpiScaledDirty = false;
    }

    public boolean canApplyTheme() {
        VectorDrawableState vectorDrawableState = this.mVectorState;
        return (vectorDrawableState != null && vectorDrawableState.canApplyTheme()) || super.canApplyTheme();
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        VectorDrawableState state = this.mVectorState;
        if (state != null) {
            this.mDpiScaledDirty |= this.mVectorState.setDensity(Drawable.resolveDensity(t.getResources(), 0));
            if (state.mThemeAttrs != null) {
                TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.VectorDrawable);
                try {
                    state.mCacheDirty = true;
                    updateStateFromTypedArray(a);
                    a.recycle();
                    this.mDpiScaledDirty = true;
                } catch (XmlPullParserException e) {
                    throw new RuntimeException(e);
                } catch (Throwable th) {
                    a.recycle();
                }
            }
            if (state.mTint != null && state.mTint.canApplyTheme()) {
                state.mTint = state.mTint.obtainForTheme(t);
            }
            VectorDrawableState vectorDrawableState = this.mVectorState;
            if (vectorDrawableState != null && vectorDrawableState.canApplyTheme()) {
                this.mVectorState.applyTheme(t);
            }
            updateLocalState(t.getResources());
        }
    }

    public float getPixelSize() {
        VectorDrawableState vectorDrawableState = this.mVectorState;
        if (vectorDrawableState == null || vectorDrawableState.mBaseWidth == 0 || this.mVectorState.mBaseHeight == 0 || this.mVectorState.mViewportHeight == 0.0f || this.mVectorState.mViewportWidth == 0.0f) {
            return 1.0f;
        }
        float intrinsicWidth = (float) this.mVectorState.mBaseWidth;
        float intrinsicHeight = (float) this.mVectorState.mBaseHeight;
        return Math.min(this.mVectorState.mViewportWidth / intrinsicWidth, this.mVectorState.mViewportHeight / intrinsicHeight);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0022 A:{Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }} */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0019 A:{Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }} */
    public static android.graphics.drawable.VectorDrawable create(android.content.res.Resources r6, int r7) {
        /*
        r0 = "parser error";
        r1 = r6.getXml(r7);	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
        r2 = android.util.Xml.asAttributeSet(r1);	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
    L_0x000b:
        r3 = r1.next();	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
        r4 = r3;
        r5 = 2;
        if (r3 == r5) goto L_0x0017;
    L_0x0013:
        r3 = 1;
        if (r4 == r3) goto L_0x0017;
    L_0x0016:
        goto L_0x000b;
    L_0x0017:
        if (r4 != r5) goto L_0x0022;
    L_0x0019:
        r3 = new android.graphics.drawable.VectorDrawable;	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
        r3.<init>();	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
        r3.inflate(r6, r1, r2);	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
        return r3;
    L_0x0022:
        r3 = new org.xmlpull.v1.XmlPullParserException;	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
        r5 = "No start tag found";
        r3.<init>(r5);	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
        throw r3;	 Catch:{ XmlPullParserException -> 0x0031, IOException -> 0x002a }
    L_0x002a:
        r1 = move-exception;
        r2 = LOGTAG;
        android.util.Log.e(r2, r0, r1);
        goto L_0x0038;
    L_0x0031:
        r1 = move-exception;
        r2 = LOGTAG;
        android.util.Log.e(r2, r0, r1);
    L_0x0038:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.VectorDrawable.create(android.content.res.Resources, int):android.graphics.drawable.VectorDrawable");
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        try {
            Trace.traceBegin(8192, "VectorDrawable#inflate");
            if (!(this.mVectorState.mRootGroup == null && this.mVectorState.mNativeTree == null)) {
                if (this.mVectorState.mRootGroup != null) {
                    VMRuntime.getRuntime().registerNativeFree(this.mVectorState.mRootGroup.getNativeSize());
                    this.mVectorState.mRootGroup.setTree(null);
                }
                this.mVectorState.mRootGroup = new VGroup();
                if (this.mVectorState.mNativeTree != null) {
                    VMRuntime.getRuntime().registerNativeFree(316);
                    this.mVectorState.mNativeTree.release();
                }
                this.mVectorState.createNativeTree(this.mVectorState.mRootGroup);
            }
            VectorDrawableState state = this.mVectorState;
            state.setDensity(Drawable.resolveDensity(r, 0));
            TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.VectorDrawable);
            updateStateFromTypedArray(a);
            a.recycle();
            this.mDpiScaledDirty = true;
            state.mCacheDirty = true;
            inflateChildElements(r, parser, attrs, theme);
            state.onTreeConstructionFinished();
            updateLocalState(r);
        } finally {
            Trace.traceEnd(8192);
        }
    }

    private void updateStateFromTypedArray(TypedArray a) throws XmlPullParserException {
        VectorDrawableState state = this.mVectorState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        int tintMode = a.getInt(6, -1);
        if (tintMode != -1) {
            state.mBlendMode = Drawable.parseBlendMode(tintMode, BlendMode.SRC_IN);
        }
        ColorStateList tint = a.getColorStateList(1);
        if (tint != null) {
            state.mTint = tint;
        }
        state.mAutoMirrored = a.getBoolean(5, state.mAutoMirrored);
        state.setViewportSize(a.getFloat(9.8E-45f, state.mViewportWidth), a.getFloat(1.1E-44f, state.mViewportHeight));
        StringBuilder stringBuilder;
        if (state.mViewportWidth <= 0.0f) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(a.getPositionDescription());
            stringBuilder.append("<vector> tag requires viewportWidth > 0");
            throw new XmlPullParserException(stringBuilder.toString());
        } else if (state.mViewportHeight > 0.0f) {
            state.mBaseWidth = a.getDimensionPixelSize(3, state.mBaseWidth);
            state.mBaseHeight = a.getDimensionPixelSize(2, state.mBaseHeight);
            if (state.mBaseWidth <= 0) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(a.getPositionDescription());
                stringBuilder.append("<vector> tag requires width > 0");
                throw new XmlPullParserException(stringBuilder.toString());
            } else if (state.mBaseHeight > 0) {
                state.mOpticalInsets = Insets.of(a.getDimensionPixelOffset(9, state.mOpticalInsets.left), a.getDimensionPixelOffset(10, state.mOpticalInsets.top), a.getDimensionPixelOffset(11, state.mOpticalInsets.right), a.getDimensionPixelOffset(12, state.mOpticalInsets.bottom));
                state.setAlpha(a.getFloat(5.6E-45f, state.getAlpha()));
                String name = a.getString(null);
                if (name != null) {
                    state.mRootName = name;
                    state.mVGTargetsMap.put(name, state);
                }
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(a.getPositionDescription());
                stringBuilder.append("<vector> tag requires height > 0");
                throw new XmlPullParserException(stringBuilder.toString());
            }
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(a.getPositionDescription());
            stringBuilder.append("<vector> tag requires viewportHeight > 0");
            throw new XmlPullParserException(stringBuilder.toString());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00fc A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00cd  */
    private void inflateChildElements(android.content.res.Resources r12, org.xmlpull.v1.XmlPullParser r13, android.util.AttributeSet r14, android.content.res.Resources.Theme r15) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r11 = this;
        r0 = r11.mVectorState;
        r1 = 1;
        r2 = new java.util.Stack;
        r2.<init>();
        r3 = r0.mRootGroup;
        r2.push(r3);
        r3 = r13.getEventType();
        r4 = r13.getDepth();
        r5 = 1;
        r4 = r4 + r5;
    L_0x0017:
        r6 = "path";
        if (r3 == r5) goto L_0x00cb;
    L_0x001c:
        r7 = r13.getDepth();
        r8 = 3;
        if (r7 >= r4) goto L_0x0025;
    L_0x0023:
        if (r3 == r8) goto L_0x00cb;
    L_0x0025:
        r7 = 2;
        r9 = "group";
        if (r3 != r7) goto L_0x00b6;
    L_0x002a:
        r7 = r13.getName();
        r8 = r2.peek();
        r8 = (android.graphics.drawable.VectorDrawable.VGroup) r8;
        r6 = r6.equals(r7);
        if (r6 == 0) goto L_0x005d;
    L_0x003a:
        r6 = new android.graphics.drawable.VectorDrawable$VFullPath;
        r6.<init>();
        r6.inflate(r12, r14, r15);
        r8.addChild(r6);
        r9 = r6.getPathName();
        if (r9 == 0) goto L_0x0054;
    L_0x004b:
        r9 = r0.mVGTargetsMap;
        r10 = r6.getPathName();
        r9.put(r10, r6);
    L_0x0054:
        r1 = 0;
        r9 = r0.mChangingConfigurations;
        r10 = r6.mChangingConfigurations;
        r9 = r9 | r10;
        r0.mChangingConfigurations = r9;
        goto L_0x00b5;
    L_0x005d:
        r6 = "clip-path";
        r6 = r6.equals(r7);
        if (r6 == 0) goto L_0x0087;
    L_0x0065:
        r6 = new android.graphics.drawable.VectorDrawable$VClipPath;
        r6.<init>();
        r6.inflate(r12, r14, r15);
        r8.addChild(r6);
        r9 = r6.getPathName();
        if (r9 == 0) goto L_0x007f;
    L_0x0076:
        r9 = r0.mVGTargetsMap;
        r10 = r6.getPathName();
        r9.put(r10, r6);
    L_0x007f:
        r9 = r0.mChangingConfigurations;
        r10 = r6.mChangingConfigurations;
        r9 = r9 | r10;
        r0.mChangingConfigurations = r9;
        goto L_0x00b4;
    L_0x0087:
        r6 = r9.equals(r7);
        if (r6 == 0) goto L_0x00b4;
    L_0x008d:
        r6 = new android.graphics.drawable.VectorDrawable$VGroup;
        r6.<init>();
        r6.inflate(r12, r14, r15);
        r8.addChild(r6);
        r2.push(r6);
        r9 = r6.getGroupName();
        if (r9 == 0) goto L_0x00aa;
    L_0x00a1:
        r9 = r0.mVGTargetsMap;
        r10 = r6.getGroupName();
        r9.put(r10, r6);
    L_0x00aa:
        r9 = r0.mChangingConfigurations;
        r10 = r6.mChangingConfigurations;
        r9 = r9 | r10;
        r0.mChangingConfigurations = r9;
        goto L_0x00b5;
    L_0x00b5:
        goto L_0x00c5;
    L_0x00b6:
        if (r3 != r8) goto L_0x00b5;
    L_0x00b8:
        r6 = r13.getName();
        r7 = r9.equals(r6);
        if (r7 == 0) goto L_0x00c5;
    L_0x00c2:
        r2.pop();
    L_0x00c5:
        r3 = r13.next();
        goto L_0x0017;
    L_0x00cb:
        if (r1 == 0) goto L_0x00fc;
    L_0x00cd:
        r5 = new java.lang.StringBuffer;
        r5.<init>();
        r7 = r5.length();
        if (r7 <= 0) goto L_0x00dd;
    L_0x00d8:
        r7 = " or ";
        r5.append(r7);
    L_0x00dd:
        r5.append(r6);
        r6 = new org.xmlpull.v1.XmlPullParserException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "no ";
        r7.append(r8);
        r7.append(r5);
        r8 = " defined";
        r7.append(r8);
        r7 = r7.toString();
        r6.<init>(r7);
        throw r6;
    L_0x00fc:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.VectorDrawable.inflateChildElements(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mVectorState.getChangingConfigurations();
    }

    /* Access modifiers changed, original: 0000 */
    @UnsupportedAppUsage
    public void setAllowCaching(boolean allowCaching) {
        nSetAllowCaching(this.mVectorState.getNativeRenderer(), allowCaching);
    }

    private boolean needMirroring() {
        return isAutoMirrored() && getLayoutDirection() == 1;
    }

    public void setAutoMirrored(boolean mirrored) {
        if (this.mVectorState.mAutoMirrored != mirrored) {
            this.mVectorState.mAutoMirrored = mirrored;
            invalidateSelf();
        }
    }

    public boolean isAutoMirrored() {
        return this.mVectorState.mAutoMirrored;
    }

    public long getNativeTree() {
        return this.mVectorState.getNativeRenderer();
    }

    public void setAntiAlias(boolean aa) {
        nSetAntiAlias(this.mVectorState.mNativeTree.get(), aa);
    }
}
