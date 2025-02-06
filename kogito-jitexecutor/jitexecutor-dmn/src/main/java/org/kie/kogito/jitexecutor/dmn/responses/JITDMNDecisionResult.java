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
package org.kie.kogito.jitexecutor.dmn.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.internal.utils.MarshallingStubUtils;

public class JITDMNDecisionResult implements Serializable,
        DMNDecisionResult {

    private String decisionId;

    private String decisionName;

    private Object result;

    private List<JITDMNMessage> messages = new ArrayList<>();

    private DecisionEvaluationStatus status;

    private Map<String, Integer> evaluationHitIds;

    public JITDMNDecisionResult() {
        // Intentionally blank.
    }

    public static JITDMNDecisionResult of(DMNDecisionResult value) {
        return of(value, Collections.emptyMap());
    }

    public static JITDMNDecisionResult of(DMNDecisionResult value, Map<String, Integer> decisionEvaluationHitIdsMap) {
        JITDMNDecisionResult res = new JITDMNDecisionResult();
        res.decisionId = value.getDecisionId();
        res.decisionName = value.getDecisionName();
        res.setResult(value.getResult());
        res.setMessages(value.getMessages());
        res.status = value.getEvaluationStatus();
        res.evaluationHitIds = decisionEvaluationHitIdsMap;
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
            this.messages.add(JITDMNMessage.of(m));
        }
    }

    public Map<String, Integer> getEvaluationHitIds() {
        return evaluationHitIds;
    }

    public void setEvaluationHitIds(Map<String, Integer> evaluationHitIds) {
        this.evaluationHitIds = evaluationHitIds;
    }

    @Override
    public boolean hasErrors() {
        return messages != null && messages.stream().anyMatch(m -> m.getSeverity() == DMNMessage.Severity.ERROR);
    }
}
