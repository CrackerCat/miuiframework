package android.database;

import java.util.Iterator;

public final class CursorJoiner implements Iterator<Result>, Iterable<Result> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private int[] mColumnsLeft;
    private int[] mColumnsRight;
    private Result mCompareResult;
    private boolean mCompareResultIsValid;
    private Cursor mCursorLeft;
    private Cursor mCursorRight;
    private String[] mValues;

    /* renamed from: android.database.CursorJoiner$1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$database$CursorJoiner$Result = new int[Result.values().length];

        static {
            try {
                $SwitchMap$android$database$CursorJoiner$Result[Result.BOTH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$database$CursorJoiner$Result[Result.LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$database$CursorJoiner$Result[Result.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum Result {
        RIGHT,
        LEFT,
        BOTH
    }

    public CursorJoiner(Cursor cursorLeft, String[] columnNamesLeft, Cursor cursorRight, String[] columnNamesRight) {
        if (columnNamesLeft.length == columnNamesRight.length) {
            this.mCursorLeft = cursorLeft;
            this.mCursorRight = cursorRight;
            this.mCursorLeft.moveToFirst();
            this.mCursorRight.moveToFirst();
            this.mCompareResultIsValid = false;
            this.mColumnsLeft = buildColumnIndiciesArray(cursorLeft, columnNamesLeft);
            this.mColumnsRight = buildColumnIndiciesArray(cursorRight, columnNamesRight);
            this.mValues = new String[(this.mColumnsLeft.length * 2)];
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("you must have the same number of columns on the left and right, ");
        stringBuilder.append(columnNamesLeft.length);
        stringBuilder.append(" != ");
        stringBuilder.append(columnNamesRight.length);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public Iterator<Result> iterator() {
        return this;
    }

    private int[] buildColumnIndiciesArray(Cursor cursor, String[] columnNames) {
        int[] columns = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            columns[i] = cursor.getColumnIndexOrThrow(columnNames[i]);
        }
        return columns;
    }

    public boolean hasNext() {
        boolean z = false;
        if (this.mCompareResultIsValid) {
            int i = AnonymousClass1.$SwitchMap$android$database$CursorJoiner$Result[this.mCompareResult.ordinal()];
            if (i == 1) {
                if (!(this.mCursorLeft.isLast() && this.mCursorRight.isLast())) {
                    z = true;
                }
                return z;
            } else if (i == 2) {
                if (!(this.mCursorLeft.isLast() && this.mCursorRight.isAfterLast())) {
                    z = true;
                }
                return z;
            } else if (i == 3) {
                if (!(this.mCursorLeft.isAfterLast() && this.mCursorRight.isLast())) {
                    z = true;
                }
                return z;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("bad value for mCompareResult, ");
                stringBuilder.append(this.mCompareResult);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
        if (!(this.mCursorLeft.isAfterLast() && this.mCursorRight.isAfterLast())) {
            z = true;
        }
        return z;
    }

    public Result next() {
        if (hasNext()) {
            incrementCursors();
            boolean hasLeft = this.mCursorLeft.isAfterLast() ^ true;
            boolean hasRight = this.mCursorRight.isAfterLast() ^ true;
            if (hasLeft && hasRight) {
                populateValues(this.mValues, this.mCursorLeft, this.mColumnsLeft, 0);
                populateValues(this.mValues, this.mCursorRight, this.mColumnsRight, 1);
                int compareStrings = compareStrings(this.mValues);
                if (compareStrings == -1) {
                    this.mCompareResult = Result.LEFT;
                } else if (compareStrings == 0) {
                    this.mCompareResult = Result.BOTH;
                } else if (compareStrings == 1) {
                    this.mCompareResult = Result.RIGHT;
                }
            } else if (hasLeft) {
                this.mCompareResult = Result.LEFT;
            } else {
                this.mCompareResult = Result.RIGHT;
            }
            this.mCompareResultIsValid = true;
            return this.mCompareResult;
        }
        throw new IllegalStateException("you must only call next() when hasNext() is true");
    }

    public void remove() {
        throw new UnsupportedOperationException("not implemented");
    }

    private static void populateValues(String[] values, Cursor cursor, int[] columnIndicies, int startingIndex) {
        for (int i = 0; i < columnIndicies.length; i++) {
            values[(i * 2) + startingIndex] = cursor.getString(columnIndicies[i]);
        }
    }

    private void incrementCursors() {
        if (this.mCompareResultIsValid) {
            int i = AnonymousClass1.$SwitchMap$android$database$CursorJoiner$Result[this.mCompareResult.ordinal()];
            if (i == 1) {
                this.mCursorLeft.moveToNext();
                this.mCursorRight.moveToNext();
            } else if (i == 2) {
                this.mCursorLeft.moveToNext();
            } else if (i == 3) {
                this.mCursorRight.moveToNext();
            }
            this.mCompareResultIsValid = false;
        }
    }

    private static int compareStrings(String... values) {
        if (values.length % 2 == 0) {
            for (int index = 0; index < values.length; index += 2) {
                int i = -1;
                if (values[index] == null) {
                    if (values[index + 1] != null) {
                        return -1;
                    }
                } else if (values[index + 1] == null) {
                    return 1;
                } else {
                    int comp = values[index].compareTo(values[index + 1]);
                    if (comp != 0) {
                        if (comp >= 0) {
                            i = 1;
                        }
                        return i;
                    }
                }
            }
            return 0;
        }
        throw new IllegalArgumentException("you must specify an even number of values");
    }
}
