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

import java.net.URL;

import org.kie.kogito.taskassigning.ClientServices;
import org.kie.kogito.taskassigning.auth.AuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.BasicAuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.KeycloakAuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.NoAuthenticationCredentials;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClient;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClientConfig;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClient;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClientConfig;

public class TaskAssigningConfigUtil {

    private TaskAssigningConfigUtil() {
    }

    public static DataIndexServiceClient createDataIndexServiceClient(ClientServices clientServices, TaskAssigningConfig config) {
        TaskAssigningConfigValidator.of(config).validate();
        DataIndexServiceClientConfig clientServiceConfig = DataIndexServiceClientConfig.newBuilder()
                .serviceUrl(config.getDataIndexServerUrl().toString())
                .build();
        AuthenticationCredentials credentials = buildAuthenticationCredentials(config);
        return clientServices.dataIndexClientFactory().newClient(clientServiceConfig, credentials);
    }

    public static ProcessServiceClient createProcessServiceClient(ClientServices clientServices, TaskAssigningConfig config, URL serviceUrl) {
        TaskAssigningConfigValidator.of(config).validate();
        ProcessServiceClientConfig clientServiceConfig = ProcessServiceClientConfig.newBuilder()
                .serviceUrl(serviceUrl.toString())
                .build();
        AuthenticationCredentials credentials = buildAuthenticationCredentials(config);
        return clientServices.processServiceClientFactory().newClient(clientServiceConfig, credentials);
    }

    private static AuthenticationCredentials buildAuthenticationCredentials(TaskAssigningConfig config) {
        AuthenticationCredentials credentials;
        if (config.isKeycloakSet()) {
            KeycloakAuthenticationCredentials.Builder builder = KeycloakAuthenticationCredentials.newBuilder()
                    .serverUrl(config.getOidcAuthServerCanonicUrl().toString())
                    .realm(config.getOidcAuthServerRealm());
            config.getOidcClientId().ifPresent(builder::clientId);
            config.getOidcCredentialsSecret().ifPresent(builder::clientSecret);
            config.getClientAuthUser().ifPresent(builder::username);
            config.getClientAuthPassword().ifPresent(builder::password);
            credentials = builder.build();
        } else if (config.isBasicAuthSet()) {
            BasicAuthenticationCredentials.Builder builder = BasicAuthenticationCredentials.newBuilder();
            config.getClientAuthUser().ifPresent(builder::user);
            config.getClientAuthPassword().ifPresent(builder::password);
            credentials = builder.build();
        } else {
            credentials = NoAuthenticationCredentials.INSTANCE;
        }
        return credentials;
    }
}
