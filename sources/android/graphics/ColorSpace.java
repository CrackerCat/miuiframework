package android.graphics;

import android.graphics.Bitmap.Config;
import android.graphics.Canvas.VertexMode;
import android.graphics.Paint.Style;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import libcore.util.NativeAllocationRegistry;

public abstract class ColorSpace {
    public static final float[] ILLUMINANT_A = new float[]{0.44757f, 0.40745f};
    public static final float[] ILLUMINANT_B = new float[]{0.34842f, 0.35161f};
    public static final float[] ILLUMINANT_C = new float[]{0.31006f, 0.31616f};
    public static final float[] ILLUMINANT_D50 = new float[]{0.34567f, 0.3585f};
    private static final float[] ILLUMINANT_D50_XYZ = new float[]{0.964212f, 1.0f, 0.825188f};
    public static final float[] ILLUMINANT_D55 = new float[]{0.33242f, 0.34743f};
    public static final float[] ILLUMINANT_D60 = new float[]{0.32168f, 0.33767f};
    public static final float[] ILLUMINANT_D65 = new float[]{0.31271f, 0.32902f};
    public static final float[] ILLUMINANT_D75 = new float[]{0.29902f, 0.31485f};
    public static final float[] ILLUMINANT_E = new float[]{0.33333f, 0.33333f};
    public static final int MAX_ID = 63;
    public static final int MIN_ID = -1;
    private static final float[] NTSC_1953_PRIMARIES = new float[]{0.67f, 0.33f, 0.21f, 0.71f, 0.14f, 0.08f};
    private static final float[] SRGB_PRIMARIES = new float[]{0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f};
    private static final TransferParameters SRGB_TRANSFER_PARAMETERS = new TransferParameters(0.9478672985781991d, 0.05213270142180095d, 0.07739938080495357d, 0.04045d, 2.4d);
    private static final ColorSpace[] sNamedColorSpaces = new ColorSpace[Named.values().length];
    private final int mId;
    private final Model mModel;
    private final String mName;

    public enum Adaptation {
        BRADFORD(new float[]{0.8951f, -0.7502f, 0.0389f, 0.2664f, 1.7135f, -0.0685f, -0.1614f, 0.0367f, 1.0296f}),
        VON_KRIES(new float[]{0.40024f, -0.2263f, 0.0f, 0.7076f, 1.16532f, 0.0f, -0.08081f, 0.0457f, 0.91822f}),
        CIECAT02(new float[]{0.7328f, -0.7036f, 0.003f, 0.4296f, 1.6975f, 0.0136f, -0.1624f, 0.0061f, 0.9834f});
        
        final float[] mTransform;

        private Adaptation(float[] transform) {
            this.mTransform = transform;
        }
    }

    public static class Connector {
        private final ColorSpace mDestination;
        private final RenderIntent mIntent;
        private final ColorSpace mSource;
        private final float[] mTransform;
        private final ColorSpace mTransformDestination;
        private final ColorSpace mTransformSource;

        private static class Rgb extends Connector {
            private final Rgb mDestination;
            private final Rgb mSource;
            private final float[] mTransform;

            Rgb(Rgb source, Rgb destination, RenderIntent intent) {
                super(destination, source, destination, intent, null);
                this.mSource = source;
                this.mDestination = destination;
                this.mTransform = computeTransform(source, destination, intent);
            }

            public float[] transform(float[] rgb) {
                rgb[0] = (float) this.mSource.mClampedEotf.applyAsDouble((double) rgb[0]);
                rgb[1] = (float) this.mSource.mClampedEotf.applyAsDouble((double) rgb[1]);
                rgb[2] = (float) this.mSource.mClampedEotf.applyAsDouble((double) rgb[2]);
                ColorSpace.mul3x3Float3(this.mTransform, rgb);
                rgb[0] = (float) this.mDestination.mClampedOetf.applyAsDouble((double) rgb[0]);
                rgb[1] = (float) this.mDestination.mClampedOetf.applyAsDouble((double) rgb[1]);
                rgb[2] = (float) this.mDestination.mClampedOetf.applyAsDouble((double) rgb[2]);
                return rgb;
            }

            private static float[] computeTransform(Rgb source, Rgb destination, RenderIntent intent) {
                if (ColorSpace.compare(source.mWhitePoint, destination.mWhitePoint)) {
                    return ColorSpace.mul3x3(destination.mInverseTransform, source.mTransform);
                }
                float[] transform = source.mTransform;
                float[] inverseTransform = destination.mInverseTransform;
                float[] srcXYZ = ColorSpace.xyYToXyz(source.mWhitePoint);
                float[] dstXYZ = ColorSpace.xyYToXyz(destination.mWhitePoint);
                if (!ColorSpace.compare(source.mWhitePoint, ColorSpace.ILLUMINANT_D50)) {
                    transform = ColorSpace.mul3x3(ColorSpace.chromaticAdaptation(Adaptation.BRADFORD.mTransform, srcXYZ, Arrays.copyOf(ColorSpace.ILLUMINANT_D50_XYZ, 3)), source.mTransform);
                }
                if (!ColorSpace.compare(destination.mWhitePoint, ColorSpace.ILLUMINANT_D50)) {
                    inverseTransform = ColorSpace.inverse3x3(ColorSpace.mul3x3(ColorSpace.chromaticAdaptation(Adaptation.BRADFORD.mTransform, dstXYZ, Arrays.copyOf(ColorSpace.ILLUMINANT_D50_XYZ, 3)), destination.mTransform));
                }
                if (intent == RenderIntent.ABSOLUTE) {
                    transform = ColorSpace.mul3x3Diag(new float[]{srcXYZ[0] / dstXYZ[0], srcXYZ[1] / dstXYZ[1], srcXYZ[2] / dstXYZ[2]}, transform);
                }
                return ColorSpace.mul3x3(inverseTransform, transform);
            }
        }

        Connector(ColorSpace source, ColorSpace destination, RenderIntent intent) {
            this(source, destination, source.getModel() == Model.RGB ? ColorSpace.adapt(source, ColorSpace.ILLUMINANT_D50_XYZ) : source, destination.getModel() == Model.RGB ? ColorSpace.adapt(destination, ColorSpace.ILLUMINANT_D50_XYZ) : destination, intent, computeTransform(source, destination, intent));
        }

        private Connector(ColorSpace source, ColorSpace destination, ColorSpace transformSource, ColorSpace transformDestination, RenderIntent intent, float[] transform) {
            this.mSource = source;
            this.mDestination = destination;
            this.mTransformSource = transformSource;
            this.mTransformDestination = transformDestination;
            this.mIntent = intent;
            this.mTransform = transform;
        }

        private static float[] computeTransform(ColorSpace source, ColorSpace destination, RenderIntent intent) {
            if (intent != RenderIntent.ABSOLUTE) {
                return null;
            }
            boolean srcRGB = source.getModel() == Model.RGB;
            boolean dstRGB = destination.getModel() == Model.RGB;
            if (srcRGB && dstRGB) {
                return null;
            }
            if (!srcRGB && !dstRGB) {
                return null;
            }
            Rgb rgb = srcRGB ? source : destination;
            float[] srcXYZ = srcRGB ? ColorSpace.xyYToXyz(rgb.mWhitePoint) : ColorSpace.ILLUMINANT_D50_XYZ;
            float[] dstXYZ = dstRGB ? ColorSpace.xyYToXyz(rgb.mWhitePoint) : ColorSpace.ILLUMINANT_D50_XYZ;
            return new float[]{srcXYZ[0] / dstXYZ[0], srcXYZ[1] / dstXYZ[1], srcXYZ[2] / dstXYZ[2]};
        }

        public ColorSpace getSource() {
            return this.mSource;
        }

        public ColorSpace getDestination() {
            return this.mDestination;
        }

        public RenderIntent getRenderIntent() {
            return this.mIntent;
        }

        public float[] transform(float r, float g, float b) {
            return transform(new float[]{r, g, b});
        }

        public float[] transform(float[] v) {
            float[] xyz = this.mTransformSource.toXyz(v);
            float[] fArr = this.mTransform;
            if (fArr != null) {
                xyz[0] = xyz[0] * fArr[0];
                xyz[1] = xyz[1] * fArr[1];
                xyz[2] = xyz[2] * fArr[2];
            }
            return this.mTransformDestination.fromXyz(xyz);
        }

        static Connector identity(ColorSpace source) {
            return new Connector(source, source, RenderIntent.RELATIVE) {
                public float[] transform(float[] v) {
                    return v;
                }
            };
        }
    }

    private static final class Lab extends ColorSpace {
        private static final float A = 0.008856452f;
        private static final float B = 7.787037f;
        private static final float C = 0.13793103f;
        private static final float D = 0.20689656f;

        private Lab(String name, int id) {
            super(name, Model.LAB, id);
        }

        public boolean isWideGamut() {
            return true;
        }

        public float getMinValue(int component) {
            return component == 0 ? 0.0f : -128.0f;
        }

        public float getMaxValue(int component) {
            return component == 0 ? 100.0f : 128.0f;
        }

        public float[] toXyz(float[] v) {
            v[0] = clamp(v[0], 0.0f, 100.0f);
            v[1] = clamp(v[1], -128.0f, 128.0f);
            v[2] = clamp(v[2], -128.0f, 128.0f);
            float fy = (v[0] + 16.0f) / 116.0f;
            float fx = (v[1] * 0.002f) + fy;
            float fz = fy - (v[2] * 0.005f);
            float X = fx > D ? (fx * fx) * fx : (fx - C) * 0.12841855f;
            float Y = fy > D ? (fy * fy) * fy : (fy - C) * 0.12841855f;
            float Z = fz > D ? (fz * fz) * fz : (fz - C) * 0.12841855f;
            v[0] = ColorSpace.ILLUMINANT_D50_XYZ[0] * X;
            v[1] = ColorSpace.ILLUMINANT_D50_XYZ[1] * Y;
            v[2] = ColorSpace.ILLUMINANT_D50_XYZ[2] * Z;
            return v;
        }

        public float[] fromXyz(float[] v) {
            float X = v[0] / ColorSpace.ILLUMINANT_D50_XYZ[0];
            float Y = v[1] / ColorSpace.ILLUMINANT_D50_XYZ[1];
            float Z = v[2] / ColorSpace.ILLUMINANT_D50_XYZ[2];
            float fx = X > A ? (float) Math.pow((double) X, 0.3333333333333333d) : (X * B) + C;
            float fy = Y > A ? (float) Math.pow((double) Y, 0.3333333333333333d) : (Y * B) + C;
            float a = (fx - fy) * 500.0f;
            float b = (fy - (Z > A ? (float) Math.pow((double) Z, 0.3333333333333333d) : (B * Z) + C)) * 200.0f;
            v[0] = clamp((116.0f * fy) - 16.0f, 0.0f, 100.0f);
            v[1] = clamp(a, -128.0f, 128.0f);
            v[2] = clamp(b, -128.0f, 128.0f);
            return v;
        }

