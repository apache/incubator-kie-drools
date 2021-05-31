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
import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.ClientServices;
import org.kie.kogito.taskassigning.auth.AuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.BasicAuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.NoAuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.OidcClientAuthenticationCredentials;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskAssigningConfigUtilTest {

    private static final String DATA_INDEX_SERVER_URL = "http://localhost:8180/graphql";
    private static final String PROCESS_SERVICE_URL = "http://service1.cloud.com:8280";
    private static final String OIDC_CLIENT = "OIDC_CLIENT";
    private static final String CLIENT_USER = "CLIENT_USER";
    private static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";
    private static final Duration SYNC_INTERVAL = Duration.ofMillis(1);
    private static final Duration WAIT_FOR_IMPROVED_SOLUTION_DURATION = Duration.ofMillis(2);
    private static final Duration IMPROVE_SOLUTION_ON_BACKGROUND_DURATION = Duration.ofMillis(3);
    private static final Duration CONNECT_TIMEOUT_DURATION = Duration.ofMillis(4);
    private static final Duration READ_TIMEOUT_DURATION = Duration.ofMillis(5);

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
    void createDataIndexServiceClientOidcClientAuth() throws MalformedURLException {
        createDataIndexServiceClient(buildOidcClientConfig());
        assertOidcClientAuth();
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
    void createProcessServiceClientOidcClientAuth() throws MalformedURLException {
        createProcessServiceClient(buildOidcClientConfig());
        assertOidcClientAuth();
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
        assertThat(dataIndexServiceConfigCaptor.getValue().getConnectTimeoutMillis()).isEqualTo(CONNECT_TIMEOUT_DURATION.toMillis());
        assertThat(dataIndexServiceConfigCaptor.getValue().getReadTimeoutMillis()).isEqualTo(READ_TIMEOUT_DURATION.toMillis());
    }

    private void createProcessServiceClient(TaskAssigningConfig config) throws MalformedURLException {
        URL processServiceUrl = new URL(PROCESS_SERVICE_URL);
        ProcessServiceClient result = TaskAssigningConfigUtil.createProcessServiceClient(clientServices, config, processServiceUrl);
        assertThat(result).isSameAs(processServiceClient);
        verify(processServiceClientFactory).newClient(processServiceConfigCaptor.capture(), credentialsCaptor.capture());
        assertThat(processServiceConfigCaptor.getValue().getServiceUrl()).isEqualTo(processServiceUrl);
        assertThat(processServiceConfigCaptor.getValue().getConnectTimeoutMillis()).isEqualTo(CONNECT_TIMEOUT_DURATION.toMillis());
        assertThat(processServiceConfigCaptor.getValue().getReadTimeoutMillis()).isEqualTo(READ_TIMEOUT_DURATION.toMillis());
    }

    private void assertOidcClientAuth() {
        assertThat(credentialsCaptor.getValue()).isExactlyInstanceOf(OidcClientAuthenticationCredentials.class);
        OidcClientAuthenticationCredentials oidcClientCredentials = (OidcClientAuthenticationCredentials) credentialsCaptor.getValue();
        assertThat(oidcClientCredentials.getOidcClient()).isEqualTo(OIDC_CLIENT);
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
        config.oidcClient = Optional.empty();
        config.dataIndexServerUrl = new URL(DATA_INDEX_SERVER_URL);
        config.dataIndexConnectTimeoutDuration = CONNECT_TIMEOUT_DURATION;
        config.dataIndexReadTimeoutDuration = READ_TIMEOUT_DURATION;
        config.processRuntimeConnectTimeoutDuration = CONNECT_TIMEOUT_DURATION;
        config.processRuntimeReadTimeoutDuration = READ_TIMEOUT_DURATION;
        config.userServiceSyncInterval = SYNC_INTERVAL;
        config.waitForImprovedSolutionDuration = WAIT_FOR_IMPROVED_SOLUTION_DURATION;
        config.improveSolutionOnBackgroundDuration = IMPROVE_SOLUTION_ON_BACKGROUND_DURATION;
        return config;
    }

    private TaskAssigningConfig buildOidcClientConfig() throws MalformedURLException {
        TaskAssigningConfig config = buildConfig();
        config.oidcClient = Optional.of(OIDC_CLIENT);
        return config;
    }

    private TaskAssigningConfig buildBasicAuthConfig() throws MalformedURLException {
        TaskAssigningConfig config = buildConfig();
        config.oidcClient = Optional.empty();
        config.clientAuthUser = Optional.of(CLIENT_USER);
        config.clientAuthPassword = Optional.of(CLIENT_PASSWORD);
        return config;
    }

    private TaskAssigningConfig buildNoAuthConfig() throws MalformedURLException {
        TaskAssigningConfig config = buildConfig();
        config.oidcClient = Optional.empty();
        config.clientAuthUser = Optional.empty();
        config.clientAuthPassword = Optional.empty();
        return config;
    }
}
