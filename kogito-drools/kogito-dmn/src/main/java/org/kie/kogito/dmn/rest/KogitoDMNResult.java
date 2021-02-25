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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.core.internal.utils.MapBackedDMNContext;
import org.kie.dmn.core.internal.utils.MarshallingStubUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KogitoDMNResult implements Serializable,
        org.kie.dmn.api.core.DMNResult {

    private String namespace;

    private String modelName;

    private Map<String, Object> dmnContext = new HashMap<>();

    private List<KogitoDMNMessage> messages = new ArrayList<>();

    private Map<String, KogitoDMNDecisionResult> decisionResults = new HashMap<>();

    public KogitoDMNResult() {
        // Intentionally blank.
    }

    public KogitoDMNResult(String namespace, String modelName, org.kie.dmn.api.core.DMNResult dmnResult) {
        this.namespace = namespace;
        this.modelName = modelName;
        this.setDmnContext(dmnResult.getContext().getAll());
        this.setMessages(dmnResult.getMessages());
        this.setDecisionResults(dmnResult.getDecisionResults());
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Map<String, Object> getDmnContext() {
        return dmnContext;
    }

    public void setDmnContext(Map<String, Object> dmnContext) {
        this.dmnContext = new HashMap<>();
        for (Entry<String, Object> kv : dmnContext.entrySet()) {
            this.dmnContext.put(kv.getKey(), MarshallingStubUtils.stubDMNResult(kv.getValue(), String::valueOf));
        }
    }

    public void setMessages(List<DMNMessage> messages) {
        this.messages = new ArrayList<>();
        for (DMNMessage m : messages) {
            this.messages.add(KogitoDMNMessage.of(m));
        }
    }

    public void setDecisionResults(List<? extends DMNDecisionResult> decisionResults) {
        this.decisionResults = new HashMap<>();
        for (DMNDecisionResult dr : decisionResults) {
            this.decisionResults.put(dr.getDecisionId(), KogitoDMNDecisionResult.of(dr));
        }
    }

    @JsonIgnore
    @Override
    public DMNContext getContext() {
        return MapBackedDMNContext.of(dmnContext);
    }

    @Override
    public List<DMNMessage> getMessages() {
        return (List) messages;
    }

    @Override
    public List<DMNMessage> getMessages(Severity... sevs) {
        return this.messages.stream()
                .filter(m -> Arrays.asList(sevs).stream().anyMatch(f -> f.equals(m.getSeverity())))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasErrors() {
        return messages.stream().anyMatch(m -> DMNMessage.Severity.ERROR.equals(m.getSeverity()));
    }

    @Override
    public List<DMNDecisionResult> getDecisionResults() {
        return new ArrayList<>(decisionResults.values());
    }

    @Override
    public DMNDecisionResult getDecisionResultByName(String name) {
        return decisionResults.values().stream()
                .filter(dr -> dr.getDecisionName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown decision result name."));
    }

    @Override
    public DMNDecisionResult getDecisionResultById(String id) {
        return decisionResults.get(id);
    }

    @Override
    public String toString() {
        return new StringBuilder("KogitoDMNResult [")
                .append("namespace=").append(namespace)
                .append(", modelName=").append(modelName)
                .append(", dmnContext=").append(dmnContext)
                .append(", messages=").append(messages)
                .append(", decisionResults=").append(decisionResults)
                .append("]").toString();
    }
}
