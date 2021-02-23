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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskAssigningConfigValidatorTest {

    private static final String DATA_INDEX_SERVER_URL = "http://localhost:8180/graphql";
    private static final String AUTH_SERVER_URL = "http://localhost:8280/auth/realms/kogito";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CREDENTIALS_SECRET = "CREDENTIALS_SECRET";
    private static final String CLIENT_USER = "CLIENT_USER";
    private static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";

    @Test
    void validateDataIndexUrlNotSet() {
        TaskAssigningConfig config = new TaskAssigningConfig();
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.DATA_INDEX_SERVER_URL);
    }

    @Test
    void validateKeycloakSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidKeycloakSet();
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateKeycloakAuthServerIsNotSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidKeycloakSet();
        config.oidcAuthServerUrl = Optional.empty();
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.QUARKUS_OIDC_AUTH_SERVER_URL);
    }

    @Test
    void validateKeycloakClientIdIsNotSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidKeycloakSet();
        config.oidcClientId = Optional.empty();
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.QUARKUS_OIDC_CLIENT_ID);
    }

    @Test
    void validateKeycloakCredentialsSecretIsNotSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidKeycloakSet();
        config.oidcCredentialsSecret = Optional.empty();
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.QUARKUS_OIDC_CREDENTIALS_SECRET);
    }

    @Test
    void validateKeycloakAuthUserIsNotSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidKeycloakSet();
        config.clientAuthUser = Optional.empty();
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.CLIENT_AUTH_USER);
    }

    @Test
    void validateKeycloakAuthPasswordIsNotSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidKeycloakSet();
        config.clientAuthPassword = Optional.empty();
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.CLIENT_AUTH_PASSWORD);
    }

    @Test
    void validateBasicAuthSet() throws MalformedURLException {
        TaskAssigningConfig config = new TaskAssigningConfig();
        config.oidcTenantEnabled = false;
        config.dataIndexServerUrl = new URL(DATA_INDEX_SERVER_URL);
        config.clientAuthUser = Optional.of(CLIENT_USER);
        config.clientAuthPassword = Optional.of(CLIENT_PASSWORD);
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    private TaskAssigningConfig createValidKeycloakSet() throws MalformedURLException {
        TaskAssigningConfig config = new TaskAssigningConfig();
        config.oidcTenantEnabled = true;
        config.dataIndexServerUrl = new URL(DATA_INDEX_SERVER_URL);
        config.oidcAuthServerUrl = Optional.of(new URL(AUTH_SERVER_URL));
        config.oidcClientId = Optional.of(CLIENT_ID);
        config.oidcCredentialsSecret = Optional.of(CREDENTIALS_SECRET);
        config.clientAuthUser = Optional.of(CLIENT_USER);
        config.clientAuthPassword = Optional.of(CLIENT_PASSWORD);
        return config;
    }
}
