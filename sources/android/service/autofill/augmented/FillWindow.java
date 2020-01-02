package android.service.autofill.augmented;

import android.annotation.SystemApi;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.service.autofill.augmented.PresentationParams.Area;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.autofill.IAutofillWindowPresenter.Stub;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.pooled.PooledLambda;
import dalvik.system.CloseGuard;
import java.io.PrintWriter;

@SystemApi
public final class FillWindow implements AutoCloseable {
    private static final String TAG = FillWindow.class.getSimpleName();
    @GuardedBy({"mLock"})
    private Rect mBounds;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    @GuardedBy({"mLock"})
    private boolean mDestroyed;
    @GuardedBy({"mLock"})
    private View mFillView;
    private final FillWindowPresenter mFillWindowPresenter = new FillWindowPresenter();
    private final Object mLock = new Object();
    private AutofillProxy mProxy;
    @GuardedBy({"mLock"})
    private boolean mShowing;
    private final Handler mUiThreadHandler = new Handler(Looper.getMainLooper());
    @GuardedBy({"mLock"})
    private boolean mUpdateCalled;
    @GuardedBy({"mLock"})
    private WindowManager mWm;

    private final class FillWindowPresenter extends Stub {
        private FillWindowPresenter() {
        }

        public void show(LayoutParams p, Rect transitionEpicenter, boolean fitsSystemWindows, int layoutDirection) {
            if (AugmentedAutofillService.sDebug) {
                Log.d(FillWindow.TAG, "FillWindowPresenter.show()");
            }
            FillWindow.this.mUiThreadHandler.sendMessage(PooledLambda.obtainMessage(-$$Lambda$FillWindow$FillWindowPresenter$hdkNZGuYdsvcArvQ2SoMspO1EO8.INSTANCE, FillWindow.this, p));
        }

        public void hide(Rect transitionEpicenter) {
            if (AugmentedAutofillService.sDebug) {
                Log.d(FillWindow.TAG, "FillWindowPresenter.hide()");
            }
            FillWindow.this.mUiThreadHandler.sendMessage(PooledLambda.obtainMessage(-$$Lambda$FillWindow$FillWindowPresenter$EnBAJTZRgK05SBPnOQ9Edaq3VXs.INSTANCE, FillWindow.this));
        }
    }

