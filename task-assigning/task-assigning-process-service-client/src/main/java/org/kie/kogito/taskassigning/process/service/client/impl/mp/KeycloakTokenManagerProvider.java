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

import javax.enterprise.context.ApplicationScoped;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.microprofile.client.impl.MpClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.kie.kogito.taskassigning.process.service.client.KeycloakAuthenticationCredentials;

@ApplicationScoped
public class KeycloakTokenManagerProvider {

    public KeycloakTokenManagerProvider() {
        //CDI proxying
    }

    public KeycloakTokenManager newTokenManager(KeycloakAuthenticationCredentials credentials) {
        ResteasyClient client = new MpClientBuilderImpl().build();
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(credentials.getServerUrl())
                .resteasyClient(client)
                .realm(credentials.getRealm())
                .username(credentials.getUsername())
                .password(credentials.getPassword())
                .clientId(credentials.getClientId())
                .clientSecret(credentials.getClientSecret())
                .grantType(OAuth2Constants.PASSWORD)
                .build();

        return new KeycloakTokenManager(keycloak);
    }
}