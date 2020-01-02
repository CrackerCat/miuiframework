package com.android.server.backup;

import android.app.backup.BlobBackupHelper;
import android.content.pm.IShortcutService;
import android.content.pm.IShortcutService.Stub;
import android.os.ServiceManager;
import android.util.Slog;

public class ShortcutBackupHelper extends BlobBackupHelper {
    private static final int BLOB_VERSION = 1;
    private static final String KEY_USER_FILE = "shortcutuser.xml";
    private static final String TAG = "ShortcutBackupAgent";

    public ShortcutBackupHelper() {
        super(1, KEY_USER_FILE);
    }

    private IShortcutService getShortcutService() {
        return Stub.asInterface(ServiceManager.getService("shortcut"));
    }

    /* Access modifiers changed, original: protected */
    public byte[] getBackupPayload(String key) {
        int i = (key.hashCode() == -792920646 && key.equals(KEY_USER_FILE)) ? 0 : -1;
        String str = TAG;
        if (i != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown key: ");
            stringBuilder.append(key);
            Slog.w(str, stringBuilder.toString());
        } else {
            try {
                return getShortcutService().getBackupPayload(0);
            } catch (Exception e) {
                Slog.wtf(str, "Backup failed", e);
            }
        }
        return null;
    }

    /* Access modifiers changed, original: protected */
    public void applyRestoredPayload(String key, byte[] payload) {
        int i = (key.hashCode() == -792920646 && key.equals(KEY_USER_FILE)) ? 0 : -1;
        String str = TAG;
        if (i != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown key: ");
            stringBuilder.append(key);
            Slog.w(str, stringBuilder.toString());
            return;
        }
        try {
            getShortcutService().applyRestore(payload, 0);
        } catch (Exception e) {
            Slog.wtf(str, "Restore failed", e);
        }
    }
}
