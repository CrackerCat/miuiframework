package android.graphics.drawable;

import android.annotation.UnsupportedAppUsage;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.os.SystemClock;
import android.util.AttributeSet;
import com.android.ims.ImsConfig;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimationDrawable extends DrawableContainer implements Runnable, Animatable {
    private boolean mAnimating;
    private AnimationState mAnimationState;
    @UnsupportedAppUsage
    private int mCurFrame;
    private boolean mMutated;
    private boolean mRunning;

    private static final class AnimationState extends DrawableContainerState {
        private int[] mDurations;
        private boolean mOneShot = false;

        AnimationState(AnimationState orig, AnimationDrawable owner, Resources res) {
            super(orig, owner, res);
            if (orig != null) {
                this.mDurations = orig.mDurations;
                this.mOneShot = orig.mOneShot;
                return;
            }
            this.mDurations = new int[getCapacity()];
            this.mOneShot = false;
        }

        private void mutate() {
            this.mDurations = (int[]) this.mDurations.clone();
        }

        public Drawable newDrawable() {
            return new AnimationDrawable(this, null);
        }

        public Drawable newDrawable(Resources res) {
            return new AnimationDrawable(this, res);
        }

        public void addFrame(Drawable dr, int dur) {
            this.mDurations[super.addChild(dr)] = dur;
        }

        public void growArray(int oldSize, int newSize) {
            super.growArray(oldSize, newSize);
            int[] newDurations = new int[newSize];
            System.arraycopy(this.mDurations, 0, newDurations, 0, oldSize);
            this.mDurations = newDurations;
        }
    }

    public AnimationDrawable() {
        this(null, null);
    }

    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (!visible) {
            unscheduleSelf(this);
        } else if (restart || changed) {
            int i = 0;
            boolean startFromZero = restart || (!(this.mRunning || this.mAnimationState.mOneShot) || this.mCurFrame >= this.mAnimationState.getChildCount());
            if (!startFromZero) {
                i = this.mCurFrame;
            }
            setFrame(i, true, this.mAnimating);
        }
        return changed;
    }

    public void start() {
        boolean z = true;
        this.mAnimating = true;
        if (!isRunning()) {
            if (this.mAnimationState.getChildCount() <= 1 && this.mAnimationState.mOneShot) {
                z = false;
            }
            setFrame(0, false, z);
        }
    }

    public void stop() {
        this.mAnimating = false;
        if (isRunning()) {
            this.mCurFrame = 0;
            unscheduleSelf(this);
        }
    }

    public boolean isRunning() {
        return this.mRunning;
    }

    public void run() {
        nextFrame(false);
    }

    public void unscheduleSelf(Runnable what) {
        this.mRunning = false;
        super.unscheduleSelf(what);
    }

    public int getNumberOfFrames() {
        return this.mAnimationState.getChildCount();
    }

    public Drawable getFrame(int index) {
        return this.mAnimationState.getChild(index);
    }

    public int getDuration(int i) {
        return this.mAnimationState.mDurations[i];
    }

    public boolean isOneShot() {
        return this.mAnimationState.mOneShot;
    }

    public void setOneShot(boolean oneShot) {
        this.mAnimationState.mOneShot = oneShot;
    }

    public void addFrame(Drawable frame, int duration) {
        this.mAnimationState.addFrame(frame, duration);
        if (!this.mRunning) {
            setFrame(0, true, false);
        }
    }

    private void nextFrame(boolean unschedule) {
        boolean z = true;
        int nextFrame = this.mCurFrame + 1;
        int numFrames = this.mAnimationState.getChildCount();
        boolean isLastFrame = this.mAnimationState.mOneShot && nextFrame >= numFrames - 1;
        if (!this.mAnimationState.mOneShot && nextFrame >= numFrames) {
            nextFrame = 0;
        }
        if (isLastFrame) {
            z = false;
        }
        setFrame(nextFrame, unschedule, z);
    }

    private void setFrame(int frame, boolean unschedule, boolean animate) {
        if (frame < this.mAnimationState.getChildCount()) {
            this.mAnimating = animate;
            this.mCurFrame = frame;
            selectDrawable(frame);
            if (unschedule || animate) {
                unscheduleSelf(this);
            }
            if (animate) {
                this.mCurFrame = frame;
                this.mRunning = true;
                scheduleSelf(this, SystemClock.uptimeMillis() + ((long) this.mAnimationState.mDurations[frame]));
            }
        }
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.AnimationDrawable);
        super.inflateWithAttributes(r, parser, a, 0);
        updateStateFromTypedArray(a);
        updateDensity(r);
        a.recycle();
        inflateChildElements(r, parser, attrs, theme);
        setFrame(0, true, false);
    }

    private void inflateChildElements(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int next = parser.next();
            int type = next;
            if (next != 1) {
                next = parser.getDepth();
                int depth = next;
                if (next < innerDepth && type == 3) {
                    return;
                }
                if (type == 2) {
                    if (depth > innerDepth) {
                        continue;
                    } else if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.AnimationDrawableItem);
                        int duration = a.getInt(0, -1);
                        StringBuilder stringBuilder;
                        if (duration >= 0) {
                            Drawable dr = a.getDrawable(1);
                            a.recycle();
                            if (dr == null) {
                                while (true) {
                                    int next2 = parser.next();
                                    type = next2;
                                    if (next2 != 4) {
                                        break;
                                    }
                                }
                                if (type == 2) {
                                    dr = Drawable.createFromXmlInner(r, parser, attrs, theme);
                                } else {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(parser.getPositionDescription());
                                    stringBuilder.append(": <item> tag requires a 'drawable' attribute or child tag defining a drawable");
                                    throw new XmlPullParserException(stringBuilder.toString());
                                }
                            }
                            this.mAnimationState.addFrame(dr, duration);
                            if (dr != null) {
                                dr.setCallback(this);
                            }
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(parser.getPositionDescription());
                            stringBuilder.append(": <item> tag requires a 'duration' attribute");
                            throw new XmlPullParserException(stringBuilder.toString());
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        AnimationState animationState = this.mAnimationState;
        animationState.mVariablePadding = a.getBoolean(1, animationState.mVariablePadding);
        animationState = this.mAnimationState;
        animationState.mOneShot = a.getBoolean(2, animationState.mOneShot);
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mAnimationState.mutate();
            this.mMutated = true;
        }
        return this;
    }

    /* Access modifiers changed, original: 0000 */
    public AnimationState cloneConstantState() {
        return new AnimationState(this.mAnimationState, this, null);
    }

    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    /* Access modifiers changed, original: protected */
    public void setConstantState(DrawableContainerState state) {
        super.setConstantState(state);
        if (state instanceof AnimationState) {
            this.mAnimationState = (AnimationState) state;
        }
    }

    private AnimationDrawable(AnimationState state, Resources res) {
        this.mCurFrame = 0;
        setConstantState(new AnimationState(state, this, res));
        if (state != null) {
            setFrame(0, true, false);
        }
    }
}
