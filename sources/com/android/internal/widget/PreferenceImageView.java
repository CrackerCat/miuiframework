package com.android.internal.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class PreferenceImageView extends ImageView {
    public PreferenceImageView(Context context) {
        this(context, null);
    }

    @UnsupportedAppUsage
    public PreferenceImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize;
        int maxWidth;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == Integer.MIN_VALUE || widthMode == 0) {
            widthSize = MeasureSpec.getSize(widthMeasureSpec);
            maxWidth = getMaxWidth();
            if (maxWidth != Integer.MAX_VALUE && (maxWidth < widthSize || widthMode == 0)) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, Integer.MIN_VALUE);
            }
        }
        widthSize = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSize == Integer.MIN_VALUE || widthSize == 0) {
            maxWidth = MeasureSpec.getSize(heightMeasureSpec);
            int maxHeight = getMaxHeight();
            if (maxHeight != Integer.MAX_VALUE && (maxHeight < maxWidth || widthSize == 0)) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, Integer.MIN_VALUE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
