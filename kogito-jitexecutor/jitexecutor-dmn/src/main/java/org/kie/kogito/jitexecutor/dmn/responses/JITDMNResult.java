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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.core.internal.utils.MapBackedDMNContext;
import org.kie.dmn.core.internal.utils.MarshallingStubUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JITDMNResult implements Serializable,
        org.kie.dmn.api.core.DMNResult {

    private String namespace;

    private String modelName;

    private Map<String, Object> dmnContext = new HashMap<>();

    private List<JITDMNMessage> messages = new ArrayList<>();

    private List<DMNDecisionResult> decisionResults;

    public static JITDMNResult of(String namespace, String modelName, org.kie.dmn.api.core.DMNResult dmnResult, Map<String, Map<String, Integer>> decisionEvaluationHitIdsMap) {
        JITDMNResult toReturn = new JITDMNResult();
        toReturn.namespace = namespace;
        toReturn.modelName = modelName;
        toReturn.dmnContext = internalGetContext(dmnResult.getContext().getAll());
        toReturn.messages = internalGetMessages(dmnResult.getMessages());
        toReturn.decisionResults = internalGetDecisionResults(dmnResult.getDecisionResults(), decisionEvaluationHitIdsMap);
        return toReturn;
    }

    public JITDMNResult() {
        // Intentionally blank.
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
        this.dmnContext = dmnContext;
    }

    public void setMessages(List<JITDMNMessage> messages) {
        this.messages = messages;
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

    public void setDecisionResults(List<DMNDecisionResult> decisionResults) {
        this.decisionResults = decisionResults;
    }

    @Override
    public List<DMNDecisionResult> getDecisionResults() {
        return decisionResults;
    }

    @Override
    public DMNDecisionResult getDecisionResultByName(String name) {
        return decisionResults.stream()
                .filter(dr -> dr.getDecisionName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown decision result name."));
    }

    @Override
    public DMNDecisionResult getDecisionResultById(String id) {
        return decisionResults.stream()
                .filter(dr -> dr.getDecisionId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown decision result id."));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JITDMNResult that)) {
            return false;
        }
        return Objects.equals(namespace, that.namespace) && Objects.equals(modelName, that.modelName) && Objects.equals(dmnContext, that.dmnContext) && Objects.equals(messages, that.messages)
                && Objects.equals(decisionResults, that.decisionResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, modelName, dmnContext, messages, decisionResults);
    }

    @Override
    public String toString() {
        return new StringBuilder("JITDMNResult [")
                .append("namespace=").append(namespace)
                .append(", modelName=").append(modelName)
                .append(", dmnContext=").append(dmnContext)
                .append(", messages=").append(messages)
                .append(", decisionResults=").append(decisionResults)
                .append("]").toString();
    }

    private static Map<String, Object> internalGetContext(Map<String, Object> source) {
        Map<String, Object> toReturn = new HashMap<>();
        for (Entry<String, Object> kv : source.entrySet()) {
            toReturn.put(kv.getKey(), MarshallingStubUtils.stubDMNResult(kv.getValue(), String::valueOf));
        }
        return toReturn;
    }

    private static List<JITDMNMessage> internalGetMessages(List<? extends DMNMessage> messages) {
        return messages.stream().map(JITDMNMessage::of).collect(Collectors.toList());
    }

    private static List<DMNDecisionResult> internalGetDecisionResults(List<? extends DMNDecisionResult> decisionResults, Map<String, Map<String, Integer>> decisionEvaluationHitIdsMap) {
        return decisionResults.stream().map(dr -> JITDMNDecisionResult.of(dr, decisionEvaluationHitIdsMap.getOrDefault(dr.getDecisionName(), Collections.emptyMap()))).collect(Collectors.toList());
    }
}
