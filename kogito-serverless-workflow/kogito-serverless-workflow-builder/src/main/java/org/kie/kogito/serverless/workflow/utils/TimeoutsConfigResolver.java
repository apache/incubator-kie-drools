/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serverless.workflow.utils;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.timeouts.TimeoutsDefinition;

public class TimeoutsConfigResolver {

    private static final String NON_NEGATIVE_DURATION_MUST_BE_PROVIDED = "When configured, it must be set with a greater than zero ISO 8601 time duration. For example PT30S.";

    private static final String INVALID_EVENT_TIMEOUT_FOR_STATE_ERROR = "An invalid \"eventTimeout\": \"%s\" configuration was provided for the state \"%s\" in the serverless workflow: \"%s\". " +
            NON_NEGATIVE_DURATION_MUST_BE_PROVIDED;

    private static final String INVALID_EVENT_TIMEOUT_FOR_WORKFLOW_ERROR = "An invalid \"eventTimeout\": \"%s\" configuration was provided for the serverless workflow: \"%s\". " +
            NON_NEGATIVE_DURATION_MUST_BE_PROVIDED;

    private TimeoutsConfigResolver() {
    }

    public static String resolveEventTimeout(State state, Workflow workflow) {
        TimeoutsDefinition timeouts = state.getTimeouts();
        if (timeouts != null && timeouts.getEventTimeout() != null) {
            validateDuration(timeouts.getEventTimeout(),
                    String.format(INVALID_EVENT_TIMEOUT_FOR_STATE_ERROR,
                            timeouts.getEventTimeout(),
                            state.getName(),
                            workflow.getName()));
            return timeouts.getEventTimeout();
        } else {
            timeouts = workflow.getTimeouts();
            if (timeouts != null && timeouts.getEventTimeout() != null) {
                validateDuration(timeouts.getEventTimeout(),
                        String.format(INVALID_EVENT_TIMEOUT_FOR_WORKFLOW_ERROR,
                                timeouts.getEventTimeout(),
                                workflow.getName()));
                return timeouts.getEventTimeout();
            }
        }
        return null;
    }

    private static void validateDuration(String value, String message) {
        try {
            Duration.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(message, e);
        }
    }
}