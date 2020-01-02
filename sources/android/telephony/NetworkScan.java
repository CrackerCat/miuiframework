package android.telephony;

import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephony.Stub;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NetworkScan {
    public static final int ERROR_INTERRUPTED = 10002;
    public static final int ERROR_INVALID_SCAN = 2;
    public static final int ERROR_INVALID_SCANID = 10001;
    public static final int ERROR_MODEM_ERROR = 1;
    public static final int ERROR_MODEM_UNAVAILABLE = 3;
    public static final int ERROR_RADIO_INTERFACE_ERROR = 10000;
    public static final int ERROR_UNSUPPORTED = 4;
    public static final int SUCCESS = 0;
    private static final String TAG = "NetworkScan";
    private final int mScanId;
    private final int mSubId;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ScanErrorCode {
    }

    public void stopScan() {
        ITelephony telephony = getITelephony();
        String str = TAG;
        if (telephony == null) {
            Rlog.e(str, "Failed to get the ITelephony instance.");
        }
        try {
            telephony.stopNetworkScan(this.mSubId, this.mScanId);
        } catch (IllegalArgumentException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("stopNetworkScan - no active scan for ScanID=");
            stringBuilder.append(this.mScanId);
            Rlog.d(str, stringBuilder.toString());
        } catch (RemoteException ex) {
            Rlog.e(str, "stopNetworkScan  RemoteException", ex);
        } catch (RuntimeException ex2) {
            Rlog.e(str, "stopNetworkScan  RuntimeException", ex2);
        }
    }

    @Deprecated
    public void stop() throws RemoteException {
        try {
            stopScan();
        } catch (RuntimeException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to stop the network scan with id ");
            stringBuilder.append(this.mScanId);
            throw new RemoteException(stringBuilder.toString());
        }
    }

    public NetworkScan(int scanId, int subId) {
        this.mScanId = scanId;
        this.mSubId = subId;
    }

    private ITelephony getITelephony() {
        return Stub.asInterface(ServiceManager.getService("phone"));
    }
}
