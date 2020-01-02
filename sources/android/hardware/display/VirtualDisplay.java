package android.hardware.display;

import android.view.Display;
import android.view.Surface;

public final class VirtualDisplay {
    private final Display mDisplay;
    private final DisplayManagerGlobal mGlobal;
    private Surface mSurface;
    private IVirtualDisplayCallback mToken;

    public static abstract class Callback {
        public void onPaused() {
        }

        public void onResumed() {
        }

        public void onStopped() {
        }
    }

    VirtualDisplay(DisplayManagerGlobal global, Display display, IVirtualDisplayCallback token, Surface surface) {
        this.mGlobal = global;
        this.mDisplay = display;
        this.mToken = token;
        this.mSurface = surface;
    }

    public Display getDisplay() {
        return this.mDisplay;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public void setSurface(Surface surface) {
        if (this.mSurface != surface) {
            this.mGlobal.setVirtualDisplaySurface(this.mToken, surface);
            this.mSurface = surface;
        }
    }

    public void resize(int width, int height, int densityDpi) {
        this.mGlobal.resizeVirtualDisplay(this.mToken, width, height, densityDpi);
    }

    public void release() {
        IVirtualDisplayCallback iVirtualDisplayCallback = this.mToken;
        if (iVirtualDisplayCallback != null) {
            this.mGlobal.releaseVirtualDisplay(iVirtualDisplayCallback);
            this.mToken = null;
        }
    }

    public void setDisplayState(boolean isOn) {
        IVirtualDisplayCallback iVirtualDisplayCallback = this.mToken;
        if (iVirtualDisplayCallback != null) {
            this.mGlobal.setVirtualDisplayState(iVirtualDisplayCallback, isOn);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("VirtualDisplay{display=");
        stringBuilder.append(this.mDisplay);
        stringBuilder.append(", token=");
        stringBuilder.append(this.mToken);
        stringBuilder.append(", surface=");
        stringBuilder.append(this.mSurface);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
