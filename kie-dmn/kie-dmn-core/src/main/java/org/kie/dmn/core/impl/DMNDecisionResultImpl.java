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
