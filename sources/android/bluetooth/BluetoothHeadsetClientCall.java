package android.bluetooth;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import java.util.UUID;

public final class BluetoothHeadsetClientCall implements Parcelable {
    public static final int CALL_STATE_ACTIVE = 0;
    public static final int CALL_STATE_ALERTING = 3;
    public static final int CALL_STATE_DIALING = 2;
    public static final int CALL_STATE_HELD = 1;
    public static final int CALL_STATE_HELD_BY_RESPONSE_AND_HOLD = 6;
    public static final int CALL_STATE_INCOMING = 4;
    public static final int CALL_STATE_TERMINATED = 7;
    public static final int CALL_STATE_WAITING = 5;
    public static final Creator<BluetoothHeadsetClientCall> CREATOR = new Creator<BluetoothHeadsetClientCall>() {
        public BluetoothHeadsetClientCall createFromParcel(Parcel in) {
            return new BluetoothHeadsetClientCall((BluetoothDevice) in.readParcelable(null), in.readInt(), UUID.fromString(in.readString()), in.readInt(), in.readString(), in.readInt() == 1, in.readInt() == 1, in.readInt() == 1);
        }

        public BluetoothHeadsetClientCall[] newArray(int size) {
            return new BluetoothHeadsetClientCall[size];
        }
    };
    private final long mCreationElapsedMilli;
    private final BluetoothDevice mDevice;
    private final int mId;
    private final boolean mInBandRing;
    private boolean mMultiParty;
    private String mNumber;
    private final boolean mOutgoing;
    private int mState;
    private final UUID mUUID;

    public BluetoothHeadsetClientCall(BluetoothDevice device, int id, int state, String number, boolean multiParty, boolean outgoing, boolean inBandRing) {
        this(device, id, UUID.randomUUID(), state, number, multiParty, outgoing, inBandRing);
    }

    public BluetoothHeadsetClientCall(BluetoothDevice device, int id, UUID uuid, int state, String number, boolean multiParty, boolean outgoing, boolean inBandRing) {
        this.mDevice = device;
        this.mId = id;
        this.mUUID = uuid;
        this.mState = state;
        this.mNumber = number != null ? number : "";
        this.mMultiParty = multiParty;
        this.mOutgoing = outgoing;
        this.mInBandRing = inBandRing;
        this.mCreationElapsedMilli = SystemClock.elapsedRealtime();
    }

    public void setState(int state) {
        this.mState = state;
    }

    public void setNumber(String number) {
        this.mNumber = number;
    }

    public void setMultiParty(boolean multiParty) {
        this.mMultiParty = multiParty;
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    @UnsupportedAppUsage
    public int getId() {
        return this.mId;
    }

    public UUID getUUID() {
        return this.mUUID;
    }

    @UnsupportedAppUsage
    public int getState() {
        return this.mState;
    }

    @UnsupportedAppUsage
    public String getNumber() {
        return this.mNumber;
    }

    public long getCreationElapsedMilli() {
        return this.mCreationElapsedMilli;
    }

    @UnsupportedAppUsage
    public boolean isMultiParty() {
        return this.mMultiParty;
    }

    @UnsupportedAppUsage
    public boolean isOutgoing() {
        return this.mOutgoing;
    }

    public boolean isInBandRing() {
        return this.mInBandRing;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean loggable) {
        StringBuilder builder = new StringBuilder("BluetoothHeadsetClientCall{mDevice: ");
        Object obj = this.mDevice;
        if (!loggable) {
            obj = Integer.valueOf(obj.hashCode());
        }
        builder.append(obj);
        builder.append(", mId: ");
        builder.append(this.mId);
        builder.append(", mUUID: ");
        builder.append(this.mUUID);
        builder.append(", mState: ");
        int i = this.mState;
        switch (i) {
            case 0:
                builder.append("ACTIVE");
                break;
            case 1:
                builder.append("HELD");
                break;
            case 2:
                builder.append("DIALING");
                break;
            case 3:
                builder.append("ALERTING");
                break;
            case 4:
                builder.append("INCOMING");
                break;
            case 5:
                builder.append("WAITING");
                break;
            case 6:
                builder.append("HELD_BY_RESPONSE_AND_HOLD");
                break;
            case 7:
                builder.append("TERMINATED");
                break;
            default:
                builder.append(i);
                break;
        }
        builder.append(", mNumber: ");
        obj = this.mNumber;
        if (!loggable) {
            obj = Integer.valueOf(obj.hashCode());
        }
        builder.append(obj);
        builder.append(", mMultiParty: ");
        builder.append(this.mMultiParty);
        builder.append(", mOutgoing: ");
        builder.append(this.mOutgoing);
        builder.append(", mInBandRing: ");
        builder.append(this.mInBandRing);
        builder.append("}");
        return builder.toString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.mDevice, 0);
        out.writeInt(this.mId);
        out.writeString(this.mUUID.toString());
        out.writeInt(this.mState);
        out.writeString(this.mNumber);
        out.writeInt(this.mMultiParty);
        out.writeInt(this.mOutgoing);
        out.writeInt(this.mInBandRing);
    }

    public int describeContents() {
        return 0;
    }
}
