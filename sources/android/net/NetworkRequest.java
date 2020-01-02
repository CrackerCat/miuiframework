package android.net;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Process;
import android.text.TextUtils;
import android.util.proto.ProtoOutputStream;
import java.util.Objects;
import java.util.Set;

public class NetworkRequest implements Parcelable {
    public static final Creator<NetworkRequest> CREATOR = new Creator<NetworkRequest>() {
        public NetworkRequest createFromParcel(Parcel in) {
            return new NetworkRequest((NetworkCapabilities) NetworkCapabilities.CREATOR.createFromParcel(in), in.readInt(), in.readInt(), Type.valueOf(in.readString()));
        }

        public NetworkRequest[] newArray(int size) {
            return new NetworkRequest[size];
        }
    };
    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    public final int legacyType;
    @UnsupportedAppUsage
    public final NetworkCapabilities networkCapabilities;
    @UnsupportedAppUsage
    public final int requestId;
    public final Type type;

    /* renamed from: android.net.NetworkRequest$2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$net$NetworkRequest$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$android$net$NetworkRequest$Type[Type.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$net$NetworkRequest$Type[Type.LISTEN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$net$NetworkRequest$Type[Type.TRACK_DEFAULT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$net$NetworkRequest$Type[Type.REQUEST.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$net$NetworkRequest$Type[Type.BACKGROUND_REQUEST.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public static class Builder {
        private final NetworkCapabilities mNetworkCapabilities = new NetworkCapabilities();

        public Builder() {
            this.mNetworkCapabilities.setSingleUid(Process.myUid());
        }

        public NetworkRequest build() {
            NetworkCapabilities nc = new NetworkCapabilities(this.mNetworkCapabilities);
            nc.maybeMarkCapabilitiesRestricted();
            return new NetworkRequest(nc, -1, 0, Type.NONE);
        }

        public Builder addCapability(int capability) {
            this.mNetworkCapabilities.addCapability(capability);
            return this;
        }

        public Builder removeCapability(int capability) {
            this.mNetworkCapabilities.removeCapability(capability);
            return this;
        }

        public Builder setCapabilities(NetworkCapabilities nc) {
            this.mNetworkCapabilities.set(nc);
            return this;
        }

        public Builder setUids(Set<UidRange> uids) {
            this.mNetworkCapabilities.setUids(uids);
            return this;
        }

        public Builder addUnwantedCapability(int capability) {
            this.mNetworkCapabilities.addUnwantedCapability(capability);
            return this;
        }

        @UnsupportedAppUsage
        public Builder clearCapabilities() {
            this.mNetworkCapabilities.clearAll();
            return this;
        }

        public Builder addTransportType(int transportType) {
            this.mNetworkCapabilities.addTransportType(transportType);
            return this;
        }

        public Builder removeTransportType(int transportType) {
            this.mNetworkCapabilities.removeTransportType(transportType);
            return this;
        }

        public Builder setLinkUpstreamBandwidthKbps(int upKbps) {
            this.mNetworkCapabilities.setLinkUpstreamBandwidthKbps(upKbps);
            return this;
        }

        public Builder setLinkDownstreamBandwidthKbps(int downKbps) {
            this.mNetworkCapabilities.setLinkDownstreamBandwidthKbps(downKbps);
            return this;
        }

        public Builder setNetworkSpecifier(String networkSpecifier) {
            NetworkSpecifier networkSpecifier2;
            if (TextUtils.isEmpty(networkSpecifier)) {
                networkSpecifier2 = null;
            } else {
                networkSpecifier2 = new StringNetworkSpecifier(networkSpecifier);
            }
            return setNetworkSpecifier(networkSpecifier2);
        }

        public Builder setNetworkSpecifier(NetworkSpecifier networkSpecifier) {
            MatchAllNetworkSpecifier.checkNotMatchAllNetworkSpecifier(networkSpecifier);
            this.mNetworkCapabilities.setNetworkSpecifier(networkSpecifier);
            return this;
        }

        @SystemApi
        public Builder setSignalStrength(int signalStrength) {
            this.mNetworkCapabilities.setSignalStrength(signalStrength);
            return this;
        }
    }

    public enum Type {
        NONE,
        LISTEN,
        TRACK_DEFAULT,
        REQUEST,
        BACKGROUND_REQUEST
    }

    public NetworkRequest(NetworkCapabilities nc, int legacyType, int rId, Type type) {
        if (nc != null) {
            this.requestId = rId;
            this.networkCapabilities = nc;
            this.legacyType = legacyType;
            this.type = type;
            return;
        }
        throw new NullPointerException();
    }

    public NetworkRequest(NetworkRequest that) {
        this.networkCapabilities = new NetworkCapabilities(that.networkCapabilities);
        this.requestId = that.requestId;
        this.legacyType = that.legacyType;
        this.type = that.type;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        this.networkCapabilities.writeToParcel(dest, flags);
        dest.writeInt(this.legacyType);
        dest.writeInt(this.requestId);
        dest.writeString(this.type.name());
    }

    public boolean isListen() {
        return this.type == Type.LISTEN;
    }

    public boolean isRequest() {
        return isForegroundRequest() || isBackgroundRequest();
    }

    public boolean isForegroundRequest() {
        return this.type == Type.TRACK_DEFAULT || this.type == Type.REQUEST;
    }

    public boolean isBackgroundRequest() {
        return this.type == Type.BACKGROUND_REQUEST;
    }

    public boolean hasCapability(int capability) {
        return this.networkCapabilities.hasCapability(capability);
    }

    public boolean hasUnwantedCapability(int capability) {
        return this.networkCapabilities.hasUnwantedCapability(capability);
    }

    public boolean hasTransport(int transportType) {
        return this.networkCapabilities.hasTransport(transportType);
    }

    public String toString() {
        String stringBuilder;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("NetworkRequest [ ");
        stringBuilder2.append(this.type);
        stringBuilder2.append(" id=");
        stringBuilder2.append(this.requestId);
        if (this.legacyType != -1) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(", legacyType=");
            stringBuilder3.append(this.legacyType);
            stringBuilder = stringBuilder3.toString();
        } else {
            stringBuilder = "";
        }
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append(", ");
        stringBuilder2.append(this.networkCapabilities.toString());
        stringBuilder2.append(" ]");
        return stringBuilder2.toString();
    }

    private int typeToProtoEnum(Type t) {
        int i = AnonymousClass2.$SwitchMap$android$net$NetworkRequest$Type[t.ordinal()];
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        if (i == 3) {
            return 3;
        }
        if (i != 4) {
            return i != 5 ? 0 : 5;
        } else {
            return 4;
        }
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1159641169921L, typeToProtoEnum(this.type));
        proto.write(1120986464258L, this.requestId);
        proto.write(1120986464259L, this.legacyType);
        this.networkCapabilities.writeToProto(proto, 1146756268036L);
        proto.end(token);
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof NetworkRequest)) {
            return false;
        }
        NetworkRequest that = (NetworkRequest) obj;
        if (that.legacyType == this.legacyType && that.requestId == this.requestId && that.type == this.type && Objects.equals(that.networkCapabilities, this.networkCapabilities)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.requestId), Integer.valueOf(this.legacyType), this.networkCapabilities, this.type});
    }
}
