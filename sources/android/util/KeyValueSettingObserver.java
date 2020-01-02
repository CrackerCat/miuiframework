package android.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public abstract class KeyValueSettingObserver {
    private static final String TAG = "KeyValueSettingObserver";
    private final ContentObserver mObserver;
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private final ContentResolver mResolver;
    private final Uri mSettingUri;

    private class SettingObserver extends ContentObserver {
        private SettingObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            KeyValueSettingObserver.this.setParserValue();
            KeyValueSettingObserver keyValueSettingObserver = KeyValueSettingObserver.this;
            keyValueSettingObserver.update(keyValueSettingObserver.mParser);
        }
    }

    public abstract String getSettingValue(ContentResolver contentResolver);

    public abstract void update(KeyValueListParser keyValueListParser);

    public KeyValueSettingObserver(Handler handler, ContentResolver resolver, Uri uri) {
        this.mObserver = new SettingObserver(handler);
        this.mResolver = resolver;
        this.mSettingUri = uri;
    }

    public void start() {
        this.mResolver.registerContentObserver(this.mSettingUri, false, this.mObserver);
        setParserValue();
        update(this.mParser);
    }

    public void stop() {
        this.mResolver.unregisterContentObserver(this.mObserver);
    }

    private void setParserValue() {
        String setting = getSettingValue(this.mResolver);
        try {
            this.mParser.setString(setting);
        } catch (IllegalArgumentException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Malformed setting: ");
            stringBuilder.append(setting);
            Slog.e(TAG, stringBuilder.toString());
        }
    }
}
