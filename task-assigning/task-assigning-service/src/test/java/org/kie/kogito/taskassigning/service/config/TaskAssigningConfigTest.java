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
import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskAssigningConfigTest {

    private static final String DATA_INDEX_SERVER_URL = "http://localhost:8180/graphql";
    private static final String OIDC_CLIENT = "OIDC_CLIENT";
    private static final String CLIENT_AUTH_USER = "CLIENT_AUTH_USER";
    private static final String CLIENT_AUTH_PASSWORD = "CLIENT_AUTH_PASSWORD";
    private static final int DATA_LOADER_PAGE_SIZE = 10;
    private static final int PUBLISH_WINDOW_SIZE = 3;
    private static final String USER_SERVICE_CONNECTOR = "USER_SERVICE_CONNECTOR";
    private static final Duration USER_SERVICE_SYNC_INTERVAL = Duration.ofMillis(2000);
    private static final Duration WAIT_FOR_IMPROVED_SOLUTION_DURATION = Duration.ofMillis(3000);
    private static final Duration IMPROVE_SOLUTION_ON_BACKGROUND_DURATION = Duration.ofMillis(4000);
    private static final Duration TIMEOUT_DURATION = Duration.ofMillis(5000);

    private TaskAssigningConfig config;

    @BeforeEach
    void setUp() {
        config = new TaskAssigningConfig();
    }

    @Test
    void isOidcClientSetTrue() {
        config.oidcClient = Optional.of(OIDC_CLIENT);
        assertThat(config.isOidcClientSet()).isTrue();
    }

    @Test
    void isOidcClientSetFalse() {
        config.oidcClient = Optional.empty();
        assertThat(config.isOidcClientSet()).isFalse();
    }

    @Test
    void getOidcClient() {
        config.oidcClient = Optional.of(OIDC_CLIENT);
        assertThat(config.getOidcClient()).hasValue(OIDC_CLIENT);
    }

    @Test
    void isBasicAuthSetTrue() {
        config.oidcClient = Optional.empty();
        config.clientAuthUser = Optional.of(CLIENT_AUTH_USER);
        assertThat(config.isBasicAuthSet()).isTrue();
    }

    @Test
    void isBasicAuthSetFalseOidcClientSet() {
        config.oidcClient = Optional.of(OIDC_CLIENT);
        config.clientAuthUser = Optional.of(CLIENT_AUTH_USER);
        assertThat(config.isBasicAuthSet()).isFalse();
    }

    @Test
    void isBasicAuthSetFalse() {
        config.oidcClient = Optional.empty();
        config.clientAuthUser = Optional.empty();
        assertThat(config.isBasicAuthSet()).isFalse();
    }

    @Test
    void getClientAuthUser() {
        config.clientAuthUser = Optional.of(CLIENT_AUTH_USER);
        assertThat(config.getClientAuthUser()).contains(CLIENT_AUTH_USER);
    }

    @Test
    void getClientAuthPassword() {
        config.clientAuthPassword = Optional.of(CLIENT_AUTH_PASSWORD);
        assertThat(config.getClientAuthPassword()).contains(CLIENT_AUTH_PASSWORD);
    }

    @Test
    void getDataIndexServerUrl() throws Exception {
        URL url = new URL(DATA_INDEX_SERVER_URL);
        config.dataIndexServerUrl = url;
        assertThat(config.getDataIndexServerUrl()).isEqualTo(url);
    }

    @Test
    void getDataLoaderPageSize() {
        config.dataLoaderPageSize = DATA_LOADER_PAGE_SIZE;
        assertThat(config.getDataLoaderPageSize()).isEqualTo(DATA_LOADER_PAGE_SIZE);
    }

    @Test
    void getPublishWindowSize() {
        config.publishWindowSize = PUBLISH_WINDOW_SIZE;
        assertThat(config.getPublishWindowSize()).isEqualTo(PUBLISH_WINDOW_SIZE);
    }

    @Test
    void getUserServiceConnector() {
        config.userServiceConnector = USER_SERVICE_CONNECTOR;
        assertThat(config.getUserServiceConnector()).isEqualTo(USER_SERVICE_CONNECTOR);
    }

    @Test
    void getUserServiceSyncInterval() {
        config.userServiceSyncInterval = USER_SERVICE_SYNC_INTERVAL;
        assertThat(config.getUserServiceSyncInterval()).isEqualTo(USER_SERVICE_SYNC_INTERVAL);
    }

    @Test
    void getWaitForImprovedSolutionDuration() {
        config.waitForImprovedSolutionDuration = WAIT_FOR_IMPROVED_SOLUTION_DURATION;
        assertThat(config.getWaitForImprovedSolutionDuration()).isEqualTo(WAIT_FOR_IMPROVED_SOLUTION_DURATION);
    }

    @Test
    void getImproveSolutionOnBackgroundDuration() {
        config.improveSolutionOnBackgroundDuration = IMPROVE_SOLUTION_ON_BACKGROUND_DURATION;
        assertThat(config.getImproveSolutionOnBackgroundDuration()).isEqualTo(IMPROVE_SOLUTION_ON_BACKGROUND_DURATION);
    }

    @Test
    void getDataIndexConnectTimeoutDuration() {
        config.dataIndexConnectTimeoutDuration = TIMEOUT_DURATION;
        assertThat(config.getDataIndexConnectTimeoutDuration()).isEqualTo(TIMEOUT_DURATION);
    }

    @Test
    void getDataIndexReadTimeoutDuration() {
        config.dataIndexReadTimeoutDuration = TIMEOUT_DURATION;
        assertThat(config.getDataIndexReadTimeoutDuration()).isEqualTo(TIMEOUT_DURATION);
    }

    @Test
    void getProcessRuntimeConnectTimeoutDuration() {
        config.processRuntimeConnectTimeoutDuration = TIMEOUT_DURATION;
        assertThat(config.getProcessRuntimeConnectTimeoutDuration()).isEqualTo(TIMEOUT_DURATION);
    }

    @Test
    void getProcessRuntimeReadTimeoutDuration() {
        config.processRuntimeReadTimeoutDuration = TIMEOUT_DURATION;
        assertThat(config.getProcessRuntimeReadTimeoutDuration()).isEqualTo(TIMEOUT_DURATION);
    }
}
