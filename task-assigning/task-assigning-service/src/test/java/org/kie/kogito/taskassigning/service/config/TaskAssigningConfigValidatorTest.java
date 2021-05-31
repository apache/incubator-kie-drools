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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskAssigningConfigValidatorTest {

    private static final String DATA_INDEX_SERVER_URL = "http://localhost:8180/graphql";
    private static final String OIDC_CLIENT = "OIDC_CLIENT";
    private static final String CLIENT_USER = "CLIENT_USER";
    private static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";
    private static final Duration SYNC_INTERVAL = Duration.ofMillis(1);
    private static final Duration SYNC_INTERVAL_NEGATIVE = Duration.ofMillis(-1);
    private static final Duration INVALID_NEGATIVE_TIMER_DURATION = Duration.parse("-PT0.001S");
    private static final Duration INVALID_NON_NEGATIVE_TIMER_DURATION = Duration.parse("PT0.00001S");
    private static final Duration VALID_TIMER_DURATION = Duration.parse("PT0.001S");
    private static final Duration CONNECT_TIMEOUT_DURATION = Duration.ofMillis(1);
    private static final Duration INVALID_NEGATIVE_CONNECT_TIMEOUT_DURATION = Duration.ofMillis(-1);
    private static final Duration READ_TIMEOUT_DURATION = Duration.ofMillis(2);
    private static final Duration INVALID_NEGATIVE_READ_TIMEOUT_DURATION = Duration.ofMillis(-2);

    @Test
    void validateDataIndexUrlNotSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.dataIndexServerUrl = null;
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.DATA_INDEX_SERVER_URL);
    }

    @Test
    void validateOidcClientSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateOidcClientIsNotSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.oidcClient = Optional.empty();
        assertThat(config.isOidcClientSet()).isFalse();
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateBasicAuthSet() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.oidcClient = Optional.empty();
        assertThat(config.isBasicAuthSet()).isTrue();
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateUserServiceSyncInterval() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.userServiceSyncInterval = SYNC_INTERVAL;
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateUserServiceSyncIntervalInvalidNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.userServiceSyncInterval = SYNC_INTERVAL_NEGATIVE;
        executeInvalidTimerDuration(config, TaskAssigningConfigProperties.USER_SERVICE_SYNC_INTERVAL);
    }

    @Test
    void validateWaitForImprovedSolutionDurationSuccessful() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.waitForImprovedSolutionDuration = VALID_TIMER_DURATION;
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateWaitForImprovedSolutionDurationInvalidNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.waitForImprovedSolutionDuration = INVALID_NEGATIVE_TIMER_DURATION;
        executeInvalidTimerDuration(config, TaskAssigningConfigProperties.WAIT_FOR_IMPROVED_SOLUTION_DURATION);
    }

    @Test
    void validateWaitForImprovedSolutionDurationInvalidNonNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.waitForImprovedSolutionDuration = INVALID_NON_NEGATIVE_TIMER_DURATION;
        executeInvalidTimerDuration(config, TaskAssigningConfigProperties.WAIT_FOR_IMPROVED_SOLUTION_DURATION);
    }

    @Test
    void validateImproveSolutionOnBackgroundDurationSuccessful() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.improveSolutionOnBackgroundDuration = VALID_TIMER_DURATION;
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateImproveSolutionOnBackgroundDurationInvalidNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.improveSolutionOnBackgroundDuration = INVALID_NEGATIVE_TIMER_DURATION;
        executeInvalidTimerDuration(config, TaskAssigningConfigProperties.IMPROVE_SOLUTION_ON_BACKGROUND_DURATION);
    }

    @Test
    void validateImproveSolutionOnBackgroundDurationInvalidNonNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.improveSolutionOnBackgroundDuration = INVALID_NON_NEGATIVE_TIMER_DURATION;
        executeInvalidTimerDuration(config, TaskAssigningConfigProperties.IMPROVE_SOLUTION_ON_BACKGROUND_DURATION);
    }

    @Test
    void validateDataIndexConnectTimeoutDuration() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.dataIndexConnectTimeoutDuration = CONNECT_TIMEOUT_DURATION;
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateDataIndexConnectTimeoutDurationInvalidNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.dataIndexConnectTimeoutDuration = INVALID_NEGATIVE_CONNECT_TIMEOUT_DURATION;
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.DATA_INDEX_CONNECT_TIMEOUT_DURATION);
    }

    @Test
    void validateDataIndexReadTimeoutDuration() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.dataIndexReadTimeoutDuration = READ_TIMEOUT_DURATION;
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateDataIndexReadTimeoutDurationInvalidNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.dataIndexReadTimeoutDuration = INVALID_NEGATIVE_READ_TIMEOUT_DURATION;
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.DATA_INDEX_READ_TIMEOUT_DURATION);
    }

    @Test
    void validateProcessRuntimeConnectTimeoutDuration() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.processRuntimeConnectTimeoutDuration = CONNECT_TIMEOUT_DURATION;
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateProcessRuntimeConnectTimeoutDurationInvalidNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.processRuntimeConnectTimeoutDuration = INVALID_NEGATIVE_CONNECT_TIMEOUT_DURATION;
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.PROCESS_RUNTIME_CONNECT_TIMEOUT_DURATION);
    }

    @Test
    void validateProcessRuntimeReadTimeoutDuration() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.processRuntimeReadTimeoutDuration = READ_TIMEOUT_DURATION;
        Assertions.assertDoesNotThrow(() -> TaskAssigningConfigValidator.of(config).validate());
    }

    @Test
    void validateProcessRuntimeReadTimeoutDurationInvalidNegative() throws MalformedURLException {
        TaskAssigningConfig config = createValidConfig();
        config.processRuntimeReadTimeoutDuration = INVALID_NEGATIVE_READ_TIMEOUT_DURATION;
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(TaskAssigningConfigProperties.PROCESS_RUNTIME_READ_TIMEOUT_DURATION);
    }

    private void executeInvalidTimerDuration(TaskAssigningConfig config, String propertyName) {
        assertThatThrownBy(() -> TaskAssigningConfigValidator.of(config).validate())
                .hasMessageContaining(propertyName);
    }

    private TaskAssigningConfig createValidConfig() throws MalformedURLException {
        TaskAssigningConfig config = new TaskAssigningConfig();
        config.oidcClient = Optional.of(OIDC_CLIENT);
        config.dataIndexServerUrl = new URL(DATA_INDEX_SERVER_URL);
        config.dataIndexConnectTimeoutDuration = CONNECT_TIMEOUT_DURATION;
        config.dataIndexReadTimeoutDuration = READ_TIMEOUT_DURATION;
        config.processRuntimeConnectTimeoutDuration = CONNECT_TIMEOUT_DURATION;
        config.processRuntimeReadTimeoutDuration = READ_TIMEOUT_DURATION;
        config.clientAuthUser = Optional.of(CLIENT_USER);
        config.clientAuthPassword = Optional.of(CLIENT_PASSWORD);
        config.userServiceSyncInterval = Duration.ZERO;
        config.waitForImprovedSolutionDuration = Duration.ZERO;
        config.improveSolutionOnBackgroundDuration = Duration.ZERO;
        return config;
    }
}
