package android.view;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.IWindowFocusObserver.Stub;
import java.util.HashMap;

public class WindowId implements Parcelable {
    public static final Creator<WindowId> CREATOR = new Creator<WindowId>() {
        public WindowId createFromParcel(Parcel in) {
            IBinder target = in.readStrongBinder();
            return target != null ? new WindowId(target) : null;
        }

        public WindowId[] newArray(int size) {
            return new WindowId[size];
        }
    };
    private final IWindowId mToken;

    public static abstract class FocusObserver {
        final Handler mHandler = new H();
        final Stub mIObserver = new Stub() {
            public void focusGained(IBinder inputToken) {
                WindowId token;
                synchronized (FocusObserver.this.mRegistrations) {
                    token = (WindowId) FocusObserver.this.mRegistrations.get(inputToken);
                }
                if (FocusObserver.this.mHandler != null) {
                    FocusObserver.this.mHandler.sendMessage(FocusObserver.this.mHandler.obtainMessage(1, token));
                } else {
                    FocusObserver.this.onFocusGained(token);
                }
            }

            public void focusLost(IBinder inputToken) {
                WindowId token;
                synchronized (FocusObserver.this.mRegistrations) {
                    token = (WindowId) FocusObserver.this.mRegistrations.get(inputToken);
                }
                if (FocusObserver.this.mHandler != null) {
                    FocusObserver.this.mHandler.sendMessage(FocusObserver.this.mHandler.obtainMessage(2, token));
                } else {
                    FocusObserver.this.onFocusLost(token);
                }
            }
        };
        final HashMap<IBinder, WindowId> mRegistrations = new HashMap();

        class H extends Handler {
            H() {
            }

            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i == 1) {
                    FocusObserver.this.onFocusGained((WindowId) msg.obj);
                } else if (i != 2) {
                    super.handleMessage(msg);
                } else {
                    FocusObserver.this.onFocusLost((WindowId) msg.obj);
                }
            }
        }

        public abstract void onFocusGained(WindowId windowId);

        public abstract void onFocusLost(WindowId windowId);
    }

    public boolean isFocused() {
        try {
            return this.mToken.isFocused();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void registerFocusObserver(FocusObserver observer) {
        synchronized (observer.mRegistrations) {
            if (observer.mRegistrations.containsKey(this.mToken.asBinder())) {
                throw new IllegalStateException("Focus observer already registered with input token");
            }
            observer.mRegistrations.put(this.mToken.asBinder(), this);
            try {
                this.mToken.registerFocusObserver(observer.mIObserver);
            } catch (RemoteException e) {
            }
        }
    }

    public void unregisterFocusObserver(FocusObserver observer) {
        synchronized (observer.mRegistrations) {
            if (observer.mRegistrations.remove(this.mToken.asBinder()) != null) {
                try {
                    this.mToken.unregisterFocusObserver(observer.mIObserver);
                } catch (RemoteException e) {
                }
            } else {
                throw new IllegalStateException("Focus observer not registered with input token");
            }
        }
    }

    public boolean equals(Object otherObj) {
        if (otherObj instanceof WindowId) {
            return this.mToken.asBinder().equals(((WindowId) otherObj).mToken.asBinder());
        }
        return false;
    }

    public int hashCode() {
        return this.mToken.asBinder().hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("IntentSender{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(": ");
        sb.append(this.mToken.asBinder());
        sb.append('}');
        return sb.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeStrongBinder(this.mToken.asBinder());
    }

    public IWindowId getTarget() {
        return this.mToken;
    }

    public WindowId(IWindowId target) {
        this.mToken = target;
    }

    public WindowId(IBinder target) {
        this.mToken = IWindowId.Stub.asInterface(target);
    }
}