        private static float clamp(float x, float min, float max) {
            if (x < min) {
                return min;
            }
            return x > max ? max : x;
        }
    }

    public enum Model {
        RGB(3),
        XYZ(3),
        LAB(3),
        CMYK(4);
        
        private final int mComponentCount;

        private Model(int componentCount) {
            this.mComponentCount = componentCount;
        }

        public int getComponentCount() {
            return this.mComponentCount;
        }
    }

    public enum Named {
        SRGB,
        LINEAR_SRGB,
        EXTENDED_SRGB,
        LINEAR_EXTENDED_SRGB,
        BT709,
        BT2020,
        DCI_P3,
        DISPLAY_P3,
        NTSC_1953,
        SMPTE_C,
        ADOBE_RGB,
        PRO_PHOTO_RGB,
        ACES,
        ACESCG,
        CIE_XYZ,
        CIE_LAB
    }

    public enum RenderIntent {
        PERCEPTUAL,
        RELATIVE,
        SATURATION,
        ABSOLUTE
    }

    public static class Renderer {
        private static final int CHROMATICITY_RESOLUTION = 32;
        private static final int NATIVE_SIZE = 1440;
        private static final double ONE_THIRD = 0.3333333333333333d;
        private static final float[] SPECTRUM_LOCUS_X = new float[]{0.175596f, 0.172787f, 0.170806f, 0.170085f, 0.160343f, 0.146958f, 0.139149f, 0.133536f, 0.126688f, 0.11583f, 0.109616f, 0.099146f, 0.09131f, 0.07813f, 0.068717f, 0.054675f, 0.040763f, 0.027497f, 0.01627f, 0.008169f, 0.004876f, 0.003983f, 0.003859f, 0.004646f, 0.007988f, 0.01387f, 0.022244f, 0.027273f, 0.03282f, 0.038851f, 0.045327f, 0.052175f, 0.059323f, 0.066713f, 0.074299f, 0.089937f, 0.114155f, 0.138695f, 0.154714f, 0.192865f, 0.229607f, 0.26576f, 0.301588f, 0.337346f, 0.373083f, 0.408717f, 0.444043f, 0.478755f, 0.512467f, 0.544767f, 0.575132f, 0.602914f, 0.627018f, 0.648215f, 0.665746f, 0.680061f, 0.691487f, 0.700589f, 0.707901f, 0.714015f, 0.719017f, 0.723016f, 0.734674f, 0.717203f, 0.699732f, 0.68226f, 0.664789f, 0.647318f, 0.629847f, 0.612376f, 0.594905f, 0.577433f, 0.559962f, 0.542491f, 0.52502f, 0.507549f, 0.490077f, 0.472606f, 0.455135f, 0.437664f, 0.420193f, 0.402721f, 0.38525f, 0.367779f, 0.350308f, 0.332837f, 0.315366f, 0.297894f, 0.280423f, 0.262952f, 0.245481f, 0.22801f, 0.210538f, 0.193067f, 0.175596f};
        private static final float[] SPECTRUM_LOCUS_Y = new float[]{0.005295f, 0.0048f, 0.005472f, 0.005976f, 0.014496f, 0.026643f, 0.035211f, 0.042704f, 0.053441f, 0.073601f, 0.086866f, 0.112037f, 0.132737f, 0.170464f, 0.200773f, 0.254155f, 0.317049f, 0.387997f, 0.463035f, 0.538504f, 0.587196f, 0.610526f, 0.654897f, 0.67597f, 0.715407f, 0.750246f, 0.779682f, 0.792153f, 0.802971f, 0.812059f, 0.81943f, 0.8252f, 0.82946f, 0.832306f, 0.833833f, 0.833316f, 0.826231f, 0.814796f, 0.805884f, 0.781648f, 0.754347f, 0.724342f, 0.692326f, 0.658867f, 0.62447f, 0.589626f, 0.554734f, 0.520222f, 0.486611f, 0.454454f, 0.424252f, 0.396516f, 0.37251f, 0.351413f, 0.334028f, 0.319765f, 0.308359f, 0.299317f, 0.292044f, 0.285945f, 0.280951f, 0.276964f, 0.265326f, 0.2572f, 0.249074f, 0.240948f, 0.232822f, 0.224696f, 0.21657f, 0.208444f, 0.200318f, 0.192192f, 0.184066f, 0.17594f, 0.167814f, 0.159688f, 0.151562f, 0.143436f, 0.135311f, 0.127185f, 0.119059f, 0.110933f, 0.102807f, 0.094681f, 0.086555f, 0.078429f, 0.070303f, 0.062177f, 0.054051f, 0.045925f, 0.037799f, 0.029673f, 0.021547f, 0.013421f, 0.005295f};
        private static final float UCS_SCALE = 1.5f;
        private boolean mClip;
        private final List<Pair<ColorSpace, Integer>> mColorSpaces;
        private final List<Point> mPoints;
        private boolean mShowWhitePoint;
        private int mSize;
        private boolean mUcs;

        private static class Point {
            final int mColor;
            final ColorSpace mColorSpace;
            final float[] mRgb;

            Point(ColorSpace colorSpace, float[] rgb, int color) {
                this.mColorSpace = colorSpace;
                this.mRgb = rgb;
                this.mColor = color;
            }
        }

        private Renderer() {
            this.mSize = 1024;
            this.mShowWhitePoint = true;
            this.mClip = false;
            this.mUcs = false;
            this.mColorSpaces = new ArrayList(2);
            this.mPoints = new ArrayList(0);
        }

        public Renderer clip(boolean clip) {
            this.mClip = clip;
            return this;
        }

        public Renderer uniformChromaticityScale(boolean ucs) {
            this.mUcs = ucs;
            return this;
        }

        public Renderer size(int size) {
            this.mSize = Math.max(128, size);
            return this;
        }

        public Renderer showWhitePoint(boolean show) {
            this.mShowWhitePoint = show;
            return this;
        }

        public Renderer add(ColorSpace colorSpace, int color) {
            this.mColorSpaces.add(new Pair(colorSpace, Integer.valueOf(color)));
            return this;
        }

        public Renderer add(ColorSpace colorSpace, float r, float g, float b, int pointColor) {
            this.mPoints.add(new Point(colorSpace, new float[]{r, g, b}, pointColor));
            return this;
        }

