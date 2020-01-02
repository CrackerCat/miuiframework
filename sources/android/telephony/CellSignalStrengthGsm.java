package android.telephony;

import android.annotation.UnsupportedAppUsage;
import android.hardware.radio.V1_0.GsmSignalStrength;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import java.util.Objects;

public final class CellSignalStrengthGsm extends CellSignalStrength implements Parcelable {
    public static final Creator<CellSignalStrengthGsm> CREATOR = new Creator<CellSignalStrengthGsm>() {
        public CellSignalStrengthGsm createFromParcel(Parcel in) {
            return new CellSignalStrengthGsm(in, null);
        }

        public CellSignalStrengthGsm[] newArray(int size) {
            return new CellSignalStrengthGsm[size];
        }
    };
    private static final boolean DBG = false;
    private static final int GSM_RSSI_GOOD = -97;
    private static final int GSM_RSSI_GREAT = -89;
    private static final int GSM_RSSI_MAX = -51;
    private static final int GSM_RSSI_MIN = -113;
    private static final int GSM_RSSI_MODERATE = -103;
    private static final int GSM_RSSI_POOR = -107;
    private static final String LOG_TAG = "CellSignalStrengthGsm";
    private static final CellSignalStrengthGsm sInvalid = new CellSignalStrengthGsm();
    private static final int[] sRssiThresholds = new int[]{-107, -103, -97, -89};
    @UnsupportedAppUsage
    private int mBitErrorRate;
    private int mLevel;
    private int mRssi;
    @UnsupportedAppUsage(maxTargetSdk = 28)
    private int mTimingAdvance;

    /* synthetic */ CellSignalStrengthGsm(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    @UnsupportedAppUsage
    public CellSignalStrengthGsm() {
        setDefaultValues();
    }

    public CellSignalStrengthGsm(int rssi, int ber, int ta) {
        this.mRssi = CellSignalStrength.inRangeOrUnavailable(rssi, -113, -51);
        this.mBitErrorRate = CellSignalStrength.inRangeOrUnavailable(ber, 0, 7, 99);
        this.mTimingAdvance = CellSignalStrength.inRangeOrUnavailable(ta, 0, 219);
        updateLevel(null, null);
    }

    public CellSignalStrengthGsm(GsmSignalStrength gsm) {
        this(CellSignalStrength.getRssiDbmFromAsu(gsm.signalStrength), gsm.bitErrorRate, gsm.timingAdvance);
        if (this.mRssi == Integer.MAX_VALUE) {
            setDefaultValues();
        }
    }

    public CellSignalStrengthGsm(CellSignalStrengthGsm s) {
        copyFrom(s);
    }

    /* Access modifiers changed, original: protected */
    public void copyFrom(CellSignalStrengthGsm s) {
        this.mRssi = s.mRssi;
        this.mBitErrorRate = s.mBitErrorRate;
        this.mTimingAdvance = s.mTimingAdvance;
        this.mLevel = s.mLevel;
        this.mMiuiLevel = s.mMiuiLevel;
    }

    public CellSignalStrengthGsm copy() {
        return new CellSignalStrengthGsm(this);
    }

    public void setDefaultValues() {
        this.mRssi = Integer.MAX_VALUE;
        this.mBitErrorRate = Integer.MAX_VALUE;
        this.mTimingAdvance = Integer.MAX_VALUE;
        this.mLevel = 0;
        this.mMiuiLevel = 0;
    }

    public int getLevel() {
        return this.mLevel;
    }

    public void updateLevel(PersistableBundle cc, ServiceState ss) {
        this.mMiuiLevel = MiuiCellSignalStrengthGsm.updateLevel(cc, ss, this.mRssi);
        int[] rssiThresholds;
        if (cc == null) {
            rssiThresholds = sRssiThresholds;
        } else {
            rssiThresholds = cc.getIntArray(CarrierConfigManager.KEY_GSM_RSSI_THRESHOLDS_INT_ARRAY);
            if (rssiThresholds == null || rssiThresholds.length != 4) {
                rssiThresholds = sRssiThresholds;
            }
        }
        int level = 4;
        int i = this.mRssi;
        if (i < -113 || i > -51) {
            this.mLevel = 0;
            return;
        }
        while (level > 0 && this.mRssi < rssiThresholds[level - 1]) {
            level--;
        }
        this.mLevel = level;
    }

    public int getTimingAdvance() {
        return this.mTimingAdvance;
    }

    public int getDbm() {
        return this.mRssi;
    }

    public int getAsuLevel() {
        return CellSignalStrength.getAsuFromRssiDbm(this.mRssi);
    }

    public int getRssi() {
        return this.mRssi;
    }

    public int getBitErrorRate() {
        return this.mBitErrorRate;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.mRssi), Integer.valueOf(this.mBitErrorRate), Integer.valueOf(this.mTimingAdvance)});
    }

    public boolean isValid() {
        return equals(sInvalid) ^ 1;
    }

    public boolean equals(Object o) {
        boolean z = false;
        if (!(o instanceof CellSignalStrengthGsm)) {
            return false;
        }
        CellSignalStrengthGsm s = (CellSignalStrengthGsm) o;
        if (this.mRssi == s.mRssi && this.mBitErrorRate == s.mBitErrorRate && this.mTimingAdvance == s.mTimingAdvance && this.mLevel == s.mLevel) {
            z = true;
        }
        return z;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CellSignalStrengthGsm: rssi=");
        stringBuilder.append(this.mRssi);
        stringBuilder.append(" ber=");
        stringBuilder.append(this.mBitErrorRate);
        stringBuilder.append(" mTa=");
        stringBuilder.append(this.mTimingAdvance);
        stringBuilder.append(" miuiLevel=");
        stringBuilder.append(this.mMiuiLevel);
        stringBuilder.append(" mLevel=");
        stringBuilder.append(this.mLevel);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRssi);
        dest.writeInt(this.mBitErrorRate);
        dest.writeInt(this.mTimingAdvance);
        dest.writeInt(this.mLevel);
        dest.writeInt(this.mMiuiLevel);
    }

    private CellSignalStrengthGsm(Parcel in) {
        this.mRssi = in.readInt();
        this.mBitErrorRate = in.readInt();
        this.mTimingAdvance = in.readInt();
        this.mLevel = in.readInt();
        this.mMiuiLevel = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}
