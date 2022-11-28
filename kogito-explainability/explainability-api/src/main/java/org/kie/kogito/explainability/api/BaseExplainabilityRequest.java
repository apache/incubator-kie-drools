/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = BaseExplainabilityRequest.EXPLAINABILITY_TYPE_FIELD)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LIMEExplainabilityRequest.class, name = LIMEExplainabilityRequest.EXPLAINABILITY_TYPE_NAME),
        @JsonSubTypes.Type(value = CounterfactualExplainabilityRequest.class, name = CounterfactualExplainabilityRequest.EXPLAINABILITY_TYPE_NAME)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseExplainabilityRequest {

    public static final String EXPLAINABILITY_TYPE_FIELD = "type";
    public static final String EXECUTION_ID_FIELD = "executionId";
    public static final String SERVICE_URL_FIELD = "serviceUrl";
    public static final String MODEL_IDENTIFIER_FIELD = "modelIdentifier";

    @JsonProperty(EXECUTION_ID_FIELD)
    @NotNull(message = "executionId must be provided.")
    private String executionId;

    @JsonProperty(SERVICE_URL_FIELD)
    @NotBlank(message = "serviceUrl is mandatory.")
    private String serviceUrl;

    @JsonProperty(MODEL_IDENTIFIER_FIELD)
    @NotNull(message = "modelIdentifier object must be provided.")
    @Valid
    private ModelIdentifier modelIdentifier;

    protected BaseExplainabilityRequest() {
    }

    protected BaseExplainabilityRequest(@NotNull String executionId,
            @NotBlank String serviceUrl,
            @NotNull ModelIdentifier modelIdentifier) {
        this.executionId = Objects.requireNonNull(executionId);
        this.serviceUrl = Objects.requireNonNull(serviceUrl);
        this.modelIdentifier = Objects.requireNonNull(modelIdentifier);
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public ModelIdentifier getModelIdentifier() {
        return modelIdentifier;
    }

}
