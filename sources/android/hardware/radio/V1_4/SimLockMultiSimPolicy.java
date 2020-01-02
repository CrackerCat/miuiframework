package android.hardware.radio.V1_4;

import java.util.ArrayList;

public final class SimLockMultiSimPolicy {
    public static final int NO_MULTISIM_POLICY = 0;
    public static final int ONE_VALID_SIM_MUST_BE_PRESENT = 1;

    public static final String toString(int o) {
        if (o == 0) {
            return "NO_MULTISIM_POLICY";
        }
        if (o == 1) {
            return "ONE_VALID_SIM_MUST_BE_PRESENT";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0x");
        stringBuilder.append(Integer.toHexString(o));
        return stringBuilder.toString();
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList();
        int flipped = 0;
        list.add("NO_MULTISIM_POLICY");
        if ((o & 1) == 1) {
            list.add("ONE_VALID_SIM_MUST_BE_PRESENT");
            flipped = 0 | 1;
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
