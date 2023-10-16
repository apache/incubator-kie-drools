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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualExplainabilityResult extends BaseExplainabilityResult {

    public static final String EXPLAINABILITY_TYPE_NAME = "counterfactual";

    public static final String COUNTERFACTUAL_ID_FIELD = "counterfactualId";

    public static final String COUNTERFACTUAL_SOLUTION_ID_FIELD = "solutionId";

    public static final String COUNTERFACTUAL_SEQUENCE_ID_FIELD = "sequenceId";

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

    @JsonProperty(COUNTERFACTUAL_SEQUENCE_ID_FIELD)
    @NotNull(message = "sequenceId must be provided.")
    private Long sequenceId;

    @JsonProperty(IS_VALID_FIELD)
    @NotNull(message = "isValid object must be provided.")
    private Boolean isValid;

    @JsonProperty(STAGE_FIELD)
    @NotNull(message = "stage object must be provided.")
    private Stage stage;

    @JsonProperty(INPUTS_FIELD)
    @NotNull(message = "inputs object must be provided.")
    private Collection<NamedTypedValue> inputs;

    @JsonProperty(OUTPUTS_FIELD)
    @NotNull(message = "outputs object must be provided.")
    private Collection<NamedTypedValue> outputs;

    public CounterfactualExplainabilityResult() {
    }

    public CounterfactualExplainabilityResult(@NotNull String executionId,
            @NotNull String counterfactualId,
            @NotNull String solutionId,
            @NotNull Long sequenceId,
            @NotNull ExplainabilityStatus status,
            String statusDetails,
            @NotNull Boolean isValid,
            @NotNull Stage stage,
            @NotNull Collection<NamedTypedValue> inputs,
            @NotNull Collection<NamedTypedValue> outputs) {
        super(executionId, status, statusDetails);
        this.counterfactualId = Objects.requireNonNull(counterfactualId);
        this.solutionId = Objects.requireNonNull(solutionId);
        this.sequenceId = Objects.requireNonNull(sequenceId);
        this.isValid = Objects.requireNonNull(isValid);
        this.stage = Objects.requireNonNull(stage);
        this.inputs = Objects.requireNonNull(inputs);
        this.outputs = Objects.requireNonNull(outputs);
    }

    public static CounterfactualExplainabilityResult buildSucceeded(String executionId,
            String counterfactualId,
            String solutionId,
            Long sequenceId,
            Boolean isValid,
            CounterfactualExplainabilityResult.Stage stage,
            Collection<NamedTypedValue> inputs,
            Collection<NamedTypedValue> outputs) {
        return new CounterfactualExplainabilityResult(executionId,
                counterfactualId,
                solutionId,
                sequenceId,
                ExplainabilityStatus.SUCCEEDED,
                null,
                isValid,
                stage,
                inputs,
                outputs);
    }

    public static CounterfactualExplainabilityResult buildFailed(String executionId,
            String counterfactualId,
            String statusDetails) {
        return new CounterfactualExplainabilityResult(executionId,
                counterfactualId,
                UUID.randomUUID().toString(),
                Long.MAX_VALUE,
                ExplainabilityStatus.FAILED,
                statusDetails,
                Boolean.FALSE,
                Stage.FINAL,
                Collections.emptyList(),
                Collections.emptyList());
    }

    public String getCounterfactualId() {
        return counterfactualId;
    }

    public String getSolutionId() {
        return solutionId;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public Boolean isValid() {
        return isValid;
    }

    public Stage getStage() {
        return stage;
    }

    public Collection<NamedTypedValue> getInputs() {
        return inputs;
    }

    public Collection<NamedTypedValue> getOutputs() {
        return outputs;
    }

}
