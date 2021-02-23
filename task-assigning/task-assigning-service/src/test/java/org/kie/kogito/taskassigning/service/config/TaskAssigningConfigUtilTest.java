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

package org.kie.kogito.taskassigning.service.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.ClientServices;
import org.kie.kogito.taskassigning.auth.AuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.BasicAuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.KeycloakAuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.NoAuthenticationCredentials;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClient;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClientConfig;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClientFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskAssigningConfigUtilTest {

    private static final String DATA_INDEX_SERVER_URL = "http://localhost:8180/graphql";
    private static final String AUTH_SERVER_URL = "http://localhost:8280/auth/realms/kogito";
    private static final String CANONIC_AUTH_SERVER_URL = "http://localhost:8280/auth";
    private static final String REALM = "kogito";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CREDENTIALS_SECRET = "CREDENTIALS_SECRET";
    private static final String CLIENT_USER = "CLIENT_USER";
    private static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";

    @Mock
    ClientServices clientServices;

    @Mock
    DataIndexServiceClientFactory clientFactory;

    @Mock
    DataIndexServiceClient serviceClient;

    @Captor
    ArgumentCaptor<DataIndexServiceClientConfig> serviceConfigCaptor;

    @Captor
    ArgumentCaptor<AuthenticationCredentials> credentialsCaptor;

    @BeforeEach
    void setUp() {
        doReturn(clientFactory).when(clientServices).dataIndexClientFactory();
        doReturn(serviceClient).when(clientFactory).newClient(any(), any());
    }

    @Test
    void createDataIndexServiceClientKeycloakAuth() throws MalformedURLException {
        TaskAssigningConfig config = new TaskAssigningConfig();
        config.oidcTenantEnabled = true;
        config.dataIndexServerUrl = new URL(DATA_INDEX_SERVER_URL);
        config.oidcAuthServerUrl = Optional.of(new URL(AUTH_SERVER_URL));
        config.oidcClientId = Optional.of(CLIENT_ID);
        config.oidcCredentialsSecret = Optional.of(CREDENTIALS_SECRET);
        config.clientAuthUser = Optional.of(CLIENT_USER);
        config.clientAuthPassword = Optional.of(CLIENT_PASSWORD);

        DataIndexServiceClient result = TaskAssigningConfigUtil.createDataIndexServiceClient(clientServices, config);
        assertThat(result).isSameAs(serviceClient);
        verify(clientFactory).newClient(serviceConfigCaptor.capture(), credentialsCaptor.capture());
        assertThat(serviceConfigCaptor.getValue().getServiceUrl()).isEqualTo(new URL(DATA_INDEX_SERVER_URL));
        assertThat(credentialsCaptor.getValue()).isExactlyInstanceOf(KeycloakAuthenticationCredentials.class);
        KeycloakAuthenticationCredentials keycloakCredentials = (KeycloakAuthenticationCredentials) credentialsCaptor.getValue();
        assertThat(keycloakCredentials.getServerUrl()).isEqualTo(CANONIC_AUTH_SERVER_URL);
        assertThat(keycloakCredentials.getClientId()).isEqualTo(CLIENT_ID);
        assertThat(keycloakCredentials.getClientSecret()).isEqualTo(CREDENTIALS_SECRET);
        assertThat(keycloakCredentials.getRealm()).isEqualTo(REALM);
        assertThat(keycloakCredentials.getUsername()).isEqualTo(CLIENT_USER);
        assertThat(keycloakCredentials.getPassword()).isEqualTo(CLIENT_PASSWORD);
    }

    @Test
    void createDataIndexServiceClientBasicAuth() throws MalformedURLException {
        TaskAssigningConfig config = new TaskAssigningConfig();
        config.oidcTenantEnabled = false;
        config.dataIndexServerUrl = new URL(DATA_INDEX_SERVER_URL);
        config.clientAuthUser = Optional.of(CLIENT_USER);
        config.clientAuthPassword = Optional.of(CLIENT_PASSWORD);

        DataIndexServiceClient result = TaskAssigningConfigUtil.createDataIndexServiceClient(clientServices, config);
        assertThat(result).isSameAs(serviceClient);
        verify(clientFactory).newClient(serviceConfigCaptor.capture(), credentialsCaptor.capture());
        assertThat(serviceConfigCaptor.getValue().getServiceUrl()).isEqualTo(new URL(DATA_INDEX_SERVER_URL));
        assertThat(credentialsCaptor.getValue()).isExactlyInstanceOf(BasicAuthenticationCredentials.class);
        BasicAuthenticationCredentials basicCredentials = (BasicAuthenticationCredentials) credentialsCaptor.getValue();
        assertThat(basicCredentials.getUser()).isEqualTo(CLIENT_USER);
        assertThat(basicCredentials.getPassword()).isEqualTo(CLIENT_PASSWORD);
    }

    @Test
    void createDataIndexServiceClientNoAuth() throws MalformedURLException {
        TaskAssigningConfig config = new TaskAssigningConfig();
        config.oidcTenantEnabled = false;
        config.dataIndexServerUrl = new URL(DATA_INDEX_SERVER_URL);
        config.clientAuthUser = Optional.empty();
        config.clientAuthPassword = Optional.empty();

        DataIndexServiceClient result = TaskAssigningConfigUtil.createDataIndexServiceClient(clientServices, config);
        assertThat(result).isSameAs(serviceClient);
        verify(clientFactory).newClient(serviceConfigCaptor.capture(), credentialsCaptor.capture());
        assertThat(serviceConfigCaptor.getValue().getServiceUrl()).isEqualTo(new URL(DATA_INDEX_SERVER_URL));
        assertThat(credentialsCaptor.getValue()).isExactlyInstanceOf(NoAuthenticationCredentials.class);
        assertThat(credentialsCaptor.getValue()).isSameAs(NoAuthenticationCredentials.INSTANCE);
    }
}
