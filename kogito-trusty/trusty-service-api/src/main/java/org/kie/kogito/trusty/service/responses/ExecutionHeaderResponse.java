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

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An execution header.
 */
public class ExecutionHeaderResponse {

    @JsonProperty("executionId")
    private String executionId;

    @JsonProperty("executionDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private OffsetDateTime executionDate;

    @JsonProperty("executionSucceeded")
    private Boolean hasSucceeded;

    @JsonProperty("executorName")
    private String executorName;

    @JsonProperty("executedModelName")
    private String executedModelName;

    @JsonProperty("executedModelNamespace")
    private String executedModelNamespace;

    @JsonProperty("executionType")
    private ExecutionType executionType;

    private ExecutionHeaderResponse() {
    }

    public ExecutionHeaderResponse(String executionId,
                                   OffsetDateTime executionDate,
                                   Boolean hasSucceeded,
                                   String executorName,
                                   String executedModelName,
                                   String executedModelNamespace,
                                   ExecutionType executionType) {
        this.executionId = executionId;
        this.executionDate = executionDate;
        this.hasSucceeded = hasSucceeded;
        this.executorName = executorName;
        this.executedModelName = executedModelName;
        this.executedModelNamespace = executedModelNamespace;
        this.executionType = executionType;
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
    public Boolean hasSucceeded() {
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
     * Gets the namespace of the executed model.
     *
     * @return The namespace of the executed model.
     */
    public String getExecutedModelNamespace() {
        return executedModelNamespace;
    }

    /**
     * Gets the execution type.
     *
     * @return The execution type.
     */
    public ExecutionType getExecutionType() {
        return executionType;
    }
}