package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.util.Log;

public abstract class ActionProvider {
    private static final String TAG = "ActionProvider";
    private SubUiVisibilityListener mSubUiVisibilityListener;
    private VisibilityListener mVisibilityListener;

    public interface SubUiVisibilityListener {
        void onSubUiVisibilityChanged(boolean z);
    }

    public interface VisibilityListener {
        void onActionProviderVisibilityChanged(boolean z);
    }

    @Deprecated
    public abstract View onCreateActionView();

    public ActionProvider(Context context) {
    }

    public View onCreateActionView(MenuItem forItem) {
        return onCreateActionView();
    }

    public boolean overridesItemVisibility() {
        return false;
    }

    public boolean isVisible() {
        return true;
    }

    public void refreshVisibility() {
        if (this.mVisibilityListener != null && overridesItemVisibility()) {
            this.mVisibilityListener.onActionProviderVisibilityChanged(isVisible());
        }
    }

    public boolean onPerformDefaultAction() {
        return false;
    }

    public boolean hasSubMenu() {
        return false;
    }

    public void onPrepareSubMenu(SubMenu subMenu) {
    }

    public void subUiVisibilityChanged(boolean isVisible) {
        SubUiVisibilityListener subUiVisibilityListener = this.mSubUiVisibilityListener;
        if (subUiVisibilityListener != null) {
            subUiVisibilityListener.onSubUiVisibilityChanged(isVisible);
        }
    }

    @UnsupportedAppUsage
    public void setSubUiVisibilityListener(SubUiVisibilityListener listener) {
        this.mSubUiVisibilityListener = listener;
    }

    public void setVisibilityListener(VisibilityListener listener) {
        if (this.mVisibilityListener != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setVisibilityListener: Setting a new ActionProvider.VisibilityListener when one is already set. Are you reusing this ");
            stringBuilder.append(getClass().getSimpleName());
            stringBuilder.append(" instance while it is still in use somewhere else?");
            Log.w(TAG, stringBuilder.toString());
        }
        this.mVisibilityListener = listener;
    }

    @UnsupportedAppUsage
    public void reset() {
        this.mVisibilityListener = null;
        this.mSubUiVisibilityListener = null;
    }
}
