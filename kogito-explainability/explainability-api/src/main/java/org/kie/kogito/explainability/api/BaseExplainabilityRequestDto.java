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

import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.kie.kogito.tracing.typedvalue.TypedValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = BaseExplainabilityRequestDto.EXPLAINABILITY_TYPE_FIELD)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LIMEExplainabilityRequestDto.class, name = LIMEExplainabilityRequestDto.EXPLAINABILITY_TYPE_NAME),
        @JsonSubTypes.Type(value = CounterfactualExplainabilityRequestDto.class, name = CounterfactualExplainabilityRequestDto.EXPLAINABILITY_TYPE_NAME)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseExplainabilityRequestDto {

    public static final String EXPLAINABILITY_TYPE_FIELD = "type";

    @JsonProperty("executionId")
    @NotNull(message = "executionId must be provided.")
    private String executionId;

    @JsonProperty("serviceUrl")
    @NotBlank(message = "serviceUrl is mandatory.")
    private String serviceUrl;

    @JsonProperty("modelIdentifier")
    @NotNull(message = "modelIdentifier object must be provided.")
    @Valid
    private ModelIdentifierDto modelIdentifier;

    @JsonProperty("inputs")
    @NotNull(message = "inputs object must be provided.")
    private Map<String, TypedValue> inputs;

    @JsonProperty("outputs")
    @NotNull(message = "outputs object must be provided.")
    private Map<String, TypedValue> outputs;

    protected BaseExplainabilityRequestDto() {
    }

    public BaseExplainabilityRequestDto(@NotNull String executionId,
            @NotBlank String serviceUrl,
            @NotNull ModelIdentifierDto modelIdentifier,
            @NotNull Map<String, TypedValue> inputs,
            @NotNull Map<String, TypedValue> outputs) {
        this.executionId = Objects.requireNonNull(executionId);
        this.serviceUrl = Objects.requireNonNull(serviceUrl);
        this.modelIdentifier = Objects.requireNonNull(modelIdentifier);
        this.inputs = Objects.requireNonNull(inputs);
        this.outputs = Objects.requireNonNull(outputs);
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public ModelIdentifierDto getModelIdentifier() {
        return modelIdentifier;
    }

    public Map<String, TypedValue> getInputs() {
        return inputs;
    }

    public Map<String, TypedValue> getOutputs() {
        return outputs;
    }
}
