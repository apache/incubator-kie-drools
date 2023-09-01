package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;

public class BeforeEvaluateContextEntryEventImpl
        implements BeforeEvaluateContextEntryEvent {

    private final String nodeName;
    private final String variableName;
    private final String variableId;
    private final String expressionId;
    private final DMNResult result;

    public BeforeEvaluateContextEntryEventImpl(String nodeName, String variableName, String variableId, String expressionId, DMNResult result) {
        this.nodeName = nodeName;
        this.variableName = variableName;
        this.variableId = variableId;
        this.expressionId = expressionId;
        this.result = result;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public String getVariableId() {
        return variableId;
    }

    @Override
    public String getExpressionId() {
        return expressionId;
    }

    @Override
    public DMNResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "BeforeEvaluateContextEntryEventImpl{" +
                "nodeName='" + nodeName + '\'' +
                ", variableName='" + variableName + '\'' +
                ", variableId='" + variableId + '\'' +
                ", expressionId='" + expressionId + '\'' +
                '}';
    }
}
