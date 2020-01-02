package android.webkit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@Deprecated
public class WebViewFragment extends Fragment {
    private boolean mIsWebViewAvailable;
    private WebView mWebView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView webView = this.mWebView;
        if (webView != null) {
            webView.destroy();
        }
        this.mWebView = new WebView(getContext());
        this.mIsWebViewAvailable = true;
        return this.mWebView;
    }

    public void onPause() {
        super.onPause();
        this.mWebView.onPause();
    }

    public void onResume() {
        this.mWebView.onResume();
        super.onResume();
    }

    public void onDestroyView() {
        this.mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    public void onDestroy() {
        WebView webView = this.mWebView;
        if (webView != null) {
            webView.destroy();
            this.mWebView = null;
        }
        super.onDestroy();
    }

    public WebView getWebView() {
        return this.mIsWebViewAvailable ? this.mWebView : null;
    }
}
