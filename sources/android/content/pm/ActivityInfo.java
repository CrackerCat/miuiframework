package android.content.pm;

import android.annotation.UnsupportedAppUsage;
import android.content.res.TypedArray;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Printer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ActivityInfo extends ComponentInfo implements Parcelable {
    public static final int COLOR_MODE_DEFAULT = 0;
    public static final int COLOR_MODE_HDR = 2;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT = 1;
    public static final int CONFIG_ASSETS_PATHS = Integer.MIN_VALUE;
    public static final int CONFIG_COLOR_MODE = 16384;
    public static final int CONFIG_DENSITY = 4096;
    public static final int CONFIG_FONT_SCALE = 1073741824;
    public static final int CONFIG_KEYBOARD = 16;
    public static final int CONFIG_KEYBOARD_HIDDEN = 32;
    public static final int CONFIG_LAYOUT_DIRECTION = 8192;
    public static final int CONFIG_LOCALE = 4;
    public static final int CONFIG_MCC = 1;
    public static final int CONFIG_MNC = 2;
    public static int[] CONFIG_NATIVE_BITS = new int[]{2, 1, 4, 8, 16, 32, 64, 128, 2048, 4096, 512, 8192, 256, 16384, 65536};
    public static final int CONFIG_NAVIGATION = 64;
    public static final int CONFIG_ORIENTATION = 128;
    public static final int CONFIG_SCREEN_LAYOUT = 256;
    public static final int CONFIG_SCREEN_SIZE = 1024;
    public static final int CONFIG_SMALLEST_SCREEN_SIZE = 2048;
    public static final int CONFIG_TOUCHSCREEN = 8;
    public static final int CONFIG_UI_MODE = 512;
    public static final int CONFIG_WINDOW_CONFIGURATION = 536870912;
    public static final Creator<ActivityInfo> CREATOR = new Creator<ActivityInfo>() {
        public ActivityInfo createFromParcel(Parcel source) {
            return new ActivityInfo(source, null);
        }

        public ActivityInfo[] newArray(int size) {
            return new ActivityInfo[size];
        }
    };
    public static final int DOCUMENT_LAUNCH_ALWAYS = 2;
    public static final int DOCUMENT_LAUNCH_INTO_EXISTING = 1;
    public static final int DOCUMENT_LAUNCH_NEVER = 3;
    public static final int DOCUMENT_LAUNCH_NONE = 0;
    @UnsupportedAppUsage
    public static final int FLAG_ALLOW_EMBEDDED = Integer.MIN_VALUE;
    public static final int FLAG_ALLOW_TASK_REPARENTING = 64;
    public static final int FLAG_ALWAYS_FOCUSABLE = 262144;
    public static final int FLAG_ALWAYS_RETAIN_TASK_STATE = 8;
    public static final int FLAG_AUTO_REMOVE_FROM_RECENTS = 8192;
    public static final int FLAG_CLEAR_TASK_ON_LAUNCH = 4;
    public static final int FLAG_ENABLE_VR_MODE = 32768;
    public static final int FLAG_EXCLUDE_FROM_RECENTS = 32;
    public static final int FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS = 256;
    public static final int FLAG_FINISH_ON_TASK_LAUNCH = 2;
    public static final int FLAG_HARDWARE_ACCELERATED = 512;
    public static final int FLAG_IMMERSIVE = 2048;
    public static final int FLAG_IMPLICITLY_VISIBLE_TO_INSTANT_APP = 2097152;
    public static final int FLAG_INHERIT_SHOW_WHEN_LOCKED = 1;
    public static final int FLAG_MULTIPROCESS = 1;
    public static final int FLAG_NO_HISTORY = 128;
    public static final int FLAG_RELINQUISH_TASK_IDENTITY = 4096;
    public static final int FLAG_RESUME_WHILE_PAUSING = 16384;
    @UnsupportedAppUsage
    public static final int FLAG_SHOW_FOR_ALL_USERS = 1024;
    public static final int FLAG_SHOW_WHEN_LOCKED = 8388608;
    public static final int FLAG_SINGLE_USER = 1073741824;
    public static final int FLAG_STATE_NOT_NEEDED = 16;
    public static final int FLAG_SUPPORTS_PICTURE_IN_PICTURE = 4194304;
    public static final int FLAG_SYSTEM_USER_ONLY = 536870912;
    public static final int FLAG_TURN_SCREEN_ON = 16777216;
    public static final int FLAG_VISIBLE_TO_INSTANT_APP = 1048576;
    public static final int LAUNCH_MULTIPLE = 0;
    public static final int LAUNCH_SINGLE_INSTANCE = 3;
    public static final int LAUNCH_SINGLE_TASK = 2;
    public static final int LAUNCH_SINGLE_TOP = 1;
    public static final int LOCK_TASK_LAUNCH_MODE_ALWAYS = 2;
    public static final int LOCK_TASK_LAUNCH_MODE_DEFAULT = 0;
    public static final int LOCK_TASK_LAUNCH_MODE_IF_WHITELISTED = 3;
    public static final int LOCK_TASK_LAUNCH_MODE_NEVER = 1;
    public static final int PERSIST_ACROSS_REBOOTS = 2;
    public static final int PERSIST_NEVER = 1;
    public static final int PERSIST_ROOT_ONLY = 0;
    public static final int RESIZE_MODE_FORCE_RESIZABLE_LANDSCAPE_ONLY = 5;
    public static final int RESIZE_MODE_FORCE_RESIZABLE_PORTRAIT_ONLY = 6;
    public static final int RESIZE_MODE_FORCE_RESIZABLE_PRESERVE_ORIENTATION = 7;
    public static final int RESIZE_MODE_FORCE_RESIZEABLE = 4;
    public static final int RESIZE_MODE_RESIZEABLE = 2;
    public static final int RESIZE_MODE_RESIZEABLE_AND_PIPABLE_DEPRECATED = 3;
    public static final int RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION = 1;
    public static final int RESIZE_MODE_UNRESIZEABLE = 0;
    public static final int SCREEN_ORIENTATION_BEHIND = 3;
    public static final int SCREEN_ORIENTATION_FULL_SENSOR = 10;
    public static final int SCREEN_ORIENTATION_FULL_USER = 13;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
    public static final int SCREEN_ORIENTATION_LOCKED = 14;
    public static final int SCREEN_ORIENTATION_NOSENSOR = 5;
    public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
    public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
    public static final int SCREEN_ORIENTATION_SENSOR = 4;
    public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
    public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7;
    public static final int SCREEN_ORIENTATION_UNSET = -2;
    public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
    public static final int SCREEN_ORIENTATION_USER = 2;
    public static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11;
    public static final int SCREEN_ORIENTATION_USER_PORTRAIT = 12;
    public static final int UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW = 1;
    public int colorMode;
    public int configChanges;
    public int documentLaunchMode;
    public int flags;
    public int launchMode;
    public String launchToken;
    public int lockTaskLaunchMode;
    public float maxAspectRatio;
    public int maxRecents;
    public float minAspectRatio;
    public String parentActivityName;
    public String permission;
    public int persistableMode;
    public int privateFlags;
    public String requestedVrComponent;
    @UnsupportedAppUsage
    public int resizeMode;
    public int rotationAnimation;
    public int screenOrientation;
    public int softInputMode;
    public String targetActivity;
    public String taskAffinity;
    public int theme;
    public int uiOptions;
    public WindowLayout windowLayout;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ColorMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Config {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenOrientation {
    }

    public static final class WindowLayout {
        public final int gravity;
        public final int height;
        public final float heightFraction;
        public final int minHeight;
        public final int minWidth;
        public final int width;
        public final float widthFraction;

        public WindowLayout(int width, float widthFraction, int height, float heightFraction, int gravity, int minWidth, int minHeight) {
            this.width = width;
            this.widthFraction = widthFraction;
            this.height = height;
            this.heightFraction = heightFraction;
            this.gravity = gravity;
            this.minWidth = minWidth;
            this.minHeight = minHeight;
        }

        WindowLayout(Parcel source) {
            this.width = source.readInt();
            this.widthFraction = source.readFloat();
            this.height = source.readInt();
            this.heightFraction = source.readFloat();
            this.gravity = source.readInt();
            this.minWidth = source.readInt();
            this.minHeight = source.readInt();
        }

        public boolean hasSpecifiedSize() {
            return this.width >= 0 || this.height >= 0 || this.widthFraction >= 0.0f || this.heightFraction >= 0.0f;
        }
    }

    /* synthetic */ ActivityInfo(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    @UnsupportedAppUsage
    public static int activityInfoConfigJavaToNative(int input) {
        int output = 0;
        int i = 0;
        while (true) {
            int[] iArr = CONFIG_NATIVE_BITS;
            if (i >= iArr.length) {
                return output;
            }
            if (((1 << i) & input) != 0) {
                output |= iArr[i];
            }
            i++;
        }
    }

    public static int activityInfoConfigNativeToJava(int input) {
        int output = 0;
        int i = 0;
        while (true) {
            int[] iArr = CONFIG_NATIVE_BITS;
            if (i >= iArr.length) {
                return output;
            }
            if ((iArr[i] & input) != 0) {
                output |= 1 << i;
            }
            i++;
        }
    }

    public int getRealConfigChanged() {
        if (this.applicationInfo.targetSdkVersion < 13) {
            return (this.configChanges | 1024) | 2048;
        }
        return this.configChanges;
    }

    public static final String lockTaskLaunchModeToString(int lockTaskLaunchMode) {
        if (lockTaskLaunchMode == 0) {
            return "LOCK_TASK_LAUNCH_MODE_DEFAULT";
        }
        if (lockTaskLaunchMode == 1) {
            return "LOCK_TASK_LAUNCH_MODE_NEVER";
        }
        if (lockTaskLaunchMode == 2) {
            return "LOCK_TASK_LAUNCH_MODE_ALWAYS";
        }
        if (lockTaskLaunchMode == 3) {
            return "LOCK_TASK_LAUNCH_MODE_IF_WHITELISTED";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("unknown=");
        stringBuilder.append(lockTaskLaunchMode);
        return stringBuilder.toString();
    }

    public ActivityInfo() {
        this.resizeMode = 2;
        this.colorMode = 0;
        this.screenOrientation = -1;
        this.uiOptions = 0;
        this.rotationAnimation = -1;
    }

    public ActivityInfo(ActivityInfo orig) {
        super((ComponentInfo) orig);
        this.resizeMode = 2;
        this.colorMode = 0;
        this.screenOrientation = -1;
        this.uiOptions = 0;
        this.rotationAnimation = -1;
        this.theme = orig.theme;
        this.launchMode = orig.launchMode;
        this.documentLaunchMode = orig.documentLaunchMode;
        this.permission = orig.permission;
        this.taskAffinity = orig.taskAffinity;
        this.targetActivity = orig.targetActivity;
        this.flags = orig.flags;
        this.privateFlags = orig.privateFlags;
        this.screenOrientation = orig.screenOrientation;
        this.configChanges = orig.configChanges;
        this.softInputMode = orig.softInputMode;
        this.uiOptions = orig.uiOptions;
        this.parentActivityName = orig.parentActivityName;
        this.maxRecents = orig.maxRecents;
        this.lockTaskLaunchMode = orig.lockTaskLaunchMode;
        this.windowLayout = orig.windowLayout;
        this.resizeMode = orig.resizeMode;
        this.requestedVrComponent = orig.requestedVrComponent;
        this.rotationAnimation = orig.rotationAnimation;
        this.colorMode = orig.colorMode;
        this.maxAspectRatio = orig.maxAspectRatio;
        this.minAspectRatio = orig.minAspectRatio;
    }

    public final int getThemeResource() {
        int i = this.theme;
        return i != 0 ? i : this.applicationInfo.theme;
    }

    private String persistableModeToString() {
        int i = this.persistableMode;
        if (i == 0) {
            return "PERSIST_ROOT_ONLY";
        }
        if (i == 1) {
            return "PERSIST_NEVER";
        }
        if (i == 2) {
            return "PERSIST_ACROSS_REBOOTS";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UNKNOWN=");
        stringBuilder.append(this.persistableMode);
        return stringBuilder.toString();
    }

    public boolean hasFixedAspectRatio() {
        return (this.maxAspectRatio == 0.0f && this.minAspectRatio == 0.0f) ? false : true;
    }

    public boolean isFixedOrientation() {
        return isFixedOrientationLandscape() || isFixedOrientationPortrait() || this.screenOrientation == 14;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isFixedOrientationLandscape() {
        return isFixedOrientationLandscape(this.screenOrientation);
    }

    public static boolean isFixedOrientationLandscape(int orientation) {
        return orientation == 0 || orientation == 6 || orientation == 8 || orientation == 11;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isFixedOrientationPortrait() {
        return isFixedOrientationPortrait(this.screenOrientation);
    }

    public static boolean isFixedOrientationPortrait(int orientation) {
        return orientation == 1 || orientation == 7 || orientation == 9 || orientation == 12;
    }

    @UnsupportedAppUsage
    public boolean supportsPictureInPicture() {
        return (this.flags & 4194304) != 0;
    }

    @UnsupportedAppUsage
    public static boolean isResizeableMode(int mode) {
        return mode == 2 || mode == 4 || mode == 6 || mode == 5 || mode == 7 || mode == 1;
    }

    public static boolean isPreserveOrientationMode(int mode) {
        return mode == 6 || mode == 5 || mode == 7;
    }

    public static String resizeModeToString(int mode) {
        if (mode == 0) {
            return "RESIZE_MODE_UNRESIZEABLE";
        }
        if (mode == 1) {
            return "RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION";
        }
        if (mode == 2) {
            return "RESIZE_MODE_RESIZEABLE";
        }
        if (mode == 4) {
            return "RESIZE_MODE_FORCE_RESIZEABLE";
        }
        if (mode == 5) {
            return "RESIZE_MODE_FORCE_RESIZABLE_LANDSCAPE_ONLY";
        }
        if (mode == 6) {
            return "RESIZE_MODE_FORCE_RESIZABLE_PORTRAIT_ONLY";
        }
        if (mode == 7) {
            return "RESIZE_MODE_FORCE_RESIZABLE_PRESERVE_ORIENTATION";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("unknown=");
        stringBuilder.append(mode);
        return stringBuilder.toString();
    }

    public void dump(Printer pw, String prefix) {
        dump(pw, prefix, 3);
    }

    public void dump(Printer pw, String prefix, int dumpFlags) {
        StringBuilder stringBuilder;
        super.dumpFront(pw, prefix);
        if (this.permission != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("permission=");
            stringBuilder.append(this.permission);
            pw.println(stringBuilder.toString());
        }
        if ((dumpFlags & 1) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("taskAffinity=");
            stringBuilder.append(this.taskAffinity);
            stringBuilder.append(" targetActivity=");
            stringBuilder.append(this.targetActivity);
            stringBuilder.append(" persistableMode=");
            stringBuilder.append(persistableModeToString());
            pw.println(stringBuilder.toString());
        }
        if (!(this.launchMode == 0 && this.flags == 0 && this.privateFlags == 0 && this.theme == 0)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("launchMode=");
            stringBuilder.append(this.launchMode);
            stringBuilder.append(" flags=0x");
            stringBuilder.append(Integer.toHexString(this.flags));
            stringBuilder.append(" privateFlags=0x");
            stringBuilder.append(Integer.toHexString(this.privateFlags));
            stringBuilder.append(" theme=0x");
            stringBuilder.append(Integer.toHexString(this.theme));
            pw.println(stringBuilder.toString());
        }
        if (!(this.screenOrientation == -1 && this.configChanges == 0 && this.softInputMode == 0)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("screenOrientation=");
            stringBuilder.append(this.screenOrientation);
            stringBuilder.append(" configChanges=0x");
            stringBuilder.append(Integer.toHexString(this.configChanges));
            stringBuilder.append(" softInputMode=0x");
            stringBuilder.append(Integer.toHexString(this.softInputMode));
            pw.println(stringBuilder.toString());
        }
        if (this.uiOptions != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append(" uiOptions=0x");
            stringBuilder.append(Integer.toHexString(this.uiOptions));
            pw.println(stringBuilder.toString());
        }
        if ((dumpFlags & 1) != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("lockTaskLaunchMode=");
            stringBuilder.append(lockTaskLaunchModeToString(this.lockTaskLaunchMode));
            pw.println(stringBuilder.toString());
        }
        if (this.windowLayout != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("windowLayout=");
            stringBuilder.append(this.windowLayout.width);
            String str = "|";
            stringBuilder.append(str);
            stringBuilder.append(this.windowLayout.widthFraction);
            String str2 = ", ";
            stringBuilder.append(str2);
            stringBuilder.append(this.windowLayout.height);
            stringBuilder.append(str);
            stringBuilder.append(this.windowLayout.heightFraction);
            stringBuilder.append(str2);
            stringBuilder.append(this.windowLayout.gravity);
            pw.println(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append("resizeMode=");
        stringBuilder.append(resizeModeToString(this.resizeMode));
        pw.println(stringBuilder.toString());
        if (this.requestedVrComponent != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("requestedVrComponent=");
            stringBuilder.append(this.requestedVrComponent);
            pw.println(stringBuilder.toString());
        }
        if (this.maxAspectRatio != 0.0f) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("maxAspectRatio=");
            stringBuilder.append(this.maxAspectRatio);
            pw.println(stringBuilder.toString());
        }
        if (this.minAspectRatio != 0.0f) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("minAspectRatio=");
            stringBuilder.append(this.minAspectRatio);
            pw.println(stringBuilder.toString());
        }
        super.dumpBack(pw, prefix, dumpFlags);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ActivityInfo{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        stringBuilder.append(this.name);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeInt(this.theme);
        dest.writeInt(this.launchMode);
        dest.writeInt(this.documentLaunchMode);
        dest.writeString(this.permission);
        dest.writeString(this.taskAffinity);
        dest.writeString(this.targetActivity);
        dest.writeString(this.launchToken);
        dest.writeInt(this.flags);
        dest.writeInt(this.privateFlags);
        dest.writeInt(this.screenOrientation);
        dest.writeInt(this.configChanges);
        dest.writeInt(this.softInputMode);
        dest.writeInt(this.uiOptions);
        dest.writeString(this.parentActivityName);
        dest.writeInt(this.persistableMode);
        dest.writeInt(this.maxRecents);
        dest.writeInt(this.lockTaskLaunchMode);
        if (this.windowLayout != null) {
            dest.writeInt(1);
            dest.writeInt(this.windowLayout.width);
            dest.writeFloat(this.windowLayout.widthFraction);
            dest.writeInt(this.windowLayout.height);
            dest.writeFloat(this.windowLayout.heightFraction);
            dest.writeInt(this.windowLayout.gravity);
            dest.writeInt(this.windowLayout.minWidth);
            dest.writeInt(this.windowLayout.minHeight);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.resizeMode);
        dest.writeString(this.requestedVrComponent);
        dest.writeInt(this.rotationAnimation);
        dest.writeInt(this.colorMode);
        dest.writeFloat(this.maxAspectRatio);
        dest.writeFloat(this.minAspectRatio);
    }

    public static boolean isTranslucentOrFloating(TypedArray attributes) {
        boolean isTranslucent = attributes.getBoolean(5, false);
        boolean isSwipeToDismiss = !attributes.hasValue(5) && attributes.getBoolean(25, false);
        if (attributes.getBoolean(true, false) || isTranslucent || isSwipeToDismiss) {
            return true;
        }
        return false;
    }

    public static String screenOrientationToString(int orientation) {
        switch (orientation) {
            case -2:
                return "SCREEN_ORIENTATION_UNSET";
            case -1:
                return "SCREEN_ORIENTATION_UNSPECIFIED";
            case 0:
                return "SCREEN_ORIENTATION_LANDSCAPE";
            case 1:
                return "SCREEN_ORIENTATION_PORTRAIT";
            case 2:
                return "SCREEN_ORIENTATION_USER";
            case 3:
                return "SCREEN_ORIENTATION_BEHIND";
            case 4:
                return "SCREEN_ORIENTATION_SENSOR";
            case 5:
                return "SCREEN_ORIENTATION_NOSENSOR";
            case 6:
                return "SCREEN_ORIENTATION_SENSOR_LANDSCAPE";
            case 7:
                return "SCREEN_ORIENTATION_SENSOR_PORTRAIT";
            case 8:
                return "SCREEN_ORIENTATION_REVERSE_LANDSCAPE";
            case 9:
                return "SCREEN_ORIENTATION_REVERSE_PORTRAIT";
            case 10:
                return "SCREEN_ORIENTATION_FULL_SENSOR";
            case 11:
                return "SCREEN_ORIENTATION_USER_LANDSCAPE";
            case 12:
                return "SCREEN_ORIENTATION_USER_PORTRAIT";
            case 13:
                return "SCREEN_ORIENTATION_FULL_USER";
            case 14:
                return "SCREEN_ORIENTATION_LOCKED";
            default:
                return Integer.toString(orientation);
        }
    }

    public static String colorModeToString(int colorMode) {
        if (colorMode == 0) {
            return "COLOR_MODE_DEFAULT";
        }
        if (colorMode == 1) {
            return "COLOR_MODE_WIDE_COLOR_GAMUT";
        }
        if (colorMode != 2) {
            return Integer.toString(colorMode);
        }
        return "COLOR_MODE_HDR";
    }

    private ActivityInfo(Parcel source) {
        super(source);
        this.resizeMode = 2;
        this.colorMode = 0;
        this.screenOrientation = -1;
        this.uiOptions = 0;
        this.rotationAnimation = -1;
        this.theme = source.readInt();
        this.launchMode = source.readInt();
        this.documentLaunchMode = source.readInt();
        this.permission = source.readString();
        this.taskAffinity = source.readString();
        this.targetActivity = source.readString();
        this.launchToken = source.readString();
        this.flags = source.readInt();
        this.privateFlags = source.readInt();
        this.screenOrientation = source.readInt();
        this.configChanges = source.readInt();
        this.softInputMode = source.readInt();
        this.uiOptions = source.readInt();
        this.parentActivityName = source.readString();
        this.persistableMode = source.readInt();
        this.maxRecents = source.readInt();
        this.lockTaskLaunchMode = source.readInt();
        if (source.readInt() == 1) {
            this.windowLayout = new WindowLayout(source);
        }
        this.resizeMode = source.readInt();
        this.requestedVrComponent = source.readString();
        this.rotationAnimation = source.readInt();
        this.colorMode = source.readInt();
        this.maxAspectRatio = source.readFloat();
        this.minAspectRatio = source.readFloat();
    }
}