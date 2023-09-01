package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;

public class BeforeEvaluateDecisionEventImpl
        implements BeforeEvaluateDecisionEvent {

    private DecisionNode  decision;
    private DMNResult     result;
    private long          timestamp;

    public BeforeEvaluateDecisionEventImpl(DecisionNode decision, DMNResult result) {
        this.decision = decision;
        this.result = result;
    }

    @Override
    public DecisionNode getDecision() {
        return this.decision;
    }

    @Override
    public DMNResult getResult() {
        return this.result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BeforeEvaluateDecisionEvent{ name='" + decision.getName() + "' id='" + decision.getId() + "' }";
    }
}
