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

import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class EvaluateContextEntryResult {

    @JsonInclude(NON_NULL)
    private String variableId;
    private String variableName;
    private String expressionId;
    private Object expressionResult;

    public EvaluateContextEntryResult(String variableId, String variableName, String expressionId, Object expressionResult) {
        this.variableId = variableId;
        this.variableName = variableName;
        this.expressionId = expressionId;
        this.expressionResult = expressionResult;
    }

    private EvaluateContextEntryResult() {
    }

    public String getVariableId() {
        return variableId;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getExpressionId() {
        return expressionId;
    }

    public Object getExpressionResult() {
        return expressionResult;
    }

    public static EvaluateContextEntryResult from(BeforeEvaluateContextEntryEvent event) {
        return new EvaluateContextEntryResult(event.getVariableId(), event.getVariableName(), event.getExpressionId(), null);
    }

    public static EvaluateContextEntryResult from(AfterEvaluateContextEntryEvent event) {
        return new EvaluateContextEntryResult(event.getVariableId(), event.getVariableName(), event.getExpressionId(), event.getExpressionResult());
    }
}