        public Bitmap render() {
            Paint paint = new Paint(1);
            int i = this.mSize;
            Bitmap bitmap = Bitmap.createBitmap(i, i, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            float[] primaries = new float[6];
            float[] whitePoint = new float[2];
            Path path = new Path();
            setTransform(canvas, 1440, 1440, primaries);
            drawBox(canvas, 1440, 1440, paint, path);
            setUcsTransform(canvas, 1440);
            i = 1440;
            int width = 1440;
            int i2 = i;
            float[] whitePoint2 = whitePoint;
            Paint paint2 = paint;
            float[] primaries2 = primaries;
            Path path2 = path;
            Canvas canvas2 = canvas;
            float[] fArr = primaries2;
            drawLocus(canvas, 1440, i2, paint2, path2, fArr);
            Bitmap bitmap2 = bitmap;
            drawGamuts(canvas2, width, i2, paint2, path2, fArr, whitePoint2);
            drawPoints(canvas2, width, i, paint);
            return bitmap2;
        }

        private void drawPoints(Canvas canvas, int width, int height, Paint paint) {
            paint.setStyle(Style.FILL);
            float radius = 4.0f / (this.mUcs ? UCS_SCALE : 1.0f);
            float[] v = new float[3];
            float[] xy = new float[2];
            for (Point point : this.mPoints) {
                v[0] = point.mRgb[0];
                v[1] = point.mRgb[1];
                v[2] = point.mRgb[2];
                point.mColorSpace.toXyz(v);
                paint.setColor(point.mColor);
                float sum = (v[0] + v[1]) + v[2];
                xy[0] = v[0] / sum;
                xy[1] = v[1] / sum;
                if (this.mUcs) {
                    ColorSpace.xyYToUv(xy);
                }
                canvas.drawCircle(((float) width) * xy[0], ((float) height) - (((float) height) * xy[1]), radius, paint);
            }
        }

        private void drawGamuts(Canvas canvas, int width, int height, Paint paint, Path path, float[] primaries, float[] whitePoint) {
            Canvas canvas2 = canvas;
            int i = width;
            int i2 = height;
            Paint paint2 = paint;
            Path path2 = path;
            float[] fArr = primaries;
            float[] fArr2 = whitePoint;
            float radius = 4.0f / (this.mUcs ? UCS_SCALE : 1.0f);
            Iterator it = this.mColorSpaces.iterator();
            while (it.hasNext()) {
                Pair<ColorSpace, Integer> item = (Pair) it.next();
                ColorSpace colorSpace = item.first;
                int color = ((Integer) item.second).intValue();
                if (colorSpace.getModel() == Model.RGB) {
                    Rgb rgb = (Rgb) colorSpace;
                    getPrimaries(rgb, fArr, this.mUcs);
                    path.rewind();
                    Iterator it2 = it;
                    path2.moveTo(((float) i) * fArr[0], ((float) i2) - (((float) i2) * fArr[1]));
                    path2.lineTo(((float) i) * fArr[2], ((float) i2) - (((float) i2) * fArr[3]));
                    path2.lineTo(((float) i) * fArr[4], ((float) i2) - (((float) i2) * fArr[5]));
                    path.close();
                    paint2.setStyle(Style.STROKE);
                    paint2.setColor(color);
                    canvas2.drawPath(path2, paint2);
                    if (this.mShowWhitePoint) {
                        rgb.getWhitePoint(fArr2);
                        if (this.mUcs) {
                            ColorSpace.xyYToUv(whitePoint);
                        }
                        paint2.setStyle(Style.FILL);
                        paint2.setColor(color);
                        canvas2.drawCircle(((float) i) * fArr2[0], ((float) i2) - (((float) i2) * fArr2[1]), radius, paint2);
                    }
                    it = it2;
                }
            }
        }

        private static void getPrimaries(Rgb rgb, float[] primaries, boolean asUcs) {
            if (rgb.equals(ColorSpace.get(Named.EXTENDED_SRGB)) || rgb.equals(ColorSpace.get(Named.LINEAR_EXTENDED_SRGB))) {
                primaries[0] = 1.41f;
                primaries[1] = 0.33f;
                primaries[2] = 0.27f;
                primaries[3] = 1.24f;
                primaries[4] = -0.23f;
                primaries[5] = -0.57f;
            } else {
                rgb.getPrimaries(primaries);
            }
            if (asUcs) {
                ColorSpace.xyYToUv(primaries);
            }
        }

        private void drawLocus(Canvas canvas, int width, int height, Paint paint, Path path, float[] primaries) {
            int i;
            Path path2;
            float[] vertices;
            Canvas canvas2 = canvas;
            int i2 = width;
            int i3 = height;
            Paint paint2 = paint;
            Path path3 = path;
            float[] fArr = primaries;
            float[] vertices2 = new float[(((SPECTRUM_LOCUS_X.length * 32) * 6) * 2)];
            int[] colors = new int[vertices2.length];
            computeChromaticityMesh(vertices2, colors);
            if (this.mUcs) {
                ColorSpace.xyYToUv(vertices2);
            }
            for (i = 0; i < vertices2.length; i += 2) {
                vertices2[i] = vertices2[i] * ((float) i2);
                vertices2[i + 1] = ((float) i3) - (vertices2[i + 1] * ((float) i3));
            }
            if (!this.mClip || this.mColorSpaces.size() <= 0) {
                path2 = path3;
                float[] vertices3 = vertices2;
                vertices = vertices3;
                canvas.drawVertices(VertexMode.TRIANGLES, vertices3.length, vertices3, 0, null, 0, colors, 0, null, 0, 0, paint);
            } else {
                for (Pair<ColorSpace, Integer> item : this.mColorSpaces) {
                    ColorSpace colorSpace = item.first;
                    if (colorSpace.getModel() == Model.RGB) {
                        getPrimaries((Rgb) colorSpace, fArr, this.mUcs);
                        break;
                    }
                }
                path.rewind();
                path3.moveTo(((float) i2) * fArr[0], ((float) i3) - (((float) i3) * fArr[1]));
                path3.lineTo(((float) i2) * fArr[2], ((float) i3) - (((float) i3) * fArr[3]));
                path3.lineTo(((float) i2) * fArr[4], ((float) i3) - (((float) i3) * fArr[5]));
                path.close();
                int[] solid = new int[colors.length];
                Arrays.fill(solid, -9671572);
                Canvas canvas3 = canvas;
                int[] colors2 = colors;
                float[] vertices4 = vertices2;
                canvas3.drawVertices(VertexMode.TRIANGLES, vertices2.length, vertices2, 0, null, 0, solid, 0, null, 0, 0, paint);
                canvas.save();
                Path path4 = path;
                canvas2.clipPath(path4);
                float[] vertices5 = vertices4;
                float[] vertices6 = vertices5;
                path2 = path4;
                canvas3.drawVertices(VertexMode.TRIANGLES, vertices5.length, vertices5, 0, null, 0, colors2, 0, null, 0, 0, paint);
                canvas.restore();
                vertices = vertices6;
            }
            i = 372;
            path.reset();
            path2.moveTo(vertices[372], vertices[372 + 1]);
            for (int x = 2; x < SPECTRUM_LOCUS_X.length; x++) {
                i += 384;
                path2.lineTo(vertices[i], vertices[i + 1]);
            }
            path.close();
            Paint paint3 = paint;
            paint3.setStrokeWidth(4.0f / (this.mUcs ? UCS_SCALE : 1.0f));
            paint3.setStyle(Style.STROKE);
            paint3.setColor(-16777216);
            canvas2.drawPath(path2, paint3);
        }

        private void drawBox(Canvas canvas, int width, int height, Paint paint, Path path) {
            int lineCount;
            float scale;
            int i;
            float x;
            float y;
            Paint paint2;
            Canvas canvas2;
            float f;
            Canvas canvas3 = canvas;
            int i2 = width;
            int i3 = height;
            Paint paint3 = paint;
            Path path2 = path;
            if (this.mUcs) {
                lineCount = 7;
                scale = UCS_SCALE;
            } else {
                lineCount = 10;
                scale = 1.0f;
            }
            paint3.setStyle(Style.STROKE);
            paint3.setStrokeWidth(2.0f);
            paint3.setColor(-4144960);
            for (i = 1; i < lineCount - 1; i++) {
                float v = ((float) i) / 10.0f;
                x = (((float) i2) * v) * scale;
                y = ((float) i3) - ((((float) i3) * v) * scale);
                paint2 = paint;
                canvas.drawLine(0.0f, y, ((float) i2) * 0.9f, y, paint2);
                canvas2 = canvas;
                f = x;
                canvas2.drawLine(f, (float) i3, x, ((float) i3) * 0.1f, paint2);
            }
            paint3.setStrokeWidth(4.0f);
            paint3.setColor(-16777216);
            for (i = 1; i < lineCount - 1; i++) {
                x = ((float) i) / 10.0f;
                y = (((float) i2) * x) * scale;
                float y2 = ((float) i3) - ((((float) i3) * x) * scale);
                paint2 = paint;
                canvas.drawLine(0.0f, y2, ((float) i2) / 100.0f, y2, paint2);
                canvas2 = canvas;
                f = y;
                canvas2.drawLine(f, (float) i3, y, ((float) i3) - (((float) i3) / 100.0f), paint2);
            }
            paint3.setStyle(Style.FILL);
            paint3.setTextSize(36.0f);
            int i4 = 0;
            paint3.setTypeface(Typeface.create("sans-serif-light", 0));
            Rect bounds = new Rect();
            int i5 = 1;
            while (i5 < lineCount - 1) {
                String text = new StringBuilder();
                text.append("0.");
                text.append(i5);
                text = text.toString();
                paint3.getTextBounds(text, i4, text.length(), bounds);
                float v2 = ((float) i5) / 10.0f;
                float x2 = (((float) i2) * v2) * scale;
                canvas3.drawText(text, (((float) i2) * -0.05f) + 10.0f, (((float) bounds.height()) / 2.0f) + (((float) i3) - ((((float) i3) * v2) * scale)), paint3);
                canvas3.drawText(text, x2 - (((float) bounds.width()) / 2.0f), (float) ((bounds.height() + i3) + 16), paint3);
                i5++;
                float f2 = 2.0f;
                i4 = 0;
            }
            paint3.setStyle(Style.STROKE);
            path2.moveTo(0.0f, (float) i3);
            path2.lineTo(((float) i2) * 0.9f, (float) i3);
            path2.lineTo(((float) i2) * 0.9f, ((float) i3) * 0.1f);
            path2.lineTo(0.0f, ((float) i3) * 0.1f);
            path.close();
            canvas3.drawPath(path2, paint3);
        }

        private void setTransform(Canvas canvas, int width, int height, float[] primaries) {
            RectF primariesBounds = new RectF();
            for (Pair<ColorSpace, Integer> item : this.mColorSpaces) {
                ColorSpace colorSpace = item.first;
                if (colorSpace.getModel() == Model.RGB) {
                    getPrimaries((Rgb) colorSpace, primaries, this.mUcs);
                    primariesBounds.left = Math.min(primariesBounds.left, primaries[4]);
                    primariesBounds.top = Math.min(primariesBounds.top, primaries[5]);
                    primariesBounds.right = Math.max(primariesBounds.right, primaries[0]);
                    primariesBounds.bottom = Math.max(primariesBounds.bottom, primaries[3]);
                }
            }
            float max = this.mUcs ? 0.6f : 0.9f;
            primariesBounds.left = Math.min(0.0f, primariesBounds.left);
            primariesBounds.top = Math.min(0.0f, primariesBounds.top);
            primariesBounds.right = Math.max(max, primariesBounds.right);
            primariesBounds.bottom = Math.max(max, primariesBounds.bottom);
            float scale = Math.min(max / primariesBounds.width(), max / primariesBounds.height());
            int i = this.mSize;
            canvas.scale(((float) i) / 1440.0f, ((float) i) / 1440.0f);
            canvas.scale(scale, scale);
            canvas.translate(((primariesBounds.width() - max) * ((float) width)) / 2.0f, ((primariesBounds.height() - max) * ((float) height)) / 2.0f);
            canvas.translate(((float) width) * 0.05f, ((float) height) * -0.05f);
        }

        private void setUcsTransform(Canvas canvas, int height) {
            if (this.mUcs) {
                canvas.translate(0.0f, ((float) height) - (((float) height) * UCS_SCALE));
                canvas.scale(UCS_SCALE, UCS_SCALE);
            }
        }

        private static void computeChromaticityMesh(float[] vertices, int[] colors) {
            ColorSpace colorSpace = ColorSpace.get(Named.SRGB);
            float[] color = new float[3];
            int vertexIndex = 0;
            int colorIndex = 0;
            int x = 0;
            while (true) {
                float[] fArr = SPECTRUM_LOCUS_X;
                if (x < fArr.length) {
                    int colorIndex2;
                    float a1;
                    int nextX;
                    float radius2;
                    int c;
                    float radius1;
                    float v1y;
                    int vertexIndex2;
                    int nextX2 = (x % (fArr.length - 1)) + 1;
                    float a12 = (float) Math.atan2(((double) SPECTRUM_LOCUS_Y[x]) - ONE_THIRD, ((double) fArr[x]) - ONE_THIRD);
                    float a2 = (float) Math.atan2(((double) SPECTRUM_LOCUS_Y[nextX2]) - ONE_THIRD, ((double) SPECTRUM_LOCUS_X[nextX2]) - ONE_THIRD);
                    float radius12 = (float) Math.pow(sqr(((double) SPECTRUM_LOCUS_X[x]) - ONE_THIRD) + sqr(((double) SPECTRUM_LOCUS_Y[x]) - ONE_THIRD), 0.5d);
                    float radius22 = (float) Math.pow(sqr(((double) SPECTRUM_LOCUS_X[nextX2]) - ONE_THIRD) + sqr(((double) SPECTRUM_LOCUS_Y[nextX2]) - ONE_THIRD), 0.5d);
                    int c2 = 1;
                    while (c2 <= 32) {
                        float f1 = ((float) c2) / 32.0f;
                        float f2 = ((float) (c2 - 1)) / 32.0f;
                        int vertexIndex3 = vertexIndex;
                        colorIndex2 = colorIndex;
                        double cr1 = ((double) radius12) * Math.cos((double) a12);
                        ColorSpace colorSpace2 = colorSpace;
                        float[] color2 = color;
                        double sr1 = ((double) radius12) * Math.sin((double) a12);
                        a1 = a12;
                        nextX = nextX2;
                        colorSpace = ((double) radius22) * Math.cos((double) a2);
                        radius2 = radius22;
                        c = c2;
                        double sr2 = ((double) radius22) * Math.sin((double) a2);
                        radius22 = (float) ((((double) f1) * cr1) + 1.46601547E13f);
                        float a22 = a2;
                        radius1 = radius12;
                        a2 = (float) ((((double) f1) * sr1) + 1.46601547E13f);
                        v1y = a2;
                        float v1z = (1.0f - radius22) - a2;
                        a2 = (float) ((((double) f2) * cr1) + 1.46601547E13f);
                        radius12 = (float) ((((double) f2) * sr1) + 1.46601547E13f);
                        float v2z = (1.0f - a2) - radius12;
                        float v3x = (float) ((((double) f2) * colorSpace) + 1.46601547E13f);
                        int x2 = x;
                        float v3y = (float) ((((double) f2) * sr2) + 1.46601547E13f);
                        float v3z = (1.0f - v3x) - v3y;
                        vertexIndex2 = vertexIndex3;
                        float v4x = (float) ((((double) f1) * colorSpace) + 1.46601547E13f);
                        double cr2 = colorSpace;
                        colorSpace = (float) ((((double) f1) * sr2) + 4599676419421066581);
                        float v4z = (1.0f - v4x) - colorSpace;
                        ColorSpace colorSpace3 = colorSpace2;
                        float f = v1y;
                        v1y = a22;
                        double d = sr2;
                        fArr = color2;
                        double sr22 = d;
                        float v1y2 = f;
                        colors[colorIndex2] = computeColor(fArr, radius22, v1y2, v1z, colorSpace3);
                        colors[colorIndex2 + 1] = computeColor(fArr, a2, radius12, v2z, colorSpace3);
                        colors[colorIndex2 + 2] = computeColor(fArr, v3x, v3y, v3z, colorSpace3);
                        colors[colorIndex2 + 3] = colors[colorIndex2];
                        colors[colorIndex2 + 4] = colors[colorIndex2 + 2];
                        colors[colorIndex2 + 5] = computeColor(fArr, v4x, colorSpace, v4z, colorSpace3);
                        colorIndex2 += 6;
                        int vertexIndex4 = vertexIndex2 + 1;
                        vertices[vertexIndex2] = radius22;
                        int vertexIndex5 = vertexIndex4 + 1;
                        vertices[vertexIndex4] = v1y2;
                        vertexIndex4 = vertexIndex5 + 1;
                        vertices[vertexIndex5] = a2;
                        vertexIndex5 = vertexIndex4 + 1;
                        vertices[vertexIndex4] = radius12;
                        vertexIndex4 = vertexIndex5 + 1;
                        vertices[vertexIndex5] = v3x;
                        vertexIndex5 = vertexIndex4 + 1;
                        vertices[vertexIndex4] = v3y;
                        vertexIndex4 = vertexIndex5 + 1;
                        vertices[vertexIndex5] = radius22;
                        vertexIndex5 = vertexIndex4 + 1;
                        vertices[vertexIndex4] = v1y2;
                        vertexIndex4 = vertexIndex5 + 1;
                        vertices[vertexIndex5] = v3x;
                        vertexIndex5 = vertexIndex4 + 1;
                        vertices[vertexIndex4] = v3y;
                        vertexIndex4 = vertexIndex5 + 1;
                        vertices[vertexIndex5] = v4x;
                        vertexIndex5 = vertexIndex4 + 1;
                        vertices[vertexIndex4] = colorSpace;
                        c2 = c + 1;
                        color = fArr;
                        x = x2;
                        colorSpace = colorSpace3;
                        x2 = 1431655765;
                        colorIndex = colorIndex2;
                        nextX2 = nextX;
                        a12 = a1;
                        radius22 = radius2;
                        radius12 = radius1;
                        a2 = v1y;
                        vertexIndex = vertexIndex5;
                    }
                    vertexIndex2 = vertexIndex;
                    colorIndex2 = colorIndex;
                    a1 = a12;
                    nextX = nextX2;
                    v1y = a2;
                    radius1 = radius12;
                    radius2 = radius22;
                    c = c2;
                    x++;
                } else {
                    return;
                }
            }
        }

        private static int computeColor(float[] color, float x, float y, float z, ColorSpace cs) {
            color[0] = x;
            color[1] = y;
            color[2] = z;
            cs.fromXyz(color);
            return ((((((int) (color[0] * 255.0f)) & 255) << 16) | -16777216) | ((((int) (color[1] * 255.0f)) & 255) << 8)) | (((int) (color[2] * 255.0f)) & 255);
        }

        private static double sqr(double v) {
            return v * v;
        }
    }

