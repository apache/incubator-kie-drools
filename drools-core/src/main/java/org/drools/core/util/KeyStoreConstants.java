/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

    public static final String KEY_CERTIFICATE_TYPE = "JKS";

    public static final String KEY_PASSWORD_TYPE = "JCEKS";

    // true if you allow verifying with old sign algorithm "MD5withRSA"
    public static final String PROP_VERIFY_OLD_SIGN = "drools.serialization.verify.old.sign";
}
