package android.security.net.config;

import android.os.Environment;
import android.os.UserHandle;
import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Set;

public final class SystemCertificateSource extends DirectoryCertificateSource {
    private final File mUserRemovedCaDir;

    private static class NoPreloadHolder {
        private static final SystemCertificateSource INSTANCE = new SystemCertificateSource();

        private NoPreloadHolder() {
        }
    }

    public /* bridge */ /* synthetic */ Set findAllByIssuerAndSignature(X509Certificate x509Certificate) {
        return super.findAllByIssuerAndSignature(x509Certificate);
    }

    public /* bridge */ /* synthetic */ X509Certificate findByIssuerAndSignature(X509Certificate x509Certificate) {
        return super.findByIssuerAndSignature(x509Certificate);
    }

    public /* bridge */ /* synthetic */ X509Certificate findBySubjectAndPublicKey(X509Certificate x509Certificate) {
        return super.findBySubjectAndPublicKey(x509Certificate);
    }

    public /* bridge */ /* synthetic */ Set getCertificates() {
        return super.getCertificates();
    }

    public /* bridge */ /* synthetic */ void handleTrustStorageUpdate() {
        super.handleTrustStorageUpdate();
    }

    private SystemCertificateSource() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.getenv("ANDROID_ROOT"));
        stringBuilder.append("/etc/security/cacerts");
        super(new File(stringBuilder.toString()));
        this.mUserRemovedCaDir = new File(Environment.getUserConfigDirectory(UserHandle.myUserId()), "cacerts-removed");
    }

    public static SystemCertificateSource getInstance() {
        return NoPreloadHolder.INSTANCE;
    }

    /* Access modifiers changed, original: protected */
    public boolean isCertMarkedAsRemoved(String caFile) {
        return new File(this.mUserRemovedCaDir, caFile).exists();
    }
}
