package android.hardware.contexthub.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class ContextHub {
    public byte chreApiMajorVersion;
    public byte chreApiMinorVersion;
    public short chrePatchVersion;
    public long chrePlatformId;
    public ArrayList<PhysicalSensor> connectedSensors = new ArrayList();
    public int hubId;
    public int maxSupportedMsgLen;
    public String name = new String();
    public float peakMips;
    public float peakPowerDrawMw;
    public int platformVersion;
    public float sleepPowerDrawMw;
    public float stoppedPowerDrawMw;
    public String toolchain = new String();
    public int toolchainVersion;
    public String vendor = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != ContextHub.class) {
            return false;
        }
        ContextHub other = (ContextHub) otherObject;
        if (HidlSupport.deepEquals(this.name, other.name) && HidlSupport.deepEquals(this.vendor, other.vendor) && HidlSupport.deepEquals(this.toolchain, other.toolchain) && this.platformVersion == other.platformVersion && this.toolchainVersion == other.toolchainVersion && this.hubId == other.hubId && this.peakMips == other.peakMips && this.stoppedPowerDrawMw == other.stoppedPowerDrawMw && this.sleepPowerDrawMw == other.sleepPowerDrawMw && this.peakPowerDrawMw == other.peakPowerDrawMw && HidlSupport.deepEquals(this.connectedSensors, other.connectedSensors) && this.maxSupportedMsgLen == other.maxSupportedMsgLen && this.chrePlatformId == other.chrePlatformId && this.chreApiMajorVersion == other.chreApiMajorVersion && this.chreApiMinorVersion == other.chreApiMinorVersion && this.chrePatchVersion == other.chrePatchVersion) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(this.name)), Integer.valueOf(HidlSupport.deepHashCode(this.vendor)), Integer.valueOf(HidlSupport.deepHashCode(this.toolchain)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.platformVersion))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.toolchainVersion))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.hubId))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.peakMips))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.stoppedPowerDrawMw))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.sleepPowerDrawMw))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.peakPowerDrawMw))), Integer.valueOf(HidlSupport.deepHashCode(this.connectedSensors)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxSupportedMsgLen))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.chrePlatformId))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.chreApiMajorVersion))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.chreApiMinorVersion))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.chrePatchVersion)))});
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(".name = ");
        builder.append(this.name);
        builder.append(", .vendor = ");
        builder.append(this.vendor);
        builder.append(", .toolchain = ");
        builder.append(this.toolchain);
        builder.append(", .platformVersion = ");
        builder.append(this.platformVersion);
        builder.append(", .toolchainVersion = ");
        builder.append(this.toolchainVersion);
        builder.append(", .hubId = ");
        builder.append(this.hubId);
        builder.append(", .peakMips = ");
        builder.append(this.peakMips);
        builder.append(", .stoppedPowerDrawMw = ");
        builder.append(this.stoppedPowerDrawMw);
        builder.append(", .sleepPowerDrawMw = ");
        builder.append(this.sleepPowerDrawMw);
        builder.append(", .peakPowerDrawMw = ");
        builder.append(this.peakPowerDrawMw);
        builder.append(", .connectedSensors = ");
        builder.append(this.connectedSensors);
        builder.append(", .maxSupportedMsgLen = ");
        builder.append(this.maxSupportedMsgLen);
        builder.append(", .chrePlatformId = ");
        builder.append(this.chrePlatformId);
        builder.append(", .chreApiMajorVersion = ");
        builder.append(this.chreApiMajorVersion);
        builder.append(", .chreApiMinorVersion = ");
        builder.append(this.chreApiMinorVersion);
        builder.append(", .chrePatchVersion = ");
        builder.append(this.chrePatchVersion);
        builder.append("}");
        return builder.toString();
    }

    public final void readFromParcel(HwParcel parcel) {
        readEmbeddedFromParcel(parcel, parcel.readBuffer(120), 0);
    }

    public static final ArrayList<ContextHub> readVectorFromParcel(HwParcel parcel) {
        ArrayList<ContextHub> _hidl_vec = new ArrayList();
        HwBlob _hidl_blob = parcel.readBuffer(16);
        int _hidl_vec_size = _hidl_blob.getInt32(8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 120), _hidl_blob.handle(), 0, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ContextHub _hidl_vec_element = new ContextHub();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 120));
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        HwBlob hwBlob = _hidl_blob;
        this.name = hwBlob.getString(_hidl_offset + 0);
        parcel.readEmbeddedBuffer((long) (this.name.getBytes().length + 1), _hidl_blob.handle(), (_hidl_offset + 0) + 0, false);
        this.vendor = hwBlob.getString(_hidl_offset + 16);
        parcel.readEmbeddedBuffer((long) (this.vendor.getBytes().length + 1), _hidl_blob.handle(), (_hidl_offset + 16) + 0, false);
        this.toolchain = hwBlob.getString(_hidl_offset + 32);
        parcel.readEmbeddedBuffer((long) (this.toolchain.getBytes().length + 1), _hidl_blob.handle(), (_hidl_offset + 32) + 0, false);
        this.platformVersion = hwBlob.getInt32(_hidl_offset + 48);
        this.toolchainVersion = hwBlob.getInt32(_hidl_offset + 52);
        this.hubId = hwBlob.getInt32(_hidl_offset + 56);
        this.peakMips = hwBlob.getFloat(_hidl_offset + 60);
        this.stoppedPowerDrawMw = hwBlob.getFloat(_hidl_offset + 64);
        this.sleepPowerDrawMw = hwBlob.getFloat(_hidl_offset + 68);
        this.peakPowerDrawMw = hwBlob.getFloat(_hidl_offset + 72);
        int _hidl_vec_size = hwBlob.getInt32((_hidl_offset + 80) + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 96), _hidl_blob.handle(), (_hidl_offset + 80) + 0, true);
        this.connectedSensors.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            PhysicalSensor _hidl_vec_element = new PhysicalSensor();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 96));
            this.connectedSensors.add(_hidl_vec_element);
        }
        HwParcel hwParcel = parcel;
        this.maxSupportedMsgLen = hwBlob.getInt32(_hidl_offset + 96);
        this.chrePlatformId = hwBlob.getInt64(_hidl_offset + 104);
        this.chreApiMajorVersion = hwBlob.getInt8(_hidl_offset + 112);
        this.chreApiMinorVersion = hwBlob.getInt8(_hidl_offset + 113);
        this.chrePatchVersion = hwBlob.getInt16(_hidl_offset + 114);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(120);
        writeEmbeddedToBlob(_hidl_blob, 0);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<ContextHub> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8, _hidl_vec_size);
        _hidl_blob.putBool(12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 120);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ((ContextHub) _hidl_vec.get(_hidl_index_0)).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 120));
        }
        _hidl_blob.putBlob(0, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(_hidl_offset + 0, this.name);
        _hidl_blob.putString(16 + _hidl_offset, this.vendor);
        _hidl_blob.putString(32 + _hidl_offset, this.toolchain);
        _hidl_blob.putInt32(48 + _hidl_offset, this.platformVersion);
        _hidl_blob.putInt32(52 + _hidl_offset, this.toolchainVersion);
        _hidl_blob.putInt32(56 + _hidl_offset, this.hubId);
        _hidl_blob.putFloat(60 + _hidl_offset, this.peakMips);
        _hidl_blob.putFloat(64 + _hidl_offset, this.stoppedPowerDrawMw);
        _hidl_blob.putFloat(68 + _hidl_offset, this.sleepPowerDrawMw);
        _hidl_blob.putFloat(72 + _hidl_offset, this.peakPowerDrawMw);
        int _hidl_vec_size = this.connectedSensors.size();
        _hidl_blob.putInt32((_hidl_offset + 80) + 8, _hidl_vec_size);
        _hidl_blob.putBool((_hidl_offset + 80) + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 96);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ((PhysicalSensor) this.connectedSensors.get(_hidl_index_0)).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 96));
        }
        _hidl_blob.putBlob((80 + _hidl_offset) + 0, childBlob);
        _hidl_blob.putInt32(96 + _hidl_offset, this.maxSupportedMsgLen);
        _hidl_blob.putInt64(104 + _hidl_offset, this.chrePlatformId);
        _hidl_blob.putInt8(112 + _hidl_offset, this.chreApiMajorVersion);
        _hidl_blob.putInt8(113 + _hidl_offset, this.chreApiMinorVersion);
        _hidl_blob.putInt16(114 + _hidl_offset, this.chrePatchVersion);
    }
}
