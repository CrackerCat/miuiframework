package android.telephony.euicc;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.service.carrier.CarrierIdentifier;
import android.text.TextUtils;
import android.text.format.DateFormat;
import com.android.internal.annotations.VisibleForTesting;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

@SystemApi
public final class EuiccRulesAuthTable implements Parcelable {
    public static final Creator<EuiccRulesAuthTable> CREATOR = new Creator<EuiccRulesAuthTable>() {
        public EuiccRulesAuthTable createFromParcel(Parcel source) {
            return new EuiccRulesAuthTable(source, null);
        }

        public EuiccRulesAuthTable[] newArray(int size) {
            return new EuiccRulesAuthTable[size];
        }
    };
    public static final int POLICY_RULE_FLAG_CONSENT_REQUIRED = 1;
    private final CarrierIdentifier[][] mCarrierIds;
    private final int[] mPolicyRuleFlags;
    private final int[] mPolicyRules;

    public static final class Builder {
        private CarrierIdentifier[][] mCarrierIds;
        private int[] mPolicyRuleFlags;
        private int[] mPolicyRules;
        private int mPosition;

        public Builder(int ruleNum) {
            this.mPolicyRules = new int[ruleNum];
            this.mCarrierIds = new CarrierIdentifier[ruleNum][];
            this.mPolicyRuleFlags = new int[ruleNum];
        }

        public EuiccRulesAuthTable build() {
            int i = this.mPosition;
            int[] iArr = this.mPolicyRules;
            if (i == iArr.length) {
                return new EuiccRulesAuthTable(iArr, this.mCarrierIds, this.mPolicyRuleFlags, null);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Not enough rules are added, expected: ");
            stringBuilder.append(this.mPolicyRules.length);
            stringBuilder.append(", added: ");
            stringBuilder.append(this.mPosition);
            throw new IllegalStateException(stringBuilder.toString());
        }

        public Builder add(int policyRules, List<CarrierIdentifier> carrierId, int policyRuleFlags) {
            int i = this.mPosition;
            int[] iArr = this.mPolicyRules;
            if (i < iArr.length) {
                iArr[i] = policyRules;
                if (carrierId != null && carrierId.size() > 0) {
                    this.mCarrierIds[this.mPosition] = (CarrierIdentifier[]) carrierId.toArray(new CarrierIdentifier[carrierId.size()]);
                }
                int[] iArr2 = this.mPolicyRuleFlags;
                int i2 = this.mPosition;
                iArr2[i2] = policyRuleFlags;
                this.mPosition = i2 + 1;
                return this;
            }
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PolicyRuleFlag {
    }

    /* synthetic */ EuiccRulesAuthTable(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    /* synthetic */ EuiccRulesAuthTable(int[] x0, CarrierIdentifier[][] x1, int[] x2, AnonymousClass1 x3) {
        this(x0, x1, x2);
    }

    @VisibleForTesting
    public static boolean match(String mccRule, String mcc) {
        if (mccRule.length() < mcc.length()) {
            return false;
        }
        int i = 0;
        while (i < mccRule.length()) {
            if (mccRule.charAt(i) != DateFormat.DAY && (i >= mcc.length() || mccRule.charAt(i) != mcc.charAt(i))) {
                return false;
            }
            i++;
        }
        return true;
    }

    private EuiccRulesAuthTable(int[] policyRules, CarrierIdentifier[][] carrierIds, int[] policyRuleFlags) {
        this.mPolicyRules = policyRules;
        this.mCarrierIds = carrierIds;
        this.mPolicyRuleFlags = policyRuleFlags;
    }

    public int findIndex(int policy, CarrierIdentifier carrierId) {
        int i = 0;
        loop0:
        while (true) {
            int[] iArr = this.mPolicyRules;
            if (i >= iArr.length) {
                return -1;
            }
            if ((iArr[i] & policy) != 0) {
                CarrierIdentifier[] carrierIds = this.mCarrierIds[i];
                if (!(carrierIds == null || carrierIds.length == 0)) {
                    for (CarrierIdentifier ruleCarrierId : carrierIds) {
                        if (match(ruleCarrierId.getMcc(), carrierId.getMcc()) && match(ruleCarrierId.getMnc(), carrierId.getMnc())) {
                            String gid = ruleCarrierId.getGid1();
                            if (TextUtils.isEmpty(gid) || gid.equals(carrierId.getGid1())) {
                                gid = ruleCarrierId.getGid2();
                                if (TextUtils.isEmpty(gid) || gid.equals(carrierId.getGid2())) {
                                    return i;
                                }
                            }
                        }
                    }
                    continue;
                }
            }
            i++;
        }
        return i;
    }

    public boolean hasPolicyRuleFlag(int index, int flag) {
        if (index >= 0 && index < this.mPolicyRules.length) {
            return (this.mPolicyRuleFlags[index] & flag) != 0;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.mPolicyRules);
        for (CarrierIdentifier[] ids : this.mCarrierIds) {
            dest.writeTypedArray(ids, flags);
        }
        dest.writeIntArray(this.mPolicyRuleFlags);
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EuiccRulesAuthTable that = (EuiccRulesAuthTable) obj;
        if (this.mCarrierIds.length != that.mCarrierIds.length) {
            return false;
        }
        int i = 0;
        while (true) {
            CarrierIdentifier[] carrierIds = this.mCarrierIds;
            if (i < carrierIds.length) {
                carrierIds = carrierIds[i];
                CarrierIdentifier[] thatCarrierIds = that.mCarrierIds[i];
                if (carrierIds == null || thatCarrierIds == null) {
                    if (carrierIds != null || thatCarrierIds != null) {
                        return false;
                    }
                } else if (carrierIds.length != thatCarrierIds.length) {
                    return false;
                } else {
                    for (int j = 0; j < carrierIds.length; j++) {
                        if (!carrierIds[j].equals(thatCarrierIds[j])) {
                            return false;
                        }
                    }
                    continue;
                }
                i++;
            } else {
                if (!(Arrays.equals(this.mPolicyRules, that.mPolicyRules) && Arrays.equals(this.mPolicyRuleFlags, that.mPolicyRuleFlags))) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    private EuiccRulesAuthTable(Parcel source) {
        this.mPolicyRules = source.createIntArray();
        int len = this.mPolicyRules.length;
        this.mCarrierIds = new CarrierIdentifier[len][];
        for (int i = 0; i < len; i++) {
            this.mCarrierIds[i] = (CarrierIdentifier[]) source.createTypedArray(CarrierIdentifier.CREATOR);
        }
        this.mPolicyRuleFlags = source.createIntArray();
    }
}
