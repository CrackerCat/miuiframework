package android.security.keystore;

import android.security.Credentials;
import android.security.GateKeeper;
import android.security.KeyStore;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterDefs;
import android.security.keystore.KeyProperties.BlockMode;
import android.security.keystore.KeyProperties.Digest;
import android.security.keystore.KeyProperties.EncryptionPadding;
import android.security.keystore.KeyProperties.Origin;
import android.security.keystore.KeyProperties.Purpose;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.ProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;

public class AndroidKeyStoreSecretKeyFactorySpi extends SecretKeyFactorySpi {
    private final KeyStore mKeyStore = KeyStore.getInstance();

    /* Access modifiers changed, original: protected */
    public KeySpec engineGetKeySpec(SecretKey key, Class keySpecClass) throws InvalidKeySpecException {
        StringBuilder stringBuilder;
        if (keySpecClass == null) {
            throw new InvalidKeySpecException("keySpecClass == null");
        } else if (!(key instanceof AndroidKeyStoreSecretKey)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Only Android KeyStore secret keys supported: ");
            stringBuilder.append(key != null ? key.getClass().getName() : "null");
            throw new InvalidKeySpecException(stringBuilder.toString());
        } else if (SecretKeySpec.class.isAssignableFrom(keySpecClass)) {
            throw new InvalidKeySpecException("Key material export of Android KeyStore keys is not supported");
        } else if (KeyInfo.class.equals(keySpecClass)) {
            AndroidKeyStoreKey keystoreKey = (AndroidKeyStoreKey) key;
            String keyAliasInKeystore = keystoreKey.getAlias();
            String str = Credentials.USER_PRIVATE_KEY;
            if (keyAliasInKeystore.startsWith(str)) {
                str = keyAliasInKeystore.substring(str.length());
            } else {
                str = Credentials.USER_SECRET_KEY;
                if (keyAliasInKeystore.startsWith(str)) {
                    str = keyAliasInKeystore.substring(str.length());
                } else {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Invalid key alias: ");
                    stringBuilder2.append(keyAliasInKeystore);
                    throw new InvalidKeySpecException(stringBuilder2.toString());
                }
            }
            return getKeyInfo(this.mKeyStore, str, keyAliasInKeystore, keystoreKey.getUid());
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unsupported key spec: ");
            stringBuilder.append(keySpecClass.getName());
            throw new InvalidKeySpecException(stringBuilder.toString());
        }
    }

