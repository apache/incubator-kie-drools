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

public class DecisionOutcomesResponse {

    @JsonProperty("header")
    private ExecutionHeaderResponse header;

    @JsonProperty("outcomes")
    private Collection<DecisionOutcomeResponse> outcomes;

    private DecisionOutcomesResponse() {
    }

    public DecisionOutcomesResponse(ExecutionHeaderResponse header, Collection<DecisionOutcomeResponse> outcomes) {
        this.header = header;
        this.outcomes = outcomes;
    }

    public ExecutionHeaderResponse getHeader() {
        return header;
    }

    public Collection<DecisionOutcomeResponse> getOutcomes() {
        return outcomes;
    }

    public static DecisionOutcomesResponse from(Decision decision) {
        if (decision == null) {
            return null;
        }
        Collection<DecisionOutcomeResponse> outcomes = decision.getOutcomes() == null ? null : decision.getOutcomes().stream()
                .map(DecisionOutcomeResponse::from)
                .collect(Collectors.toList());
        return new DecisionOutcomesResponse(ExecutionHeaderResponse.fromExecution(decision), outcomes);
    }
}
