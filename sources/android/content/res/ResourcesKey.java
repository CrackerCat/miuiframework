package android.content.res;

import android.annotation.UnsupportedAppUsage;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.Objects;

public final class ResourcesKey {
    public final CompatibilityInfo mCompatInfo;
    public final int mDisplayId;
    private final int mHash;
    public final String[] mLibDirs;
    public final String[] mOverlayDirs;
    public final Configuration mOverrideConfiguration;
    @UnsupportedAppUsage
    public final String mResDir;
    @UnsupportedAppUsage
    public final String[] mSplitResDirs;

    @UnsupportedAppUsage
    public ResourcesKey(String resDir, String[] splitResDirs, String[] overlayDirs, String[] libDirs, int displayId, Configuration overrideConfig, CompatibilityInfo compatInfo) {
        this.mResDir = resDir;
        this.mSplitResDirs = splitResDirs;
        this.mOverlayDirs = overlayDirs;
        this.mLibDirs = libDirs;
        this.mDisplayId = displayId;
        this.mOverrideConfiguration = new Configuration(overrideConfig != null ? overrideConfig : Configuration.EMPTY);
        this.mCompatInfo = compatInfo != null ? compatInfo : CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mHash = (((((((((((((17 * 31) + Objects.hashCode(this.mResDir)) * 31) + Arrays.hashCode(this.mSplitResDirs)) * 31) + Arrays.hashCode(this.mOverlayDirs)) * 31) + Arrays.hashCode(this.mLibDirs)) * 31) + this.mDisplayId) * 31) + Objects.hashCode(this.mOverrideConfiguration)) * 31) + Objects.hashCode(this.mCompatInfo);
    }

    public boolean hasOverrideConfiguration() {
        return Configuration.EMPTY.equals(this.mOverrideConfiguration) ^ 1;
    }

    public boolean isPathReferenced(String path) {
        String str = this.mResDir;
        boolean z = true;
        if (str != null && str.startsWith(path)) {
            return true;
        }
        if (!(anyStartsWith(this.mSplitResDirs, path) || anyStartsWith(this.mOverlayDirs, path) || anyStartsWith(this.mLibDirs, path))) {
            z = false;
        }
        return z;
    }

    private static boolean anyStartsWith(String[] list, String prefix) {
        if (list != null) {
            for (String s : list) {
                if (s != null && s.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int hashCode() {
        return this.mHash;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ResourcesKey)) {
            return false;
        }
        ResourcesKey peer = (ResourcesKey) obj;
        if (this.mHash == peer.mHash && Objects.equals(this.mResDir, peer.mResDir) && Arrays.equals(this.mSplitResDirs, peer.mSplitResDirs) && Arrays.equals(this.mOverlayDirs, peer.mOverlayDirs) && Arrays.equals(this.mLibDirs, peer.mLibDirs) && this.mDisplayId == peer.mDisplayId && Objects.equals(this.mOverrideConfiguration, peer.mOverrideConfiguration) && Objects.equals(this.mCompatInfo, peer.mCompatInfo)) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder().append("ResourcesKey{");
        builder.append(" mHash=");
        builder.append(Integer.toHexString(this.mHash));
        builder.append(" mResDir=");
        builder.append(this.mResDir);
        builder.append(" mSplitDirs=[");
        Object[] objArr = this.mSplitResDirs;
        CharSequence charSequence = ",";
        if (objArr != null) {
            builder.append(TextUtils.join(charSequence, objArr));
        }
        String str = "]";
        builder.append(str);
        builder.append(" mOverlayDirs=[");
        Object[] objArr2 = this.mOverlayDirs;
        if (objArr2 != null) {
            builder.append(TextUtils.join(charSequence, objArr2));
        }
        builder.append(str);
        builder.append(" mLibDirs=[");
        objArr2 = this.mLibDirs;
        if (objArr2 != null) {
            builder.append(TextUtils.join(charSequence, objArr2));
        }
        builder.append(str);
        builder.append(" mDisplayId=");
        builder.append(this.mDisplayId);
        builder.append(" mOverrideConfig=");
        builder.append(Configuration.resourceQualifierString(this.mOverrideConfiguration));
        builder.append(" mCompatInfo=");
        builder.append(this.mCompatInfo);
        builder.append("}");
        return builder.toString();
    }
}
