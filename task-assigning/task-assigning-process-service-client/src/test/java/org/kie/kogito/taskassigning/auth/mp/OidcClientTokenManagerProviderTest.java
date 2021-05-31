/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.auth.mp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.auth.OidcClientAuthenticationCredentials;
import org.kie.kogito.taskassigning.config.OidcClientLookup;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.oidc.client.OidcClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OidcClientTokenManagerProviderTest {

    private static final String OIDC_CLIENT = "OIDC_CLIENT";

    @Mock
    private OidcClientLookup oidcClientLookup;

    @Mock
    private OidcClient oidcClient;

    private OidcClientTokenManagerProvider tokenManagerProvider;

    private OidcClientAuthenticationCredentials credentials;

    @BeforeEach
    void setUp() {
        credentials = OidcClientAuthenticationCredentials.newBuilder().oidcClient(OIDC_CLIENT).build();
        tokenManagerProvider = new OidcClientTokenManagerProvider(oidcClientLookup);
    }

    @Test
    void newTokenManager() {
        doReturn(oidcClient).when(oidcClientLookup).lookup(OIDC_CLIENT);
        assertThat(tokenManagerProvider.newTokenManager(credentials)).isNotNull();
    }

    @Test
    void newTokenManagerFailure() {
        doReturn(null).when(oidcClientLookup).lookup(OIDC_CLIENT);
        assertThatThrownBy(() -> tokenManagerProvider.newTokenManager(credentials))
                .hasMessageContaining("No OidcClient was found")
                .hasMessageContaining(OIDC_CLIENT);
    }
}
