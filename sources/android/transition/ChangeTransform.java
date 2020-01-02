package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatArrayEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.GhostView;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.R;
import java.util.Map;

public class ChangeTransform extends Transition {
    private static final Property<PathAnimatorMatrix, float[]> NON_TRANSLATIONS_PROPERTY = new Property<PathAnimatorMatrix, float[]>(float[].class, "nonTranslations") {
        public float[] get(PathAnimatorMatrix object) {
            return null;
        }

        public void set(PathAnimatorMatrix object, float[] value) {
            object.setValues(value);
        }
    };
    private static final String PROPNAME_INTERMEDIATE_MATRIX = "android:changeTransform:intermediateMatrix";
    private static final String PROPNAME_INTERMEDIATE_PARENT_MATRIX = "android:changeTransform:intermediateParentMatrix";
    private static final String PROPNAME_MATRIX = "android:changeTransform:matrix";
    private static final String PROPNAME_PARENT = "android:changeTransform:parent";
    private static final String PROPNAME_PARENT_MATRIX = "android:changeTransform:parentMatrix";
    private static final String PROPNAME_TRANSFORMS = "android:changeTransform:transforms";
    private static final String TAG = "ChangeTransform";
    private static final Property<PathAnimatorMatrix, PointF> TRANSLATIONS_PROPERTY = new Property<PathAnimatorMatrix, PointF>(PointF.class, "translations") {
        public PointF get(PathAnimatorMatrix object) {
            return null;
        }

        public void set(PathAnimatorMatrix object, PointF value) {
            object.setTranslation(value);
        }
    };
    private static final String[] sTransitionProperties = new String[]{PROPNAME_MATRIX, PROPNAME_TRANSFORMS, PROPNAME_PARENT_MATRIX};
    private boolean mReparent = true;
    private Matrix mTempMatrix = new Matrix();
    private boolean mUseOverlay = true;

    private static class GhostListener extends TransitionListenerAdapter {
        private GhostView mGhostView;
        private View mStartView;
        private View mView;

        public GhostListener(View view, View startView, GhostView ghostView) {
            this.mView = view;
            this.mStartView = startView;
            this.mGhostView = ghostView;
        }

        public void onTransitionEnd(Transition transition) {
            transition.removeListener(this);
            GhostView.removeGhost(this.mView);
            this.mView.setTagInternal(R.id.transitionTransform, null);
            this.mView.setTagInternal(R.id.parentMatrix, null);
            this.mStartView.setTransitionAlpha(1.0f);
        }

        public void onTransitionPause(Transition transition) {
            this.mGhostView.setVisibility(4);
        }

        public void onTransitionResume(Transition transition) {
            this.mGhostView.setVisibility(0);
        }
    }

    private static class PathAnimatorMatrix {
        private final Matrix mMatrix = new Matrix();
        private float mTranslationX;
        private float mTranslationY;
        private final float[] mValues;
        private final View mView;

        public PathAnimatorMatrix(View view, float[] values) {
            this.mView = view;
            this.mValues = (float[]) values.clone();
            float[] fArr = this.mValues;
            this.mTranslationX = fArr[2];
            this.mTranslationY = fArr[5];
            setAnimationMatrix();
        }

        public void setValues(float[] values) {
            System.arraycopy(values, 0, this.mValues, 0, values.length);
            setAnimationMatrix();
        }

        public void setTranslation(PointF translation) {
            this.mTranslationX = translation.x;
            this.mTranslationY = translation.y;
            setAnimationMatrix();
        }

        private void setAnimationMatrix() {
            float[] fArr = this.mValues;
            fArr[2] = this.mTranslationX;
            fArr[5] = this.mTranslationY;
            this.mMatrix.setValues(fArr);
            this.mView.setAnimationMatrix(this.mMatrix);
        }

        public Matrix getMatrix() {
            return this.mMatrix;
        }
    }

    private static class Transforms {
        public final float rotationX;
        public final float rotationY;
        public final float rotationZ;
        public final float scaleX;
        public final float scaleY;
        public final float translationX;
        public final float translationY;
        public final float translationZ;

        public Transforms(View view) {
            this.translationX = view.getTranslationX();
            this.translationY = view.getTranslationY();
            this.translationZ = view.getTranslationZ();
            this.scaleX = view.getScaleX();
            this.scaleY = view.getScaleY();
            this.rotationX = view.getRotationX();
            this.rotationY = view.getRotationY();
            this.rotationZ = view.getRotation();
        }

        public void restore(View view) {
            ChangeTransform.setTransforms(view, this.translationX, this.translationY, this.translationZ, this.scaleX, this.scaleY, this.rotationX, this.rotationY, this.rotationZ);
        }

