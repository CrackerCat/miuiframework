package android.service.contentcapture;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SystemApi
public final class ActivityEvent implements Parcelable {
    public static final Creator<ActivityEvent> CREATOR = new Creator<ActivityEvent>() {
        public ActivityEvent createFromParcel(Parcel parcel) {
            return new ActivityEvent((ComponentName) parcel.readParcelable(null), parcel.readInt());
        }

        public ActivityEvent[] newArray(int size) {
            return new ActivityEvent[size];
        }
    };
    public static final int TYPE_ACTIVITY_DESTROYED = 24;
    public static final int TYPE_ACTIVITY_PAUSED = 2;
    public static final int TYPE_ACTIVITY_RESUMED = 1;
    public static final int TYPE_ACTIVITY_STOPPED = 23;
    private final ComponentName mComponentName;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ActivityEventType {
    }

    public ActivityEvent(ComponentName componentName, int type) {
        this.mComponentName = componentName;
        this.mType = type;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public int getEventType() {
        return this.mType;
    }

    public static String getTypeAsString(int type) {
        if (type == 1) {
            return "ACTIVITY_RESUMED";
        }
        if (type == 2) {
            return "ACTIVITY_PAUSED";
        }
        if (type == 23) {
            return "ACTIVITY_STOPPED";
        }
        if (type == 24) {
            return "ACTIVITY_DESTROYED";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UKNOWN_TYPE: ");
        stringBuilder.append(type);
        return stringBuilder.toString();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ActivityEvent[");
        stringBuilder.append(this.mComponentName.toShortString());
        stringBuilder.append("]:");
        stringBuilder.append(getTypeAsString(this.mType));
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mComponentName, flags);
        parcel.writeInt(this.mType);
    }
}
