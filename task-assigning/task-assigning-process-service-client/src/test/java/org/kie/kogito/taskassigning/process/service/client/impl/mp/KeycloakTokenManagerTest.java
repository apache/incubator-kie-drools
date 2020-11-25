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
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class KeycloakTokenManagerTest {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    @Mock
    private Keycloak keycloak;

    @Mock
    private TokenManager tokenManager;

    @Test
    void getAccessTokenString() {
        doReturn(tokenManager).when(keycloak).tokenManager();
        doReturn(ACCESS_TOKEN).when(tokenManager).getAccessTokenString();
        KeycloakTokenManager keycloakTokenManager = new KeycloakTokenManager(keycloak);
        assertThat(keycloakTokenManager.getAccessTokenString()).isEqualTo(ACCESS_TOKEN);
    }
}
