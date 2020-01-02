package android.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.Op;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class SmartSelectSprite {
    private static final int CORNER_DURATION = 50;
    private static final int EXPAND_DURATION = 300;
    static final Comparator<RectF> RECTANGLE_COMPARATOR = Comparator.comparingDouble(-$$Lambda$SmartSelectSprite$c8eqlh2kO_X0luLU2BexwK921WA.INSTANCE).thenComparingDouble(-$$Lambda$SmartSelectSprite$mdkXIT1_UNlJQMaziE_E815aIKE.INSTANCE);
    private Animator mActiveAnimator = null;
    private final Interpolator mCornerInterpolator;
    private Drawable mExistingDrawable = null;
    private RectangleList mExistingRectangleList = null;
    private final Interpolator mExpandInterpolator;
    private final int mFillColor;
    private final Runnable mInvalidator;

    private static final class RectangleList extends Shape {
        private static final String PROPERTY_LEFT_BOUNDARY = "leftBoundary";
        private static final String PROPERTY_RIGHT_BOUNDARY = "rightBoundary";
        private int mDisplayType;
        private final Path mOutlinePolygonPath;
        private final List<RoundedRectangleShape> mRectangles;
        private final List<RoundedRectangleShape> mReversedRectangles;

        @Retention(RetentionPolicy.SOURCE)
        private @interface DisplayType {
            public static final int POLYGON = 1;
            public static final int RECTANGLES = 0;
        }

        /* synthetic */ RectangleList(List x0, AnonymousClass1 x1) {
            this(x0);
        }

        private RectangleList(List<RoundedRectangleShape> rectangles) {
            this.mDisplayType = 0;
            this.mRectangles = new ArrayList(rectangles);
            this.mReversedRectangles = new ArrayList(rectangles);
            Collections.reverse(this.mReversedRectangles);
            this.mOutlinePolygonPath = generateOutlinePolygonPath(rectangles);
        }

        private void setLeftBoundary(float leftBoundary) {
            float boundarySoFar = (float) getTotalWidth();
            for (RoundedRectangleShape rectangle : this.mReversedRectangles) {
                float rectangleLeftBoundary = boundarySoFar - rectangle.getBoundingWidth();
                if (leftBoundary < rectangleLeftBoundary) {
                    rectangle.setStartBoundary(0.0f);
                } else if (leftBoundary > boundarySoFar) {
                    rectangle.setStartBoundary(rectangle.getBoundingWidth());
                } else {
                    rectangle.setStartBoundary((rectangle.getBoundingWidth() - boundarySoFar) + leftBoundary);
                }
                boundarySoFar = rectangleLeftBoundary;
            }
        }

        private void setRightBoundary(float rightBoundary) {
            float boundarySoFar = 0.0f;
            for (RoundedRectangleShape rectangle : this.mRectangles) {
                float rectangleRightBoundary = rectangle.getBoundingWidth() + boundarySoFar;
                if (rectangleRightBoundary < rightBoundary) {
                    rectangle.setEndBoundary(rectangle.getBoundingWidth());
                } else if (boundarySoFar > rightBoundary) {
                    rectangle.setEndBoundary(0.0f);
                } else {
                    rectangle.setEndBoundary(rightBoundary - boundarySoFar);
                }
                boundarySoFar = rectangleRightBoundary;
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void setDisplayType(int displayType) {
            this.mDisplayType = displayType;
        }

        private int getTotalWidth() {
            int sum = 0;
            for (RoundedRectangleShape rectangle : this.mRectangles) {
                sum = (int) (((float) sum) + rectangle.getBoundingWidth());
            }
            return sum;
        }

        public void draw(Canvas canvas, Paint paint) {
            if (this.mDisplayType == 1) {
                drawPolygon(canvas, paint);
            } else {
                drawRectangles(canvas, paint);
            }
        }

        private void drawRectangles(Canvas canvas, Paint paint) {
            for (RoundedRectangleShape rectangle : this.mRectangles) {
                rectangle.draw(canvas, paint);
            }
        }

        private void drawPolygon(Canvas canvas, Paint paint) {
            canvas.drawPath(this.mOutlinePolygonPath, paint);
        }

        private static Path generateOutlinePolygonPath(List<RoundedRectangleShape> rectangles) {
            Path path = new Path();
            for (RoundedRectangleShape shape : rectangles) {
                Path rectanglePath = new Path();
                rectanglePath.addRect(shape.mBoundingRectangle, Direction.CW);
                path.op(rectanglePath, Op.UNION);
            }
            return path;
        }
    }

    static final class RectangleWithTextSelectionLayout {
        private final RectF mRectangle;
        private final int mTextSelectionLayout;

        RectangleWithTextSelectionLayout(RectF rectangle, int textSelectionLayout) {
            this.mRectangle = (RectF) Preconditions.checkNotNull(rectangle);
            this.mTextSelectionLayout = textSelectionLayout;
        }

        public RectF getRectangle() {
            return this.mRectangle;
        }

        public int getTextSelectionLayout() {
            return this.mTextSelectionLayout;
        }
    }

    private static final class RoundedRectangleShape extends Shape {
        private static final String PROPERTY_ROUND_RATIO = "roundRatio";
        private final RectF mBoundingRectangle;
        private final float mBoundingWidth;
        private final Path mClipPath;
        private final RectF mDrawRect;
        private final int mExpansionDirection;
        private final boolean mInverted;
        private float mLeftBoundary;
        private float mRightBoundary;
        private float mRoundRatio;

        @Retention(RetentionPolicy.SOURCE)
        private @interface ExpansionDirection {
            public static final int CENTER = 0;
            public static final int LEFT = -1;
            public static final int RIGHT = 1;
        }

        /* synthetic */ RoundedRectangleShape(RectF x0, int x1, boolean x2, AnonymousClass1 x3) {
            this(x0, x1, x2);
        }

        private static int invert(int expansionDirection) {
            return expansionDirection * -1;
        }

        private RoundedRectangleShape(RectF boundingRectangle, int expansionDirection, boolean inverted) {
            this.mRoundRatio = 1.0f;
            this.mDrawRect = new RectF();
            this.mClipPath = new Path();
            this.mLeftBoundary = 0.0f;
            this.mRightBoundary = 0.0f;
            this.mBoundingRectangle = new RectF(boundingRectangle);
            this.mBoundingWidth = boundingRectangle.width();
            boolean z = inverted && expansionDirection != 0;
            this.mInverted = z;
            if (inverted) {
                this.mExpansionDirection = invert(expansionDirection);
            } else {
                this.mExpansionDirection = expansionDirection;
            }
            if (boundingRectangle.height() > boundingRectangle.width()) {
                setRoundRatio(0.0f);
            } else {
                setRoundRatio(1.0f);
            }
        }

        public void draw(Canvas canvas, Paint paint) {
            if (this.mLeftBoundary != this.mRightBoundary) {
                float cornerRadius = getCornerRadius();
                float adjustedCornerRadius = getAdjustedCornerRadius();
                this.mDrawRect.set(this.mBoundingRectangle);
                this.mDrawRect.left = (this.mBoundingRectangle.left + this.mLeftBoundary) - (cornerRadius / 2.0f);
                this.mDrawRect.right = (this.mBoundingRectangle.left + this.mRightBoundary) + (cornerRadius / 2.0f);
                canvas.save();
                this.mClipPath.reset();
                this.mClipPath.addRoundRect(this.mDrawRect, adjustedCornerRadius, adjustedCornerRadius, Direction.CW);
                canvas.clipPath(this.mClipPath);
                canvas.drawRect(this.mBoundingRectangle, paint);
                canvas.restore();
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void setRoundRatio(float roundRatio) {
            this.mRoundRatio = roundRatio;
        }

        /* Access modifiers changed, original: 0000 */
        public float getRoundRatio() {
            return this.mRoundRatio;
        }

        private void setStartBoundary(float startBoundary) {
            if (this.mInverted) {
                this.mRightBoundary = this.mBoundingWidth - startBoundary;
            } else {
                this.mLeftBoundary = startBoundary;
            }
        }

        private void setEndBoundary(float endBoundary) {
            if (this.mInverted) {
                this.mLeftBoundary = this.mBoundingWidth - endBoundary;
            } else {
                this.mRightBoundary = endBoundary;
            }
        }

        private float getCornerRadius() {
            return Math.min(this.mBoundingRectangle.width(), this.mBoundingRectangle.height());
        }

        private float getAdjustedCornerRadius() {
            return getCornerRadius() * this.mRoundRatio;
        }

        private float getBoundingWidth() {
            return (float) ((int) (this.mBoundingRectangle.width() + getCornerRadius()));
        }
    }

    SmartSelectSprite(Context context, int highlightColor, Runnable invalidator) {
        this.mExpandInterpolator = AnimationUtils.loadInterpolator(context, 17563661);
        this.mCornerInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
        this.mFillColor = highlightColor;
        this.mInvalidator = (Runnable) Preconditions.checkNotNull(invalidator);
    }

    public void startAnimation(PointF start, List<RectangleWithTextSelectionLayout> destinationRectangles, Runnable onAnimationEnd) {
        RectangleWithTextSelectionLayout centerRectangle;
        PointF pointF = start;
        List<RectangleWithTextSelectionLayout> list = destinationRectangles;
        cancelAnimation();
        AnimatorUpdateListener updateListener = new -$$Lambda$SmartSelectSprite$2pck5xTffRWoiD4l_tkO_IIf5iM(this);
        int rectangleCount = destinationRectangles.size();
        ArrayList shapes = new ArrayList(rectangleCount);
        List cornerAnimators = new ArrayList(rectangleCount);
        int startingOffset = 0;
        for (RectangleWithTextSelectionLayout rectangleWithTextSelectionLayout : destinationRectangles) {
            RectF rectangle = rectangleWithTextSelectionLayout.getRectangle();
            if (contains(rectangle, pointF)) {
                centerRectangle = rectangleWithTextSelectionLayout;
                break;
            }
            startingOffset = (int) (((float) startingOffset) + rectangle.width());
        }
        centerRectangle = null;
        if (centerRectangle != null) {
            int startingOffset2 = (int) (((float) startingOffset) + (pointF.x - centerRectangle.getRectangle().left));
            int[] expansionDirections = generateDirections(centerRectangle, list);
            for (int index = 0; index < rectangleCount; index++) {
                RectangleWithTextSelectionLayout rectangleWithTextSelectionLayout2 = (RectangleWithTextSelectionLayout) list.get(index);
                RoundedRectangleShape shape = new RoundedRectangleShape(rectangleWithTextSelectionLayout2.getRectangle(), expansionDirections[index], rectangleWithTextSelectionLayout2.getTextSelectionLayout() == 0, null);
                cornerAnimators.add(createCornerAnimator(shape, updateListener));
                shapes.add(shape);
            }
            RectangleList rectangleList = new RectangleList(shapes, null);
            ShapeDrawable shapeDrawable = new ShapeDrawable(rectangleList);
            Paint paint = shapeDrawable.getPaint();
            paint.setColor(this.mFillColor);
            paint.setStyle(Style.FILL);
            this.mExistingRectangleList = rectangleList;
            this.mExistingDrawable = shapeDrawable;
            this.mActiveAnimator = createAnimator(rectangleList, (float) startingOffset2, (float) startingOffset2, cornerAnimators, updateListener, onAnimationEnd);
            this.mActiveAnimator.start();
            return;
        }
        throw new IllegalArgumentException("Center point is not inside any of the rectangles!");
    }

    public /* synthetic */ void lambda$startAnimation$2$SmartSelectSprite(ValueAnimator valueAnimator) {
        this.mInvalidator.run();
    }

    public boolean isAnimationActive() {
        Animator animator = this.mActiveAnimator;
        return animator != null && animator.isRunning();
    }

    private Animator createAnimator(RectangleList rectangleList, float startingOffsetLeft, float startingOffsetRight, List<Animator> cornerAnimators, AnimatorUpdateListener updateListener, Runnable onAnimationEnd) {
        ObjectAnimator rightBoundaryAnimator = ObjectAnimator.ofFloat((Object) rectangleList, "rightBoundary", startingOffsetRight, (float) rectangleList.getTotalWidth());
        ObjectAnimator leftBoundaryAnimator = ObjectAnimator.ofFloat((Object) rectangleList, "leftBoundary", startingOffsetLeft, 0.0f);
        rightBoundaryAnimator.setDuration(300);
        leftBoundaryAnimator.setDuration(300);
        rightBoundaryAnimator.addUpdateListener(updateListener);
        leftBoundaryAnimator.addUpdateListener(updateListener);
        rightBoundaryAnimator.setInterpolator(this.mExpandInterpolator);
        leftBoundaryAnimator.setInterpolator(this.mExpandInterpolator);
        new AnimatorSet().playTogether((Collection) cornerAnimators);
        new AnimatorSet().playTogether(leftBoundaryAnimator, rightBoundaryAnimator);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(boundaryAnimator, cornerAnimator);
        setUpAnimatorListener(animatorSet, onAnimationEnd);
        return animatorSet;
    }

    private void setUpAnimatorListener(Animator animator, final Runnable onAnimationEnd) {
        animator.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                SmartSelectSprite.this.mExistingRectangleList.setDisplayType(1);
                SmartSelectSprite.this.mInvalidator.run();
                onAnimationEnd.run();
            }

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private ObjectAnimator createCornerAnimator(RoundedRectangleShape shape, AnimatorUpdateListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat((Object) shape, "roundRatio", shape.getRoundRatio(), 0.0f);
        animator.setDuration(50);
        animator.addUpdateListener(listener);
        animator.setInterpolator(this.mCornerInterpolator);
        return animator;
    }

    private static int[] generateDirections(RectangleWithTextSelectionLayout centerRectangle, List<RectangleWithTextSelectionLayout> rectangles) {
        int i;
        int[] result = new int[rectangles.size()];
        int centerRectangleIndex = rectangles.indexOf(centerRectangle);
        for (i = 0; i < centerRectangleIndex - 1; i++) {
            result[i] = -1;
        }
        if (rectangles.size() == 1) {
            result[centerRectangleIndex] = 0;
        } else if (centerRectangleIndex == 0) {
            result[centerRectangleIndex] = -1;
        } else if (centerRectangleIndex == rectangles.size() - 1) {
            result[centerRectangleIndex] = 1;
        } else {
            result[centerRectangleIndex] = 0;
        }
        for (i = centerRectangleIndex + 1; i < result.length; i++) {
            result[i] = 1;
        }
        return result;
    }

    private static boolean contains(RectF rectangle, PointF point) {
        float x = point.x;
        float y = point.y;
        return x >= rectangle.left && x <= rectangle.right && y >= rectangle.top && y <= rectangle.bottom;
    }

    private void removeExistingDrawables() {
        this.mExistingDrawable = null;
        this.mExistingRectangleList = null;
        this.mInvalidator.run();
    }

    public void cancelAnimation() {
        Animator animator = this.mActiveAnimator;
        if (animator != null) {
            animator.cancel();
            this.mActiveAnimator = null;
            removeExistingDrawables();
        }
    }

    public void draw(Canvas canvas) {
        Drawable drawable = this.mExistingDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }
}
