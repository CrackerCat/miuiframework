package android.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.RenderNode;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewPropertyAnimator {
    static final int ALPHA = 2048;
    static final int NONE = 0;
    static final int ROTATION = 32;
    static final int ROTATION_X = 64;
    static final int ROTATION_Y = 128;
    static final int SCALE_X = 8;
    static final int SCALE_Y = 16;
    private static final int TRANSFORM_MASK = 2047;
    static final int TRANSLATION_X = 1;
    static final int TRANSLATION_Y = 2;
    static final int TRANSLATION_Z = 4;
    static final int X = 256;
    static final int Y = 512;
    static final int Z = 1024;
    private Runnable mAnimationStarter = new Runnable() {
        public void run() {
            ViewPropertyAnimator.this.startAnimation();
        }
    };
    private HashMap<Animator, Runnable> mAnimatorCleanupMap;
    private AnimatorEventListener mAnimatorEventListener = new AnimatorEventListener(this, null);
    private HashMap<Animator, PropertyBundle> mAnimatorMap = new HashMap();
    private HashMap<Animator, Runnable> mAnimatorOnEndMap;
    private HashMap<Animator, Runnable> mAnimatorOnStartMap;
    private HashMap<Animator, Runnable> mAnimatorSetupMap;
    private long mDuration;
    private boolean mDurationSet = false;
    private TimeInterpolator mInterpolator;
    private boolean mInterpolatorSet = false;
    private AnimatorListener mListener = null;
    ArrayList<NameValuesHolder> mPendingAnimations = new ArrayList();
    private Runnable mPendingCleanupAction;
    private Runnable mPendingOnEndAction;
    private Runnable mPendingOnStartAction;
    private Runnable mPendingSetupAction;
    private long mStartDelay = 0;
    private boolean mStartDelaySet = false;
    private ValueAnimator mTempValueAnimator;
    private AnimatorUpdateListener mUpdateListener = null;
    final View mView;

    private class AnimatorEventListener implements AnimatorListener, AnimatorUpdateListener {
        private AnimatorEventListener() {
        }

        /* synthetic */ AnimatorEventListener(ViewPropertyAnimator x0, AnonymousClass1 x1) {
            this();
        }

        public void onAnimationStart(Animator animation) {
            Runnable r;
            if (ViewPropertyAnimator.this.mAnimatorSetupMap != null) {
                r = (Runnable) ViewPropertyAnimator.this.mAnimatorSetupMap.get(animation);
                if (r != null) {
                    r.run();
                }
                ViewPropertyAnimator.this.mAnimatorSetupMap.remove(animation);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnStartMap != null) {
                r = (Runnable) ViewPropertyAnimator.this.mAnimatorOnStartMap.get(animation);
                if (r != null) {
                    r.run();
                }
                ViewPropertyAnimator.this.mAnimatorOnStartMap.remove(animation);
            }
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationStart(animation);
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationCancel(animation);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnEndMap != null) {
                ViewPropertyAnimator.this.mAnimatorOnEndMap.remove(animation);
            }
        }

        public void onAnimationRepeat(Animator animation) {
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationRepeat(animation);
            }
        }

        public void onAnimationEnd(Animator animation) {
            Runnable r;
            ViewPropertyAnimator.this.mView.setHasTransientState(false);
            if (ViewPropertyAnimator.this.mAnimatorCleanupMap != null) {
                r = (Runnable) ViewPropertyAnimator.this.mAnimatorCleanupMap.get(animation);
                if (r != null) {
                    r.run();
                }
                ViewPropertyAnimator.this.mAnimatorCleanupMap.remove(animation);
            }
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationEnd(animation);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnEndMap != null) {
                r = (Runnable) ViewPropertyAnimator.this.mAnimatorOnEndMap.get(animation);
                if (r != null) {
                    r.run();
                }
                ViewPropertyAnimator.this.mAnimatorOnEndMap.remove(animation);
            }
            ViewPropertyAnimator.this.mAnimatorMap.remove(animation);
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            PropertyBundle propertyBundle = (PropertyBundle) ViewPropertyAnimator.this.mAnimatorMap.get(animation);
            if (propertyBundle != null) {
                boolean hardwareAccelerated = ViewPropertyAnimator.this.mView.isHardwareAccelerated();
                if (!hardwareAccelerated) {
                    ViewPropertyAnimator.this.mView.invalidateParentCaches();
                }
                float fraction = animation.getAnimatedFraction();
                int propertyMask = propertyBundle.mPropertyMask;
                if ((propertyMask & 2047) != 0) {
                    ViewPropertyAnimator.this.mView.invalidateViewProperty(hardwareAccelerated, false);
                }
                ArrayList<NameValuesHolder> valueList = propertyBundle.mNameValuesHolder;
                if (valueList != null) {
                    int count = valueList.size();
                    for (int i = 0; i < count; i++) {
                        NameValuesHolder values = (NameValuesHolder) valueList.get(i);
                        ViewPropertyAnimator.this.setValue(values.mNameConstant, values.mFromValue + (values.mDeltaValue * fraction));
                    }
                }
                if (!((propertyMask & 2047) == 0 || hardwareAccelerated)) {
                    View view = ViewPropertyAnimator.this.mView;
                    view.mPrivateFlags |= 32;
                }
                ViewPropertyAnimator.this.mView.invalidateViewProperty(false, false);
                if (ViewPropertyAnimator.this.mUpdateListener != null) {
                    ViewPropertyAnimator.this.mUpdateListener.onAnimationUpdate(animation);
                }
            }
        }
    }

    static class NameValuesHolder {
        float mDeltaValue;
        float mFromValue;
        int mNameConstant;

        NameValuesHolder(int nameConstant, float fromValue, float deltaValue) {
            this.mNameConstant = nameConstant;
            this.mFromValue = fromValue;
            this.mDeltaValue = deltaValue;
        }
    }

    private static class PropertyBundle {
        ArrayList<NameValuesHolder> mNameValuesHolder;
        int mPropertyMask;

        PropertyBundle(int propertyMask, ArrayList<NameValuesHolder> nameValuesHolder) {
            this.mPropertyMask = propertyMask;
            this.mNameValuesHolder = nameValuesHolder;
        }

        /* Access modifiers changed, original: 0000 */
        public boolean cancel(int propertyConstant) {
            if ((this.mPropertyMask & propertyConstant) != 0) {
                int count = this.mNameValuesHolder;
                if (count != 0) {
                    count = count.size();
                    for (int i = 0; i < count; i++) {
                        if (((NameValuesHolder) this.mNameValuesHolder.get(i)).mNameConstant == propertyConstant) {
                            this.mNameValuesHolder.remove(i);
                            this.mPropertyMask &= ~propertyConstant;
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    ViewPropertyAnimator(View view) {
        this.mView = view;
        view.ensureTransformationInfo();
    }

    public ViewPropertyAnimator setDuration(long duration) {
        if (duration >= 0) {
            this.mDurationSet = true;
            this.mDuration = duration;
            return this;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Animators cannot have negative duration: ");
        stringBuilder.append(duration);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public long getDuration() {
        if (this.mDurationSet) {
            return this.mDuration;
        }
        if (this.mTempValueAnimator == null) {
            this.mTempValueAnimator = new ValueAnimator();
        }
        return this.mTempValueAnimator.getDuration();
    }

    public long getStartDelay() {
        if (this.mStartDelaySet) {
            return this.mStartDelay;
        }
        return 0;
    }

    public ViewPropertyAnimator setStartDelay(long startDelay) {
        if (startDelay >= 0) {
            this.mStartDelaySet = true;
            this.mStartDelay = startDelay;
            return this;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Animators cannot have negative start delay: ");
        stringBuilder.append(startDelay);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public ViewPropertyAnimator setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolatorSet = true;
        this.mInterpolator = interpolator;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        if (this.mInterpolatorSet) {
            return this.mInterpolator;
        }
        if (this.mTempValueAnimator == null) {
            this.mTempValueAnimator = new ValueAnimator();
        }
        return this.mTempValueAnimator.getInterpolator();
    }

    public ViewPropertyAnimator setListener(AnimatorListener listener) {
        this.mListener = listener;
        return this;
    }

    /* Access modifiers changed, original: 0000 */
    public AnimatorListener getListener() {
        return this.mListener;
    }

    public ViewPropertyAnimator setUpdateListener(AnimatorUpdateListener listener) {
        this.mUpdateListener = listener;
        return this;
    }

    /* Access modifiers changed, original: 0000 */
    public AnimatorUpdateListener getUpdateListener() {
        return this.mUpdateListener;
    }

    public void start() {
        this.mView.removeCallbacks(this.mAnimationStarter);
        startAnimation();
    }

    public void cancel() {
        if (this.mAnimatorMap.size() > 0) {
            for (Animator runningAnim : ((HashMap) this.mAnimatorMap.clone()).keySet()) {
                runningAnim.cancel();
            }
        }
        this.mPendingAnimations.clear();
        this.mPendingSetupAction = null;
        this.mPendingCleanupAction = null;
        this.mPendingOnStartAction = null;
        this.mPendingOnEndAction = null;
        this.mView.removeCallbacks(this.mAnimationStarter);
    }

    public ViewPropertyAnimator x(float value) {
        animateProperty(256, value);
        return this;
    }

    public ViewPropertyAnimator xBy(float value) {
        animatePropertyBy(256, value);
        return this;
    }

    public ViewPropertyAnimator y(float value) {
        animateProperty(512, value);
        return this;
    }

    public ViewPropertyAnimator yBy(float value) {
        animatePropertyBy(512, value);
        return this;
    }

    public ViewPropertyAnimator z(float value) {
        animateProperty(1024, value);
        return this;
    }

    public ViewPropertyAnimator zBy(float value) {
        animatePropertyBy(1024, value);
        return this;
    }

    public ViewPropertyAnimator rotation(float value) {
        animateProperty(32, value);
        return this;
    }

    public ViewPropertyAnimator rotationBy(float value) {
        animatePropertyBy(32, value);
        return this;
    }

    public ViewPropertyAnimator rotationX(float value) {
        animateProperty(64, value);
        return this;
    }

    public ViewPropertyAnimator rotationXBy(float value) {
        animatePropertyBy(64, value);
        return this;
    }

    public ViewPropertyAnimator rotationY(float value) {
        animateProperty(128, value);
        return this;
    }

    public ViewPropertyAnimator rotationYBy(float value) {
        animatePropertyBy(128, value);
        return this;
    }

    public ViewPropertyAnimator translationX(float value) {
        animateProperty(1, value);
        return this;
    }

    public ViewPropertyAnimator translationXBy(float value) {
        animatePropertyBy(1, value);
        return this;
    }

    public ViewPropertyAnimator translationY(float value) {
        animateProperty(2, value);
        return this;
    }

    public ViewPropertyAnimator translationYBy(float value) {
        animatePropertyBy(2, value);
        return this;
    }

    public ViewPropertyAnimator translationZ(float value) {
        animateProperty(4, value);
        return this;
    }

    public ViewPropertyAnimator translationZBy(float value) {
        animatePropertyBy(4, value);
        return this;
    }

    public ViewPropertyAnimator scaleX(float value) {
        animateProperty(8, value);
        return this;
    }

    public ViewPropertyAnimator scaleXBy(float value) {
        animatePropertyBy(8, value);
        return this;
    }

    public ViewPropertyAnimator scaleY(float value) {
        animateProperty(16, value);
        return this;
    }

    public ViewPropertyAnimator scaleYBy(float value) {
        animatePropertyBy(16, value);
        return this;
    }

    public ViewPropertyAnimator alpha(float value) {
        animateProperty(2048, value);
        return this;
    }

    public ViewPropertyAnimator alphaBy(float value) {
        animatePropertyBy(2048, value);
        return this;
    }

    public ViewPropertyAnimator withLayer() {
        this.mPendingSetupAction = new Runnable() {
            public void run() {
                ViewPropertyAnimator.this.mView.setLayerType(2, null);
                if (ViewPropertyAnimator.this.mView.isAttachedToWindow()) {
                    ViewPropertyAnimator.this.mView.buildLayer();
                }
            }
        };
        final int currentLayerType = this.mView.getLayerType();
        this.mPendingCleanupAction = new Runnable() {
            public void run() {
                ViewPropertyAnimator.this.mView.setLayerType(currentLayerType, null);
            }
        };
        if (this.mAnimatorSetupMap == null) {
            this.mAnimatorSetupMap = new HashMap();
        }
        if (this.mAnimatorCleanupMap == null) {
            this.mAnimatorCleanupMap = new HashMap();
        }
        return this;
    }

    public ViewPropertyAnimator withStartAction(Runnable runnable) {
        this.mPendingOnStartAction = runnable;
        if (runnable != null && this.mAnimatorOnStartMap == null) {
            this.mAnimatorOnStartMap = new HashMap();
        }
        return this;
    }

    public ViewPropertyAnimator withEndAction(Runnable runnable) {
        this.mPendingOnEndAction = runnable;
        if (runnable != null && this.mAnimatorOnEndMap == null) {
            this.mAnimatorOnEndMap = new HashMap();
        }
        return this;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean hasActions() {
        return (this.mPendingSetupAction == null && this.mPendingCleanupAction == null && this.mPendingOnStartAction == null && this.mPendingOnEndAction == null) ? false : true;
    }

    private void startAnimation() {
        this.mView.setHasTransientState(true);
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1065353216});
        ArrayList<NameValuesHolder> nameValueList = (ArrayList) this.mPendingAnimations.clone();
        this.mPendingAnimations.clear();
        int propertyMask = 0;
        for (int i = 0; i < nameValueList.size(); i++) {
            propertyMask |= ((NameValuesHolder) nameValueList.get(i)).mNameConstant;
        }
        this.mAnimatorMap.put(animator, new PropertyBundle(propertyMask, nameValueList));
        Runnable runnable = this.mPendingSetupAction;
        if (runnable != null) {
            this.mAnimatorSetupMap.put(animator, runnable);
            this.mPendingSetupAction = null;
        }
        runnable = this.mPendingCleanupAction;
        if (runnable != null) {
            this.mAnimatorCleanupMap.put(animator, runnable);
            this.mPendingCleanupAction = null;
        }
        runnable = this.mPendingOnStartAction;
        if (runnable != null) {
            this.mAnimatorOnStartMap.put(animator, runnable);
            this.mPendingOnStartAction = null;
        }
        runnable = this.mPendingOnEndAction;
        if (runnable != null) {
            this.mAnimatorOnEndMap.put(animator, runnable);
            this.mPendingOnEndAction = null;
        }
        animator.addUpdateListener(this.mAnimatorEventListener);
        animator.addListener(this.mAnimatorEventListener);
        if (this.mStartDelaySet) {
            animator.setStartDelay(this.mStartDelay);
        }
        if (this.mDurationSet) {
            animator.setDuration(this.mDuration);
        }
        if (this.mInterpolatorSet) {
            animator.setInterpolator(this.mInterpolator);
        }
        animator.start();
    }

    private void animateProperty(int constantName, float toValue) {
        float fromValue = getValue(constantName);
        animatePropertyBy(constantName, fromValue, toValue - fromValue);
    }

    private void animatePropertyBy(int constantName, float byValue) {
        animatePropertyBy(constantName, getValue(constantName), byValue);
    }

    private void animatePropertyBy(int constantName, float startValue, float byValue) {
        if (this.mAnimatorMap.size() > 0) {
            Animator animatorToCancel = null;
            for (Animator runningAnim : this.mAnimatorMap.keySet()) {
                PropertyBundle bundle = (PropertyBundle) this.mAnimatorMap.get(runningAnim);
                if (bundle.cancel(constantName) && bundle.mPropertyMask == 0) {
                    animatorToCancel = runningAnim;
                    break;
                }
            }
            if (animatorToCancel != null) {
                animatorToCancel.cancel();
            }
        }
        this.mPendingAnimations.add(new NameValuesHolder(constantName, startValue, byValue));
        this.mView.removeCallbacks(this.mAnimationStarter);
        this.mView.postOnAnimation(this.mAnimationStarter);
    }

    private void setValue(int propertyConstant, float value) {
        RenderNode renderNode = this.mView.mRenderNode;
        if (propertyConstant == 1) {
            renderNode.setTranslationX(value);
        } else if (propertyConstant == 2) {
            renderNode.setTranslationY(value);
        } else if (propertyConstant == 4) {
            renderNode.setTranslationZ(value);
        } else if (propertyConstant == 8) {
            renderNode.setScaleX(value);
        } else if (propertyConstant == 16) {
            renderNode.setScaleY(value);
        } else if (propertyConstant == 32) {
            renderNode.setRotationZ(value);
        } else if (propertyConstant == 64) {
            renderNode.setRotationX(value);
        } else if (propertyConstant == 128) {
            renderNode.setRotationY(value);
        } else if (propertyConstant == 256) {
            renderNode.setTranslationX(value - ((float) this.mView.mLeft));
        } else if (propertyConstant == 512) {
            renderNode.setTranslationY(value - ((float) this.mView.mTop));
        } else if (propertyConstant == 1024) {
            renderNode.setTranslationZ(value - renderNode.getElevation());
        } else if (propertyConstant == 2048) {
            this.mView.setAlphaInternal(value);
            renderNode.setAlpha(value);
        }
    }

    private float getValue(int propertyConstant) {
        RenderNode node = this.mView.mRenderNode;
        if (propertyConstant == 1) {
            return node.getTranslationX();
        }
        if (propertyConstant == 2) {
            return node.getTranslationY();
        }
        if (propertyConstant == 4) {
            return node.getTranslationZ();
        }
        if (propertyConstant == 8) {
            return node.getScaleX();
        }
        if (propertyConstant == 16) {
            return node.getScaleY();
        }
        if (propertyConstant == 32) {
            return node.getRotationZ();
        }
        if (propertyConstant == 64) {
            return node.getRotationX();
        }
        if (propertyConstant == 128) {
            return node.getRotationY();
        }
        if (propertyConstant == 256) {
            return ((float) this.mView.mLeft) + node.getTranslationX();
        }
        if (propertyConstant == 512) {
            return ((float) this.mView.mTop) + node.getTranslationY();
        }
        if (propertyConstant == 1024) {
            return node.getElevation() + node.getTranslationZ();
        }
        if (propertyConstant != 2048) {
            return 0.0f;
        }
        return this.mView.getAlpha();
    }
}
