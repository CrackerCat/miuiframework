package android.bluetooth;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.Objects;

public final class BluetoothCodecStatus implements Parcelable {
    public static final Creator<BluetoothCodecStatus> CREATOR = new Creator<BluetoothCodecStatus>() {
        public BluetoothCodecStatus createFromParcel(Parcel in) {
            return new BluetoothCodecStatus((BluetoothCodecConfig) in.readTypedObject(BluetoothCodecConfig.CREATOR), (BluetoothCodecConfig[]) in.createTypedArray(BluetoothCodecConfig.CREATOR), (BluetoothCodecConfig[]) in.createTypedArray(BluetoothCodecConfig.CREATOR));
        }

        public BluetoothCodecStatus[] newArray(int size) {
            return new BluetoothCodecStatus[size];
        }
    };
    @UnsupportedAppUsage
    public static final String EXTRA_CODEC_STATUS = "android.bluetooth.codec.extra.CODEC_STATUS";
    private final BluetoothCodecConfig mCodecConfig;
    private final BluetoothCodecConfig[] mCodecsLocalCapabilities;
    private final BluetoothCodecConfig[] mCodecsSelectableCapabilities;

    public BluetoothCodecStatus(BluetoothCodecConfig codecConfig, BluetoothCodecConfig[] codecsLocalCapabilities, BluetoothCodecConfig[] codecsSelectableCapabilities) {
        this.mCodecConfig = codecConfig;
        this.mCodecsLocalCapabilities = codecsLocalCapabilities;
        this.mCodecsSelectableCapabilities = codecsSelectableCapabilities;
    }

    public boolean equals(Object o) {
        boolean z = false;
        if (!(o instanceof BluetoothCodecStatus)) {
            return false;
        }
        BluetoothCodecStatus other = (BluetoothCodecStatus) o;
        if (Objects.equals(other.mCodecConfig, this.mCodecConfig) && sameCapabilities(other.mCodecsLocalCapabilities, this.mCodecsLocalCapabilities) && sameCapabilities(other.mCodecsSelectableCapabilities, this.mCodecsSelectableCapabilities)) {
            z = true;
        }
        return z;
    }

    public static boolean sameCapabilities(BluetoothCodecConfig[] c1, BluetoothCodecConfig[] c2) {
        boolean z = false;
        if (c1 == null) {
            if (c2 == null) {
                z = true;
            }
            return z;
        } else if (c2 != null && c1.length == c2.length) {
            return Arrays.asList(c1).containsAll(Arrays.asList(c2));
        } else {
            return false;
        }
    }

    public int hashCode() {
        r0 = new Object[3];
        BluetoothCodecConfig[] bluetoothCodecConfigArr = this.mCodecsLocalCapabilities;
        r0[1] = bluetoothCodecConfigArr;
        r0[2] = bluetoothCodecConfigArr;
        return Objects.hash(r0);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{mCodecConfig:");
        stringBuilder.append(this.mCodecConfig);
        stringBuilder.append(",mCodecsLocalCapabilities:");
        stringBuilder.append(Arrays.toString(this.mCodecsLocalCapabilities));
        stringBuilder.append(",mCodecsSelectableCapabilities:");
        stringBuilder.append(Arrays.toString(this.mCodecsSelectableCapabilities));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedObject(this.mCodecConfig, 0);
        out.writeTypedArray(this.mCodecsLocalCapabilities, 0);
        out.writeTypedArray(this.mCodecsSelectableCapabilities, 0);
    }

    @UnsupportedAppUsage
    public BluetoothCodecConfig getCodecConfig() {
        return this.mCodecConfig;
    }

    @UnsupportedAppUsage
    public BluetoothCodecConfig[] getCodecsLocalCapabilities() {
        return this.mCodecsLocalCapabilities;
    }

    @UnsupportedAppUsage
    public BluetoothCodecConfig[] getCodecsSelectableCapabilities() {
        return this.mCodecsSelectableCapabilities;
    }
}
