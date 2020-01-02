package android.gesture;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.android.internal.R;
import java.util.ArrayList;

public class GestureOverlayView extends FrameLayout {
    private static final boolean DITHER_FLAG = true;
    private static final int FADE_ANIMATION_RATE = 16;
    private static final boolean GESTURE_RENDERING_ANTIALIAS = true;
    public static final int GESTURE_STROKE_TYPE_MULTIPLE = 1;
    public static final int GESTURE_STROKE_TYPE_SINGLE = 0;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    private int mCertainGestureColor;
    private int mCurrentColor;
    private Gesture mCurrentGesture;
    private float mCurveEndX;
    private float mCurveEndY;
    private long mFadeDuration;
    private boolean mFadeEnabled;
    private long mFadeOffset;
    private float mFadingAlpha;
    private boolean mFadingHasStarted;
    private final FadeOutRunnable mFadingOut;
    private long mFadingStart;
    private final Paint mGesturePaint;
    private float mGestureStrokeAngleThreshold;
    private float mGestureStrokeLengthThreshold;
    private float mGestureStrokeSquarenessTreshold;
    private int mGestureStrokeType;
    private float mGestureStrokeWidth;
    private boolean mGestureVisible;
    private boolean mHandleGestureActions;
    private boolean mInterceptEvents;
    private final AccelerateDecelerateInterpolator mInterpolator;
    private final Rect mInvalidRect;
    private int mInvalidateExtraBorder;
    private boolean mIsFadingOut;
    private boolean mIsGesturing;
    private boolean mIsListeningForGestures;
    private final ArrayList<OnGestureListener> mOnGestureListeners;
    private final ArrayList<OnGesturePerformedListener> mOnGesturePerformedListeners;
    private final ArrayList<OnGesturingListener> mOnGesturingListeners;
    private int mOrientation;
    private final Path mPath;
    private boolean mPreviousWasGesturing;
    private boolean mResetGesture;
    private final ArrayList<GesturePoint> mStrokeBuffer;
    private float mTotalLength;
    private int mUncertainGestureColor;
    private float mX;
    private float mY;

    private class FadeOutRunnable implements Runnable {
        boolean fireActionPerformed;
        boolean resetMultipleStrokes;

        private FadeOutRunnable() {
        }

        public void run() {
            if (GestureOverlayView.this.mIsFadingOut) {
                long duration = AnimationUtils.currentAnimationTimeMillis() - GestureOverlayView.this.mFadingStart;
                if (duration > GestureOverlayView.this.mFadeDuration) {
                    if (this.fireActionPerformed) {
                        GestureOverlayView.this.fireOnGesturePerformed();
                    }
                    GestureOverlayView.this.mPreviousWasGesturing = false;
                    GestureOverlayView.this.mIsFadingOut = false;
                    GestureOverlayView.this.mFadingHasStarted = false;
                    GestureOverlayView.this.mPath.rewind();
                    GestureOverlayView.this.mCurrentGesture = null;
                    GestureOverlayView.this.setPaintAlpha(255);
                } else {
                    GestureOverlayView.this.mFadingHasStarted = true;
                    float interpolatedTime = Math.max(0.0f, Math.min(1.0f, ((float) duration) / ((float) GestureOverlayView.this.mFadeDuration)));
                    GestureOverlayView gestureOverlayView = GestureOverlayView.this;
                    gestureOverlayView.mFadingAlpha = 1.0f - gestureOverlayView.mInterpolator.getInterpolation(interpolatedTime);
                    gestureOverlayView = GestureOverlayView.this;
                    gestureOverlayView.setPaintAlpha((int) (gestureOverlayView.mFadingAlpha * 255.0f));
                    GestureOverlayView.this.postDelayed(this, 16);
                }
            } else if (this.resetMultipleStrokes) {
                GestureOverlayView.this.mResetGesture = true;
            } else {
                GestureOverlayView.this.fireOnGesturePerformed();
                GestureOverlayView.this.mFadingHasStarted = false;
                GestureOverlayView.this.mPath.rewind();
                GestureOverlayView.this.mCurrentGesture = null;
                GestureOverlayView.this.mPreviousWasGesturing = false;
                GestureOverlayView.this.setPaintAlpha(255);
            }
            GestureOverlayView.this.invalidate();
        }
    }

