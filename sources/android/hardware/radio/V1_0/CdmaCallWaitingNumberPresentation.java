package android.hardware.radio.V1_0;

import com.android.internal.telephony.IccCardConstants;
import java.util.ArrayList;

public final class CdmaCallWaitingNumberPresentation {
    public static final int ALLOWED = 0;
    public static final int RESTRICTED = 1;
    public static final int UNKNOWN = 2;

    public static final String toString(int o) {
        if (o == 0) {
            return "ALLOWED";
        }
        if (o == 1) {
            return "RESTRICTED";
        }
        if (o == 2) {
            return IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0x");
        stringBuilder.append(Integer.toHexString(o));
        return stringBuilder.toString();
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList();
        int flipped = 0;
        list.add("ALLOWED");
        if ((o & 1) == 1) {
            list.add("RESTRICTED");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add(IccCardConstants.INTENT_VALUE_ICC_UNKNOWN);
            flipped |= 2;
        }
        if (o != flipped) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("0x");
            stringBuilder.append(Integer.toHexString((~flipped) & o));
            list.add(stringBuilder.toString());
        }
        return String.join(" | ", list);
    }
}
