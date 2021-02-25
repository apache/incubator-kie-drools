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
package org.kie.kogito.dmn.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.internal.utils.MarshallingStubUtils;

public class KogitoDMNDecisionResult implements Serializable,
        DMNDecisionResult {

    private String decisionId;

    private String decisionName;

    private Object result;

    private List<KogitoDMNMessage> messages = new ArrayList<>();

    private DecisionEvaluationStatus status;

    public KogitoDMNDecisionResult() {
        // Intentionally blank.
    }

    public static KogitoDMNDecisionResult of(DMNDecisionResult value) {
        KogitoDMNDecisionResult res = new KogitoDMNDecisionResult();
        res.decisionId = value.getDecisionId();
        res.decisionName = value.getDecisionName();
        res.setResult(value.getResult());
        res.setMessages(value.getMessages());
        res.status = value.getEvaluationStatus();
        return res;
    }

    @Override
    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }

    @Override
    public String getDecisionName() {
        return decisionName;
    }

    public void setDecisionName(String decisionName) {
        this.decisionName = decisionName;
    }

    @Override
    public DecisionEvaluationStatus getEvaluationStatus() {
        return status;
    }

    public void setEvaluationStatus(DecisionEvaluationStatus status) {
        this.status = status;
    }

    @Override
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = MarshallingStubUtils.stubDMNResult(result, String::valueOf);
    }

    public List<DMNMessage> getMessages() {
        return (List) messages;
    }

    public void setMessages(List<DMNMessage> messages) {
        this.messages = new ArrayList<>();
        for (DMNMessage m : messages) {
            this.messages.add(KogitoDMNMessage.of(m));
        }
    }

    @Override
    public boolean hasErrors() {
        return messages != null && messages.stream().anyMatch(m -> m.getSeverity() == DMNMessage.Severity.ERROR);
    }
}
