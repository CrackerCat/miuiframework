package android.inputmethodservice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.WindowManager.BadTokenException;
import android.view.WindowManager.LayoutParams;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.IInputMethodManager.Stub;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SoftInputWindow extends Dialog {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "SoftInputWindow";
    private static final String TAG = "SoftInputWindow";
    private final Rect mBounds = new Rect();
    final Callback mCallback;
    final DispatcherState mDispatcherState;
    final int mGravity;
    final android.view.KeyEvent.Callback mKeyEventCallback;
    final String mName;
    private IInputMethodManager mService;
    final boolean mTakesFocus;
    private int mWindowState = 0;
    final int mWindowType;

    public interface Callback {
        void onBackPressed();
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface SoftInputWindowState {
        public static final int DESTROYED = 4;
        public static final int REJECTED_AT_LEAST_ONCE = 3;
        public static final int SHOWN_AT_LEAST_ONCE = 2;
        public static final int TOKEN_PENDING = 0;
        public static final int TOKEN_SET = 1;
    }

    public void setToken(IBinder token) {
        int i = this.mWindowState;
        if (i == 0) {
            LayoutParams lp = getWindow().getAttributes();
            lp.token = token;
            getWindow().setAttributes(lp);
            updateWindowState(1);
        } else if (i == 1 || i == 2 || i == 3) {
            throw new IllegalStateException("setToken can be called only once");
        } else if (i == 4) {
            Log.i("SoftInputWindow", "Ignoring setToken() because window is already destroyed.");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected state=");
            stringBuilder.append(this.mWindowState);
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    public SoftInputWindow(Context context, String name, int theme, Callback callback, android.view.KeyEvent.Callback keyEventCallback, DispatcherState dispatcherState, int windowType, int gravity, boolean takesFocus) {
        super(context, theme);
        this.mName = name;
        this.mCallback = callback;
        this.mKeyEventCallback = keyEventCallback;
        this.mDispatcherState = dispatcherState;
        this.mWindowType = windowType;
        this.mGravity = gravity;
        this.mTakesFocus = takesFocus;
        initDockWindow();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.mDispatcherState.reset();
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        getWindow().getDecorView().getHitRect(this.mBounds);
        if (ev.isWithinBoundsNoHistory((float) this.mBounds.left, (float) this.mBounds.top, (float) (this.mBounds.right - 1), (float) (this.mBounds.bottom - 1))) {
            return super.dispatchTouchEvent(ev);
        }
        MotionEvent temp = ev.clampNoHistory((float) this.mBounds.left, (float) this.mBounds.top, (float) (this.mBounds.right - 1), (float) (this.mBounds.bottom - 1));
        boolean handled = super.dispatchTouchEvent(temp);
        temp.recycle();
        return handled;
    }

    public void setGravity(int gravity) {
        LayoutParams lp = getWindow().getAttributes();
        lp.gravity = gravity;
        updateWidthHeight(lp);
        getWindow().setAttributes(lp);
    }

    public int getGravity() {
        return getWindow().getAttributes().gravity;
    }

    private void updateWidthHeight(LayoutParams lp) {
        if (lp.gravity == 48 || lp.gravity == 80) {
            lp.width = -1;
            lp.height = -2;
            return;
        }
        lp.width = -2;
        lp.height = -1;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        android.view.KeyEvent.Callback callback = this.mKeyEventCallback;
        if (callback == null || !callback.onKeyDown(keyCode, event)) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        android.view.KeyEvent.Callback callback = this.mKeyEventCallback;
        if (callback == null || !callback.onKeyLongPress(keyCode, event)) {
            return super.onKeyLongPress(keyCode, event);
        }
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        android.view.KeyEvent.Callback callback = this.mKeyEventCallback;
        if (callback == null || !callback.onKeyUp(keyCode, event)) {
            return super.onKeyUp(keyCode, event);
        }
        return true;
    }

    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        android.view.KeyEvent.Callback callback = this.mKeyEventCallback;
        if (callback == null || !callback.onKeyMultiple(keyCode, count, event)) {
            return super.onKeyMultiple(keyCode, count, event);
        }
        return true;
    }

    public void onBackPressed() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void initDockWindow() {
        int windowSetFlags;
        LayoutParams lp = getWindow().getAttributes();
        lp.type = this.mWindowType;
        lp.setTitle(this.mName);
        lp.gravity = this.mGravity;
        updateWidthHeight(lp);
        getWindow().setAttributes(lp);
        int windowModFlags = 266;
        if (this.mTakesFocus) {
            windowSetFlags = 256 | 32;
            windowModFlags = 266 | 32;
        } else {
            windowSetFlags = 256 | 8;
        }
        getWindow().setFlags(windowSetFlags, windowModFlags);
    }

    public final void show() {
        int i = this.mWindowState;
        if (i != 0) {
            String str = "SoftInputWindow";
            if (i == 1 || i == 2) {
                try {
                    super.show();
                    updateWindowState(2);
                } catch (BadTokenException e) {
                    Log.i(str, "Probably the IME window token is already invalidated. show() does nothing.");
                    updateWindowState(3);
                }
                return;
            } else if (i == 3) {
                Log.i(str, "Not trying to call show() because it was already rejected once.");
                return;
            } else if (i == 4) {
                Log.i(str, "Ignoring show() because the window is already destroyed.");
                return;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected state=");
                stringBuilder.append(this.mWindowState);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
        throw new IllegalStateException("Window token is not set yet.");
    }

    /* Access modifiers changed, original: final */
    public final void dismissForDestroyIfNecessary() {
        int i = this.mWindowState;
        if (i == 0 || i == 1) {
            updateWindowState(4);
            return;
        }
        String str = "SoftInputWindow";
        if (i == 2) {
            try {
                getWindow().setWindowAnimations(0);
                dismiss();
            } catch (BadTokenException e) {
                Log.i(str, "Probably the IME window token is already invalidated. No need to dismiss it.");
            }
            updateWindowState(4);
        } else if (i == 3) {
            Log.i(str, "Not trying to dismiss the window because it is most likely unnecessary.");
            updateWindowState(4);
        } else if (i != 4) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected state=");
            stringBuilder.append(this.mWindowState);
            throw new IllegalStateException(stringBuilder.toString());
        } else {
            throw new IllegalStateException("dismissForDestroyIfNecessary can be called only once");
        }
    }

    private void updateWindowState(int newState) {
        this.mWindowState = newState;
    }

    private static String stateToString(int state) {
        if (state == 0) {
            return "TOKEN_PENDING";
        }
        if (state == 1) {
            return "TOKEN_SET";
        }
        if (state == 2) {
            return "SHOWN_AT_LEAST_ONCE";
        }
        if (state == 3) {
            return "REJECTED_AT_LEAST_ONCE";
        }
        if (state == 4) {
            return "DESTROYED";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown state=");
        stringBuilder.append(state);
        throw new IllegalStateException(stringBuilder.toString());
    }

    /* Access modifiers changed, original: 0000 */
    public void showWithTokenCheck() {
        String str = "SoftInputWindow";
        if (this.mService == null) {
            try {
                this.mService = Stub.asInterface(ServiceManager.getServiceOrThrow(Context.INPUT_METHOD_SERVICE));
            } catch (Exception e) {
                Log.w(str, "Fail to get input method service");
            }
        }
        if (this.mService == null || getWindow() == null || getWindow().getAttributes() == null || getWindow().getAttributes().token == null) {
            show();
            return;
        }
        try {
            if (this.mService.isTokenValid(getWindow().getAttributes().token)) {
                show();
            } else {
                Log.w(str, "Token attached by input_method is invalid");
            }
        } catch (RemoteException e2) {
            Log.w(str, "Fail to invoke IMMS");
            show();
        }
    }
}
