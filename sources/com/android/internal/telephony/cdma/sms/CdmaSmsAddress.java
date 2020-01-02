package com.android.internal.telephony.cdma.sms;

import android.util.SparseBooleanArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.SmsAddress;
import com.android.internal.util.HexDump;
import org.apache.miui.commons.lang3.CharUtils;
import org.apache.miui.commons.lang3.ClassUtils;

public class CdmaSmsAddress extends SmsAddress {
    public static final int DIGIT_MODE_4BIT_DTMF = 0;
    public static final int DIGIT_MODE_8BIT_CHAR = 1;
    public static final int NUMBERING_PLAN_ISDN_TELEPHONY = 1;
    public static final int NUMBERING_PLAN_UNKNOWN = 0;
    public static final int NUMBER_MODE_DATA_NETWORK = 1;
    public static final int NUMBER_MODE_NOT_DATA_NETWORK = 0;
    public static final int SMS_ADDRESS_MAX = 36;
    public static final int SMS_SUBADDRESS_MAX = 36;
    public static final int TON_ABBREVIATED = 6;
    public static final int TON_ALPHANUMERIC = 5;
    public static final int TON_INTERNATIONAL_OR_IP = 1;
    public static final int TON_NATIONAL_OR_EMAIL = 2;
    public static final int TON_NETWORK = 3;
    public static final int TON_RESERVED = 7;
    public static final int TON_SUBSCRIBER = 4;
    public static final int TON_UNKNOWN = 0;
    private static final SparseBooleanArray numericCharDialableMap = new SparseBooleanArray(numericCharsDialable.length + numericCharsSugar.length);
    private static final char[] numericCharsDialable = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '#'};
    private static final char[] numericCharsSugar = new char[]{'(', ')', ' ', '-', '+', ClassUtils.PACKAGE_SEPARATOR_CHAR, '/', '\\'};
    public int digitMode;
    public int numberMode;
    public int numberOfDigits;
    public int numberPlan;

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CdmaSmsAddress ");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ digitMode=");
        stringBuilder.append(this.digitMode);
        builder.append(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(", numberMode=");
        stringBuilder.append(this.numberMode);
        builder.append(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(", numberPlan=");
        stringBuilder.append(this.numberPlan);
        builder.append(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(", numberOfDigits=");
        stringBuilder.append(this.numberOfDigits);
        builder.append(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(", ton=");
        stringBuilder.append(this.ton);
        builder.append(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(", address=\"");
        stringBuilder.append(this.address);
        stringBuilder.append("\"");
        builder.append(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(", origBytes=");
        stringBuilder.append(HexDump.toHexString(this.origBytes));
        builder.append(stringBuilder.toString());
        builder.append(" }");
        return builder.toString();
    }

    @VisibleForTesting
    public static byte[] parseToDtmf(String address) {
        int digits = address.length();
        byte[] result = new byte[digits];
        for (int i = 0; i < digits; i++) {
            int val;
            char c = address.charAt(i);
            if (c >= '1' && c <= '9') {
                val = c - 48;
            } else if (c == '0') {
                val = 10;
            } else if (c == '*') {
                val = 11;
            } else if (c != '#') {
                return null;
            } else {
                val = 12;
            }
            result[i] = (byte) val;
        }
        return result;
    }

    static {
        char[] cArr;
        int i = 0;
        while (true) {
            cArr = numericCharsDialable;
            if (i >= cArr.length) {
                break;
            }
            numericCharDialableMap.put(cArr[i], true);
            i++;
        }
        i = 0;
        while (true) {
            cArr = numericCharsSugar;
            if (i < cArr.length) {
                numericCharDialableMap.put(cArr[i], false);
                i++;
            } else {
                return;
            }
        }
    }

    private static String filterNumericSugar(String address) {
        StringBuilder builder = new StringBuilder();
        int len = address.length();
        for (int i = 0; i < len; i++) {
            char c = address.charAt(i);
            int mapIndex = numericCharDialableMap.indexOfKey(c);
            if (mapIndex < 0) {
                return null;
            }
            if (numericCharDialableMap.valueAt(mapIndex)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private static String filterWhitespace(String address) {
        StringBuilder builder = new StringBuilder();
        int len = address.length();
        for (int i = 0; i < len; i++) {
            char c = address.charAt(i);
            if (!(c == ' ' || c == CharUtils.CR || c == 10 || c == 9)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static CdmaSmsAddress parse(String address) {
        byte[] origBytes;
        CdmaSmsAddress addr = new CdmaSmsAddress();
        addr.address = address;
        addr.ton = 0;
        addr.digitMode = 0;
        addr.numberPlan = 0;
        addr.numberMode = 0;
        String filteredAddr = filterNumericSugar(address);
        String str = "+";
        if (address.contains(str) || filteredAddr == null) {
            addr.digitMode = 1;
            addr.numberMode = 1;
            filteredAddr = filterWhitespace(address);
            if (address.contains("@")) {
                addr.ton = 2;
            } else if (address.contains(str) && filterNumericSugar(address) != null) {
                addr.ton = 1;
                addr.numberPlan = 1;
                addr.numberMode = 0;
                filteredAddr = filterNumericSugar(address);
            }
            origBytes = UserData.stringToAscii(filteredAddr);
        } else {
            origBytes = parseToDtmf(filteredAddr);
        }
        if (origBytes == null) {
            return null;
        }
        addr.origBytes = origBytes;
        addr.numberOfDigits = origBytes.length;
        return addr;
    }
}
