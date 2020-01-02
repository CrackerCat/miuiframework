package android.graphics.drawable;

import android.annotation.UnsupportedAppUsage;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.SystemClock;
import android.util.SparseArray;

public class DrawableContainer extends Drawable implements Callback {
    private static final boolean DEBUG = false;
    private static final boolean DEFAULT_DITHER = true;
    private static final String TAG = "DrawableContainer";
    private int mAlpha = 255;
    private Runnable mAnimationRunnable;
    private BlockInvalidateCallback mBlockInvalidateCallback;
    private int mCurIndex = -1;
    private Drawable mCurrDrawable;
    @UnsupportedAppUsage
    private DrawableContainerState mDrawableContainerState;
    private long mEnterAnimationEnd;
    private long mExitAnimationEnd;
    private boolean mHasAlpha;
    private Rect mHotspotBounds;
    @UnsupportedAppUsage
    private Drawable mLastDrawable;
    private int mLastIndex = -1;
    private boolean mMutated;

    public static abstract class DrawableContainerState extends ConstantState {
        boolean mAutoMirrored;
        BlendMode mBlendMode;
        boolean mCanConstantState;
        int mChangingConfigurations;
        boolean mCheckedConstantSize;
        boolean mCheckedConstantState;
        boolean mCheckedOpacity;
        boolean mCheckedPadding;
        boolean mCheckedStateful;
        int mChildrenChangingConfigurations;
        ColorFilter mColorFilter;
        int mConstantHeight;
        int mConstantMinimumHeight;
        int mConstantMinimumWidth;
        @UnsupportedAppUsage
        Rect mConstantPadding;
        boolean mConstantSize = false;
        int mConstantWidth;
        int mDensity = 160;
        boolean mDither = true;
        SparseArray<ConstantState> mDrawableFutures;
        @UnsupportedAppUsage
        Drawable[] mDrawables;
        int mEnterFadeDuration = 0;
        int mExitFadeDuration = 0;
        @UnsupportedAppUsage
        boolean mHasColorFilter;
        boolean mHasTintList;
        boolean mHasTintMode;
        int mLayoutDirection;
        boolean mMutated;
        int mNumChildren;
        int mOpacity;
        final DrawableContainer mOwner;
        Resources mSourceRes;
        boolean mStateful;
        ColorStateList mTintList;
        boolean mVariablePadding = false;

        @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
        protected DrawableContainerState(DrawableContainerState orig, DrawableContainer owner, Resources res) {
            this.mOwner = owner;
            Resources resources = res != null ? res : orig != null ? orig.mSourceRes : null;
            this.mSourceRes = resources;
            this.mDensity = Drawable.resolveDensity(res, orig != null ? orig.mDensity : 0);
            if (orig != null) {
                this.mChangingConfigurations = orig.mChangingConfigurations;
                this.mChildrenChangingConfigurations = orig.mChildrenChangingConfigurations;
                this.mCheckedConstantState = true;
                this.mCanConstantState = true;
                this.mVariablePadding = orig.mVariablePadding;
                this.mConstantSize = orig.mConstantSize;
                this.mDither = orig.mDither;
                this.mMutated = orig.mMutated;
                this.mLayoutDirection = orig.mLayoutDirection;
                this.mEnterFadeDuration = orig.mEnterFadeDuration;
                this.mExitFadeDuration = orig.mExitFadeDuration;
                this.mAutoMirrored = orig.mAutoMirrored;
                this.mColorFilter = orig.mColorFilter;
                this.mHasColorFilter = orig.mHasColorFilter;
                this.mTintList = orig.mTintList;
                this.mBlendMode = orig.mBlendMode;
                this.mHasTintList = orig.mHasTintList;
                this.mHasTintMode = orig.mHasTintMode;
                if (orig.mDensity == this.mDensity) {
                    if (orig.mCheckedPadding) {
                        this.mConstantPadding = new Rect(orig.mConstantPadding);
                        this.mCheckedPadding = true;
                    }
                    if (orig.mCheckedConstantSize) {
                        this.mConstantWidth = orig.mConstantWidth;
                        this.mConstantHeight = orig.mConstantHeight;
                        this.mConstantMinimumWidth = orig.mConstantMinimumWidth;
                        this.mConstantMinimumHeight = orig.mConstantMinimumHeight;
                        this.mCheckedConstantSize = true;
                    }
                }
                if (orig.mCheckedOpacity) {
                    this.mOpacity = orig.mOpacity;
                    this.mCheckedOpacity = true;
                }
                if (orig.mCheckedStateful) {
                    this.mStateful = orig.mStateful;
                    this.mCheckedStateful = true;
                }
                Drawable[] origDr = orig.mDrawables;
                this.mDrawables = new Drawable[origDr.length];
                this.mNumChildren = orig.mNumChildren;
                SparseArray<ConstantState> origDf = orig.mDrawableFutures;
                if (origDf != null) {
                    this.mDrawableFutures = origDf.clone();
                } else {
                    this.mDrawableFutures = new SparseArray(this.mNumChildren);
                }
                int N = this.mNumChildren;
                for (int i = 0; i < N; i++) {
                    if (origDr[i] != null) {
                        ConstantState cs = origDr[i].getConstantState();
                        if (cs != null) {
                            this.mDrawableFutures.put(i, cs);
                        } else {
                            this.mDrawables[i] = origDr[i];
                        }
                    }
                }
                return;
            }
            this.mDrawables = new Drawable[10];
            this.mNumChildren = 0;
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations | this.mChildrenChangingConfigurations;
        }

