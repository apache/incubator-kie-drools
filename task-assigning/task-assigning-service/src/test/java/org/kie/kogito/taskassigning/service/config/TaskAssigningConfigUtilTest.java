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
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClient;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClientConfig;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClientFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskAssigningConfigUtilTest {

    private static final String DATA_INDEX_SERVER_URL = "http://localhost:8180/graphql";
    private static final String AUTH_SERVER_URL = "http://localhost:8280/auth/realms/kogito";
    private static final String CANONIC_AUTH_SERVER_URL = "http://localhost:8280/auth";
    private static final String PROCESS_SERVICE_URL = "http://service1.cloud.com:8280";
    private static final String REALM = "kogito";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CREDENTIALS_SECRET = "CREDENTIALS_SECRET";
    private static final String CLIENT_USER = "CLIENT_USER";
    private static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";

    @Mock
    ClientServices clientServices;

    @Mock
    DataIndexServiceClientFactory dataIndexServiceClientFactory;

    @Mock
    DataIndexServiceClient dataIndexServiceClient;

    @Mock
    ProcessServiceClientFactory processServiceClientFactory;

    @Mock
    ProcessServiceClient processServiceClient;

    @Captor
    ArgumentCaptor<DataIndexServiceClientConfig> dataIndexServiceConfigCaptor;

    @Captor
    ArgumentCaptor<ProcessServiceClientConfig> processServiceConfigCaptor;

    @Captor
    ArgumentCaptor<AuthenticationCredentials> credentialsCaptor;

    @BeforeEach
    void setUp() {
        lenient().doReturn(dataIndexServiceClientFactory).when(clientServices).dataIndexClientFactory();
        lenient().doReturn(processServiceClientFactory).when(clientServices).processServiceClientFactory();
        lenient().doReturn(dataIndexServiceClient).when(dataIndexServiceClientFactory).newClient(any(), any());
        lenient().doReturn(processServiceClient).when(processServiceClientFactory).newClient(any(), any());
    }

    @Test
    void createDataIndexServiceClientKeycloakAuth() throws MalformedURLException {
        createDataIndexServiceClient(buildKeycloakConfig());
        assertKeycloakAuth();
    }

    @Test
    void createDataIndexServiceClientBasicAuth() throws MalformedURLException {
        createDataIndexServiceClient(buildBasicAuthConfig());
        assertBasicAuth();
    }

    @Test
    void createDataIndexServiceClientNoAuth() throws MalformedURLException {
        createDataIndexServiceClient(buildNoAuthConfig());
        assertNoAuth();
    }

    @Test
    void createProcessServiceClientKeycloakAuth() throws MalformedURLException {
        createProcessServiceClient(buildKeycloakConfig());
        assertKeycloakAuth();
    }

    @Test
    void createProcessServiceClientBasicAuth() throws MalformedURLException {
        createProcessServiceClient(buildBasicAuthConfig());
        assertBasicAuth();
    }

    @Test
    void createProcessServiceClientNoAuth() throws MalformedURLException {
        createProcessServiceClient(buildBasicAuthConfig());
        assertBasicAuth();
    }

    private void createDataIndexServiceClient(TaskAssigningConfig config) throws MalformedURLException {
        DataIndexServiceClient result = TaskAssigningConfigUtil.createDataIndexServiceClient(clientServices, config);
        assertThat(result).isSameAs(dataIndexServiceClient);
        verify(dataIndexServiceClientFactory).newClient(dataIndexServiceConfigCaptor.capture(), credentialsCaptor.capture());
        assertThat(dataIndexServiceConfigCaptor.getValue().getServiceUrl()).isEqualTo(new URL(DATA_INDEX_SERVER_URL));
    }

    private void createProcessServiceClient(TaskAssigningConfig config) throws MalformedURLException {
        URL processServiceUrl = new URL(PROCESS_SERVICE_URL);
        ProcessServiceClient result = TaskAssigningConfigUtil.createProcessServiceClient(clientServices, config, processServiceUrl);
        assertThat(result).isSameAs(processServiceClient);
        verify(processServiceClientFactory).newClient(processServiceConfigCaptor.capture(), credentialsCaptor.capture());
        assertThat(processServiceConfigCaptor.getValue().getServiceUrl()).isEqualTo(processServiceUrl);
    }

    private void assertKeycloakAuth() {
        assertThat(credentialsCaptor.getValue()).isExactlyInstanceOf(KeycloakAuthenticationCredentials.class);
        KeycloakAuthenticationCredentials keycloakCredentials = (KeycloakAuthenticationCredentials) credentialsCaptor.getValue();
        assertThat(keycloakCredentials.getServerUrl()).isEqualTo(CANONIC_AUTH_SERVER_URL);
        assertThat(keycloakCredentials.getClientId()).isEqualTo(CLIENT_ID);
        assertThat(keycloakCredentials.getClientSecret()).isEqualTo(CREDENTIALS_SECRET);
        assertThat(keycloakCredentials.getRealm()).isEqualTo(REALM);
        assertThat(keycloakCredentials.getUsername()).isEqualTo(CLIENT_USER);
        assertThat(keycloakCredentials.getPassword()).isEqualTo(CLIENT_PASSWORD);
    }

    private void assertBasicAuth() {
        assertThat(credentialsCaptor.getValue()).isExactlyInstanceOf(BasicAuthenticationCredentials.class);
        BasicAuthenticationCredentials basicCredentials = (BasicAuthenticationCredentials) credentialsCaptor.getValue();
        assertThat(basicCredentials.getUser()).isEqualTo(CLIENT_USER);
        assertThat(basicCredentials.getPassword()).isEqualTo(CLIENT_PASSWORD);
    }

    private void assertNoAuth() {
        assertThat(credentialsCaptor.getValue()).isExactlyInstanceOf(NoAuthenticationCredentials.class);
        assertThat(credentialsCaptor.getValue()).isSameAs(NoAuthenticationCredentials.INSTANCE);
    }

    private TaskAssigningConfig buildConfig() throws MalformedURLException {
        TaskAssigningConfig config = new TaskAssigningConfig();
        config.dataIndexServerUrl = new URL(DATA_INDEX_SERVER_URL);
        return config;

    }

    private TaskAssigningConfig buildKeycloakConfig() throws MalformedURLException {
        TaskAssigningConfig config = buildConfig();
        config.oidcTenantEnabled = true;
        config.oidcAuthServerUrl = Optional.of(new URL(AUTH_SERVER_URL));
        config.oidcClientId = Optional.of(CLIENT_ID);
        config.oidcCredentialsSecret = Optional.of(CREDENTIALS_SECRET);
        config.clientAuthUser = Optional.of(CLIENT_USER);
        config.clientAuthPassword = Optional.of(CLIENT_PASSWORD);
        return config;
    }

    private TaskAssigningConfig buildBasicAuthConfig() throws MalformedURLException {
        TaskAssigningConfig config = buildConfig();
        config.oidcTenantEnabled = false;
        config.clientAuthUser = Optional.of(CLIENT_USER);
        config.clientAuthPassword = Optional.of(CLIENT_PASSWORD);
        return config;
    }

    private TaskAssigningConfig buildNoAuthConfig() throws MalformedURLException {
        TaskAssigningConfig config = buildConfig();
        config.oidcTenantEnabled = false;
        config.clientAuthUser = Optional.empty();
        config.clientAuthPassword = Optional.empty();
        return config;
    }
}
