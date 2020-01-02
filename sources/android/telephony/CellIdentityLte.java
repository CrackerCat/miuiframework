package android.telephony;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import java.util.Objects;
import miui.telephony.PhoneDebug;

public final class CellIdentityLte extends CellIdentity {
    public static final Creator<CellIdentityLte> CREATOR = new Creator<CellIdentityLte>() {
        public CellIdentityLte createFromParcel(Parcel in) {
            in.readInt();
            return CellIdentityLte.createFromParcelBody(in);
        }

        public CellIdentityLte[] newArray(int size) {
            return new CellIdentityLte[size];
        }
    };
    private static final boolean DBG = false;
    private static final int MAX_BANDWIDTH = 20000;
    private static final int MAX_CI = 268435455;
    private static final int MAX_EARFCN = 262143;
    private static final int MAX_PCI = 503;
    private static final int MAX_TAC = 65535;
    private static final String TAG = CellIdentityLte.class.getSimpleName();
    private final int mBandwidth;
    private final int mCi;
    private final int mEarfcn;
    private final int mPci;
    private final int mTac;

    @UnsupportedAppUsage
    public CellIdentityLte() {
        super(TAG, 3, null, null, null, null);
        this.mCi = Integer.MAX_VALUE;
        this.mPci = Integer.MAX_VALUE;
        this.mTac = Integer.MAX_VALUE;
        this.mEarfcn = Integer.MAX_VALUE;
        this.mBandwidth = Integer.MAX_VALUE;
    }

    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    public CellIdentityLte(int mcc, int mnc, int ci, int pci, int tac) {
        this(ci, pci, tac, Integer.MAX_VALUE, Integer.MAX_VALUE, String.valueOf(mcc), String.valueOf(mnc), null, null);
    }

    public CellIdentityLte(int ci, int pci, int tac, int earfcn, int bandwidth, String mccStr, String mncStr, String alphal, String alphas) {
        super(TAG, 3, mccStr, mncStr, alphal, alphas);
        int i = ci;
        this.mCi = CellIdentity.inRangeOrUnavailable(ci, 0, (int) MAX_CI);
        int i2 = pci;
        this.mPci = CellIdentity.inRangeOrUnavailable(pci, 0, 503);
        int i3 = tac;
        this.mTac = CellIdentity.inRangeOrUnavailable(tac, 0, 65535);
        int i4 = earfcn;
        this.mEarfcn = CellIdentity.inRangeOrUnavailable(earfcn, 0, (int) MAX_EARFCN);
        int i5 = bandwidth;
        this.mBandwidth = CellIdentity.inRangeOrUnavailable(bandwidth, 0, 20000);
    }

    public CellIdentityLte(android.hardware.radio.V1_0.CellIdentityLte cid) {
        this(cid.ci, cid.pci, cid.tac, cid.earfcn, Integer.MAX_VALUE, cid.mcc, cid.mnc, "", "");
    }

    public CellIdentityLte(android.hardware.radio.V1_2.CellIdentityLte cid) {
        this(cid.base.ci, cid.base.pci, cid.base.tac, cid.base.earfcn, cid.bandwidth, cid.base.mcc, cid.base.mnc, cid.operatorNames.alphaLong, cid.operatorNames.alphaShort);
    }

    private CellIdentityLte(CellIdentityLte cid) {
        this(cid.mCi, cid.mPci, cid.mTac, cid.mEarfcn, cid.mBandwidth, cid.mMccStr, cid.mMncStr, cid.mAlphaLong, cid.mAlphaShort);
    }

    public CellIdentityLte sanitizeLocationInfo() {
        return new CellIdentityLte(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, this.mMccStr, this.mMncStr, this.mAlphaLong, this.mAlphaShort);
    }

    /* Access modifiers changed, original: 0000 */
    public CellIdentityLte copy() {
        return new CellIdentityLte(this);
    }

    @Deprecated
    public int getMcc() {
        return this.mMccStr != null ? Integer.valueOf(this.mMccStr).intValue() : Integer.MAX_VALUE;
    }

    @Deprecated
    public int getMnc() {
        return this.mMncStr != null ? Integer.valueOf(this.mMncStr).intValue() : Integer.MAX_VALUE;
    }

    public int getCi() {
        return this.mCi;
    }

    public int getPci() {
        return this.mPci;
    }

    public int getTac() {
        return this.mTac;
    }

    public int getEarfcn() {
        return this.mEarfcn;
    }

    public int getBandwidth() {
        return this.mBandwidth;
    }

    public String getMccString() {
        return this.mMccStr;
    }

    public String getMncString() {
        return this.mMncStr;
    }

    public String getMobileNetworkOperator() {
        if (this.mMccStr == null || this.mMncStr == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mMccStr);
        stringBuilder.append(this.mMncStr);
        return stringBuilder.toString();
    }

    public int getChannelNumber() {
        return this.mEarfcn;
    }

    public GsmCellLocation asCellLocation() {
        GsmCellLocation cl = new GsmCellLocation();
        int tac = this.mTac;
        int cid = -1;
        if (tac == Integer.MAX_VALUE) {
            tac = -1;
        }
        int i = this.mCi;
        if (i != Integer.MAX_VALUE) {
            cid = i;
        }
        cl.setLacAndCid(tac, cid);
        cl.setPsc(0);
        return cl;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.mCi), Integer.valueOf(this.mPci), Integer.valueOf(this.mTac), Integer.valueOf(super.hashCode())});
    }

    public boolean equals(Object other) {
        boolean z = true;
        if (this == other) {
            return true;
        }
        if (!(other instanceof CellIdentityLte)) {
            return false;
        }
        CellIdentityLte o = (CellIdentityLte) other;
        if (!(this.mCi == o.mCi && this.mPci == o.mPci && this.mTac == o.mTac && this.mEarfcn == o.mEarfcn && this.mBandwidth == o.mBandwidth && TextUtils.equals(this.mMccStr, o.mMccStr) && TextUtils.equals(this.mMncStr, o.mMncStr) && super.equals(other))) {
            z = false;
        }
        return z;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(TAG);
        sb.append(":{");
        if (PhoneDebug.VDBG) {
            sb.append(" mCi=");
            sb.append(this.mCi);
            sb.append(" mPci=");
            sb.append(this.mPci);
            sb.append(" mTac=");
            sb.append(this.mTac);
            sb.append(" mEarfcn=");
            sb.append(this.mEarfcn);
        }
        sb.append(" mBandwidth=");
        sb.append(this.mBandwidth);
        sb.append(" mMcc=");
        sb.append(this.mMccStr);
        sb.append(" mMnc=");
        sb.append(this.mMncStr);
        sb.append(" mAlphaLong=");
        sb.append(this.mAlphaLong);
        sb.append(" mAlphaShort=");
        sb.append(this.mAlphaShort);
        sb.append("}");
        return sb.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, 3);
        dest.writeInt(this.mCi);
        dest.writeInt(this.mPci);
        dest.writeInt(this.mTac);
        dest.writeInt(this.mEarfcn);
        dest.writeInt(this.mBandwidth);
    }

    private CellIdentityLte(Parcel in) {
        super(TAG, 3, in);
        this.mCi = in.readInt();
        this.mPci = in.readInt();
        this.mTac = in.readInt();
        this.mEarfcn = in.readInt();
        this.mBandwidth = in.readInt();
    }

    protected static CellIdentityLte createFromParcelBody(Parcel in) {
        return new CellIdentityLte(in);
    }
}
