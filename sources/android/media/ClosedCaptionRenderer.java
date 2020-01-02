package android.media;

import android.content.Context;
import android.media.SubtitleController.Renderer;

public class ClosedCaptionRenderer extends Renderer {
    private Cea608CCWidget mCCWidget;
    private final Context mContext;

    public ClosedCaptionRenderer(Context context) {
        this.mContext = context;
    }

    public boolean supports(MediaFormat format) {
        String mimeType = MediaFormat.KEY_MIME;
        if (!format.containsKey(mimeType)) {
            return false;
        }
        return "text/cea-608".equals(format.getString(mimeType));
    }

    public SubtitleTrack createTrack(MediaFormat format) {
        if ("text/cea-608".equals(format.getString(MediaFormat.KEY_MIME))) {
            if (this.mCCWidget == null) {
                this.mCCWidget = new Cea608CCWidget(this.mContext);
            }
            return new Cea608CaptionTrack(this.mCCWidget, format);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No matching format: ");
        stringBuilder.append(format.toString());
        throw new RuntimeException(stringBuilder.toString());
    }
}
