/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.impl;

import org.kie.dmn.core.api.DMNMessage;

import java.util.ArrayList;
import java.util.List;

public class DMNDecisionResultImpl
        implements org.kie.dmn.core.api.DMNDecisionResult {
    private String           decisionId;
    private String           decisionName;
    private Object           result;
    private List<DMNMessage> messages;

    public DMNDecisionResultImpl(String decisionId, String decisionName) {
        this( decisionId, decisionName, null, new ArrayList<>(  ) );
    }

    public DMNDecisionResultImpl(String decisionId, String decisionName, Object result, List<DMNMessage> messages) {
        this.decisionId = decisionId;
        this.decisionName = decisionName;
        this.result = result;
        this.messages = messages;
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
}
