package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public class ModemActivityInfo implements Parcelable {
    public static final Creator<ModemActivityInfo> CREATOR = new Creator<ModemActivityInfo>() {
        public ModemActivityInfo createFromParcel(Parcel in) {
            long timestamp = in.readLong();
            int sleepTimeMs = in.readInt();
            int idleTimeMs = in.readInt();
            int[] txTimeMs = new int[5];
            for (int i = 0; i < 5; i++) {
                txTimeMs[i] = in.readInt();
            }
            return new ModemActivityInfo(timestamp, sleepTimeMs, idleTimeMs, txTimeMs, in.readInt(), in.readInt());
        }

        public ModemActivityInfo[] newArray(int size) {
            return new ModemActivityInfo[size];
        }
    };
    public static final int TX_POWER_LEVELS = 5;
    private int mEnergyUsed;
    private int mIdleTimeMs;
    private int mRxTimeMs;
    private int mSleepTimeMs;
    private long mTimestamp;
    private int[] mTxTimeMs = new int[5];

    public ModemActivityInfo(long timestamp, int sleepTimeMs, int idleTimeMs, int[] txTimeMs, int rxTimeMs, int energyUsed) {
        this.mTimestamp = timestamp;
        this.mSleepTimeMs = sleepTimeMs;
        this.mIdleTimeMs = idleTimeMs;
        if (txTimeMs != null) {
            System.arraycopy(txTimeMs, 0, this.mTxTimeMs, 0, Math.min(txTimeMs.length, 5));
        }
        this.mRxTimeMs = rxTimeMs;
        this.mEnergyUsed = energyUsed;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ModemActivityInfo{ mTimestamp=");
        stringBuilder.append(this.mTimestamp);
        stringBuilder.append(" mSleepTimeMs=");
        stringBuilder.append(this.mSleepTimeMs);
        stringBuilder.append(" mIdleTimeMs=");
        stringBuilder.append(this.mIdleTimeMs);
        stringBuilder.append(" mTxTimeMs[]=");
        stringBuilder.append(Arrays.toString(this.mTxTimeMs));
        stringBuilder.append(" mRxTimeMs=");
        stringBuilder.append(this.mRxTimeMs);
        stringBuilder.append(" mEnergyUsed=");
        stringBuilder.append(this.mEnergyUsed);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mTimestamp);
        dest.writeInt(this.mSleepTimeMs);
        dest.writeInt(this.mIdleTimeMs);
        for (int i = 0; i < 5; i++) {
            dest.writeInt(this.mTxTimeMs[i]);
        }
        dest.writeInt(this.mRxTimeMs);
        dest.writeInt(this.mEnergyUsed);
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    public int[] getTxTimeMillis() {
        return this.mTxTimeMs;
    }

    public void setTxTimeMillis(int[] txTimeMs) {
        this.mTxTimeMs = txTimeMs;
    }

    public int getSleepTimeMillis() {
        return this.mSleepTimeMs;
    }

    public void setSleepTimeMillis(int sleepTimeMillis) {
        this.mSleepTimeMs = sleepTimeMillis;
    }

    public int getIdleTimeMillis() {
        return this.mIdleTimeMs;
    }

    public void setIdleTimeMillis(int idleTimeMillis) {
        this.mIdleTimeMs = idleTimeMillis;
    }

    public int getRxTimeMillis() {
        return this.mRxTimeMs;
    }

    public void setRxTimeMillis(int rxTimeMillis) {
        this.mRxTimeMs = rxTimeMillis;
    }

    public int getEnergyUsed() {
        return this.mEnergyUsed;
    }

    public void setEnergyUsed(int energyUsed) {
        this.mEnergyUsed = energyUsed;
    }

    public boolean isValid() {
        boolean z = false;
        for (int txVal : getTxTimeMillis()) {
            if (txVal < 0) {
                return false;
            }
        }
        if (getIdleTimeMillis() >= 0 && getSleepTimeMillis() >= 0 && getRxTimeMillis() >= 0 && getEnergyUsed() >= 0 && !isEmpty()) {
            z = true;
        }
        return z;
    }

    private boolean isEmpty() {
        boolean z = false;
        for (int txVal : getTxTimeMillis()) {
            if (txVal != 0) {
                return false;
            }
        }
        if (getIdleTimeMillis() == 0 && getSleepTimeMillis() == 0 && getRxTimeMillis() == 0 && getEnergyUsed() == 0) {
            z = true;
        }
        return z;
    }
}
