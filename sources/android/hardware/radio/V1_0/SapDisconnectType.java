package android.hardware.radio.V1_0;

import java.util.ArrayList;

public final class SapDisconnectType {
    public static final int GRACEFUL = 0;
    public static final int IMMEDIATE = 1;

    public static final String toString(int o) {
        if (o == 0) {
            return "GRACEFUL";
        }
        if (o == 1) {
            return "IMMEDIATE";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0x");
        stringBuilder.append(Integer.toHexString(o));
        return stringBuilder.toString();
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList();
        int flipped = 0;
        list.add("GRACEFUL");
        if ((o & 1) == 1) {
            list.add("IMMEDIATE");
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
