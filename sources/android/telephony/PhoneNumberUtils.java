package android.telephony;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.location.Country;
import android.location.CountryDetector;
import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.PersistableBundle;
import android.telecom.PhoneAccount;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.TtsSpan;
import android.text.style.TtsSpan.TelephoneBuilder;
import android.util.SparseIntArray;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.android.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.android.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource;
import com.android.internal.R;
import com.android.internal.midi.MidiConstants;
import com.android.internal.telephony.TelephonyProperties;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberUtils {
    private static final String BCD_CALLED_PARTY_EXTENDED = "*#abc";
    private static final String BCD_EF_ADN_EXTENDED = "*#,N;";
    public static final int BCD_EXTENDED_TYPE_CALLED_PARTY = 2;
    public static final int BCD_EXTENDED_TYPE_EF_ADN = 1;
    private static final int CCC_LENGTH = COUNTRY_CALLING_CALL.length;
    private static final String CLIR_OFF = "#31#";
    private static final String CLIR_ON = "*31#";
    private static final boolean[] COUNTRY_CALLING_CALL = new boolean[]{true, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, true, false, true, true, true, true, true, false, true, false, false, true, true, false, false, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, false, true, false, false, true, true, true, true, true, true, true, false, false, true, false};
    private static final boolean DBG = false;
    public static final int FORMAT_JAPAN = 2;
    public static final int FORMAT_NANP = 1;
    public static final int FORMAT_UNKNOWN = 0;
    private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN = Pattern.compile("[\\+]?[0-9.-]+");
    private static final String JAPAN_ISO_COUNTRY_CODE = "JP";
    private static final SparseIntArray KEYPAD_MAP = new SparseIntArray();
    private static final String KOREA_ISO_COUNTRY_CODE = "KR";
    static final String LOG_TAG = "PhoneNumberUtils";
    @UnsupportedAppUsage
    static final int MIN_MATCH = 7;
    private static final String[] NANP_COUNTRIES = new String[]{"US", "CA", "AS", "AI", "AG", "BS", "BB", "BM", "VG", "KY", "DM", "DO", "GD", "GU", "JM", "PR", "MS", "MP", "KN", "LC", "VC", "TT", "TC", "VI"};
    private static final String NANP_IDP_STRING = "011";
    private static final int NANP_LENGTH = 10;
    private static final int NANP_STATE_DASH = 4;
    private static final int NANP_STATE_DIGIT = 1;
    private static final int NANP_STATE_ONE = 3;
    private static final int NANP_STATE_PLUS = 2;
    public static final char PAUSE = ',';
    private static final char PLUS_SIGN_CHAR = '+';
    private static final String PLUS_SIGN_STRING = "+";
    public static final int TOA_International = 145;
    public static final int TOA_Unknown = 129;
    public static final char WAIT = ';';
    public static final char WILD = 'N';
    private static String[] sConvertToEmergencyMap = null;
    private static Country sCountryDetector = null;

    @Retention(RetentionPolicy.SOURCE)
    public @interface BcdExtendType {
    }

    private static class CountryCallingCodeAndNewIndex {
        public final int countryCallingCode;
        public final int newIndex;

        public CountryCallingCodeAndNewIndex(int countryCode, int newIndex) {
            this.countryCallingCode = countryCode;
            this.newIndex = newIndex;
        }
    }

    static {
        KEYPAD_MAP.put(97, 50);
        KEYPAD_MAP.put(98, 50);
        KEYPAD_MAP.put(99, 50);
        KEYPAD_MAP.put(65, 50);
        KEYPAD_MAP.put(66, 50);
        KEYPAD_MAP.put(67, 50);
        KEYPAD_MAP.put(100, 51);
        KEYPAD_MAP.put(101, 51);
        KEYPAD_MAP.put(102, 51);
        KEYPAD_MAP.put(68, 51);
        KEYPAD_MAP.put(69, 51);
        KEYPAD_MAP.put(70, 51);
        KEYPAD_MAP.put(103, 52);
        KEYPAD_MAP.put(104, 52);
        KEYPAD_MAP.put(105, 52);
        KEYPAD_MAP.put(71, 52);
        KEYPAD_MAP.put(72, 52);
        KEYPAD_MAP.put(73, 52);
        KEYPAD_MAP.put(106, 53);
        KEYPAD_MAP.put(107, 53);
        KEYPAD_MAP.put(108, 53);
        KEYPAD_MAP.put(74, 53);
        KEYPAD_MAP.put(75, 53);
        KEYPAD_MAP.put(76, 53);
        KEYPAD_MAP.put(109, 54);
        KEYPAD_MAP.put(110, 54);
        KEYPAD_MAP.put(111, 54);
        KEYPAD_MAP.put(77, 54);
        KEYPAD_MAP.put(78, 54);
        KEYPAD_MAP.put(79, 54);
        KEYPAD_MAP.put(112, 55);
        KEYPAD_MAP.put(113, 55);
        KEYPAD_MAP.put(114, 55);
        KEYPAD_MAP.put(115, 55);
        KEYPAD_MAP.put(80, 55);
        KEYPAD_MAP.put(81, 55);
        KEYPAD_MAP.put(82, 55);
        KEYPAD_MAP.put(83, 55);
        KEYPAD_MAP.put(116, 56);
        KEYPAD_MAP.put(117, 56);
        KEYPAD_MAP.put(118, 56);
        KEYPAD_MAP.put(84, 56);
        KEYPAD_MAP.put(85, 56);
        KEYPAD_MAP.put(86, 56);
        KEYPAD_MAP.put(119, 57);
        KEYPAD_MAP.put(120, 57);
        KEYPAD_MAP.put(121, 57);
        KEYPAD_MAP.put(122, 57);
        KEYPAD_MAP.put(87, 57);
        KEYPAD_MAP.put(88, 57);
        KEYPAD_MAP.put(89, 57);
        KEYPAD_MAP.put(90, 57);
    }

    public static boolean isISODigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static final boolean is12Key(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#';
    }

    public static final boolean isDialable(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == PLUS_SIGN_CHAR || c == WILD;
    }

    public static final boolean isReallyDialable(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == PLUS_SIGN_CHAR;
    }

    public static final boolean isNonSeparator(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == PLUS_SIGN_CHAR || c == WILD || c == ';' || c == ',';
    }

    public static final boolean isStartsPostDial(char c) {
        return c == ',' || c == ';';
    }

    private static boolean isPause(char c) {
        return c == 'p' || c == 'P';
    }

    private static boolean isToneWait(char c) {
        return c == 'w' || c == 'W';
    }

    private static boolean isSeparator(char ch) {
        return !isDialable(ch) && ((DateFormat.AM_PM > ch || ch > DateFormat.TIME_ZONE) && (DateFormat.CAPITAL_AM_PM > ch || ch > 'Z'));
    }

    /* JADX WARNING: Missing block: B:24:0x006c, code skipped:
            if (r12 != null) goto L_0x006e;
     */
    /* JADX WARNING: Missing block: B:25:0x006e, code skipped:
            r12.close();
     */
    /* JADX WARNING: Missing block: B:30:0x007d, code skipped:
            if (r12 == null) goto L_0x0080;
     */
    /* JADX WARNING: Missing block: B:31:0x0080, code skipped:
            return r0;
     */
    public static java.lang.String getNumberFromIntent(android.content.Intent r13, android.content.Context r14) {
        /*
        r0 = 0;
        r7 = r13.getData();
        r1 = 0;
        if (r7 != 0) goto L_0x0009;
    L_0x0008:
        return r1;
    L_0x0009:
        r8 = r7.getScheme();
        r2 = "tel";
        r2 = r8.equals(r2);
        if (r2 != 0) goto L_0x0087;
    L_0x0016:
        r2 = "sip";
        r2 = r8.equals(r2);
        if (r2 == 0) goto L_0x0021;
    L_0x001f:
        goto L_0x0087;
    L_0x0021:
        if (r14 != 0) goto L_0x0024;
    L_0x0023:
        return r1;
    L_0x0024:
        r9 = r13.resolveType(r14);
        r1 = 0;
        r10 = r7.getAuthority();
        r2 = "contacts";
        r2 = r2.equals(r10);
        if (r2 == 0) goto L_0x003a;
    L_0x0035:
        r1 = "number";
        r11 = r1;
        goto L_0x0047;
    L_0x003a:
        r2 = "com.android.contacts";
        r2 = r2.equals(r10);
        if (r2 == 0) goto L_0x0046;
    L_0x0042:
        r1 = "data1";
        r11 = r1;
        goto L_0x0047;
    L_0x0046:
        r11 = r1;
    L_0x0047:
        r12 = 0;
        r1 = r14.getContentResolver();	 Catch:{ RuntimeException -> 0x0074 }
        r2 = 1;
        r3 = new java.lang.String[r2];	 Catch:{ RuntimeException -> 0x0074 }
        r2 = 0;
        r3[r2] = r11;	 Catch:{ RuntimeException -> 0x0074 }
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r2 = r7;
        r1 = r1.query(r2, r3, r4, r5, r6);	 Catch:{ RuntimeException -> 0x0074 }
        r12 = r1;
        if (r12 == 0) goto L_0x006c;
    L_0x005d:
        r1 = r12.moveToFirst();	 Catch:{ RuntimeException -> 0x0074 }
        if (r1 == 0) goto L_0x006c;
    L_0x0063:
        r1 = r12.getColumnIndex(r11);	 Catch:{ RuntimeException -> 0x0074 }
        r1 = r12.getString(r1);	 Catch:{ RuntimeException -> 0x0074 }
        r0 = r1;
    L_0x006c:
        if (r12 == 0) goto L_0x0080;
    L_0x006e:
        r12.close();
        goto L_0x0080;
    L_0x0072:
        r1 = move-exception;
        goto L_0x0081;
    L_0x0074:
        r1 = move-exception;
        r2 = "PhoneNumberUtils";
        r3 = "Error getting phone number.";
        android.telephony.Rlog.e(r2, r3, r1);	 Catch:{ all -> 0x0072 }
        if (r12 == 0) goto L_0x0080;
    L_0x007f:
        goto L_0x006e;
    L_0x0080:
        return r0;
    L_0x0081:
        if (r12 == 0) goto L_0x0086;
    L_0x0083:
        r12.close();
    L_0x0086:
        throw r1;
    L_0x0087:
        r1 = r7.getSchemeSpecificPart();
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.telephony.PhoneNumberUtils.getNumberFromIntent(android.content.Intent, android.content.Context):java.lang.String");
    }

    public static String extractNetworkPortion(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                ret.append(digit);
            } else if (c == PLUS_SIGN_CHAR) {
                String prefix = ret.toString();
                if (prefix.length() == 0 || prefix.equals(CLIR_ON) || prefix.equals(CLIR_OFF)) {
                    ret.append(c);
                }
            } else if (isDialable(c)) {
                ret.append(c);
            } else if (isStartsPostDial(c)) {
                break;
            }
        }
        return ret.toString();
    }

    @UnsupportedAppUsage
    public static String extractNetworkPortionAlt(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        boolean haveSeenPlus = false;
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            if (c == PLUS_SIGN_CHAR) {
                if (haveSeenPlus) {
                    continue;
                } else {
                    haveSeenPlus = true;
                }
            }
            if (isDialable(c)) {
                ret.append(c);
            } else if (isStartsPostDial(c)) {
                break;
            }
        }
        return ret.toString();
    }

    public static String stripSeparators(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                ret.append(digit);
            } else if (isNonSeparator(c)) {
                ret.append(c);
            }
        }
        return ret.toString();
    }

    public static String convertAndStrip(String phoneNumber) {
        return stripSeparators(convertKeypadLettersToDigits(phoneNumber));
    }

    @UnsupportedAppUsage
    public static String convertPreDial(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            if (isPause(c)) {
                c = ',';
            } else if (isToneWait(c)) {
                c = ';';
            }
            ret.append(c);
        }
        return ret.toString();
    }

    private static int minPositive(int a, int b) {
        if (a >= 0 && b >= 0) {
            return a < b ? a : b;
        } else if (a >= 0) {
            return a;
        } else {
            if (b >= 0) {
                return b;
            }
            return -1;
        }
    }

    private static void log(String msg) {
        Rlog.d(LOG_TAG, msg);
    }

    private static int indexOfLastNetworkChar(String a) {
        int origLength = a.length();
        int trimIndex = minPositive(a.indexOf(44), a.indexOf(59));
        if (trimIndex < 0) {
            return origLength - 1;
        }
        return trimIndex - 1;
    }

    public static String extractPostDialPortion(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        int s = phoneNumber.length();
        for (int i = indexOfLastNetworkChar(phoneNumber) + 1; i < s; i++) {
            char c = phoneNumber.charAt(i);
            if (isNonSeparator(c)) {
                ret.append(c);
            }
        }
        return ret.toString();
    }

    public static boolean compare(String a, String b) {
        return compare(a, b, false);
    }

    public static boolean compare(Context context, String a, String b) {
        return compare(a, b, context.getResources().getBoolean(R.bool.config_use_strict_phone_number_comparation));
    }

    @UnsupportedAppUsage
    public static boolean compare(String a, String b, boolean useStrictComparation) {
        return useStrictComparation ? compareStrictly(a, b) : compareLoosely(a, b);
    }

    @UnsupportedAppUsage
    public static boolean compareLoosely(String a, String b) {
        int numNonDialableCharsInA = 0;
        int numNonDialableCharsInB = 0;
        boolean z = false;
        if (a == null || b == null) {
            if (a == b) {
                z = true;
            }
            return z;
        } else if (a.length() == 0 || b.length() == 0) {
            return false;
        } else {
            int ia = indexOfLastNetworkChar(a);
            int ib = indexOfLastNetworkChar(b);
            int matched = 0;
            while (ia >= 0 && ib >= 0) {
                boolean skipCmp = false;
                char ca = a.charAt(ia);
                if (!isDialable(ca)) {
                    ia--;
                    skipCmp = true;
                    numNonDialableCharsInA++;
                }
                char cb = b.charAt(ib);
                if (!isDialable(cb)) {
                    ib--;
                    skipCmp = true;
                    numNonDialableCharsInB++;
                }
                if (!skipCmp) {
                    if (cb != ca && ca != WILD && cb != WILD) {
                        break;
                    }
                    ia--;
                    ib--;
                    matched++;
                }
            }
            if (matched < 7) {
                int effectiveALen = a.length() - numNonDialableCharsInA;
                return effectiveALen == b.length() - numNonDialableCharsInB && effectiveALen == matched;
            } else if (matched >= 7 && (ia < 0 || ib < 0)) {
                return true;
            } else {
                if (matchIntlPrefix(a, ia + 1) && matchIntlPrefix(b, ib + 1)) {
                    return true;
                }
                if (matchTrunkPrefix(a, ia + 1) && matchIntlPrefixAndCC(b, ib + 1)) {
                    return true;
                }
                return matchTrunkPrefix(b, ib + 1) && matchIntlPrefixAndCC(a, ia + 1);
            }
        }
    }

    @UnsupportedAppUsage
    public static boolean compareStrictly(String a, String b) {
        return compareStrictly(a, b, true);
    }

    @UnsupportedAppUsage
    public static boolean compareStrictly(String a, String b, boolean acceptInvalidCCCPrefix) {
        boolean z;
        String str = a;
        String str2 = b;
        boolean z2 = acceptInvalidCCCPrefix;
        if (str == null) {
            z = true;
        } else if (str2 == null) {
            z = true;
        } else if (a.length() == 0 && b.length() == 0) {
            return false;
        } else {
            int tmp;
            char chB;
            int forwardIndexA = 0;
            int forwardIndexB = 0;
            CountryCallingCodeAndNewIndex cccA = tryGetCountryCallingCodeAndNewIndex(str, z2);
            CountryCallingCodeAndNewIndex cccB = tryGetCountryCallingCodeAndNewIndex(b, acceptInvalidCCCPrefix);
            boolean bothHasCountryCallingCode = false;
            boolean okToIgnorePrefix = true;
            boolean trunkPrefixIsOmittedA = false;
            boolean trunkPrefixIsOmittedB = false;
            if (cccA == null || cccB == null) {
                if (cccA == null && cccB == null) {
                    okToIgnorePrefix = false;
                } else {
                    if (cccA != null) {
                        forwardIndexA = cccA.newIndex;
                    } else {
                        tmp = tryGetTrunkPrefixOmittedIndex(str, 0);
                        if (tmp >= 0) {
                            forwardIndexA = tmp;
                            trunkPrefixIsOmittedA = true;
                        }
                    }
                    if (cccB != null) {
                        forwardIndexB = cccB.newIndex;
                    } else {
                        tmp = tryGetTrunkPrefixOmittedIndex(str2, 0);
                        if (tmp >= 0) {
                            forwardIndexB = tmp;
                            trunkPrefixIsOmittedB = true;
                        }
                    }
                }
            } else if (cccA.countryCallingCode != cccB.countryCallingCode) {
                return false;
            } else {
                okToIgnorePrefix = false;
                bothHasCountryCallingCode = true;
                forwardIndexA = cccA.newIndex;
                forwardIndexB = cccB.newIndex;
            }
            tmp = a.length() - 1;
            int backwardIndexB = b.length() - 1;
            while (tmp >= forwardIndexA && backwardIndexB >= forwardIndexB) {
                boolean skip_compare = false;
                char chA = str.charAt(tmp);
                chB = str2.charAt(backwardIndexB);
                if (isSeparator(chA)) {
                    tmp--;
                    skip_compare = true;
                }
                if (isSeparator(chB)) {
                    backwardIndexB--;
                    skip_compare = true;
                }
                if (!skip_compare) {
                    if (chA != chB) {
                        return false;
                    }
                    tmp--;
                    backwardIndexB--;
                }
            }
            if (!okToIgnorePrefix) {
                boolean maybeNamp = !bothHasCountryCallingCode;
                while (tmp >= forwardIndexA) {
                    chB = str.charAt(tmp);
                    if (isDialable(chB)) {
                        if (!maybeNamp || tryGetISODigit(chB) != 1) {
                            return false;
                        }
                        maybeNamp = false;
                    }
                    tmp--;
                    z2 = acceptInvalidCCCPrefix;
                }
                while (backwardIndexB >= forwardIndexB) {
                    char chB2 = str2.charAt(backwardIndexB);
                    if (isDialable(chB2)) {
                        if (!maybeNamp || tryGetISODigit(chB2) != 1) {
                            return false;
                        }
                        maybeNamp = false;
                    }
                    backwardIndexB--;
                }
            } else if ((!trunkPrefixIsOmittedA || forwardIndexA > tmp) && checkPrefixIsIgnorable(str, forwardIndexA, tmp)) {
                if ((trunkPrefixIsOmittedB && forwardIndexB <= backwardIndexB) || !checkPrefixIsIgnorable(str2, forwardIndexB, backwardIndexB)) {
                    if (z2) {
                        return compare(str, str2, false);
                    }
                    return false;
                }
            } else if (z2) {
                return compare(str, str2, false);
            } else {
                return false;
            }
            return true;
        }
        if (str != str2) {
            z = false;
        }
        return z;
    }

    public static String toCallerIDMinMatch(String phoneNumber) {
        return internalGetStrippedReversed(extractNetworkPortionAlt(phoneNumber), 7);
    }

    public static String getStrippedReversed(String phoneNumber) {
        String np = extractNetworkPortionAlt(phoneNumber);
        if (np == null) {
            return null;
        }
        return internalGetStrippedReversed(np, np.length());
    }

    private static String internalGetStrippedReversed(String np, int numDigits) {
        if (np == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder(numDigits);
        int length = np.length();
        int i = length - 1;
        int s = length;
        while (i >= 0 && s - i <= numDigits) {
            ret.append(np.charAt(i));
            i--;
        }
        return ret.toString();
    }

    public static String stringFromStringAndTOA(String s, int TOA) {
        if (s == null) {
            return null;
        }
        if (TOA != 145 || s.length() <= 0 || s.charAt(0) == PLUS_SIGN_CHAR) {
            return s;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PLUS_SIGN_STRING);
        stringBuilder.append(s);
        return stringBuilder.toString();
    }

    public static int toaFromString(String s) {
        if (s == null || s.length() <= 0 || s.charAt(0) != PLUS_SIGN_CHAR) {
            return 129;
        }
        return 145;
    }

    @Deprecated
    public static String calledPartyBCDToString(byte[] bytes, int offset, int length) {
        return calledPartyBCDToString(bytes, offset, length, 1);
    }

    public static String calledPartyBCDToString(byte[] bytes, int offset, int length, int bcdExtType) {
        boolean prependPlus = false;
        StringBuilder ret = new StringBuilder((length * 2) + 1);
        String str = "";
        if (length < 2) {
            return str;
        }
        if ((bytes[offset] & 240) == 144) {
            prependPlus = true;
        }
        internalCalledPartyBCDFragmentToString(ret, bytes, offset + 1, length - 1, bcdExtType);
        if (prependPlus && ret.length() == 0) {
            return str;
        }
        if (prependPlus) {
            String retString = ret.toString();
            Matcher m = Pattern.compile("(^[#*])(.*)([#*])(.*)(#)$").matcher(retString);
            boolean matches = m.matches();
            String str2 = PLUS_SIGN_STRING;
            if (!matches) {
                Matcher m2 = Pattern.compile("(^[#*])(.*)([#*])(.*)").matcher(retString);
                if (m2.matches()) {
                    ret = new StringBuilder();
                    ret.append(m2.group(1));
                    ret.append(m2.group(2));
                    ret.append(m2.group(3));
                    ret.append(str2);
                    ret.append(m2.group(4));
                } else {
                    ret = new StringBuilder();
                    ret.append(PLUS_SIGN_CHAR);
                    ret.append(retString);
                }
            } else if (str.equals(m.group(2))) {
                ret = new StringBuilder();
                ret.append(m.group(1));
                ret.append(m.group(3));
                ret.append(m.group(4));
                ret.append(m.group(5));
                ret.append(str2);
            } else {
                ret = new StringBuilder();
                ret.append(m.group(1));
                ret.append(m.group(2));
                ret.append(m.group(3));
                ret.append(str2);
                ret.append(m.group(4));
                ret.append(m.group(5));
            }
        }
        return ret.toString();
    }

    private static void internalCalledPartyBCDFragmentToString(StringBuilder sb, byte[] bytes, int offset, int length, int bcdExtType) {
        int i = offset;
        while (i < length + offset) {
            char c = bcdToChar((byte) (bytes[i] & 15), bcdExtType);
            if (c != 0) {
                sb.append(c);
                byte b = (byte) ((bytes[i] >> 4) & MidiConstants.STATUS_CHANNEL_MASK);
                if (b == MidiConstants.STATUS_CHANNEL_MASK && i + 1 == length + offset) {
                    break;
                }
                c = bcdToChar(b, bcdExtType);
                if (c != 0) {
                    sb.append(c);
                    i++;
                } else {
                    return;
                }
            }
            return;
        }
    }

    @Deprecated
    public static String calledPartyBCDFragmentToString(byte[] bytes, int offset, int length) {
        return calledPartyBCDFragmentToString(bytes, offset, length, 1);
    }

    public static String calledPartyBCDFragmentToString(byte[] bytes, int offset, int length, int bcdExtType) {
        StringBuilder ret = new StringBuilder(length * 2);
        internalCalledPartyBCDFragmentToString(ret, bytes, offset, length, bcdExtType);
        return ret.toString();
    }

    private static char bcdToChar(byte b, int bcdExtType) {
        if (b < (byte) 10) {
            return (char) (b + 48);
        }
        String extended = null;
        if (1 == bcdExtType) {
            extended = BCD_EF_ADN_EXTENDED;
        } else if (2 == bcdExtType) {
            extended = BCD_CALLED_PARTY_EXTENDED;
        }
        if (extended == null || b - 10 >= extended.length()) {
            return 0;
        }
        return extended.charAt(b - 10);
    }

    private static int charToBCD(char c, int bcdExtType) {
        if ('0' <= c && c <= '9') {
            return c - 48;
        }
        String extended = null;
        if (1 == bcdExtType) {
            extended = BCD_EF_ADN_EXTENDED;
        } else if (2 == bcdExtType) {
            extended = BCD_CALLED_PARTY_EXTENDED;
        }
        if (extended != null && extended.indexOf(c) != -1) {
            return extended.indexOf(c) + 10;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("invalid char for BCD ");
        stringBuilder.append(c);
        throw new RuntimeException(stringBuilder.toString());
    }

    public static boolean isWellFormedSmsAddress(String address) {
        String networkPortion = extractNetworkPortion(address);
        return (networkPortion.equals(PLUS_SIGN_STRING) || TextUtils.isEmpty(networkPortion) || !isDialable(networkPortion)) ? false : true;
    }

    public static boolean isGlobalPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        return GLOBAL_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    private static boolean isDialable(String address) {
        int count = address.length();
        for (int i = 0; i < count; i++) {
            if (!isDialable(address.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNonSeparator(String address) {
        int count = address.length();
        for (int i = 0; i < count; i++) {
            if (!isNonSeparator(address.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static byte[] networkPortionToCalledPartyBCD(String s) {
        return numberToCalledPartyBCDHelper(extractNetworkPortion(s), false, 1);
    }

    public static byte[] networkPortionToCalledPartyBCDWithLength(String s) {
        return numberToCalledPartyBCDHelper(extractNetworkPortion(s), true, 1);
    }

    @Deprecated
    public static byte[] numberToCalledPartyBCD(String number) {
        return numberToCalledPartyBCD(number, 1);
    }

    public static byte[] numberToCalledPartyBCD(String number, int bcdExtType) {
        return numberToCalledPartyBCDHelper(number, false, bcdExtType);
    }

    private static byte[] numberToCalledPartyBCDHelper(String number, boolean includeLength, int bcdExtType) {
        String str = number;
        int numberLenReal = number.length();
        int numberLenEffective = numberLenReal;
        char c = PLUS_SIGN_CHAR;
        boolean hasPlus = str.indexOf(43) != -1;
        if (hasPlus) {
            numberLenEffective--;
        }
        if (numberLenEffective == 0) {
            return null;
        }
        int i;
        int i2;
        int resultLen = (numberLenEffective + 1) / 2;
        int extraBytes = 1;
        if (includeLength) {
            extraBytes = 1 + 1;
        }
        resultLen += extraBytes;
        byte[] result = new byte[resultLen];
        int digitCount = 0;
        int i3 = 0;
        while (i3 < numberLenReal) {
            char c2 = str.charAt(i3);
            if (c2 == c) {
                i = bcdExtType;
            } else {
                int i4 = (digitCount >> 1) + extraBytes;
                result[i4] = (byte) (((byte) ((charToBCD(c2, bcdExtType) & 15) << ((digitCount & 1) == 1 ? 4 : 0))) | result[i4]);
                digitCount++;
            }
            i3++;
            c = PLUS_SIGN_CHAR;
        }
        i = bcdExtType;
        if ((digitCount & 1) == 1) {
            i2 = (digitCount >> 1) + extraBytes;
            result[i2] = (byte) (result[i2] | 240);
        }
        i2 = 0;
        if (includeLength) {
            int offset = 0 + 1;
            result[0] = (byte) (resultLen - 1);
            i2 = offset;
        }
        result[i2] = (byte) (hasPlus ? 145 : 129);
        return result;
    }

    @Deprecated
    public static String formatNumber(String source) {
        Editable text = new SpannableStringBuilder(source);
        formatNumber(text, getFormatTypeForLocale(Locale.getDefault()));
        return text.toString();
    }

    @Deprecated
    @UnsupportedAppUsage
    public static String formatNumber(String source, int defaultFormattingType) {
        Editable text = new SpannableStringBuilder(source);
        formatNumber(text, defaultFormattingType);
        return text.toString();
    }

    @Deprecated
    public static int getFormatTypeForLocale(Locale locale) {
        return getFormatTypeFromCountryCode(locale.getCountry());
    }

    @Deprecated
    public static void formatNumber(Editable text, int defaultFormattingType) {
        int formatType = defaultFormattingType;
        if (text.length() > 2 && text.charAt(0) == PLUS_SIGN_CHAR) {
            formatType = text.charAt(1) == '1' ? 1 : (text.length() >= 3 && text.charAt(1) == '8' && text.charAt(2) == '1') ? 2 : 0;
        }
        if (formatType == 0) {
            removeDashes(text);
        } else if (formatType == 1) {
            formatNanpNumber(text);
        } else if (formatType == 2) {
            formatJapaneseNumber(text);
        }
    }

    @Deprecated
    public static void formatNanpNumber(Editable text) {
        int length = text.length();
        if (length <= "+1-nnn-nnn-nnnn".length() && length > 5) {
            int i;
            CharSequence saved = text.subSequence(0, length);
            removeDashes(text);
            length = text.length();
            int[] dashPositions = new int[3];
            int numDashes = 0;
            int state = 1;
            int numDigits = 0;
            for (int i2 = 0; i2 < length; i2++) {
                char c = text.charAt(i2);
                if (c != PLUS_SIGN_CHAR) {
                    if (c != '-') {
                        switch (c) {
                            case '1':
                                if (numDigits == 0 || state == 2) {
                                    state = 3;
                                    continue;
                                }
                            case '0':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                if (state == 2) {
                                    text.replace(0, length, saved);
                                    return;
                                }
                                int numDashes2;
                                if (state == 3) {
                                    numDashes2 = numDashes + 1;
                                    dashPositions[numDashes] = i2;
                                } else if (state == 4 || !(numDigits == 3 || numDigits == 6)) {
                                    numDashes2 = numDashes;
                                } else {
                                    numDashes2 = numDashes + 1;
                                    dashPositions[numDashes] = i2;
                                }
                                numDigits++;
                                state = 1;
                                numDashes = numDashes2;
                                continue;
                            default:
                                break;
                        }
                        text.replace(0, length, saved);
                        return;
                    }
                    state = 4;
                } else if (i2 == 0) {
                    state = 2;
                } else {
                    text.replace(0, length, saved);
                    return;
                }
            }
            if (numDigits == 7) {
                numDashes--;
            }
            for (i = 0; i < numDashes; i++) {
                int pos = dashPositions[i];
                text.replace(pos + i, pos + i, "-");
            }
            i = text.length();
            while (i > 0 && text.charAt(i - 1) == '-') {
                text.delete(i - 1, i);
                i--;
            }
        }
    }

    @Deprecated
    public static void formatJapaneseNumber(Editable text) {
        JapanesePhoneNumberFormatter.format(text);
    }

    private static void removeDashes(Editable text) {
        int p = 0;
        while (p < text.length()) {
            if (text.charAt(p) == '-') {
                text.delete(p, p + 1);
            } else {
                p++;
            }
        }
    }

    public static String formatNumberToE164(String phoneNumber, String defaultCountryIso) {
        return formatNumberInternal(phoneNumber, defaultCountryIso, PhoneNumberFormat.E164);
    }

    public static String formatNumberToRFC3966(String phoneNumber, String defaultCountryIso) {
        return formatNumberInternal(phoneNumber, defaultCountryIso, PhoneNumberFormat.RFC3966);
    }

    private static String formatNumberInternal(String rawPhoneNumber, String defaultCountryIso, PhoneNumberFormat formatIdentifier) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber phoneNumber = util.parse(rawPhoneNumber, defaultCountryIso);
            if (util.isValidNumber(phoneNumber)) {
                return util.format(phoneNumber, formatIdentifier);
            }
        } catch (NumberParseException e) {
        }
        return null;
    }

    public static boolean isInternationalNumber(String phoneNumber, String defaultCountryIso) {
        boolean z = false;
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.startsWith("#") || phoneNumber.startsWith("*")) {
            return false;
        }
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        try {
            if (util.parseAndKeepRawInput(phoneNumber, defaultCountryIso).getCountryCode() != util.getCountryCodeForRegion(defaultCountryIso)) {
                z = true;
            }
            return z;
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static String formatNumber(String phoneNumber, String defaultCountryIso) {
        String str = JAPAN_ISO_COUNTRY_CODE;
        String str2 = KOREA_ISO_COUNTRY_CODE;
        if (phoneNumber.startsWith("#") || phoneNumber.startsWith("*")) {
            return phoneNumber;
        }
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        String result = null;
        try {
            PhoneNumber pn = util.parseAndKeepRawInput(phoneNumber, defaultCountryIso);
            if (str2.equalsIgnoreCase(defaultCountryIso) && pn.getCountryCode() == util.getCountryCodeForRegion(str2) && pn.getCountryCodeSource() == CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN) {
                result = util.format(pn, PhoneNumberFormat.NATIONAL);
                return result;
            } else if (str.equalsIgnoreCase(defaultCountryIso) && pn.getCountryCode() == util.getCountryCodeForRegion(str) && pn.getCountryCodeSource() == CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN) {
                result = util.format(pn, PhoneNumberFormat.NATIONAL);
                return result;
            } else {
                result = util.formatInOriginalFormat(pn, defaultCountryIso);
                return result;
            }
        } catch (Exception e) {
            Rlog.e(LOG_TAG, "formatNumber exception: ", e);
        }
    }

    public static String formatNumber(String phoneNumber, String phoneNumberE164, String defaultCountryIso) {
        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            if (!isDialable(phoneNumber.charAt(i))) {
                return phoneNumber;
            }
        }
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        if (phoneNumberE164 != null && phoneNumberE164.length() >= 2 && phoneNumberE164.charAt(0) == PLUS_SIGN_CHAR) {
            try {
                String regionCode = util.getRegionCodeForNumber(util.parse(phoneNumberE164, "ZZ"));
                if (!TextUtils.isEmpty(regionCode) && normalizeNumber(phoneNumber).indexOf(phoneNumberE164.substring(1)) <= 0) {
                    defaultCountryIso = regionCode;
                }
            } catch (NumberParseException e) {
            }
        }
        String result = formatNumber(phoneNumber, defaultCountryIso);
        return result != null ? result : phoneNumber;
    }

    public static String normalizeNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            PhoneNumberUtilsInjector.appendNonSeparator(sb, c, i);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (sb.length() == 0 && c == PLUS_SIGN_CHAR) {
                sb.append(c);
            } else if ((c >= DateFormat.AM_PM && c <= DateFormat.TIME_ZONE) || (c >= DateFormat.CAPITAL_AM_PM && c <= 'Z')) {
                return normalizeNumber(convertKeypadLettersToDigits(phoneNumber));
            }
        }
        return sb.toString();
    }

    public static String replaceUnicodeDigits(String number) {
        StringBuilder normalizedDigits = new StringBuilder(number.length());
        for (char c : number.toCharArray()) {
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                normalizedDigits.append(digit);
            } else {
                normalizedDigits.append(c);
            }
        }
        return normalizedDigits.toString();
    }

    @Deprecated
    public static boolean isEmergencyNumber(String number) {
        return isEmergencyNumber(getDefaultVoiceSubId(), number);
    }

    @Deprecated
    @UnsupportedAppUsage
    public static boolean isEmergencyNumber(int subId, String number) {
        return isEmergencyNumberInternal(subId, number, true);
    }

    @Deprecated
    public static boolean isPotentialEmergencyNumber(String number) {
        return isPotentialEmergencyNumber(getDefaultVoiceSubId(), number);
    }

    @Deprecated
    @UnsupportedAppUsage
    public static boolean isPotentialEmergencyNumber(int subId, String number) {
        return isEmergencyNumberInternal(subId, number, false);
    }

    private static boolean isEmergencyNumberInternal(String number, boolean useExactMatch) {
        return isEmergencyNumberInternal(getDefaultVoiceSubId(), number, useExactMatch);
    }

    private static boolean isEmergencyNumberInternal(int subId, String number, boolean useExactMatch) {
        return isEmergencyNumberInternal(subId, number, null, useExactMatch);
    }

    @Deprecated
    @UnsupportedAppUsage
    public static boolean isEmergencyNumber(String number, String defaultCountryIso) {
        return isEmergencyNumber(getDefaultVoiceSubId(), number, defaultCountryIso);
    }

    @Deprecated
    public static boolean isEmergencyNumber(int subId, String number, String defaultCountryIso) {
        return isEmergencyNumberInternal(subId, number, defaultCountryIso, true);
    }

    @Deprecated
    public static boolean isPotentialEmergencyNumber(String number, String defaultCountryIso) {
        return isPotentialEmergencyNumber(getDefaultVoiceSubId(), number, defaultCountryIso);
    }

    @Deprecated
    public static boolean isPotentialEmergencyNumber(int subId, String number, String defaultCountryIso) {
        return isEmergencyNumberInternal(subId, number, defaultCountryIso, false);
    }

    private static boolean isEmergencyNumberInternal(String number, String defaultCountryIso, boolean useExactMatch) {
        return isEmergencyNumberInternal(getDefaultVoiceSubId(), number, defaultCountryIso, useExactMatch);
    }

    private static boolean isEmergencyNumberInternal(int subId, String number, String defaultCountryIso, boolean useExactMatch) {
        if (!useExactMatch) {
            return TelephonyManager.getDefault().isPotentialEmergencyNumber(number);
        }
        try {
            return TelephonyManager.getDefault().isEmergencyNumber(number);
        } catch (RuntimeException ex) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isEmergencyNumberInternal: RuntimeException: ");
            stringBuilder.append(ex);
            Rlog.e(LOG_TAG, stringBuilder.toString());
            return false;
        }
    }

    @Deprecated
    public static boolean isLocalEmergencyNumber(Context context, String number) {
        return isLocalEmergencyNumber(context, getDefaultVoiceSubId(), number);
    }

    @Deprecated
    @UnsupportedAppUsage
    public static boolean isLocalEmergencyNumber(Context context, int subId, String number) {
        return isLocalEmergencyNumberInternal(subId, number, context, true);
    }

    @Deprecated
    @UnsupportedAppUsage
    public static boolean isPotentialLocalEmergencyNumber(Context context, String number) {
        return isPotentialLocalEmergencyNumber(context, getDefaultVoiceSubId(), number);
    }

    @Deprecated
    @UnsupportedAppUsage
    public static boolean isPotentialLocalEmergencyNumber(Context context, int subId, String number) {
        return isLocalEmergencyNumberInternal(subId, number, context, false);
    }

    private static boolean isLocalEmergencyNumberInternal(String number, Context context, boolean useExactMatch) {
        return isLocalEmergencyNumberInternal(getDefaultVoiceSubId(), number, context, useExactMatch);
    }

    private static boolean isLocalEmergencyNumberInternal(int subId, String number, Context context, boolean useExactMatch) {
        String countryIso = getCountryIso(context);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("isLocalEmergencyNumberInternal");
        stringBuilder.append(countryIso);
        String stringBuilder2 = stringBuilder.toString();
        String str = LOG_TAG;
        Rlog.w(str, stringBuilder2);
        if (countryIso == null) {
            countryIso = context.getResources().getConfiguration().locale.getCountry();
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("No CountryDetector; falling back to countryIso based on locale: ");
            stringBuilder3.append(countryIso);
            Rlog.w(str, stringBuilder3.toString());
        }
        return isEmergencyNumberInternal(subId, number, countryIso, useExactMatch);
    }

    private static String getCountryIso(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getCountryIso ");
        stringBuilder.append(sCountryDetector);
        Rlog.w(LOG_TAG, stringBuilder.toString());
        if (sCountryDetector == null) {
            CountryDetector detector = (CountryDetector) context.getSystemService(Context.COUNTRY_DETECTOR);
            if (detector != null) {
                sCountryDetector = detector.detectCountry();
            }
        }
        Country country = sCountryDetector;
        if (country == null) {
            return null;
        }
        return country.getCountryIso();
    }

    public static void resetCountryDetectorInfo() {
        sCountryDetector = null;
    }

    public static boolean isVoiceMailNumber(String number) {
        return isVoiceMailNumber(SubscriptionManager.getDefaultSubscriptionId(), number);
    }

    public static boolean isVoiceMailNumber(int subId, String number) {
        return isVoiceMailNumber(null, subId, number);
    }

    @UnsupportedAppUsage
    public static boolean isVoiceMailNumber(Context context, int subId, String number) {
        TelephonyManager tm;
        boolean z = false;
        if (context == null) {
            try {
                tm = TelephonyManager.getDefault();
            } catch (SecurityException e) {
                return false;
            }
        }
        tm = TelephonyManager.from(context);
        String vmNumber = tm.getVoiceMailNumber(subId);
        String mdn = tm.getLine1Number(subId);
        number = extractNetworkPortionAlt(number);
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        boolean compareWithMdn = false;
        if (context != null) {
            CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(Context.CARRIER_CONFIG_SERVICE);
            if (configManager != null) {
                PersistableBundle b = configManager.getConfigForSubId(subId);
                if (b != null) {
                    compareWithMdn = b.getBoolean(CarrierConfigManager.KEY_MDN_IS_ADDITIONAL_VOICEMAIL_NUMBER_BOOL);
                }
            }
        }
        if (!compareWithMdn) {
            return compare(number, vmNumber);
        }
        if (compare(number, vmNumber) || compare(number, mdn)) {
            z = true;
        }
        return z;
    }

    public static String convertKeypadLettersToDigits(String input) {
        if (input == null) {
            return input;
        }
        int len = input.length();
        if (len == 0) {
            return input;
        }
        char[] out = input.toCharArray();
        for (int i = 0; i < len; i++) {
            char c = out[i];
            out[i] = (char) KEYPAD_MAP.get(c, c);
        }
        return new String(out);
    }

    @UnsupportedAppUsage
    public static String cdmaCheckAndProcessPlusCode(String dialStr) {
        if (!TextUtils.isEmpty(dialStr) && isReallyDialable(dialStr.charAt(0)) && isNonSeparator(dialStr)) {
            String defaultIso = "";
            String currIso = PhoneNumberUtilsInjector.getCdmaTelephonyProperty(TelephonyProperties.PROPERTY_OPERATOR_ISO_COUNTRY, defaultIso);
            defaultIso = PhoneNumberUtilsInjector.getCdmaTelephonyProperty(TelephonyProperties.PROPERTY_ICC_OPERATOR_ISO_COUNTRY, defaultIso);
            if (!(TextUtils.isEmpty(currIso) || TextUtils.isEmpty(defaultIso))) {
                return cdmaCheckAndProcessPlusCodeByNumberFormat(dialStr, getFormatTypeFromCountryCode(currIso), getFormatTypeFromCountryCode(defaultIso));
            }
        }
        return dialStr;
    }

    public static String cdmaCheckAndProcessPlusCodeForSms(String dialStr) {
        if (!TextUtils.isEmpty(dialStr) && isReallyDialable(dialStr.charAt(0)) && isNonSeparator(dialStr)) {
            String defaultIso = TelephonyManager.getDefault().getSimCountryIso();
            if (!TextUtils.isEmpty(defaultIso)) {
                int format = getFormatTypeFromCountryCode(defaultIso);
                return cdmaCheckAndProcessPlusCodeByNumberFormat(dialStr, format, format);
            }
        }
        return dialStr;
    }

    public static String cdmaCheckAndProcessPlusCodeByNumberFormat(String dialStr, int currFormat, int defaultFormat) {
        String retStr = dialStr;
        boolean useNanp = currFormat == defaultFormat && currFormat == 1;
        if (dialStr != null && dialStr.lastIndexOf(PLUS_SIGN_STRING) != -1) {
            String tempDialStr = dialStr;
            retStr = null;
            do {
                String networkDialStr;
                if (useNanp) {
                    networkDialStr = extractNetworkPortion(tempDialStr);
                } else {
                    networkDialStr = extractNetworkPortionAlt(tempDialStr);
                }
                networkDialStr = processPlusCode(networkDialStr, useNanp);
                if (!TextUtils.isEmpty(networkDialStr)) {
                    if (retStr == null) {
                        retStr = networkDialStr;
                    } else {
                        retStr = retStr.concat(networkDialStr);
                    }
                    String postDialStr = extractPostDialPortion(tempDialStr);
                    if (!TextUtils.isEmpty(postDialStr)) {
                        int dialableIndex = findDialableIndexFromPostDialStr(postDialStr);
                        if (dialableIndex >= 1) {
                            retStr = appendPwCharBackToOrigDialStr(dialableIndex, retStr, postDialStr);
                            tempDialStr = postDialStr.substring(dialableIndex);
                        } else {
                            if (dialableIndex < 0) {
                                postDialStr = "";
                            }
                            Rlog.e("wrong postDialStr=", postDialStr);
                        }
                    }
                    if (TextUtils.isEmpty(postDialStr)) {
                        break;
                    }
                } else {
                    Rlog.e("checkAndProcessPlusCode: null newDialStr", networkDialStr);
                    return dialStr;
                }
            } while (!TextUtils.isEmpty(tempDialStr));
        }
        return retStr;
    }

    public static CharSequence createTtsSpannable(CharSequence phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        Spannable spannable = Factory.getInstance().newSpannable(phoneNumber);
        addTtsSpan(spannable, 0, spannable.length());
        return spannable;
    }

    public static void addTtsSpan(Spannable s, int start, int endExclusive) {
        s.setSpan(createTtsSpan(s.subSequence(start, endExclusive).toString()), start, endExclusive, 33);
    }

    @Deprecated
    @UnsupportedAppUsage
    public static CharSequence ttsSpanAsPhoneNumber(CharSequence phoneNumber) {
        return createTtsSpannable(phoneNumber);
    }

    @Deprecated
    public static void ttsSpanAsPhoneNumber(Spannable s, int start, int end) {
        addTtsSpan(s, start, end);
    }

    public static TtsSpan createTtsSpan(String phoneNumberString) {
        if (phoneNumberString == null) {
            return null;
        }
        PhoneNumber phoneNumber = null;
        try {
            phoneNumber = PhoneNumberUtil.getInstance().parse(phoneNumberString, null);
        } catch (NumberParseException e) {
        }
        TelephoneBuilder builder = new TelephoneBuilder();
        if (phoneNumber == null) {
            builder.setNumberParts(splitAtNonNumerics(phoneNumberString));
        } else {
            if (phoneNumber.hasCountryCode()) {
                builder.setCountryCode(Integer.toString(phoneNumber.getCountryCode()));
            }
            builder.setNumberParts(Long.toString(phoneNumber.getNationalNumber()));
        }
        return builder.build();
    }

    private static String splitAtNonNumerics(CharSequence number) {
        StringBuilder sb = new StringBuilder(number.length());
        int i = 0;
        while (true) {
            int length = number.length();
            Object obj = WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
            if (i >= length) {
                return sb.toString().replaceAll(" +", obj).trim();
            }
            if (is12Key(number.charAt(i))) {
                obj = Character.valueOf(number.charAt(i));
            }
            sb.append(obj);
            i++;
        }
    }

    private static String getCurrentIdp(boolean useNanp) {
        if (useNanp) {
            return NANP_IDP_STRING;
        }
        return PhoneNumberUtilsInjector.getCdmaTelephonyProperty(TelephonyProperties.PROPERTY_OPERATOR_IDP_STRING, useNanp ? NANP_IDP_STRING : PLUS_SIGN_STRING);
    }

    private static boolean isTwoToNine(char c) {
        if (c < '2' || c > '9') {
            return false;
        }
        return true;
    }

    private static int getFormatTypeFromCountryCode(String country) {
        for (String compareToIgnoreCase : NANP_COUNTRIES) {
            if (compareToIgnoreCase.compareToIgnoreCase(country) == 0) {
                return 1;
            }
        }
        if ("jp".compareToIgnoreCase(country) == 0) {
            return 2;
        }
        return 0;
    }

    @UnsupportedAppUsage
    public static boolean isNanp(String dialStr) {
        if (dialStr == null) {
            Rlog.e("isNanp: null dialStr passed in", dialStr);
            return false;
        } else if (dialStr.length() != 10 || !isTwoToNine(dialStr.charAt(0)) || !isTwoToNine(dialStr.charAt(3))) {
            return false;
        } else {
            for (int i = 1; i < 10; i++) {
                if (!isISODigit(dialStr.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    private static boolean isOneNanp(String dialStr) {
        if (dialStr != null) {
            String newDialStr = dialStr.substring(1);
            if (dialStr.charAt(0) == '1' && isNanp(newDialStr)) {
                return true;
            }
            return false;
        }
        Rlog.e("isOneNanp: null dialStr passed in", dialStr);
        return false;
    }

    @UnsupportedAppUsage
    public static boolean isUriNumber(String number) {
        return number != null && (number.contains("@") || number.contains("%40"));
    }

    @UnsupportedAppUsage
    public static String getUsernameFromUriNumber(String number) {
        int delimiterIndex = number.indexOf(64);
        if (delimiterIndex < 0) {
            delimiterIndex = number.indexOf("%40");
        }
        if (delimiterIndex < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getUsernameFromUriNumber: no delimiter found in SIP addr '");
            stringBuilder.append(number);
            stringBuilder.append("'");
            Rlog.w(LOG_TAG, stringBuilder.toString());
            delimiterIndex = number.length();
        }
        return number.substring(0, delimiterIndex);
    }

    public static Uri convertSipUriToTelUri(Uri source) {
        if (!"sip".equals(source.getScheme())) {
            return source;
        }
        String[] numberParts = source.getSchemeSpecificPart().split("[@;:]");
        if (numberParts.length == 0) {
            return source;
        }
        return Uri.fromParts(PhoneAccount.SCHEME_TEL, numberParts[0], null);
    }

    private static String processPlusCode(String networkDialStr, boolean useNanp) {
        String retStr = networkDialStr;
        if (networkDialStr == null || networkDialStr.charAt(0) != PLUS_SIGN_CHAR || networkDialStr.length() <= 1) {
            return retStr;
        }
        String newStr = networkDialStr.substring(1);
        if (useNanp && isOneNanp(newStr)) {
            return newStr;
        }
        return networkDialStr.replaceFirst("[+]", getCurrentIdp(useNanp));
    }

    private static int findDialableIndexFromPostDialStr(String postDialStr) {
        for (int index = 0; index < postDialStr.length(); index++) {
            if (isReallyDialable(postDialStr.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    private static String appendPwCharBackToOrigDialStr(int dialableIndex, String origStr, String dialStr) {
        if (dialableIndex == 1) {
            return dialStr.charAt(0);
        }
        return origStr.concat(dialStr.substring(0, dialableIndex));
    }

    private static boolean matchIntlPrefix(String a, int len) {
        int state = 0;
        for (int i = 0; i < len; i++) {
            char c = a.charAt(i);
            if (state != 0) {
                if (state != 2) {
                    if (state != 4) {
                        if (isNonSeparator(c)) {
                            return false;
                        }
                    } else if (c == '1') {
                        state = 5;
                    } else if (isNonSeparator(c)) {
                        return false;
                    }
                } else if (c == '0') {
                    state = 3;
                } else if (c == '1') {
                    state = 4;
                } else if (isNonSeparator(c)) {
                    return false;
                }
            } else if (c == PLUS_SIGN_CHAR) {
                state = 1;
            } else if (c == '0') {
                state = 2;
            } else if (isNonSeparator(c)) {
                return false;
            }
        }
        boolean z = true;
        if (!(state == 1 || state == 3 || state == 5)) {
            z = false;
        }
        return z;
    }

    private static boolean matchIntlPrefixAndCC(String a, int len) {
        int state = 0;
        int i = 0;
        while (true) {
            boolean z = false;
            if (i < len) {
                char c = a.charAt(i);
                switch (state) {
                    case 0:
                        if (c != PLUS_SIGN_CHAR) {
                            if (c != '0') {
                                if (!isNonSeparator(c)) {
                                    break;
                                }
                                return false;
                            }
                            state = 2;
                            break;
                        }
                        state = 1;
                        break;
                    case 1:
                    case 3:
                    case 5:
                        if (!isISODigit(c)) {
                            if (!isNonSeparator(c)) {
                                break;
                            }
                            return false;
                        }
                        state = 6;
                        break;
                    case 2:
                        if (c != '0') {
                            if (c != '1') {
                                if (!isNonSeparator(c)) {
                                    break;
                                }
                                return false;
                            }
                            state = 4;
                            break;
                        }
                        state = 3;
                        break;
                    case 4:
                        if (c != '1') {
                            if (!isNonSeparator(c)) {
                                break;
                            }
                            return false;
                        }
                        state = 5;
                        break;
                    case 6:
                    case 7:
                        if (!isISODigit(c)) {
                            if (!isNonSeparator(c)) {
                                break;
                            }
                            return false;
                        }
                        state++;
                        break;
                    default:
                        if (!isNonSeparator(c)) {
                            break;
                        }
                        return false;
                }
                i++;
            } else {
                if (state == 6 || state == 7 || state == 8) {
                    z = true;
                }
                return z;
            }
        }
    }

    private static boolean matchTrunkPrefix(String a, int len) {
        boolean found = false;
        for (int i = 0; i < len; i++) {
            char c = a.charAt(i);
            if (c == '0' && !found) {
                found = true;
            } else if (isNonSeparator(c)) {
                return false;
            }
        }
        return found;
    }

    private static boolean isCountryCallingCode(int countryCallingCodeCandidate) {
        return countryCallingCodeCandidate > 0 && countryCallingCodeCandidate < CCC_LENGTH && COUNTRY_CALLING_CALL[countryCallingCodeCandidate];
    }

    private static int tryGetISODigit(char ch) {
        if ('0' > ch || ch > '9') {
            return -1;
        }
        return ch - 48;
    }

    private static CountryCallingCodeAndNewIndex tryGetCountryCallingCodeAndNewIndex(String str, boolean acceptThailandCase) {
        int state = 0;
        int ccc = 0;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            switch (state) {
                case 0:
                    if (ch != PLUS_SIGN_CHAR) {
                        if (ch != '0') {
                            if (ch != '1') {
                                if (!isDialable(ch)) {
                                    break;
                                }
                                return null;
                            } else if (acceptThailandCase) {
                                state = 8;
                                break;
                            } else {
                                return null;
                            }
                        }
                        state = 2;
                        break;
                    }
                    state = 1;
                    break;
                case 1:
                case 3:
                case 5:
                case 6:
                case 7:
                    int ret = tryGetISODigit(ch);
                    if (ret <= 0) {
                        if (!isDialable(ch)) {
                            break;
                        }
                        return null;
                    }
                    ccc = (ccc * 10) + ret;
                    if (ccc < 100 && !isCountryCallingCode(ccc)) {
                        if (state != 1 && state != 3 && state != 5) {
                            state++;
                            break;
                        }
                        state = 6;
                        break;
                    }
                    return new CountryCallingCodeAndNewIndex(ccc, i + 1);
                    break;
                case 2:
                    if (ch != '0') {
                        if (ch != '1') {
                            if (!isDialable(ch)) {
                                break;
                            }
                            return null;
                        }
                        state = 4;
                        break;
                    }
                    state = 3;
                    break;
                case 4:
                    if (ch != '1') {
                        if (!isDialable(ch)) {
                            break;
                        }
                        return null;
                    }
                    state = 5;
                    break;
                case 8:
                    if (ch != '6') {
                        if (!isDialable(ch)) {
                            break;
                        }
                        return null;
                    }
                    state = 9;
                    break;
                case 9:
                    if (ch == '6') {
                        return new CountryCallingCodeAndNewIndex(66, i + 1);
                    }
                    return null;
                default:
                    return null;
            }
        }
        return null;
    }

    private static int tryGetTrunkPrefixOmittedIndex(String str, int currentIndex) {
        int length = str.length();
        for (int i = currentIndex; i < length; i++) {
            char ch = str.charAt(i);
            if (tryGetISODigit(ch) >= 0) {
                return i + 1;
            }
            if (isDialable(ch)) {
                return -1;
            }
        }
        return -1;
    }

    private static boolean checkPrefixIsIgnorable(String str, int forwardIndex, int backwardIndex) {
        boolean trunk_prefix_was_read = false;
        while (backwardIndex >= forwardIndex) {
            if (tryGetISODigit(str.charAt(backwardIndex)) >= 0) {
                if (trunk_prefix_was_read) {
                    return false;
                }
                trunk_prefix_was_read = true;
            } else if (isDialable(str.charAt(backwardIndex))) {
                return false;
            }
            backwardIndex--;
        }
        return true;
    }

    private static int getDefaultVoiceSubId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    public static String convertToEmergencyNumber(Context context, String number) {
        if (context == null || TextUtils.isEmpty(number)) {
            return number;
        }
        String normalizedNumber = normalizeNumber(number);
        if (isEmergencyNumber(normalizedNumber)) {
            return number;
        }
        if (sConvertToEmergencyMap == null) {
            sConvertToEmergencyMap = context.getResources().getStringArray(R.array.config_convert_to_emergency_number_map);
        }
        String[] strArr = sConvertToEmergencyMap;
        if (strArr == null || strArr.length == 0) {
            return number;
        }
        for (String convertMap : strArr) {
            String[] entry = null;
            String[] filterNumbers = null;
            String convertedNumber = null;
            if (!TextUtils.isEmpty(convertMap)) {
                entry = convertMap.split(":");
            }
            if (entry != null && entry.length == 2) {
                convertedNumber = entry[1];
                if (!TextUtils.isEmpty(entry[0])) {
                    filterNumbers = entry[0].split(",");
                }
            }
            if (!(TextUtils.isEmpty(convertedNumber) || filterNumbers == null || filterNumbers.length == 0)) {
                for (String filterNumber : filterNumbers) {
                    if (!TextUtils.isEmpty(filterNumber) && filterNumber.equals(normalizedNumber)) {
                        return convertedNumber;
                    }
                }
                continue;
            }
        }
        return number;
    }
}
