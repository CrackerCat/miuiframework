package com.android.internal.content;

import android.annotation.UnsupportedAppUsage;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Objects;

public class ReferrerIntent extends Intent {
    public static final Creator<ReferrerIntent> CREATOR = new Creator<ReferrerIntent>() {
        public ReferrerIntent createFromParcel(Parcel source) {
            return new ReferrerIntent(source);
        }

        public ReferrerIntent[] newArray(int size) {
            return new ReferrerIntent[size];
        }
    };
    @UnsupportedAppUsage
    public final String mReferrer;

    @UnsupportedAppUsage
    public ReferrerIntent(Intent baseIntent, String referrer) {
        super(baseIntent);
        this.mReferrer = referrer;
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeString(this.mReferrer);
    }

    ReferrerIntent(Parcel in) {
        readFromParcel(in);
        this.mReferrer = in.readString();
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null || !(obj instanceof ReferrerIntent)) {
            return false;
        }
        ReferrerIntent other = (ReferrerIntent) obj;
        if (filterEquals(other) && Objects.equals(this.mReferrer, other.mReferrer)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return (((17 * 31) + filterHashCode()) * 31) + Objects.hashCode(this.mReferrer);
    }
}
