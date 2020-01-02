package android.widget;

import android.R;
import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

@Deprecated
public class SlidingDrawer extends ViewGroup {
    private static final int ANIMATION_FRAME_DURATION = 16;
    private static final int COLLAPSED_FULL_CLOSED = -10002;
    private static final int EXPANDED_FULL_OPEN = -10001;
    private static final float MAXIMUM_ACCELERATION = 2000.0f;
    private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;
    private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;
    private static final float MAXIMUM_TAP_VELOCITY = 100.0f;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    private static final int TAP_THRESHOLD = 6;
    private static final int VELOCITY_UNITS = 1000;
    private boolean mAllowSingleTap;
    private boolean mAnimateOnClick;
    private float mAnimatedAcceleration;
    private float mAnimatedVelocity;
    private boolean mAnimating;
    private long mAnimationLastTime;
    private float mAnimationPosition;
    private int mBottomOffset;
    private View mContent;
    private final int mContentId;
    private long mCurrentAnimationTime;
    private boolean mExpanded;
    private final Rect mFrame;
    private View mHandle;
    private int mHandleHeight;
    private final int mHandleId;
    private int mHandleWidth;
    private final Rect mInvalidate;
    private boolean mLocked;
    private final int mMaximumAcceleration;
    private final int mMaximumMajorVelocity;
    private final int mMaximumMinorVelocity;
    private final int mMaximumTapVelocity;
    private OnDrawerCloseListener mOnDrawerCloseListener;
    private OnDrawerOpenListener mOnDrawerOpenListener;
    private OnDrawerScrollListener mOnDrawerScrollListener;
    private final Runnable mSlidingRunnable;
    private final int mTapThreshold;
    @UnsupportedAppUsage
    private int mTopOffset;
    @UnsupportedAppUsage
    private int mTouchDelta;
    @UnsupportedAppUsage
    private boolean mTracking;
    @UnsupportedAppUsage
    private VelocityTracker mVelocityTracker;
    private final int mVelocityUnits;
    private boolean mVertical;

    private class DrawerToggler implements OnClickListener {
        private DrawerToggler() {
        }

        /* synthetic */ DrawerToggler(SlidingDrawer x0, AnonymousClass1 x1) {
            this();
        }

        public void onClick(View v) {
            if (!SlidingDrawer.this.mLocked) {
                if (SlidingDrawer.this.mAnimateOnClick) {
                    SlidingDrawer.this.animateToggle();
                } else {
                    SlidingDrawer.this.toggle();
                }
            }
        }
    }

    public interface OnDrawerCloseListener {
        void onDrawerClosed();
    }

    public interface OnDrawerOpenListener {
        void onDrawerOpened();
    }

    public interface OnDrawerScrollListener {
        void onScrollEnded();

        void onScrollStarted();
    }

    public SlidingDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mFrame = new Rect();
        this.mInvalidate = new Rect();
        this.mSlidingRunnable = new Runnable() {
            public void run() {
                SlidingDrawer.this.doAnimation();
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingDrawer, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, R.styleable.SlidingDrawer, attrs, a, defStyleAttr, defStyleRes);
        this.mVertical = a.getInt(0, 1) == 1;
        this.mBottomOffset = (int) a.getDimension(1, 0.0f);
        this.mTopOffset = (int) a.getDimension(2, 0.0f);
        this.mAllowSingleTap = a.getBoolean(3, true);
        this.mAnimateOnClick = a.getBoolean(6, true);
        int handleId = a.getResourceId(4, 0);
        if (handleId != 0) {
            int contentId = a.getResourceId(5, 0);
            if (contentId == 0) {
                throw new IllegalArgumentException("The content attribute is required and must refer to a valid child.");
            } else if (handleId != contentId) {
                this.mHandleId = handleId;
                this.mContentId = contentId;
                float density = getResources().getDisplayMetrics().density;
                this.mTapThreshold = (int) ((6.0f * density) + 0.5f);
                this.mMaximumTapVelocity = (int) ((100.0f * density) + 0.5f);
                this.mMaximumMinorVelocity = (int) ((MAXIMUM_MINOR_VELOCITY * density) + 0.5f);
                this.mMaximumMajorVelocity = (int) ((200.0f * density) + 0.5f);
                this.mMaximumAcceleration = (int) ((MAXIMUM_ACCELERATION * density) + 0.5f);
                this.mVelocityUnits = (int) ((1000.0f * density) + 0.5f);
                a.recycle();
                setAlwaysDrawnWithCacheEnabled(false);
                return;
            } else {
                throw new IllegalArgumentException("The content and handle attributes must refer to different children.");
            }
        }
        throw new IllegalArgumentException("The handle attribute is required and must refer to a valid child.");
    }

