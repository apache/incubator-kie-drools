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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.kie.kogito.tracing.typedvalue.TypedValue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CounterfactualExplainabilityResultDto extends BaseExplainabilityResultDto {

    public static final String EXPLAINABILITY_TYPE_NAME = "counterfactual";

    public static final String COUNTERFACTUAL_ID_FIELD = "counterfactualId";

    public static final String SOLUTION_ID_FIELD = "solutionId";

    public static final String IS_VALID_FIELD = "isValid";

    public static final String STAGE_FIELD = "stage";

    public static final String INPUTS_FIELD = "inputs";

    public static final String OUTPUTS_FIELD = "outputs";

    public enum Stage {
        INTERMEDIATE,
        FINAL
    }

    @JsonProperty(COUNTERFACTUAL_ID_FIELD)
    @NotNull(message = "counterfactualId must be provided.")
    private String counterfactualId;

    @JsonProperty(SOLUTION_ID_FIELD)
    @NotNull(message = "solutionId must be provided.")
    private String solutionId;

    @JsonProperty(IS_VALID_FIELD)
    @NotNull(message = "isValid must be provided.")
    private Boolean isValid;

    @JsonProperty(STAGE_FIELD)
    @NotNull(message = "stage must be provided.")
    private CounterfactualExplainabilityResultDto.Stage stage;

    @JsonProperty(INPUTS_FIELD)
    @NotNull(message = "inputs object must be provided.")
    private Map<String, TypedValue> inputs;

    @JsonProperty(OUTPUTS_FIELD)
    @NotNull(message = "outputs object must be provided.")
    private Map<String, TypedValue> outputs;

    private CounterfactualExplainabilityResultDto() {
        super();
    }

    private CounterfactualExplainabilityResultDto(@NotNull String executionId,
            @NotNull String counterfactualId,
            @NotNull String solutionId,
            @NotNull ExplainabilityStatus status,
            String statusDetails,
            @NotNull Boolean isValid,
            @NotNull CounterfactualExplainabilityResultDto.Stage stage,
            @NotNull Map<String, TypedValue> inputs,
            @NotNull Map<String, TypedValue> outputs) {
        super(executionId, status, statusDetails);
        this.counterfactualId = Objects.requireNonNull(counterfactualId);
        this.solutionId = Objects.requireNonNull(solutionId);
        this.isValid = Objects.requireNonNull(isValid);
        this.stage = Objects.requireNonNull(stage);
        this.inputs = Objects.requireNonNull(inputs);
        this.outputs = Objects.requireNonNull(outputs);
    }

    public static CounterfactualExplainabilityResultDto buildSucceeded(String executionId,
            String counterfactualId,
            String solutionId,
            Boolean isValid,
            CounterfactualExplainabilityResultDto.Stage stage,
            Map<String, TypedValue> inputs,
            Map<String, TypedValue> outputs) {
        return new CounterfactualExplainabilityResultDto(executionId,
                counterfactualId,
                solutionId,
                ExplainabilityStatus.SUCCEEDED,
                null,
                isValid,
                stage,
                inputs,
                outputs);
    }

    public static CounterfactualExplainabilityResultDto buildFailed(String executionId,
            String counterfactualId,
            String statusDetails) {
        return new CounterfactualExplainabilityResultDto(executionId,
                counterfactualId,
                UUID.randomUUID().toString(),
                ExplainabilityStatus.FAILED,
                statusDetails,
                Boolean.FALSE,
                Stage.FINAL,
                Collections.emptyMap(),
                Collections.emptyMap());
    }

    public String getCounterfactualId() {
        return counterfactualId;
    }

    public String getSolutionId() {
        return solutionId;
    }

    public Boolean isValid() {
        return isValid;
    }

    public Stage getStage() {
        return stage;
    }

    public Map<String, TypedValue> getInputs() {
        return inputs;
    }

    public Map<String, TypedValue> getOutputs() {
        return outputs;
    }
}
