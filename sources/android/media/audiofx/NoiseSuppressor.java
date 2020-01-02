package android.media.audiofx;

import android.util.Log;

public class NoiseSuppressor extends AudioEffect {
    private static final String TAG = "NoiseSuppressor";

    public static boolean isAvailable() {
        return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS);
    }

    public static NoiseSuppressor create(int audioSession) {
        String str = TAG;
        try {
            return new NoiseSuppressor(audioSession);
        } catch (IllegalArgumentException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("not implemented on this device ");
            stringBuilder.append(null);
            Log.w(str, stringBuilder.toString());
            return null;
        } catch (UnsupportedOperationException e2) {
            Log.w(str, "not enough resources");
            return null;
        } catch (RuntimeException e3) {
            Log.w(str, "not enough memory");
            return null;
        }
    }

    private NoiseSuppressor(int audioSession) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        super(EFFECT_TYPE_NS, EFFECT_TYPE_NULL, 0, audioSession);
    }
}
