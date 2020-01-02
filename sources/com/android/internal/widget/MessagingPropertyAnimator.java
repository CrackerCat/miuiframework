package com.android.internal.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.IntProperty;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.internal.R;
import com.android.internal.widget.MessagingLinearLayout.MessagingChild;
import com.android.internal.widget.ViewClippingUtil.ClippingParameters;

public class MessagingPropertyAnimator implements OnLayoutChangeListener {
    private static final Interpolator ALPHA_IN = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator ALPHA_OUT = new PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f);
    private static final long APPEAR_ANIMATION_LENGTH = 210;
    private static final ClippingParameters CLIPPING_PARAMETERS = -$$Lambda$MessagingPropertyAnimator$7coWc0tjIUC7grCXucNFbpYTxDI.INSTANCE;
    private static final int TAG_ALPHA_ANIMATOR = 16909459;
    private static final int TAG_FIRST_LAYOUT = 16909460;
    private static final int TAG_LAYOUT_TOP = 16909461;
    private static final int TAG_TOP = 16909463;
    private static final int TAG_TOP_ANIMATOR = 16909462;
    private static final IntProperty<View> TOP = new IntProperty<View>("top") {
        public void setValue(View object, int value) {
            MessagingPropertyAnimator.setTop(object, value);
        }

        public Integer get(View object) {
            return Integer.valueOf(MessagingPropertyAnimator.getTop(object));
        }
    };

    static /* synthetic */ boolean lambda$static$0(View view) {
        return view.getId() == R.id.notification_messaging;
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        setLayoutTop(v, top);
        if (isFirstLayout(v)) {
            setFirstLayout(v, false);
            setTop(v, top);
            return;
        }
        startTopAnimation(v, getTop(v), top, MessagingLayout.FAST_OUT_SLOW_IN);
    }

    private static boolean isFirstLayout(View view) {
        Boolean tag = (Boolean) view.getTag(16909460);
        if (tag == null) {
            return true;
        }
        return tag.booleanValue();
    }

    public static void recycle(View view) {
        setFirstLayout(view, true);
    }

    private static void setFirstLayout(View view, boolean first) {
        view.setTagInternal(16909460, Boolean.valueOf(first));
    }

    private static void setLayoutTop(View view, int top) {
        view.setTagInternal(16909461, Integer.valueOf(top));
    }

    public static int getLayoutTop(View view) {
        Integer tag = (Integer) view.getTag(16909461);
        if (tag == null) {
            return getTop(view);
        }
        return tag.intValue();
    }

    public static void startLocalTranslationFrom(View view, int startTranslation, Interpolator interpolator) {
        startTopAnimation(view, getTop(view) + startTranslation, getLayoutTop(view), interpolator);
    }

    public static void startLocalTranslationTo(View view, int endTranslation, Interpolator interpolator) {
        int top = getTop(view);
        startTopAnimation(view, top, top + endTranslation, interpolator);
    }

    public static int getTop(View v) {
        Integer tag = (Integer) v.getTag(16909463);
        if (tag == null) {
            return v.getTop();
        }
        return tag.intValue();
    }

    private static void setTop(View v, int value) {
        v.setTagInternal(16909463, Integer.valueOf(value));
        updateTopAndBottom(v);
    }

    private static void updateTopAndBottom(View v) {
        int top = getTop(v);
        int height = v.getHeight();
        v.setTop(top);
        v.setBottom(height + top);
    }

    private static void startTopAnimation(final View v, int start, int end, Interpolator interpolator) {
        ObjectAnimator existing = (ObjectAnimator) v.getTag(16909462);
        if (existing != null) {
            existing.cancel();
        }
        if (!v.isShown() || start == end || (MessagingLinearLayout.isGone(v) && !isHidingAnimated(v))) {
            setTop(v, end);
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofInt((Object) v, TOP, start, end);
        setTop(v, start);
        animator.setInterpolator(interpolator);
        animator.setDuration((long) APPEAR_ANIMATION_LENGTH);
        animator.addListener(new AnimatorListenerAdapter() {
            public boolean mCancelled;

            public void onAnimationEnd(Animator animation) {
                v.setTagInternal(16909462, null);
                MessagingPropertyAnimator.setClippingDeactivated(v, false);
            }

            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }
        });
        setClippingDeactivated(v, true);
        v.setTagInternal(16909462, animator);
        animator.start();
    }

    private static boolean isHidingAnimated(View v) {
        if (v instanceof MessagingChild) {
            return ((MessagingChild) v).isHidingAnimated();
        }
        return false;
    }

    public static void fadeIn(final View v) {
        ObjectAnimator existing = (ObjectAnimator) v.getTag(16909459);
        if (existing != null) {
            existing.cancel();
        }
        if (v.getVisibility() == 4) {
            v.setVisibility(0);
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat((Object) v, View.ALPHA, 0.0f, 1.0f);
        v.setAlpha(0.0f);
        animator.setInterpolator(ALPHA_IN);
        animator.setDuration((long) APPEAR_ANIMATION_LENGTH);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                v.setTagInternal(16909459, null);
                MessagingPropertyAnimator.updateLayerType(v, false);
            }
        });
        updateLayerType(v, true);
        v.setTagInternal(16909459, animator);
        animator.start();
    }

    private static void updateLayerType(View view, boolean animating) {
        if (view.hasOverlappingRendering() && animating) {
            view.setLayerType(2, null);
        } else if (view.getLayerType() == 2) {
            view.setLayerType(0, null);
        }
    }

    public static void fadeOut(final View view, final Runnable endAction) {
        ObjectAnimator existing = (ObjectAnimator) view.getTag(16909459);
        if (existing != null) {
            existing.cancel();
        }
        if (!view.isShown() || (MessagingLinearLayout.isGone(view) && !isHidingAnimated(view))) {
            view.setAlpha(0.0f);
            if (endAction != null) {
                endAction.run();
            }
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat((Object) view, View.ALPHA, view.getAlpha(), 0.0f);
        animator.setInterpolator(ALPHA_OUT);
        animator.setDuration((long) APPEAR_ANIMATION_LENGTH);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                view.setTagInternal(16909459, null);
                MessagingPropertyAnimator.updateLayerType(view, false);
                Runnable runnable = endAction;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        updateLayerType(view, true);
        view.setTagInternal(16909459, animator);
        animator.start();
    }

    public static void setClippingDeactivated(View transformedView, boolean deactivated) {
        ViewClippingUtil.setClippingDeactivated(transformedView, deactivated, CLIPPING_PARAMETERS);
    }

    public static boolean isAnimatingTranslation(View v) {
        return v.getTag(16909462) != null;
    }

    public static boolean isAnimatingAlpha(View v) {
        return v.getTag(16909459) != null;
    }

    public static void setToLaidOutPosition(View view) {
        setTop(view, getLayoutTop(view));
    }
}