    public boolean update(Area area, View rootView, long flags) {
        if (AugmentedAutofillService.sDebug) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Updating ");
            stringBuilder.append(area);
            stringBuilder.append(" + with ");
            stringBuilder.append(rootView);
            Log.d(str, stringBuilder.toString());
        }
        Preconditions.checkNotNull(area);
        Preconditions.checkNotNull(area.proxy);
        Preconditions.checkNotNull(rootView);
        PresentationParams smartSuggestion = area.proxy.getSmartSuggestionParams();
        if (smartSuggestion == null) {
            Log.w(TAG, "No SmartSuggestionParams");
            return false;
        } else if (area.getBounds() == null) {
            Log.wtf(TAG, "No Rect on SmartSuggestionParams");
            return false;
        } else {
            synchronized (this.mLock) {
                checkNotDestroyedLocked();
                this.mProxy = area.proxy;
                this.mWm = (WindowManager) rootView.getContext().getSystemService(WindowManager.class);
                this.mFillView = rootView;
                this.mFillView.setOnTouchListener(new -$$Lambda$FillWindow$j5yljDJ4VfFL37B-iWRonGGOKN4(this));
                this.mShowing = false;
                this.mBounds = new Rect(area.getBounds());
                if (AugmentedAutofillService.sDebug) {
                    String str2 = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Created FillWindow: params= ");
                    stringBuilder2.append(smartSuggestion);
                    stringBuilder2.append(" view=");
                    stringBuilder2.append(rootView);
                    Log.d(str2, stringBuilder2.toString());
                }
                this.mUpdateCalled = true;
                this.mDestroyed = false;
                this.mProxy.setFillWindow(this);
            }
            return true;
        }
    }

    public /* synthetic */ boolean lambda$update$0$FillWindow(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4) {
            if (AugmentedAutofillService.sVerbose) {
                Log.v(TAG, "Outside touch detected, hiding the window");
            }
            hide();
        }
        return false;
    }

    /* Access modifiers changed, original: 0000 */
    public void show() {
        if (AugmentedAutofillService.sDebug) {
            Log.d(TAG, "show()");
        }
        synchronized (this.mLock) {
            checkNotDestroyedLocked();
            if (this.mWm == null || this.mFillView == null) {
                throw new IllegalStateException("update() not called yet, or already destroyed()");
            }
            if (this.mProxy != null) {
                try {
                    this.mProxy.requestShowFillUi(this.mBounds.right - this.mBounds.left, this.mBounds.bottom - this.mBounds.top, null, this.mFillWindowPresenter);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error requesting to show fill window", e);
                }
                this.mProxy.report(2);
            }
        }
    }

    private void hide() {
        if (AugmentedAutofillService.sDebug) {
            Log.d(TAG, "hide()");
        }
        synchronized (this.mLock) {
            checkNotDestroyedLocked();
            if (this.mWm == null || this.mFillView == null) {
                throw new IllegalStateException("update() not called yet, or already destroyed()");
            }
            if (this.mProxy != null && this.mShowing) {
                try {
                    this.mProxy.requestHideFillUi();
                } catch (RemoteException e) {
                    Log.w(TAG, "Error requesting to hide fill window", e);
                }
            }
        }
    }

    private void handleShow(LayoutParams p) {
        if (AugmentedAutofillService.sDebug) {
            Log.d(TAG, "handleShow()");
        }
        synchronized (this.mLock) {
            if (!(this.mWm == null || this.mFillView == null)) {
                p.flags |= 262144;
                if (this.mShowing) {
                    this.mWm.updateViewLayout(this.mFillView, p);
                } else {
                    this.mWm.addView(this.mFillView, p);
                    this.mShowing = true;
                }
            }
        }
    }

    private void handleHide() {
        if (AugmentedAutofillService.sDebug) {
            Log.d(TAG, "handleHide()");
        }
        synchronized (this.mLock) {
            if (!(this.mWm == null || this.mFillView == null || !this.mShowing)) {
                this.mWm.removeView(this.mFillView);
                this.mShowing = false;
            }
        }
    }

    public void destroy() {
        if (AugmentedAutofillService.sDebug) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("destroy(): mDestroyed=");
            stringBuilder.append(this.mDestroyed);
            stringBuilder.append(" mShowing=");
            stringBuilder.append(this.mShowing);
            stringBuilder.append(" mFillView=");
            stringBuilder.append(this.mFillView);
            Log.d(str, stringBuilder.toString());
        }
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                return;
            }
            if (this.mUpdateCalled) {
                hide();
                this.mProxy.report(3);
            }
            this.mDestroyed = true;
            this.mCloseGuard.close();
        }
    }

    /* Access modifiers changed, original: protected */
    public void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            destroy();
        } finally {
            super.finalize();
        }
    }

    private void checkNotDestroyedLocked() {
        if (this.mDestroyed) {
            throw new IllegalStateException("already destroyed()");
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        synchronized (this) {
            pw.print(prefix);
            pw.print("destroyed: ");
            pw.println(this.mDestroyed);
            pw.print(prefix);
            pw.print("updateCalled: ");
            pw.println(this.mUpdateCalled);
            if (this.mFillView != null) {
                pw.print(prefix);
                pw.print("fill window: ");
                pw.println(this.mShowing ? "shown" : "hidden");
                pw.print(prefix);
                pw.print("fill view: ");
                pw.println(this.mFillView);
                pw.print(prefix);
                pw.print("mBounds: ");
                pw.println(this.mBounds);
                pw.print(prefix);
                pw.print("mWm: ");
                pw.println(this.mWm);
            }
        }
    }

    public void close() throws Exception {
        destroy();
    }
}
