/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.trusty.service.common.responses;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.kie.kogito.trusty.service.common.responses.decision.DecisionHeaderResponse;
import org.kie.kogito.trusty.service.common.responses.process.ProcessHeaderResponse;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;

public class ResponseUtils {

    private ResponseUtils() {
    }

    public static ExecutionHeaderResponse executionHeaderResponseFrom(Execution execution) {
        OffsetDateTime ldt = OffsetDateTime.ofInstant((Instant.ofEpochMilli(execution.getExecutionTimestamp())),
                ZoneOffset.UTC);
        switch (execution.getExecutionType()) {
            case DECISION:
                return getDecisionHeaderResponse(ldt, (Decision) execution);
            case PROCESS:
                return getProcessHeaderResponse(ldt, (Decision) execution);
            default:
                throw new IllegalArgumentException("Unmanaged ExecutionType " + execution.getExecutionType());
        }
    }

    private static ExecutionHeaderResponse getDecisionHeaderResponse(OffsetDateTime ldt, Decision execution) {
        return new DecisionHeaderResponse(execution.getExecutionId(),
                ldt,
                execution.hasSucceeded(),
                execution.getExecutorName(),
                execution.getExecutedModelName(),
                execution.getExecutedModelNamespace());
    }

    private static ExecutionHeaderResponse getProcessHeaderResponse(OffsetDateTime ldt, Decision execution) {
        return new ProcessHeaderResponse(execution.getExecutionId(),
                ldt,
                execution.hasSucceeded(),
                execution.getExecutorName(),
                execution.getExecutedModelName(),
                execution.getExecutedModelNamespace());
    }

}