    /* Access modifiers changed, original: protected */
    public void onFinishInflate() {
        this.mHandle = findViewById(this.mHandleId);
        View view = this.mHandle;
        if (view != null) {
            view.setOnClickListener(new DrawerToggler(this, null));
            this.mContent = findViewById(this.mContentId);
            view = this.mContent;
            if (view != null) {
                view.setVisibility(8);
                return;
            }
            throw new IllegalArgumentException("The content attribute is must refer to an existing child.");
        }
        throw new IllegalArgumentException("The handle attribute is must refer to an existing child.");
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == 0 || heightSpecMode == 0) {
            throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
        }
        View handle = this.mHandle;
        measureChild(handle, widthMeasureSpec, heightMeasureSpec);
        if (this.mVertical) {
            this.mContent.measure(MeasureSpec.makeMeasureSpec(widthSpecSize, 1073741824), MeasureSpec.makeMeasureSpec((heightSpecSize - handle.getMeasuredHeight()) - this.mTopOffset, 1073741824));
        } else {
            this.mContent.measure(MeasureSpec.makeMeasureSpec((widthSpecSize - handle.getMeasuredWidth()) - this.mTopOffset, 1073741824), MeasureSpec.makeMeasureSpec(heightSpecSize, 1073741824));
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    /* Access modifiers changed, original: protected */
    public void dispatchDraw(Canvas canvas) {
        long drawingTime = getDrawingTime();
        View handle = this.mHandle;
        boolean isVertical = this.mVertical;
        drawChild(canvas, handle, drawingTime);
        if (this.mTracking || this.mAnimating) {
            Bitmap cache = this.mContent.getDrawingCache();
            float f = 0.0f;
            if (cache == null) {
                canvas.save();
                float left = isVertical ? 0.0f : (float) (handle.getLeft() - this.mTopOffset);
                if (isVertical) {
                    f = (float) (handle.getTop() - this.mTopOffset);
                }
                canvas.translate(left, f);
                drawChild(canvas, this.mContent, drawingTime);
                canvas.restore();
            } else if (isVertical) {
                canvas.drawBitmap(cache, 0.0f, (float) handle.getBottom(), null);
            } else {
                canvas.drawBitmap(cache, (float) handle.getRight(), 0.0f, null);
            }
        } else if (this.mExpanded) {
            drawChild(canvas, this.mContent, drawingTime);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!this.mTracking) {
            int childLeft;
            int childTop;
            int width = r - l;
            int height = b - t;
            View handle = this.mHandle;
            int childWidth = handle.getMeasuredWidth();
            int childHeight = handle.getMeasuredHeight();
            View content = this.mContent;
            if (this.mVertical) {
                childLeft = (width - childWidth) / 2;
                childTop = this.mExpanded ? this.mTopOffset : (height - childHeight) + this.mBottomOffset;
                content.layout(0, this.mTopOffset + childHeight, content.getMeasuredWidth(), (this.mTopOffset + childHeight) + content.getMeasuredHeight());
            } else {
                childLeft = this.mExpanded ? this.mTopOffset : (width - childWidth) + this.mBottomOffset;
                childTop = (height - childHeight) / 2;
                int i = this.mTopOffset;
                content.layout(i + childWidth, 0, (i + childWidth) + content.getMeasuredWidth(), content.getMeasuredHeight());
            }
            handle.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            this.mHandleHeight = handle.getHeight();
            this.mHandleWidth = handle.getWidth();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mLocked) {
            return false;
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        Rect frame = this.mFrame;
        View handle = this.mHandle;
        handle.getHitRect(frame);
        if (!this.mTracking && !frame.contains((int) x, (int) y)) {
            return false;
        }
        if (action == 0) {
            this.mTracking = true;
            handle.setPressed(true);
            prepareContent();
            OnDrawerScrollListener onDrawerScrollListener = this.mOnDrawerScrollListener;
            if (onDrawerScrollListener != null) {
                onDrawerScrollListener.onScrollStarted();
            }
            int top;
            if (this.mVertical) {
                top = this.mHandle.getTop();
                this.mTouchDelta = ((int) y) - top;
                prepareTracking(top);
            } else {
                top = this.mHandle.getLeft();
                this.mTouchDelta = ((int) x) - top;
                prepareTracking(top);
            }
            this.mVelocityTracker.addMovement(event);
        }
        return true;
    }

    /* JADX WARNING: Missing block: B:10:0x001a, code skipped:
            if (r0 != 3) goto L_0x0108;
     */
    public boolean onTouchEvent(android.view.MotionEvent r14) {
        /*
        r13 = this;
        r0 = r13.mLocked;
        r1 = 1;
        if (r0 == 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = r13.mTracking;
        r2 = 0;
        if (r0 == 0) goto L_0x0108;
    L_0x000b:
        r0 = r13.mVelocityTracker;
        r0.addMovement(r14);
        r0 = r14.getAction();
        if (r0 == r1) goto L_0x0034;
    L_0x0016:
        r3 = 2;
        if (r0 == r3) goto L_0x001e;
    L_0x0019:
        r3 = 3;
        if (r0 == r3) goto L_0x0034;
    L_0x001c:
        goto L_0x0108;
    L_0x001e:
        r3 = r13.mVertical;
        if (r3 == 0) goto L_0x0027;
    L_0x0022:
        r3 = r14.getY();
        goto L_0x002b;
    L_0x0027:
        r3 = r14.getX();
    L_0x002b:
        r3 = (int) r3;
        r4 = r13.mTouchDelta;
        r3 = r3 - r4;
        r13.moveHandle(r3);
        goto L_0x0108;
    L_0x0034:
        r3 = r13.mVelocityTracker;
        r4 = r13.mVelocityUnits;
        r3.computeCurrentVelocity(r4);
        r4 = r3.getYVelocity();
        r5 = r3.getXVelocity();
        r6 = r13.mVertical;
        r7 = 0;
        if (r6 == 0) goto L_0x005d;
    L_0x0048:
        r8 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
        if (r8 >= 0) goto L_0x004e;
    L_0x004c:
        r8 = r1;
        goto L_0x004f;
    L_0x004e:
        r8 = r2;
    L_0x004f:
        r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r7 >= 0) goto L_0x0054;
    L_0x0053:
        r5 = -r5;
    L_0x0054:
        r7 = r13.mMaximumMinorVelocity;
        r9 = (float) r7;
        r9 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1));
        if (r9 <= 0) goto L_0x0071;
    L_0x005b:
        r5 = (float) r7;
        goto L_0x0071;
    L_0x005d:
        r8 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r8 >= 0) goto L_0x0063;
    L_0x0061:
        r8 = r1;
        goto L_0x0064;
    L_0x0063:
        r8 = r2;
    L_0x0064:
        r7 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
        if (r7 >= 0) goto L_0x0069;
    L_0x0068:
        r4 = -r4;
    L_0x0069:
        r7 = r13.mMaximumMinorVelocity;
        r9 = (float) r7;
        r9 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));
        if (r9 <= 0) goto L_0x0071;
    L_0x0070:
        r4 = (float) r7;
    L_0x0071:
        r9 = (double) r5;
        r11 = (double) r4;
        r9 = java.lang.Math.hypot(r9, r11);
        r7 = (float) r9;
        if (r8 == 0) goto L_0x007b;
    L_0x007a:
        r7 = -r7;
    L_0x007b:
        r9 = r13.mHandle;
        r9 = r9.getTop();
        r10 = r13.mHandle;
        r10 = r10.getLeft();
        r11 = java.lang.Math.abs(r7);
        r12 = r13.mMaximumTapVelocity;
        r12 = (float) r12;
        r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1));
        if (r11 >= 0) goto L_0x0100;
    L_0x0092:
        r11 = r13.mExpanded;
        if (r6 == 0) goto L_0x00b4;
    L_0x0096:
        if (r11 == 0) goto L_0x009f;
    L_0x0098:
        r11 = r13.mTapThreshold;
        r12 = r13.mTopOffset;
        r11 = r11 + r12;
        if (r9 < r11) goto L_0x00d1;
    L_0x009f:
        r11 = r13.mExpanded;
        if (r11 != 0) goto L_0x00f7;
    L_0x00a3:
        r11 = r13.mBottomOffset;
        r12 = r13.mBottom;
        r11 = r11 + r12;
        r12 = r13.mTop;
        r11 = r11 - r12;
        r12 = r13.mHandleHeight;
        r11 = r11 - r12;
        r12 = r13.mTapThreshold;
        r11 = r11 - r12;
        if (r9 <= r11) goto L_0x00f7;
    L_0x00b3:
        goto L_0x00d1;
    L_0x00b4:
        if (r11 == 0) goto L_0x00bd;
    L_0x00b6:
        r11 = r13.mTapThreshold;
        r12 = r13.mTopOffset;
        r11 = r11 + r12;
        if (r10 < r11) goto L_0x00d1;
    L_0x00bd:
        r11 = r13.mExpanded;
        if (r11 != 0) goto L_0x00f7;
    L_0x00c1:
        r11 = r13.mBottomOffset;
        r12 = r13.mRight;
        r11 = r11 + r12;
        r12 = r13.mLeft;
        r11 = r11 - r12;
        r12 = r13.mHandleWidth;
        r11 = r11 - r12;
        r12 = r13.mTapThreshold;
        r11 = r11 - r12;
        if (r10 <= r11) goto L_0x00f7;
    L_0x00d1:
        r11 = r13.mAllowSingleTap;
        if (r11 == 0) goto L_0x00ee;
    L_0x00d5:
        r13.playSoundEffect(r2);
        r11 = r13.mExpanded;
        if (r11 == 0) goto L_0x00e5;
    L_0x00dc:
        if (r6 == 0) goto L_0x00e0;
    L_0x00de:
        r11 = r9;
        goto L_0x00e1;
    L_0x00e0:
        r11 = r10;
    L_0x00e1:
        r13.animateClose(r11, r1);
        goto L_0x0108;
    L_0x00e5:
        if (r6 == 0) goto L_0x00e9;
    L_0x00e7:
        r11 = r9;
        goto L_0x00ea;
    L_0x00e9:
        r11 = r10;
    L_0x00ea:
        r13.animateOpen(r11, r1);
        goto L_0x0108;
    L_0x00ee:
        if (r6 == 0) goto L_0x00f2;
    L_0x00f0:
        r11 = r9;
        goto L_0x00f3;
    L_0x00f2:
        r11 = r10;
    L_0x00f3:
        r13.performFling(r11, r7, r2, r1);
        goto L_0x0108;
    L_0x00f7:
        if (r6 == 0) goto L_0x00fb;
    L_0x00f9:
        r11 = r9;
        goto L_0x00fc;
    L_0x00fb:
        r11 = r10;
    L_0x00fc:
        r13.performFling(r11, r7, r2, r1);
        goto L_0x0108;
    L_0x0100:
        if (r6 == 0) goto L_0x0104;
    L_0x0102:
        r11 = r9;
        goto L_0x0105;
    L_0x0104:
        r11 = r10;
    L_0x0105:
        r13.performFling(r11, r7, r2, r1);
    L_0x0108:
        r0 = r13.mTracking;
        if (r0 != 0) goto L_0x0118;
    L_0x010c:
        r0 = r13.mAnimating;
        if (r0 != 0) goto L_0x0118;
    L_0x0110:
        r0 = super.onTouchEvent(r14);
        if (r0 == 0) goto L_0x0117;
    L_0x0116:
        goto L_0x0118;
    L_0x0117:
        r1 = r2;
    L_0x0118:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.SlidingDrawer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void animateClose(int position, boolean notifyScrollListener) {
        prepareTracking(position);
        performFling(position, (float) this.mMaximumAcceleration, true, notifyScrollListener);
    }

    private void animateOpen(int position, boolean notifyScrollListener) {
        prepareTracking(position);
        performFling(position, (float) (-this.mMaximumAcceleration), true, notifyScrollListener);
    }

    /* JADX WARNING: Missing block: B:29:0x0063, code skipped:
            if (r8 > ((float) (-r6.mMaximumMajorVelocity))) goto L_0x0065;
     */
    private void performFling(int r7, float r8, boolean r9, boolean r10) {
        /*
        r6 = this;
        r0 = (float) r7;
        r6.mAnimationPosition = r0;
        r6.mAnimatedVelocity = r8;
        r0 = r6.mExpanded;
        r1 = 0;
        if (r0 == 0) goto L_0x0043;
    L_0x000a:
        if (r9 != 0) goto L_0x0037;
    L_0x000c:
        r0 = r6.mMaximumMajorVelocity;
        r0 = (float) r0;
        r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x0037;
    L_0x0013:
        r0 = r6.mTopOffset;
        r2 = r6.mVertical;
        if (r2 == 0) goto L_0x001c;
    L_0x0019:
        r2 = r6.mHandleHeight;
        goto L_0x001e;
    L_0x001c:
        r2 = r6.mHandleWidth;
    L_0x001e:
        r0 = r0 + r2;
        if (r7 <= r0) goto L_0x002a;
    L_0x0021:
        r0 = r6.mMaximumMajorVelocity;
        r0 = -r0;
        r0 = (float) r0;
        r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x002a;
    L_0x0029:
        goto L_0x0037;
    L_0x002a:
        r0 = r6.mMaximumAcceleration;
        r0 = -r0;
        r0 = (float) r0;
        r6.mAnimatedAcceleration = r0;
        r0 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1));
        if (r0 <= 0) goto L_0x007d;
    L_0x0034:
        r6.mAnimatedVelocity = r1;
        goto L_0x007d;
    L_0x0037:
        r0 = r6.mMaximumAcceleration;
        r0 = (float) r0;
        r6.mAnimatedAcceleration = r0;
        r0 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1));
        if (r0 >= 0) goto L_0x007d;
    L_0x0040:
        r6.mAnimatedVelocity = r1;
        goto L_0x007d;
    L_0x0043:
        if (r9 != 0) goto L_0x0071;
    L_0x0045:
        r0 = r6.mMaximumMajorVelocity;
        r0 = (float) r0;
        r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x0065;
    L_0x004c:
        r0 = r6.mVertical;
        if (r0 == 0) goto L_0x0055;
    L_0x0050:
        r0 = r6.getHeight();
        goto L_0x0059;
    L_0x0055:
        r0 = r6.getWidth();
    L_0x0059:
        r0 = r0 / 2;
        if (r7 <= r0) goto L_0x0071;
    L_0x005d:
        r0 = r6.mMaximumMajorVelocity;
        r0 = -r0;
        r0 = (float) r0;
        r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x0071;
    L_0x0065:
        r0 = r6.mMaximumAcceleration;
        r0 = (float) r0;
        r6.mAnimatedAcceleration = r0;
        r0 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1));
        if (r0 >= 0) goto L_0x007d;
    L_0x006e:
        r6.mAnimatedVelocity = r1;
        goto L_0x007d;
    L_0x0071:
        r0 = r6.mMaximumAcceleration;
        r0 = -r0;
        r0 = (float) r0;
        r6.mAnimatedAcceleration = r0;
        r0 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1));
        if (r0 <= 0) goto L_0x007d;
    L_0x007b:
        r6.mAnimatedVelocity = r1;
    L_0x007d:
        r0 = android.os.SystemClock.uptimeMillis();
        r6.mAnimationLastTime = r0;
        r2 = 16;
        r4 = r0 + r2;
        r6.mCurrentAnimationTime = r4;
        r4 = 1;
        r6.mAnimating = r4;
        r4 = r6.mSlidingRunnable;
        r6.removeCallbacks(r4);
        r4 = r6.mSlidingRunnable;
        r6.postDelayed(r4, r2);
        r6.stopTracking(r10);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.SlidingDrawer.performFling(int, float, boolean, boolean):void");
    }

    @UnsupportedAppUsage
    private void prepareTracking(int position) {
        this.mTracking = true;
        this.mVelocityTracker = VelocityTracker.obtain();
        if (this.mExpanded ^ true) {
            int height;
            int i;
            this.mAnimatedAcceleration = (float) this.mMaximumAcceleration;
            this.mAnimatedVelocity = (float) this.mMaximumMajorVelocity;
            int i2 = this.mBottomOffset;
            if (this.mVertical) {
                height = getHeight();
                i = this.mHandleHeight;
            } else {
                height = getWidth();
                i = this.mHandleWidth;
            }
            this.mAnimationPosition = (float) (i2 + (height - i));
            moveHandle((int) this.mAnimationPosition);
            this.mAnimating = true;
            removeCallbacks(this.mSlidingRunnable);
            long now = SystemClock.uptimeMillis();
            this.mAnimationLastTime = now;
            this.mCurrentAnimationTime = 16 + now;
            this.mAnimating = true;
            return;
        }
        if (this.mAnimating) {
            this.mAnimating = false;
            removeCallbacks(this.mSlidingRunnable);
        }
        moveHandle(position);
    }

    private void moveHandle(int position) {
        View handle = this.mHandle;
        int top;
        int deltaY;
        int i;
        Rect frame;
        Rect region;
        if (this.mVertical) {
            if (position == EXPANDED_FULL_OPEN) {
                handle.offsetTopAndBottom(this.mTopOffset - handle.getTop());
                invalidate();
            } else if (position == COLLAPSED_FULL_CLOSED) {
                handle.offsetTopAndBottom((((this.mBottomOffset + this.mBottom) - this.mTop) - this.mHandleHeight) - handle.getTop());
                invalidate();
            } else {
                top = handle.getTop();
                deltaY = position - top;
                i = this.mTopOffset;
                if (position < i) {
                    deltaY = i - top;
                } else if (deltaY > (((this.mBottomOffset + this.mBottom) - this.mTop) - this.mHandleHeight) - top) {
                    deltaY = (((this.mBottomOffset + this.mBottom) - this.mTop) - this.mHandleHeight) - top;
                }
                handle.offsetTopAndBottom(deltaY);
                frame = this.mFrame;
                region = this.mInvalidate;
                handle.getHitRect(frame);
                region.set(frame);
                region.union(frame.left, frame.top - deltaY, frame.right, frame.bottom - deltaY);
                region.union(0, frame.bottom - deltaY, getWidth(), (frame.bottom - deltaY) + this.mContent.getHeight());
                invalidate(region);
            }
        } else if (position == EXPANDED_FULL_OPEN) {
            handle.offsetLeftAndRight(this.mTopOffset - handle.getLeft());
            invalidate();
        } else if (position == COLLAPSED_FULL_CLOSED) {
            handle.offsetLeftAndRight((((this.mBottomOffset + this.mRight) - this.mLeft) - this.mHandleWidth) - handle.getLeft());
            invalidate();
        } else {
            top = handle.getLeft();
            deltaY = position - top;
            i = this.mTopOffset;
            if (position < i) {
                deltaY = i - top;
            } else if (deltaY > (((this.mBottomOffset + this.mRight) - this.mLeft) - this.mHandleWidth) - top) {
                deltaY = (((this.mBottomOffset + this.mRight) - this.mLeft) - this.mHandleWidth) - top;
            }
            handle.offsetLeftAndRight(deltaY);
            frame = this.mFrame;
            region = this.mInvalidate;
            handle.getHitRect(frame);
            region.set(frame);
            region.union(frame.left - deltaY, frame.top, frame.right - deltaY, frame.bottom);
            region.union(frame.right - deltaY, 0, (frame.right - deltaY) + this.mContent.getWidth(), getHeight());
            invalidate(region);
        }
    }

    @UnsupportedAppUsage
    private void prepareContent() {
        if (!this.mAnimating) {
            View content = this.mContent;
            if (content.isLayoutRequested()) {
                int childHeight;
                if (this.mVertical) {
                    childHeight = this.mHandleHeight;
                    content.measure(MeasureSpec.makeMeasureSpec(this.mRight - this.mLeft, 1073741824), MeasureSpec.makeMeasureSpec(((this.mBottom - this.mTop) - childHeight) - this.mTopOffset, 1073741824));
                    content.layout(0, this.mTopOffset + childHeight, content.getMeasuredWidth(), (this.mTopOffset + childHeight) + content.getMeasuredHeight());
                } else {
                    childHeight = this.mHandle.getWidth();
                    content.measure(MeasureSpec.makeMeasureSpec(((this.mRight - this.mLeft) - childHeight) - this.mTopOffset, 1073741824), MeasureSpec.makeMeasureSpec(this.mBottom - this.mTop, 1073741824));
                    int i = this.mTopOffset;
                    content.layout(childHeight + i, 0, (i + childHeight) + content.getMeasuredWidth(), content.getMeasuredHeight());
                }
            }
            content.getViewTreeObserver().dispatchOnPreDraw();
            if (!content.isHardwareAccelerated()) {
                content.buildDrawingCache();
            }
            content.setVisibility(8);
        }
    }

    private void stopTracking(boolean notifyScrollListener) {
        this.mHandle.setPressed(false);
        this.mTracking = false;
        if (notifyScrollListener) {
            OnDrawerScrollListener onDrawerScrollListener = this.mOnDrawerScrollListener;
            if (onDrawerScrollListener != null) {
                onDrawerScrollListener.onScrollEnded();
            }
        }
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void doAnimation() {
        if (this.mAnimating) {
            incrementAnimation();
            if (this.mAnimationPosition >= ((float) ((this.mBottomOffset + (this.mVertical ? getHeight() : getWidth())) - 1))) {
                this.mAnimating = false;
                closeDrawer();
                return;
            }
            float f = this.mAnimationPosition;
            if (f < ((float) this.mTopOffset)) {
                this.mAnimating = false;
                openDrawer();
                return;
            }
            moveHandle((int) f);
            this.mCurrentAnimationTime += 16;
            postDelayed(this.mSlidingRunnable, 16);
        }
    }

    private void incrementAnimation() {
        long now = SystemClock.uptimeMillis();
        float t = ((float) (now - this.mAnimationLastTime)) / 1000.0f;
        float position = this.mAnimationPosition;
        float v = this.mAnimatedVelocity;
        float a = this.mAnimatedAcceleration;
        this.mAnimationPosition = ((v * t) + position) + (((0.5f * a) * t) * t);
        this.mAnimatedVelocity = (a * t) + v;
        this.mAnimationLastTime = now;
    }

    public void toggle() {
        if (this.mExpanded) {
            closeDrawer();
        } else {
            openDrawer();
        }
        invalidate();
        requestLayout();
    }

    public void animateToggle() {
        if (this.mExpanded) {
            animateClose();
        } else {
            animateOpen();
        }
    }

    public void open() {
        openDrawer();
        invalidate();
        requestLayout();
        sendAccessibilityEvent(32);
    }

    public void close() {
        closeDrawer();
        invalidate();
        requestLayout();
    }

    public void animateClose() {
        prepareContent();
        OnDrawerScrollListener scrollListener = this.mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }
        animateClose(this.mVertical ? this.mHandle.getTop() : this.mHandle.getLeft(), false);
        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    public void animateOpen() {
        prepareContent();
        OnDrawerScrollListener scrollListener = this.mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }
        animateOpen(this.mVertical ? this.mHandle.getTop() : this.mHandle.getLeft(), false);
        sendAccessibilityEvent(32);
        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    public CharSequence getAccessibilityClassName() {
        return SlidingDrawer.class.getName();
    }

    private void closeDrawer() {
        moveHandle(COLLAPSED_FULL_CLOSED);
        this.mContent.setVisibility(8);
        this.mContent.destroyDrawingCache();
        if (this.mExpanded) {
            this.mExpanded = false;
            OnDrawerCloseListener onDrawerCloseListener = this.mOnDrawerCloseListener;
            if (onDrawerCloseListener != null) {
                onDrawerCloseListener.onDrawerClosed();
            }
        }
    }

    private void openDrawer() {
        moveHandle(EXPANDED_FULL_OPEN);
        this.mContent.setVisibility(0);
        if (!this.mExpanded) {
            this.mExpanded = true;
            OnDrawerOpenListener onDrawerOpenListener = this.mOnDrawerOpenListener;
            if (onDrawerOpenListener != null) {
                onDrawerOpenListener.onDrawerOpened();
            }
        }
    }

    public void setOnDrawerOpenListener(OnDrawerOpenListener onDrawerOpenListener) {
        this.mOnDrawerOpenListener = onDrawerOpenListener;
    }

    public void setOnDrawerCloseListener(OnDrawerCloseListener onDrawerCloseListener) {
        this.mOnDrawerCloseListener = onDrawerCloseListener;
    }

    public void setOnDrawerScrollListener(OnDrawerScrollListener onDrawerScrollListener) {
        this.mOnDrawerScrollListener = onDrawerScrollListener;
    }

    public View getHandle() {
        return this.mHandle;
    }

    public View getContent() {
        return this.mContent;
    }

    public void unlock() {
        this.mLocked = false;
    }

    public void lock() {
        this.mLocked = true;
    }

    public boolean isOpened() {
        return this.mExpanded;
    }

    public boolean isMoving() {
        return this.mTracking || this.mAnimating;
    }
}
