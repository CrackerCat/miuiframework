package android.util;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Path;

public class PathParser {
    static final String LOGTAG = PathParser.class.getSimpleName();

    public static class PathData {
        long mNativePathData;

        public PathData() {
            this.mNativePathData = 0;
            this.mNativePathData = PathParser.nCreateEmptyPathData();
        }

        public PathData(PathData data) {
            this.mNativePathData = 0;
            this.mNativePathData = PathParser.nCreatePathData(data.mNativePathData);
        }

        public PathData(String pathString) {
            this.mNativePathData = 0;
            this.mNativePathData = PathParser.nCreatePathDataFromString(pathString, pathString.length());
            if (this.mNativePathData == 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid pathData: ");
                stringBuilder.append(pathString);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }

        public long getNativePtr() {
            return this.mNativePathData;
        }

        public void setPathData(PathData source) {
            PathParser.nSetPathData(this.mNativePathData, source.mNativePathData);
        }

        /* Access modifiers changed, original: protected */
        public void finalize() throws Throwable {
            long j = this.mNativePathData;
            if (j != 0) {
                PathParser.nFinalize(j);
                this.mNativePathData = 0;
            }
            super.finalize();
        }
    }

    private static native boolean nCanMorph(long j, long j2);

    private static native long nCreateEmptyPathData();

    private static native long nCreatePathData(long j);

    private static native long nCreatePathDataFromString(String str, int i);

    private static native void nCreatePathFromPathData(long j, long j2);

    private static native void nFinalize(long j);

    private static native boolean nInterpolatePathData(long j, long j2, long j3, float f);

    private static native void nParseStringForPath(long j, String str, int i);

    private static native void nSetPathData(long j, long j2);

    @UnsupportedAppUsage
    public static Path createPathFromPathData(String pathString) {
        if (pathString != null) {
            Path path = new Path();
            nParseStringForPath(path.mNativePath, pathString, pathString.length());
            return path;
        }
        throw new IllegalArgumentException("Path string can not be null.");
    }

    public static void createPathFromPathData(Path outPath, PathData data) {
        nCreatePathFromPathData(outPath.mNativePath, data.mNativePathData);
    }

    public static boolean canMorph(PathData pathDataFrom, PathData pathDataTo) {
        return nCanMorph(pathDataFrom.mNativePathData, pathDataTo.mNativePathData);
    }

    public static boolean interpolatePathData(PathData outData, PathData fromData, PathData toData, float fraction) {
        return nInterpolatePathData(outData.mNativePathData, fromData.mNativePathData, toData.mNativePathData, fraction);
    }
}
