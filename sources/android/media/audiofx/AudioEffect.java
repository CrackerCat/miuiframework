package android.media.audiofx;

import android.annotation.UnsupportedAppUsage;
import android.app.ActivityThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.UUID;

public class AudioEffect {
    public static final String ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION = "android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION";
    public static final String ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL = "android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL";
    public static final String ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION = "android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION";
    public static final int ALREADY_EXISTS = -2;
    public static final int CONTENT_TYPE_GAME = 2;
    public static final int CONTENT_TYPE_MOVIE = 1;
    public static final int CONTENT_TYPE_MUSIC = 0;
    public static final int CONTENT_TYPE_VOICE = 3;
    public static final String EFFECT_AUXILIARY = "Auxiliary";
    public static final String EFFECT_INSERT = "Insert";
    public static final String EFFECT_PRE_PROCESSING = "Pre Processing";
    public static final UUID EFFECT_TYPE_AEC = UUID.fromString("7b491460-8d4d-11e0-bd61-0002a5d5c51b");
    public static final UUID EFFECT_TYPE_AGC = UUID.fromString("0a8abfe0-654c-11e0-ba26-0002a5d5c51b");
    public static final UUID EFFECT_TYPE_BASS_BOOST = UUID.fromString("0634f220-ddd4-11db-a0fc-0002a5d5c51b");
    public static final UUID EFFECT_TYPE_DYNAMICS_PROCESSING = UUID.fromString("7261676f-6d75-7369-6364-28e2fd3ac39e");
    public static final UUID EFFECT_TYPE_ENV_REVERB = UUID.fromString("c2e5d5f0-94bd-4763-9cac-4e234d06839e");
    public static final UUID EFFECT_TYPE_EQUALIZER = UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b");
    public static final UUID EFFECT_TYPE_LOUDNESS_ENHANCER = UUID.fromString("fe3199be-aed0-413f-87bb-11260eb63cf1");
    public static final UUID EFFECT_TYPE_NS = UUID.fromString("58b4b260-8e06-11e0-aa8e-0002a5d5c51b");
    public static final UUID EFFECT_TYPE_NULL = UUID.fromString("ec7178ec-e5e1-4432-a3f4-4657e6795210");
    public static final UUID EFFECT_TYPE_PRESET_REVERB = UUID.fromString("47382d60-ddd8-11db-bf3a-0002a5d5c51b");
    public static final UUID EFFECT_TYPE_VIRTUALIZER = UUID.fromString("37cc2c00-dddd-11db-8577-0002a5d5c51b");
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -4;
    public static final int ERROR_DEAD_OBJECT = -7;
    public static final int ERROR_INVALID_OPERATION = -5;
    public static final int ERROR_NO_INIT = -3;
    public static final int ERROR_NO_MEMORY = -6;
    public static final String EXTRA_AUDIO_SESSION = "android.media.extra.AUDIO_SESSION";
    public static final String EXTRA_CONTENT_TYPE = "android.media.extra.CONTENT_TYPE";
    public static final String EXTRA_PACKAGE_NAME = "android.media.extra.PACKAGE_NAME";
    public static final int NATIVE_EVENT_CONTROL_STATUS = 0;
    public static final int NATIVE_EVENT_ENABLED_STATUS = 1;
    public static final int NATIVE_EVENT_PARAMETER_CHANGED = 2;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_UNINITIALIZED = 0;
    public static final int SUCCESS = 0;
    private static final String TAG = "AudioEffect-JAVA";
    private OnControlStatusChangeListener mControlChangeStatusListener = null;
    private Descriptor mDescriptor;
    private OnEnableStatusChangeListener mEnableStatusChangeListener = null;
    private int mId;
    private long mJniData;
    public final Object mListenerLock = new Object();
    private long mNativeAudioEffect;
    public NativeEventHandler mNativeEventHandler = null;
    private OnParameterChangeListener mParameterChangeListener = null;
    private int mState = 0;
    private final Object mStateLock = new Object();

    public static class Descriptor {
        public String connectMode;
        public String implementor;
        public String name;
        public UUID type;
        public UUID uuid;

