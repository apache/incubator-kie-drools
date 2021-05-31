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

import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.auth.OidcClientAuthenticationCredentials;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OidcClientAuthenticationFilterProviderTest extends AbstractAuthenticationFilterProviderTest<OidcClientAuthenticationCredentials> {

    private static final String OIDC_CLIENT = "OIDC_CLIENT";

    @Mock
    private OidcClientTokenManagerProvider tokenManagerProvider;

    @Override
    protected AuthenticationFilterProvider<OidcClientAuthenticationCredentials> createProvider() {
        return new OidcClientAuthenticationFilterProvider(tokenManagerProvider);
    }

    @Override
    protected Class<OidcClientAuthenticationCredentials> getType() {
        return OidcClientAuthenticationCredentials.class;
    }

    @Override
    protected OidcClientAuthenticationCredentials getCredentials() {
        return OidcClientAuthenticationCredentials.newBuilder().oidcClient(OIDC_CLIENT).build();
    }
}
