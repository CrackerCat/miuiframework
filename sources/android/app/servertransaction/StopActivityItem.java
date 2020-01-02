package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.Trace;

public class StopActivityItem extends ActivityLifecycleItem {
    public static final Creator<StopActivityItem> CREATOR = new Creator<StopActivityItem>() {
        public StopActivityItem createFromParcel(Parcel in) {
            return new StopActivityItem(in, null);
        }

        public StopActivityItem[] newArray(int size) {
            return new StopActivityItem[size];
        }
    };
    private static final String TAG = "StopActivityItem";
    private int mConfigChanges;
    private boolean mShowWindow;

    /* synthetic */ StopActivityItem(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public void execute(ClientTransactionHandler client, IBinder token, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64, "activityStop");
        client.handleStopActivity(token, this.mShowWindow, this.mConfigChanges, pendingActions, true, "STOP_ACTIVITY_ITEM");
        Trace.traceEnd(64);
    }

    public void postExecute(ClientTransactionHandler client, IBinder token, PendingTransactionActions pendingActions) {
        client.reportStop(pendingActions);
    }

    public int getTargetState() {
        return 5;
    }

    private StopActivityItem() {
    }

    public static StopActivityItem obtain(boolean showWindow, int configChanges) {
        StopActivityItem instance = (StopActivityItem) ObjectPool.obtain(StopActivityItem.class);
        if (instance == null) {
            instance = new StopActivityItem();
        }
        instance.mShowWindow = showWindow;
        instance.mConfigChanges = configChanges;
        return instance;
    }

    public void recycle() {
        super.recycle();
        this.mShowWindow = false;
        this.mConfigChanges = 0;
        ObjectPool.recycle(this);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.mShowWindow);
        dest.writeInt(this.mConfigChanges);
    }

    private StopActivityItem(Parcel in) {
        this.mShowWindow = in.readBoolean();
        this.mConfigChanges = in.readInt();
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StopActivityItem other = (StopActivityItem) o;
        if (!(this.mShowWindow == other.mShowWindow && this.mConfigChanges == other.mConfigChanges)) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (((17 * 31) + this.mShowWindow) * 31) + this.mConfigChanges;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("StopActivityItem{showWindow=");
        stringBuilder.append(this.mShowWindow);
        stringBuilder.append(",configChanges=");
        stringBuilder.append(this.mConfigChanges);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