    public interface OnGestureListener {
        void onGesture(GestureOverlayView gestureOverlayView, MotionEvent motionEvent);

        void onGestureCancelled(GestureOverlayView gestureOverlayView, MotionEvent motionEvent);

        void onGestureEnded(GestureOverlayView gestureOverlayView, MotionEvent motionEvent);

        void onGestureStarted(GestureOverlayView gestureOverlayView, MotionEvent motionEvent);
    }

    public interface OnGesturePerformedListener {
        void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture);
    }

    public interface OnGesturingListener {
        void onGesturingEnded(GestureOverlayView gestureOverlayView);

        void onGesturingStarted(GestureOverlayView gestureOverlayView);
    }

    public GestureOverlayView(Context context) {
        super(context);
        this.mGesturePaint = new Paint();
        this.mFadeDuration = 150;
        this.mFadeOffset = 420;
        this.mFadeEnabled = true;
        this.mCertainGestureColor = -256;
        this.mUncertainGestureColor = 1224736512;
        this.mGestureStrokeWidth = 12.0f;
        this.mInvalidateExtraBorder = 10;
        this.mGestureStrokeType = 0;
        this.mGestureStrokeLengthThreshold = 50.0f;
        this.mGestureStrokeSquarenessTreshold = 0.275f;
        this.mGestureStrokeAngleThreshold = 40.0f;
        this.mOrientation = 1;
        this.mInvalidRect = new Rect();
        this.mPath = new Path();
        this.mGestureVisible = true;
        this.mIsGesturing = false;
        this.mPreviousWasGesturing = false;
        this.mInterceptEvents = true;
        this.mStrokeBuffer = new ArrayList(100);
        this.mOnGestureListeners = new ArrayList();
        this.mOnGesturePerformedListeners = new ArrayList();
        this.mOnGesturingListeners = new ArrayList();
        this.mIsFadingOut = false;
        this.mFadingAlpha = 1.0f;
        this.mInterpolator = new AccelerateDecelerateInterpolator();
        this.mFadingOut = new FadeOutRunnable();
        init();
    }

    public GestureOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.gestureOverlayViewStyle);
    }

    public GestureOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GestureOverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mGesturePaint = new Paint();
        this.mFadeDuration = 150;
        this.mFadeOffset = 420;
        this.mFadeEnabled = true;
        this.mCertainGestureColor = -256;
        this.mUncertainGestureColor = 1224736512;
        this.mGestureStrokeWidth = 12.0f;
        this.mInvalidateExtraBorder = 10;
        this.mGestureStrokeType = 0;
        this.mGestureStrokeLengthThreshold = 50.0f;
        this.mGestureStrokeSquarenessTreshold = 0.275f;
        this.mGestureStrokeAngleThreshold = 40.0f;
        this.mOrientation = 1;
        this.mInvalidRect = new Rect();
        this.mPath = new Path();
        this.mGestureVisible = true;
        this.mIsGesturing = false;
        this.mPreviousWasGesturing = false;
        this.mInterceptEvents = true;
        this.mStrokeBuffer = new ArrayList(100);
        this.mOnGestureListeners = new ArrayList();
        this.mOnGesturePerformedListeners = new ArrayList();
        this.mOnGesturingListeners = new ArrayList();
        this.mIsFadingOut = false;
        this.mFadingAlpha = 1.0f;
        this.mInterpolator = new AccelerateDecelerateInterpolator();
        this.mFadingOut = new FadeOutRunnable();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GestureOverlayView, defStyleAttr, defStyleRes);
        this.mGestureStrokeWidth = a.getFloat(1, this.mGestureStrokeWidth);
        this.mInvalidateExtraBorder = Math.max(1, ((int) this.mGestureStrokeWidth) - 1);
        this.mCertainGestureColor = a.getColor(2, this.mCertainGestureColor);
        this.mUncertainGestureColor = a.getColor(3, this.mUncertainGestureColor);
        this.mFadeDuration = (long) a.getInt(5, (int) this.mFadeDuration);
        this.mFadeOffset = (long) a.getInt(4, (int) this.mFadeOffset);
        this.mGestureStrokeType = a.getInt(6, this.mGestureStrokeType);
        this.mGestureStrokeLengthThreshold = a.getFloat(7, this.mGestureStrokeLengthThreshold);
        this.mGestureStrokeAngleThreshold = a.getFloat(9, this.mGestureStrokeAngleThreshold);
        this.mGestureStrokeSquarenessTreshold = a.getFloat(8, this.mGestureStrokeSquarenessTreshold);
        this.mInterceptEvents = a.getBoolean(10, this.mInterceptEvents);
        this.mFadeEnabled = a.getBoolean(11, this.mFadeEnabled);
        this.mOrientation = a.getInt(0, this.mOrientation);
        a.recycle();
        init();
    }

    private void init() {
        setWillNotDraw(false);
        Paint gesturePaint = this.mGesturePaint;
        gesturePaint.setAntiAlias(true);
        gesturePaint.setColor(this.mCertainGestureColor);
        gesturePaint.setStyle(Style.STROKE);
        gesturePaint.setStrokeJoin(Join.ROUND);
        gesturePaint.setStrokeCap(Cap.ROUND);
        gesturePaint.setStrokeWidth(this.mGestureStrokeWidth);
        gesturePaint.setDither(true);
        this.mCurrentColor = this.mCertainGestureColor;
        setPaintAlpha(255);
    }

    public ArrayList<GesturePoint> getCurrentStroke() {
        return this.mStrokeBuffer;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public void setGestureColor(int color) {
        this.mCertainGestureColor = color;
    }

    public void setUncertainGestureColor(int color) {
        this.mUncertainGestureColor = color;
    }

    public int getUncertainGestureColor() {
        return this.mUncertainGestureColor;
    }

    public int getGestureColor() {
        return this.mCertainGestureColor;
    }

    public float getGestureStrokeWidth() {
        return this.mGestureStrokeWidth;
    }

    public void setGestureStrokeWidth(float gestureStrokeWidth) {
        this.mGestureStrokeWidth = gestureStrokeWidth;
        this.mInvalidateExtraBorder = Math.max(1, ((int) gestureStrokeWidth) - 1);
        this.mGesturePaint.setStrokeWidth(gestureStrokeWidth);
    }

    public int getGestureStrokeType() {
        return this.mGestureStrokeType;
    }

    public void setGestureStrokeType(int gestureStrokeType) {
        this.mGestureStrokeType = gestureStrokeType;
    }

    public float getGestureStrokeLengthThreshold() {
        return this.mGestureStrokeLengthThreshold;
    }

    public void setGestureStrokeLengthThreshold(float gestureStrokeLengthThreshold) {
        this.mGestureStrokeLengthThreshold = gestureStrokeLengthThreshold;
    }

    public float getGestureStrokeSquarenessTreshold() {
        return this.mGestureStrokeSquarenessTreshold;
    }

    public void setGestureStrokeSquarenessTreshold(float gestureStrokeSquarenessTreshold) {
        this.mGestureStrokeSquarenessTreshold = gestureStrokeSquarenessTreshold;
    }

    public float getGestureStrokeAngleThreshold() {
        return this.mGestureStrokeAngleThreshold;
    }

    public void setGestureStrokeAngleThreshold(float gestureStrokeAngleThreshold) {
        this.mGestureStrokeAngleThreshold = gestureStrokeAngleThreshold;
    }

    public boolean isEventsInterceptionEnabled() {
        return this.mInterceptEvents;
    }

    public void setEventsInterceptionEnabled(boolean enabled) {
        this.mInterceptEvents = enabled;
    }

    public boolean isFadeEnabled() {
        return this.mFadeEnabled;
    }

    public void setFadeEnabled(boolean fadeEnabled) {
        this.mFadeEnabled = fadeEnabled;
    }

    public Gesture getGesture() {
        return this.mCurrentGesture;
    }

    public void setGesture(Gesture gesture) {
        if (this.mCurrentGesture != null) {
            clear(false);
        }
        setCurrentColor(this.mCertainGestureColor);
        this.mCurrentGesture = gesture;
        Path path = this.mCurrentGesture.toPath();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        this.mPath.rewind();
        this.mPath.addPath(path, (-bounds.left) + ((((float) getWidth()) - bounds.width()) / 2.0f), (-bounds.top) + ((((float) getHeight()) - bounds.height()) / 2.0f));
        this.mResetGesture = true;
        invalidate();
    }

    public Path getGesturePath() {
        return this.mPath;
    }

    public Path getGesturePath(Path path) {
        path.set(this.mPath);
        return path;
    }

    public boolean isGestureVisible() {
        return this.mGestureVisible;
    }

    public void setGestureVisible(boolean visible) {
        this.mGestureVisible = visible;
    }

    public long getFadeOffset() {
        return this.mFadeOffset;
    }

    public void setFadeOffset(long fadeOffset) {
        this.mFadeOffset = fadeOffset;
    }

    public void addOnGestureListener(OnGestureListener listener) {
        this.mOnGestureListeners.add(listener);
    }

    public void removeOnGestureListener(OnGestureListener listener) {
        this.mOnGestureListeners.remove(listener);
    }

    public void removeAllOnGestureListeners() {
        this.mOnGestureListeners.clear();
    }

    public void addOnGesturePerformedListener(OnGesturePerformedListener listener) {
        this.mOnGesturePerformedListeners.add(listener);
        if (this.mOnGesturePerformedListeners.size() > 0) {
            this.mHandleGestureActions = true;
        }
    }

    public void removeOnGesturePerformedListener(OnGesturePerformedListener listener) {
        this.mOnGesturePerformedListeners.remove(listener);
        if (this.mOnGesturePerformedListeners.size() <= 0) {
            this.mHandleGestureActions = false;
        }
    }

    public void removeAllOnGesturePerformedListeners() {
        this.mOnGesturePerformedListeners.clear();
        this.mHandleGestureActions = false;
    }

    public void addOnGesturingListener(OnGesturingListener listener) {
        this.mOnGesturingListeners.add(listener);
    }

    public void removeOnGesturingListener(OnGesturingListener listener) {
        this.mOnGesturingListeners.remove(listener);
    }

    public void removeAllOnGesturingListeners() {
        this.mOnGesturingListeners.clear();
    }

    public boolean isGesturing() {
        return this.mIsGesturing;
    }

    private void setCurrentColor(int color) {
        this.mCurrentColor = color;
        if (this.mFadingHasStarted) {
            setPaintAlpha((int) (this.mFadingAlpha * 255.0f));
        } else {
            setPaintAlpha(255);
        }
        invalidate();
    }

    public Paint getGesturePaint() {
        return this.mGesturePaint;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mCurrentGesture != null && this.mGestureVisible) {
            canvas.drawPath(this.mPath, this.mGesturePaint);
        }
    }

    private void setPaintAlpha(int alpha) {
        alpha += alpha >> 7;
        int i = this.mCurrentColor;
        this.mGesturePaint.setColor(((i << 8) >>> 8) | ((((i >>> 24) * alpha) >> 8) << 24));
    }

    public void clear(boolean animated) {
        clear(animated, false, true);
    }

    private void clear(boolean animated, boolean fireActionPerformed, boolean immediate) {
        setPaintAlpha(255);
        removeCallbacks(this.mFadingOut);
        this.mResetGesture = false;
        FadeOutRunnable fadeOutRunnable = this.mFadingOut;
        fadeOutRunnable.fireActionPerformed = fireActionPerformed;
        fadeOutRunnable.resetMultipleStrokes = false;
        if (!animated || this.mCurrentGesture == null) {
            this.mFadingAlpha = 1.0f;
            this.mIsFadingOut = false;
            this.mFadingHasStarted = false;
            if (immediate) {
                this.mCurrentGesture = null;
                this.mPath.rewind();
                invalidate();
                return;
            } else if (fireActionPerformed) {
                postDelayed(this.mFadingOut, this.mFadeOffset);
                return;
            } else if (this.mGestureStrokeType == 1) {
                FadeOutRunnable fadeOutRunnable2 = this.mFadingOut;
                fadeOutRunnable2.resetMultipleStrokes = true;
                postDelayed(fadeOutRunnable2, this.mFadeOffset);
                return;
            } else {
                this.mCurrentGesture = null;
                this.mPath.rewind();
                invalidate();
                return;
            }
        }
        this.mFadingAlpha = 1.0f;
        this.mIsFadingOut = true;
        this.mFadingHasStarted = false;
        long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        long j = this.mFadeOffset;
        this.mFadingStart = currentAnimationTimeMillis + j;
        postDelayed(this.mFadingOut, j);
    }

    public void cancelClearAnimation() {
        setPaintAlpha(255);
        this.mIsFadingOut = false;
        this.mFadingHasStarted = false;
        removeCallbacks(this.mFadingOut);
        this.mPath.rewind();
        this.mCurrentGesture = null;
    }

    public void cancelGesture() {
        int i;
        this.mIsListeningForGestures = false;
        this.mCurrentGesture.addStroke(new GestureStroke(this.mStrokeBuffer));
        long now = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
        ArrayList<OnGestureListener> listeners = this.mOnGestureListeners;
        int count = listeners.size();
        for (i = 0; i < count; i++) {
            ((OnGestureListener) listeners.get(i)).onGestureCancelled(this, event);
        }
        event.recycle();
        clear(false);
        this.mIsGesturing = false;
        this.mPreviousWasGesturing = false;
        this.mStrokeBuffer.clear();
        ArrayList<OnGesturingListener> otherListeners = this.mOnGesturingListeners;
        count = otherListeners.size();
        for (i = 0; i < count; i++) {
            ((OnGesturingListener) otherListeners.get(i)).onGesturingEnded(this);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelClearAnimation();
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0025  */
    /* JADX WARNING: Missing block: B:9:0x0017, code skipped:
            if (r3.mPreviousWasGesturing != false) goto L_0x0019;
     */
    public boolean dispatchTouchEvent(android.view.MotionEvent r4) {
        /*
        r3 = this;
        r0 = r3.isEnabled();
        if (r0 == 0) goto L_0x002d;
    L_0x0006:
        r0 = r3.mIsGesturing;
        r1 = 1;
        if (r0 != 0) goto L_0x0019;
    L_0x000b:
        r0 = r3.mCurrentGesture;
        if (r0 == 0) goto L_0x001f;
    L_0x000f:
        r0 = r0.getStrokesCount();
        if (r0 <= 0) goto L_0x001f;
    L_0x0015:
        r0 = r3.mPreviousWasGesturing;
        if (r0 == 0) goto L_0x001f;
    L_0x0019:
        r0 = r3.mInterceptEvents;
        if (r0 == 0) goto L_0x001f;
    L_0x001d:
        r0 = r1;
        goto L_0x0020;
    L_0x001f:
        r0 = 0;
    L_0x0020:
        r3.processEvent(r4);
        if (r0 == 0) goto L_0x0029;
    L_0x0025:
        r2 = 3;
        r4.setAction(r2);
    L_0x0029:
        super.dispatchTouchEvent(r4);
        return r1;
    L_0x002d:
        r0 = super.dispatchTouchEvent(r4);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.gesture.GestureOverlayView.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean processEvent(MotionEvent event) {
        int action = event.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action != 2) {
                    if (action == 3 && this.mIsListeningForGestures) {
                        touchUp(event, true);
                        invalidate();
                        return true;
                    }
                } else if (this.mIsListeningForGestures) {
                    Rect rect = touchMove(event);
                    if (rect != null) {
                        invalidate(rect);
                    }
                    return true;
                }
            } else if (this.mIsListeningForGestures) {
                touchUp(event, false);
                invalidate();
                return true;
            }
            return false;
        }
        touchDown(event);
        invalidate();
        return true;
    }

    private void touchDown(MotionEvent event) {
        this.mIsListeningForGestures = true;
        float x = event.getX();
        float y = event.getY();
        this.mX = x;
        this.mY = y;
        this.mTotalLength = 0.0f;
        this.mIsGesturing = false;
        if (this.mGestureStrokeType == 0 || this.mResetGesture) {
            if (this.mHandleGestureActions) {
                setCurrentColor(this.mUncertainGestureColor);
            }
            this.mResetGesture = false;
            this.mCurrentGesture = null;
            this.mPath.rewind();
        } else {
            Gesture gesture = this.mCurrentGesture;
            if ((gesture == null || gesture.getStrokesCount() == 0) && this.mHandleGestureActions) {
                setCurrentColor(this.mUncertainGestureColor);
            }
        }
        if (this.mFadingHasStarted) {
            cancelClearAnimation();
        } else if (this.mIsFadingOut) {
            setPaintAlpha(255);
            this.mIsFadingOut = false;
            this.mFadingHasStarted = false;
            removeCallbacks(this.mFadingOut);
        }
        if (this.mCurrentGesture == null) {
            this.mCurrentGesture = new Gesture();
        }
        this.mStrokeBuffer.add(new GesturePoint(x, y, event.getEventTime()));
        this.mPath.moveTo(x, y);
        int border = this.mInvalidateExtraBorder;
        this.mInvalidRect.set(((int) x) - border, ((int) y) - border, ((int) x) + border, ((int) y) + border);
        this.mCurveEndX = x;
        this.mCurveEndY = y;
        ArrayList<OnGestureListener> listeners = this.mOnGestureListeners;
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
            ((OnGestureListener) listeners.get(i)).onGestureStarted(this, event);
        }
    }

    private Rect touchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float previousX = this.mX;
        float previousY = this.mY;
        float dx = Math.abs(x - previousX);
        float dy = Math.abs(y - previousY);
        MotionEvent motionEvent;
        if (dx >= 3.0f || dy >= 3.0f) {
            Rect areaToRefresh;
            Rect areaToRefresh2 = this.mInvalidRect;
            int border = this.mInvalidateExtraBorder;
            float f = this.mCurveEndX;
            int i = ((int) f) - border;
            float f2 = this.mCurveEndY;
            areaToRefresh2.set(i, ((int) f2) - border, ((int) f) + border, ((int) f2) + border);
            f = (x + previousX) / 2.0f;
            this.mCurveEndX = f;
            f2 = (y + previousY) / 2.0f;
            this.mCurveEndY = f2;
            float cY = f2;
            this.mPath.quadTo(previousX, previousY, f, cY);
            areaToRefresh2.union(((int) previousX) - border, ((int) previousY) - border, ((int) previousX) + border, ((int) previousY) + border);
            areaToRefresh2.union(((int) f) - border, ((int) cY) - border, ((int) f) + border, ((int) cY) + border);
            this.mX = x;
            this.mY = y;
            this.mStrokeBuffer.add(new GesturePoint(x, y, event.getEventTime()));
            if (!this.mHandleGestureActions || this.mIsGesturing) {
                areaToRefresh = areaToRefresh2;
            } else {
                this.mTotalLength += (float) Math.hypot((double) dx, (double) dy);
                if (this.mTotalLength > this.mGestureStrokeLengthThreshold) {
                    OrientedBoundingBox box = GestureUtils.computeOrientedBoundingBox(this.mStrokeBuffer);
                    float angle = Math.abs(box.orientation);
                    if (angle > 90.0f) {
                        angle = 180.0f - angle;
                    }
                    if (box.squareness > this.mGestureStrokeSquarenessTreshold || (this.mOrientation != 1 ? angle <= this.mGestureStrokeAngleThreshold : angle >= this.mGestureStrokeAngleThreshold)) {
                        this.mIsGesturing = true;
                        setCurrentColor(this.mCertainGestureColor);
                        ArrayList<OnGesturingListener> listeners = this.mOnGesturingListeners;
                        int count = listeners.size();
                        int i2 = 0;
                        while (i2 < count) {
                            areaToRefresh = areaToRefresh2;
                            ((OnGesturingListener) listeners.get(i2)).onGesturingStarted(this);
                            i2++;
                            areaToRefresh2 = areaToRefresh;
                        }
                        areaToRefresh = areaToRefresh2;
                    } else {
                        areaToRefresh = areaToRefresh2;
                    }
                } else {
                    areaToRefresh = areaToRefresh2;
                }
            }
            ArrayList<OnGestureListener> listeners2 = this.mOnGestureListeners;
            int count2 = listeners2.size();
            for (int i3 = 0; i3 < count2; i3++) {
                ((OnGestureListener) listeners2.get(i3)).onGesture(this, event);
            }
            motionEvent = event;
            return areaToRefresh;
        }
        motionEvent = event;
        return null;
    }

    private void touchUp(MotionEvent event, boolean cancel) {
        int count;
        this.mIsListeningForGestures = false;
        Gesture gesture = this.mCurrentGesture;
        if (gesture != null) {
            gesture.addStroke(new GestureStroke(this.mStrokeBuffer));
            if (cancel) {
                cancelGesture(event);
            } else {
                ArrayList<OnGestureListener> listeners = this.mOnGestureListeners;
                count = listeners.size();
                for (int i = 0; i < count; i++) {
                    ((OnGestureListener) listeners.get(i)).onGestureEnded(this, event);
                }
                boolean z = true;
                boolean z2 = this.mHandleGestureActions && this.mFadeEnabled;
                if (!(this.mHandleGestureActions && this.mIsGesturing)) {
                    z = false;
                }
                clear(z2, z, false);
            }
        } else {
            cancelGesture(event);
        }
        this.mStrokeBuffer.clear();
        this.mPreviousWasGesturing = this.mIsGesturing;
        this.mIsGesturing = false;
        ArrayList<OnGesturingListener> listeners2 = this.mOnGesturingListeners;
        int count2 = listeners2.size();
        for (count = 0; count < count2; count++) {
            ((OnGesturingListener) listeners2.get(count)).onGesturingEnded(this);
        }
    }

    private void cancelGesture(MotionEvent event) {
        ArrayList<OnGestureListener> listeners = this.mOnGestureListeners;
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
            ((OnGestureListener) listeners.get(i)).onGestureCancelled(this, event);
        }
        clear(false);
    }

    private void fireOnGesturePerformed() {
        ArrayList<OnGesturePerformedListener> actionListeners = this.mOnGesturePerformedListeners;
        int count = actionListeners.size();
        for (int i = 0; i < count; i++) {
            ((OnGesturePerformedListener) actionListeners.get(i)).onGesturePerformed(this, this.mCurrentGesture);
        }
    }
}
