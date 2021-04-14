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
package org.kie.kogito.explainability.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = BaseExplainabilityResultDto.EXPLAINABILITY_TYPE_FIELD)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LIMEExplainabilityResultDto.class, name = LIMEExplainabilityResultDto.EXPLAINABILITY_TYPE_NAME),
        @JsonSubTypes.Type(value = CounterfactualExplainabilityResultDto.class, name = CounterfactualExplainabilityResultDto.EXPLAINABILITY_TYPE_NAME)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseExplainabilityResultDto {

    public static final String EXPLAINABILITY_TYPE_FIELD = "type";

    @JsonProperty("executionId")
    private String executionId;

    @JsonProperty("status")
    private ExplainabilityStatus status;

    @JsonProperty("statusDetails")
    private String statusDetails;

    protected BaseExplainabilityResultDto() {
    }

    protected BaseExplainabilityResultDto(String executionId, ExplainabilityStatus status, String statusDetails) {
        this.executionId = executionId;
        this.status = status;
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