        public Descriptor(String type, String uuid, String connectMode, String name, String implementor) {
            this.type = UUID.fromString(type);
            this.uuid = UUID.fromString(uuid);
            this.connectMode = connectMode;
            this.name = name;
            this.implementor = implementor;
        }

        public Descriptor(Parcel in) {
            this.type = UUID.fromString(in.readString());
            this.uuid = UUID.fromString(in.readString());
            this.connectMode = in.readString();
            this.name = in.readString();
            this.implementor = in.readString();
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.type, this.uuid, this.connectMode, this.name, this.implementor});
        }

        public void writeToParcel(Parcel dest) {
            dest.writeString(this.type.toString());
            dest.writeString(this.uuid.toString());
            dest.writeString(this.connectMode);
            dest.writeString(this.name);
            dest.writeString(this.implementor);
        }

        public boolean equals(Object o) {
            boolean z = true;
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof Descriptor)) {
                return false;
            }
            Descriptor that = (Descriptor) o;
            if (!(this.type.equals(that.type) && this.uuid.equals(that.uuid) && this.connectMode.equals(that.connectMode) && this.name.equals(that.name) && this.implementor.equals(that.implementor))) {
                z = false;
            }
            return z;
        }
    }

    private class NativeEventHandler extends Handler {
        private AudioEffect mAudioEffect;

        public NativeEventHandler(AudioEffect ae, Looper looper) {
            super(looper);
            this.mAudioEffect = ae;
        }

        public void handleMessage(Message msg) {
            if (this.mAudioEffect != null) {
                int i = msg.what;
                boolean z = true;
                AudioEffect audioEffect;
                if (i == 0) {
                    OnControlStatusChangeListener controlStatusChangeListener;
                    synchronized (AudioEffect.this.mListenerLock) {
                        controlStatusChangeListener = this.mAudioEffect.mControlChangeStatusListener;
                    }
                    if (controlStatusChangeListener != null) {
                        audioEffect = this.mAudioEffect;
                        if (msg.arg1 == 0) {
                            z = false;
                        }
                        controlStatusChangeListener.onControlStatusChange(audioEffect, z);
                    }
                } else if (i == 1) {
                    OnEnableStatusChangeListener enableStatusChangeListener;
                    synchronized (AudioEffect.this.mListenerLock) {
                        enableStatusChangeListener = this.mAudioEffect.mEnableStatusChangeListener;
                    }
                    if (enableStatusChangeListener != null) {
                        audioEffect = this.mAudioEffect;
                        if (msg.arg1 == 0) {
                            z = false;
                        }
                        enableStatusChangeListener.onEnableStatusChange(audioEffect, z);
                    }
                } else if (i != 2) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("handleMessage() Unknown event type: ");
                    stringBuilder.append(msg.what);
                    Log.e(AudioEffect.TAG, stringBuilder.toString());
                } else {
                    OnParameterChangeListener parameterChangeListener;
                    synchronized (AudioEffect.this.mListenerLock) {
                        parameterChangeListener = this.mAudioEffect.mParameterChangeListener;
                    }
                    if (parameterChangeListener != null) {
                        int vOffset = msg.arg1;
                        byte[] p = msg.obj;
                        int status = AudioEffect.byteArrayToInt(p, 0);
                        int psize = AudioEffect.byteArrayToInt(p, 4);
                        int vsize = AudioEffect.byteArrayToInt(p, 8);
                        byte[] param = new byte[psize];
                        byte[] value = new byte[vsize];
                        System.arraycopy(p, 12, param, 0, psize);
                        System.arraycopy(p, vOffset, value, 0, vsize);
                        parameterChangeListener.onParameterChange(this.mAudioEffect, status, param, value);
                    }
                }
            }
        }
    }

    public interface OnControlStatusChangeListener {
        void onControlStatusChange(AudioEffect audioEffect, boolean z);
    }

    public interface OnEnableStatusChangeListener {
        void onEnableStatusChange(AudioEffect audioEffect, boolean z);
    }

    public interface OnParameterChangeListener {
        void onParameterChange(AudioEffect audioEffect, int i, byte[] bArr, byte[] bArr2);
    }

    private final native int native_command(int i, int i2, byte[] bArr, int i3, byte[] bArr2);

    private final native void native_finalize();

    private final native boolean native_getEnabled();

    private final native int native_getParameter(int i, byte[] bArr, int i2, byte[] bArr2);

    private final native boolean native_hasControl();

    private static final native void native_init();

    private static native Object[] native_query_effects();

    private static native Object[] native_query_pre_processing(int i);

    private final native void native_release();

    private final native int native_setEnabled(boolean z);

    private final native int native_setParameter(int i, byte[] bArr, int i2, byte[] bArr2);

    private final native int native_setup(Object obj, String str, String str2, int i, int i2, int[] iArr, Object[] objArr, String str3);

    static {
        System.loadLibrary("audioeffect_jni");
        native_init();
    }

    @UnsupportedAppUsage
    public AudioEffect(UUID type, UUID uuid, int priority, int audioSession) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        UUID uuid2 = type;
        int[] id = new int[1];
        Descriptor[] desc = new Descriptor[1];
        int initResult = native_setup(new WeakReference(this), type.toString(), uuid.toString(), priority, audioSession, id, desc, ActivityThread.currentOpPackageName());
        if (initResult == 0 || initResult == -2) {
            this.mId = id[0];
            this.mDescriptor = desc[0];
            synchronized (this.mStateLock) {
                this.mState = 1;
            }
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Error code ");
        stringBuilder.append(initResult);
        stringBuilder.append(" when initializing AudioEffect.");
        Log.e(TAG, stringBuilder.toString());
        StringBuilder stringBuilder2;
        if (initResult == -5) {
            throw new UnsupportedOperationException("Effect library not loaded");
        } else if (initResult != -4) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Cannot initialize effect engine for type: ");
            stringBuilder2.append(uuid2);
            stringBuilder2.append(" Error: ");
            stringBuilder2.append(initResult);
            throw new RuntimeException(stringBuilder2.toString());
        } else {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Effect type: ");
            stringBuilder2.append(uuid2);
            stringBuilder2.append(" not supported.");
            throw new IllegalArgumentException(stringBuilder2.toString());
        }
    }

    public void release() {
        synchronized (this.mStateLock) {
            native_release();
            this.mState = 0;
        }
    }

    /* Access modifiers changed, original: protected */
    public void finalize() {
        native_finalize();
    }

    public Descriptor getDescriptor() throws IllegalStateException {
        checkState("getDescriptor()");
        return this.mDescriptor;
    }

    public static Descriptor[] queryEffects() {
        return (Descriptor[]) native_query_effects();
    }

    public static Descriptor[] queryPreProcessings(int audioSession) {
        return (Descriptor[]) native_query_pre_processing(audioSession);
    }

    public static boolean isEffectTypeAvailable(UUID type) {
        Descriptor[] desc = queryEffects();
        if (desc == null) {
            return false;
        }
        for (Descriptor descriptor : desc) {
            if (descriptor.type.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public int setEnabled(boolean enabled) throws IllegalStateException {
        checkState("setEnabled()");
        return native_setEnabled(enabled);
    }

    public int setParameter(byte[] param, byte[] value) throws IllegalStateException {
        checkState("setParameter()");
        return native_setParameter(param.length, param, value.length, value);
    }

    public int setParameter(int param, int value) throws IllegalStateException {
        return setParameter(intToByteArray(param), intToByteArray(value));
    }

    public int setParameter(int param, short value) throws IllegalStateException {
        return setParameter(intToByteArray(param), shortToByteArray(value));
    }

    public int setParameter(int param, byte[] value) throws IllegalStateException {
        return setParameter(intToByteArray(param), value);
    }

    public int setParameter(int[] param, int[] value) throws IllegalStateException {
        if (param.length > 2 || value.length > 2) {
            return -4;
        }
        byte[] p2;
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        p2 = intToByteArray(value[0]);
        if (value.length > 1) {
            byte[] v2 = intToByteArray(value[1]);
            p2 = concatArrays(p2, v2);
        }
        return setParameter(p, p2);
    }

    @UnsupportedAppUsage
    public int setParameter(int[] param, short[] value) throws IllegalStateException {
        if (param.length > 2 || value.length > 2) {
            return -4;
        }
        byte[] p2;
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        p2 = shortToByteArray(value[0]);
        if (value.length > 1) {
            byte[] v2 = shortToByteArray(value[1]);
            p2 = concatArrays(p2, v2);
        }
        return setParameter(p, p2);
    }

    public int setParameter(int[] param, byte[] value) throws IllegalStateException {
        if (param.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        return setParameter(p, value);
    }

    public int getParameter(byte[] param, byte[] value) throws IllegalStateException {
        checkState("getParameter()");
        return native_getParameter(param.length, param, value.length, value);
    }

    public int getParameter(int param, byte[] value) throws IllegalStateException {
        return getParameter(intToByteArray(param), value);
    }

    public int getParameter(int param, int[] value) throws IllegalStateException {
        if (value.length > 2) {
            return -4;
        }
        int status;
        byte[] v = new byte[(value.length * 4)];
        int status2 = getParameter(intToByteArray(param), v);
        if (status2 == 4 || status2 == 8) {
            value[0] = byteArrayToInt(v);
            if (status2 == 8) {
                value[1] = byteArrayToInt(v, 4);
            }
            status = status2 / 4;
        } else {
            status = -1;
        }
        return status;
    }

    public int getParameter(int param, short[] value) throws IllegalStateException {
        if (value.length > 2) {
            return -4;
        }
        int status;
        byte[] v = new byte[(value.length * 2)];
        int status2 = getParameter(intToByteArray(param), v);
        if (status2 == 2 || status2 == 4) {
            value[0] = byteArrayToShort(v);
            if (status2 == 4) {
                value[1] = byteArrayToShort(v, 2);
            }
            status = status2 / 2;
        } else {
            status = -1;
        }
        return status;
    }

    @UnsupportedAppUsage
    public int getParameter(int[] param, int[] value) throws IllegalStateException {
        if (param.length > 2 || value.length > 2) {
            return -4;
        }
        int status;
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        byte[] v = new byte[(value.length * 4)];
        int status2 = getParameter(p, v);
        if (status2 == 4 || status2 == 8) {
            value[0] = byteArrayToInt(v);
            if (status2 == 8) {
                value[1] = byteArrayToInt(v, 4);
            }
            status = status2 / 4;
        } else {
            status = -1;
        }
        return status;
    }

    public int getParameter(int[] param, short[] value) throws IllegalStateException {
        if (param.length > 2 || value.length > 2) {
            return -4;
        }
        byte[] p2;
        int status;
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        p2 = new byte[(value.length * 2)];
        int status2 = getParameter(p, p2);
        if (status2 == 2 || status2 == 4) {
            value[0] = byteArrayToShort(p2);
            if (status2 == 4) {
                value[1] = byteArrayToShort(p2, 2);
            }
            status = status2 / 2;
        } else {
            status = -1;
        }
        return status;
    }

    @UnsupportedAppUsage
    public int getParameter(int[] param, byte[] value) throws IllegalStateException {
        if (param.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        return getParameter(p, value);
    }

    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    public int command(int cmdCode, byte[] command, byte[] reply) throws IllegalStateException {
        checkState("command()");
        return native_command(cmdCode, command.length, command, reply.length, reply);
    }

    public int getId() throws IllegalStateException {
        checkState("getId()");
        return this.mId;
    }

    public boolean getEnabled() throws IllegalStateException {
        checkState("getEnabled()");
        return native_getEnabled();
    }

    public boolean hasControl() throws IllegalStateException {
        checkState("hasControl()");
        return native_hasControl();
    }

    public void setEnableStatusListener(OnEnableStatusChangeListener listener) {
        synchronized (this.mListenerLock) {
            this.mEnableStatusChangeListener = listener;
        }
        if (listener != null && this.mNativeEventHandler == null) {
            createNativeEventHandler();
        }
    }

    public void setControlStatusListener(OnControlStatusChangeListener listener) {
        synchronized (this.mListenerLock) {
            this.mControlChangeStatusListener = listener;
        }
        if (listener != null && this.mNativeEventHandler == null) {
            createNativeEventHandler();
        }
    }

    public void setParameterListener(OnParameterChangeListener listener) {
        synchronized (this.mListenerLock) {
            this.mParameterChangeListener = listener;
        }
        if (listener != null && this.mNativeEventHandler == null) {
            createNativeEventHandler();
        }
    }

    private void createNativeEventHandler() {
        Looper myLooper = Looper.myLooper();
        Looper looper = myLooper;
        if (myLooper != null) {
            this.mNativeEventHandler = new NativeEventHandler(this, looper);
            return;
        }
        myLooper = Looper.getMainLooper();
        looper = myLooper;
        if (myLooper != null) {
            this.mNativeEventHandler = new NativeEventHandler(this, looper);
        } else {
            this.mNativeEventHandler = null;
        }
    }

    private static void postEventFromNative(Object effect_ref, int what, int arg1, int arg2, Object obj) {
        AudioEffect effect = (AudioEffect) ((WeakReference) effect_ref).get();
        if (effect != null) {
            Message m = effect.mNativeEventHandler;
            if (m != null) {
                effect.mNativeEventHandler.sendMessage(m.obtainMessage(what, arg1, arg2, obj));
            }
        }
    }

    @UnsupportedAppUsage
    public void checkState(String methodName) throws IllegalStateException {
        synchronized (this.mStateLock) {
            if (this.mState == 1) {
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(methodName);
                stringBuilder.append(" called on uninitialized AudioEffect.");
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
    }

    public void checkStatus(int status) {
        if (!isError(status)) {
            return;
        }
        if (status == -5) {
            throw new UnsupportedOperationException("AudioEffect: invalid parameter operation");
        } else if (status != -4) {
            throw new RuntimeException("AudioEffect: set/get parameter error");
        } else {
            throw new IllegalArgumentException("AudioEffect: bad parameter value");
        }
    }

    public static boolean isError(int status) {
        return status < 0;
    }

    public static int byteArrayToInt(byte[] valueBuf) {
        return byteArrayToInt(valueBuf, 0);
    }

    public static int byteArrayToInt(byte[] valueBuf, int offset) {
        ByteBuffer converter = ByteBuffer.wrap(valueBuf);
        converter.order(ByteOrder.nativeOrder());
        return converter.getInt(offset);
    }

    public static byte[] intToByteArray(int value) {
        ByteBuffer converter = ByteBuffer.allocate(4);
        converter.order(ByteOrder.nativeOrder());
        converter.putInt(value);
        return converter.array();
    }

    public static short byteArrayToShort(byte[] valueBuf) {
        return byteArrayToShort(valueBuf, 0);
    }

    public static short byteArrayToShort(byte[] valueBuf, int offset) {
        ByteBuffer converter = ByteBuffer.wrap(valueBuf);
        converter.order(ByteOrder.nativeOrder());
        return converter.getShort(offset);
    }

    public static byte[] shortToByteArray(short value) {
        ByteBuffer converter = ByteBuffer.allocate(2);
        converter.order(ByteOrder.nativeOrder());
        converter.putShort(value);
        return converter.array();
    }

    public static float byteArrayToFloat(byte[] valueBuf) {
        return byteArrayToFloat(valueBuf, 0);
    }

    public static float byteArrayToFloat(byte[] valueBuf, int offset) {
        ByteBuffer converter = ByteBuffer.wrap(valueBuf);
        converter.order(ByteOrder.nativeOrder());
        return converter.getFloat(offset);
    }

    public static byte[] floatToByteArray(float value) {
        ByteBuffer converter = ByteBuffer.allocate(4);
        converter.order(ByteOrder.nativeOrder());
        converter.putFloat(value);
        return converter.array();
    }

    public static byte[] concatArrays(byte[]... arrays) {
        int len = 0;
        for (byte[] a : arrays) {
            len += a.length;
        }
        byte[] b = new byte[len];
        int offs = 0;
        for (byte[] a2 : arrays) {
            System.arraycopy(a2, 0, b, offs, a2.length);
            offs += a2.length;
        }
        return b;
    }
}
