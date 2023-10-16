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
package org.kie.kogito.trusty.storage.api.model;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.kie.kogito.ModelDomain;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.kie.kogito.trusty.storage.api.model.process.Process;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base abstract class for <b>Execution</b>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "@type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Decision.class, name = "DECISION"),
        @JsonSubTypes.Type(value = Process.class, name = "PROCESS")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Execution {

    public static final String EXECUTION_ID_FIELD = "executionId";
    public static final String SOURCE_URL_FIELD = "sourceUrl";
    public static final String SERVICE_URL_FIELD = "serviceUrl";
    public static final String EXECUTION_TIMESTAMP_FIELD = "executionTimestamp";
    public static final String HAS_SUCCEEDED_FIELD = "hasSucceeded";
    public static final String EXECUTOR_NAME_FIELD = "executorName";
    public static final String EXECUTED_MODEL_NAME_FIELD = "executedModelName";
    public static final String EXECUTED_MODEL_NAMESPACE_FIELD = "executedModelNamespace";
    public static final String EXECUTION_TYPE_FIELD = "executionType";

    @JsonProperty(EXECUTION_ID_FIELD)
    @NotNull(message = "executionId must be provided.")
    private String executionId;

    @JsonProperty(SOURCE_URL_FIELD)
    private String sourceUrl;

    @JsonProperty(SERVICE_URL_FIELD)
    private String serviceUrl;

    @JsonProperty(EXECUTION_TIMESTAMP_FIELD)
    private Long executionTimestamp;

    @JsonProperty(HAS_SUCCEEDED_FIELD)
    private Boolean hasSucceeded;

    @JsonProperty(EXECUTOR_NAME_FIELD)
    private String executorName;

    @JsonProperty(EXECUTED_MODEL_NAME_FIELD)
    private String executedModelName;

    @JsonProperty(EXECUTION_TYPE_FIELD)
    private ModelDomain executionType;

    @JsonProperty("@type")
    private ModelDomain modelDomain;

    public Execution() {
    }

    public Execution(ModelDomain modelDomain) {
        this.executionType = modelDomain;
        this.modelDomain = modelDomain;
    }

    public Execution(@NotNull String executionId,
            String sourceUrl,
            String serviceUrl,
            Long executionTimestamp,
            Boolean hasSucceeded,
            String executorName,
            String executedModelName,
            ModelDomain modelDomain) {
        this(modelDomain);
        this.executionId = Objects.requireNonNull(executionId);
        this.sourceUrl = sourceUrl;
        this.serviceUrl = serviceUrl;
        this.executionTimestamp = executionTimestamp;
        this.hasSucceeded = hasSucceeded;
        this.executorName = executorName;
        this.executedModelName = executedModelName;
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
     * Sets the execution id.
     *
     * @param executionId The execution Id.
     */
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    /**
     * Gets the source URL of the Cloud Event where the execution happened.
     *
     * @return The source URL.
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * Sets the source URL of the Cloud Event where the execution happened.
     *
     * @param sourceUrl The source URL.
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     * Gets the source URL of the service where the execution happened.
     *
     * @return The service URL.
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Sets the service URL of the service where the execution happened.
     *
     * @param serviceUrl The service URL.
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * Gets the execution date.
     *
     * @return The execution date.
     */
    public Long getExecutionTimestamp() {
        return executionTimestamp;
    }

    /**
     * Sets the execution timestamp.
     *
     * @param executionTimestamp The execution timestamp.
     */
    public void setExecutionTimestamp(Long executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
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
     * Sets the executor name.
     *
     * @param executorName The executor name.
     */
    public void setExecutorName(String executorName) {
        this.executorName = executorName;
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
     * Sets the executed model name.
     *
     * @param executedModelName The executed model name.
     */
    public void setExecutedModelName(String executedModelName) {
        this.executedModelName = executedModelName;
    }

    /**
     * Gets the execution type.
     *
     * @return The execution type.
     */
    public ModelDomain getExecutionType() {
        return executionType;
    }

    /**
     * Sets the execution type.
     *
     * @param executionType The execution type.
     */
    public void setExecutionType(ModelDomain executionType) {
        this.executionType = executionType;
    }

    /**
     * Sets the success information.
     *
     * @param hasSucceeded Success value.
     */
    public void setSuccess(Boolean hasSucceeded) {
        this.hasSucceeded = hasSucceeded;
    }
}
