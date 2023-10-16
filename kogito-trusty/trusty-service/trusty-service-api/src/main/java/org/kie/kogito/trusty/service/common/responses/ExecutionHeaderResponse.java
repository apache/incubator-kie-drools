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

import java.time.OffsetDateTime;

import org.kie.kogito.ModelDomain;
import org.kie.kogito.trusty.service.common.responses.decision.DecisionHeaderResponse;
import org.kie.kogito.trusty.service.common.responses.process.ProcessHeaderResponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base abstract class for <b>ExecutionHeaderResponse</b>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "@type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DecisionHeaderResponse.class, name = "DECISION"),
        @JsonSubTypes.Type(value = ProcessHeaderResponse.class, name = "PROCESS")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ExecutionHeaderResponse {

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

    @JsonProperty("executionType")
    private ModelDomain executionType;

    @JsonProperty("@type")
    private ModelDomain modelDomain;

    protected ExecutionHeaderResponse() {
    }

    protected ExecutionHeaderResponse(String executionId,
            OffsetDateTime executionDate,
            Boolean hasSucceeded,
            String executorName,
            String executedModelName,
            ModelDomain modelDomain) {
        this.executionId = executionId;
        this.executionDate = executionDate;
        this.hasSucceeded = hasSucceeded;
        this.executorName = executorName;
        this.executedModelName = executedModelName;
        this.executionType = modelDomain;
        this.modelDomain = modelDomain;
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
     * Gets the execution type.
     *
     * @return The execution type.
     */
    public ModelDomain getExecutionType() {
        return executionType;
    }
}
