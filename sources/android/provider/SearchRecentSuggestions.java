package android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.Semaphore;

public class SearchRecentSuggestions {
    private static final String LOG_TAG = "SearchSuggestions";
    private static final int MAX_HISTORY_COUNT = 250;
    public static final String[] QUERIES_PROJECTION_1LINE;
    public static final String[] QUERIES_PROJECTION_2LINE;
    public static final int QUERIES_PROJECTION_DATE_INDEX = 1;
    public static final int QUERIES_PROJECTION_DISPLAY1_INDEX = 3;
    public static final int QUERIES_PROJECTION_DISPLAY2_INDEX = 4;
    public static final int QUERIES_PROJECTION_QUERY_INDEX = 2;
    private static final Semaphore sWritesInProgress = new Semaphore(0);
    private final String mAuthority;
    private final Context mContext;
    private final Uri mSuggestionsUri;
    private final boolean mTwoLineDisplay;

    private static class SuggestionColumns implements BaseColumns {
        public static final String DATE = "date";
        public static final String DISPLAY1 = "display1";
        public static final String DISPLAY2 = "display2";
        public static final String QUERY = "query";

        private SuggestionColumns() {
        }
    }

    static {
        String str = SuggestionColumns.DISPLAY1;
        String str2 = "query";
        String str3 = "date";
        String str4 = "_id";
        QUERIES_PROJECTION_1LINE = new String[]{str4, str3, str2, str};
        QUERIES_PROJECTION_2LINE = new String[]{str4, str3, str2, str, SuggestionColumns.DISPLAY2};
    }

    public SearchRecentSuggestions(Context context, String authority, int mode) {
        if (TextUtils.isEmpty(authority) || (mode & 1) == 0) {
            throw new IllegalArgumentException();
        }
        this.mTwoLineDisplay = (mode & 2) != 0;
        this.mContext = context;
        this.mAuthority = new String(authority);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("content://");
        stringBuilder.append(this.mAuthority);
        stringBuilder.append("/suggestions");
        this.mSuggestionsUri = Uri.parse(stringBuilder.toString());
    }

    public void saveRecentQuery(final String queryString, final String line2) {
        if (!TextUtils.isEmpty(queryString)) {
            if (this.mTwoLineDisplay || TextUtils.isEmpty(line2)) {
                new Thread("saveRecentQuery") {
                    public void run() {
                        SearchRecentSuggestions.this.saveRecentQueryBlocking(queryString, line2);
                        SearchRecentSuggestions.sWritesInProgress.release();
                    }
                }.start();
                return;
            }
            throw new IllegalArgumentException();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void waitForSave() {
        while (true) {
            sWritesInProgress.acquireUninterruptibly();
            if (sWritesInProgress.availablePermits() <= 0) {
                return;
            }
        }
    }

    private void saveRecentQueryBlocking(String queryString, String line2) {
        ContentResolver cr = this.mContext.getContentResolver();
        long now = System.currentTimeMillis();
        try {
            ContentValues values = new ContentValues();
            values.put(SuggestionColumns.DISPLAY1, queryString);
            if (this.mTwoLineDisplay) {
                values.put(SuggestionColumns.DISPLAY2, line2);
            }
            values.put("query", queryString);
            values.put("date", Long.valueOf(now));
            cr.insert(this.mSuggestionsUri, values);
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "saveRecentQuery", e);
        }
        truncateHistory(cr, 250);
    }

    public void clearHistory() {
        truncateHistory(this.mContext.getContentResolver(), 0);
    }

    /* Access modifiers changed, original: protected */
    public void truncateHistory(ContentResolver cr, int maxEntries) {
        if (maxEntries >= 0) {
            String selection = null;
            if (maxEntries > 0) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("_id IN (SELECT _id FROM suggestions ORDER BY date DESC LIMIT -1 OFFSET ");
                    stringBuilder.append(String.valueOf(maxEntries));
                    stringBuilder.append(")");
                    selection = stringBuilder.toString();
                } catch (RuntimeException e) {
                    Log.e(LOG_TAG, "truncateHistory", e);
                    return;
                }
            }
            cr.delete(this.mSuggestionsUri, selection, null);
            return;
        }
        throw new IllegalArgumentException();
    }
}