        public final int addChild(Drawable dr) {
            int pos = this.mNumChildren;
            if (pos >= this.mDrawables.length) {
                growArray(pos, pos + 10);
            }
            dr.mutate();
            dr.setVisible(false, true);
            dr.setCallback(this.mOwner);
            this.mDrawables[pos] = dr;
            this.mNumChildren++;
            this.mChildrenChangingConfigurations |= dr.getChangingConfigurations();
            invalidateCache();
            this.mConstantPadding = null;
            this.mCheckedPadding = false;
            this.mCheckedConstantSize = false;
            this.mCheckedConstantState = false;
            return pos;
        }

        /* Access modifiers changed, original: 0000 */
        public void invalidateCache() {
            this.mCheckedOpacity = false;
            this.mCheckedStateful = false;
        }

        /* Access modifiers changed, original: final */
        public final int getCapacity() {
            return this.mDrawables.length;
        }

        private void createAllFutures() {
            int futureCount = this.mDrawableFutures;
            if (futureCount != 0) {
                futureCount = futureCount.size();
                for (int keyIndex = 0; keyIndex < futureCount; keyIndex++) {
                    this.mDrawables[this.mDrawableFutures.keyAt(keyIndex)] = prepareDrawable(((ConstantState) this.mDrawableFutures.valueAt(keyIndex)).newDrawable(this.mSourceRes));
                }
                this.mDrawableFutures = null;
            }
        }

        private Drawable prepareDrawable(Drawable child) {
            child.setLayoutDirection(this.mLayoutDirection);
            child = child.mutate();
            child.setCallback(this.mOwner);
            return child;
        }

        public final int getChildCount() {
            return this.mNumChildren;
        }

        public final Drawable[] getChildren() {
            createAllFutures();
            return this.mDrawables;
        }

        public final Drawable getChild(int index) {
            Drawable result = this.mDrawables[index];
            if (result != null) {
                return result;
            }
            int keyIndex = this.mDrawableFutures;
            if (keyIndex != 0) {
                keyIndex = keyIndex.indexOfKey(index);
                if (keyIndex >= 0) {
                    Drawable prepared = prepareDrawable(((ConstantState) this.mDrawableFutures.valueAt(keyIndex)).newDrawable(this.mSourceRes));
                    this.mDrawables[index] = prepared;
                    this.mDrawableFutures.removeAt(keyIndex);
                    if (this.mDrawableFutures.size() == 0) {
                        this.mDrawableFutures = null;
                    }
                    return prepared;
                }
            }
            return null;
        }

