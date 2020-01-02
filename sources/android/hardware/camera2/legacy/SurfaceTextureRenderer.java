package android.hardware.camera2.legacy;

import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.legacy.LegacyExceptionUtils.BufferQueueAbandonedException;
import android.net.wifi.WifiEnterpriseConfig;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.AnrMonitor;
import android.os.Environment;
import android.os.SystemProperties;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SurfaceTextureRenderer {
    private static final boolean DEBUG = false;
    private static final int EGL_COLOR_BITLENGTH = 8;
    private static final int EGL_RECORDABLE_ANDROID = 12610;
    private static final int FLIP_TYPE_BOTH = 3;
    private static final int FLIP_TYPE_HORIZONTAL = 1;
    private static final int FLIP_TYPE_NONE = 0;
    private static final int FLIP_TYPE_VERTICAL = 2;
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final int GLES_VERSION = 2;
    private static final int GL_MATRIX_SIZE = 16;
    private static final String LEGACY_PERF_PROPERTY = "persist.camera.legacy_perf";
    private static final int PBUFFER_PIXEL_BYTES = 4;
    private static final String TAG = SurfaceTextureRenderer.class.getSimpleName();
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 20;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private static final int VERTEX_POS_SIZE = 3;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private static final int VERTEX_UV_SIZE = 2;
    private static final float[] sBothFlipTriangleVertices = new float[]{-1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] sHorizontalFlipTriangleVertices = new float[]{-1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] sRegularTriangleVertices = new float[]{-1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
    private static final float[] sVerticalFlipTriangleVertices = new float[]{-1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f};
    private FloatBuffer mBothFlipTriangleVertices;
    private EGLConfig mConfigs;
    private List<EGLSurfaceHolder> mConversionSurfaces = new ArrayList();
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private final int mFacing;
    private FloatBuffer mHorizontalFlipTriangleVertices;
    private float[] mMVPMatrix = new float[16];
    private ByteBuffer mPBufferPixels;
    private PerfMeasurement mPerfMeasurer = null;
    private int mProgram;
    private FloatBuffer mRegularTriangleVertices;
    private float[] mSTMatrix = new float[16];
    private volatile SurfaceTexture mSurfaceTexture;
    private List<EGLSurfaceHolder> mSurfaces = new ArrayList();
    private int mTextureID = 0;
    private FloatBuffer mVerticalFlipTriangleVertices;
    private int maPositionHandle;
    private int maTextureHandle;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;

    private class EGLSurfaceHolder {
        EGLSurface eglSurface;
        int height;
        Surface surface;
        int width;

        private EGLSurfaceHolder() {
        }
    }

    public SurfaceTextureRenderer(int facing) {
        this.mFacing = facing;
        this.mRegularTriangleVertices = ByteBuffer.allocateDirect(sRegularTriangleVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mRegularTriangleVertices.put(sRegularTriangleVertices).position(0);
        this.mHorizontalFlipTriangleVertices = ByteBuffer.allocateDirect(sHorizontalFlipTriangleVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mHorizontalFlipTriangleVertices.put(sHorizontalFlipTriangleVertices).position(0);
        this.mVerticalFlipTriangleVertices = ByteBuffer.allocateDirect(sVerticalFlipTriangleVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mVerticalFlipTriangleVertices.put(sVerticalFlipTriangleVertices).position(0);
        this.mBothFlipTriangleVertices = ByteBuffer.allocateDirect(sBothFlipTriangleVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mBothFlipTriangleVertices.put(sBothFlipTriangleVertices).position(0);
        Matrix.setIdentityM(this.mSTMatrix, 0);
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("glCreateShader type=");
        stringBuilder.append(shaderType);
        checkGlError(stringBuilder.toString());
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] != 0) {
            return shader;
        }
        String str = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        String str2 = "Could not compile shader ";
        stringBuilder2.append(str2);
        stringBuilder2.append(shaderType);
        stringBuilder2.append(":");
        Log.e(str, stringBuilder2.toString());
        str = TAG;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        stringBuilder2.append(GLES20.glGetShaderInfoLog(shader));
        Log.e(str, stringBuilder2.toString());
        GLES20.glDeleteShader(shader);
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str2);
        stringBuilder2.append(shaderType);
        throw new IllegalStateException(stringBuilder2.toString());
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        String str = "glAttachShader";
        checkGlError(str);
        GLES20.glAttachShader(program, pixelShader);
        checkGlError(str);
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 1) {
            return program;
        }
        Log.e(TAG, "Could not link program: ");
        Log.e(TAG, GLES20.glGetProgramInfoLog(program));
        GLES20.glDeleteProgram(program);
        throw new IllegalStateException("Could not link program");
    }

    private void drawFrame(SurfaceTexture st, int width, int height, int flipType) throws BufferQueueAbandonedException {
        int i = width;
        int i2 = height;
        int i3 = flipType;
        checkGlError("onDrawFrame start");
        st.getTransformMatrix(this.mSTMatrix);
        Matrix.setIdentityM(this.mMVPMatrix, 0);
        try {
            Size dimens = LegacyCameraDevice.getTextureSize(st);
            float texWidth = (float) dimens.getWidth();
            float texHeight = (float) dimens.getHeight();
            if (texWidth <= 0.0f || texHeight <= 0.0f) {
                throw new IllegalStateException("Illegal intermediate texture with dimension of 0");
            }
            Buffer triangleVertices;
            RectF intermediate = new RectF(0.0f, 0.0f, texWidth, texHeight);
            RectF output = new RectF(0.0f, 0.0f, (float) i, (float) i2);
            android.graphics.Matrix boxingXform = new android.graphics.Matrix();
            boxingXform.setRectToRect(output, intermediate, ScaleToFit.CENTER);
            boxingXform.mapRect(output);
            Matrix.scaleM(this.mMVPMatrix, 0, intermediate.width() / output.width(), intermediate.height() / output.height(), 1.0f);
            GLES20.glViewport(0, 0, i, i2);
            GLES20.glUseProgram(this.mProgram);
            checkGlError("glUseProgram");
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, this.mTextureID);
            if (i3 == 1) {
                triangleVertices = this.mHorizontalFlipTriangleVertices;
            } else if (i3 == 2) {
                triangleVertices = this.mVerticalFlipTriangleVertices;
            } else if (i3 != 3) {
                triangleVertices = this.mRegularTriangleVertices;
            } else {
                triangleVertices = this.mBothFlipTriangleVertices;
            }
            triangleVertices.position(0);
            Buffer buffer = triangleVertices;
            GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 20, buffer);
            checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(this.maPositionHandle);
            checkGlError("glEnableVertexAttribArray maPositionHandle");
            triangleVertices.position(3);
            GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, 20, buffer);
            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(this.maTextureHandle);
            checkGlError("glEnableVertexAttribArray maTextureHandle");
            GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, this.mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(this.muSTMatrixHandle, 1, false, this.mSTMatrix, 0);
            GLES20.glDrawArrays(5, 0, 4);
            checkGlDrawError("glDrawArrays");
        } catch (BufferQueueAbandonedException e) {
            throw new IllegalStateException("Surface abandoned, skipping drawFrame...", e);
        }
    }

    private void initializeGLState() {
        this.mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        int i = this.mProgram;
        if (i != 0) {
            this.maPositionHandle = GLES20.glGetAttribLocation(i, "aPosition");
            checkGlError("glGetAttribLocation aPosition");
            if (this.maPositionHandle != -1) {
                this.maTextureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
                checkGlError("glGetAttribLocation aTextureCoord");
                if (this.maTextureHandle != -1) {
                    this.muMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");
                    checkGlError("glGetUniformLocation uMVPMatrix");
                    if (this.muMVPMatrixHandle != -1) {
                        this.muSTMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uSTMatrix");
                        checkGlError("glGetUniformLocation uSTMatrix");
                        if (this.muSTMatrixHandle != -1) {
                            int[] textures = new int[1];
                            GLES20.glGenTextures(1, textures, 0);
                            this.mTextureID = textures[0];
                            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, this.mTextureID);
                            checkGlError("glBindTexture mTextureID");
                            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10241, 9728.0f);
                            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10240, 9729.0f);
                            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10242, 33071);
                            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10243, 33071);
                            checkGlError("glTexParameter");
                            return;
                        }
                        throw new IllegalStateException("Could not get attrib location for uSTMatrix");
                    }
                    throw new IllegalStateException("Could not get attrib location for uMVPMatrix");
                }
                throw new IllegalStateException("Could not get attrib location for aTextureCoord");
            }
            throw new IllegalStateException("Could not get attrib location for aPosition");
        }
        throw new IllegalStateException("failed creating program");
    }

    private int getTextureId() {
        return this.mTextureID;
    }

    private void clearState() {
        this.mSurfaces.clear();
        for (EGLSurfaceHolder holder : this.mConversionSurfaces) {
            try {
                LegacyCameraDevice.disconnectSurface(holder.surface);
            } catch (BufferQueueAbandonedException e) {
                Log.w(TAG, "Surface abandoned, skipping...", e);
            }
        }
        this.mConversionSurfaces.clear();
        this.mPBufferPixels = null;
        if (this.mSurfaceTexture != null) {
            this.mSurfaceTexture.release();
        }
        this.mSurfaceTexture = null;
    }

    private void configureEGLContext() {
        this.mEGLDisplay = EGL14.eglGetDisplay(0);
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            int[] version = new int[2];
            if (EGL14.eglInitialize(this.mEGLDisplay, version, 0, version, 1)) {
                EGLConfig[] configs = new EGLConfig[1];
                EGLConfig[] eGLConfigArr = configs;
                EGL14.eglChooseConfig(this.mEGLDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12352, 4, 12610, 1, 12339, 5, 12344}, 0, eGLConfigArr, 0, configs.length, new int[1], 0);
                checkEglError("eglCreateContext RGB888+recordable ES2");
                this.mConfigs = configs[0];
                this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT, new int[]{12440, 2, 12344}, 0);
                checkEglError("eglCreateContext");
                if (this.mEGLContext == EGL14.EGL_NO_CONTEXT) {
                    throw new IllegalStateException("No EGLContext could be made");
                }
                return;
            }
            throw new IllegalStateException("Cannot initialize EGL14");
        }
        throw new IllegalStateException("No EGL14 display");
    }

    private void configureEGLOutputSurfaces(Collection<EGLSurfaceHolder> surfaces) {
        if (surfaces == null || surfaces.size() == 0) {
            throw new IllegalStateException("No Surfaces were provided to draw to");
        }
        int[] surfaceAttribs = new int[]{12344};
        for (EGLSurfaceHolder holder : surfaces) {
            holder.eglSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, this.mConfigs, holder.surface, surfaceAttribs, 0);
            checkEglError("eglCreateWindowSurface");
        }
    }

    private void configureEGLPbufferSurfaces(Collection<EGLSurfaceHolder> surfaces) {
        if (surfaces == null || surfaces.size() == 0) {
            throw new IllegalStateException("No Surfaces were provided to draw to");
        }
        int maxLength = 0;
        for (EGLSurfaceHolder holder : surfaces) {
            int length = holder.width * holder.height;
            maxLength = length > maxLength ? length : maxLength;
            holder.eglSurface = EGL14.eglCreatePbufferSurface(this.mEGLDisplay, this.mConfigs, new int[]{12375, holder.width, 12374, holder.height, 12344}, 0);
            checkEglError("eglCreatePbufferSurface");
        }
        this.mPBufferPixels = ByteBuffer.allocateDirect(maxLength * 4).order(ByteOrder.nativeOrder());
    }

    private void releaseEGLContext() {
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            dumpGlTiming();
            List<EGLSurfaceHolder> list = this.mSurfaces;
            if (list != null) {
                for (EGLSurfaceHolder holder : list) {
                    if (holder.eglSurface != null) {
                        EGL14.eglDestroySurface(this.mEGLDisplay, holder.eglSurface);
                    }
                }
            }
            list = this.mConversionSurfaces;
            if (list != null) {
                for (EGLSurfaceHolder holder2 : list) {
                    if (holder2.eglSurface != null) {
                        EGL14.eglDestroySurface(this.mEGLDisplay, holder2.eglSurface);
                    }
                }
            }
            EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(this.mEGLDisplay);
        }
        this.mConfigs = null;
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        clearState();
    }

    private void makeCurrent(EGLSurface surface) throws BufferQueueAbandonedException {
        EGL14.eglMakeCurrent(this.mEGLDisplay, surface, surface, this.mEGLContext);
        checkEglDrawError("makeCurrent");
    }

    private boolean swapBuffers(EGLSurface surface) throws BufferQueueAbandonedException {
        boolean result = EGL14.eglSwapBuffers(this.mEGLDisplay, surface);
        int error = EGL14.eglGetError();
        if (error == 12288) {
            return result;
        }
        if (error == 12299 || error == 12301) {
            throw new BufferQueueAbandonedException();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("swapBuffers: EGL error: 0x");
        stringBuilder.append(Integer.toHexString(error));
        throw new IllegalStateException(stringBuilder.toString());
    }

    private void checkEglDrawError(String msg) throws BufferQueueAbandonedException {
        int eglGetError = EGL14.eglGetError();
        int error = eglGetError;
        if (eglGetError != 12299) {
            eglGetError = EGL14.eglGetError();
            error = eglGetError;
            if (eglGetError != 12288) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(msg);
                stringBuilder.append(": EGL error: 0x");
                stringBuilder.append(Integer.toHexString(error));
                throw new IllegalStateException(stringBuilder.toString());
            }
            return;
        }
        throw new BufferQueueAbandonedException();
    }

    private void checkEglError(String msg) {
        int eglGetError = EGL14.eglGetError();
        int error = eglGetError;
        if (eglGetError != 12288) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(msg);
            stringBuilder.append(": EGL error: 0x");
            stringBuilder.append(Integer.toHexString(error));
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    private void checkGlError(String msg) {
        int glGetError = GLES20.glGetError();
        int error = glGetError;
        if (glGetError != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(msg);
            stringBuilder.append(": GLES20 error: 0x");
            stringBuilder.append(Integer.toHexString(error));
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    private void checkGlDrawError(String msg) throws BufferQueueAbandonedException {
        int error;
        boolean surfaceAbandoned = false;
        boolean glError = false;
        while (true) {
            int glGetError = GLES20.glGetError();
            error = glGetError;
            if (glGetError == 0) {
                break;
            } else if (error == 1285) {
                surfaceAbandoned = true;
            } else {
                glError = true;
            }
        }
        if (glError) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(msg);
            stringBuilder.append(": GLES20 error: 0x");
            stringBuilder.append(Integer.toHexString(error));
            throw new IllegalStateException(stringBuilder.toString());
        } else if (surfaceAbandoned) {
            throw new BufferQueueAbandonedException();
        }
    }

    private void dumpGlTiming() {
        if (this.mPerfMeasurer != null) {
            File legacyStorageDir = new File(Environment.getExternalStorageDirectory(), "CameraLegacy");
            if (legacyStorageDir.exists() || legacyStorageDir.mkdirs()) {
                String str;
                EGLSurfaceHolder surface;
                StringBuilder path = new StringBuilder(legacyStorageDir.getPath());
                path.append(File.separator);
                path.append("durations_");
                Time now = new Time();
                now.setToNow();
                path.append(now.format2445());
                path.append("_S");
                Iterator it = this.mSurfaces.iterator();
                while (true) {
                    str = "_%d_%d";
                    if (!it.hasNext()) {
                        break;
                    }
                    surface = (EGLSurfaceHolder) it.next();
                    path.append(String.format(str, new Object[]{Integer.valueOf(surface.width), Integer.valueOf(surface.height)}));
                }
                path.append("_C");
                for (EGLSurfaceHolder surface2 : this.mConversionSurfaces) {
                    path.append(String.format(str, new Object[]{Integer.valueOf(surface2.width), Integer.valueOf(surface2.height)}));
                }
                path.append(AnrMonitor.TRACES_FILE_TYPE);
                this.mPerfMeasurer.dumpPerformanceData(path.toString());
                return;
            }
            Log.e(TAG, "Failed to create directory for data dump");
        }
    }

    private void setupGlTiming() {
        if (PerfMeasurement.isGlTimingSupported()) {
            Log.d(TAG, "Enabling GL performance measurement");
            this.mPerfMeasurer = new PerfMeasurement();
            return;
        }
        Log.d(TAG, "GL performance measurement not supported on this device");
        this.mPerfMeasurer = null;
    }

    private void beginGlTiming() {
        PerfMeasurement perfMeasurement = this.mPerfMeasurer;
        if (perfMeasurement != null) {
            perfMeasurement.startTimer();
        }
    }

    private void addGlTimestamp(long timestamp) {
        PerfMeasurement perfMeasurement = this.mPerfMeasurer;
        if (perfMeasurement != null) {
            perfMeasurement.addTimestamp(timestamp);
        }
    }

    private void endGlTiming() {
        PerfMeasurement perfMeasurement = this.mPerfMeasurer;
        if (perfMeasurement != null) {
            perfMeasurement.stopTimer();
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.mSurfaceTexture;
    }

    public void configureSurfaces(Collection<Pair<Surface, Size>> surfaces) {
        String str;
        releaseEGLContext();
        if (surfaces == null || surfaces.size() == 0) {
            Log.w(TAG, "No output surfaces configured for GL drawing.");
            return;
        }
        Iterator it = surfaces.iterator();
        while (true) {
            str = "Surface abandoned, skipping configuration... ";
            if (!it.hasNext()) {
                break;
            }
            Pair<Surface, Size> p = (Pair) it.next();
            Surface s = p.first;
            Size surfaceSize = p.second;
            try {
                EGLSurfaceHolder holder = new EGLSurfaceHolder();
                holder.surface = s;
                holder.width = surfaceSize.getWidth();
                holder.height = surfaceSize.getHeight();
                if (LegacyCameraDevice.needsConversion(s)) {
                    this.mConversionSurfaces.add(holder);
                    LegacyCameraDevice.connectSurface(s);
                } else {
                    this.mSurfaces.add(holder);
                }
            } catch (BufferQueueAbandonedException e) {
                Log.w(TAG, str, e);
            }
        }
        configureEGLContext();
        if (this.mSurfaces.size() > 0) {
            configureEGLOutputSurfaces(this.mSurfaces);
        }
        if (this.mConversionSurfaces.size() > 0) {
            configureEGLPbufferSurfaces(this.mConversionSurfaces);
        }
        try {
            EGLSurface eGLSurface;
            if (this.mSurfaces.size() > 0) {
                eGLSurface = ((EGLSurfaceHolder) this.mSurfaces.get(0)).eglSurface;
            } else {
                eGLSurface = ((EGLSurfaceHolder) this.mConversionSurfaces.get(0)).eglSurface;
            }
            makeCurrent(eGLSurface);
        } catch (BufferQueueAbandonedException e2) {
            Log.w(TAG, str, e2);
        }
        initializeGLState();
        this.mSurfaceTexture = new SurfaceTexture(getTextureId());
        if (SystemProperties.getBoolean(LEGACY_PERF_PROPERTY, false)) {
            setupGlTiming();
        }
    }

    public void drawIntoSurfaces(CaptureCollector targetCollector) {
        String str = "Surface abandoned, dropping frame. ";
        List list = this.mSurfaces;
        if (list == null || list.size() == 0) {
            list = this.mConversionSurfaces;
            CaptureCollector captureCollector;
            if (list == null) {
                captureCollector = targetCollector;
            } else if (list.size() == 0) {
                captureCollector = targetCollector;
            }
            return;
        }
        boolean doTiming = targetCollector.hasPendingPreviewCaptures();
        checkGlError("before updateTexImage");
        if (doTiming) {
            beginGlTiming();
        }
        this.mSurfaceTexture.updateTexImage();
        long timestamp = this.mSurfaceTexture.getTimestamp();
        Pair<RequestHolder, Long> captureHolder = targetCollector.previewCaptured(timestamp);
        if (captureHolder == null) {
            if (doTiming) {
                endGlTiming();
            }
            return;
        }
        RequestHolder request = captureHolder.first;
        Collection targetSurfaces = request.getHolderTargets();
        if (doTiming) {
            addGlTimestamp(timestamp);
        }
        List<Long> targetSurfaceIds = new ArrayList();
        try {
            targetSurfaceIds = LegacyCameraDevice.getSurfaceIds(targetSurfaces);
        } catch (BufferQueueAbandonedException e) {
            Log.w(TAG, str, e);
            request.setOutputAbandoned();
        }
        for (EGLSurfaceHolder holder : this.mSurfaces) {
            if (LegacyCameraDevice.containsSurfaceId(holder.surface, targetSurfaceIds)) {
                try {
                    LegacyCameraDevice.setSurfaceDimens(holder.surface, holder.width, holder.height);
                    makeCurrent(holder.eglSurface);
                    LegacyCameraDevice.setNextTimestamp(holder.surface, ((Long) captureHolder.second).longValue());
                    drawFrame(this.mSurfaceTexture, holder.width, holder.height, this.mFacing == 0 ? 1 : 0);
                    swapBuffers(holder.eglSurface);
                } catch (BufferQueueAbandonedException e2) {
                    Log.w(TAG, str, e2);
                    request.setOutputAbandoned();
                }
            }
        }
        for (EGLSurfaceHolder holder2 : this.mConversionSurfaces) {
            long timestamp2;
            if (LegacyCameraDevice.containsSurfaceId(holder2.surface, targetSurfaceIds)) {
                try {
                    makeCurrent(holder2.eglSurface);
                    drawFrame(this.mSurfaceTexture, holder2.width, holder2.height, this.mFacing == 0 ? 3 : 2);
                    this.mPBufferPixels.clear();
                    timestamp2 = timestamp;
                    GLES20.glReadPixels(0, 0, holder2.width, holder2.height, 6408, 5121, this.mPBufferPixels);
                    checkGlError("glReadPixels");
                    try {
                        int format = LegacyCameraDevice.detectSurfaceType(holder2.surface);
                        LegacyCameraDevice.setSurfaceDimens(holder2.surface, holder2.width, holder2.height);
                        LegacyCameraDevice.setNextTimestamp(holder2.surface, ((Long) captureHolder.second).longValue());
                        LegacyCameraDevice.produceFrame(holder2.surface, this.mPBufferPixels.array(), holder2.width, holder2.height, format);
                    } catch (BufferQueueAbandonedException e22) {
                        Log.w(TAG, str, e22);
                        request.setOutputAbandoned();
                    }
                } catch (BufferQueueAbandonedException e222) {
                    timestamp2 = timestamp;
                    throw new IllegalStateException("Surface abandoned, skipping drawFrame...", e222);
                }
            }
            timestamp2 = timestamp;
            timestamp = timestamp2;
        }
        targetCollector.previewProduced();
        if (doTiming) {
            endGlTiming();
        }
    }

    public void cleanupEGLContext() {
        releaseEGLContext();
    }

    public void flush() {
        Log.e(TAG, "Flush not yet implemented.");
    }
}
