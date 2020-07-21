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

package org.kie.kogito.trusty.service.responses;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.kie.kogito.trusty.storage.api.model.ExecutionTypeEnum;

/**
 * An execution header.
 */
public class ExecutionHeaderResponse {

    @JsonProperty("executionId")
    private String executionId;

    @JsonProperty("executionDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private OffsetDateTime executionDate;

    @JsonProperty("hasSucceeded")
    private boolean hasSucceeded;

    @JsonProperty("executorName")
    private String executorName;

    @JsonProperty("executedModelName")
    private String executedModelName;

    @JsonProperty("executionType")
    private ExecutionTypeEnum executionType;

    private ExecutionHeaderResponse() {
    }

    public ExecutionHeaderResponse(String executionId, OffsetDateTime executionDate, boolean hasSucceeded, String executorName, String executedModelName, ExecutionTypeEnum executionType) {
        this.executionId = executionId;
        this.executionDate = executionDate;
        this.hasSucceeded = hasSucceeded;
        this.executorName = executorName;
        this.executedModelName = executedModelName;
        this.executionType = executionType;
    }

    public static ExecutionHeaderResponse fromExecution(Execution execution) {
        OffsetDateTime ldt = OffsetDateTime.ofInstant((Instant.ofEpochMilli(execution.getExecutionTimestamp())), ZoneOffset.UTC);
        return new ExecutionHeaderResponse(execution.getExecutionId(), ldt, execution.hasSucceeded(), execution.getExecutorName(), execution.getExecutedModelName(), execution.getExecutionType());
    }

    /**
     * Gets the execution id.
     *
     * @return The execution id.
     */
    public String getExecutionId() {
        return executionId;
    }

    /**
     * Gets the execution date.
     *
     * @return The execution date.
     */
    public OffsetDateTime getExecutionDate() {
        return executionDate;
    }

    /**
     * Gets the information of the operational success of the execution.
     *
     * @return true if the execution was successful from an technical point of view, false otherwise.
     */
    public boolean hasSucceeded() {
        return hasSucceeded;
    }

    /**
     * Gets the executor name.
     *
     * @return The executor name.
     */
    public String getExecutorName() {
        return executorName;
    }

    /**
     * Gets the name of the executed model.
     *
     * @return The name of the executed model.
     */
    public String getExecutedModelName() {
        return executedModelName;
    }

    /**
     * Gets the execution type.
     *
     * @return The execution type.
     */
    public ExecutionTypeEnum getExecutionType() {
        return executionType;
    }
}