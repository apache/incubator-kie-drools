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
package org.kie.kogito.explainability.api;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = BaseExplainabilityResult.EXPLAINABILITY_TYPE_FIELD)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LIMEExplainabilityResult.class, name = LIMEExplainabilityResult.EXPLAINABILITY_TYPE_NAME),
        @JsonSubTypes.Type(value = CounterfactualExplainabilityResult.class, name = CounterfactualExplainabilityResult.EXPLAINABILITY_TYPE_NAME)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseExplainabilityResult {

    public static final String EXPLAINABILITY_TYPE_FIELD = "type";
    public static final String EXECUTION_ID_FIELD = "executionId";
    public static final String STATUS_FIELD = "status";
    public static final String STATUS_DETAILS_FIELD = "statusDetails";

    @JsonProperty(EXECUTION_ID_FIELD)
    @NotNull(message = "executionId must be provided.")
    private String executionId;

    @JsonProperty(STATUS_FIELD)
    @NotNull(message = "status object must be provided.")
    private ExplainabilityStatus status;

    @JsonProperty(STATUS_DETAILS_FIELD)
    private String statusDetails;

    protected BaseExplainabilityResult() {
    }

    protected BaseExplainabilityResult(@NotNull String executionId,
            @NotNull ExplainabilityStatus status,
            String statusDetails) {
        this.executionId = Objects.requireNonNull(executionId);
        this.status = Objects.requireNonNull(status);
        this.statusDetails = statusDetails;
    }

    public String getExecutionId() {
        return executionId;
    }

    public ExplainabilityStatus getStatus() {
        return status;
    }

    public String getStatusDetails() {
        return statusDetails;
    }
}
