package android.content.pm;

import android.content.pm.PackageParser.Package;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SharedLibraryInfo implements Parcelable {
    public static final Creator<SharedLibraryInfo> CREATOR = new Creator<SharedLibraryInfo>() {
        public SharedLibraryInfo createFromParcel(Parcel source) {
            return new SharedLibraryInfo(source, null);
        }

        public SharedLibraryInfo[] newArray(int size) {
            return new SharedLibraryInfo[size];
        }
    };
    public static final int TYPE_BUILTIN = 0;
    public static final int TYPE_DYNAMIC = 1;
    public static final int TYPE_STATIC = 2;
    public static final int VERSION_UNDEFINED = -1;
    private final List<String> mCodePaths;
    private final VersionedPackage mDeclaringPackage;
    private List<SharedLibraryInfo> mDependencies;
    private final List<VersionedPackage> mDependentPackages;
    private final String mName;
    private final String mPackageName;
    private final String mPath;
    private final int mType;
    private final long mVersion;

    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }

    /* synthetic */ SharedLibraryInfo(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public static SharedLibraryInfo createForStatic(Package pkg) {
        return new SharedLibraryInfo(null, pkg.packageName, pkg.getAllCodePaths(), pkg.staticSharedLibName, pkg.staticSharedLibVersion, 2, new VersionedPackage(pkg.manifestPackageName, pkg.getLongVersionCode()), null, null);
    }

    public static SharedLibraryInfo createForDynamic(Package pkg, String name) {
        return new SharedLibraryInfo(null, pkg.packageName, pkg.getAllCodePaths(), name, -1, 1, new VersionedPackage(pkg.packageName, pkg.getLongVersionCode()), null, null);
    }

    public SharedLibraryInfo(String path, String packageName, List<String> codePaths, String name, long version, int type, VersionedPackage declaringPackage, List<VersionedPackage> dependentPackages, List<SharedLibraryInfo> dependencies) {
        this.mPath = path;
        this.mPackageName = packageName;
        this.mCodePaths = codePaths;
        this.mName = name;
        this.mVersion = version;
        this.mType = type;
        this.mDeclaringPackage = declaringPackage;
        this.mDependentPackages = dependentPackages;
        this.mDependencies = dependencies;
    }

    private SharedLibraryInfo(Parcel parcel) {
        this(parcel.readString(), parcel.readString(), parcel.readArrayList(null), parcel.readString(), parcel.readLong(), parcel.readInt(), (VersionedPackage) parcel.readParcelable(null), parcel.readArrayList(null), parcel.createTypedArrayList(CREATOR));
    }

    public int getType() {
        return this.mType;
    }

    public String getName() {
        return this.mName;
    }

    public String getPath() {
        return this.mPath;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public List<String> getAllCodePaths() {
        if (getPath() == null) {
            return this.mCodePaths;
        }
        ArrayList<String> list = new ArrayList();
        list.add(getPath());
        return list;
    }

    public void addDependency(SharedLibraryInfo info) {
        if (info != null) {
            if (this.mDependencies == null) {
                this.mDependencies = new ArrayList();
            }
            this.mDependencies.add(info);
        }
    }

    public void clearDependencies() {
        this.mDependencies = null;
    }

    public List<SharedLibraryInfo> getDependencies() {
        return this.mDependencies;
    }

    @Deprecated
    public int getVersion() {
        long j = this.mVersion;
        if (j >= 0) {
            j &= 2147483647L;
        }
        return (int) j;
    }

    public long getLongVersion() {
        return this.mVersion;
    }

    public boolean isBuiltin() {
        return this.mType == 0;
    }

    public boolean isDynamic() {
        return this.mType == 1;
    }

    public boolean isStatic() {
        return this.mType == 2;
    }

    public VersionedPackage getDeclaringPackage() {
        return this.mDeclaringPackage;
    }

    public List<VersionedPackage> getDependentPackages() {
        List list = this.mDependentPackages;
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SharedLibraryInfo{name:");
        stringBuilder.append(this.mName);
        stringBuilder.append(", type:");
        stringBuilder.append(typeToString(this.mType));
        stringBuilder.append(", version:");
        stringBuilder.append(this.mVersion);
        stringBuilder.append(!getDependentPackages().isEmpty() ? " has dependents" : "");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mPath);
        parcel.writeString(this.mPackageName);
        parcel.writeList(this.mCodePaths);
        parcel.writeString(this.mName);
        parcel.writeLong(this.mVersion);
        parcel.writeInt(this.mType);
        parcel.writeParcelable(this.mDeclaringPackage, flags);
        parcel.writeList(this.mDependentPackages);
        parcel.writeTypedList(this.mDependencies);
    }

    private static String typeToString(int type) {
        if (type == 0) {
            return "builtin";
        }
        if (type == 1) {
            return "dynamic";
        }
        if (type != 2) {
            return "unknown";
        }
        return "static";
    }
}
