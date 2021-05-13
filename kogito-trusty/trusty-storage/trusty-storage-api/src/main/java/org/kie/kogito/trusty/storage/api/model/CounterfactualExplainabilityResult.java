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

package org.kie.kogito.trusty.storage.api.model;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualExplainabilityResult extends BaseExplainabilityResult {

    public static final String EXPLAINABILITY_TYPE_NAME = "counterfactual";

    public static final String COUNTERFACTUAL_ID_FIELD = "counterfactualId";

    public static final String COUNTERFACTUAL_SOLUTION_ID_FIELD = "solutionId";

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

    @JsonProperty(COUNTERFACTUAL_SOLUTION_ID_FIELD)
    @NotNull(message = "solutionId must be provided.")
    private String solutionId;

    @JsonProperty(IS_VALID_FIELD)
    @NotNull(message = "isValid object must be provided.")
    private Boolean isValid;

    @JsonProperty(STAGE_FIELD)
    @NotNull(message = "stage object must be provided.")
    private Stage stage;

    @JsonProperty(INPUTS_FIELD)
    @NotNull(message = "inputs object must be provided.")
    private Collection<TypedVariableWithValue> inputs;

    @JsonProperty(OUTPUTS_FIELD)
    @NotNull(message = "outputs object must be provided.")
    private Collection<TypedVariableWithValue> outputs;

    public CounterfactualExplainabilityResult() {
    }

    public CounterfactualExplainabilityResult(@NotNull String executionId,
            @NotNull String counterfactualId,
            @NotNull String solutionId,
            @NotNull ExplainabilityStatus status,
            String statusDetails,
            @NotNull Boolean isValid,
            @NotNull Stage stage,
            @NotNull Collection<TypedVariableWithValue> inputs,
            @NotNull Collection<TypedVariableWithValue> outputs) {
        super(executionId, status, statusDetails);
        this.counterfactualId = counterfactualId;
        this.solutionId = solutionId;
        this.isValid = isValid;
        this.stage = stage;
        this.inputs = inputs;
        this.outputs = outputs;
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

    public Collection<TypedVariableWithValue> getInputs() {
        return inputs;
    }

    public Collection<TypedVariableWithValue> getOutputs() {
        return outputs;
    }

    //-------------
    // Test methods
    //-------------

    public void setCounterfactualId(String counterfactualId) {
        this.counterfactualId = counterfactualId;
    }

    public void setSolutionId(String solutionId) {
        this.solutionId = solutionId;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInputs(Collection<TypedVariableWithValue> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(Collection<TypedVariableWithValue> outputs) {
        this.outputs = outputs;
    }
}
