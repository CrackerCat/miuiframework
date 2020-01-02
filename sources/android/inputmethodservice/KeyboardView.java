package android.inputmethodservice;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard.Key;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.android.internal.R;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class KeyboardView extends View implements OnClickListener {
    private static final int DEBOUNCE_TIME = 70;
    private static final boolean DEBUG = false;
    private static final int DELAY_AFTER_PREVIEW = 70;
    private static final int DELAY_BEFORE_PREVIEW = 0;
    private static final int[] KEY_DELETE = new int[]{-5};
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int[] LONG_PRESSABLE_STATE_SET = new int[]{16843324};
    private static int MAX_NEARBY_KEYS = 12;
    private static final int MSG_LONGPRESS = 4;
    private static final int MSG_REMOVE_PREVIEW = 2;
    private static final int MSG_REPEAT = 3;
    private static final int MSG_SHOW_PREVIEW = 1;
    private static final int MULTITAP_INTERVAL = 800;
    private static final int NOT_A_KEY = -1;
    private static final int REPEAT_INTERVAL = 50;
    private static final int REPEAT_START_DELAY = 400;
    private boolean mAbortKey;
    private AccessibilityManager mAccessibilityManager;
    private AudioManager mAudioManager;
    private float mBackgroundDimAmount;
    private Bitmap mBuffer;
    private Canvas mCanvas;
    private Rect mClipRegion;
    private final int[] mCoordinates;
    private int mCurrentKey;
    private int mCurrentKeyIndex;
    private long mCurrentKeyTime;
    private Rect mDirtyRect;
    private boolean mDisambiguateSwipe;
    private int[] mDistances;
    private int mDownKey;
    private long mDownTime;
    private boolean mDrawPending;
    private GestureDetector mGestureDetector;
    Handler mHandler;
    private boolean mHeadsetRequiredToHearPasswordsAnnounced;
    private boolean mInMultiTap;
    private Key mInvalidatedKey;
    @UnsupportedAppUsage
    private Drawable mKeyBackground;
    private int[] mKeyIndices;
    private int mKeyTextColor;
    private int mKeyTextSize;
    private Keyboard mKeyboard;
    private OnKeyboardActionListener mKeyboardActionListener;
    private boolean mKeyboardChanged;
    private Key[] mKeys;
    @UnsupportedAppUsage
    private int mLabelTextSize;
    private int mLastCodeX;
    private int mLastCodeY;
    private int mLastKey;
    private long mLastKeyTime;
    private long mLastMoveTime;
    private int mLastSentIndex;
    private long mLastTapTime;
    private int mLastX;
    private int mLastY;
    private KeyboardView mMiniKeyboard;
    private Map<Key, View> mMiniKeyboardCache;
    private View mMiniKeyboardContainer;
    private int mMiniKeyboardOffsetX;
    private int mMiniKeyboardOffsetY;
    private boolean mMiniKeyboardOnScreen;
    private int mOldPointerCount;
    private float mOldPointerX;
    private float mOldPointerY;
    private Rect mPadding;
    private Paint mPaint;
    private PopupWindow mPopupKeyboard;
    private int mPopupLayout;
    private View mPopupParent;
    private int mPopupPreviewX;
    private int mPopupPreviewY;
    private int mPopupX;
    private int mPopupY;
    private boolean mPossiblePoly;
    private boolean mPreviewCentered;
    private int mPreviewHeight;
    private StringBuilder mPreviewLabel;
    private int mPreviewOffset;
    private PopupWindow mPreviewPopup;
    @UnsupportedAppUsage
    private TextView mPreviewText;
    private int mPreviewTextSizeLarge;
    private boolean mProximityCorrectOn;
    private int mProximityThreshold;
    private int mRepeatKeyIndex;
    private int mShadowColor;
    private float mShadowRadius;
    private boolean mShowPreview;
    private boolean mShowTouchPoints;
    private int mStartX;
    private int mStartY;
    private int mSwipeThreshold;
    private SwipeTracker mSwipeTracker;
    private int mTapCount;
    private int mVerticalCorrection;

    public interface OnKeyboardActionListener {
        void onKey(int i, int[] iArr);

        void onPress(int i);

        void onRelease(int i);

        void onText(CharSequence charSequence);

        void swipeDown();

        void swipeLeft();

        void swipeRight();

        void swipeUp();
    }

    private static class SwipeTracker {
        static final int LONGEST_PAST_TIME = 200;
        static final int NUM_PAST = 4;
        final long[] mPastTime;
        final float[] mPastX;
        final float[] mPastY;
        float mXVelocity;
        float mYVelocity;

        private SwipeTracker() {
            this.mPastX = new float[4];
            this.mPastY = new float[4];
            this.mPastTime = new long[4];
        }

        /* synthetic */ SwipeTracker(AnonymousClass1 x0) {
            this();
        }

        public void clear() {
            this.mPastTime[0] = 0;
        }

        public void addMovement(MotionEvent ev) {
            long time = ev.getEventTime();
            int N = ev.getHistorySize();
            for (int i = 0; i < N; i++) {
                addPoint(ev.getHistoricalX(i), ev.getHistoricalY(i), ev.getHistoricalEventTime(i));
            }
            addPoint(ev.getX(), ev.getY(), time);
        }

        private void addPoint(float x, float y, long time) {
            int drop = -1;
            long[] pastTime = this.mPastTime;
            int i = 0;
            while (i < 4 && pastTime[i] != 0) {
                if (pastTime[i] < time - 200) {
                    drop = i;
                }
                i++;
            }
            if (i == 4 && drop < 0) {
                drop = 0;
            }
            if (drop == i) {
                drop--;
            }
            float[] pastX = this.mPastX;
            float[] pastY = this.mPastY;
            if (drop >= 0) {
                int start = drop + 1;
                int count = (4 - drop) - 1;
                System.arraycopy(pastX, start, pastX, 0, count);
                System.arraycopy(pastY, start, pastY, 0, count);
                System.arraycopy(pastTime, start, pastTime, 0, count);
                i -= drop + 1;
            }
            pastX[i] = x;
            pastY[i] = y;
            pastTime[i] = time;
            i++;
            if (i < 4) {
                pastTime[i] = 0;
            }
        }

        public void computeCurrentVelocity(int units) {
            computeCurrentVelocity(units, Float.MAX_VALUE);
        }

        public void computeCurrentVelocity(int units, float maxVelocity) {
            long[] pastTime;
            float max;
            int i = units;
            float f = maxVelocity;
            float[] pastX = this.mPastX;
            float[] pastY = this.mPastY;
            long[] pastTime2 = this.mPastTime;
            float oldestX = pastX[0];
            float oldestY = pastY[0];
            long oldestTime = pastTime2[0];
            float accumX = 0.0f;
            float accumY = 0.0f;
            int N = 0;
            while (N < 4 && pastTime2[N] != 0) {
                N++;
            }
            int i2 = 1;
            while (i2 < N) {
                float[] pastX2;
                int dur = (int) (pastTime2[i2] - oldestTime);
                if (dur == 0) {
                    pastX2 = pastX;
                    pastTime = pastTime2;
                } else {
                    pastX2 = pastX;
                    pastTime = pastTime2;
                    pastX = ((pastX[i2] - oldestX) / ((float) dur)) * ((float) i);
                    if (accumX == 0.0f) {
                        accumX = pastX;
                    } else {
                        accumX = (accumX + pastX) * 0.5f;
                    }
                    float vel = ((pastY[i2] - oldestY) / ((float) dur)) * ((float) i);
                    if (accumY == null) {
                        accumY = vel;
                    } else {
                        accumY = (accumY + vel) * 0.5f;
                    }
                }
                i2++;
                pastX = pastX2;
                pastTime2 = pastTime;
            }
            pastTime = pastTime2;
            if (accumX < 0.0f) {
                max = Math.max(accumX, -f);
            } else {
                max = Math.min(accumX, f);
            }
            this.mXVelocity = max;
            if (accumY < 0.0f) {
                max = Math.max(accumY, -f);
            } else {
                max = Math.min(accumY, f);
            }
            this.mYVelocity = max;
        }

        public float getXVelocity() {
            return this.mXVelocity;
        }

        public float getYVelocity() {
            return this.mYVelocity;
        }
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.keyboardViewStyle);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Context context2 = context;
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mCurrentKeyIndex = -1;
        this.mCoordinates = new int[2];
        this.mPreviewCentered = false;
        this.mShowPreview = true;
        this.mShowTouchPoints = true;
        this.mCurrentKey = -1;
        this.mDownKey = -1;
        this.mKeyIndices = new int[12];
        this.mRepeatKeyIndex = -1;
        this.mClipRegion = new Rect(0, 0, 0, 0);
        this.mSwipeTracker = new SwipeTracker();
        this.mOldPointerCount = 1;
        this.mDistances = new int[MAX_NEARBY_KEYS];
        this.mPreviewLabel = new StringBuilder(1);
        this.mDirtyRect = new Rect();
        TypedArray a = context2.obtainStyledAttributes(attrs, android.R.styleable.KeyboardView, defStyleAttr, defStyleRes);
        LayoutInflater inflate = (LayoutInflater) context2.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int previewLayout = 0;
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    this.mShadowColor = a.getColor(attr, 0);
                    break;
                case 1:
                    this.mShadowRadius = a.getFloat(attr, 0.0f);
                    break;
                case 2:
                    this.mKeyBackground = a.getDrawable(attr);
                    break;
                case 3:
                    this.mKeyTextSize = a.getDimensionPixelSize(attr, 18);
                    break;
                case 4:
                    this.mLabelTextSize = a.getDimensionPixelSize(attr, 14);
                    break;
                case 5:
                    this.mKeyTextColor = a.getColor(attr, -16777216);
                    break;
                case 6:
                    previewLayout = a.getResourceId(attr, 0);
                    break;
                case 7:
                    this.mPreviewOffset = a.getDimensionPixelOffset(attr, 0);
                    break;
                case 8:
                    this.mPreviewHeight = a.getDimensionPixelSize(attr, 80);
                    break;
                case 9:
                    this.mVerticalCorrection = a.getDimensionPixelOffset(attr, 0);
                    break;
                case 10:
                    this.mPopupLayout = a.getResourceId(attr, 0);
                    break;
                default:
                    break;
            }
        }
        this.mBackgroundDimAmount = this.mContext.obtainStyledAttributes(R.styleable.Theme).getFloat(2, 0.5f);
        this.mPreviewPopup = new PopupWindow(context2);
        if (previewLayout != 0) {
            this.mPreviewText = (TextView) inflate.inflate(previewLayout, null);
            this.mPreviewTextSizeLarge = (int) this.mPreviewText.getTextSize();
            this.mPreviewPopup.setContentView(this.mPreviewText);
            this.mPreviewPopup.setBackgroundDrawable(null);
        } else {
            this.mShowPreview = false;
        }
        this.mPreviewPopup.setTouchable(false);
        this.mPopupKeyboard = new PopupWindow(context2);
        this.mPopupKeyboard.setBackgroundDrawable(null);
        this.mPopupParent = this;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize((float) 0);
        this.mPaint.setTextAlign(Align.CENTER);
        this.mPaint.setAlpha(255);
        this.mPadding = new Rect(0, 0, 0, 0);
        this.mMiniKeyboardCache = new HashMap();
        this.mKeyBackground.getPadding(this.mPadding);
        this.mSwipeThreshold = (int) (getResources().getDisplayMetrics().density * 500.0f);
        this.mDisambiguateSwipe = getResources().getBoolean(R.bool.config_swipeDisambiguation);
        this.mAccessibilityManager = AccessibilityManager.getInstance(context);
        this.mAudioManager = (AudioManager) context2.getSystemService("audio");
        resetMultiTap();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initGestureDetector();
        if (this.mHandler == null) {
            this.mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    int i = msg.what;
                    if (i == 1) {
                        KeyboardView.this.showKey(msg.arg1);
                    } else if (i == 2) {
                        KeyboardView.this.mPreviewText.setVisibility(4);
                    } else if (i != 3) {
                        if (i == 4) {
                            KeyboardView.this.openPopupIfRequired((MotionEvent) msg.obj);
                        }
                    } else if (KeyboardView.this.repeatKey()) {
                        sendMessageDelayed(Message.obtain((Handler) this, 3), 50);
                    }
                }
            };
        }
    }

    private void initGestureDetector() {
        if (this.mGestureDetector == null) {
            this.mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
                public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                    if (KeyboardView.this.mPossiblePoly) {
                        return false;
                    }
                    float absX = Math.abs(velocityX);
                    float absY = Math.abs(velocityY);
                    float deltaX = me2.getX() - me1.getX();
                    float deltaY = me2.getY() - me1.getY();
                    int travelX = KeyboardView.this.getWidth() / 2;
                    int travelY = KeyboardView.this.getHeight() / 2;
                    KeyboardView.this.mSwipeTracker.computeCurrentVelocity(1000);
                    float endingVelocityX = KeyboardView.this.mSwipeTracker.getXVelocity();
                    float endingVelocityY = KeyboardView.this.mSwipeTracker.getYVelocity();
                    boolean sendDownKey = false;
                    if (velocityX <= ((float) KeyboardView.this.mSwipeThreshold) || absY >= absX || deltaX <= ((float) travelX)) {
                        if (velocityX >= ((float) (-KeyboardView.this.mSwipeThreshold)) || absY >= absX || deltaX >= ((float) (-travelX))) {
                            if (velocityY >= ((float) (-KeyboardView.this.mSwipeThreshold)) || absX >= absY || deltaY >= ((float) (-travelY))) {
                                if (velocityY > ((float) KeyboardView.this.mSwipeThreshold) && absX < absY / 2.0f && deltaY > ((float) travelY)) {
                                    if (!KeyboardView.this.mDisambiguateSwipe || endingVelocityY >= velocityY / 4.0f) {
                                        KeyboardView.this.swipeDown();
                                        return true;
                                    }
                                    sendDownKey = true;
                                }
                            } else if (!KeyboardView.this.mDisambiguateSwipe || endingVelocityY <= velocityY / 4.0f) {
                                KeyboardView.this.swipeUp();
                                return true;
                            } else {
                                sendDownKey = true;
                            }
                        } else if (!KeyboardView.this.mDisambiguateSwipe || endingVelocityX <= velocityX / 4.0f) {
                            KeyboardView.this.swipeLeft();
                            return true;
                        } else {
                            sendDownKey = true;
                        }
                    } else if (!KeyboardView.this.mDisambiguateSwipe || endingVelocityX >= velocityX / 4.0f) {
                        KeyboardView.this.swipeRight();
                        return true;
                    } else {
                        sendDownKey = true;
                    }
                    if (sendDownKey) {
                        KeyboardView keyboardView = KeyboardView.this;
                        keyboardView.detectAndSendKey(keyboardView.mDownKey, KeyboardView.this.mStartX, KeyboardView.this.mStartY, me1.getEventTime());
                    }
                    return false;
                }
            });
            this.mGestureDetector.setIsLongpressEnabled(false);
        }
    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        this.mKeyboardActionListener = listener;
    }

    /* Access modifiers changed, original: protected */
    public OnKeyboardActionListener getOnKeyboardActionListener() {
        return this.mKeyboardActionListener;
    }

    public void setKeyboard(Keyboard keyboard) {
        if (this.mKeyboard != null) {
            showPreview(-1);
        }
        removeMessages();
        this.mKeyboard = keyboard;
        List<Key> keys = this.mKeyboard.getKeys();
        this.mKeys = (Key[]) keys.toArray(new Key[keys.size()]);
        requestLayout();
        this.mKeyboardChanged = true;
        invalidateAllKeys();
        computeProximityThreshold(keyboard);
        this.mMiniKeyboardCache.clear();
        this.mAbortKey = true;
    }

    public Keyboard getKeyboard() {
        return this.mKeyboard;
    }

    public boolean setShifted(boolean shifted) {
        Keyboard keyboard = this.mKeyboard;
        if (keyboard == null || !keyboard.setShifted(shifted)) {
            return false;
        }
        invalidateAllKeys();
        return true;
    }

    public boolean isShifted() {
        Keyboard keyboard = this.mKeyboard;
        if (keyboard != null) {
            return keyboard.isShifted();
        }
        return false;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        this.mShowPreview = previewEnabled;
    }

    public boolean isPreviewEnabled() {
        return this.mShowPreview;
    }

    public void setVerticalCorrection(int verticalOffset) {
    }

    public void setPopupParent(View v) {
        this.mPopupParent = v;
    }

    public void setPopupOffset(int x, int y) {
        this.mMiniKeyboardOffsetX = x;
        this.mMiniKeyboardOffsetY = y;
        if (this.mPreviewPopup.isShowing()) {
            this.mPreviewPopup.dismiss();
        }
    }

    public void setProximityCorrectionEnabled(boolean enabled) {
        this.mProximityCorrectOn = enabled;
    }

    public boolean isProximityCorrectionEnabled() {
        return this.mProximityCorrectOn;
    }

    public void onClick(View v) {
        dismissPopupKeyboard();
    }

    private CharSequence adjustCase(CharSequence label) {
        if (!this.mKeyboard.isShifted() || label == null || label.length() >= 3 || !Character.isLowerCase(label.charAt(0))) {
            return label;
        }
        return label.toString().toUpperCase();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Keyboard keyboard = this.mKeyboard;
        if (keyboard == null) {
            setMeasuredDimension(this.mPaddingLeft + this.mPaddingRight, this.mPaddingTop + this.mPaddingBottom);
            return;
        }
        int width = (keyboard.getMinWidth() + this.mPaddingLeft) + this.mPaddingRight;
        if (MeasureSpec.getSize(widthMeasureSpec) < width + 10) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        setMeasuredDimension(width, (this.mKeyboard.getHeight() + this.mPaddingTop) + this.mPaddingBottom);
    }

    private void computeProximityThreshold(Keyboard keyboard) {
        if (keyboard != null) {
            Key[] keys = this.mKeys;
            if (keys != null) {
                int i;
                int dimensionSum = 0;
                for (Key key : keys) {
                    dimensionSum += Math.min(key.width, key.height) + key.gap;
                }
                if (dimensionSum >= 0 && length != 0) {
                    this.mProximityThreshold = (int) ((((float) dimensionSum) * 1.4f) / ((float) length));
                    i = this.mProximityThreshold;
                    this.mProximityThreshold = i * i;
                }
            }
        }
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Keyboard keyboard = this.mKeyboard;
        if (keyboard != null) {
            keyboard.resize(w, h);
        }
        this.mBuffer = null;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawPending || this.mBuffer == null || this.mKeyboardChanged) {
            onBufferDraw();
        }
        canvas.drawBitmap(this.mBuffer, 0.0f, 0.0f, null);
    }

    private void onBufferDraw() {
        if (this.mBuffer == null || this.mKeyboardChanged) {
            Bitmap bitmap = this.mBuffer;
            if (bitmap == null || (this.mKeyboardChanged && !(bitmap.getWidth() == getWidth() && this.mBuffer.getHeight() == getHeight()))) {
                this.mBuffer = Bitmap.createBitmap(Math.max(1, getWidth()), Math.max(1, getHeight()), Config.ARGB_8888);
                this.mCanvas = new Canvas(this.mBuffer);
            }
            invalidateAllKeys();
            this.mKeyboardChanged = false;
        }
        if (this.mKeyboard != null) {
            boolean drawSingleKey;
            int keyCount;
            Key invalidKey;
            Key[] keys;
            this.mCanvas.save();
            Canvas canvas = this.mCanvas;
            canvas.clipRect(this.mDirtyRect);
            Paint paint = this.mPaint;
            Drawable keyBackground = this.mKeyBackground;
            Rect clipRegion = this.mClipRegion;
            Rect padding = this.mPadding;
            int kbdPaddingLeft = this.mPaddingLeft;
            int kbdPaddingTop = this.mPaddingTop;
            Key[] keys2 = this.mKeys;
            Key invalidKey2 = this.mInvalidatedKey;
            paint.setColor(this.mKeyTextColor);
            if (invalidKey2 == null || !canvas.getClipBounds(clipRegion) || (invalidKey2.x + kbdPaddingLeft) - 1 > clipRegion.left || (invalidKey2.y + kbdPaddingTop) - 1 > clipRegion.top || ((invalidKey2.x + invalidKey2.width) + kbdPaddingLeft) + 1 < clipRegion.right || ((invalidKey2.y + invalidKey2.height) + kbdPaddingTop) + 1 < clipRegion.bottom) {
                drawSingleKey = false;
            } else {
                drawSingleKey = true;
            }
            canvas.drawColor(0, Mode.CLEAR);
            int keyCount2 = keys2.length;
            int i = 0;
            while (i < keyCount2) {
                Key key = keys2[i];
                if (!drawSingleKey || invalidKey2 == key) {
                    int[] drawableState = key.getCurrentDrawableState();
                    keyBackground.setState(drawableState);
                    String label = key.label == null ? null : adjustCase(key.label).toString();
                    Rect bounds = keyBackground.getBounds();
                    keyCount = keyCount2;
                    if (key.width == bounds.right && key.height == bounds.bottom) {
                        Rect rect = bounds;
                    } else {
                        keyBackground.setBounds(0, 0, key.width, key.height);
                    }
                    canvas.translate((float) (key.x + kbdPaddingLeft), (float) (key.y + kbdPaddingTop));
                    keyBackground.draw(canvas);
                    if (label != null) {
                        if (label.length() <= 1 || key.codes.length >= 2) {
                            paint.setTextSize((float) this.mKeyTextSize);
                            paint.setTypeface(Typeface.DEFAULT);
                        } else {
                            paint.setTextSize((float) this.mLabelTextSize);
                            paint.setTypeface(Typeface.DEFAULT_BOLD);
                        }
                        paint.setShadowLayer(this.mShadowRadius, 0.0f, 0.0f, this.mShadowColor);
                        canvas.drawText(label, (float) ((((key.width - padding.left) - padding.right) / 2) + padding.left), (((float) (((key.height - padding.top) - padding.bottom) / 2)) + ((paint.getTextSize() - paint.descent()) / 2.0f)) + ((float) padding.top), paint);
                        paint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                        String str = label;
                        invalidKey = invalidKey2;
                        keys = keys2;
                    } else if (key.icon != null) {
                        int drawableX = ((((key.width - padding.left) - padding.right) - key.icon.getIntrinsicWidth()) / 2) + padding.left;
                        int drawableY = ((((key.height - padding.top) - padding.bottom) - key.icon.getIntrinsicHeight()) / 2) + padding.top;
                        canvas.translate((float) drawableX, (float) drawableY);
                        invalidKey = invalidKey2;
                        keys = keys2;
                        key.icon.setBounds(0, 0, key.icon.getIntrinsicWidth(), key.icon.getIntrinsicHeight());
                        key.icon.draw(canvas);
                        canvas.translate((float) (-drawableX), (float) (-drawableY));
                    } else {
                        invalidKey = invalidKey2;
                        keys = keys2;
                    }
                    canvas.translate((float) ((-key.x) - kbdPaddingLeft), (float) ((-key.y) - kbdPaddingTop));
                } else {
                    keyCount = keyCount2;
                    invalidKey = invalidKey2;
                    keys = keys2;
                }
                i++;
                keyCount2 = keyCount;
                invalidKey2 = invalidKey;
                keys2 = keys;
            }
            keyCount = keyCount2;
            invalidKey = invalidKey2;
            keys = keys2;
            this.mInvalidatedKey = null;
            if (this.mMiniKeyboardOnScreen) {
                paint.setColor(((int) (this.mBackgroundDimAmount * 255.0f)) << 24);
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), paint);
            } else {
                Key key2 = invalidKey;
                Key[] keyArr = keys;
            }
            this.mCanvas.restore();
            this.mDrawPending = false;
            this.mDirtyRect.setEmpty();
        }
    }

    /* JADX WARNING: Missing block: B:8:0x003a, code skipped:
            if (r14 >= r0.mProximityThreshold) goto L_0x003c;
     */
    /* JADX WARNING: Missing block: B:9:0x003c, code skipped:
            if (r13 != false) goto L_0x003e;
     */
    /* JADX WARNING: Missing block: B:11:0x0045, code skipped:
            if (r11.codes[0] <= 32) goto L_0x0091;
     */
    /* JADX WARNING: Missing block: B:12:0x0047, code skipped:
            r14 = r11.codes.length;
     */
    /* JADX WARNING: Missing block: B:13:0x004a, code skipped:
            if (r12 >= r7) goto L_0x004f;
     */
    /* JADX WARNING: Missing block: B:14:0x004c, code skipped:
            r7 = r12;
            r6 = r8[r10];
     */
    /* JADX WARNING: Missing block: B:15:0x004f, code skipped:
            if (r3 != null) goto L_0x0054;
     */
    /* JADX WARNING: Missing block: B:16:0x0051, code skipped:
            r16 = r4;
     */
    /* JADX WARNING: Missing block: B:17:0x0054, code skipped:
            r15 = 0;
     */
    /* JADX WARNING: Missing block: B:18:0x0055, code skipped:
            r1 = r0.mDistances;
     */
    /* JADX WARNING: Missing block: B:19:0x0058, code skipped:
            if (r15 >= r1.length) goto L_0x008e;
     */
    /* JADX WARNING: Missing block: B:21:0x005c, code skipped:
            if (r1[r15] <= r12) goto L_0x0085;
     */
    /* JADX WARNING: Missing block: B:22:0x005e, code skipped:
            r16 = r4;
            java.lang.System.arraycopy(r1, r15, r1, r15 + r14, (r1.length - r15) - r14);
            java.lang.System.arraycopy(r3, r15, r3, r15 + r14, (r3.length - r15) - r14);
            r1 = 0;
     */
    /* JADX WARNING: Missing block: B:23:0x0071, code skipped:
            if (r1 >= r14) goto L_0x0093;
     */
    /* JADX WARNING: Missing block: B:24:0x0073, code skipped:
            r3[r15 + r1] = r11.codes[r1];
            r0.mDistances[r15 + r1] = r12;
            r1 = r1 + 1;
     */
    /* JADX WARNING: Missing block: B:25:0x0085, code skipped:
            r16 = r4;
            r15 = r15 + 1;
            r1 = r18;
            r2 = r19;
     */
    /* JADX WARNING: Missing block: B:26:0x008e, code skipped:
            r16 = r4;
     */
    /* JADX WARNING: Missing block: B:27:0x0091, code skipped:
            r16 = r4;
     */
    /* JADX WARNING: Missing block: B:28:0x0093, code skipped:
            r10 = r10 + 1;
            r1 = r18;
            r2 = r19;
            r4 = r16;
     */
    private int getKeyIndices(int r18, int r19, int[] r20) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18;
        r2 = r19;
        r3 = r20;
        r4 = r0.mKeys;
        r5 = -1;
        r6 = -1;
        r7 = r0.mProximityThreshold;
        r7 = r7 + 1;
        r8 = r0.mDistances;
        r9 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        java.util.Arrays.fill(r8, r9);
        r8 = r0.mKeyboard;
        r8 = r8.getNearestKeys(r1, r2);
        r9 = r8.length;
        r10 = 0;
    L_0x0020:
        if (r10 >= r9) goto L_0x009c;
    L_0x0022:
        r11 = r8[r10];
        r11 = r4[r11];
        r12 = 0;
        r13 = r11.isInside(r1, r2);
        if (r13 == 0) goto L_0x002f;
    L_0x002d:
        r5 = r8[r10];
    L_0x002f:
        r14 = r0.mProximityCorrectOn;
        if (r14 == 0) goto L_0x003c;
    L_0x0033:
        r14 = r11.squaredDistanceFrom(r1, r2);
        r12 = r14;
        r15 = r0.mProximityThreshold;
        if (r14 < r15) goto L_0x003e;
    L_0x003c:
        if (r13 == 0) goto L_0x0091;
    L_0x003e:
        r14 = r11.codes;
        r15 = 0;
        r14 = r14[r15];
        r15 = 32;
        if (r14 <= r15) goto L_0x0091;
    L_0x0047:
        r14 = r11.codes;
        r14 = r14.length;
        if (r12 >= r7) goto L_0x004f;
    L_0x004c:
        r7 = r12;
        r6 = r8[r10];
    L_0x004f:
        if (r3 != 0) goto L_0x0054;
    L_0x0051:
        r16 = r4;
        goto L_0x0093;
    L_0x0054:
        r15 = 0;
    L_0x0055:
        r1 = r0.mDistances;
        r2 = r1.length;
        if (r15 >= r2) goto L_0x008e;
    L_0x005a:
        r2 = r1[r15];
        if (r2 <= r12) goto L_0x0085;
    L_0x005e:
        r2 = r15 + r14;
        r16 = r4;
        r4 = r1.length;
        r4 = r4 - r15;
        r4 = r4 - r14;
        java.lang.System.arraycopy(r1, r15, r1, r2, r4);
        r1 = r15 + r14;
        r2 = r3.length;
        r2 = r2 - r15;
        r2 = r2 - r14;
        java.lang.System.arraycopy(r3, r15, r3, r1, r2);
        r1 = 0;
    L_0x0071:
        if (r1 >= r14) goto L_0x0084;
    L_0x0073:
        r2 = r15 + r1;
        r4 = r11.codes;
        r4 = r4[r1];
        r3[r2] = r4;
        r2 = r0.mDistances;
        r4 = r15 + r1;
        r2[r4] = r12;
        r1 = r1 + 1;
        goto L_0x0071;
    L_0x0084:
        goto L_0x0093;
    L_0x0085:
        r16 = r4;
        r15 = r15 + 1;
        r1 = r18;
        r2 = r19;
        goto L_0x0055;
    L_0x008e:
        r16 = r4;
        goto L_0x0093;
    L_0x0091:
        r16 = r4;
    L_0x0093:
        r10 = r10 + 1;
        r1 = r18;
        r2 = r19;
        r4 = r16;
        goto L_0x0020;
    L_0x009c:
        r16 = r4;
        r1 = -1;
        if (r5 != r1) goto L_0x00a2;
    L_0x00a1:
        r5 = r6;
    L_0x00a2:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.inputmethodservice.KeyboardView.getKeyIndices(int, int, int[]):int");
    }

    private void detectAndSendKey(int index, int x, int y, long eventTime) {
        if (index != -1) {
            Key key = this.mKeys;
            if (index < key.length) {
                key = key[index];
                if (key.text != null) {
                    this.mKeyboardActionListener.onText(key.text);
                    this.mKeyboardActionListener.onRelease(-1);
                } else {
                    int code = key.codes[0];
                    int[] codes = new int[MAX_NEARBY_KEYS];
                    Arrays.fill(codes, -1);
                    getKeyIndices(x, y, codes);
                    if (this.mInMultiTap) {
                        if (this.mTapCount != -1) {
                            this.mKeyboardActionListener.onKey(-5, KEY_DELETE);
                        } else {
                            this.mTapCount = 0;
                        }
                        code = key.codes[this.mTapCount];
                    }
                    this.mKeyboardActionListener.onKey(code, codes);
                    this.mKeyboardActionListener.onRelease(code);
                }
                this.mLastSentIndex = index;
                this.mLastTapTime = eventTime;
            }
        }
    }

    private CharSequence getPreviewText(Key key) {
        if (!this.mInMultiTap) {
            return adjustCase(key.label);
        }
        int i = 0;
        this.mPreviewLabel.setLength(0);
        StringBuilder stringBuilder = this.mPreviewLabel;
        int[] iArr = key.codes;
        int i2 = this.mTapCount;
        if (i2 >= 0) {
            i = i2;
        }
        stringBuilder.append((char) iArr[i]);
        return adjustCase(this.mPreviewLabel);
    }

    private void showPreview(int keyIndex) {
        int oldKeyIndex = this.mCurrentKeyIndex;
        PopupWindow previewPopup = this.mPreviewPopup;
        this.mCurrentKeyIndex = keyIndex;
        Key[] keys = this.mKeys;
        int i = this.mCurrentKeyIndex;
        if (oldKeyIndex != i) {
            if (oldKeyIndex != -1 && keys.length > oldKeyIndex) {
                Key oldKey = keys[oldKeyIndex];
                oldKey.onReleased(i == -1);
                invalidateKey(oldKeyIndex);
                i = oldKey.codes[0];
                sendAccessibilityEventForUnicodeCharacter(256, i);
                sendAccessibilityEventForUnicodeCharacter(65536, i);
            }
            Key newKey = this.mCurrentKeyIndex;
            if (newKey != -1 && keys.length > newKey) {
                newKey = keys[newKey];
                newKey.onPressed();
                invalidateKey(this.mCurrentKeyIndex);
                int keyCode = newKey.codes[0];
                sendAccessibilityEventForUnicodeCharacter(128, keyCode);
                sendAccessibilityEventForUnicodeCharacter(32768, keyCode);
            }
        }
        if (oldKeyIndex != this.mCurrentKeyIndex && this.mShowPreview) {
            Handler handler;
            this.mHandler.removeMessages(1);
            if (previewPopup.isShowing() && keyIndex == -1) {
                handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(2), 70);
            }
            if (keyIndex == -1) {
                return;
            }
            if (previewPopup.isShowing() && this.mPreviewText.getVisibility() == 0) {
                showKey(keyIndex);
                return;
            }
            handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1, keyIndex, 0), 0);
        }
    }

    @UnsupportedAppUsage
    private void showKey(int keyIndex) {
        PopupWindow previewPopup = this.mPreviewPopup;
        Key[] keys = this.mKeys;
        if (keyIndex >= 0 && keyIndex < this.mKeys.length) {
            Key key = keys[keyIndex];
            if (key.icon != null) {
                this.mPreviewText.setCompoundDrawables(null, null, null, key.iconPreview != null ? key.iconPreview : key.icon);
                this.mPreviewText.setText(null);
            } else {
                this.mPreviewText.setCompoundDrawables(null, null, null, null);
                this.mPreviewText.setText(getPreviewText(key));
                if (key.label.length() <= 1 || key.codes.length >= 2) {
                    this.mPreviewText.setTextSize(0, (float) this.mPreviewTextSizeLarge);
                    this.mPreviewText.setTypeface(Typeface.DEFAULT);
                } else {
                    this.mPreviewText.setTextSize(0, (float) this.mKeyTextSize);
                    this.mPreviewText.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
            this.mPreviewText.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
            int popupWidth = Math.max(this.mPreviewText.getMeasuredWidth(), (key.width + this.mPreviewText.getPaddingLeft()) + this.mPreviewText.getPaddingRight());
            int popupHeight = this.mPreviewHeight;
            LayoutParams lp = this.mPreviewText.getLayoutParams();
            if (lp != null) {
                lp.width = popupWidth;
                lp.height = popupHeight;
            }
            if (this.mPreviewCentered) {
                this.mPopupPreviewX = 160 - (this.mPreviewText.getMeasuredWidth() / 2);
                this.mPopupPreviewY = -this.mPreviewText.getMeasuredHeight();
            } else {
                this.mPopupPreviewX = (key.x - this.mPreviewText.getPaddingLeft()) + this.mPaddingLeft;
                this.mPopupPreviewY = (key.y - popupHeight) + this.mPreviewOffset;
            }
            this.mHandler.removeMessages(2);
            getLocationInWindow(this.mCoordinates);
            int[] iArr = this.mCoordinates;
            iArr[0] = iArr[0] + this.mMiniKeyboardOffsetX;
            iArr[1] = iArr[1] + this.mMiniKeyboardOffsetY;
            this.mPreviewText.getBackground().setState(key.popupResId != 0 ? LONG_PRESSABLE_STATE_SET : EMPTY_STATE_SET);
            int i = this.mPopupPreviewX;
            int[] iArr2 = this.mCoordinates;
            this.mPopupPreviewX = i + iArr2[0];
            this.mPopupPreviewY += iArr2[1];
            getLocationOnScreen(iArr2);
            if (this.mPopupPreviewY + this.mCoordinates[1] < 0) {
                if (key.x + key.width <= getWidth() / 2) {
                    this.mPopupPreviewX += (int) (((double) key.width) * 2.5d);
                } else {
                    this.mPopupPreviewX -= (int) (((double) key.width) * 2.5d);
                }
                this.mPopupPreviewY += popupHeight;
            }
            if (previewPopup.isShowing()) {
                previewPopup.update(this.mPopupPreviewX, this.mPopupPreviewY, popupWidth, popupHeight);
            } else {
                previewPopup.setWidth(popupWidth);
                previewPopup.setHeight(popupHeight);
                previewPopup.showAtLocation(this.mPopupParent, 0, this.mPopupPreviewX, this.mPopupPreviewY);
            }
            this.mPreviewText.setVisibility(0);
        }
    }

    private void sendAccessibilityEventForUnicodeCharacter(int eventType, int code) {
        if (this.mAccessibilityManager.isEnabled()) {
            String text;
            AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
            onInitializeAccessibilityEvent(event);
            if (code != 10) {
                switch (code) {
                    case -6:
                        text = this.mContext.getString(R.string.keyboardview_keycode_alt);
                        break;
                    case -5:
                        text = this.mContext.getString(R.string.keyboardview_keycode_delete);
                        break;
                    case -4:
                        text = this.mContext.getString(R.string.keyboardview_keycode_done);
                        break;
                    case -3:
                        text = this.mContext.getString(R.string.keyboardview_keycode_cancel);
                        break;
                    case -2:
                        text = this.mContext.getString(R.string.keyboardview_keycode_mode_change);
                        break;
                    case -1:
                        text = this.mContext.getString(R.string.keyboardview_keycode_shift);
                        break;
                    default:
                        text = String.valueOf((char) code);
                        break;
                }
            }
            text = this.mContext.getString(R.string.keyboardview_keycode_enter);
            event.getText().add(text);
            this.mAccessibilityManager.sendAccessibilityEvent(event);
        }
    }

    public void invalidateAllKeys() {
        this.mDirtyRect.union(0, 0, getWidth(), getHeight());
        this.mDrawPending = true;
        invalidate();
    }

    public void invalidateKey(int keyIndex) {
        Key key = this.mKeys;
        if (key != null && keyIndex >= 0 && keyIndex < key.length) {
            key = key[keyIndex];
            this.mInvalidatedKey = key;
            this.mDirtyRect.union(key.x + this.mPaddingLeft, key.y + this.mPaddingTop, (key.x + key.width) + this.mPaddingLeft, (key.y + key.height) + this.mPaddingTop);
            onBufferDraw();
            invalidate(key.x + this.mPaddingLeft, key.y + this.mPaddingTop, (key.x + key.width) + this.mPaddingLeft, (key.y + key.height) + this.mPaddingTop);
        }
    }

    @UnsupportedAppUsage
    private boolean openPopupIfRequired(MotionEvent me) {
        if (this.mPopupLayout == 0) {
            return false;
        }
        Key popupKey = this.mCurrentKey;
        if (popupKey >= null) {
            Key[] keyArr = this.mKeys;
            if (popupKey < keyArr.length) {
                boolean result = onLongPress(keyArr[popupKey]);
                if (result) {
                    this.mAbortKey = true;
                    showPreview(-1);
                }
                return result;
            }
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    public boolean onLongPress(Key popupKey) {
        int popupKeyboardId = popupKey.popupResId;
        if (popupKeyboardId == 0) {
            return false;
        }
        this.mMiniKeyboardContainer = (View) this.mMiniKeyboardCache.get(popupKey);
        View view = this.mMiniKeyboardContainer;
        if (view == null) {
            Keyboard keyboard;
            this.mMiniKeyboardContainer = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.mPopupLayout, null);
            this.mMiniKeyboard = (KeyboardView) this.mMiniKeyboardContainer.findViewById(16908326);
            View closeButton = this.mMiniKeyboardContainer.findViewById(16908327);
            if (closeButton != null) {
                closeButton.setOnClickListener(this);
            }
            this.mMiniKeyboard.setOnKeyboardActionListener(new OnKeyboardActionListener() {
                public void onKey(int primaryCode, int[] keyCodes) {
                    KeyboardView.this.mKeyboardActionListener.onKey(primaryCode, keyCodes);
                    KeyboardView.this.dismissPopupKeyboard();
                }

                public void onText(CharSequence text) {
                    KeyboardView.this.mKeyboardActionListener.onText(text);
                    KeyboardView.this.dismissPopupKeyboard();
                }

                public void swipeLeft() {
                }

                public void swipeRight() {
                }

                public void swipeUp() {
                }

                public void swipeDown() {
                }

                public void onPress(int primaryCode) {
                    KeyboardView.this.mKeyboardActionListener.onPress(primaryCode);
                }

                public void onRelease(int primaryCode) {
                    KeyboardView.this.mKeyboardActionListener.onRelease(primaryCode);
                }
            });
            if (popupKey.popupCharacters != null) {
                keyboard = new Keyboard(getContext(), popupKeyboardId, popupKey.popupCharacters, -1, getPaddingLeft() + getPaddingRight());
            } else {
                keyboard = new Keyboard(getContext(), popupKeyboardId);
            }
            this.mMiniKeyboard.setKeyboard(keyboard);
            this.mMiniKeyboard.setPopupParent(this);
            this.mMiniKeyboardContainer.measure(MeasureSpec.makeMeasureSpec(getWidth(), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getHeight(), Integer.MIN_VALUE));
            this.mMiniKeyboardCache.put(popupKey, this.mMiniKeyboardContainer);
        } else {
            this.mMiniKeyboard = (KeyboardView) view.findViewById(16908326);
        }
        getLocationInWindow(this.mCoordinates);
        this.mPopupX = popupKey.x + this.mPaddingLeft;
        this.mPopupY = popupKey.y + this.mPaddingTop;
        this.mPopupX = (this.mPopupX + popupKey.width) - this.mMiniKeyboardContainer.getMeasuredWidth();
        this.mPopupY -= this.mMiniKeyboardContainer.getMeasuredHeight();
        int x = (this.mPopupX + this.mMiniKeyboardContainer.getPaddingRight()) + this.mCoordinates[0];
        int y = (this.mPopupY + this.mMiniKeyboardContainer.getPaddingBottom()) + this.mCoordinates[1];
        this.mMiniKeyboard.setPopupOffset(x < 0 ? 0 : x, y);
        this.mMiniKeyboard.setShifted(isShifted());
        this.mPopupKeyboard.setContentView(this.mMiniKeyboardContainer);
        this.mPopupKeyboard.setWidth(this.mMiniKeyboardContainer.getMeasuredWidth());
        this.mPopupKeyboard.setHeight(this.mMiniKeyboardContainer.getMeasuredHeight());
        this.mPopupKeyboard.showAtLocation((View) this, 0, x, y);
        this.mMiniKeyboardOnScreen = true;
        invalidateAllKeys();
        return true;
    }

    public boolean onHoverEvent(MotionEvent event) {
        if (!this.mAccessibilityManager.isTouchExplorationEnabled() || event.getPointerCount() != 1) {
            return true;
        }
        int action = event.getAction();
        if (action == 7) {
            event.setAction(2);
        } else if (action == 9) {
            event.setAction(0);
        } else if (action == 10) {
            event.setAction(1);
        }
        return onTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent me) {
        boolean result;
        MotionEvent motionEvent = me;
        int pointerCount = me.getPointerCount();
        int action = me.getAction();
        long now = me.getEventTime();
        if (pointerCount != this.mOldPointerCount) {
            MotionEvent down;
            if (pointerCount == 1) {
                down = MotionEvent.obtain(now, now, 0, me.getX(), me.getY(), me.getMetaState());
                result = onModifiedTouchEvent(down, false);
                down.recycle();
                if (action == 1) {
                    result = onModifiedTouchEvent(motionEvent, true);
                }
            } else {
                boolean z = true;
                down = MotionEvent.obtain(now, now, 1, this.mOldPointerX, this.mOldPointerY, me.getMetaState());
                result = onModifiedTouchEvent(down, z);
                down.recycle();
            }
        } else if (pointerCount == 1) {
            result = onModifiedTouchEvent(motionEvent, false);
            this.mOldPointerX = me.getX();
            this.mOldPointerY = me.getY();
        } else {
            result = true;
        }
        this.mOldPointerCount = pointerCount;
        return result;
    }

    private boolean onModifiedTouchEvent(MotionEvent me, boolean possiblePoly) {
        MotionEvent motionEvent = me;
        int touchX = ((int) me.getX()) - this.mPaddingLeft;
        int touchY = ((int) me.getY()) - this.mPaddingTop;
        int i = this.mVerticalCorrection;
        if (touchY >= (-i)) {
            touchY += i;
        }
        int action = me.getAction();
        long eventTime = me.getEventTime();
        int keyIndex = getKeyIndices(touchX, touchY, null);
        this.mPossiblePoly = possiblePoly;
        if (action == 0) {
            this.mSwipeTracker.clear();
        }
        this.mSwipeTracker.addMovement(motionEvent);
        if (this.mAbortKey && action != 0 && action != 3) {
            return true;
        }
        if (this.mGestureDetector.onTouchEvent(motionEvent)) {
            showPreview(-1);
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(4);
            return true;
        } else if (this.mMiniKeyboardOnScreen && action != 3) {
            return true;
        } else {
            int i2;
            int touchY2;
            if (action == 0) {
                i2 = 0;
                this.mAbortKey = false;
                this.mStartX = touchX;
                this.mStartY = touchY;
                this.mLastCodeX = touchX;
                this.mLastCodeY = touchY;
                this.mLastKeyTime = 0;
                this.mCurrentKeyTime = 0;
                this.mLastKey = -1;
                this.mCurrentKey = keyIndex;
                this.mDownKey = keyIndex;
                this.mDownTime = me.getEventTime();
                this.mLastMoveTime = this.mDownTime;
                checkMultiTap(eventTime, keyIndex);
                OnKeyboardActionListener onKeyboardActionListener = this.mKeyboardActionListener;
                if (keyIndex != -1) {
                    i2 = this.mKeys[keyIndex].codes[0];
                }
                onKeyboardActionListener.onPress(i2);
                int i3 = this.mCurrentKey;
                if (i3 < 0 || !this.mKeys[i3].repeatable) {
                    i3 = -1;
                } else {
                    this.mRepeatKeyIndex = this.mCurrentKey;
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(3), 400);
                    repeatKey();
                    if (this.mAbortKey) {
                        this.mRepeatKeyIndex = -1;
                    } else {
                        i3 = -1;
                    }
                }
                if (this.mCurrentKey != i3) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(4, motionEvent), (long) LONGPRESS_TIMEOUT);
                }
                showPreview(keyIndex);
            } else if (action == 1) {
                removeMessages();
                if (keyIndex == this.mCurrentKey) {
                    this.mCurrentKeyTime += eventTime - this.mLastMoveTime;
                } else {
                    resetMultiTap();
                    this.mLastKey = this.mCurrentKey;
                    this.mLastKeyTime = (this.mCurrentKeyTime + eventTime) - this.mLastMoveTime;
                    this.mCurrentKey = keyIndex;
                    this.mCurrentKeyTime = 0;
                }
                long j = this.mCurrentKeyTime;
                if (j < this.mLastKeyTime && j < 70) {
                    i = this.mLastKey;
                    if (i != -1) {
                        this.mCurrentKey = i;
                        i2 = this.mLastCodeX;
                        touchY2 = this.mLastCodeY;
                        showPreview(-1);
                        Arrays.fill(this.mKeyIndices, -1);
                        if (!(this.mRepeatKeyIndex != -1 || this.mMiniKeyboardOnScreen || this.mAbortKey)) {
                            detectAndSendKey(this.mCurrentKey, i2, touchY2, eventTime);
                        }
                        invalidateKey(keyIndex);
                        this.mRepeatKeyIndex = -1;
                        this.mLastX = i2;
                        this.mLastY = touchY2;
                        return true;
                    }
                }
                i2 = touchX;
                touchY2 = touchY;
                showPreview(-1);
                Arrays.fill(this.mKeyIndices, -1);
                detectAndSendKey(this.mCurrentKey, i2, touchY2, eventTime);
                invalidateKey(keyIndex);
                this.mRepeatKeyIndex = -1;
                this.mLastX = i2;
                this.mLastY = touchY2;
                return true;
            } else if (action == 2) {
                boolean continueLongPress;
                if (keyIndex != -1) {
                    int i4 = this.mCurrentKey;
                    if (i4 == -1) {
                        this.mCurrentKey = keyIndex;
                        this.mCurrentKeyTime = eventTime - this.mDownTime;
                        continueLongPress = false;
                    } else if (keyIndex == i4) {
                        continueLongPress = false;
                        this.mCurrentKeyTime += eventTime - this.mLastMoveTime;
                        continueLongPress = true;
                    } else {
                        continueLongPress = false;
                        if (this.mRepeatKeyIndex == -1) {
                            resetMultiTap();
                            this.mLastKey = this.mCurrentKey;
                            this.mLastCodeX = this.mLastX;
                            this.mLastCodeY = this.mLastY;
                            this.mLastKeyTime = (this.mCurrentKeyTime + eventTime) - this.mLastMoveTime;
                            this.mCurrentKey = keyIndex;
                            this.mCurrentKeyTime = 0;
                        }
                    }
                } else {
                    continueLongPress = false;
                }
                if (!continueLongPress) {
                    this.mHandler.removeMessages(4);
                    if (keyIndex != -1) {
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(4, motionEvent), (long) LONGPRESS_TIMEOUT);
                    }
                }
                showPreview(this.mCurrentKey);
                this.mLastMoveTime = eventTime;
            } else if (action == 3) {
                removeMessages();
                dismissPopupKeyboard();
                this.mAbortKey = true;
                showPreview(-1);
                invalidateKey(this.mCurrentKey);
            }
            i2 = touchX;
            touchY2 = touchY;
            this.mLastX = i2;
            this.mLastY = touchY2;
            return true;
        }
    }

    @UnsupportedAppUsage
    private boolean repeatKey() {
        Key key = this.mKeys[this.mRepeatKeyIndex];
        detectAndSendKey(this.mCurrentKey, key.x, key.y, this.mLastTapTime);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void swipeRight() {
        this.mKeyboardActionListener.swipeRight();
    }

    /* Access modifiers changed, original: protected */
    public void swipeLeft() {
        this.mKeyboardActionListener.swipeLeft();
    }

    /* Access modifiers changed, original: protected */
    public void swipeUp() {
        this.mKeyboardActionListener.swipeUp();
    }

    /* Access modifiers changed, original: protected */
    public void swipeDown() {
        this.mKeyboardActionListener.swipeDown();
    }

    public void closing() {
        if (this.mPreviewPopup.isShowing()) {
            this.mPreviewPopup.dismiss();
        }
        removeMessages();
        dismissPopupKeyboard();
        this.mBuffer = null;
        this.mCanvas = null;
        this.mMiniKeyboardCache.clear();
    }

    private void removeMessages() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(3);
            this.mHandler.removeMessages(4);
            this.mHandler.removeMessages(1);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closing();
    }

    private void dismissPopupKeyboard() {
        if (this.mPopupKeyboard.isShowing()) {
            this.mPopupKeyboard.dismiss();
            this.mMiniKeyboardOnScreen = false;
            invalidateAllKeys();
        }
    }

    public boolean handleBack() {
        if (!this.mPopupKeyboard.isShowing()) {
            return false;
        }
        dismissPopupKeyboard();
        return true;
    }

    private void resetMultiTap() {
        this.mLastSentIndex = -1;
        this.mTapCount = 0;
        this.mLastTapTime = -1;
        this.mInMultiTap = false;
    }

    private void checkMultiTap(long eventTime, int keyIndex) {
        if (keyIndex != -1) {
            Key key = this.mKeys[keyIndex];
            if (key.codes.length > 1) {
                this.mInMultiTap = true;
                if (eventTime >= this.mLastTapTime + 800 || keyIndex != this.mLastSentIndex) {
                    this.mTapCount = -1;
                    return;
                } else {
                    this.mTapCount = (this.mTapCount + 1) % key.codes.length;
                    return;
                }
            }
            if (eventTime > this.mLastTapTime + 800 || keyIndex != this.mLastSentIndex) {
                resetMultiTap();
            }
        }
    }
}
