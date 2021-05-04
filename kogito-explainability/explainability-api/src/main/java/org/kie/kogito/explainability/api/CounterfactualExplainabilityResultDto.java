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

import javax.validation.constraints.NotNull;

import org.kie.kogito.tracing.typedvalue.TypedValue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CounterfactualExplainabilityResultDto extends BaseExplainabilityResultDto {

    public static final String EXPLAINABILITY_TYPE_NAME = "counterfactual";

    public static final String COUNTERFACTUAL_ID_FIELD = "counterfactualId";

    public static final String IS_VALID_FIELD = "isValid";

    public static final String INPUTS_FIELD = "inputs";

    public static final String OUTPUTS_FIELD = "outputs";

    @JsonProperty(COUNTERFACTUAL_ID_FIELD)
    @NotNull(message = "counterfactualId must be provided.")
    private String counterfactualId;

    @JsonProperty(IS_VALID_FIELD)
    private Boolean isValid;

    @JsonProperty(INPUTS_FIELD)
    private Map<String, TypedValue> inputs;

    @JsonProperty(OUTPUTS_FIELD)
    private Map<String, TypedValue> outputs;

    private CounterfactualExplainabilityResultDto() {
        super();
    }

    private CounterfactualExplainabilityResultDto(String executionId,
            String counterfactualId,
            ExplainabilityStatus status,
            String statusDetails,
            boolean isValid,
            Map<String, TypedValue> inputs,
            Map<String, TypedValue> outputs) {
        super(executionId, status, statusDetails);
        this.counterfactualId = counterfactualId;
        this.isValid = isValid;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public static CounterfactualExplainabilityResultDto buildSucceeded(String executionId,
            String counterfactualId,
            Boolean isValid,
            Map<String, TypedValue> inputs,
            Map<String, TypedValue> outputs) {
        return new CounterfactualExplainabilityResultDto(executionId,
                counterfactualId,
                ExplainabilityStatus.SUCCEEDED,
                null,
                isValid,
                inputs,
                outputs);
    }

    public static CounterfactualExplainabilityResultDto buildFailed(String executionId,
            String counterfactualId,
            String statusDetails) {
        return new CounterfactualExplainabilityResultDto(executionId,
                counterfactualId,
                ExplainabilityStatus.FAILED,
                statusDetails,
                Boolean.FALSE,
                Collections.emptyMap(),
                Collections.emptyMap());
    }

    public String getCounterfactualId() {
        return counterfactualId;
    }

    public Boolean getValid() {
        return isValid;
    }

    public Map<String, TypedValue> getInputs() {
        return inputs;
    }

    public Map<String, TypedValue> getOutputs() {
        return outputs;
    }
}
