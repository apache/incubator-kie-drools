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

package org.kie.kogito.taskassigning.process.service.client;

import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.process.service.client.WireMockKeycloakResource.CLIENT_ID;
import static org.kie.kogito.taskassigning.process.service.client.WireMockKeycloakResource.KEYCLOAK_PASSWORD;
import static org.kie.kogito.taskassigning.process.service.client.WireMockKeycloakResource.KEYCLOAK_USER;
import static org.kie.kogito.taskassigning.process.service.client.WireMockKeycloakResource.KEY_CLOAK_SERVICE_URL;
import static org.kie.kogito.taskassigning.process.service.client.WireMockKeycloakResource.REALM;
import static org.kie.kogito.taskassigning.process.service.client.WireMockKeycloakResource.SECRET;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.AUTH_PASSWORD;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.AUTH_USER;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.BASIC_AUTH_PROCESS_ID;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.GROUP1;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.GROUP2;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.KEYCLOAK_AUTH_PROCESS_ID;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.PHASE1;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.PHASE2;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.PROCESS_ID;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.PROCESS_INSTANCE_ID;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.PROCESS_SERVICE_URL;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.TASK_ID;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.USER;
import static org.kie.kogito.taskassigning.process.service.client.WireMockProcessResource.WORKITEM_ID;

@QuarkusTest
@QuarkusTestResource(WireMockProcessResource.class)
@QuarkusTestResource(WireMockKeycloakResource.class)
class ProcessServiceClientTest {

    @Inject
    ClientServices clientServices;

    @Test
    void getAvailablePhases() {
        ProcessServiceClientConfig config = createServiceConfig();
        ProcessServiceClient client = clientServices.processServiceClientFactory().newClient(config, NoAuthenticationCredentials.INSTANCE);
        Set<String> phases = client.getAvailablePhases(PROCESS_ID,
                                                       PROCESS_INSTANCE_ID,
                                                       TASK_ID,
                                                       WORKITEM_ID,
                                                       USER,
                                                       Arrays.asList(GROUP1, GROUP2));

        assertThat(phases).containsExactlyInAnyOrder(PHASE2, PHASE1);
    }

    @Test
    void getAvailablePhasesBasicAuthentication() {
        ProcessServiceClientConfig config = createServiceConfig();
        BasicAuthenticationCredentials credentials = BasicAuthenticationCredentials.newBuilder()
                .user(AUTH_USER)
                .password(AUTH_PASSWORD)
                .build();
        ProcessServiceClient client = clientServices.processServiceClientFactory().newClient(config, credentials);
        Set<String> phases = client.getAvailablePhases(BASIC_AUTH_PROCESS_ID,
                                                       PROCESS_INSTANCE_ID,
                                                       TASK_ID,
                                                       WORKITEM_ID,
                                                       USER,
                                                       Arrays.asList(GROUP1, GROUP2));

        assertThat(phases).containsExactlyInAnyOrder(PHASE2, PHASE1);
    }

    @Test
    void getAvailablePhasesWithKeyCloakAuthentication() {
        ProcessServiceClientConfig config = createServiceConfig();
        String keyCloakServerUrl = System.getProperty(KEY_CLOAK_SERVICE_URL);
        KeycloakAuthenticationCredentials credentials = KeycloakAuthenticationCredentials.newBuilder()
                .serverUrl(keyCloakServerUrl)
                .realm(REALM)
                .username(KEYCLOAK_USER)
                .password(KEYCLOAK_PASSWORD)
                .clientId(CLIENT_ID)
                .clientSecret(SECRET)
                .build();
        ProcessServiceClient client = clientServices.processServiceClientFactory().newClient(config, credentials);
        Set<String> phases = client.getAvailablePhases(KEYCLOAK_AUTH_PROCESS_ID,
                                                       PROCESS_INSTANCE_ID,
                                                       TASK_ID,
                                                       WORKITEM_ID,
                                                       USER,
                                                       Arrays.asList(GROUP1, GROUP2));

        assertThat(phases).containsExactlyInAnyOrder(PHASE2, PHASE1);
    }

    @Test
    void transitionTask() {
        ProcessServiceClientConfig config = createServiceConfig();
        ProcessServiceClient client = clientServices.processServiceClientFactory().newClient(config, NoAuthenticationCredentials.INSTANCE);
        client.transitionTask(PROCESS_ID,
                              PROCESS_INSTANCE_ID,
                              TASK_ID,
                              WORKITEM_ID,
                              PHASE1,
                              USER,
                              Arrays.asList(GROUP1, GROUP2));
    }

    private ProcessServiceClientConfig createServiceConfig() {
        String serviceUrl = System.getProperty(PROCESS_SERVICE_URL);
        return ProcessServiceClientConfig.newBuilder()
                .serviceUrl(serviceUrl)
                .build();
    }
}
