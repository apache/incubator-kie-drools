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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.CLIENT_AUTH_PASSWORD;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.CLIENT_AUTH_USER;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_INDEX_CONNECT_TIMEOUT_DURATION;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_INDEX_READ_TIMEOUT_DURATION;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_INDEX_SERVER_URL;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_LOADER_PAGE_SIZE;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.IMPROVE_SOLUTION_ON_BACKGROUND_DURATION;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.OIDC_CLIENT;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.PROCESS_RUNTIME_CONNECT_TIMEOUT_DURATION;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.PROCESS_RUNTIME_READ_TIMEOUT_DURATION;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.PUBLISH_WINDOW_SIZE;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.USER_SERVICE_CONNECTOR;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.USER_SERVICE_SYNC_INTERVAL;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.WAIT_FOR_IMPROVED_SOLUTION_DURATION;

@ApplicationScoped
public class TaskAssigningConfig {

    public static final String DEFAULT_USER_SERVICE_CONNECTOR = "PropertiesConnector";

    @Inject
    @ConfigProperty(name = OIDC_CLIENT)
    Optional<String> oidcClient;

    @Inject
    @ConfigProperty(name = CLIENT_AUTH_USER)
    Optional<String> clientAuthUser;

    @Inject
    @ConfigProperty(name = CLIENT_AUTH_PASSWORD)
    Optional<String> clientAuthPassword;

    @Inject
    @ConfigProperty(name = DATA_INDEX_SERVER_URL)
    URL dataIndexServerUrl;

    @Inject
    @ConfigProperty(name = DATA_INDEX_CONNECT_TIMEOUT_DURATION, defaultValue = "PT30S")
    Duration dataIndexConnectTimeoutDuration;

    @Inject
    @ConfigProperty(name = DATA_INDEX_READ_TIMEOUT_DURATION, defaultValue = "PT3M")
    Duration dataIndexReadTimeoutDuration;

    @Inject
    @ConfigProperty(name = DATA_LOADER_PAGE_SIZE, defaultValue = "3000")
    int dataLoaderPageSize;

    @Inject
    @ConfigProperty(name = PUBLISH_WINDOW_SIZE, defaultValue = "2")
    int publishWindowSize;

    @Inject
    @ConfigProperty(name = USER_SERVICE_CONNECTOR, defaultValue = DEFAULT_USER_SERVICE_CONNECTOR)
    String userServiceConnector;

    @Inject
    @ConfigProperty(name = USER_SERVICE_SYNC_INTERVAL, defaultValue = "PT2H")
    Duration userServiceSyncInterval;

    @Inject
    @ConfigProperty(name = WAIT_FOR_IMPROVED_SOLUTION_DURATION, defaultValue = "PT0S")
    Duration waitForImprovedSolutionDuration;

    @Inject
    @ConfigProperty(name = IMPROVE_SOLUTION_ON_BACKGROUND_DURATION, defaultValue = "PT1M")
    Duration improveSolutionOnBackgroundDuration;

    @Inject
    @ConfigProperty(name = PROCESS_RUNTIME_CONNECT_TIMEOUT_DURATION, defaultValue = "PT30S")
    Duration processRuntimeConnectTimeoutDuration;

    @Inject
    @ConfigProperty(name = PROCESS_RUNTIME_READ_TIMEOUT_DURATION, defaultValue = "PT1M")
    Duration processRuntimeReadTimeoutDuration;

    public Optional<String> getOidcClient() {
        return oidcClient;
    }

    public Optional<String> getClientAuthUser() {
        return clientAuthUser;
    }

    public Optional<String> getClientAuthPassword() {
        return clientAuthPassword;
    }

    public URL getDataIndexServerUrl() {
        return dataIndexServerUrl;
    }

    public Duration getDataIndexConnectTimeoutDuration() {
        return dataIndexConnectTimeoutDuration;
    }

    public Duration getDataIndexReadTimeoutDuration() {
        return dataIndexReadTimeoutDuration;
    }

    public boolean isOidcClientSet() {
        return oidcClient.isPresent();
    }

    public boolean isBasicAuthSet() {
        return !isOidcClientSet() && clientAuthUser.isPresent();
    }

    public int getDataLoaderPageSize() {
        return dataLoaderPageSize;
    }

    public int getPublishWindowSize() {
        return publishWindowSize;
    }

    public String getUserServiceConnector() {
        return userServiceConnector;
    }

    public Duration getUserServiceSyncInterval() {
        return userServiceSyncInterval;
    }

    public Duration getWaitForImprovedSolutionDuration() {
        return waitForImprovedSolutionDuration;
    }

    public Duration getImproveSolutionOnBackgroundDuration() {
        return improveSolutionOnBackgroundDuration;
    }

    public Duration getProcessRuntimeConnectTimeoutDuration() {
        return processRuntimeConnectTimeoutDuration;
    }

    public Duration getProcessRuntimeReadTimeoutDuration() {
        return processRuntimeReadTimeoutDuration;
    }

    @Override
    public String toString() {
        return "TaskAssigningConfig{" +
                "oidcClient=" + oidcClient +
                ", clientAuthUser=" + clientAuthUser +
                ", clientAuthPassword=" + (clientAuthPassword.isEmpty() ? null : "*****") +
                ", dataIndexServerUrl=" + dataIndexServerUrl +
                ", dataIndexConnectTimeoutDuration=" + dataIndexConnectTimeoutDuration +
                ", dataIndexReadTimeoutDuration=" + dataIndexReadTimeoutDuration +
                ", dataLoaderPageSize=" + dataLoaderPageSize +
                ", publishWindowSize=" + publishWindowSize +
                ", userServiceConnector=" + userServiceConnector +
                ", userServiceSyncInterval=" + userServiceSyncInterval +
                ", waitForImprovedSolutionDuration=" + waitForImprovedSolutionDuration +
                ", improveSolutionOnBackgroundDuration= " + improveSolutionOnBackgroundDuration +
                ", processRuntimeConnectTimeoutDuration=" + processRuntimeConnectTimeoutDuration +
                ", processRuntimeReadTimeoutDuration=" + processRuntimeReadTimeoutDuration +
                '}';
    }
}