    public static class Rgb extends ColorSpace {
        private final DoubleUnaryOperator mClampedEotf;
        private final DoubleUnaryOperator mClampedOetf;
        private final DoubleUnaryOperator mEotf;
        private final float[] mInverseTransform;
        private final boolean mIsSrgb;
        private final boolean mIsWideGamut;
        private final float mMax;
        private final float mMin;
        private final long mNativePtr;
        private final DoubleUnaryOperator mOetf;
        private final float[] mPrimaries;
        private final TransferParameters mTransferParameters;
        private final float[] mTransform;
        private final float[] mWhitePoint;

        private static class NoImagePreloadHolder {
            public static final NativeAllocationRegistry sRegistry = new NativeAllocationRegistry(Rgb.class.getClassLoader(), Rgb.nativeGetNativeFinalizer(), 0);

            private NoImagePreloadHolder() {
            }
        }

        public static class TransferParameters {
            public final double a;
            public final double b;
            public final double c;
            public final double d;
            public final double e;
            public final double f;
            public final double g;

            public TransferParameters(double a, double b, double c, double d, double g) {
                this(a, b, c, d, 0.0d, 0.0d, g);
            }

            public TransferParameters(double a, double b, double c, double d, double e, double f, double g) {
                double d2 = a;
                double d3 = c;
                double d4 = d;
                double d5 = g;
                double d6;
                double d7;
                double d8;
                if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) {
                    d6 = b;
                    d7 = e;
                    d8 = f;
                } else if (Double.isNaN(d) || Double.isNaN(e) || Double.isNaN(f)) {
                    d6 = b;
                    d7 = e;
                    d8 = f;
                } else if (Double.isNaN(g)) {
                    d6 = b;
                    d7 = e;
                    d8 = f;
                } else if (d4 < 0.0d || d4 > ((double) (Math.ulp(1.0f) + 1.0f))) {
                    d6 = b;
                    d7 = e;
                    d8 = f;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Parameter d must be in the range [0..1], was ");
                    stringBuilder.append(d4);
                    throw new IllegalArgumentException(stringBuilder.toString());
                } else if (d4 == 0.0d && (d2 == 0.0d || d5 == 0.0d)) {
                    throw new IllegalArgumentException("Parameter a or g is zero, the transfer function is constant");
                } else if (d4 >= 1.0d && d3 == 0.0d) {
                    throw new IllegalArgumentException("Parameter c is zero, the transfer function is constant");
                } else if ((d2 == 0.0d || d5 == 0.0d) && d3 == 0.0d) {
                    d6 = b;
                    d7 = e;
                    d8 = f;
                    throw new IllegalArgumentException("Parameter a or g is zero, and c is zero, the transfer function is constant");
                } else if (d3 < 0.0d) {
                    d6 = b;
                    d7 = e;
                    d8 = f;
                    throw new IllegalArgumentException("The transfer function must be increasing");
                } else if (d2 < 0.0d || d5 < 0.0d) {
                    d6 = b;
                    d7 = e;
                    d8 = f;
                    throw new IllegalArgumentException("The transfer function must be positive or increasing");
                } else {
                    this.a = d2;
                    this.b = b;
                    this.c = d3;
                    this.d = d4;
                    this.e = e;
                    this.f = f;
                    this.g = d5;
                    return;
                }
                throw new IllegalArgumentException("Parameters cannot be NaN");
            }

            public boolean equals(Object o) {
                boolean z = true;
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                TransferParameters that = (TransferParameters) o;
                if (Double.compare(that.a, this.a) != 0 || Double.compare(that.b, this.b) != 0 || Double.compare(that.c, this.c) != 0 || Double.compare(that.d, this.d) != 0 || Double.compare(that.e, this.e) != 0 || Double.compare(that.f, this.f) != 0) {
                    return false;
                }
                if (Double.compare(that.g, this.g) != 0) {
                    z = false;
                }
                return z;
            }

            public int hashCode() {
                long temp = Double.doubleToLongBits(this.a);
                int result = (int) ((temp >>> 32) ^ temp);
                temp = Double.doubleToLongBits(this.b);
                int result2 = (result * 31) + ((int) ((temp >>> 32) ^ temp));
                temp = Double.doubleToLongBits(this.c);
                result = (result2 * 31) + ((int) ((temp >>> 32) ^ temp));
                temp = Double.doubleToLongBits(this.d);
                result2 = (result * 31) + ((int) ((temp >>> 32) ^ temp));
                temp = Double.doubleToLongBits(this.e);
                result = (result2 * 31) + ((int) ((temp >>> 32) ^ temp));
                temp = Double.doubleToLongBits(this.f);
                result2 = (result * 31) + ((int) ((temp >>> 32) ^ temp));
                temp = Double.doubleToLongBits(this.g);
                return (result2 * 31) + ((int) ((temp >>> 32) ^ temp));
            }
        }

        private static native long nativeCreate(float f, float f2, float f3, float f4, float f5, float f6, float f7, float[] fArr);

        private static native long nativeGetNativeFinalizer();

