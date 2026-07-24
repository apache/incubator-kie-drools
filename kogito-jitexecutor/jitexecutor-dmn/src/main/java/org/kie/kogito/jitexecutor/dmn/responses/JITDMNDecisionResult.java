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
import java.util.Objects;
import java.util.stream.Collectors;

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
        JITDMNDecisionResult toReturn = new JITDMNDecisionResult();
        toReturn.decisionId = value.getDecisionId();
        toReturn.decisionName = value.getDecisionName();
        toReturn.result = internalGetResult(value.getResult());
        toReturn.messages = internalGetMessages(value.getMessages());
        toReturn.status = value.getEvaluationStatus();
        toReturn.evaluationHitIds = decisionEvaluationHitIdsMap;
        return toReturn;
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
        this.result = result;
    }

    public List<DMNMessage> getMessages() {
        return (List) messages;
    }

    public void setMessages(List<JITDMNMessage> messages) {
        this.messages = messages;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JITDMNDecisionResult that)) {
            return false;
        }
        return Objects.equals(decisionId, that.decisionId) && Objects.equals(decisionName, that.decisionName) && Objects.equals(result, that.result) && Objects.equals(messages, that.messages)
                && status == that.status && Objects.equals(evaluationHitIds, that.evaluationHitIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(decisionId, decisionName, result, messages, status, evaluationHitIds);
    }

    @Override
    public String toString() {
        return "JITDMNDecisionResult{" +
                "decisionId='" + decisionId + '\'' +
                ", decisionName='" + decisionName + '\'' +
                ", result=" + result +
                ", messages=" + messages +
                ", status=" + status +
                ", evaluationHitIds=" + evaluationHitIds +
                '}';
    }

    private static Object internalGetResult(Object result) {
        return MarshallingStubUtils.stubDMNResult(result, String::valueOf);
    }

    private static List<JITDMNMessage> internalGetMessages(List<DMNMessage> messages) {
        return messages.stream().map(JITDMNMessage::of).collect(Collectors.toList());
    }
}
