package android.content;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.proto.ProtoOutputStream;
import java.io.PrintWriter;
import org.apache.miui.commons.lang3.ClassUtils;

public final class ComponentName implements Parcelable, Cloneable, Comparable<ComponentName> {
    public static final Creator<ComponentName> CREATOR = new Creator<ComponentName>() {
        public ComponentName createFromParcel(Parcel in) {
            return new ComponentName(in);
        }

        public ComponentName[] newArray(int size) {
            return new ComponentName[size];
        }
    };
    private final String mClass;
    private final String mPackage;

    @FunctionalInterface
    public interface WithComponentName {
        ComponentName getComponentName();
    }

    public static ComponentName createRelative(String pkg, String cls) {
        if (TextUtils.isEmpty(cls)) {
            throw new IllegalArgumentException("class name cannot be empty");
        }
        String fullName;
        if (cls.charAt(0) == ClassUtils.PACKAGE_SEPARATOR_CHAR) {
            fullName = new StringBuilder();
            fullName.append(pkg);
            fullName.append(cls);
            fullName = fullName.toString();
        } else {
            fullName = cls;
        }
        return new ComponentName(pkg, fullName);
    }

    public static ComponentName createRelative(Context pkg, String cls) {
        return createRelative(pkg.getPackageName(), cls);
    }

    public ComponentName(String pkg, String cls) {
        if (pkg == null) {
            throw new NullPointerException("package name is null");
        } else if (cls != null) {
            this.mPackage = pkg;
            this.mClass = cls;
        } else {
            throw new NullPointerException("class name is null");
        }
    }

    public ComponentName(Context pkg, String cls) {
        if (cls != null) {
            this.mPackage = pkg.getPackageName();
            this.mClass = cls;
            return;
        }
        throw new NullPointerException("class name is null");
    }

    public ComponentName(Context pkg, Class<?> cls) {
        this.mPackage = pkg.getPackageName();
        this.mClass = cls.getName();
    }

    public ComponentName clone() {
        return new ComponentName(this.mPackage, this.mClass);
    }

    public String getPackageName() {
        return this.mPackage;
    }

    public String getClassName() {
        return this.mClass;
    }

    public String getShortClassName() {
        if (this.mClass.startsWith(this.mPackage)) {
            int PN = this.mPackage.length();
            int CN = this.mClass.length();
            if (CN > PN && this.mClass.charAt(PN) == ClassUtils.PACKAGE_SEPARATOR_CHAR) {
                return this.mClass.substring(PN, CN);
            }
        }
        return this.mClass;
    }

    private static void appendShortClassName(StringBuilder sb, String packageName, String className) {
        if (className.startsWith(packageName)) {
            int PN = packageName.length();
            int CN = className.length();
            if (CN > PN && className.charAt(PN) == ClassUtils.PACKAGE_SEPARATOR_CHAR) {
                sb.append(className, PN, CN);
                return;
            }
        }
        sb.append(className);
    }

    private static void printShortClassName(PrintWriter pw, String packageName, String className) {
        if (className.startsWith(packageName)) {
            int PN = packageName.length();
            int CN = className.length();
            if (CN > PN && className.charAt(PN) == ClassUtils.PACKAGE_SEPARATOR_CHAR) {
                pw.write(className, PN, CN - PN);
                return;
            }
        }
        pw.print(className);
    }

    public static String flattenToShortString(ComponentName componentName) {
        return componentName == null ? null : componentName.flattenToShortString();
    }

    public String flattenToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mPackage);
        stringBuilder.append("/");
        stringBuilder.append(this.mClass);
        return stringBuilder.toString();
    }

    public String flattenToShortString() {
        StringBuilder sb = new StringBuilder(this.mPackage.length() + this.mClass.length());
        appendShortString(sb, this.mPackage, this.mClass);
        return sb.toString();
    }

    public void appendShortString(StringBuilder sb) {
        appendShortString(sb, this.mPackage, this.mClass);
    }

    @UnsupportedAppUsage
    public static void appendShortString(StringBuilder sb, String packageName, String className) {
        sb.append(packageName);
        sb.append('/');
        appendShortClassName(sb, packageName, className);
    }

    @UnsupportedAppUsage
    public static void printShortString(PrintWriter pw, String packageName, String className) {
        pw.print(packageName);
        pw.print('/');
        printShortClassName(pw, packageName, className);
    }

    public static ComponentName unflattenFromString(String str) {
        int sep = str.indexOf(47);
        if (sep < 0 || sep + 1 >= str.length()) {
            return null;
        }
        String pkg = str.substring(0, sep);
        String cls = str.substring(sep + 1);
        if (cls.length() > 0 && cls.charAt(0) == ClassUtils.PACKAGE_SEPARATOR_CHAR) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(pkg);
            stringBuilder.append(cls);
            cls = stringBuilder.toString();
        }
        return new ComponentName(pkg, cls);
    }

    public String toShortString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append(this.mPackage);
        stringBuilder.append("/");
        stringBuilder.append(this.mClass);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ComponentInfo{");
        stringBuilder.append(this.mPackage);
        stringBuilder.append("/");
        stringBuilder.append(this.mClass);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1138166333441L, this.mPackage);
        proto.write(1138166333442L, this.mClass);
        proto.end(token);
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj != null) {
            try {
                ComponentName other = (ComponentName) obj;
                if (this.mPackage.equals(other.mPackage) && this.mClass.equals(other.mClass)) {
                    z = true;
                }
                return z;
            } catch (ClassCastException e) {
            }
        }
        return false;
    }

    public int hashCode() {
        return this.mPackage.hashCode() + this.mClass.hashCode();
    }

    public int compareTo(ComponentName that) {
        int v = this.mPackage.compareTo(that.mPackage);
        if (v != 0) {
            return v;
        }
        return this.mClass.compareTo(that.mClass);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mPackage);
        out.writeString(this.mClass);
    }

    public static void writeToParcel(ComponentName c, Parcel out) {
        if (c != null) {
            c.writeToParcel(out, 0);
        } else {
            out.writeString(null);
        }
    }

    public static ComponentName readFromParcel(Parcel in) {
        String pkg = in.readString();
        return pkg != null ? new ComponentName(pkg, in) : null;
    }

    public ComponentName(Parcel in) {
        this.mPackage = in.readString();
        if (this.mPackage != null) {
            this.mClass = in.readString();
            if (this.mClass == null) {
                throw new NullPointerException("class name is null");
            }
            return;
        }
        throw new NullPointerException("package name is null");
    }

    private ComponentName(String pkg, Parcel in) {
        this.mPackage = pkg;
        this.mClass = in.readString();
    }
}
