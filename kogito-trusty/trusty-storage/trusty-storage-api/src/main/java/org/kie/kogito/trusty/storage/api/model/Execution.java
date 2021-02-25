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

package org.kie.kogito.trusty.storage.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An execution.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Execution {

    public final static String EXECUTION_ID_FIELD = "executionId";
    public final static String SOURCE_URL_FIELD = "sourceUrl";
    public final static String EXECUTION_TIMESTAMP_FIELD = "executionTimestamp";
    public final static String HAS_SUCCEEDED_FIELD = "hasSucceeded";
    public final static String EXECUTOR_NAME_FIELD = "executorName";
    public final static String EXECUTED_MODEL_NAME_FIELD = "executedModelName";
    public final static String EXECUTED_MODEL_NAMESPACE_FIELD = "executedModelNamespace";
    public final static String EXECUTION_TYPE_FIELD = "executionType";

    @JsonProperty(EXECUTION_ID_FIELD)
    private String executionId;

    @JsonProperty(SOURCE_URL_FIELD)
    private String sourceUrl;

    @JsonProperty(EXECUTION_TIMESTAMP_FIELD)
    private Long executionTimestamp;

    @JsonProperty(HAS_SUCCEEDED_FIELD)
    private Boolean hasSucceeded;

    @JsonProperty(EXECUTOR_NAME_FIELD)
    private String executorName;

    @JsonProperty(EXECUTED_MODEL_NAME_FIELD)
    private String executedModelName;

    @JsonProperty(EXECUTED_MODEL_NAMESPACE_FIELD)
    private String executedModelNamespace;

    @JsonProperty(EXECUTION_TYPE_FIELD)
    private ExecutionType executionType;

    public Execution() {
    }

    public Execution(ExecutionType executionType) {
        this.executionType = executionType;
    }

    public Execution(String executionId, String sourceUrl, Long executionTimestamp, Boolean hasSucceeded,
            String executorName, String executedModelName, String executedModelNamespace,
            ExecutionType executionType) {
        this.executionId = executionId;
        this.sourceUrl = sourceUrl;
        this.executionTimestamp = executionTimestamp;
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
     * Sets the execution id.
     *
     * @param executionId The execution Id.
     */
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    /**
     * Gets the source URL of the service where the execution happened.
     *
     * @return The service URL.
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * Sets the source URL of the service where the execution happened.
     *
     * @param sourceUrl The service URL.
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
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
     * Gets the namespace of the executed model.
     *
     * @return The namespace of the executed model.
     */
    public String getExecutedModelNamespace() {
        return executedModelNamespace;
    }

    /**
     * Sets the executed model namespace.
     *
     * @param executedModelNamespace The executed model namespace.
     */
    public void setExecutedModelNamespace(String executedModelNamespace) {
        this.executedModelNamespace = executedModelNamespace;
    }

    /**
     * Gets the execution type.
     *
     * @return The execution type.
     */
    public ExecutionType getExecutionType() {
        return executionType;
    }

    /**
     * Sets the execution type.
     *
     * @param executionType The execution type.
     */
    public void setExecutionType(ExecutionType executionType) {
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