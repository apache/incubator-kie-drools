package org.drools.core.util;

public class KeyStoreConstants {

    // true if packages should be signed during serialization
    public static final String PROP_SIGN = "drools.serialization.sign";
    // the URL to the key store where the private key is stored
    public static final String PROP_PVT_KS_URL = "drools.serialization.private.keyStoreURL";
    // the key store password
    public static final String PROP_PVT_KS_PWD = "drools.serialization.private.keyStorePwd";
    // the private key identifier
    public static final String PROP_PVT_ALIAS = "drools.serialization.private.keyAlias";
    // the private key password
    public static final String PROP_PVT_PWD = "drools.serialization.private.keyPwd";
    // the URL to the key store where the public key is stored
    public static final String PROP_PUB_KS_URL = "drools.serialization.public.keyStoreURL";
    // the key store password
    public static final String PROP_PUB_KS_PWD = "drools.serialization.public.keyStorePwd";

    // the URL to the key store where the private key is stored
    public static final String PROP_PWD_KS_URL = "kie.keystore.keyStoreURL";
    // the key store password
    public static final String PROP_PWD_KS_PWD = "kie.keystore.keyStorePwd";
    // the private key identifier
    public static final String PROP_PWD_ALIAS = "kie.keystore.keyAlias";
    // the private key identifier
    public static final String PROP_PWD_PWD = "kie.keystore.keyPwd";

    public static final String KEY_CERTIFICATE_TYPE = "JKS";

    public static final String KEY_PASSWORD_TYPE = "JCEKS";
}
