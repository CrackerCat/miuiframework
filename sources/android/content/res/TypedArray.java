package android.content.res;

import android.annotation.UnsupportedAppUsage;
import android.content.pm.ActivityInfo;
import android.content.res.Resources.Theme;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import dalvik.system.VMRuntime;
import java.util.Arrays;

public class TypedArray {
    static final int STYLE_ASSET_COOKIE = 2;
    static final int STYLE_CHANGING_CONFIGURATIONS = 4;
    static final int STYLE_DATA = 1;
    static final int STYLE_DENSITY = 5;
    static final int STYLE_NUM_ENTRIES = 7;
    static final int STYLE_RESOURCE_ID = 3;
    static final int STYLE_SOURCE_RESOURCE_ID = 6;
    static final int STYLE_TYPE = 0;
    @UnsupportedAppUsage
    private AssetManager mAssets;
    @UnsupportedAppUsage
    int[] mData;
    long mDataAddress;
    @UnsupportedAppUsage
    int[] mIndices;
    long mIndicesAddress;
    @UnsupportedAppUsage
    int mLength;
    @UnsupportedAppUsage
    private DisplayMetrics mMetrics;
    @UnsupportedAppUsage
    private boolean mRecycled;
    @UnsupportedAppUsage
    private final Resources mResources;
    @UnsupportedAppUsage
    Theme mTheme;
    @UnsupportedAppUsage
    TypedValue mValue = new TypedValue();
    @UnsupportedAppUsage
    Parser mXml;

    static TypedArray obtain(Resources res, int len) {
        TypedArray attrs = (TypedArray) res.mTypedArrayPool.acquire();
        if (attrs == null) {
            attrs = new MiuiTypedArray(res);
        }
        attrs.mRecycled = false;
        attrs.mAssets = res.getAssets();
        attrs.mMetrics = res.getDisplayMetrics();
        attrs.resize(len);
        return attrs;
    }

    private void resize(int len) {
        this.mLength = len;
        int dataLen = len * 7;
        int indicesLen = len + 1;
        VMRuntime runtime = VMRuntime.getRuntime();
        if (this.mDataAddress == 0 || this.mData.length < dataLen) {
            this.mData = (int[]) runtime.newNonMovableArray(Integer.TYPE, dataLen);
            this.mDataAddress = runtime.addressOf(this.mData);
            this.mIndices = (int[]) runtime.newNonMovableArray(Integer.TYPE, indicesLen);
            this.mIndicesAddress = runtime.addressOf(this.mIndices);
        }
    }

