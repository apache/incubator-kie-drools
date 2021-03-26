/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.service.common.responses;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.kie.kogito.trusty.storage.api.model.Execution;

public class ResponseUtils {

    private ResponseUtils() {
    }

    public static ExecutionHeaderResponse executionHeaderResponseFrom(Execution execution) {
        OffsetDateTime ldt = OffsetDateTime.ofInstant((Instant.ofEpochMilli(execution.getExecutionTimestamp())), ZoneOffset.UTC);
        return new ExecutionHeaderResponse(execution.getExecutionId(),
                ldt,
                execution.hasSucceeded(),
                execution.getExecutorName(),
                execution.getExecutedModelName(),
                execution.getExecutedModelNamespace(),
                executionTypeFrom(execution.getExecutionType()));
    }

    private static ExecutionType executionTypeFrom(org.kie.kogito.trusty.storage.api.model.ExecutionType executionType) {
        switch (executionType) {
            case DECISION:
                return ExecutionType.DECISION;
            case PROCESS:
                return ExecutionType.PROCESS;
        }
        throw new IllegalStateException();
    }
}
