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

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A decision.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Decision extends Execution {

    public static final String INPUTS_FIELD = "inputs";
    public static final String OUTCOMES_FIELD = "outcomes";

    @JsonProperty(INPUTS_FIELD)
    private Collection<TypedValue> inputs;

    @JsonProperty(OUTCOMES_FIELD)
    private Collection<DecisionOutcome> outcomes;

    public Decision() {
        super(ExecutionTypeEnum.DECISION);
    }

    public Decision(String executionId, Long executionTimestamp, Boolean hasSucceeded, String executorName, String executedModelName, List<TypedValue> inputs, List<DecisionOutcome> outcomes) {
        super(executionId, executionTimestamp, hasSucceeded, executorName, executedModelName, ExecutionTypeEnum.DECISION);
        this.inputs = inputs;
        this.outcomes = outcomes;
    }

    public Collection<TypedValue> getInputs() {
        return inputs;
    }

    public void setInputs(Collection<TypedValue> inputs) {
        this.inputs = inputs;
    }

    public Collection<DecisionOutcome> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(Collection<DecisionOutcome> outcomes) {
        this.outcomes = outcomes;
    }
}
