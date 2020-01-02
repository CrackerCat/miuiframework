package android.security.net.config;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import java.util.Locale;

public final class Domain {
    public final String hostname;
    public final boolean subdomainsIncluded;

    public Domain(String hostname, boolean subdomainsIncluded) {
        if (hostname != null) {
            this.hostname = hostname.toLowerCase(Locale.US);
            this.subdomainsIncluded = subdomainsIncluded;
            return;
        }
        throw new NullPointerException("Hostname must not be null");
    }

    public int hashCode() {
        return this.hostname.hashCode() ^ (this.subdomainsIncluded ? MetricsEvent.AUTOFILL_SERVICE_DISABLED_APP : MetricsEvent.ANOMALY_TYPE_UNOPTIMIZED_BT);
    }

    public boolean equals(Object other) {
        boolean z = true;
        if (other == this) {
            return true;
        }
        if (!(other instanceof Domain)) {
            return false;
        }
        Domain otherDomain = (Domain) other;
        if (!(otherDomain.subdomainsIncluded == this.subdomainsIncluded && otherDomain.hostname.equals(this.hostname))) {
            z = false;
        }
        return z;
    }
}
