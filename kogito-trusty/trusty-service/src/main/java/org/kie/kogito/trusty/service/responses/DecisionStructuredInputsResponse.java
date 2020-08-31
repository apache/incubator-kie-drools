/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.responses;

import java.util.Collection;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;

public class DecisionStructuredInputsResponse {

    @JsonProperty("inputs")
    private Collection<TypedVariableResponse> inputs;

    private DecisionStructuredInputsResponse() {
    }

    public DecisionStructuredInputsResponse(Collection<TypedVariableResponse> inputs) {
        this.inputs = inputs;
    }

    public Collection<TypedVariableResponse> getInputs() {
        return inputs;
    }

    public static DecisionStructuredInputsResponse from(Collection<DecisionInput> inputs) {
        return inputs == null ? null : new DecisionStructuredInputsResponse(inputs.stream().map(TypedVariableResponse::from).collect(Collectors.toList()));
    }

    public static DecisionStructuredInputsResponse from(Decision decision) {
        return from(decision.getInputs());
    }
}