        /* Access modifiers changed, original: final */
        public final boolean setLayoutDirection(int layoutDirection, int currentIndex) {
            boolean changed = false;
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i] != null) {
                    boolean childChanged = drawables[i].setLayoutDirection(layoutDirection);
                    if (i == currentIndex) {
                        changed = childChanged;
                    }
                }
            }
            this.mLayoutDirection = layoutDirection;
            return changed;
        }

        /* Access modifiers changed, original: final */
        public final void updateDensity(Resources res) {
            if (res != null) {
                this.mSourceRes = res;
                int targetDensity = Drawable.resolveDensity(res, this.mDensity);
                int sourceDensity = this.mDensity;
                this.mDensity = targetDensity;
                if (sourceDensity != targetDensity) {
                    this.mCheckedConstantSize = false;
                    this.mCheckedPadding = false;
                }
            }
        }

        /* Access modifiers changed, original: final */
        public final void applyTheme(Theme theme) {
            if (theme != null) {
                createAllFutures();
                int N = this.mNumChildren;
                Drawable[] drawables = this.mDrawables;
                int i = 0;
                while (i < N) {
                    if (drawables[i] != null && drawables[i].canApplyTheme()) {
                        drawables[i].applyTheme(theme);
                        this.mChildrenChangingConfigurations |= drawables[i].getChangingConfigurations();
                    }
                    i++;
                }
                updateDensity(theme.getResources());
            }
        }

        public boolean canApplyTheme() {
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                Drawable d = drawables[i];
                if (d == null) {
                    ConstantState future = (ConstantState) this.mDrawableFutures.get(i);
                    if (future != null && future.canApplyTheme()) {
                        return true;
                    }
                } else if (d.canApplyTheme()) {
                    return true;
                }
            }
            return false;
        }

        private void mutate() {
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i] != null) {
                    drawables[i].mutate();
                }
            }
            this.mMutated = true;
        }

        /* Access modifiers changed, original: final */
        public final void clearMutated() {
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i] != null) {
                    drawables[i].clearMutated();
                }
            }
            this.mMutated = false;
        }

        public final void setVariablePadding(boolean variable) {
            this.mVariablePadding = variable;
        }

        public final Rect getConstantPadding() {
            if (this.mVariablePadding) {
                return null;
            }
            if (this.mConstantPadding != null || this.mCheckedPadding) {
                return this.mConstantPadding;
            }
            createAllFutures();
            Rect r = null;
            Rect t = new Rect();
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i].getPadding(t)) {
                    if (r == null) {
                        r = new Rect(0, 0, 0, 0);
                    }
                    if (t.left > r.left) {
                        r.left = t.left;
                    }
                    if (t.top > r.top) {
                        r.top = t.top;
                    }
                    if (t.right > r.right) {
                        r.right = t.right;
                    }
                    if (t.bottom > r.bottom) {
                        r.bottom = t.bottom;
                    }
                }
            }
            this.mCheckedPadding = true;
            this.mConstantPadding = r;
            return r;
        }

        public final void setConstantSize(boolean constant) {
            this.mConstantSize = constant;
        }

        public final boolean isConstantSize() {
            return this.mConstantSize;
        }

        public final int getConstantWidth() {
            if (!this.mCheckedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantWidth;
        }

        public final int getConstantHeight() {
            if (!this.mCheckedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantHeight;
        }

        public final int getConstantMinimumWidth() {
            if (!this.mCheckedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantMinimumWidth;
        }

        public final int getConstantMinimumHeight() {
            if (!this.mCheckedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantMinimumHeight;
        }

        /* Access modifiers changed, original: protected */
        public void computeConstantSize() {
            this.mCheckedConstantSize = true;
            createAllFutures();
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            this.mConstantHeight = -1;
            this.mConstantWidth = -1;
            this.mConstantMinimumHeight = 0;
            this.mConstantMinimumWidth = 0;
            for (int i = 0; i < N; i++) {
                Drawable dr = drawables[i];
                int s = dr.getIntrinsicWidth();
                if (s > this.mConstantWidth) {
                    this.mConstantWidth = s;
                }
                s = dr.getIntrinsicHeight();
                if (s > this.mConstantHeight) {
                    this.mConstantHeight = s;
                }
                s = dr.getMinimumWidth();
                if (s > this.mConstantMinimumWidth) {
                    this.mConstantMinimumWidth = s;
                }
                s = dr.getMinimumHeight();
                if (s > this.mConstantMinimumHeight) {
                    this.mConstantMinimumHeight = s;
                }
            }
        }

        public final void setEnterFadeDuration(int duration) {
            this.mEnterFadeDuration = duration;
        }

        public final int getEnterFadeDuration() {
            return this.mEnterFadeDuration;
        }

        public final void setExitFadeDuration(int duration) {
            this.mExitFadeDuration = duration;
        }

        public final int getExitFadeDuration() {
            return this.mExitFadeDuration;
        }

        public final int getOpacity() {
            if (this.mCheckedOpacity) {
                return this.mOpacity;
            }
            createAllFutures();
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            int op = N > 0 ? drawables[0].getOpacity() : -2;
            for (int i = 1; i < N; i++) {
                op = Drawable.resolveOpacity(op, drawables[i].getOpacity());
            }
            this.mOpacity = op;
            this.mCheckedOpacity = true;
            return op;
        }

        public final boolean isStateful() {
            if (this.mCheckedStateful) {
                return this.mStateful;
            }
            createAllFutures();
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            boolean isStateful = false;
            for (int i = 0; i < N; i++) {
                if (drawables[i].isStateful()) {
                    isStateful = true;
                    break;
                }
            }
            this.mStateful = isStateful;
            this.mCheckedStateful = true;
            return isStateful;
        }

        public void growArray(int oldSize, int newSize) {
            Drawable[] newDrawables = new Drawable[newSize];
            System.arraycopy(this.mDrawables, 0, newDrawables, 0, oldSize);
            this.mDrawables = newDrawables;
        }

        public synchronized boolean canConstantState() {
            if (this.mCheckedConstantState) {
                return this.mCanConstantState;
            }
            createAllFutures();
            this.mCheckedConstantState = true;
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i].getConstantState() == null) {
                    this.mCanConstantState = false;
                    return false;
                }
            }
            this.mCanConstantState = true;
            return true;
        }
    }

    private static class BlockInvalidateCallback implements Callback {
        private Callback mCallback;

        private BlockInvalidateCallback() {
        }

        /* synthetic */ BlockInvalidateCallback(AnonymousClass1 x0) {
            this();
        }

        public BlockInvalidateCallback wrap(Callback callback) {
            this.mCallback = callback;
            return this;
        }

        public Callback unwrap() {
            Callback callback = this.mCallback;
            this.mCallback = null;
            return callback;
        }

        public void invalidateDrawable(Drawable who) {
        }

        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            Callback callback = this.mCallback;
            if (callback != null) {
                callback.scheduleDrawable(who, what, when);
            }
        }

        public void unscheduleDrawable(Drawable who, Runnable what) {
            Callback callback = this.mCallback;
            if (callback != null) {
                callback.unscheduleDrawable(who, what);
            }
        }
    }

    public void draw(Canvas canvas) {
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        drawable = this.mLastDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mDrawableContainerState.getChangingConfigurations();
    }

    private boolean needsMirroring() {
        return isAutoMirrored() && getLayoutDirection() == 1;
    }

    public boolean getPadding(Rect padding) {
        boolean result;
        Rect r = this.mDrawableContainerState.getConstantPadding();
        if (r != null) {
            padding.set(r);
            result = (((r.left | r.top) | r.bottom) | r.right) != 0;
        } else {
            Drawable drawable = this.mCurrDrawable;
            result = drawable != null ? drawable.getPadding(padding) : super.getPadding(padding);
        }
        if (needsMirroring()) {
            int left = padding.left;
            padding.left = padding.right;
            padding.right = left;
        }
        return result;
    }

    public Insets getOpticalInsets() {
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            return drawable.getOpticalInsets();
        }
        return Insets.NONE;
    }

    public void getOutline(Outline outline) {
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.getOutline(outline);
        }
    }

    public void setAlpha(int alpha) {
        if (!this.mHasAlpha || this.mAlpha != alpha) {
            this.mHasAlpha = true;
            this.mAlpha = alpha;
            Drawable drawable = this.mCurrDrawable;
            if (drawable == null) {
                return;
            }
            if (this.mEnterAnimationEnd == 0) {
                drawable.setAlpha(alpha);
            } else {
                animate(false);
            }
        }
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public void setDither(boolean dither) {
        if (this.mDrawableContainerState.mDither != dither) {
            DrawableContainerState drawableContainerState = this.mDrawableContainerState;
            drawableContainerState.mDither = dither;
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                drawable.setDither(drawableContainerState.mDither);
            }
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        DrawableContainerState drawableContainerState = this.mDrawableContainerState;
        drawableContainerState.mHasColorFilter = true;
        if (drawableContainerState.mColorFilter != colorFilter) {
            this.mDrawableContainerState.mColorFilter = colorFilter;
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                drawable.setColorFilter(colorFilter);
            }
        }
    }

    public void setTintList(ColorStateList tint) {
        DrawableContainerState drawableContainerState = this.mDrawableContainerState;
        drawableContainerState.mHasTintList = true;
        if (drawableContainerState.mTintList != tint) {
            this.mDrawableContainerState.mTintList = tint;
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                drawable.setTintList(tint);
            }
        }
    }

    public void setTintBlendMode(BlendMode blendMode) {
        DrawableContainerState drawableContainerState = this.mDrawableContainerState;
        drawableContainerState.mHasTintMode = true;
        if (drawableContainerState.mBlendMode != blendMode) {
            this.mDrawableContainerState.mBlendMode = blendMode;
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                drawable.setTintBlendMode(blendMode);
            }
        }
    }

    public void setEnterFadeDuration(int ms) {
        this.mDrawableContainerState.mEnterFadeDuration = ms;
    }

    public void setExitFadeDuration(int ms) {
        this.mDrawableContainerState.mExitFadeDuration = ms;
    }

    /* Access modifiers changed, original: protected */
    public void onBoundsChange(Rect bounds) {
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            drawable.setBounds(bounds);
        }
        drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.setBounds(bounds);
        }
    }

    public boolean isStateful() {
        return this.mDrawableContainerState.isStateful();
    }

    public boolean hasFocusStateSpecified() {
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            return drawable.hasFocusStateSpecified();
        }
        drawable = this.mLastDrawable;
        if (drawable != null) {
            return drawable.hasFocusStateSpecified();
        }
        return false;
    }

    public void setAutoMirrored(boolean mirrored) {
        if (this.mDrawableContainerState.mAutoMirrored != mirrored) {
            DrawableContainerState drawableContainerState = this.mDrawableContainerState;
            drawableContainerState.mAutoMirrored = mirrored;
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                drawable.setAutoMirrored(drawableContainerState.mAutoMirrored);
            }
        }
    }

    public boolean isAutoMirrored() {
        return this.mDrawableContainerState.mAutoMirrored;
    }

    public void jumpToCurrentState() {
        boolean changed = false;
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
            this.mLastDrawable = null;
            this.mLastIndex = -1;
            changed = true;
        }
        drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
            if (this.mHasAlpha) {
                this.mCurrDrawable.setAlpha(this.mAlpha);
            }
        }
        if (this.mExitAnimationEnd != 0) {
            this.mExitAnimationEnd = 0;
            changed = true;
        }
        if (this.mEnterAnimationEnd != 0) {
            this.mEnterAnimationEnd = 0;
            changed = true;
        }
        if (changed) {
            invalidateSelf();
        }
    }

    public void setHotspot(float x, float y) {
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.setHotspot(x, y);
        }
    }

    public void setHotspotBounds(int left, int top, int right, int bottom) {
        Rect rect = this.mHotspotBounds;
        if (rect == null) {
            this.mHotspotBounds = new Rect(left, top, right, bottom);
        } else {
            rect.set(left, top, right, bottom);
        }
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.setHotspotBounds(left, top, right, bottom);
        }
    }

    public void getHotspotBounds(Rect outRect) {
        Rect rect = this.mHotspotBounds;
        if (rect != null) {
            outRect.set(rect);
        } else {
            super.getHotspotBounds(outRect);
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean onStateChange(int[] state) {
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            return drawable.setState(state);
        }
        drawable = this.mCurrDrawable;
        if (drawable != null) {
            return drawable.setState(state);
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    public boolean onLevelChange(int level) {
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            return drawable.setLevel(level);
        }
        drawable = this.mCurrDrawable;
        if (drawable != null) {
            return drawable.setLevel(level);
        }
        return false;
    }

    public boolean onLayoutDirectionChanged(int layoutDirection) {
        return this.mDrawableContainerState.setLayoutDirection(layoutDirection, getCurrentIndex());
    }

    public int getIntrinsicWidth() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantWidth();
        }
        Drawable drawable = this.mCurrDrawable;
        return drawable != null ? drawable.getIntrinsicWidth() : -1;
    }

    public int getIntrinsicHeight() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantHeight();
        }
        Drawable drawable = this.mCurrDrawable;
        return drawable != null ? drawable.getIntrinsicHeight() : -1;
    }

    public int getMinimumWidth() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantMinimumWidth();
        }
        Drawable drawable = this.mCurrDrawable;
        return drawable != null ? drawable.getMinimumWidth() : 0;
    }

    public int getMinimumHeight() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantMinimumHeight();
        }
        Drawable drawable = this.mCurrDrawable;
        return drawable != null ? drawable.getMinimumHeight() : 0;
    }

    public void invalidateDrawable(Drawable who) {
        DrawableContainerState drawableContainerState = this.mDrawableContainerState;
        if (drawableContainerState != null) {
            drawableContainerState.invalidateCache();
        }
        if (who == this.mCurrDrawable && getCallback() != null) {
            getCallback().invalidateDrawable(this);
        }
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (who == this.mCurrDrawable && getCallback() != null) {
            getCallback().scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (who == this.mCurrDrawable && getCallback() != null) {
            getCallback().unscheduleDrawable(this, what);
        }
    }

    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        Drawable drawable = this.mLastDrawable;
        if (drawable != null) {
            drawable.setVisible(visible, restart);
        }
        drawable = this.mCurrDrawable;
        if (drawable != null) {
            drawable.setVisible(visible, restart);
        }
        return changed;
    }

    public int getOpacity() {
        Drawable drawable = this.mCurrDrawable;
        if (drawable == null || !drawable.isVisible()) {
            return -2;
        }
        return this.mDrawableContainerState.getOpacity();
    }

    public void setCurrentIndex(int index) {
        selectDrawable(index);
    }

    public int getCurrentIndex() {
        return this.mCurIndex;
    }

    public boolean selectDrawable(int index) {
        if (index == this.mCurIndex) {
            return false;
        }
        Drawable drawable;
        long now = SystemClock.uptimeMillis();
        if (this.mDrawableContainerState.mExitFadeDuration > 0) {
            drawable = this.mLastDrawable;
            if (drawable != null) {
                drawable.setVisible(false, false);
            }
            drawable = this.mCurrDrawable;
            if (drawable != null) {
                this.mLastDrawable = drawable;
                this.mLastIndex = this.mCurIndex;
                this.mExitAnimationEnd = ((long) this.mDrawableContainerState.mExitFadeDuration) + now;
            } else {
                this.mLastDrawable = null;
                this.mLastIndex = -1;
                this.mExitAnimationEnd = 0;
            }
        } else {
            drawable = this.mCurrDrawable;
            if (drawable != null) {
                drawable.setVisible(false, false);
            }
        }
        if (index < 0 || index >= this.mDrawableContainerState.mNumChildren) {
            this.mCurrDrawable = null;
            this.mCurIndex = -1;
        } else {
            drawable = this.mDrawableContainerState.getChild(index);
            this.mCurrDrawable = drawable;
            this.mCurIndex = index;
            if (drawable != null) {
                if (this.mDrawableContainerState.mEnterFadeDuration > 0) {
                    this.mEnterAnimationEnd = ((long) this.mDrawableContainerState.mEnterFadeDuration) + now;
                }
                initializeDrawableForDisplay(drawable);
            }
        }
        if (!(this.mEnterAnimationEnd == 0 && this.mExitAnimationEnd == 0)) {
            Runnable runnable = this.mAnimationRunnable;
            if (runnable == null) {
                this.mAnimationRunnable = new Runnable() {
                    public void run() {
                        DrawableContainer.this.animate(true);
                        DrawableContainer.this.invalidateSelf();
                    }
                };
            } else {
                unscheduleSelf(runnable);
            }
            animate(true);
        }
        invalidateSelf();
        return true;
    }

    private void initializeDrawableForDisplay(Drawable d) {
        if (this.mBlockInvalidateCallback == null) {
            this.mBlockInvalidateCallback = new BlockInvalidateCallback();
        }
        d.setCallback(this.mBlockInvalidateCallback.wrap(d.getCallback()));
        try {
            if (this.mDrawableContainerState.mEnterFadeDuration <= 0 && this.mHasAlpha) {
                d.setAlpha(this.mAlpha);
            }
            if (this.mDrawableContainerState.mHasColorFilter) {
                d.setColorFilter(this.mDrawableContainerState.mColorFilter);
            } else {
                if (this.mDrawableContainerState.mHasTintList) {
                    d.setTintList(this.mDrawableContainerState.mTintList);
                }
                if (this.mDrawableContainerState.mHasTintMode) {
                    d.setTintBlendMode(this.mDrawableContainerState.mBlendMode);
                }
            }
            d.setVisible(isVisible(), true);
            d.setDither(this.mDrawableContainerState.mDither);
            d.setState(getState());
            d.setLevel(getLevel());
            d.setBounds(getBounds());
            d.setLayoutDirection(getLayoutDirection());
            d.setAutoMirrored(this.mDrawableContainerState.mAutoMirrored);
            Rect hotspotBounds = this.mHotspotBounds;
            if (hotspotBounds != null) {
                d.setHotspotBounds(hotspotBounds.left, hotspotBounds.top, hotspotBounds.right, hotspotBounds.bottom);
            }
            d.setCallback(this.mBlockInvalidateCallback.unwrap());
        } catch (Throwable th) {
            d.setCallback(this.mBlockInvalidateCallback.unwrap());
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void animate(boolean schedule) {
        long j;
        this.mHasAlpha = true;
        long now = SystemClock.uptimeMillis();
        boolean animating = false;
        Drawable drawable = this.mCurrDrawable;
        if (drawable != null) {
            j = this.mEnterAnimationEnd;
            if (j != 0) {
                if (j <= now) {
                    drawable.setAlpha(this.mAlpha);
                    this.mEnterAnimationEnd = 0;
                } else {
                    this.mCurrDrawable.setAlpha(((255 - (((int) ((j - now) * 255)) / this.mDrawableContainerState.mEnterFadeDuration)) * this.mAlpha) / 255);
                    animating = true;
                }
            }
        } else {
            this.mEnterAnimationEnd = 0;
        }
        drawable = this.mLastDrawable;
        if (drawable != null) {
            j = this.mExitAnimationEnd;
            if (j != 0) {
                if (j <= now) {
                    drawable.setVisible(false, false);
                    this.mLastDrawable = null;
                    this.mLastIndex = -1;
                    this.mExitAnimationEnd = 0;
                } else {
                    this.mLastDrawable.setAlpha((this.mAlpha * (((int) ((j - now) * 255)) / this.mDrawableContainerState.mExitFadeDuration)) / 255);
                    animating = true;
                }
            }
        } else {
            this.mExitAnimationEnd = 0;
        }
        if (schedule && animating) {
            scheduleSelf(this.mAnimationRunnable, 16 + now);
        }
    }

    public Drawable getCurrent() {
        return this.mCurrDrawable;
    }

    /* Access modifiers changed, original: protected|final */
    public final void updateDensity(Resources res) {
        this.mDrawableContainerState.updateDensity(res);
    }

    public void applyTheme(Theme theme) {
        this.mDrawableContainerState.applyTheme(theme);
    }

    public boolean canApplyTheme() {
        return this.mDrawableContainerState.canApplyTheme();
    }

    public ConstantState getConstantState() {
        if (!this.mDrawableContainerState.canConstantState()) {
            return null;
        }
        this.mDrawableContainerState.mChangingConfigurations = getChangingConfigurations();
        return this.mDrawableContainerState;
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            DrawableContainerState clone = cloneConstantState();
            clone.mutate();
            setConstantState(clone);
            this.mMutated = true;
        }
        return this;
    }

    /* Access modifiers changed, original: 0000 */
    public DrawableContainerState cloneConstantState() {
        return this.mDrawableContainerState;
    }

    public void clearMutated() {
        super.clearMutated();
        this.mDrawableContainerState.clearMutated();
        this.mMutated = false;
    }

    /* Access modifiers changed, original: protected */
    public void setConstantState(DrawableContainerState state) {
        this.mDrawableContainerState = state;
        int i = this.mCurIndex;
        if (i >= 0) {
            this.mCurrDrawable = state.getChild(i);
            Drawable drawable = this.mCurrDrawable;
            if (drawable != null) {
                initializeDrawableForDisplay(drawable);
            }
        }
        this.mLastIndex = -1;
        this.mLastDrawable = null;
    }
}
