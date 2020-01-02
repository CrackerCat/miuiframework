package android.service.gatekeeper;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.annotations.VisibleForTesting;

public final class GateKeeperResponse implements Parcelable {
    public static final Creator<GateKeeperResponse> CREATOR = new Creator<GateKeeperResponse>() {
        public GateKeeperResponse createFromParcel(Parcel source) {
            int responseCode = source.readInt();
            GateKeeperResponse response = true;
            if (responseCode == 1) {
                return GateKeeperResponse.createRetryResponse(source.readInt());
            }
            if (responseCode != 0) {
                return GateKeeperResponse.createGenericResponse(responseCode);
            }
            if (source.readInt() != 1) {
                response = null;
            }
            byte[] payload = null;
            int size = source.readInt();
            if (size > 0) {
                payload = new byte[size];
                source.readByteArray(payload);
            }
            return GateKeeperResponse.createOkResponse(payload, response);
        }

        public GateKeeperResponse[] newArray(int size) {
            return new GateKeeperResponse[size];
        }
    };
    public static final int RESPONSE_ERROR = -1;
    public static final int RESPONSE_OK = 0;
    public static final int RESPONSE_RETRY = 1;
    private byte[] mPayload;
    private final int mResponseCode;
    private boolean mShouldReEnroll;
    private int mTimeout;

    private GateKeeperResponse(int responseCode) {
        this.mResponseCode = responseCode;
    }

    @VisibleForTesting
    public static GateKeeperResponse createGenericResponse(int responseCode) {
        return new GateKeeperResponse(responseCode);
    }

    private static GateKeeperResponse createRetryResponse(int timeout) {
        GateKeeperResponse response = new GateKeeperResponse(1);
        response.mTimeout = timeout;
        return response;
    }

    @VisibleForTesting
    public static GateKeeperResponse createOkResponse(byte[] payload, boolean shouldReEnroll) {
        GateKeeperResponse response = new GateKeeperResponse(0);
        response.mPayload = payload;
        response.mShouldReEnroll = shouldReEnroll;
        return response;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mResponseCode);
        int i = this.mResponseCode;
        if (i == 1) {
            dest.writeInt(this.mTimeout);
        } else if (i == 0) {
            dest.writeInt(this.mShouldReEnroll);
            byte[] bArr = this.mPayload;
            if (bArr != null) {
                dest.writeInt(bArr.length);
                dest.writeByteArray(this.mPayload);
                return;
            }
            dest.writeInt(0);
        }
    }

    public byte[] getPayload() {
        return this.mPayload;
    }

    public int getTimeout() {
        return this.mTimeout;
    }

    public boolean getShouldReEnroll() {
        return this.mShouldReEnroll;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }
}
