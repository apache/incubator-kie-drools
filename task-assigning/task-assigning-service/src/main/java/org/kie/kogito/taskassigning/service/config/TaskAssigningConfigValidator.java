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

import java.time.Duration;
import java.util.Optional;

import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.CLIENT_AUTH_PASSWORD;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.CLIENT_AUTH_USER;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_INDEX_SERVER_URL;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.QUARKUS_OIDC_AUTH_SERVER_URL;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.QUARKUS_OIDC_CLIENT_ID;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.QUARKUS_OIDC_CREDENTIALS_SECRET;

public class TaskAssigningConfigValidator {

    private static final String PROPERTY_MUST_HAVE_NON_NEGATIVE_VALUE_ERROR = "The config property: %s must be set with a non negative value, but is: %s";

    private static final String PROPERTY_MUST_HAVE_NON_NEGATIVE_ZERO_OR_GREATER_THAN_ONE_MILLISECOND_VALUE_ERROR =
            "The config property: %s must be set with a zero or a greater or equal than one milliseconds duration, but is: %s";

    private TaskAssigningConfig config;

    private TaskAssigningConfigValidator(TaskAssigningConfig config) {
        this.config = config;
    }

    public static TaskAssigningConfigValidator of(TaskAssigningConfig config) {
        return new TaskAssigningConfigValidator(config);
    }

    public void validate() {
        if (config.getDataIndexServerUrl() == null) {
            throw new IllegalArgumentException("A config value must be set for the property: " + DATA_INDEX_SERVER_URL);
        }
        if (config.isKeycloakSet()) {
            validateKeycloakConfig(config);
        }
        if (config.isBasicAuthSet()) {
            validateBasicAuth(config);
        }
        validateUserSyncConfig(config);
        validateWaitForImprovedSolutionDuration(config);
        validateImproveSolutionOnBackgroundDuration(config);
    }

    private static void validateKeycloakConfig(TaskAssigningConfig config) {
        validateOptionalIsSet(QUARKUS_OIDC_AUTH_SERVER_URL, config.getOidcAuthServerUrl());
        validateOptionalIsSet(QUARKUS_OIDC_CLIENT_ID, config.getOidcClientId());
        validateOptionalIsSet(QUARKUS_OIDC_CREDENTIALS_SECRET, config.getOidcCredentialsSecret());
        validateOptionalIsSet(CLIENT_AUTH_USER, config.getClientAuthUser());
        validateOptionalIsSet(CLIENT_AUTH_PASSWORD, config.getClientAuthPassword());
    }

    private static void validateBasicAuth(TaskAssigningConfig config) {
        validateOptionalIsSet(CLIENT_AUTH_USER, config.getClientAuthUser());
    }

    private static void validateOptionalIsSet(String propertyName, Optional<?> value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("A config value must be set for the property: " + propertyName);
        }
    }

    private static void validateUserSyncConfig(TaskAssigningConfig config) {
        if (config.getUserServiceSyncInterval().isNegative()) {
            throw new IllegalArgumentException(String.format(PROPERTY_MUST_HAVE_NON_NEGATIVE_VALUE_ERROR,
                    TaskAssigningConfigProperties.USER_SERVICE_SYNC_INTERVAL,
                    config.getUserServiceSyncInterval()));
        }
    }

    private static void validateWaitForImprovedSolutionDuration(TaskAssigningConfig config) {
        validatePropertyIsZeroOrGreaterThanZeroMillisecondsDuration(TaskAssigningConfigProperties.WAIT_FOR_IMPROVED_SOLUTION_DURATION,
                config.getWaitForImprovedSolutionDuration());
    }

    private static void validateImproveSolutionOnBackgroundDuration(TaskAssigningConfig config) {
        validatePropertyIsZeroOrGreaterThanZeroMillisecondsDuration(TaskAssigningConfigProperties.IMPROVE_SOLUTION_ON_BACKGROUND_DURATION,
                config.getImproveSolutionOnBackgroundDuration());
    }

    private static void validatePropertyIsZeroOrGreaterThanZeroMillisecondsDuration(String propertyName, Duration duration) {
        if (duration.isNegative() || (!duration.isZero() && duration.toMillis() < 1)) {
            throw new IllegalArgumentException(String.format(PROPERTY_MUST_HAVE_NON_NEGATIVE_ZERO_OR_GREATER_THAN_ONE_MILLISECOND_VALUE_ERROR, propertyName, duration));
        }
    }
}
