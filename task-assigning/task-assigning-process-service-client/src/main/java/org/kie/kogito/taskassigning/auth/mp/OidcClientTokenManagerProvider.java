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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.taskassigning.auth.OidcClientAuthenticationCredentials;
import org.kie.kogito.taskassigning.config.OidcClientLookup;

import io.quarkus.oidc.client.OidcClient;

@ApplicationScoped
public class OidcClientTokenManagerProvider {

    private final OidcClientLookup oidcClientLookup;

    @Inject
    public OidcClientTokenManagerProvider(OidcClientLookup oidcClientLookup) {
        this.oidcClientLookup = oidcClientLookup;
    }

    public OidcClientTokenManager newTokenManager(OidcClientAuthenticationCredentials credentials) {
        OidcClient oidcClient = oidcClientLookup.lookup(credentials.getOidcClient());
        if (oidcClient == null) {
            throw new IllegalArgumentException("No OidcClient was found for the configured value" +
                    " OidcClientAuthenticationCredentials.oidcClient: " + credentials.getOidcClient());
        }
        return new OidcClientTokenManager(oidcClient);
    }
}
