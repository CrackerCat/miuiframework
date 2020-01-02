package android.text;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.icu.lang.UCharacter;
import android.icu.text.CaseMap;
import android.icu.text.Edits;
import android.icu.util.ULocale;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.sysprop.DisplayProperties;
import android.telecom.Logging.Session;
import android.text.format.DateFormat;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AccessibilityClickableSpan;
import android.text.style.AccessibilityURLSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.EasyEditSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.LineHeightSpan;
import android.text.style.LocaleSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.ScaleXSpan;
import android.text.style.SpellCheckSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuggestionRangeSpan;
import android.text.style.SuggestionSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TtsSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.util.Printer;
import com.android.internal.R;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.miui.commons.lang3.CharUtils;
import org.apache.miui.commons.lang3.ClassUtils;

public class TextUtils {
    public static final int ABSOLUTE_SIZE_SPAN = 16;
    public static final int ACCESSIBILITY_CLICKABLE_SPAN = 25;
    public static final int ACCESSIBILITY_URL_SPAN = 26;
    public static final int ALIGNMENT_SPAN = 1;
    public static final int ANNOTATION = 18;
    public static final int BACKGROUND_COLOR_SPAN = 12;
    public static final int BULLET_SPAN = 8;
    public static final int CAP_MODE_CHARACTERS = 4096;
    public static final int CAP_MODE_SENTENCES = 16384;
    public static final int CAP_MODE_WORDS = 8192;
    public static final Creator<CharSequence> CHAR_SEQUENCE_CREATOR = new Creator<CharSequence>() {
        public CharSequence createFromParcel(Parcel p) {
            int kind = p.readInt();
            String string = p.readString();
            if (string == null) {
                return null;
            }
            if (kind == 1) {
                return string;
            }
            SpannableString sp = new SpannableString(string);
            while (true) {
                kind = p.readInt();
                if (kind == 0) {
                    return sp;
                }
                switch (kind) {
                    case 1:
                        TextUtils.readSpan(p, sp, new Standard(p));
                        break;
                    case 2:
                        TextUtils.readSpan(p, sp, new ForegroundColorSpan(p));
                        break;
                    case 3:
                        TextUtils.readSpan(p, sp, new RelativeSizeSpan(p));
                        break;
                    case 4:
                        TextUtils.readSpan(p, sp, new ScaleXSpan(p));
                        break;
                    case 5:
                        TextUtils.readSpan(p, sp, new StrikethroughSpan(p));
                        break;
                    case 6:
                        TextUtils.readSpan(p, sp, new UnderlineSpan(p));
                        break;
                    case 7:
                        TextUtils.readSpan(p, sp, new StyleSpan(p));
                        break;
                    case 8:
                        TextUtils.readSpan(p, sp, new BulletSpan(p));
                        break;
                    case 9:
                        TextUtils.readSpan(p, sp, new QuoteSpan(p));
                        break;
                    case 10:
                        TextUtils.readSpan(p, sp, new LeadingMarginSpan.Standard(p));
                        break;
                    case 11:
                        TextUtils.readSpan(p, sp, new URLSpan(p));
                        break;
                    case 12:
                        TextUtils.readSpan(p, sp, new BackgroundColorSpan(p));
                        break;
                    case 13:
                        TextUtils.readSpan(p, sp, new TypefaceSpan(p));
                        break;
                    case 14:
                        TextUtils.readSpan(p, sp, new SuperscriptSpan(p));
                        break;
                    case 15:
                        TextUtils.readSpan(p, sp, new SubscriptSpan(p));
                        break;
                    case 16:
                        TextUtils.readSpan(p, sp, new AbsoluteSizeSpan(p));
                        break;
                    case 17:
                        TextUtils.readSpan(p, sp, new TextAppearanceSpan(p));
                        break;
                    case 18:
                        TextUtils.readSpan(p, sp, new Annotation(p));
                        break;
                    case 19:
                        TextUtils.readSpan(p, sp, new SuggestionSpan(p));
                        break;
                    case 20:
                        TextUtils.readSpan(p, sp, new SpellCheckSpan(p));
                        break;
                    case 21:
                        TextUtils.readSpan(p, sp, new SuggestionRangeSpan(p));
                        break;
                    case 22:
                        TextUtils.readSpan(p, sp, new EasyEditSpan(p));
                        break;
                    case 23:
                        TextUtils.readSpan(p, sp, new LocaleSpan(p));
                        break;
                    case 24:
                        TextUtils.readSpan(p, sp, new TtsSpan(p));
                        break;
                    case 25:
                        TextUtils.readSpan(p, sp, new AccessibilityClickableSpan(p));
                        break;
                    case 26:
                        TextUtils.readSpan(p, sp, new AccessibilityURLSpan(p));
                        break;
                    case 27:
                        TextUtils.readSpan(p, sp, new LineBackgroundSpan.Standard(p));
                        break;
                    case 28:
                        TextUtils.readSpan(p, sp, new LineHeightSpan.Standard(p));
                        break;
                    default:
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("bogus span encoding ");
                        stringBuilder.append(kind);
                        throw new RuntimeException(stringBuilder.toString());
                }
            }
        }

        public CharSequence[] newArray(int size) {
            return new CharSequence[size];
        }
    };
    public static final int EASY_EDIT_SPAN = 22;
    static final char ELLIPSIS_FILLER = '﻿';
    private static final String ELLIPSIS_NORMAL = "…";
    private static final String ELLIPSIS_TWO_DOTS = "‥";
    private static String[] EMPTY_STRING_ARRAY = new String[0];
    public static final int FIRST_SPAN = 1;
    public static final int FOREGROUND_COLOR_SPAN = 2;
    public static final int LAST_SPAN = 28;
    public static final int LEADING_MARGIN_SPAN = 10;
    public static final int LINE_BACKGROUND_SPAN = 27;
    private static final int LINE_FEED_CODE_POINT = 10;
    public static final int LINE_HEIGHT_SPAN = 28;
    public static final int LOCALE_SPAN = 23;
    private static final int NBSP_CODE_POINT = 160;
    private static final int PARCEL_SAFE_TEXT_LENGTH = 100000;
    public static final int QUOTE_SPAN = 9;
    public static final int RELATIVE_SIZE_SPAN = 3;
    public static final int SAFE_STRING_FLAG_FIRST_LINE = 4;
    public static final int SAFE_STRING_FLAG_SINGLE_LINE = 2;
    public static final int SAFE_STRING_FLAG_TRIM = 1;
    public static final int SCALE_X_SPAN = 4;
    public static final int SPELL_CHECK_SPAN = 20;
    public static final int STRIKETHROUGH_SPAN = 5;
    public static final int STYLE_SPAN = 7;
    public static final int SUBSCRIPT_SPAN = 15;
    public static final int SUGGESTION_RANGE_SPAN = 21;
    public static final int SUGGESTION_SPAN = 19;
    public static final int SUPERSCRIPT_SPAN = 14;
    private static final String TAG = "TextUtils";
    public static final int TEXT_APPEARANCE_SPAN = 17;
    public static final int TTS_SPAN = 24;
    public static final int TYPEFACE_SPAN = 13;
    public static final int UNDERLINE_SPAN = 6;
    public static final int URL_SPAN = 11;
    private static Object sLock = new Object();
    private static char[] sTemp = null;

