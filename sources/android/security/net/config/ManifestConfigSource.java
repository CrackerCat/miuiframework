package android.security.net.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.util.Pair;
import java.util.Set;

public class ManifestConfigSource implements ConfigSource {
    private static final boolean DBG = true;
    private static final String LOG_TAG = "NetworkSecurityConfig";
    private final ApplicationInfo mApplicationInfo;
    private ConfigSource mConfigSource;
    private final Context mContext;
    private final Object mLock = new Object();

    private static final class DefaultConfigSource implements ConfigSource {
        private final NetworkSecurityConfig mDefaultConfig;

        DefaultConfigSource(boolean usesCleartextTraffic, ApplicationInfo info) {
            this.mDefaultConfig = NetworkSecurityConfig.getDefaultBuilder(info).setCleartextTrafficPermitted(usesCleartextTraffic).build();
        }

        public NetworkSecurityConfig getDefaultConfig() {
            return this.mDefaultConfig;
        }

        public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs() {
            return null;
        }
    }

    public ManifestConfigSource(Context context) {
        this.mContext = context;
        this.mApplicationInfo = new ApplicationInfo(context.getApplicationInfo());
    }

    public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs() {
        return getConfigSource().getPerDomainConfigs();
    }

    public NetworkSecurityConfig getDefaultConfig() {
        return getConfigSource().getDefaultConfig();
    }

    private ConfigSource getConfigSource() {
        synchronized (this.mLock) {
            if (this.mConfigSource != null) {
                ConfigSource configSource = this.mConfigSource;
                return configSource;
            }
            ConfigSource source;
            int configResource = this.mApplicationInfo.networkSecurityConfigRes;
            boolean debugBuild = true;
            if (configResource != 0) {
                if ((this.mApplicationInfo.flags & 2) == 0) {
                    debugBuild = false;
                }
                String str = LOG_TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Using Network Security Config from resource ");
                stringBuilder.append(this.mContext.getResources().getResourceEntryName(configResource));
                stringBuilder.append(" debugBuild: ");
                stringBuilder.append(debugBuild);
                Log.d(str, stringBuilder.toString());
                source = new XmlConfigSource(this.mContext, configResource, this.mApplicationInfo);
            } else {
                Log.d(LOG_TAG, "No Network Security Config specified, using platform default");
                if ((this.mApplicationInfo.flags & 134217728) == 0 || this.mApplicationInfo.isInstantApp()) {
                    debugBuild = false;
                }
                source = new DefaultConfigSource(debugBuild, this.mApplicationInfo);
            }
            this.mConfigSource = source;
            ConfigSource configSource2 = this.mConfigSource;
            return configSource2;
        }
    }
}
