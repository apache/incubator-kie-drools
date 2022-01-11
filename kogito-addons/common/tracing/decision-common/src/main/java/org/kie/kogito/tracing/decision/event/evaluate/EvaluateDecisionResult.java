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
package org.kie.kogito.tracing.decision.event.evaluate;

import java.util.List;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.kogito.tracing.decision.event.EventUtils;
import org.kie.kogito.tracing.event.message.Message;

import static org.kie.kogito.tracing.decision.event.EventUtils.map;

public class EvaluateDecisionResult {

    private String decisionId;
    private String decisionName;
    private DecisionEvaluationStatus evaluationStatus;
    private Object result;
    private List<Message> messages;
    private boolean errors;

    public EvaluateDecisionResult(String decisionId, String decisionName, DecisionEvaluationStatus evaluationStatus, Object result, List<Message> messages, boolean errors) {
        this.decisionId = decisionId;
        this.decisionName = decisionName;
        this.evaluationStatus = evaluationStatus;
        this.result = result;
        this.messages = messages;
        this.errors = errors;
    }

    private EvaluateDecisionResult() {
    }

    public String getDecisionId() {
        return decisionId;
    }

    public String getDecisionName() {
        return decisionName;
    }

    public DecisionEvaluationStatus getEvaluationStatus() {
        return evaluationStatus;
    }

    public Object getResult() {
        return result;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean hasErrors() {
        return errors;
    }

    public static EvaluateDecisionResult from(DMNDecisionResult dr) {
        return new EvaluateDecisionResult(
                dr.getDecisionId(),
                dr.getDecisionName(),
                dr.getEvaluationStatus(),
                dr.getResult(),
                map(dr.getMessages(), EventUtils::messageFrom),
                dr.hasErrors());
    }
}
