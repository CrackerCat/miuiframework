package android.database.sqlite;

import android.annotation.UnsupportedAppUsage;
import android.database.DatabaseUtils;
import android.os.CancellationSignal;
import java.util.Arrays;

public abstract class SQLiteProgram extends SQLiteClosable {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    @UnsupportedAppUsage
    private final Object[] mBindArgs;
    private final String[] mColumnNames;
    private final SQLiteDatabase mDatabase;
    private final int mNumParameters;
    private final boolean mReadOnly;
    @UnsupportedAppUsage
    private final String mSql;

    SQLiteProgram(SQLiteDatabase db, String sql, Object[] bindArgs, CancellationSignal cancellationSignalForPrepare) {
        this.mDatabase = db;
        this.mSql = sql.trim();
        int n = DatabaseUtils.getSqlStatementType(this.mSql);
        if (n == 4 || n == 5 || n == 6) {
            this.mReadOnly = false;
            this.mColumnNames = EMPTY_STRING_ARRAY;
            this.mNumParameters = 0;
        } else {
            boolean assumeReadOnly = true;
            if (n != 1) {
                assumeReadOnly = false;
            }
            SQLiteStatementInfo info = new SQLiteStatementInfo();
            db.getThreadSession().prepare(this.mSql, db.getThreadDefaultConnectionFlags(assumeReadOnly), cancellationSignalForPrepare, info);
            this.mReadOnly = info.readOnly;
            this.mColumnNames = info.columnNames;
            this.mNumParameters = info.numParameters;
        }
        if (bindArgs == null || bindArgs.length <= this.mNumParameters) {
            int i = this.mNumParameters;
            if (i != 0) {
                this.mBindArgs = new Object[i];
                if (bindArgs != null) {
                    System.arraycopy(bindArgs, 0, this.mBindArgs, 0, bindArgs.length);
                    return;
                }
                return;
            }
            this.mBindArgs = null;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Too many bind arguments.  ");
        stringBuilder.append(bindArgs.length);
        stringBuilder.append(" arguments were provided but the statement needs ");
        stringBuilder.append(this.mNumParameters);
        stringBuilder.append(" arguments.");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    /* Access modifiers changed, original: final */
    public final SQLiteDatabase getDatabase() {
        return this.mDatabase;
    }

    /* Access modifiers changed, original: final */
    public final String getSql() {
        return this.mSql;
    }

    /* Access modifiers changed, original: final */
    public final Object[] getBindArgs() {
        return this.mBindArgs;
    }

    /* Access modifiers changed, original: final */
    public final String[] getColumnNames() {
        return this.mColumnNames;
    }

    /* Access modifiers changed, original: protected|final */
    public final SQLiteSession getSession() {
        return this.mDatabase.getThreadSession();
    }

    /* Access modifiers changed, original: protected|final */
    public final int getConnectionFlags() {
        return this.mDatabase.getThreadDefaultConnectionFlags(this.mReadOnly);
    }

    /* Access modifiers changed, original: protected|final */
    public final void onCorruption() {
        this.mDatabase.onCorruption();
    }

    @Deprecated
    public final int getUniqueId() {
        return -1;
    }

    public void bindNull(int index) {
        bind(index, null);
    }

    public void bindLong(int index, long value) {
        bind(index, Long.valueOf(value));
    }

    public void bindDouble(int index, double value) {
        bind(index, Double.valueOf(value));
    }

    public void bindString(int index, String value) {
        if (value != null) {
            bind(index, value);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("the bind value at index ");
        stringBuilder.append(index);
        stringBuilder.append(" is null");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void bindBlob(int index, byte[] value) {
        if (value != null) {
            bind(index, value);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("the bind value at index ");
        stringBuilder.append(index);
        stringBuilder.append(" is null");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void clearBindings() {
        Object[] objArr = this.mBindArgs;
        if (objArr != null) {
            Arrays.fill(objArr, null);
        }
    }

    public void bindAllArgsAsStrings(String[] bindArgs) {
        if (bindArgs != null) {
            for (int i = bindArgs.length; i != 0; i--) {
                bindString(i, bindArgs[i - 1]);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onAllReferencesReleased() {
        clearBindings();
    }

    private void bind(int index, Object value) {
        if (index < 1 || index > this.mNumParameters) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cannot bind argument at index ");
            stringBuilder.append(index);
            stringBuilder.append(" because the index is out of range.  The statement has ");
            stringBuilder.append(this.mNumParameters);
            stringBuilder.append(" parameters.");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        this.mBindArgs[index - 1] = value;
    }
}