    static KeyInfo getKeyInfo(KeyStore keyStore, String entryAlias, String keyAliasInKeystore, int keyUid) {
        KeyCharacteristics keyCharacteristics = new KeyCharacteristics();
        int errorCode = keyStore.getKeyCharacteristics(keyAliasInKeystore, null, null, keyUid, keyCharacteristics);
        boolean invalidatedByBiometricEnrollment = true;
        StringBuilder stringBuilder;
        if (errorCode == 1) {
            int keymasterPadding;
            try {
                int origin;
                boolean insideSecureHardware;
                if (keyCharacteristics.hwEnforced.containsTag(KeymasterDefs.KM_TAG_ORIGIN)) {
                    origin = Origin.fromKeymaster(keyCharacteristics.hwEnforced.getEnum(KeymasterDefs.KM_TAG_ORIGIN, -1));
                    insideSecureHardware = true;
                } else if (keyCharacteristics.swEnforced.containsTag(KeymasterDefs.KM_TAG_ORIGIN)) {
                    origin = Origin.fromKeymaster(keyCharacteristics.swEnforced.getEnum(KeymasterDefs.KM_TAG_ORIGIN, -1));
                    insideSecureHardware = false;
                } else {
                    throw new ProviderException("Key origin not available");
                }
                long keySizeUnsigned = keyCharacteristics.getUnsignedInt(KeymasterDefs.KM_TAG_KEY_SIZE, -1);
                if (keySizeUnsigned == -1) {
                    throw new ProviderException("Key size not available");
                } else if (keySizeUnsigned <= 2147483647L) {
                    int keySize = (int) keySizeUnsigned;
                    int purposes = Purpose.allFromKeymaster(keyCharacteristics.getEnums(KeymasterDefs.KM_TAG_PURPOSE));
                    ArrayList encryptionPaddingsList = new ArrayList();
                    ArrayList signaturePaddingsList = new ArrayList();
                    for (Integer intValue : keyCharacteristics.getEnums(KeymasterDefs.KM_TAG_PADDING)) {
                        keymasterPadding = intValue.intValue();
                        try {
                            encryptionPaddingsList.add(EncryptionPadding.fromKeymaster(keymasterPadding));
                        } catch (IllegalArgumentException e) {
                            signaturePaddingsList.add(SignaturePadding.fromKeymaster(keymasterPadding));
                        }
                    }
                    String[] encryptionPaddings = (String[]) encryptionPaddingsList.toArray(new String[encryptionPaddingsList.size()]);
                    String[] signaturePaddings = (String[]) signaturePaddingsList.toArray(new String[signaturePaddingsList.size()]);
                    String[] digests = Digest.allFromKeymaster(keyCharacteristics.getEnums(KeymasterDefs.KM_TAG_DIGEST));
                    String[] blockModes = BlockMode.allFromKeymaster(keyCharacteristics.getEnums(KeymasterDefs.KM_TAG_BLOCK_MODE));
                    int keymasterSwEnforcedUserAuthenticators = keyCharacteristics.swEnforced.getEnum(KeymasterDefs.KM_TAG_USER_AUTH_TYPE, 0);
                    int keymasterHwEnforcedUserAuthenticators = keyCharacteristics.hwEnforced.getEnum(KeymasterDefs.KM_TAG_USER_AUTH_TYPE, 0);
                    List<BigInteger> keymasterSecureUserIds = keyCharacteristics.getUnsignedLongs(KeymasterDefs.KM_TAG_USER_SECURE_ID);
                    Date keyValidityStart = keyCharacteristics.getDate(KeymasterDefs.KM_TAG_ACTIVE_DATETIME);
                    Date keyValidityForOriginationEnd = keyCharacteristics.getDate(KeymasterDefs.KM_TAG_ORIGINATION_EXPIRE_DATETIME);
                    Date keyValidityForConsumptionEnd = keyCharacteristics.getDate(KeymasterDefs.KM_TAG_USAGE_EXPIRE_DATETIME);
                    boolean userAuthenticationRequired = keyCharacteristics.getBoolean(KeymasterDefs.KM_TAG_NO_AUTH_REQUIRED) ^ 1;
                    long userAuthenticationValidityDurationSeconds = keyCharacteristics.getUnsignedInt(KeymasterDefs.KM_TAG_AUTH_TIMEOUT, -1);
                    if (userAuthenticationValidityDurationSeconds <= 2147483647L) {
                        boolean userAuthenticationRequirementEnforcedBySecureHardware = userAuthenticationRequired && keymasterHwEnforcedUserAuthenticators != 0 && keymasterSwEnforcedUserAuthenticators == 0;
                        boolean userAuthenticationValidWhileOnBody = keyCharacteristics.hwEnforced.getBoolean(KeymasterDefs.KM_TAG_ALLOW_WHILE_ON_BODY);
                        boolean trustedUserPresenceRequred = keyCharacteristics.hwEnforced.getBoolean(KeymasterDefs.KM_TAG_TRUSTED_USER_PRESENCE_REQUIRED);
                        if (keymasterSwEnforcedUserAuthenticators != 2 && keymasterHwEnforcedUserAuthenticators != 2) {
                            invalidatedByBiometricEnrollment = false;
                        } else if (keymasterSecureUserIds == null || keymasterSecureUserIds.isEmpty() || keymasterSecureUserIds.contains(getGateKeeperSecureUserId())) {
                            invalidatedByBiometricEnrollment = false;
                        }
                        return new KeyInfo(entryAlias, insideSecureHardware, origin, keySize, keyValidityStart, keyValidityForOriginationEnd, keyValidityForConsumptionEnd, purposes, encryptionPaddings, signaturePaddings, digests, blockModes, userAuthenticationRequired, (int) userAuthenticationValidityDurationSeconds, userAuthenticationRequirementEnforcedBySecureHardware, userAuthenticationValidWhileOnBody, trustedUserPresenceRequred, invalidatedByBiometricEnrollment, keyCharacteristics.hwEnforced.getBoolean(KeymasterDefs.KM_TAG_TRUSTED_CONFIRMATION_REQUIRED));
                    }
                    keySizeUnsigned = new StringBuilder();
                    keySizeUnsigned.append("User authentication timeout validity too long: ");
                    keySizeUnsigned.append(userAuthenticationValidityDurationSeconds);
                    keySizeUnsigned.append(" seconds");
                    throw new ProviderException(keySizeUnsigned.toString());
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Key too large: ");
                    stringBuilder.append(keySizeUnsigned);
                    stringBuilder.append(" bits");
                    throw new ProviderException(stringBuilder.toString());
                }
            } catch (IllegalArgumentException e2) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unsupported encryption padding: ");
                stringBuilder2.append(keymasterPadding);
                throw new ProviderException(stringBuilder2.toString());
            } catch (IllegalArgumentException e3) {
                throw new ProviderException("Unsupported key characteristic", e3);
            }
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to obtain information about key. Keystore error: ");
        stringBuilder.append(errorCode);
        throw new ProviderException(stringBuilder.toString());
    }

    private static BigInteger getGateKeeperSecureUserId() throws ProviderException {
        try {
            return BigInteger.valueOf(GateKeeper.getSecureUserId());
        } catch (IllegalStateException e) {
            throw new ProviderException("Failed to get GateKeeper secure user ID", e);
        }
    }

    /* Access modifiers changed, original: protected */
    public SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("To generate secret key in Android Keystore, use KeyGenerator initialized with ");
        stringBuilder.append(KeyGenParameterSpec.class.getName());
        throw new InvalidKeySpecException(stringBuilder.toString());
    }

    /* Access modifiers changed, original: protected */
    public SecretKey engineTranslateKey(SecretKey key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        } else if (key instanceof AndroidKeyStoreSecretKey) {
            return key;
        } else {
            throw new InvalidKeyException("To import a secret key into Android Keystore, use KeyStore.setEntry");
        }
    }
}
