/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.process.service.client.KeycloakAuthenticationCredentials;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class KeycloakTokenManagerProviderTest {

    private static final String SERVER_URL = "SERVER_URL";
    private static final String REALM = "REALM";
    private static final String USER_NAME = "USER_NAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CLIENT_SECRET = "CLIENT_SECRET";

    @Test
    void newTokenManager() {
        KeycloakAuthenticationCredentials credentials = KeycloakAuthenticationCredentials.newBuilder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .username(USER_NAME)
                .password(PASSWORD)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .build();
        KeycloakTokenManagerProvider provider = new KeycloakTokenManagerProvider();
        assertThat(provider.newTokenManager(credentials)).isNotNull();
    }
}