        public boolean equals(Object that) {
            boolean z = false;
            if (!(that instanceof Transforms)) {
                return false;
            }
            Transforms thatTransform = (Transforms) that;
            if (thatTransform.translationX == this.translationX && thatTransform.translationY == this.translationY && thatTransform.translationZ == this.translationZ && thatTransform.scaleX == this.scaleX && thatTransform.scaleY == this.scaleY && thatTransform.rotationX == this.rotationX && thatTransform.rotationY == this.rotationY && thatTransform.rotationZ == this.rotationZ) {
                z = true;
            }
            return z;
        }
    }

    public ChangeTransform(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeTransform);
        this.mUseOverlay = a.getBoolean(1, true);
        this.mReparent = a.getBoolean(0, true);
        a.recycle();
    }

    public boolean getReparentWithOverlay() {
        return this.mUseOverlay;
    }

    public void setReparentWithOverlay(boolean reparentWithOverlay) {
        this.mUseOverlay = reparentWithOverlay;
    }

    public boolean getReparent() {
        return this.mReparent;
    }

    public void setReparent(boolean reparent) {
        this.mReparent = reparent;
    }

    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        if (view.getVisibility() != 8) {
            transitionValues.values.put(PROPNAME_PARENT, view.getParent());
            transitionValues.values.put(PROPNAME_TRANSFORMS, new Transforms(view));
            Matrix matrix = view.getMatrix();
            if (matrix == null || matrix.isIdentity()) {
                matrix = null;
            } else {
                matrix = new Matrix(matrix);
            }
            transitionValues.values.put(PROPNAME_MATRIX, matrix);
            if (this.mReparent) {
                Matrix parentMatrix = new Matrix();
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.transformMatrixToGlobal(parentMatrix);
                parentMatrix.preTranslate((float) (-parent.getScrollX()), (float) (-parent.getScrollY()));
                transitionValues.values.put(PROPNAME_PARENT_MATRIX, parentMatrix);
                transitionValues.values.put(PROPNAME_INTERMEDIATE_MATRIX, view.getTag(R.id.transitionTransform));
                transitionValues.values.put(PROPNAME_INTERMEDIATE_PARENT_MATRIX, view.getTag(R.id.parentMatrix));
            }
        }
    }

    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (!(startValues == null || endValues == null)) {
            Map map = startValues.values;
            String str = PROPNAME_PARENT;
            if (map.containsKey(str) && endValues.values.containsKey(str)) {
                boolean handleParentChange = this.mReparent && !parentsMatch((ViewGroup) startValues.values.get(str), (ViewGroup) endValues.values.get(str));
                Matrix startMatrix = (Matrix) startValues.values.get(PROPNAME_INTERMEDIATE_MATRIX);
                if (startMatrix != null) {
                    startValues.values.put(PROPNAME_MATRIX, startMatrix);
                }
                Matrix startParentMatrix = (Matrix) startValues.values.get(PROPNAME_INTERMEDIATE_PARENT_MATRIX);
                if (startParentMatrix != null) {
                    startValues.values.put(PROPNAME_PARENT_MATRIX, startParentMatrix);
                }
                if (handleParentChange) {
                    setMatricesForParent(startValues, endValues);
                }
                ObjectAnimator transformAnimator = createTransformAnimator(startValues, endValues, handleParentChange);
                if (handleParentChange && transformAnimator != null && this.mUseOverlay) {
                    createGhostView(sceneRoot, startValues, endValues);
                }
                return transformAnimator;
            }
        }
        return null;
    }

    private ObjectAnimator createTransformAnimator(TransitionValues startValues, TransitionValues endValues, boolean handleParentChange) {
        TransitionValues transitionValues = endValues;
        Map map = startValues.values;
        String str = PROPNAME_MATRIX;
        Matrix startMatrix = (Matrix) map.get(str);
        Matrix endMatrix = (Matrix) transitionValues.values.get(str);
        if (startMatrix == null) {
            startMatrix = Matrix.IDENTITY_MATRIX;
        }
        if (endMatrix == null) {
            endMatrix = Matrix.IDENTITY_MATRIX;
        }
        if (startMatrix.equals(endMatrix)) {
            return null;
        }
        Transforms transforms = (Transforms) transitionValues.values.get(PROPNAME_TRANSFORMS);
        View view = transitionValues.view;
        setIdentityTransforms(view);
        startMatrixValues = new float[9];
        startMatrix.getValues(startMatrixValues);
        float[] endMatrixValues = new float[9];
        endMatrix.getValues(endMatrixValues);
        PathAnimatorMatrix pathAnimatorMatrix = new PathAnimatorMatrix(view, startMatrixValues);
        PropertyValuesHolder valuesProperty = PropertyValuesHolder.ofObject(NON_TRANSLATIONS_PROPERTY, new FloatArrayEvaluator(new float[9]), startMatrixValues, endMatrixValues);
        Path path = getPathMotion().getPath(startMatrixValues[2], startMatrixValues[5], endMatrixValues[2], endMatrixValues[5]);
        PropertyValuesHolder translationProperty = PropertyValuesHolder.ofObject(TRANSLATIONS_PROPERTY, null, path);
        final Matrix finalEndMatrix = endMatrix;
        final boolean z = handleParentChange;
        final View view2 = view;
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(pathAnimatorMatrix, valuesProperty, translationProperty);
        final Transforms transforms2 = transforms;
        final PathAnimatorMatrix pathAnimatorMatrix2 = pathAnimatorMatrix;
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            private boolean mIsCanceled;
            private Matrix mTempMatrix = new Matrix();

            public void onAnimationCancel(Animator animation) {
                this.mIsCanceled = true;
            }

            public void onAnimationEnd(Animator animation) {
                if (!this.mIsCanceled) {
                    if (z && ChangeTransform.this.mUseOverlay) {
                        setCurrentMatrix(finalEndMatrix);
                    } else {
                        view2.setTagInternal(R.id.transitionTransform, null);
                        view2.setTagInternal(R.id.parentMatrix, null);
                    }
                }
                view2.setAnimationMatrix(null);
                transforms2.restore(view2);
            }

            public void onAnimationPause(Animator animation) {
                setCurrentMatrix(pathAnimatorMatrix2.getMatrix());
            }

            public void onAnimationResume(Animator animation) {
                ChangeTransform.setIdentityTransforms(view2);
            }

            private void setCurrentMatrix(Matrix currentMatrix) {
                this.mTempMatrix.set(currentMatrix);
                view2.setTagInternal(R.id.transitionTransform, this.mTempMatrix);
                transforms2.restore(view2);
            }
        };
        animator.addListener(listener);
        animator.addPauseListener(listener);
        return animator;
    }

    private boolean parentsMatch(ViewGroup startParent, ViewGroup endParent) {
        boolean z = false;
        if (isValidTarget(startParent) && isValidTarget(endParent)) {
            TransitionValues endValues = getMatchedTransitionValues(startParent, true);
            if (endValues == null) {
                return false;
            }
            if (endParent == endValues.view) {
                z = true;
            }
            return z;
        }
        if (startParent == endParent) {
            z = true;
        }
        return z;
    }

    private void createGhostView(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        View view = endValues.view;
        Matrix localEndMatrix = new Matrix((Matrix) endValues.values.get(PROPNAME_PARENT_MATRIX));
        sceneRoot.transformMatrixToLocal(localEndMatrix);
        GhostView ghostView = GhostView.addGhost(view, sceneRoot, localEndMatrix);
        Transition outerTransition = this;
        while (outerTransition.mParent != null) {
            outerTransition = outerTransition.mParent;
        }
        outerTransition.addListener(new GhostListener(view, startValues.view, ghostView));
        if (startValues.view != endValues.view) {
            startValues.view.setTransitionAlpha(0.0f);
        }
        view.setTransitionAlpha(1.0f);
    }

    private void setMatricesForParent(TransitionValues startValues, TransitionValues endValues) {
        Map map = endValues.values;
        String str = PROPNAME_PARENT_MATRIX;
        Matrix endParentMatrix = (Matrix) map.get(str);
        endValues.view.setTagInternal(R.id.parentMatrix, endParentMatrix);
        Matrix toLocal = this.mTempMatrix;
        toLocal.reset();
        endParentMatrix.invert(toLocal);
        Map map2 = startValues.values;
        String str2 = PROPNAME_MATRIX;
        Matrix startLocal = (Matrix) map2.get(str2);
        if (startLocal == null) {
            startLocal = new Matrix();
            startValues.values.put(str2, startLocal);
        }
        startLocal.postConcat((Matrix) startValues.values.get(str));
        startLocal.postConcat(toLocal);
    }

    private static void setIdentityTransforms(View view) {
        setTransforms(view, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
    }

    private static void setTransforms(View view, float translationX, float translationY, float translationZ, float scaleX, float scaleY, float rotationX, float rotationY, float rotationZ) {
        view.setTranslationX(translationX);
        view.setTranslationY(translationY);
        view.setTranslationZ(translationZ);
        view.setScaleX(scaleX);
        view.setScaleY(scaleY);
        view.setRotationX(rotationX);
        view.setRotationY(rotationY);
        view.setRotation(rotationZ);
    }
}