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
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;

public class DecisionOutcomeResponse {

    @JsonProperty("outcomeId")
    private String outcomeId;

    @JsonProperty("outcomeName")
    private String outcomeName;

    @JsonProperty("evaluationStatus")
    private String evaluationStatus;

    @JsonProperty("outcomeResult")
    private TypedValueResponse outcomeResult;

    @JsonProperty("outcomeInputs")
    private Collection<TypedValueResponse> outcomeInputs;

    @JsonProperty("messages")
    private Collection<MessageResponse> messages;

    @JsonProperty("hasErrors")
    private boolean hasErrors;

    private DecisionOutcomeResponse() {
    }

    public DecisionOutcomeResponse(String outcomeId, String outcomeName, String evaluationStatus, TypedValueResponse outcomeResult, Collection<TypedValueResponse> outcomeInputs, Collection<MessageResponse> messages, boolean hasErrors) {
        this.outcomeId = outcomeId;
        this.outcomeName = outcomeName;
        this.evaluationStatus = evaluationStatus;
        this.outcomeResult = outcomeResult;
        this.outcomeInputs = outcomeInputs;
        this.messages = messages;
        this.hasErrors = hasErrors;
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public String getOutcomeName() {
        return outcomeName;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public TypedValueResponse getOutcomeResult() {
        return outcomeResult;
    }

    public Collection<TypedValueResponse> getOutcomeInputs() {
        return outcomeInputs;
    }

    public Collection<MessageResponse> getMessages() {
        return messages;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public static DecisionOutcomeResponse from(DecisionOutcome outcome) {
        return outcome == null ? null : new DecisionOutcomeResponse(
                outcome.getOutcomeId(),
                outcome.getOutcomeName(),
                outcome.getEvaluationStatus(),
                TypedValueResponse.from(outcome.getOutcomeResult()),
                from(outcome.getOutcomeInputs(), TypedValueResponse::from),
                from(outcome.getMessages(), MessageResponse::from),
                outcome.hasErrors()
        );
    }

    public static <T, U> Collection<U> from(Collection<T> input, Function<T, U> mapper) {
        return input == null ? null : input.stream().map(mapper).collect(Collectors.toList());
    }
}