    public interface EllipsizeCallback {
        void ellipsized(int i, int i2);
    }

    private static class Reverser implements CharSequence, GetChars {
        private int mEnd;
        private CharSequence mSource;
        private int mStart;

        public Reverser(CharSequence source, int start, int end) {
            this.mSource = source;
            this.mStart = start;
            this.mEnd = end;
        }

        public int length() {
            return this.mEnd - this.mStart;
        }

        public CharSequence subSequence(int start, int end) {
            char[] buf = new char[(end - start)];
            getChars(start, end, buf, 0);
            return new String(buf);
        }

        public String toString() {
            return subSequence(0, length()).toString();
        }

        public char charAt(int off) {
            return (char) UCharacter.getMirror(this.mSource.charAt((this.mEnd - 1) - off));
        }

        public void getChars(int start, int end, char[] dest, int destoff) {
            CharSequence charSequence = this.mSource;
            int i = this.mStart;
            TextUtils.getChars(charSequence, start + i, i + end, dest, destoff);
            AndroidCharacter.mirror(dest, 0, end - start);
            int len = end - start;
            i = (end - start) / 2;
            for (int i2 = 0; i2 < i; i2++) {
                char tmp = dest[destoff + i2];
                dest[destoff + i2] = dest[((destoff + len) - i2) - 1];
                dest[((destoff + len) - i2) - 1] = tmp;
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SafeStringFlags {
    }

    public interface StringSplitter extends Iterable<String> {
        void setString(String str);
    }

    public static class SimpleStringSplitter implements StringSplitter, Iterator<String> {
        private char mDelimiter;
        private int mLength;
        private int mPosition;
        private String mString;

        public SimpleStringSplitter(char delimiter) {
            this.mDelimiter = delimiter;
        }

        public void setString(String string) {
            this.mString = string;
            this.mPosition = 0;
            this.mLength = this.mString.length();
        }

        public Iterator<String> iterator() {
            return this;
        }

        public boolean hasNext() {
            return this.mPosition < this.mLength;
        }

        public String next() {
            int end = this.mString.indexOf(this.mDelimiter, this.mPosition);
            if (end == -1) {
                end = this.mLength;
            }
            String nextString = this.mString.substring(this.mPosition, end);
            this.mPosition = end + 1;
            return nextString;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class StringWithRemovedChars {
        private final String mOriginal;
        private BitSet mRemovedChars;

        StringWithRemovedChars(String original) {
            this.mOriginal = original;
        }

        /* Access modifiers changed, original: 0000 */
        public void removeRange(int firstRemoved, int firstNonRemoved) {
            if (this.mRemovedChars == null) {
                this.mRemovedChars = new BitSet(this.mOriginal.length());
            }
            this.mRemovedChars.set(firstRemoved, firstNonRemoved);
        }

        /* Access modifiers changed, original: 0000 */
        public void removeAllCharBefore(int firstNonRemoved) {
            if (this.mRemovedChars == null) {
                this.mRemovedChars = new BitSet(this.mOriginal.length());
            }
            this.mRemovedChars.set(0, firstNonRemoved);
        }

        /* Access modifiers changed, original: 0000 */
        public void removeAllCharAfter(int firstRemoved) {
            if (this.mRemovedChars == null) {
                this.mRemovedChars = new BitSet(this.mOriginal.length());
            }
            this.mRemovedChars.set(firstRemoved, this.mOriginal.length());
        }

        public String toString() {
            if (this.mRemovedChars == null) {
                return this.mOriginal;
            }
            StringBuilder sb = new StringBuilder(this.mOriginal.length());
            for (int i = 0; i < this.mOriginal.length(); i++) {
                if (!this.mRemovedChars.get(i)) {
                    sb.append(this.mOriginal.charAt(i));
                }
            }
            return sb.toString();
        }

        /* Access modifiers changed, original: 0000 */
        public int length() {
            return this.mOriginal.length();
        }

        /* Access modifiers changed, original: 0000 */
        public int codePointAt(int offset) {
            return this.mOriginal.codePointAt(offset);
        }
    }

    public enum TruncateAt {
        START,
        MIDDLE,
        END,
        MARQUEE,
        END_SMALL
    }

    public static String getEllipsisString(TruncateAt method) {
        return method == TruncateAt.END_SMALL ? ELLIPSIS_TWO_DOTS : ELLIPSIS_NORMAL;
    }

    private TextUtils() {
    }

    public static void getChars(CharSequence s, int start, int end, char[] dest, int destoff) {
        Class<? extends CharSequence> c = s.getClass();
        if (c == String.class) {
            ((String) s).getChars(start, end, dest, destoff);
        } else if (c == StringBuffer.class) {
            ((StringBuffer) s).getChars(start, end, dest, destoff);
        } else if (c == StringBuilder.class) {
            ((StringBuilder) s).getChars(start, end, dest, destoff);
        } else if (s instanceof GetChars) {
            ((GetChars) s).getChars(start, end, dest, destoff);
        } else {
            int i = start;
            while (i < end) {
                int destoff2 = destoff + 1;
                dest[destoff] = s.charAt(i);
                i++;
                destoff = destoff2;
            }
        }
    }

    public static int indexOf(CharSequence s, char ch) {
        return indexOf(s, ch, 0);
    }

    public static int indexOf(CharSequence s, char ch, int start) {
        if (s.getClass() == String.class) {
            return ((String) s).indexOf(ch, start);
        }
        return indexOf(s, ch, start, s.length());
    }

    public static int indexOf(CharSequence s, char ch, int start, int end) {
        Class<? extends CharSequence> c = s.getClass();
        if ((s instanceof GetChars) || c == StringBuffer.class || c == StringBuilder.class || c == String.class) {
            char[] temp = obtain(500);
            while (start < end) {
                int segend = start + 500;
                if (segend > end) {
                    segend = end;
                }
                getChars(s, start, segend, temp, 0);
                int count = segend - start;
                for (int i = 0; i < count; i++) {
                    if (temp[i] == ch) {
                        recycle(temp);
                        return i + start;
                    }
                }
                start = segend;
            }
            recycle(temp);
            return -1;
        }
        for (int i2 = start; i2 < end; i2++) {
            if (s.charAt(i2) == ch) {
                return i2;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence s, char ch) {
        return lastIndexOf(s, ch, s.length() - 1);
    }

    public static int lastIndexOf(CharSequence s, char ch, int last) {
        if (s.getClass() == String.class) {
            return ((String) s).lastIndexOf(ch, last);
        }
        return lastIndexOf(s, ch, 0, last);
    }

    public static int lastIndexOf(CharSequence s, char ch, int start, int last) {
        if (last < 0) {
            return -1;
        }
        if (last >= s.length()) {
            last = s.length() - 1;
        }
        int end = last + 1;
        Class<? extends CharSequence> c = s.getClass();
        if ((s instanceof GetChars) || c == StringBuffer.class || c == StringBuilder.class || c == String.class) {
            char[] temp = obtain(500);
            while (start < end) {
                int segstart = end - 500;
                if (segstart < start) {
                    segstart = start;
                }
                getChars(s, segstart, end, temp, 0);
                for (int i = (end - segstart) - 1; i >= 0; i--) {
                    if (temp[i] == ch) {
                        recycle(temp);
                        return i + segstart;
                    }
                }
                end = segstart;
            }
            recycle(temp);
            return -1;
        }
        for (int i2 = end - 1; i2 >= start; i2--) {
            if (s.charAt(i2) == ch) {
                return i2;
            }
        }
        return -1;
    }

    public static int indexOf(CharSequence s, CharSequence needle) {
        return indexOf(s, needle, 0, s.length());
    }

    public static int indexOf(CharSequence s, CharSequence needle, int start) {
        return indexOf(s, needle, start, s.length());
    }

    public static int indexOf(CharSequence s, CharSequence needle, int start, int end) {
        int nlen = needle.length();
        if (nlen == 0) {
            return start;
        }
        char c = needle.charAt(0);
        while (true) {
            start = indexOf(s, c, start);
            if (start > end - nlen || start < 0) {
                return -1;
            }
            if (regionMatches(s, start, needle, 0, nlen)) {
                return start;
            }
            start++;
        }
    }

    public static boolean regionMatches(CharSequence one, int toffset, CharSequence two, int ooffset, int len) {
        int tempLen = len * 2;
        if (tempLen >= len) {
            char[] temp = obtain(tempLen);
            getChars(one, toffset, toffset + len, temp, 0);
            getChars(two, ooffset, ooffset + len, temp, len);
            boolean match = true;
            for (int i = 0; i < len; i++) {
                if (temp[i] != temp[i + len]) {
                    match = false;
                    break;
                }
            }
            recycle(temp);
            return match;
        }
        throw new IndexOutOfBoundsException();
    }

    public static String substring(CharSequence source, int start, int end) {
        if (source instanceof String) {
            return ((String) source).substring(start, end);
        }
        if (source instanceof StringBuilder) {
            return ((StringBuilder) source).substring(start, end);
        }
        if (source instanceof StringBuffer) {
            return ((StringBuffer) source).substring(start, end);
        }
        char[] temp = obtain(end - start);
        getChars(source, start, end, temp, 0);
        String ret = new String(temp, 0, end - start);
        recycle(temp);
        return ret;
    }

    public static String join(CharSequence delimiter, Object[] tokens) {
        int length = tokens.length;
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tokens[0]);
        for (int i = 1; i < length; i++) {
            sb.append(delimiter);
            sb.append(tokens[i]);
        }
        return sb.toString();
    }

    public static String join(CharSequence delimiter, Iterable tokens) {
        Iterator<?> it = tokens.iterator();
        if (!it.hasNext()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(it.next());
        while (it.hasNext()) {
            sb.append(delimiter);
            sb.append(it.next());
        }
        return sb.toString();
    }

    public static String[] split(String text, String expression) {
        if (text.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        return text.split(expression, -1);
    }

    public static String[] split(String text, Pattern pattern) {
        if (text.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        return pattern.split(text, -1);
    }

    public static CharSequence stringOrSpannedString(CharSequence source) {
        if (source == null) {
            return null;
        }
        if (source instanceof SpannedString) {
            return source;
        }
        if (source instanceof Spanned) {
            return new SpannedString(source);
        }
        return source.toString();
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String nullIfEmpty(String str) {
        return isEmpty(str) ? null : str;
    }

    public static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    public static String firstNotEmpty(String a, String b) {
        return !isEmpty(a) ? a : (String) Preconditions.checkStringNotEmpty(b);
    }

    public static int length(String s) {
        return s != null ? s.length() : 0;
    }

    public static String safeIntern(String s) {
        return s != null ? s.intern() : null;
    }

    public static int getTrimmedLength(CharSequence s) {
        int len = s.length();
        int start = 0;
        while (start < len && s.charAt(start) <= ' ') {
            start++;
        }
        int end = len;
        while (end > start && s.charAt(end - 1) <= ' ') {
            end--;
        }
        return end - start;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        if (!(a == null || b == null)) {
            int length = a.length();
            int length2 = length;
            if (length == b.length()) {
                if ((a instanceof String) && (b instanceof String)) {
                    return a.equals(b);
                }
                for (length = 0; length < length2; length++) {
                    if (a.charAt(length) != b.charAt(length)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static CharSequence getReverse(CharSequence source, int start, int end) {
        return new Reverser(source, start, end);
    }

    public static void writeToParcel(CharSequence cs, Parcel p, int parcelableFlags) {
        if (cs instanceof Spanned) {
            p.writeInt(0);
            p.writeString(cs.toString());
            Spanned sp = (Spanned) cs;
            Object[] os = sp.getSpans(0, cs.length(), Object.class);
            for (int i = 0; i < os.length; i++) {
                Object o = os[i];
                ParcelableSpan prop = os[i];
                if (prop instanceof CharacterStyle) {
                    prop = ((CharacterStyle) prop).getUnderlying();
                }
                if (prop instanceof ParcelableSpan) {
                    ParcelableSpan ps = prop;
                    int spanTypeId = ps.getSpanTypeIdInternal();
                    if (spanTypeId < 1 || spanTypeId > 28) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("External class \"");
                        stringBuilder.append(ps.getClass().getSimpleName());
                        stringBuilder.append("\" is attempting to use the frameworks-only ParcelableSpan interface");
                        Log.e(TAG, stringBuilder.toString());
                    } else {
                        p.writeInt(spanTypeId);
                        ps.writeToParcelInternal(p, parcelableFlags);
                        writeWhere(p, sp, o);
                    }
                }
            }
            p.writeInt(0);
            return;
        }
        p.writeInt(1);
        if (cs != null) {
            p.writeString(cs.toString());
        } else {
            p.writeString(null);
        }
    }

    private static void writeWhere(Parcel p, Spanned sp, Object o) {
        p.writeInt(sp.getSpanStart(o));
        p.writeInt(sp.getSpanEnd(o));
        p.writeInt(sp.getSpanFlags(o));
    }

    public static void dumpSpans(CharSequence cs, Printer printer, String prefix) {
        if (cs instanceof Spanned) {
            Spanned sp = (Spanned) cs;
            Object[] os = sp.getSpans(null, cs.length(), Object.class);
            for (Object o : os) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(prefix);
                stringBuilder.append(cs.subSequence(sp.getSpanStart(o), sp.getSpanEnd(o)));
                stringBuilder.append(": ");
                stringBuilder.append(Integer.toHexString(System.identityHashCode(o)));
                stringBuilder.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                stringBuilder.append(o.getClass().getCanonicalName());
                stringBuilder.append(" (");
                stringBuilder.append(sp.getSpanStart(o));
                stringBuilder.append("-");
                stringBuilder.append(sp.getSpanEnd(o));
                stringBuilder.append(") fl=#");
                stringBuilder.append(sp.getSpanFlags(o));
                printer.println(stringBuilder.toString());
            }
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(prefix);
        stringBuilder2.append(cs);
        stringBuilder2.append(": (no spans)");
        printer.println(stringBuilder2.toString());
    }

    public static CharSequence replace(CharSequence template, String[] sources, CharSequence[] destinations) {
        int i;
        int where;
        CharSequence tb = new SpannableStringBuilder(template);
        for (i = 0; i < sources.length; i++) {
            where = indexOf(tb, sources[i]);
            if (where >= 0) {
                tb.setSpan(sources[i], where, sources[i].length() + where, 33);
            }
        }
        for (i = 0; i < sources.length; i++) {
            where = tb.getSpanStart(sources[i]);
            int end = tb.getSpanEnd(sources[i]);
            if (where >= 0) {
                tb.replace(where, end, destinations[i]);
            }
        }
        return tb;
    }

    public static CharSequence expandTemplate(CharSequence template, CharSequence... values) {
        if (values.length <= 9) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(template);
            int i = 0;
            while (i < ssb.length()) {
                try {
                    if (ssb.charAt(i) == '^') {
                        char next = ssb.charAt(i + 1);
                        if (next == '^') {
                            ssb.delete(i + 1, i + 2);
                            i++;
                        } else if (Character.isDigit(next)) {
                            int which = Character.getNumericValue(next) - 1;
                            String str = "template requests value ^";
                            StringBuilder stringBuilder;
                            if (which < 0) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                stringBuilder.append(which + 1);
                                throw new IllegalArgumentException(stringBuilder.toString());
                            } else if (which < values.length) {
                                ssb.replace(i, i + 2, values[which]);
                                i += values[which].length();
                            } else {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                stringBuilder.append(which + 1);
                                stringBuilder.append("; only ");
                                stringBuilder.append(values.length);
                                stringBuilder.append(" provided");
                                throw new IllegalArgumentException(stringBuilder.toString());
                            }
                        }
                    }
                    i++;
                } catch (IndexOutOfBoundsException e) {
                }
            }
            return ssb;
        }
        throw new IllegalArgumentException("max of 9 values are supported");
    }

    public static int getOffsetBefore(CharSequence text, int offset) {
        if (offset == 0 || offset == 1) {
            return 0;
        }
        char c = text.charAt(offset - 1);
        if (c < 56320 || c > 57343) {
            offset--;
        } else {
            char c1 = text.charAt(offset - 2);
            if (c1 < 55296 || c1 > 56319) {
                offset--;
            } else {
                offset -= 2;
            }
        }
        if (text instanceof Spanned) {
            ReplacementSpan[] spans = (ReplacementSpan[]) ((Spanned) text).getSpans(offset, offset, ReplacementSpan.class);
            for (int i = 0; i < spans.length; i++) {
                int start = ((Spanned) text).getSpanStart(spans[i]);
                int end = ((Spanned) text).getSpanEnd(spans[i]);
                if (start < offset && end > offset) {
                    offset = start;
                }
            }
        }
        return offset;
    }

    public static int getOffsetAfter(CharSequence text, int offset) {
        int len = text.length();
        if (offset == len || offset == len - 1) {
            return len;
        }
        char c = text.charAt(offset);
        if (c < 55296 || c > 56319) {
            offset++;
        } else {
            char c1 = text.charAt(offset + 1);
            if (c1 < 56320 || c1 > 57343) {
                offset++;
            } else {
                offset += 2;
            }
        }
        if (text instanceof Spanned) {
            ReplacementSpan[] spans = (ReplacementSpan[]) ((Spanned) text).getSpans(offset, offset, ReplacementSpan.class);
            for (int i = 0; i < spans.length; i++) {
                int start = ((Spanned) text).getSpanStart(spans[i]);
                int end = ((Spanned) text).getSpanEnd(spans[i]);
                if (start < offset && end > offset) {
                    offset = end;
                }
            }
        }
        return offset;
    }

    private static void readSpan(Parcel p, Spannable sp, Object o) {
        sp.setSpan(o, p.readInt(), p.readInt(), p.readInt());
    }

    public static void copySpansFrom(Spanned source, int start, int end, Class kind, Spannable dest, int destoff) {
        if (kind == null) {
            kind = Object.class;
        }
        Object[] spans = source.getSpans(start, end, kind);
        for (int i = 0; i < spans.length; i++) {
            int st = source.getSpanStart(spans[i]);
            int en = source.getSpanEnd(spans[i]);
            int fl = source.getSpanFlags(spans[i]);
            if (st < start) {
                st = start;
            }
            if (en > end) {
                en = end;
            }
            dest.setSpan(spans[i], (st - start) + destoff, (en - start) + destoff, fl);
        }
    }

    public static CharSequence toUpperCase(Locale locale, CharSequence source, boolean copySpans) {
        Locale locale2 = locale;
        CharSequence charSequence = source;
        Edits edits = new Edits();
        if (copySpans) {
            SpannableStringBuilder result = (SpannableStringBuilder) CaseMap.toUpper().apply(locale2, charSequence, new SpannableStringBuilder(), edits);
            if (!edits.hasChanges()) {
                return charSequence;
            }
            Edits.Iterator iterator = edits.getFineIterator();
            int sourceLength = source.length();
            Spanned spanned = (Spanned) charSequence;
            int i = 0;
            Object[] spans = spanned.getSpans(0, sourceLength, Object.class);
            int length = spans.length;
            while (i < length) {
                int destStart;
                int destEnd;
                Object span = spans[i];
                int sourceStart = spanned.getSpanStart(span);
                int sourceEnd = spanned.getSpanEnd(span);
                int flags = spanned.getSpanFlags(span);
                if (sourceStart == sourceLength) {
                    destStart = result.length();
                } else {
                    destStart = toUpperMapToDest(iterator, sourceStart);
                }
                if (sourceEnd == sourceLength) {
                    destEnd = result.length();
                } else {
                    destEnd = toUpperMapToDest(iterator, sourceEnd);
                }
                result.setSpan(span, destStart, destEnd, flags);
                i++;
            }
            return result;
        }
        return edits.hasChanges() ? (StringBuilder) CaseMap.toUpper().apply(locale2, charSequence, new StringBuilder(), edits) : charSequence;
    }

    private static int toUpperMapToDest(Edits.Iterator iterator, int sourceIndex) {
        iterator.findSourceIndex(sourceIndex);
        if (sourceIndex == iterator.sourceIndex()) {
            return iterator.destinationIndex();
        }
        if (iterator.hasChange()) {
            return iterator.destinationIndex() + iterator.newLength();
        }
        return iterator.destinationIndex() + (sourceIndex - iterator.sourceIndex());
    }

    public static CharSequence ellipsize(CharSequence text, TextPaint p, float avail, TruncateAt where) {
        return ellipsize(text, p, avail, where, false, null);
    }

    public static CharSequence ellipsize(CharSequence text, TextPaint paint, float avail, TruncateAt where, boolean preserveLength, EllipsizeCallback callback) {
        return ellipsize(text, paint, avail, where, preserveLength, callback, TextDirectionHeuristics.FIRSTSTRONG_LTR, getEllipsisString(where));
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0143  */
    public static java.lang.CharSequence ellipsize(java.lang.CharSequence r25, android.text.TextPaint r26, float r27, android.text.TextUtils.TruncateAt r28, boolean r29, android.text.TextUtils.EllipsizeCallback r30, android.text.TextDirectionHeuristic r31, java.lang.String r32) {
        /*
        r7 = r25;
        r8 = r28;
        r9 = r30;
        r10 = r32;
        r11 = r25.length();
        r12 = 0;
        r3 = 0;
        r4 = r25.length();	 Catch:{ all -> 0x013c }
        r1 = r26;
        r2 = r25;
        r5 = r31;
        r6 = r12;
        r0 = android.text.MeasuredParagraph.buildForMeasurement(r1, r2, r3, r4, r5, r6);	 Catch:{ all -> 0x013c }
        r12 = r0;
        r0 = r12.getWholeWidth();	 Catch:{ all -> 0x013c }
        r1 = (r0 > r27 ? 1 : (r0 == r27 ? 0 : -1));
        r2 = 0;
        if (r1 > 0) goto L_0x0032;
    L_0x0027:
        if (r9 == 0) goto L_0x002c;
    L_0x0029:
        r9.ellipsized(r2, r2);	 Catch:{ all -> 0x013c }
        r12.recycle();
        return r7;
    L_0x0032:
        r13 = r26;
        r1 = r13.measureText(r10);	 Catch:{ all -> 0x013a }
        r14 = r1;
        r1 = r27 - r14;
        r3 = 0;
        r4 = r11;
        r5 = 0;
        r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
        if (r5 >= 0) goto L_0x0046;
    L_0x0042:
        r16 = r1;
        r15 = r4;
        goto L_0x007e;
    L_0x0046:
        r5 = android.text.TextUtils.TruncateAt.START;	 Catch:{ all -> 0x0136 }
        if (r8 != r5) goto L_0x0054;
    L_0x004a:
        r5 = r12.breakText(r11, r2, r1);	 Catch:{ all -> 0x0136 }
        r4 = r11 - r5;
        r16 = r1;
        r15 = r4;
        goto L_0x007e;
    L_0x0054:
        r5 = android.text.TextUtils.TruncateAt.END;	 Catch:{ all -> 0x0136 }
        r6 = 1;
        if (r8 == r5) goto L_0x0076;
    L_0x0059:
        r5 = android.text.TextUtils.TruncateAt.END_SMALL;	 Catch:{ all -> 0x0136 }
        if (r8 != r5) goto L_0x005e;
    L_0x005d:
        goto L_0x0076;
    L_0x005e:
        r5 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = r1 / r5;
        r5 = r12.breakText(r11, r2, r5);	 Catch:{ all -> 0x0136 }
        r4 = r11 - r5;
        r5 = r12.measure(r4, r11);	 Catch:{ all -> 0x0136 }
        r1 = r1 - r5;
        r5 = r12.breakText(r4, r6, r1);	 Catch:{ all -> 0x0136 }
        r3 = r5;
        r16 = r1;
        r15 = r4;
        goto L_0x007e;
    L_0x0076:
        r5 = r12.breakText(r11, r6, r1);	 Catch:{ all -> 0x0136 }
        r3 = r5;
        r16 = r1;
        r15 = r4;
    L_0x007e:
        if (r9 == 0) goto L_0x0083;
    L_0x0080:
        r9.ellipsized(r3, r15);	 Catch:{ all -> 0x0134 }
    L_0x0083:
        r1 = r12.getChars();	 Catch:{ all -> 0x0134 }
        r6 = r1;
        r1 = r7 instanceof android.text.Spanned;	 Catch:{ all -> 0x0134 }
        if (r1 == 0) goto L_0x0090;
    L_0x008c:
        r1 = r7;
        r1 = (android.text.Spanned) r1;	 Catch:{ all -> 0x0134 }
        goto L_0x0091;
    L_0x0090:
        r1 = 0;
    L_0x0091:
        r17 = r1;
        r5 = r15 - r3;
        r18 = r11 - r5;
        if (r29 == 0) goto L_0x00f1;
    L_0x0099:
        if (r18 <= 0) goto L_0x00b0;
    L_0x009b:
        r1 = r32.length();	 Catch:{ all -> 0x0134 }
        if (r5 < r1) goto L_0x00b0;
    L_0x00a1:
        r1 = r32.length();	 Catch:{ all -> 0x0134 }
        r10.getChars(r2, r1, r6, r3);	 Catch:{ all -> 0x0134 }
        r1 = r32.length();	 Catch:{ all -> 0x0134 }
        r3 = r3 + r1;
        r19 = r3;
        goto L_0x00b2;
    L_0x00b0:
        r19 = r3;
    L_0x00b2:
        r1 = r19;
    L_0x00b4:
        if (r1 >= r15) goto L_0x00be;
    L_0x00b6:
        r3 = 65279; // 0xfeff float:9.1475E-41 double:3.2252E-319;
        r6[r1] = r3;	 Catch:{ all -> 0x0134 }
        r1 = r1 + 1;
        goto L_0x00b4;
    L_0x00be:
        r1 = new java.lang.String;	 Catch:{ all -> 0x0134 }
        r1.<init>(r6, r2, r11);	 Catch:{ all -> 0x0134 }
        r4 = r1;
        if (r17 != 0) goto L_0x00cc;
        r12.recycle();
        return r4;
    L_0x00cc:
        r1 = new android.text.SpannableString;	 Catch:{ all -> 0x0134 }
        r1.<init>(r4);	 Catch:{ all -> 0x0134 }
        r20 = r1;
        r2 = 0;
        r21 = java.lang.Object.class;
        r22 = 0;
        r1 = r17;
        r3 = r11;
        r23 = r4;
        r4 = r21;
        r21 = r5;
        r5 = r20;
        r24 = r0;
        r0 = r6;
        r6 = r22;
        copySpansFrom(r1, r2, r3, r4, r5, r6);	 Catch:{ all -> 0x0134 }
        r12.recycle();
        return r20;
    L_0x00f1:
        r24 = r0;
        r21 = r5;
        r0 = r6;
        if (r18 != 0) goto L_0x00ff;
    L_0x00f8:
        r1 = "";
        r12.recycle();
        return r1;
    L_0x00ff:
        if (r17 != 0) goto L_0x0120;
    L_0x0101:
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0134 }
        r4 = r32.length();	 Catch:{ all -> 0x0134 }
        r4 = r18 + r4;
        r1.<init>(r4);	 Catch:{ all -> 0x0134 }
        r1.append(r0, r2, r3);	 Catch:{ all -> 0x0134 }
        r1.append(r10);	 Catch:{ all -> 0x0134 }
        r2 = r11 - r15;
        r1.append(r0, r15, r2);	 Catch:{ all -> 0x0134 }
        r2 = r1.toString();	 Catch:{ all -> 0x0134 }
        r12.recycle();
        return r2;
    L_0x0120:
        r1 = new android.text.SpannableStringBuilder;	 Catch:{ all -> 0x0134 }
        r1.<init>();	 Catch:{ all -> 0x0134 }
        r1.append(r7, r2, r3);	 Catch:{ all -> 0x0134 }
        r1.append(r10);	 Catch:{ all -> 0x0134 }
        r1.append(r7, r15, r11);	 Catch:{ all -> 0x0134 }
        r12.recycle();
        return r1;
    L_0x0134:
        r0 = move-exception;
        goto L_0x0141;
    L_0x0136:
        r0 = move-exception;
        r16 = r1;
        goto L_0x0141;
    L_0x013a:
        r0 = move-exception;
        goto L_0x013f;
    L_0x013c:
        r0 = move-exception;
        r13 = r26;
    L_0x013f:
        r16 = r27;
    L_0x0141:
        if (r12 == 0) goto L_0x0146;
    L_0x0143:
        r12.recycle();
    L_0x0146:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.ellipsize(java.lang.CharSequence, android.text.TextPaint, float, android.text.TextUtils$TruncateAt, boolean, android.text.TextUtils$EllipsizeCallback, android.text.TextDirectionHeuristic, java.lang.String):java.lang.CharSequence");
    }

    public static CharSequence listEllipsize(Context context, List<CharSequence> elements, String separator, TextPaint paint, float avail, int moreId) {
        List<CharSequence> list = elements;
        String str = "";
        if (list == null) {
            return str;
        }
        int totalLen = elements.size();
        if (totalLen == 0) {
            return str;
        }
        Resources res;
        BidiFormatter bidiFormatter;
        int i;
        String str2;
        int i2;
        if (context == null) {
            res = null;
            bidiFormatter = BidiFormatter.getInstance();
        } else {
            res = context.getResources();
            bidiFormatter = BidiFormatter.getInstance(res.getConfiguration().getLocales().get(0));
        }
        SpannableStringBuilder output = new SpannableStringBuilder();
        int[] endIndexes = new int[totalLen];
        for (i = 0; i < totalLen; i++) {
            output.append(bidiFormatter.unicodeWrap((CharSequence) list.get(i)));
            if (i != totalLen - 1) {
                output.append((CharSequence) separator);
            } else {
                str2 = separator;
            }
            endIndexes[i] = output.length();
        }
        str2 = separator;
        for (i = totalLen - 1; i >= 0; i--) {
            output.delete(endIndexes[i], output.length());
            int remainingElements = (totalLen - i) - 1;
            if (remainingElements > 0) {
                CharSequence morePiece;
                if (res == null) {
                    morePiece = ELLIPSIS_NORMAL;
                    i2 = moreId;
                } else {
                    morePiece = res.getQuantityString(moreId, remainingElements, Integer.valueOf(remainingElements));
                }
                output.append(bidiFormatter.unicodeWrap(morePiece));
            } else {
                i2 = moreId;
            }
            if (paint.measureText((CharSequence) output, 0, (int) output.length()) <= avail) {
                return output;
            }
        }
        TextPaint textPaint = paint;
        i2 = moreId;
        return str;
    }

    @Deprecated
    public static CharSequence commaEllipsize(CharSequence text, TextPaint p, float avail, String oneMore, String more) {
        return commaEllipsize(text, p, avail, oneMore, more, TextDirectionHeuristics.FIRSTSTRONG_LTR);
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f9  */
    @java.lang.Deprecated
    public static java.lang.CharSequence commaEllipsize(java.lang.CharSequence r22, android.text.TextPaint r23, float r24, java.lang.String r25, java.lang.String r26, android.text.TextDirectionHeuristic r27) {
        /*
        r7 = r22;
        r8 = 0;
        r9 = 0;
        r0 = r22.length();	 Catch:{ all -> 0x00ef }
        r3 = 0;
        r1 = r23;
        r2 = r22;
        r4 = r0;
        r5 = r27;
        r6 = r8;
        r1 = android.text.MeasuredParagraph.buildForMeasurement(r1, r2, r3, r4, r5, r6);	 Catch:{ all -> 0x00ef }
        r8 = r1;
        r1 = r8.getWholeWidth();	 Catch:{ all -> 0x00ef }
        r2 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1));
        if (r2 > 0) goto L_0x0029;
        r8.recycle();
        if (r9 == 0) goto L_0x0028;
    L_0x0025:
        r9.recycle();
    L_0x0028:
        return r7;
    L_0x0029:
        r2 = r8.getChars();	 Catch:{ all -> 0x00ef }
        r3 = 0;
        r4 = 0;
    L_0x002f:
        r5 = 44;
        if (r4 >= r0) goto L_0x003c;
    L_0x0033:
        r6 = r2[r4];	 Catch:{ all -> 0x00ef }
        if (r6 != r5) goto L_0x0039;
    L_0x0037:
        r3 = r3 + 1;
    L_0x0039:
        r4 = r4 + 1;
        goto L_0x002f;
    L_0x003c:
        r4 = r3 + 1;
        r6 = 0;
        r10 = "";
        r11 = 0;
        r12 = 0;
        r13 = r8.getWidths();	 Catch:{ all -> 0x00ef }
        r13 = r13.getRawArray();	 Catch:{ all -> 0x00ef }
        r14 = 0;
    L_0x004c:
        if (r14 >= r0) goto L_0x00d3;
    L_0x004e:
        r15 = (float) r11;	 Catch:{ all -> 0x00ef }
        r17 = r13[r14];	 Catch:{ all -> 0x00ef }
        r15 = r15 + r17;
        r11 = (int) r15;	 Catch:{ all -> 0x00ef }
        r15 = r2[r14];	 Catch:{ all -> 0x00ef }
        if (r15 != r5) goto L_0x00c3;
    L_0x0058:
        r12 = r12 + 1;
        r4 = r4 + -1;
        r15 = " ";
        r5 = 1;
        if (r4 != r5) goto L_0x007c;
    L_0x0061:
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0077 }
        r5.<init>();	 Catch:{ all -> 0x0077 }
        r5.append(r15);	 Catch:{ all -> 0x0077 }
        r15 = r25;
        r5.append(r15);	 Catch:{ all -> 0x00ef }
        r5 = r5.toString();	 Catch:{ all -> 0x00ef }
        r21 = r1;
        r1 = r26;
        goto L_0x009e;
    L_0x0077:
        r0 = move-exception;
        r15 = r25;
        goto L_0x00f0;
    L_0x007c:
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00ef }
        r5.<init>();	 Catch:{ all -> 0x00ef }
        r5.append(r15);	 Catch:{ all -> 0x00ef }
        r15 = 1;
        r15 = new java.lang.Object[r15];	 Catch:{ all -> 0x00ef }
        r17 = java.lang.Integer.valueOf(r4);	 Catch:{ all -> 0x00ef }
        r16 = 0;
        r15[r16] = r17;	 Catch:{ all -> 0x00ef }
        r21 = r1;
        r1 = r26;
        r15 = java.lang.String.format(r1, r15);	 Catch:{ all -> 0x00ed }
        r5.append(r15);	 Catch:{ all -> 0x00ed }
        r5 = r5.toString();	 Catch:{ all -> 0x00ed }
    L_0x009e:
        r17 = 0;
        r18 = r5.length();	 Catch:{ all -> 0x00ed }
        r15 = r23;
        r16 = r5;
        r19 = r27;
        r20 = r9;
        r15 = android.text.MeasuredParagraph.buildForMeasurement(r15, r16, r17, r18, r19, r20);	 Catch:{ all -> 0x00ed }
        r9 = r15;
        r15 = r9.getWholeWidth();	 Catch:{ all -> 0x00ed }
        r17 = r0;
        r0 = (float) r11;	 Catch:{ all -> 0x00ed }
        r0 = r0 + r15;
        r0 = (r0 > r24 ? 1 : (r0 == r24 ? 0 : -1));
        if (r0 > 0) goto L_0x00c9;
    L_0x00bd:
        r0 = r14 + 1;
        r6 = r5;
        r10 = r6;
        r6 = r0;
        goto L_0x00c9;
    L_0x00c3:
        r17 = r0;
        r21 = r1;
        r1 = r26;
    L_0x00c9:
        r14 = r14 + 1;
        r0 = r17;
        r1 = r21;
        r5 = 44;
        goto L_0x004c;
    L_0x00d3:
        r17 = r0;
        r21 = r1;
        r1 = r26;
        r0 = new android.text.SpannableStringBuilder;	 Catch:{ all -> 0x00ed }
        r0.<init>(r10);	 Catch:{ all -> 0x00ed }
        r5 = 0;
        r0.insert(r5, r7, r5, r6);	 Catch:{ all -> 0x00ed }
        r8.recycle();
        if (r9 == 0) goto L_0x00ec;
    L_0x00e9:
        r9.recycle();
    L_0x00ec:
        return r0;
    L_0x00ed:
        r0 = move-exception;
        goto L_0x00f2;
    L_0x00ef:
        r0 = move-exception;
    L_0x00f0:
        r1 = r26;
    L_0x00f2:
        if (r8 == 0) goto L_0x00f7;
    L_0x00f4:
        r8.recycle();
    L_0x00f7:
        if (r9 == 0) goto L_0x00fc;
    L_0x00f9:
        r9.recycle();
    L_0x00fc:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.commaEllipsize(java.lang.CharSequence, android.text.TextPaint, float, java.lang.String, java.lang.String, android.text.TextDirectionHeuristic):java.lang.CharSequence");
    }

    static boolean couldAffectRtl(char c) {
        return (1424 <= c && c <= 2303) || c == 8206 || c == 8207 || ((8234 <= c && c <= 8238) || ((8294 <= c && c <= 8297) || ((55296 <= c && c <= 57343) || ((64285 <= c && c <= 65023) || (65136 <= c && c <= 65278)))));
    }

    static boolean doesNotNeedBidi(char[] text, int start, int len) {
        int end = start + len;
        for (int i = start; i < end; i++) {
            if (couldAffectRtl(text[i])) {
                return false;
            }
        }
        return true;
    }

    static char[] obtain(int len) {
        char[] buf;
        synchronized (sLock) {
            buf = sTemp;
            sTemp = null;
        }
        if (buf == null || buf.length < len) {
            return ArrayUtils.newUnpaddedCharArray(len);
        }
        return buf;
    }

    static void recycle(char[] temp) {
        if (temp.length <= 1000) {
            synchronized (sLock) {
                sTemp = temp;
            }
        }
    }

    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\"') {
                sb.append("&quot;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c != DateFormat.QUOTE) {
                sb.append(c);
            } else {
                sb.append("&#39;");
            }
        }
        return sb.toString();
    }

    public static CharSequence concat(CharSequence... text) {
        if (text.length == 0) {
            return "";
        }
        int i = 0;
        if (text.length == 1) {
            return text[0];
        }
        int length;
        CharSequence piece;
        boolean spanned = false;
        for (CharSequence piece2 : text) {
            if (piece2 instanceof Spanned) {
                spanned = true;
                break;
            }
        }
        if (spanned) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            length = text.length;
            while (i < length) {
                piece2 = text[i];
                ssb.append(piece2 == null ? "null" : piece2);
                i++;
            }
            return new SpannedString(ssb);
        }
        StringBuilder sb = new StringBuilder();
        length = text.length;
        while (i < length) {
            sb.append(text[i]);
            i++;
        }
        return sb.toString();
    }

    public static boolean isGraphic(CharSequence str) {
        int len = str.length();
        int i = 0;
        while (i < len) {
            int cp = Character.codePointAt(str, i);
            int gc = Character.getType(cp);
            if (gc != 15 && gc != 16 && gc != 19 && gc != 0 && gc != 13 && gc != 14 && gc != 12) {
                return true;
            }
            i += Character.charCount(cp);
        }
        return false;
    }

    @Deprecated
    public static boolean isGraphic(char c) {
        int gc = Character.getType(c);
        return (gc == 15 || gc == 16 || gc == 19 || gc == 0 || gc == 13 || gc == 14 || gc == 12) ? false : true;
    }

    public static boolean isDigitsOnly(CharSequence str) {
        int len = str.length();
        int i = 0;
        while (i < len) {
            int cp = Character.codePointAt(str, i);
            if (!Character.isDigit(cp)) {
                return false;
            }
            i += Character.charCount(cp);
        }
        return true;
    }

    public static boolean isPrintableAscii(char c) {
        return (' ' <= c && c <= '~') || c == CharUtils.CR || c == 10;
    }

    @UnsupportedAppUsage
    public static boolean isPrintableAsciiOnly(CharSequence str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!isPrintableAscii(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int getCapsMode(CharSequence cs, int off, int reqModes) {
        if (off < 0) {
            return 0;
        }
        int mode = 0;
        if ((reqModes & 4096) != 0) {
            mode = 0 | 4096;
        }
        if ((reqModes & 24576) == 0) {
            return mode;
        }
        char charAt;
        int i = off;
        while (i > 0) {
            char c = cs.charAt(i - 1);
            if (c != '\"' && c != DateFormat.QUOTE && Character.getType(c) != 21) {
                break;
            }
            i--;
        }
        int j = i;
        while (j > 0) {
            charAt = cs.charAt(j - 1);
            char c2 = charAt;
            if (charAt != ' ' && c2 != 9) {
                break;
            }
            j--;
        }
        if (j == 0 || cs.charAt(j - 1) == 10) {
            return mode | 8192;
        }
        if ((reqModes & 16384) == 0) {
            if (i != j) {
                mode |= 8192;
            }
            return mode;
        } else if (i == j) {
            return mode;
        } else {
            while (j > 0) {
                charAt = cs.charAt(j - 1);
                if (charAt != '\"' && charAt != DateFormat.QUOTE && Character.getType(charAt) != 22) {
                    break;
                }
                j--;
            }
            if (j > 0) {
                char c3 = cs.charAt(j - 1);
                if (c3 == ClassUtils.PACKAGE_SEPARATOR_CHAR || c3 == '?' || c3 == '!') {
                    if (c3 == ClassUtils.PACKAGE_SEPARATOR_CHAR) {
                        for (int k = j - 2; k >= 0; k--) {
                            c3 = cs.charAt(k);
                            if (c3 == ClassUtils.PACKAGE_SEPARATOR_CHAR) {
                                return mode;
                            }
                            if (!Character.isLetter(c3)) {
                                break;
                            }
                        }
                    }
                    return mode | 16384;
                }
            }
            return mode;
        }
    }

    public static boolean delimitedStringContains(String delimitedString, char delimiter, String item) {
        if (isEmpty(delimitedString) || isEmpty(item)) {
            return false;
        }
        int pos = -1;
        int length = delimitedString.length();
        while (true) {
            int indexOf = delimitedString.indexOf(item, pos + 1);
            pos = indexOf;
            if (indexOf == -1) {
                return false;
            }
            if (pos <= 0 || delimitedString.charAt(pos - 1) == delimiter) {
                indexOf = item.length() + pos;
                if (indexOf == length || delimitedString.charAt(indexOf) == delimiter) {
                    return true;
                }
            }
        }
    }

    public static <T> T[] removeEmptySpans(T[] spans, Spanned spanned, Class<T> klass) {
        T[] copy = null;
        int count = 0;
        for (int i = 0; i < spans.length; i++) {
            T span = spans[i];
            if (spanned.getSpanStart(span) == spanned.getSpanEnd(span)) {
                if (copy == null) {
                    copy = (Object[]) Array.newInstance(klass, spans.length - 1);
                    System.arraycopy(spans, 0, copy, 0, i);
                    count = i;
                }
            } else if (copy != null) {
                copy[count] = span;
                count++;
            }
        }
        if (copy == null) {
            return spans;
        }
        Object[] result = (Object[]) Array.newInstance(klass, count);
        System.arraycopy(copy, 0, result, 0, count);
        return result;
    }

    @UnsupportedAppUsage
    public static long packRangeInLong(int start, int end) {
        return (((long) start) << 32) | ((long) end);
    }

    @UnsupportedAppUsage
    public static int unpackRangeStartFromLong(long range) {
        return (int) (range >>> 32);
    }

    @UnsupportedAppUsage
    public static int unpackRangeEndFromLong(long range) {
        return (int) (4294967295L & range);
    }

    public static int getLayoutDirectionFromLocale(Locale locale) {
        if ((locale == null || locale.equals(Locale.ROOT) || !ULocale.forLocale(locale).isRightToLeft()) && !((Boolean) DisplayProperties.debug_force_rtl().orElse(Boolean.valueOf(false))).booleanValue()) {
            return 0;
        }
        return 1;
    }

    public static CharSequence formatSelectedCount(int count) {
        return Resources.getSystem().getQuantityString(R.plurals.selected_count, count, Integer.valueOf(count));
    }

    public static boolean hasStyleSpan(Spanned spanned) {
        Preconditions.checkArgument(spanned != null);
        for (Class<?> clazz : new Class[]{CharacterStyle.class, ParagraphStyle.class, UpdateAppearance.class}) {
            if (spanned.nextSpanTransition(-1, spanned.length(), clazz) < spanned.length()) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence trimNoCopySpans(CharSequence charSequence) {
        if (charSequence == null || !(charSequence instanceof Spanned)) {
            return charSequence;
        }
        return new SpannableStringBuilder(charSequence);
    }

    public static void wrap(StringBuilder builder, String start, String end) {
        builder.insert(0, start);
        builder.append(end);
    }

    public static <T extends CharSequence> T trimToParcelableSize(T text) {
        return trimToSize(text, 100000);
    }

    public static <T extends CharSequence> T trimToSize(T text, int size) {
        Preconditions.checkArgument(size > 0);
        if (isEmpty(text) || text.length() <= size) {
            return text;
        }
        if (Character.isHighSurrogate(text.charAt(size - 1)) && Character.isLowSurrogate(text.charAt(size))) {
            size--;
        }
        return text.subSequence(0, size);
    }

    public static <T extends CharSequence> T trimToLengthWithEllipsis(T text, int size) {
        T trimmed = trimToSize(text, size);
        if (trimmed.length() >= text.length()) {
            return trimmed;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trimmed.toString());
        stringBuilder.append(Session.TRUNCATE_STRING);
        return stringBuilder.toString();
    }

    private static boolean isNewline(int codePoint) {
        int type = Character.getType(codePoint);
        return type == 14 || type == 13 || codePoint == 10;
    }

    private static boolean isWhiteSpace(int codePoint) {
        return Character.isWhitespace(codePoint) || codePoint == 160;
    }

    public static String withoutPrefix(String prefix, String str) {
        if (prefix == null || str == null) {
            return str;
        }
        return str.startsWith(prefix) ? str.substring(prefix.length()) : str;
    }

    public static CharSequence makeSafeForPresentation(String unclean, int maxCharactersToConsider, float ellipsizeDip, int flags) {
        String shortString;
        int i = maxCharactersToConsider;
        float f = ellipsizeDip;
        int i2 = flags;
        boolean z = true;
        boolean onlyKeepFirstLine = (i2 & 4) != 0;
        boolean forceSingleLine = (i2 & 2) != 0;
        boolean trim = (i2 & 1) != 0;
        Preconditions.checkNotNull(unclean);
        Preconditions.checkArgumentNonnegative(maxCharactersToConsider);
        Preconditions.checkArgumentNonNegative(f, "ellipsizeDip");
        Preconditions.checkFlagsArgument(i2, 7);
        if (onlyKeepFirstLine && forceSingleLine) {
            z = false;
        }
        Preconditions.checkArgument(z, "Cannot set SAFE_STRING_FLAG_SINGLE_LINE and SAFE_STRING_FLAG_FIRST_LINE at thesame time");
        if (i > 0) {
            shortString = unclean.substring(0, Math.min(unclean.length(), i));
        } else {
            String str = unclean;
            shortString = unclean;
        }
        StringWithRemovedChars gettingCleaned = new StringWithRemovedChars(Html.fromHtml(shortString).toString());
        int firstNonWhiteSpace = -1;
        int firstTrailingWhiteSpace = -1;
        int uncleanLength = gettingCleaned.length();
        int offset = 0;
        while (offset < uncleanLength) {
            int codePoint = gettingCleaned.codePointAt(offset);
            int type = Character.getType(codePoint);
            int codePointLen = Character.charCount(codePoint);
            boolean isNewline = isNewline(codePoint);
            if (onlyKeepFirstLine && isNewline) {
                gettingCleaned.removeAllCharAfter(offset);
                break;
            }
            if (forceSingleLine && isNewline) {
                gettingCleaned.removeRange(offset, offset + codePointLen);
            } else if (type == 15 && !isNewline) {
                gettingCleaned.removeRange(offset, offset + codePointLen);
            } else if (trim && !isWhiteSpace(codePoint)) {
                if (firstNonWhiteSpace == -1) {
                    firstNonWhiteSpace = offset;
                }
                firstTrailingWhiteSpace = offset + codePointLen;
            }
            offset += codePointLen;
        }
        if (trim) {
            if (firstNonWhiteSpace == -1) {
                gettingCleaned.removeAllCharAfter(0);
            } else {
                if (firstNonWhiteSpace > 0) {
                    gettingCleaned.removeAllCharBefore(firstNonWhiteSpace);
                }
                if (firstTrailingWhiteSpace < uncleanLength) {
                    gettingCleaned.removeAllCharAfter(firstTrailingWhiteSpace);
                }
            }
        }
        if (f == 0.0f) {
            return gettingCleaned.toString();
        }
        TextPaint paint = new TextPaint();
        paint.setTextSize(42.0f);
        return ellipsize(gettingCleaned.toString(), paint, f, TruncateAt.END);
    }
}