        /* Access modifiers changed, original: 0000 */
        public long getNativeInstance() {
            long j = this.mNativePtr;
            if (j != 0) {
                return j;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ColorSpace must use an ICC parametric transfer function! used ");
            stringBuilder.append(this);
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public Rgb(String name, float[] toXYZ, DoubleUnaryOperator oetf, DoubleUnaryOperator eotf) {
            this(name, computePrimaries(toXYZ), computeWhitePoint(toXYZ), null, oetf, eotf, 0.0f, 1.0f, null, -1);
        }

        public Rgb(String name, float[] primaries, float[] whitePoint, DoubleUnaryOperator oetf, DoubleUnaryOperator eotf, float min, float max) {
            this(name, primaries, whitePoint, null, oetf, eotf, min, max, null, -1);
        }

        public Rgb(String name, float[] toXYZ, TransferParameters function) {
            this(name, computePrimaries(toXYZ), computeWhitePoint(toXYZ), function, -1);
        }

        public Rgb(String name, float[] primaries, float[] whitePoint, TransferParameters function) {
            this(name, primaries, whitePoint, function, -1);
        }

        private Rgb(String name, float[] primaries, float[] whitePoint, TransferParameters function, int id) {
            DoubleUnaryOperator -__lambda_colorspace_rgb_bwzafc8vmhnuvmrutupefumlfuy;
            DoubleUnaryOperator -__lambda_colorspace_rgb_b9vgkunnse0bbgur9jbom_wk2ac;
            TransferParameters transferParameters = function;
            if (transferParameters.e == 0.0d && transferParameters.f == 0.0d) {
                -__lambda_colorspace_rgb_bwzafc8vmhnuvmrutupefumlfuy = new -$$Lambda$ColorSpace$Rgb$bWzafC8vMHNuVmRuTUPEFUMlfuY(transferParameters);
            } else {
                -__lambda_colorspace_rgb_bwzafc8vmhnuvmrutupefumlfuy = new -$$Lambda$ColorSpace$Rgb$V_0lmM2WEpxGBDV_1G1wvvidn7Y(transferParameters);
            }
            if (transferParameters.e == 0.0d && transferParameters.f == 0.0d) {
                -__lambda_colorspace_rgb_b9vgkunnse0bbgur9jbom_wk2ac = new -$$Lambda$ColorSpace$Rgb$b9VGKuNnse0bbguR9jbOM_wK2Ac(transferParameters);
            } else {
                -__lambda_colorspace_rgb_b9vgkunnse0bbgur9jbom_wk2ac = new -$$Lambda$ColorSpace$Rgb$iMkODTKa3_8kPZUnZZerD2Lv-yo(transferParameters);
            }
            this(name, primaries, whitePoint, null, -__lambda_colorspace_rgb_bwzafc8vmhnuvmrutupefumlfuy, -__lambda_colorspace_rgb_b9vgkunnse0bbgur9jbom_wk2ac, 0.0f, 1.0f, function, id);
        }

        public Rgb(String name, float[] toXYZ, double gamma) {
            this(name, computePrimaries(toXYZ), computeWhitePoint(toXYZ), gamma, 0.0f, 1.0f, -1);
        }

        public Rgb(String name, float[] primaries, float[] whitePoint, double gamma) {
            this(name, primaries, whitePoint, gamma, 0.0f, 1.0f, -1);
        }

        private Rgb(String name, float[] primaries, float[] whitePoint, double gamma, float min, float max, int id) {
            DoubleUnaryOperator identity;
            DoubleUnaryOperator identity2;
            double d = gamma;
            if (d == 1.0d) {
                identity = DoubleUnaryOperator.identity();
            } else {
                identity = new -$$Lambda$ColorSpace$Rgb$CqKld6797g7__JnuY0NeFz5q4_E(d);
            }
            if (d == 1.0d) {
                identity2 = DoubleUnaryOperator.identity();
            } else {
                identity2 = new -$$Lambda$ColorSpace$Rgb$ZvS77aTfobOSa2o9MTqYMph4Rcg(d);
            }
            this(name, primaries, whitePoint, null, identity, identity2, min, max, new TransferParameters(1.0d, 0.0d, 0.0d, 0.0d, gamma), id);
        }

        static /* synthetic */ double lambda$new$4(double gamma, double x) {
            double d = 0.0d;
            if (x >= 0.0d) {
                d = x;
            }
            return Math.pow(d, 1.0d / gamma);
        }

        static /* synthetic */ double lambda$new$5(double gamma, double x) {
            double d = 0.0d;
            if (x >= 0.0d) {
                d = x;
            }
            return Math.pow(d, gamma);
        }

        private Rgb(String name, float[] primaries, float[] whitePoint, float[] transform, DoubleUnaryOperator oetf, DoubleUnaryOperator eotf, float min, float max, TransferParameters transferParameters, int id) {
            float[] fArr = primaries;
            float[] fArr2 = whitePoint;
            float[] fArr3 = transform;
            DoubleUnaryOperator doubleUnaryOperator = oetf;
            DoubleUnaryOperator doubleUnaryOperator2 = eotf;
            float f = min;
            float f2 = max;
            super(name, Model.RGB, id);
            StringBuilder stringBuilder;
            if (fArr == null || !(fArr.length == 6 || fArr.length == 9)) {
                throw new IllegalArgumentException("The color space's primaries must be defined as an array of 6 floats in xyY or 9 floats in XYZ");
            } else if (fArr2 == null || !(fArr2.length == 2 || fArr2.length == 3)) {
                throw new IllegalArgumentException("The color space's white point must be defined as an array of 2 floats in xyY or 3 float in XYZ");
            } else if (doubleUnaryOperator == null || doubleUnaryOperator2 == null) {
                throw new IllegalArgumentException("The transfer functions of a color space cannot be null");
            } else if (f < f2) {
                this.mWhitePoint = xyWhitePoint(whitePoint);
                this.mPrimaries = xyPrimaries(primaries);
                if (fArr3 == null) {
                    this.mTransform = computeXYZMatrix(this.mPrimaries, this.mWhitePoint);
                } else if (fArr3.length == 9) {
                    this.mTransform = fArr3;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Transform must have 9 entries! Has ");
                    stringBuilder.append(fArr3.length);
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                this.mInverseTransform = ColorSpace.inverse3x3(this.mTransform);
                this.mOetf = doubleUnaryOperator;
                this.mEotf = doubleUnaryOperator2;
                this.mMin = f;
                this.mMax = f2;
                DoubleUnaryOperator clamp = new -$$Lambda$ColorSpace$Rgb$8EkhO2jIf14tuA3BvrmYJMa7YXM(this);
                this.mClampedOetf = doubleUnaryOperator.andThen(clamp);
                this.mClampedEotf = clamp.andThen(doubleUnaryOperator2);
                this.mTransferParameters = transferParameters;
                this.mIsWideGamut = isWideGamut(this.mPrimaries, f, f2);
                this.mIsSrgb = isSrgb(this.mPrimaries, this.mWhitePoint, oetf, eotf, min, max, id);
                if (this.mTransferParameters != null) {
                    float[] nativeTransform = this.mWhitePoint;
                    if (nativeTransform != null) {
                        float[] fArr4 = this.mTransform;
                        if (fArr4 != null) {
                            nativeTransform = ColorSpace.adaptToIlluminantD50(nativeTransform, fArr4);
                            this.mNativePtr = nativeCreate((float) this.mTransferParameters.a, (float) this.mTransferParameters.b, (float) this.mTransferParameters.c, (float) this.mTransferParameters.d, (float) this.mTransferParameters.e, (float) this.mTransferParameters.f, (float) this.mTransferParameters.g, nativeTransform);
                            NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, this.mNativePtr);
                            return;
                        }
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ColorSpace (");
                    stringBuilder.append(this);
                    stringBuilder.append(") cannot create native object! mWhitePoint: ");
                    stringBuilder.append(this.mWhitePoint);
                    stringBuilder.append(" mTransform: ");
                    stringBuilder.append(this.mTransform);
                    throw new IllegalStateException(stringBuilder.toString());
                }
                this.mNativePtr = 0;
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid range: min=");
                stringBuilder.append(f);
                stringBuilder.append(", max=");
                stringBuilder.append(f2);
                stringBuilder.append("; min must be strictly < max");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }

        private Rgb(Rgb colorSpace, float[] transform, float[] whitePoint) {
            this(colorSpace.getName(), colorSpace.mPrimaries, whitePoint, transform, colorSpace.mOetf, colorSpace.mEotf, colorSpace.mMin, colorSpace.mMax, colorSpace.mTransferParameters, -1);
        }

        public float[] getWhitePoint(float[] whitePoint) {
            float[] fArr = this.mWhitePoint;
            whitePoint[0] = fArr[0];
            whitePoint[1] = fArr[1];
            return whitePoint;
        }

        public float[] getWhitePoint() {
            float[] fArr = this.mWhitePoint;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getPrimaries(float[] primaries) {
            float[] fArr = this.mPrimaries;
            System.arraycopy(fArr, 0, primaries, 0, fArr.length);
            return primaries;
        }

        public float[] getPrimaries() {
            float[] fArr = this.mPrimaries;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getTransform(float[] transform) {
            float[] fArr = this.mTransform;
            System.arraycopy(fArr, 0, transform, 0, fArr.length);
            return transform;
        }

        public float[] getTransform() {
            float[] fArr = this.mTransform;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getInverseTransform(float[] inverseTransform) {
            float[] fArr = this.mInverseTransform;
            System.arraycopy(fArr, 0, inverseTransform, 0, fArr.length);
            return inverseTransform;
        }

        public float[] getInverseTransform() {
            float[] fArr = this.mInverseTransform;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public DoubleUnaryOperator getOetf() {
            return this.mClampedOetf;
        }

        public DoubleUnaryOperator getEotf() {
            return this.mClampedEotf;
        }

        public TransferParameters getTransferParameters() {
            return this.mTransferParameters;
        }

        public boolean isSrgb() {
            return this.mIsSrgb;
        }

        public boolean isWideGamut() {
            return this.mIsWideGamut;
        }

        public float getMinValue(int component) {
            return this.mMin;
        }

        public float getMaxValue(int component) {
            return this.mMax;
        }

        public float[] toLinear(float r, float g, float b) {
            return toLinear(new float[]{r, g, b});
        }

        public float[] toLinear(float[] v) {
            v[0] = (float) this.mClampedEotf.applyAsDouble((double) v[0]);
            v[1] = (float) this.mClampedEotf.applyAsDouble((double) v[1]);
            v[2] = (float) this.mClampedEotf.applyAsDouble((double) v[2]);
            return v;
        }

        public float[] fromLinear(float r, float g, float b) {
            return fromLinear(new float[]{r, g, b});
        }

        public float[] fromLinear(float[] v) {
            v[0] = (float) this.mClampedOetf.applyAsDouble((double) v[0]);
            v[1] = (float) this.mClampedOetf.applyAsDouble((double) v[1]);
            v[2] = (float) this.mClampedOetf.applyAsDouble((double) v[2]);
            return v;
        }

        public float[] toXyz(float[] v) {
            v[0] = (float) this.mClampedEotf.applyAsDouble((double) v[0]);
            v[1] = (float) this.mClampedEotf.applyAsDouble((double) v[1]);
            v[2] = (float) this.mClampedEotf.applyAsDouble((double) v[2]);
            return ColorSpace.mul3x3Float3(this.mTransform, v);
        }

        public float[] fromXyz(float[] v) {
            ColorSpace.mul3x3Float3(this.mInverseTransform, v);
            v[0] = (float) this.mClampedOetf.applyAsDouble((double) v[0]);
            v[1] = (float) this.mClampedOetf.applyAsDouble((double) v[1]);
            v[2] = (float) this.mClampedOetf.applyAsDouble((double) v[2]);
            return v;
        }

        private double clamp(double x) {
            float f = this.mMin;
            if (x >= ((double) f)) {
                f = this.mMax;
                if (x <= ((double) f)) {
                    return x;
                }
            }
            return (double) f;
        }

        /* JADX WARNING: Missing block: B:34:0x006c, code skipped:
            return false;
     */
        public boolean equals(java.lang.Object r6) {
            /*
            r5 = this;
            r0 = 1;
            if (r5 != r6) goto L_0x0004;
        L_0x0003:
            return r0;
        L_0x0004:
            r1 = 0;
            if (r6 == 0) goto L_0x006c;
        L_0x0007:
            r2 = r5.getClass();
            r3 = r6.getClass();
            if (r2 == r3) goto L_0x0012;
        L_0x0011:
            goto L_0x006c;
        L_0x0012:
            r2 = super.equals(r6);
            if (r2 != 0) goto L_0x0019;
        L_0x0018:
            return r1;
        L_0x0019:
            r2 = r6;
            r2 = (android.graphics.ColorSpace.Rgb) r2;
            r3 = r2.mMin;
            r4 = r5.mMin;
            r3 = java.lang.Float.compare(r3, r4);
            if (r3 == 0) goto L_0x0027;
        L_0x0026:
            return r1;
        L_0x0027:
            r3 = r2.mMax;
            r4 = r5.mMax;
            r3 = java.lang.Float.compare(r3, r4);
            if (r3 == 0) goto L_0x0032;
        L_0x0031:
            return r1;
        L_0x0032:
            r3 = r5.mWhitePoint;
            r4 = r2.mWhitePoint;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 != 0) goto L_0x003d;
        L_0x003c:
            return r1;
        L_0x003d:
            r3 = r5.mPrimaries;
            r4 = r2.mPrimaries;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 != 0) goto L_0x0048;
        L_0x0047:
            return r1;
        L_0x0048:
            r3 = r5.mTransferParameters;
            if (r3 == 0) goto L_0x0053;
        L_0x004c:
            r0 = r2.mTransferParameters;
            r0 = r3.equals(r0);
            return r0;
        L_0x0053:
            r3 = r2.mTransferParameters;
            if (r3 != 0) goto L_0x0058;
        L_0x0057:
            return r0;
        L_0x0058:
            r0 = r5.mOetf;
            r3 = r2.mOetf;
            r0 = r0.equals(r3);
            if (r0 != 0) goto L_0x0063;
        L_0x0062:
            return r1;
        L_0x0063:
            r0 = r5.mEotf;
            r1 = r2.mEotf;
            r0 = r0.equals(r1);
            return r0;
        L_0x006c:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.graphics.ColorSpace$Rgb.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int hashCode = ((((super.hashCode() * 31) + Arrays.hashCode(this.mWhitePoint)) * 31) + Arrays.hashCode(this.mPrimaries)) * 31;
            float f = this.mMin;
            int i = 0;
            int floatToIntBits = (hashCode + (f != 0.0f ? Float.floatToIntBits(f) : 0)) * 31;
            f = this.mMax;
            hashCode = (floatToIntBits + (f != 0.0f ? Float.floatToIntBits(f) : 0)) * 31;
            TransferParameters transferParameters = this.mTransferParameters;
            if (transferParameters != null) {
                i = transferParameters.hashCode();
            }
            hashCode += i;
            if (this.mTransferParameters == null) {
                return (((hashCode * 31) + this.mOetf.hashCode()) * 31) + this.mEotf.hashCode();
            }
            return hashCode;
        }

        private static boolean isSrgb(float[] primaries, float[] whitePoint, DoubleUnaryOperator OETF, DoubleUnaryOperator EOTF, float min, float max, int id) {
            if (id == 0) {
                return true;
            }
            if (!ColorSpace.compare(primaries, ColorSpace.SRGB_PRIMARIES) || !ColorSpace.compare(whitePoint, ILLUMINANT_D65) || min != 0.0f || max != 1.0f) {
                return false;
            }
            Rgb srgb = (Rgb) ColorSpace.get(Named.SRGB);
            double x = 0.0d;
            while (x <= 1.0d) {
                if (!compare(x, OETF, srgb.mOetf) || !compare(x, EOTF, srgb.mEotf)) {
                    return false;
                }
                x += 0.00392156862745098d;
            }
            return true;
        }

        private static boolean compare(double point, DoubleUnaryOperator a, DoubleUnaryOperator b) {
            return Math.abs(a.applyAsDouble(point) - b.applyAsDouble(point)) <= 0.001d;
        }

        private static boolean isWideGamut(float[] primaries, float min, float max) {
            return (area(primaries) / area(ColorSpace.NTSC_1953_PRIMARIES) > 0.9f && contains(primaries, ColorSpace.SRGB_PRIMARIES)) || (min < 0.0f && max > 1.0f);
        }

        private static float area(float[] primaries) {
            float Rx = primaries[0];
            float Ry = primaries[1];
            float Gx = primaries[2];
            float Gy = primaries[3];
            float Bx = primaries[4];
            float By = primaries[5];
            float r = 0.5f * ((((((Rx * Gy) + (Ry * Bx)) + (Gx * By)) - (Gy * Bx)) - (Ry * Gx)) - (Rx * By));
            return r < 0.0f ? -r : r;
        }

        private static float cross(float ax, float ay, float bx, float by) {
            return (ax * by) - (ay * bx);
        }

        /* JADX WARNING: Missing block: B:14:0x00bd, code skipped:
            return false;
     */
        /* JADX WARNING: Missing block: B:15:0x00be, code skipped:
            return false;
     */
        private static boolean contains(float[] r13, float[] r14) {
            /*
            r0 = 6;
            r0 = new float[r0];
            r1 = 0;
            r2 = r13[r1];
            r3 = r14[r1];
            r2 = r2 - r3;
            r0[r1] = r2;
            r2 = 1;
            r3 = r13[r2];
            r4 = r14[r2];
            r3 = r3 - r4;
            r0[r2] = r3;
            r3 = 2;
            r4 = r13[r3];
            r5 = r14[r3];
            r4 = r4 - r5;
            r0[r3] = r4;
            r4 = 3;
            r5 = r13[r4];
            r6 = r14[r4];
            r5 = r5 - r6;
            r0[r4] = r5;
            r5 = 4;
            r6 = r13[r5];
            r7 = r14[r5];
            r6 = r6 - r7;
            r0[r5] = r6;
            r6 = 5;
            r7 = r13[r6];
            r8 = r14[r6];
            r7 = r7 - r8;
            r0[r6] = r7;
            r7 = r0[r1];
            r8 = r0[r2];
            r9 = r14[r1];
            r10 = r14[r5];
            r9 = r9 - r10;
            r10 = r14[r2];
            r11 = r14[r6];
            r10 = r10 - r11;
            r7 = cross(r7, r8, r9, r10);
            r8 = 0;
            r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
            if (r7 < 0) goto L_0x00be;
        L_0x004a:
            r7 = r14[r1];
            r9 = r14[r3];
            r7 = r7 - r9;
            r9 = r14[r2];
            r10 = r14[r4];
            r9 = r9 - r10;
            r10 = r0[r1];
            r11 = r0[r2];
            r7 = cross(r7, r9, r10, r11);
            r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
            if (r7 >= 0) goto L_0x0061;
        L_0x0060:
            goto L_0x00be;
        L_0x0061:
            r7 = r0[r3];
            r9 = r0[r4];
            r10 = r14[r3];
            r11 = r14[r1];
            r10 = r10 - r11;
            r11 = r14[r4];
            r12 = r14[r2];
            r11 = r11 - r12;
            r7 = cross(r7, r9, r10, r11);
            r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
            if (r7 < 0) goto L_0x00bd;
        L_0x0077:
            r7 = r14[r3];
            r9 = r14[r5];
            r7 = r7 - r9;
            r9 = r14[r4];
            r10 = r14[r6];
            r9 = r9 - r10;
            r10 = r0[r3];
            r11 = r0[r4];
            r7 = cross(r7, r9, r10, r11);
            r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
            if (r7 >= 0) goto L_0x008e;
        L_0x008d:
            goto L_0x00bd;
        L_0x008e:
            r7 = r0[r5];
            r9 = r0[r6];
            r10 = r14[r5];
            r3 = r14[r3];
            r10 = r10 - r3;
            r3 = r14[r6];
            r4 = r14[r4];
            r3 = r3 - r4;
            r3 = cross(r7, r9, r10, r3);
            r3 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1));
            if (r3 < 0) goto L_0x00bc;
        L_0x00a4:
            r3 = r14[r5];
            r4 = r14[r1];
            r3 = r3 - r4;
            r4 = r14[r6];
            r7 = r14[r2];
            r4 = r4 - r7;
            r5 = r0[r5];
            r6 = r0[r6];
            r3 = cross(r3, r4, r5, r6);
            r3 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1));
            if (r3 >= 0) goto L_0x00bb;
        L_0x00ba:
            goto L_0x00bc;
        L_0x00bb:
            return r2;
        L_0x00bc:
            return r1;
        L_0x00bd:
            return r1;
        L_0x00be:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.graphics.ColorSpace$Rgb.contains(float[], float[]):boolean");
        }

        private static float[] computePrimaries(float[] toXYZ) {
            float[] r = ColorSpace.mul3x3Float3(toXYZ, new float[]{1.0f, 0.0f, 0.0f});
            float[] g = ColorSpace.mul3x3Float3(toXYZ, new float[]{0.0f, 1.0f, 0.0f});
            float[] b = ColorSpace.mul3x3Float3(toXYZ, new float[]{0.0f, 0.0f, 1.0f});
            float rSum = (r[0] + r[1]) + r[2];
            float gSum = (g[0] + g[1]) + g[2];
            float bSum = (b[0] + b[1]) + b[2];
            return new float[]{r[0] / rSum, r[1] / rSum, g[0] / gSum, g[1] / gSum, b[0] / bSum, b[1] / bSum};
        }

        private static float[] computeWhitePoint(float[] toXYZ) {
            float[] w = ColorSpace.mul3x3Float3(toXYZ, new float[]{1.0f, 1.0f, 1.0f});
            float sum = (w[0] + w[1]) + w[2];
            return new float[]{w[0] / sum, w[1] / sum};
        }

        private static float[] xyPrimaries(float[] primaries) {
            float[] xyPrimaries = new float[6];
            if (primaries.length == 9) {
                float sum = (primaries[0] + primaries[1]) + primaries[2];
                xyPrimaries[0] = primaries[0] / sum;
                xyPrimaries[1] = primaries[1] / sum;
                float sum2 = (primaries[3] + primaries[4]) + primaries[5];
                xyPrimaries[2] = primaries[3] / sum2;
                xyPrimaries[3] = primaries[4] / sum2;
                sum = (primaries[6] + primaries[7]) + primaries[8];
                xyPrimaries[4] = primaries[6] / sum;
                xyPrimaries[5] = primaries[7] / sum;
            } else {
                System.arraycopy(primaries, 0, xyPrimaries, 0, 6);
            }
            return xyPrimaries;
        }

        private static float[] xyWhitePoint(float[] whitePoint) {
            float[] xyWhitePoint = new float[2];
            if (whitePoint.length == 3) {
                float sum = (whitePoint[0] + whitePoint[1]) + whitePoint[2];
                xyWhitePoint[0] = whitePoint[0] / sum;
                xyWhitePoint[1] = whitePoint[1] / sum;
            } else {
                System.arraycopy(whitePoint, 0, xyWhitePoint, 0, 2);
            }
            return xyWhitePoint;
        }

        private static float[] computeXYZMatrix(float[] primaries, float[] whitePoint) {
            float Rx = primaries[0];
            float Ry = primaries[1];
            float Gx = primaries[2];
            float Gy = primaries[3];
            float Bx = primaries[4];
            float By = primaries[5];
            float Wx = whitePoint[0];
            float Wy = whitePoint[1];
            float oneRxRy = (1.0f - Rx) / Ry;
            float oneGxGy = (1.0f - Gx) / Gy;
            float RxRy = Rx / Ry;
            float GxGy = Gx / Gy;
            float BxBy = Bx / By;
            float WxWy = Wx / Wy;
            float BY = (((((1.0f - Wx) / Wy) - oneRxRy) * (GxGy - RxRy)) - ((WxWy - RxRy) * (oneGxGy - oneRxRy))) / (((((1.0f - Bx) / By) - oneRxRy) * (GxGy - RxRy)) - ((BxBy - RxRy) * (oneGxGy - oneRxRy)));
            float GY = ((WxWy - RxRy) - ((BxBy - RxRy) * BY)) / (GxGy - RxRy);
            float RYRy = ((1.0f - GY) - BY) / Ry;
            float GYGy = GY / Gy;
            float BYBy = BY / By;
            return new float[]{RYRy * Rx, (1.0f - GY) - BY, ((1.0f - Rx) - Ry) * RYRy, GYGy * Gx, GY, ((1.0f - Gx) - Gy) * GYGy, BYBy * Bx, BY, ((1.0f - Bx) - By) * BYBy};
        }
    }

    private static final class Xyz extends ColorSpace {
        private Xyz(String name, int id) {
            super(name, Model.XYZ, id);
        }

        public boolean isWideGamut() {
            return true;
        }

        public float getMinValue(int component) {
            return -2.0f;
        }

        public float getMaxValue(int component) {
            return 2.0f;
        }

        public float[] toXyz(float[] v) {
            v[0] = clamp(v[0]);
            v[1] = clamp(v[1]);
            v[2] = clamp(v[2]);
            return v;
        }

        public float[] fromXyz(float[] v) {
            v[0] = clamp(v[0]);
            v[1] = clamp(v[1]);
            v[2] = clamp(v[2]);
            return v;
        }

        private static float clamp(float x) {
            if (x < -2.0f) {
                return -2.0f;
            }
            return x > 2.0f ? 2.0f : x;
        }
    }

    public abstract float[] fromXyz(float[] fArr);

    public abstract float getMaxValue(int i);

    public abstract float getMinValue(int i);

    public abstract boolean isWideGamut();

    public abstract float[] toXyz(float[] fArr);

    static {
        sNamedColorSpaces[Named.SRGB.ordinal()] = new Rgb("sRGB IEC61966-2.1", SRGB_PRIMARIES, ILLUMINANT_D65, SRGB_TRANSFER_PARAMETERS, Named.SRGB.ordinal());
        sNamedColorSpaces[Named.LINEAR_SRGB.ordinal()] = new Rgb("sRGB IEC61966-2.1 (Linear)", SRGB_PRIMARIES, ILLUMINANT_D65, 1.0d, 0.0f, 1.0f, Named.LINEAR_SRGB.ordinal());
        sNamedColorSpaces[Named.EXTENDED_SRGB.ordinal()] = new Rgb("scRGB-nl IEC 61966-2-2:2003", SRGB_PRIMARIES, ILLUMINANT_D65, null, -$$Lambda$ColorSpace$BNp-1CyCzsQzfE-Ads9uc4rJDfw.INSTANCE, -$$Lambda$ColorSpace$S2rlqJvkXGTpUF6mZhvkElds8JE.INSTANCE, -0.799f, 2.399f, SRGB_TRANSFER_PARAMETERS, Named.EXTENDED_SRGB.ordinal());
        sNamedColorSpaces[Named.LINEAR_EXTENDED_SRGB.ordinal()] = new Rgb("scRGB IEC 61966-2-2:2003", SRGB_PRIMARIES, ILLUMINANT_D65, 1.0d, -0.5f, 7.499f, Named.LINEAR_EXTENDED_SRGB.ordinal());
        sNamedColorSpaces[Named.BT709.ordinal()] = new Rgb("Rec. ITU-R BT.709-5", new float[]{0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f}, ILLUMINANT_D65, new TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.BT709.ordinal());
        sNamedColorSpaces[Named.BT2020.ordinal()] = new Rgb("Rec. ITU-R BT.2020-1", new float[]{0.708f, 0.292f, 0.17f, 0.797f, 0.131f, 0.046f}, ILLUMINANT_D65, new TransferParameters(0.9096697898662786d, 0.09033021013372146d, 0.2222222222222222d, 0.08145d, 2.2222222222222223d), Named.BT2020.ordinal());
        sNamedColorSpaces[Named.DCI_P3.ordinal()] = new Rgb("SMPTE RP 431-2-2007 DCI (P3)", new float[]{0.68f, 0.32f, 0.265f, 0.69f, 0.15f, 0.06f}, new float[]{0.314f, 0.351f}, 2.6d, 0.0f, 1.0f, Named.DCI_P3.ordinal());
        sNamedColorSpaces[Named.DISPLAY_P3.ordinal()] = new Rgb("Display P3", new float[]{0.68f, 0.32f, 0.265f, 0.69f, 0.15f, 0.06f}, ILLUMINANT_D65, SRGB_TRANSFER_PARAMETERS, Named.DISPLAY_P3.ordinal());
        sNamedColorSpaces[Named.NTSC_1953.ordinal()] = new Rgb("NTSC (1953)", NTSC_1953_PRIMARIES, ILLUMINANT_C, new TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.NTSC_1953.ordinal());
        sNamedColorSpaces[Named.SMPTE_C.ordinal()] = new Rgb("SMPTE-C RGB", new float[]{0.63f, 0.34f, 0.31f, 0.595f, 0.155f, 0.07f}, ILLUMINANT_D65, new TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.SMPTE_C.ordinal());
        sNamedColorSpaces[Named.ADOBE_RGB.ordinal()] = new Rgb("Adobe RGB (1998)", new float[]{0.64f, 0.33f, 0.21f, 0.71f, 0.15f, 0.06f}, ILLUMINANT_D65, 2.2d, 0.0f, 1.0f, Named.ADOBE_RGB.ordinal());
        sNamedColorSpaces[Named.PRO_PHOTO_RGB.ordinal()] = new Rgb("ROMM RGB ISO 22028-2:2013", new float[]{0.7347f, 0.2653f, 0.1596f, 0.8404f, 0.0366f, 1.0E-4f}, ILLUMINANT_D50, new TransferParameters(1.0d, 0.0d, 0.0625d, 0.031248d, 1.8d), Named.PRO_PHOTO_RGB.ordinal());
        sNamedColorSpaces[Named.ACES.ordinal()] = new Rgb("SMPTE ST 2065-1:2012 ACES", new float[]{0.7347f, 0.2653f, 0.0f, 1.0f, 1.0E-4f, -0.077f}, ILLUMINANT_D60, 1.0d, -65504.0f, 65504.0f, Named.ACES.ordinal());
        sNamedColorSpaces[Named.ACESCG.ordinal()] = new Rgb("Academy S-2014-004 ACEScg", new float[]{0.713f, 0.293f, 0.165f, 0.83f, 0.128f, 0.044f}, ILLUMINANT_D60, 1.0d, -65504.0f, 65504.0f, Named.ACESCG.ordinal());
        sNamedColorSpaces[Named.CIE_XYZ.ordinal()] = new Xyz("Generic XYZ", Named.CIE_XYZ.ordinal());
        sNamedColorSpaces[Named.CIE_LAB.ordinal()] = new Lab("Generic L*a*b*", Named.CIE_LAB.ordinal());
    }

    private ColorSpace(String name, Model model, int id) {
        if (name == null || name.length() < 1) {
            throw new IllegalArgumentException("The name of a color space cannot be null and must contain at least 1 character");
        } else if (model == null) {
            throw new IllegalArgumentException("A color space must have a model");
        } else if (id < -1 || id > 63) {
            throw new IllegalArgumentException("The id must be between -1 and 63");
        } else {
            this.mName = name;
            this.mModel = model;
            this.mId = id;
        }
    }

    public String getName() {
        return this.mName;
    }

    public int getId() {
        return this.mId;
    }

    public Model getModel() {
        return this.mModel;
    }

    public int getComponentCount() {
        return this.mModel.getComponentCount();
    }

    public boolean isSrgb() {
        return false;
    }

    public float[] toXyz(float r, float g, float b) {
        return toXyz(new float[]{r, g, b});
    }

    public float[] fromXyz(float x, float y, float z) {
        float[] xyz = new float[this.mModel.getComponentCount()];
        xyz[0] = x;
        xyz[1] = y;
        xyz[2] = z;
        return fromXyz(xyz);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mName);
        stringBuilder.append(" (id=");
        stringBuilder.append(this.mId);
        stringBuilder.append(", model=");
        stringBuilder.append(this.mModel);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColorSpace that = (ColorSpace) o;
        if (this.mId != that.mId || !this.mName.equals(that.mName)) {
            return false;
        }
        if (this.mModel != that.mModel) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (((this.mName.hashCode() * 31) + this.mModel.hashCode()) * 31) + this.mId;
    }

    public static Connector connect(ColorSpace source, ColorSpace destination) {
        return connect(source, destination, RenderIntent.PERCEPTUAL);
    }

    public static Connector connect(ColorSpace source, ColorSpace destination, RenderIntent intent) {
        if (source.equals(destination)) {
            return Connector.identity(source);
        }
        if (source.getModel() == Model.RGB && destination.getModel() == Model.RGB) {
            return new Rgb((Rgb) source, (Rgb) destination, intent);
        }
        return new Connector(source, destination, intent);
    }

    public static Connector connect(ColorSpace source) {
        return connect(source, RenderIntent.PERCEPTUAL);
    }

    public static Connector connect(ColorSpace source, RenderIntent intent) {
        if (source.isSrgb()) {
            return Connector.identity(source);
        }
        if (source.getModel() == Model.RGB) {
            return new Rgb((Rgb) source, (Rgb) get(Named.SRGB), intent);
        }
        return new Connector(source, get(Named.SRGB), intent);
    }

    public static ColorSpace adapt(ColorSpace colorSpace, float[] whitePoint) {
        return adapt(colorSpace, whitePoint, Adaptation.BRADFORD);
    }

    public static ColorSpace adapt(ColorSpace colorSpace, float[] whitePoint, Adaptation adaptation) {
        if (colorSpace.getModel() != Model.RGB) {
            return colorSpace;
        }
        Rgb rgb = (Rgb) colorSpace;
        if (compare(rgb.mWhitePoint, whitePoint)) {
            return colorSpace;
        }
        return new Rgb(rgb, mul3x3(chromaticAdaptation(adaptation.mTransform, xyYToXyz(rgb.getWhitePoint()), whitePoint.length == 3 ? Arrays.copyOf(whitePoint, 3) : xyYToXyz(whitePoint)), rgb.mTransform), whitePoint, null);
    }

    private static float[] adaptToIlluminantD50(float[] origWhitePoint, float[] origTransform) {
        float[] desired = ILLUMINANT_D50;
        if (compare(origWhitePoint, desired)) {
            return origTransform;
        }
        return mul3x3(chromaticAdaptation(Adaptation.BRADFORD.mTransform, xyYToXyz(origWhitePoint), xyYToXyz(desired)), origTransform);
    }

    static ColorSpace get(int index) {
        if (index >= 0 && index < Named.values().length) {
            return sNamedColorSpaces[index];
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid ID, must be in the range [0..");
        stringBuilder.append(Named.values().length);
        stringBuilder.append(")");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static ColorSpace get(Named name) {
        return sNamedColorSpaces[name.ordinal()];
    }

    public static ColorSpace match(float[] toXYZD50, TransferParameters function) {
        for (ColorSpace colorSpace : sNamedColorSpaces) {
            if (colorSpace.getModel() == Model.RGB) {
                Rgb rgb = (Rgb) adapt(colorSpace, ILLUMINANT_D50_XYZ);
                if (compare(toXYZD50, rgb.mTransform) && compare(function, rgb.mTransferParameters)) {
                    return colorSpace;
                }
            }
        }
        return null;
    }

    public static Renderer createRenderer() {
        return new Renderer();
    }

    private static double rcpResponse(double x, double a, double b, double c, double d, double g) {
        return x >= d * c ? (Math.pow(x, 1.0d / g) - b) / a : x / c;
    }

    private static double response(double x, double a, double b, double c, double d, double g) {
        return x >= d ? Math.pow((a * x) + b, g) : c * x;
    }

    private static double rcpResponse(double x, double a, double b, double c, double d, double e, double f, double g) {
        return x >= d * c ? (Math.pow(x - e, 1.0d / g) - b) / a : (x - f) / c;
    }

    private static double response(double x, double a, double b, double c, double d, double e, double f, double g) {
        if (x >= d) {
            return Math.pow((a * x) + b, g) + e;
        }
        double d2 = g;
        return (c * x) + f;
    }

    private static double absRcpResponse(double x, double a, double b, double c, double d, double g) {
        double d2 = x;
        return Math.copySign(rcpResponse(d2 < 0.0d ? -d2 : d2, a, b, c, d, g), d2);
    }

    private static double absResponse(double x, double a, double b, double c, double d, double g) {
        double d2 = x;
        return Math.copySign(response(d2 < 0.0d ? -d2 : d2, a, b, c, d, g), d2);
    }

    private static boolean compare(TransferParameters a, TransferParameters b) {
        boolean z = true;
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null || Math.abs(a.a - b.a) >= 0.001d || Math.abs(a.b - b.b) >= 0.001d || Math.abs(a.c - b.c) >= 0.001d || Math.abs(a.d - b.d) >= 0.002d || Math.abs(a.e - b.e) >= 0.001d || Math.abs(a.f - b.f) >= 0.001d || Math.abs(a.g - b.g) >= 0.001d) {
            z = false;
        }
        return z;
    }

    private static boolean compare(float[] a, float[] b) {
        if (a == b) {
            return true;
        }
        int i = 0;
        while (i < a.length) {
            if (Float.compare(a[i], b[i]) != 0 && Math.abs(a[i] - b[i]) > 0.001f) {
                return false;
            }
            i++;
        }
        return true;
    }

    private static float[] inverse3x3(float[] m) {
        float[] fArr = m;
        float a = fArr[0];
        float b = fArr[3];
        float c = fArr[6];
        float d = fArr[1];
        float e = fArr[4];
        float f = fArr[7];
        float g = fArr[2];
        float h = fArr[5];
        float i = fArr[8];
        float A = (e * i) - (f * h);
        float B = (f * g) - (d * i);
        float C = (d * h) - (e * g);
        float det = ((a * A) + (b * B)) + (c * C);
        float[] inverted = new float[fArr.length];
        inverted[0] = A / det;
        inverted[1] = B / det;
        inverted[2] = C / det;
        inverted[3] = ((c * h) - (b * i)) / det;
        inverted[4] = ((a * i) - (c * g)) / det;
        inverted[5] = ((b * g) - (a * h)) / det;
        inverted[6] = ((b * f) - (c * e)) / det;
        inverted[7] = ((c * d) - (a * f)) / det;
        inverted[8] = ((a * e) - (b * d)) / det;
        return inverted;
    }

    public static float[] mul3x3(float[] lhs, float[] rhs) {
        return new float[]{((lhs[0] * rhs[0]) + (lhs[3] * rhs[1])) + (lhs[6] * rhs[2]), ((lhs[1] * rhs[0]) + (lhs[4] * rhs[1])) + (lhs[7] * rhs[2]), ((lhs[2] * rhs[0]) + (lhs[5] * rhs[1])) + (lhs[8] * rhs[2]), ((lhs[0] * rhs[3]) + (lhs[3] * rhs[4])) + (lhs[6] * rhs[5]), ((lhs[1] * rhs[3]) + (lhs[4] * rhs[4])) + (lhs[7] * rhs[5]), ((lhs[2] * rhs[3]) + (lhs[5] * rhs[4])) + (lhs[8] * rhs[5]), ((lhs[0] * rhs[6]) + (lhs[3] * rhs[7])) + (lhs[6] * rhs[8]), ((lhs[1] * rhs[6]) + (lhs[4] * rhs[7])) + (lhs[7] * rhs[8]), ((lhs[2] * rhs[6]) + (lhs[5] * rhs[7])) + (lhs[8] * rhs[8])};
    }

    private static float[] mul3x3Float3(float[] lhs, float[] rhs) {
        float r0 = rhs[0];
        float r1 = rhs[1];
        float r2 = rhs[2];
        rhs[0] = ((lhs[0] * r0) + (lhs[3] * r1)) + (lhs[6] * r2);
        rhs[1] = ((lhs[1] * r0) + (lhs[4] * r1)) + (lhs[7] * r2);
        rhs[2] = ((lhs[2] * r0) + (lhs[5] * r1)) + (lhs[8] * r2);
        return rhs;
    }

    private static float[] mul3x3Diag(float[] lhs, float[] rhs) {
        return new float[]{lhs[0] * rhs[0], lhs[1] * rhs[1], lhs[2] * rhs[2], lhs[0] * rhs[3], lhs[1] * rhs[4], lhs[2] * rhs[5], lhs[0] * rhs[6], lhs[1] * rhs[7], lhs[2] * rhs[8]};
    }

    private static float[] xyYToXyz(float[] xyY) {
        return new float[]{xyY[0] / xyY[1], 1.0f, ((1.0f - xyY[0]) - xyY[1]) / xyY[1]};
    }

    private static void xyYToUv(float[] xyY) {
        for (int i = 0; i < xyY.length; i += 2) {
            float x = xyY[i];
            float y = xyY[i + 1];
            float d = ((-2.0f * x) + (12.0f * y)) + 3.0f;
            float v = (9.0f * y) / d;
            xyY[i] = (4.0f * x) / d;
            xyY[i + 1] = v;
        }
    }

    private static float[] chromaticAdaptation(float[] matrix, float[] srcWhitePoint, float[] dstWhitePoint) {
        float[] srcLMS = mul3x3Float3(matrix, srcWhitePoint);
        float[] dstLMS = mul3x3Float3(matrix, dstWhitePoint);
        return mul3x3(inverse3x3(matrix), mul3x3Diag(new float[]{dstLMS[0] / srcLMS[0], dstLMS[1] / srcLMS[1], dstLMS[2] / srcLMS[2]}, matrix));
    }

    public static float[] cctToXyz(int cct) {
        if (cct >= 1) {
            float f;
            float f2;
            float icct = 1000.0f / ((float) cct);
            float icct2 = icct * icct;
            if (((float) cct) <= 4000.0f) {
                f = (((0.8776956f * icct) + 0.17991f) - (0.2343589f * icct2)) - ((0.2661239f * icct2) * icct);
            } else {
                f = (((0.2226347f * icct) + 0.24039f) + (2.1070378f * icct2)) - ((3.025847f * icct2) * icct);
            }
            float x = f;
            f = x * x;
            if (((float) cct) <= 2222.0f) {
                f2 = (((2.1855583f * x) - 22.118805f) - (1.3481102f * f)) - ((1.1063814f * f) * x);
            } else if (((float) cct) <= 4000.0f) {
                f2 = (((2.09137f * x) - 26.561451f) - (1.3741859f * f)) - ((0.9549476f * f) * x);
            } else {
                f2 = (((3.7511299f * x) - 12.159526f) - (5.873387f * f)) + ((3.081758f * f) * x);
            }
            float y = f2;
            return xyYToXyz(new float[]{x, y});
        }
        throw new IllegalArgumentException("Temperature must be greater than 0");
    }

    public static float[] cctToIlluminantdXyz(int cct) {
        if (cct >= 1) {
            float f;
            float icct = 1.0f / ((float) cct);
            float icct2 = icct * icct;
            if (((float) cct) <= 7000.0f) {
                f = (((99.11f * icct) + 0.244063f) + (2967800.0f * icct2)) - ((4.6070001E9f * icct2) * icct);
            } else {
                f = (((247.48f * icct) + 0.23704f) + (1901800.0f * icct2)) - ((2.0064E9f * icct2) * icct);
            }
            float x = f;
            f = (((-3.0f * x) * x) + (2.87f * x)) - 0.275f;
            return xyYToXyz(new float[]{x, f});
        }
        throw new IllegalArgumentException("Temperature must be greater than 0");
    }

    public static float[] chromaticAdaptation(Adaptation adaptation, float[] srcWhitePoint, float[] dstWhitePoint) {
        float[] srcXyz = srcWhitePoint.length == 3 ? Arrays.copyOf(srcWhitePoint, 3) : xyYToXyz(srcWhitePoint);
        float[] dstXyz = dstWhitePoint.length == 3 ? Arrays.copyOf(dstWhitePoint, 3) : xyYToXyz(dstWhitePoint);
        if (compare(srcXyz, dstXyz)) {
            return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        }
        return chromaticAdaptation(adaptation.mTransform, srcXyz, dstXyz);
    }

    /* Access modifiers changed, original: 0000 */
    public long getNativeInstance() {
        throw new IllegalArgumentException("colorSpace must be an RGB color space");
    }
}
