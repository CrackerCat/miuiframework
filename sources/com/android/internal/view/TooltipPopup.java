package com.android.internal.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import android.widget.TextView;
import com.android.internal.R;

public class TooltipPopup {
    private static final String TAG = "TooltipPopup";
    private final View mContentView;
    private final Context mContext;
    private final LayoutParams mLayoutParams = new LayoutParams();
    private final TextView mMessageView;
    private final int[] mTmpAnchorPos = new int[2];
    private final int[] mTmpAppPos = new int[2];
    private final Rect mTmpDisplayFrame = new Rect();

    public TooltipPopup(Context context) {
        this.mContext = context;
        this.mContentView = LayoutInflater.from(this.mContext).inflate((int) R.layout.tooltip, null);
        this.mMessageView = (TextView) this.mContentView.findViewById(16908299);
        this.mLayoutParams.setTitle(this.mContext.getString(R.string.tooltip_popup_title));
        this.mLayoutParams.packageName = this.mContext.getOpPackageName();
        LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.type = 1005;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.format = -3;
        layoutParams.windowAnimations = R.style.Animation_Tooltip;
        layoutParams.flags = 24;
    }

    public void show(View anchorView, int anchorX, int anchorY, boolean fromTouch, CharSequence tooltipText) {
        if (isShowing()) {
            hide();
        }
        this.mMessageView.setText(tooltipText);
        computePosition(anchorView, anchorX, anchorY, fromTouch, this.mLayoutParams);
        ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).addView(this.mContentView, this.mLayoutParams);
    }

    public void hide() {
        if (isShowing()) {
            ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).removeView(this.mContentView);
        }
    }

    public View getContentView() {
        return this.mContentView;
    }

    public boolean isShowing() {
        return this.mContentView.getParent() != null;
    }

    private void computePosition(View anchorView, int anchorX, int anchorY, boolean fromTouch, LayoutParams outParams) {
        int offsetX;
        int offsetAbove;
        int offsetBelow;
        int i;
        LayoutParams layoutParams = outParams;
        layoutParams.token = anchorView.getApplicationWindowToken();
        int tooltipPreciseAnchorThreshold = this.mContext.getResources().getDimensionPixelOffset(R.dimen.tooltip_precise_anchor_threshold);
        if (anchorView.getWidth() >= tooltipPreciseAnchorThreshold) {
            offsetX = anchorX;
        } else {
            offsetX = anchorView.getWidth() / 2;
        }
        if (anchorView.getHeight() >= tooltipPreciseAnchorThreshold) {
            offsetAbove = this.mContext.getResources().getDimensionPixelOffset(R.dimen.tooltip_precise_anchor_extra_offset);
            offsetBelow = anchorY + offsetAbove;
            offsetAbove = anchorY - offsetAbove;
        } else {
            offsetBelow = anchorView.getHeight();
            offsetAbove = 0;
        }
        layoutParams.gravity = 49;
        int tooltipOffset = this.mContext.getResources();
        if (fromTouch) {
            i = R.dimen.tooltip_y_offset_touch;
        } else {
            i = R.dimen.tooltip_y_offset_non_touch;
        }
        tooltipOffset = tooltipOffset.getDimensionPixelOffset(i);
        View appView = WindowManagerGlobal.getInstance().getWindowView(anchorView.getApplicationWindowToken());
        if (appView == null) {
            Slog.e(TAG, "Cannot find app view");
            return;
        }
        appView.getWindowVisibleDisplayFrame(this.mTmpDisplayFrame);
        appView.getLocationOnScreen(this.mTmpAppPos);
        anchorView.getLocationOnScreen(this.mTmpAnchorPos);
        int[] iArr = this.mTmpAnchorPos;
        int i2 = iArr[0];
        int[] iArr2 = this.mTmpAppPos;
        iArr[0] = i2 - iArr2[0];
        iArr[1] = iArr[1] - iArr2[1];
        layoutParams.x = (iArr[0] + offsetX) - (appView.getWidth() / 2);
        int spec = MeasureSpec.makeMeasureSpec(0, 0);
        this.mContentView.measure(spec, spec);
        int tooltipHeight = this.mContentView.getMeasuredHeight();
        iArr2 = this.mTmpAnchorPos;
        int yAbove = ((iArr2[1] + offsetAbove) - tooltipOffset) - tooltipHeight;
        i2 = (iArr2[1] + offsetBelow) + tooltipOffset;
        if (fromTouch) {
            if (yAbove >= 0) {
                layoutParams.y = yAbove;
            } else {
                layoutParams.y = i2;
            }
        } else if (i2 + tooltipHeight <= this.mTmpDisplayFrame.height()) {
            layoutParams.y = i2;
        } else {
            layoutParams.y = yAbove;
        }
    }
}