    public int length() {
        if (!this.mRecycled) {
            return this.mLength;
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public int getIndexCount() {
        if (!this.mRecycled) {
            return this.mIndices[0];
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public int getIndex(int at) {
        if (!this.mRecycled) {
            return this.mIndices[at + 1];
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public Resources getResources() {
        if (!this.mRecycled) {
            return this.mResources;
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public CharSequence getText(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int type = this.mData[index + 0];
        if (type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index);
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            return v.coerceToString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getText of bad type: 0x");
        stringBuilder.append(Integer.toHexString(type));
        throw new RuntimeException(stringBuilder.toString());
    }

    public String getString(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int type = this.mData[index + 0];
        String str = null;
        if (type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index).toString();
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            CharSequence cs = v.coerceToString();
            if (cs != null) {
                str = cs.toString();
            }
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getString of bad type: 0x");
        stringBuilder.append(Integer.toHexString(type));
        throw new RuntimeException(stringBuilder.toString());
    }

    public String getNonResourceString(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        if (data[index + 0] != 3 || data[index + 2] >= 0) {
            return null;
        }
        return this.mXml.getPooledString(data[index + 1]).toString();
    }

    @UnsupportedAppUsage
    public String getNonConfigurationString(int index, int allowedChangingConfigs) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        String str = null;
        if (((~allowedChangingConfigs) & ActivityInfo.activityInfoConfigNativeToJava(data[index + 4])) != 0 || type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index).toString();
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            CharSequence cs = v.coerceToString();
            if (cs != null) {
                str = cs.toString();
            }
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getNonConfigurationString of bad type: 0x");
        stringBuilder.append(Integer.toHexString(type));
        throw new RuntimeException(stringBuilder.toString());
    }

    public boolean getBoolean(int index, boolean defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type < 16 || type > 31) {
            TypedValue v = this.mValue;
            if (getValueAt(index, v)) {
                StrictMode.noteResourceMismatch(v);
                return XmlUtils.convertValueToBoolean(v.coerceToString(), defValue);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getBoolean of bad type: 0x");
            stringBuilder.append(Integer.toHexString(type));
            throw new RuntimeException(stringBuilder.toString());
        }
        return data[index + 1] != 0;
    }

    public int getInt(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            StrictMode.noteResourceMismatch(v);
            return XmlUtils.convertValueToInt(v.coerceToString(), defValue);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getInt of bad type: 0x");
        stringBuilder.append(Integer.toHexString(type));
        throw new RuntimeException(stringBuilder.toString());
    }

    public float getFloat(int index, float defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 4) {
            return Float.intBitsToFloat(data[index + 1]);
        }
        if (type >= 16 && type <= 31) {
            return (float) data[index + 1];
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            CharSequence str = v.coerceToString();
            if (str != null) {
                StrictMode.noteResourceMismatch(v);
                return Float.parseFloat(str.toString());
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getFloat of bad type: 0x");
        stringBuilder.append(Integer.toHexString(type));
        throw new RuntimeException(stringBuilder.toString());
    }

    public int getColor(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int attrIndex = index;
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        TypedValue value;
        if (type == 3) {
            value = this.mValue;
            if (getValueAt(index, value)) {
                return this.mResources.loadColorStateList(value, value.resourceId, this.mTheme).getDefaultColor();
            }
            return defValue;
        } else if (type == 2) {
            value = this.mValue;
            getValueAt(index, value);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to resolve attribute at index ");
            stringBuilder.append(attrIndex);
            stringBuilder.append(": ");
            stringBuilder.append(value);
            throw new UnsupportedOperationException(stringBuilder.toString());
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Can't convert value at index ");
            stringBuilder2.append(attrIndex);
            stringBuilder2.append(" to color: type=0x");
            stringBuilder2.append(Integer.toHexString(type));
            throw new UnsupportedOperationException(stringBuilder2.toString());
        }
    }

    public ComplexColor getComplexColor(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (!getValueAt(index * 7, value)) {
            return null;
        }
        if (value.type != 2) {
            return this.mResources.loadComplexColor(value, value.resourceId, this.mTheme);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to resolve attribute at index ");
        stringBuilder.append(index);
        stringBuilder.append(": ");
        stringBuilder.append(value);
        throw new UnsupportedOperationException(stringBuilder.toString());
    }

    public ColorStateList getColorStateList(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (!getValueAt(index * 7, value)) {
            return null;
        }
        if (value.type != 2) {
            return this.mResources.loadColorStateList(value, value.resourceId, this.mTheme);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to resolve attribute at index ");
        stringBuilder.append(index);
        stringBuilder.append(": ");
        stringBuilder.append(value);
        throw new UnsupportedOperationException(stringBuilder.toString());
    }

    public int getInteger(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int attrIndex = index;
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        if (type == 2) {
            TypedValue value = this.mValue;
            getValueAt(index, value);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to resolve attribute at index ");
            stringBuilder.append(attrIndex);
            stringBuilder.append(": ");
            stringBuilder.append(value);
            throw new UnsupportedOperationException(stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Can't convert value at index ");
        stringBuilder2.append(attrIndex);
        stringBuilder2.append(" to integer: type=0x");
        stringBuilder2.append(Integer.toHexString(type));
        throw new UnsupportedOperationException(stringBuilder2.toString());
    }

    public float getDimension(int index, float defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int attrIndex = index;
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimension(data[index + 1], this.mMetrics);
        }
        if (type == 2) {
            TypedValue value = this.mValue;
            getValueAt(index, value);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to resolve attribute at index ");
            stringBuilder.append(attrIndex);
            stringBuilder.append(": ");
            stringBuilder.append(value);
            throw new UnsupportedOperationException(stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Can't convert value at index ");
        stringBuilder2.append(attrIndex);
        stringBuilder2.append(" to dimension: type=0x");
        stringBuilder2.append(Integer.toHexString(type));
        throw new UnsupportedOperationException(stringBuilder2.toString());
    }

    public int getDimensionPixelOffset(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int attrIndex = index;
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelOffset(data[index + 1], this.mMetrics);
        }
        if (type == 2) {
            TypedValue value = this.mValue;
            getValueAt(index, value);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to resolve attribute at index ");
            stringBuilder.append(attrIndex);
            stringBuilder.append(": ");
            stringBuilder.append(value);
            throw new UnsupportedOperationException(stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Can't convert value at index ");
        stringBuilder2.append(attrIndex);
        stringBuilder2.append(" to dimension: type=0x");
        stringBuilder2.append(Integer.toHexString(type));
        throw new UnsupportedOperationException(stringBuilder2.toString());
    }

    public int getDimensionPixelSize(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int attrIndex = index;
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index + 1], this.mMetrics);
        }
        if (type == 2) {
            TypedValue value = this.mValue;
            getValueAt(index, value);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to resolve attribute at index ");
            stringBuilder.append(attrIndex);
            stringBuilder.append(": ");
            stringBuilder.append(value);
            throw new UnsupportedOperationException(stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Can't convert value at index ");
        stringBuilder2.append(attrIndex);
        stringBuilder2.append(" to dimension: type=0x");
        stringBuilder2.append(Integer.toHexString(type));
        throw new UnsupportedOperationException(stringBuilder2.toString());
    }

    public int getLayoutDimension(int index, String name) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int attrIndex = index;
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index + 1], this.mMetrics);
        }
        if (type == 2) {
            TypedValue value = this.mValue;
            getValueAt(index, value);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to resolve attribute at index ");
            stringBuilder.append(attrIndex);
            stringBuilder.append(": ");
            stringBuilder.append(value);
            throw new UnsupportedOperationException(stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getPositionDescription());
        stringBuilder2.append(": You must supply a ");
        stringBuilder2.append(name);
        stringBuilder2.append(" attribute.");
        throw new UnsupportedOperationException(stringBuilder2.toString());
    }

    public int getLayoutDimension(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index + 1], this.mMetrics);
        }
        return defValue;
    }

    public float getFraction(int index, int base, int pbase, float defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int attrIndex = index;
        index *= 7;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 6) {
            return TypedValue.complexToFraction(data[index + 1], (float) base, (float) pbase);
        }
        if (type == 2) {
            TypedValue value = this.mValue;
            getValueAt(index, value);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to resolve attribute at index ");
            stringBuilder.append(attrIndex);
            stringBuilder.append(": ");
            stringBuilder.append(value);
            throw new UnsupportedOperationException(stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Can't convert value at index ");
        stringBuilder2.append(attrIndex);
        stringBuilder2.append(" to fraction: type=0x");
        stringBuilder2.append(Integer.toHexString(type));
        throw new UnsupportedOperationException(stringBuilder2.toString());
    }

    public int getResourceId(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        if (data[index + 0] != 0) {
            int resid = data[index + 3];
            if (resid != 0) {
                return resid;
            }
        }
        return defValue;
    }

    public int getThemeAttributeId(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        if (data[index + 0] == 2) {
            return data[index + 1];
        }
        return defValue;
    }

    public Drawable getDrawable(int index) {
        return getDrawableForDensity(index, 0);
    }

    public Drawable getDrawableForDensity(int index, int density) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (!getValueAt(index * 7, value)) {
            return null;
        }
        if (value.type != 2) {
            if (density > 0) {
                this.mResources.getValueForDensity(value.resourceId, density, value, true);
            }
            return this.mResources.loadDrawable(value, value.resourceId, density, this.mTheme);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to resolve attribute at index ");
        stringBuilder.append(index);
        stringBuilder.append(": ");
        stringBuilder.append(value);
        throw new UnsupportedOperationException(stringBuilder.toString());
    }

    public Typeface getFont(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (!getValueAt(index * 7, value)) {
            return null;
        }
        if (value.type != 2) {
            return this.mResources.getFont(value, value.resourceId);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to resolve attribute at index ");
        stringBuilder.append(index);
        stringBuilder.append(": ");
        stringBuilder.append(value);
        throw new UnsupportedOperationException(stringBuilder.toString());
    }

    public CharSequence[] getTextArray(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (getValueAt(index * 7, value)) {
            return this.mResources.getTextArray(value.resourceId);
        }
        return null;
    }

    public boolean getValue(int index, TypedValue outValue) {
        if (!this.mRecycled) {
            return getValueAt(index * 7, outValue);
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public int getType(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        return this.mData[(index * 7) + 0];
    }

    public int getSourceResourceId(int index, int defaultValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int resid = this.mData[(index * 7) + 6];
        if (resid != 0) {
            return resid;
        }
        return defaultValue;
    }

    public boolean hasValue(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        return this.mData[(index * 7) + 0] != 0;
    }

    public boolean hasValueOrEmpty(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 7;
        int[] data = this.mData;
        if (data[index + 0] != 0 || data[index + 1] == 1) {
            return true;
        }
        return false;
    }

    public TypedValue peekValue(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (getValueAt(index * 7, value)) {
            return value;
        }
        return null;
    }

    public String getPositionDescription() {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        Parser parser = this.mXml;
        return parser != null ? parser.getPositionDescription() : "<internal>";
    }

    public void recycle() {
        if (this.mRecycled) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(toString());
            stringBuilder.append(" recycled twice!");
            throw new RuntimeException(stringBuilder.toString());
        }
        this.mRecycled = true;
        this.mXml = null;
        this.mTheme = null;
        this.mAssets = null;
        this.mResources.mTypedArrayPool.release(this);
    }

    @UnsupportedAppUsage
    public int[] extractThemeAttrs() {
        return extractThemeAttrs(null);
    }

    @UnsupportedAppUsage
    public int[] extractThemeAttrs(int[] scrap) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int[] attrs = null;
        int[] data = this.mData;
        int N = length();
        for (int i = 0; i < N; i++) {
            int index = i * 7;
            if (data[index + 0] == 2) {
                data[index + 0] = 0;
                int attr = data[index + 1];
                if (attr != 0) {
                    if (attrs == null) {
                        if (scrap == null || scrap.length != N) {
                            attrs = new int[N];
                        } else {
                            attrs = scrap;
                            Arrays.fill(attrs, 0);
                        }
                    }
                    attrs[i] = attr;
                }
            }
        }
        return attrs;
    }

    public int getChangingConfigurations() {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int changingConfig = 0;
        int[] data = this.mData;
        int N = length();
        for (int i = 0; i < N; i++) {
            int index = i * 7;
            if (data[index + 0] != 0) {
                changingConfig |= ActivityInfo.activityInfoConfigNativeToJava(data[index + 4]);
            }
        }
        return changingConfig;
    }

    @UnsupportedAppUsage
    private boolean getValueAt(int index, TypedValue outValue) {
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return false;
        }
        outValue.type = type;
        outValue.data = data[index + 1];
        outValue.assetCookie = data[index + 2];
        outValue.resourceId = data[index + 3];
        outValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(data[index + 4]);
        outValue.density = data[index + 5];
        outValue.string = type == 3 ? loadStringValueAt(index) : null;
        outValue.sourceResourceId = data[index + 6];
        return true;
    }

    private CharSequence loadStringValueAt(int index) {
        int[] data = this.mData;
        int cookie = data[index + 2];
        if (cookie >= 0) {
            return this.mAssets.getPooledStringForCookie(cookie, data[index + 1]);
        }
        Parser parser = this.mXml;
        if (parser != null) {
            return parser.getPooledString(data[index + 1]);
        }
        return null;
    }

    protected TypedArray(Resources resources) {
        this.mResources = resources;
        this.mMetrics = this.mResources.getDisplayMetrics();
        this.mAssets = this.mResources.getAssets();
    }

    public String toString() {
        return Arrays.toString(this.mData);
    }
}
