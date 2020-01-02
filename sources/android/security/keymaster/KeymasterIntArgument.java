package android.security.keymaster;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;

class KeymasterIntArgument extends KeymasterArgument {
    @UnsupportedAppUsage
    public final int value;

    @UnsupportedAppUsage
    public KeymasterIntArgument(int tag, int value) {
        super(tag);
        int tagType = KeymasterDefs.getTagType(tag);
        if (tagType == 268435456 || tagType == 536870912 || tagType == 805306368 || tagType == 1073741824) {
            this.value = value;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Bad int tag ");
        stringBuilder.append(tag);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    @UnsupportedAppUsage
    public KeymasterIntArgument(int tag, Parcel in) {
        super(tag);
        this.value = in.readInt();
    }

    public void writeValue(Parcel out) {
        out.writeInt(this.value);
    }
}
