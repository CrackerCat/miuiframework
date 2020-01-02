package android.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.SharedElementCallback.OnSharedElementsReadyListener;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import com.android.internal.view.OneShotPreDrawListener;
import java.util.ArrayList;

class EnterTransitionCoordinator extends ActivityTransitionCoordinator {
    private static final int MIN_ANIMATION_FRAMES = 2;
    private static final String TAG = "EnterTransitionCoordinator";
    private Activity mActivity;
    private boolean mAreViewsReady;
    private ObjectAnimator mBackgroundAnimator;
    private Transition mEnterViewsTransition;
    private boolean mHasStopped;
    private boolean mIsCanceled;
    private final boolean mIsCrossTask;
    private boolean mIsExitTransitionComplete;
    private boolean mIsReadyForTransition;
    private boolean mIsViewsTransitionStarted;
    private ArrayList<String> mPendingExitNames;
    private Drawable mReplacedBackground;
    private boolean mSharedElementTransitionStarted;
    private Bundle mSharedElementsBundle;
    private OneShotPreDrawListener mViewsReadyListener;
    private boolean mWasOpaque;

    public EnterTransitionCoordinator(Activity activity, ResultReceiver resultReceiver, ArrayList<String> sharedElementNames, boolean isReturning, boolean isCrossTask) {
        Window window = activity.getWindow();
        boolean z = isReturning && !isCrossTask;
        super(window, sharedElementNames, getListener(activity, z), isReturning);
        this.mActivity = activity;
        this.mIsCrossTask = isCrossTask;
        setResultReceiver(resultReceiver);
        prepareEnter();
        Bundle resultReceiverBundle = new Bundle();
        resultReceiverBundle.putParcelable("android:remoteReceiver", this);
        this.mResultReceiver.send(100, resultReceiverBundle);
        final View decorView = getDecor();
        if (decorView != null) {
            final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (EnterTransitionCoordinator.this.mIsReadyForTransition) {
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener(this);
                        } else {
                            decorView.getViewTreeObserver().removeOnPreDrawListener(this);
                        }
                    }
                    return false;
                }
            });
        }
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isCrossTask() {
        return this.mIsCrossTask;
    }

    public void viewInstancesReady(ArrayList<String> accepted, ArrayList<String> localNames, ArrayList<View> localViews) {
        boolean remap = false;
        for (int i = 0; i < localViews.size(); i++) {
            View view = (View) localViews.get(i);
            if (!TextUtils.equals(view.getTransitionName(), (CharSequence) localNames.get(i)) || !view.isAttachedToWindow()) {
                remap = true;
                break;
            }
        }
        if (remap) {
            triggerViewsReady(mapNamedElements(accepted, localNames));
        } else {
            triggerViewsReady(mapSharedElements(accepted, localViews));
        }
    }

    public void namedViewsReady(ArrayList<String> accepted, ArrayList<String> localNames) {
        triggerViewsReady(mapNamedElements(accepted, localNames));
    }

    public Transition getEnterViewsTransition() {
        return this.mEnterViewsTransition;
    }

    /* Access modifiers changed, original: protected */
    public void viewsReady(ArrayMap<String, View> sharedElements) {
        super.viewsReady(sharedElements);
        this.mIsReadyForTransition = true;
        hideViews(this.mSharedElements);
        Transition viewsTransition = getViewsTransition();
        if (!(viewsTransition == null || this.mTransitioningViews == null)) {
            ActivityTransitionCoordinator.removeExcludedViews(viewsTransition, this.mTransitioningViews);
            stripOffscreenViews();
            hideViews(this.mTransitioningViews);
        }
        if (this.mIsReturning) {
            sendSharedElementDestination();
        } else {
            moveSharedElementsToOverlay();
        }
        if (this.mSharedElementsBundle != null) {
            onTakeSharedElements();
        }
    }

    private void triggerViewsReady(ArrayMap<String, View> sharedElements) {
        if (!this.mAreViewsReady) {
            this.mAreViewsReady = true;
            ViewGroup decor = getDecor();
            if (decor == null || (decor.isAttachedToWindow() && (sharedElements.isEmpty() || !((View) sharedElements.valueAt(0)).isLayoutRequested()))) {
                viewsReady(sharedElements);
            } else {
                this.mViewsReadyListener = OneShotPreDrawListener.add(decor, new -$$Lambda$EnterTransitionCoordinator$wYWFlx9zS3bxJYkN44Bpwx_EKis(this, sharedElements));
                decor.invalidate();
            }
        }
    }

    public /* synthetic */ void lambda$triggerViewsReady$0$EnterTransitionCoordinator(ArrayMap sharedElements) {
        this.mViewsReadyListener = null;
        viewsReady(sharedElements);
    }

    private ArrayMap<String, View> mapNamedElements(ArrayList<String> accepted, ArrayList<String> localNames) {
        ArrayMap<String, View> sharedElements = new ArrayMap();
        ViewGroup decorView = getDecor();
        if (decorView != null) {
            decorView.findNamedViews(sharedElements);
        }
        if (accepted != null) {
            for (int i = 0; i < localNames.size(); i++) {
                String localName = (String) localNames.get(i);
                String acceptedName = (String) accepted.get(i);
                if (!(localName == null || localName.equals(acceptedName))) {
                    View view = (View) sharedElements.get(localName);
                    if (view != null) {
                        sharedElements.put(acceptedName, view);
                    }
                }
            }
        }
        return sharedElements;
    }

    private void sendSharedElementDestination() {
        boolean allReady;
        View decorView = getDecor();
        if (allowOverlappingTransitions() && getEnterViewsTransition() != null) {
            allReady = false;
        } else if (decorView == null) {
            allReady = true;
        } else {
            allReady = decorView.isLayoutRequested() ^ 1;
            if (allReady) {
                for (int i = 0; i < this.mSharedElements.size(); i++) {
                    if (((View) this.mSharedElements.get(i)).isLayoutRequested()) {
                        allReady = false;
                        break;
                    }
                }
            }
        }
        if (allReady) {
            Bundle state = captureSharedElementState();
            moveSharedElementsToOverlay();
            this.mResultReceiver.send(107, state);
        } else if (decorView != null) {
            OneShotPreDrawListener.add(decorView, new -$$Lambda$EnterTransitionCoordinator$dV8bqDBqB_WsCnMyvajWuP4ArwA(this));
        }
        if (allowOverlappingTransitions()) {
            startEnterTransitionOnly();
        }
    }

    public /* synthetic */ void lambda$sendSharedElementDestination$1$EnterTransitionCoordinator() {
        if (this.mResultReceiver != null) {
            Bundle state = captureSharedElementState();
            moveSharedElementsToOverlay();
            this.mResultReceiver.send(107, state);
        }
    }

    private static SharedElementCallback getListener(Activity activity, boolean isReturning) {
        return isReturning ? activity.mExitTransitionListener : activity.mEnterTransitionListener;
    }

    /* Access modifiers changed, original: protected */
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode != 103) {
            if (resultCode != 104) {
                if (resultCode == 106) {
                    cancel();
                } else if (resultCode == 108 && !this.mIsCanceled) {
                    this.mPendingExitNames = this.mAllSharedElementNames;
                }
            } else if (!this.mIsCanceled) {
                this.mIsExitTransitionComplete = true;
                if (this.mSharedElementTransitionStarted) {
                    onRemoteExitTransitionComplete();
                }
            }
        } else if (!this.mIsCanceled) {
            this.mSharedElementsBundle = resultData;
            onTakeSharedElements();
        }
    }

    public boolean isWaitingForRemoteExit() {
        return this.mIsReturning && this.mResultReceiver != null;
    }

    public ArrayList<String> getPendingExitSharedElementNames() {
        return this.mPendingExitNames;
    }

    public void forceViewsToAppear() {
        if (this.mIsReturning) {
            if (this.mIsReadyForTransition) {
                if (!this.mSharedElementTransitionStarted) {
                    moveSharedElementsFromOverlay();
                    this.mSharedElementTransitionStarted = true;
                    showViews(this.mSharedElements, true);
                    this.mSharedElements.clear();
                    sharedElementTransitionComplete();
                }
                if (!this.mIsViewsTransitionStarted) {
                    this.mIsViewsTransitionStarted = true;
                    showViews(this.mTransitioningViews, true);
                    setTransitioningViewsVisiblity(0, true);
                    this.mTransitioningViews.clear();
                    viewsTransitionComplete();
                }
                cancelPendingTransitions();
            } else {
                this.mIsReadyForTransition = true;
                if (getDecor() != null) {
                    OneShotPreDrawListener oneShotPreDrawListener = this.mViewsReadyListener;
                    if (oneShotPreDrawListener != null) {
                        oneShotPreDrawListener.removeListener();
                        this.mViewsReadyListener = null;
                    }
                }
                showViews(this.mTransitioningViews, true);
                setTransitioningViewsVisiblity(0, true);
                this.mSharedElements.clear();
                this.mAllSharedElementNames.clear();
                this.mTransitioningViews.clear();
                this.mIsReadyForTransition = true;
                viewsTransitionComplete();
                sharedElementTransitionComplete();
            }
            this.mAreViewsReady = true;
            if (this.mResultReceiver != null) {
                this.mResultReceiver.send(106, null);
                this.mResultReceiver = null;
            }
        }
    }

    private void cancel() {
        if (!this.mIsCanceled) {
            this.mIsCanceled = true;
            if (getViewsTransition() == null || this.mIsViewsTransitionStarted) {
                showViews(this.mSharedElements, true);
            } else if (this.mTransitioningViews != null) {
                this.mTransitioningViews.addAll(this.mSharedElements);
            }
            moveSharedElementsFromOverlay();
            this.mSharedElementNames.clear();
            this.mSharedElements.clear();
            this.mAllSharedElementNames.clear();
            startSharedElementTransition(null);
            onRemoteExitTransitionComplete();
        }
    }

    public boolean isReturning() {
        return this.mIsReturning;
    }

    /* Access modifiers changed, original: protected */
    public void prepareEnter() {
        ViewGroup decorView = getDecor();
        if (this.mActivity != null && decorView != null) {
            if (!isCrossTask()) {
                this.mActivity.overridePendingTransition(0, 0);
            }
            if (this.mIsReturning) {
                this.mActivity = null;
            } else {
                this.mWasOpaque = this.mActivity.convertToTranslucent(null, null);
                Drawable background = decorView.getBackground();
                if (background == null) {
                    background = new ColorDrawable(0);
                    this.mReplacedBackground = background;
                } else {
                    getWindow().setBackgroundDrawable(null);
                    background = background.mutate();
                    background.setAlpha(0);
                }
                getWindow().setBackgroundDrawable(background);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public Transition getViewsTransition() {
        Window window = getWindow();
        if (window == null) {
            return null;
        }
        if (this.mIsReturning) {
            return window.getReenterTransition();
        }
        return window.getEnterTransition();
    }

    /* Access modifiers changed, original: protected */
    public Transition getSharedElementTransition() {
        Window window = getWindow();
        if (window == null) {
            return null;
        }
        if (this.mIsReturning) {
            return window.getSharedElementReenterTransition();
        }
        return window.getSharedElementEnterTransition();
    }

    private void startSharedElementTransition(Bundle sharedElementState) {
        ViewGroup decorView = getDecor();
        if (decorView != null) {
            ArrayList<String> rejectedNames = new ArrayList(this.mAllSharedElementNames);
            rejectedNames.removeAll(this.mSharedElementNames);
            ArrayList<View> rejectedSnapshots = createSnapshots(sharedElementState, rejectedNames);
            if (this.mListener != null) {
                this.mListener.onRejectSharedElements(rejectedSnapshots);
            }
            removeNullViews(rejectedSnapshots);
            startRejectedAnimations(rejectedSnapshots);
            ArrayList<View> sharedElementSnapshots = createSnapshots(sharedElementState, this.mSharedElementNames);
            boolean startEnterTransition = true;
            showViews(this.mSharedElements, true);
            scheduleSetSharedElementEnd(sharedElementSnapshots);
            ArrayList<SharedElementOriginalState> originalImageViewState = setSharedElementState(sharedElementState, sharedElementSnapshots);
            requestLayoutForSharedElements();
            if (!allowOverlappingTransitions() || this.mIsReturning) {
                startEnterTransition = false;
            }
            lambda$scheduleGhostVisibilityChange$1$ActivityTransitionCoordinator(4);
            scheduleGhostVisibilityChange(4);
            pauseInput();
            Transition transition = beginTransition(decorView, startEnterTransition, true);
            scheduleGhostVisibilityChange(0);
            lambda$scheduleGhostVisibilityChange$1$ActivityTransitionCoordinator(0);
            if (startEnterTransition) {
                startEnterTransition(transition);
            }
            ActivityTransitionCoordinator.setOriginalSharedElementState(this.mSharedElements, originalImageViewState);
            if (this.mResultReceiver != null) {
                decorView.postOnAnimation(new Runnable() {
                    int mAnimations;

                    public void run() {
                        int i = this.mAnimations;
                        this.mAnimations = i + 1;
                        if (i < 2) {
                            View decorView = EnterTransitionCoordinator.this.getDecor();
                            if (decorView != null) {
                                decorView.postOnAnimation(this);
                            }
                        } else if (EnterTransitionCoordinator.this.mResultReceiver != null) {
                            EnterTransitionCoordinator.this.mResultReceiver.send(101, null);
                            EnterTransitionCoordinator.this.mResultReceiver = null;
                        }
                    }
                });
            }
        }
    }

    private static void removeNullViews(ArrayList<View> views) {
        if (views != null) {
            for (int i = views.size() - 1; i >= 0; i--) {
                if (views.get(i) == null) {
                    views.remove(i);
                }
            }
        }
    }

    private void onTakeSharedElements() {
        if (this.mIsReadyForTransition && this.mSharedElementsBundle != null) {
            final Bundle sharedElementState = this.mSharedElementsBundle;
            this.mSharedElementsBundle = null;
            OnSharedElementsReadyListener listener = new OnSharedElementsReadyListener() {
                public void onSharedElementsReady() {
                    View decorView = EnterTransitionCoordinator.this.getDecor();
                    if (decorView != null) {
                        OneShotPreDrawListener.add(decorView, false, new -$$Lambda$EnterTransitionCoordinator$3$I_t9rJUkrW7bwRLQtTrE8DgvPZs(this, sharedElementState));
                        decorView.invalidate();
                    }
                }

                public /* synthetic */ void lambda$onSharedElementsReady$1$EnterTransitionCoordinator$3(Bundle sharedElementState) {
                    EnterTransitionCoordinator.this.startTransition(new -$$Lambda$EnterTransitionCoordinator$3$bzpzcEqxdHzyaWu6Gq6AOD9dFMo(this, sharedElementState));
                }

                public /* synthetic */ void lambda$onSharedElementsReady$0$EnterTransitionCoordinator$3(Bundle sharedElementState) {
                    EnterTransitionCoordinator.this.startSharedElementTransition(sharedElementState);
                }
            };
            if (this.mListener == null) {
                listener.onSharedElementsReady();
            } else {
                this.mListener.onSharedElementsArrived(this.mSharedElementNames, this.mSharedElements, listener);
            }
        }
    }

    private void requestLayoutForSharedElements() {
        int numSharedElements = this.mSharedElements.size();
        for (int i = 0; i < numSharedElements; i++) {
            ((View) this.mSharedElements.get(i)).requestLayout();
        }
    }

    private Transition beginTransition(ViewGroup decorView, boolean startEnterTransition, boolean startSharedElementTransition) {
        Transition sharedElementTransition = null;
        if (startSharedElementTransition) {
            if (!this.mSharedElementNames.isEmpty()) {
                sharedElementTransition = configureTransition(getSharedElementTransition(), false);
            }
            if (sharedElementTransition == null) {
                sharedElementTransitionStarted();
                sharedElementTransitionComplete();
            } else {
                sharedElementTransition.addListener(new TransitionListenerAdapter() {
                    public void onTransitionStart(Transition transition) {
                        EnterTransitionCoordinator.this.sharedElementTransitionStarted();
                    }

                    public void onTransitionEnd(Transition transition) {
                        transition.removeListener(this);
                        EnterTransitionCoordinator.this.sharedElementTransitionComplete();
                    }
                });
            }
        }
        Transition viewsTransition = null;
        if (startEnterTransition) {
            this.mIsViewsTransitionStarted = true;
            if (!(this.mTransitioningViews == null || this.mTransitioningViews.isEmpty())) {
                viewsTransition = configureTransition(getViewsTransition(), true);
            }
            if (viewsTransition == null) {
                viewsTransitionComplete();
            } else {
                final ArrayList<View> transitioningViews = this.mTransitioningViews;
                viewsTransition.addListener(new ContinueTransitionListener() {
                    public void onTransitionStart(Transition transition) {
                        EnterTransitionCoordinator.this.mEnterViewsTransition = transition;
                        ArrayList arrayList = transitioningViews;
                        if (arrayList != null) {
                            EnterTransitionCoordinator.this.showViews(arrayList, false);
                        }
                        super.onTransitionStart(transition);
                    }

                    public void onTransitionEnd(Transition transition) {
                        EnterTransitionCoordinator.this.mEnterViewsTransition = null;
                        transition.removeListener(this);
                        EnterTransitionCoordinator.this.viewsTransitionComplete();
                        super.onTransitionEnd(transition);
                    }
                });
            }
        }
        Transition transition = ActivityTransitionCoordinator.mergeTransitions(sharedElementTransition, viewsTransition);
        if (transition != null) {
            transition.addListener(new ContinueTransitionListener());
            if (startEnterTransition) {
                setTransitioningViewsVisiblity(4, false);
            }
            TransitionManager.beginDelayedTransition(decorView, transition);
            if (startEnterTransition) {
                setTransitioningViewsVisiblity(0, false);
            }
            decorView.invalidate();
        } else {
            transitionStarted();
        }
        return transition;
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionsComplete() {
        moveSharedElementsFromOverlay();
        ViewGroup decorView = getDecor();
        if (decorView != null) {
            decorView.sendAccessibilityEvent(2048);
            Window window = getWindow();
            if (window != null && this.mReplacedBackground == decorView.getBackground()) {
                window.setBackgroundDrawable(null);
            }
        }
    }

    private void sharedElementTransitionStarted() {
        this.mSharedElementTransitionStarted = true;
        if (this.mIsExitTransitionComplete) {
            send(104, null);
        }
    }

    private void startEnterTransition(Transition transition) {
        ViewGroup decorView = getDecor();
        if (this.mIsReturning || decorView == null) {
            backgroundAnimatorComplete();
            return;
        }
        Drawable background = decorView.getBackground();
        if (background != null) {
            Object background2 = background.mutate();
            getWindow().setBackgroundDrawable(background2);
            this.mBackgroundAnimator = ObjectAnimator.ofInt(background2, "alpha", 255);
            this.mBackgroundAnimator.setDuration(getFadeDuration());
            this.mBackgroundAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    EnterTransitionCoordinator.this.makeOpaque();
                    EnterTransitionCoordinator.this.backgroundAnimatorComplete();
                }
            });
            this.mBackgroundAnimator.start();
        } else if (transition != null) {
            transition.addListener(new TransitionListenerAdapter() {
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    EnterTransitionCoordinator.this.makeOpaque();
                }
            });
            backgroundAnimatorComplete();
        } else {
            makeOpaque();
            backgroundAnimatorComplete();
        }
    }

    public void stop() {
        ObjectAnimator objectAnimator = this.mBackgroundAnimator;
        if (objectAnimator != null) {
            objectAnimator.end();
            this.mBackgroundAnimator = null;
        } else if (this.mWasOpaque) {
            ViewGroup decorView = getDecor();
            if (decorView != null) {
                Drawable drawable = decorView.getBackground();
                if (drawable != null) {
                    drawable.setAlpha(1);
                }
            }
        }
        makeOpaque();
        this.mIsCanceled = true;
        this.mResultReceiver = null;
        this.mActivity = null;
        moveSharedElementsFromOverlay();
        if (this.mTransitioningViews != null) {
            showViews(this.mTransitioningViews, true);
            setTransitioningViewsVisiblity(0, true);
        }
        showViews(this.mSharedElements, true);
        clearState();
    }

    public boolean cancelEnter() {
        lambda$scheduleGhostVisibilityChange$1$ActivityTransitionCoordinator(4);
        this.mHasStopped = true;
        this.mIsCanceled = true;
        clearState();
        return super.cancelPendingTransitions();
    }

    /* Access modifiers changed, original: protected */
    public void clearState() {
        this.mSharedElementsBundle = null;
        this.mEnterViewsTransition = null;
        this.mResultReceiver = null;
        ObjectAnimator objectAnimator = this.mBackgroundAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mBackgroundAnimator = null;
        }
        super.clearState();
    }

    private void makeOpaque() {
        if (!this.mHasStopped) {
            Activity activity = this.mActivity;
            if (activity != null) {
                if (this.mWasOpaque) {
                    activity.convertFromTranslucent();
                }
                this.mActivity = null;
            }
        }
    }

    private boolean allowOverlappingTransitions() {
        if (this.mIsReturning) {
            return getWindow().getAllowReturnTransitionOverlap();
        }
        return getWindow().getAllowEnterTransitionOverlap();
    }

    private void startRejectedAnimations(final ArrayList<View> rejectedSnapshots) {
        if (rejectedSnapshots != null && !rejectedSnapshots.isEmpty()) {
            final ViewGroup decorView = getDecor();
            if (decorView != null) {
                ViewGroupOverlay overlay = decorView.getOverlay();
                ObjectAnimator animator = null;
                int numRejected = rejectedSnapshots.size();
                for (int i = 0; i < numRejected; i++) {
                    Object snapshot = (View) rejectedSnapshots.get(i);
                    overlay.add(snapshot);
                    animator = ObjectAnimator.ofFloat(snapshot, View.ALPHA, 1.0f, 0.0f);
                    animator.start();
                }
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ViewGroupOverlay overlay = decorView.getOverlay();
                        int numRejected = rejectedSnapshots.size();
                        for (int i = 0; i < numRejected; i++) {
                            overlay.remove((View) rejectedSnapshots.get(i));
                        }
                    }
                });
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onRemoteExitTransitionComplete() {
        if (!allowOverlappingTransitions()) {
            startEnterTransitionOnly();
        }
    }

    private void startEnterTransitionOnly() {
        startTransition(new Runnable() {
            public void run() {
                ViewGroup decorView = EnterTransitionCoordinator.this.getDecor();
                if (decorView != null) {
                    EnterTransitionCoordinator.this.startEnterTransition(EnterTransitionCoordinator.this.beginTransition(decorView, true, false));
                }
            }
        });
    }
}
