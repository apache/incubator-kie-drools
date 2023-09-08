/**
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
package org.kie.dmn.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.api.core.DMNMessage;

public class DMNDecisionResultImpl
        implements org.kie.dmn.api.core.DMNDecisionResult {
    private String           decisionId;
    private String           decisionName;
    private Object           result;
    private List<DMNMessage> messages;
    private DecisionEvaluationStatus status;

    public DMNDecisionResultImpl(String decisionId, String decisionName) {
        this( decisionId, decisionName, DecisionEvaluationStatus.NOT_EVALUATED, null, new ArrayList<>(  ) );
    }

    public DMNDecisionResultImpl(String decisionId, String decisionName, DecisionEvaluationStatus status, Object result, List<DMNMessage> messages) {
        this.decisionId = decisionId;
        this.decisionName = decisionName;
        this.result = result;
        this.messages = messages;
        this.status = status;
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

    @Override
    public List<DMNMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<DMNMessage> messages) {
        this.messages = messages;
    }

    @Override
    public boolean hasErrors() {
        return messages != null && messages.stream().anyMatch( m -> m.getSeverity() == DMNMessage.Severity.ERROR );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DMNDecisionResultImpl [decisionId=");
        builder.append(decisionId);
        builder.append(", decisionName=");
        builder.append(decisionName);
        builder.append(", result=");
        builder.append(result);
        builder.append(", messages=");
        builder.append(messages);
        builder.append(", status=");
        builder.append(status);
        builder.append("]");
        return builder.toString();
    }

}
