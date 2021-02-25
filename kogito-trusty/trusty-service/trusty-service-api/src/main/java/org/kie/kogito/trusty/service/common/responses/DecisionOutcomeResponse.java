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

package org.kie.kogito.trusty.service.common.responses;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DecisionOutcomeResponse {

    @JsonProperty("outcomeId")
    private String outcomeId;

    @JsonProperty("outcomeName")
    private String outcomeName;

    @JsonProperty("evaluationStatus")
    private String evaluationStatus;

    @JsonProperty("outcomeResult")
    private TypedVariableResponse outcomeResult;

    @JsonProperty("outcomeInputs")
    private Collection<TypedVariableResponse> outcomeInputs;

    @JsonProperty("messages")
    private Collection<MessageResponse> messages;

    @JsonProperty("hasErrors")
    private boolean hasErrors;

    private DecisionOutcomeResponse() {
    }

    public DecisionOutcomeResponse(String outcomeId, String outcomeName, String evaluationStatus, TypedVariableResponse outcomeResult, Collection<TypedVariableResponse> outcomeInputs,
            Collection<MessageResponse> messages, boolean hasErrors) {
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

    public TypedVariableResponse getOutcomeResult() {
        return outcomeResult;
    }

    public Collection<TypedVariableResponse> getOutcomeInputs() {
        return outcomeInputs;
    }

    public Collection<MessageResponse> getMessages() {
        return messages;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }
}
