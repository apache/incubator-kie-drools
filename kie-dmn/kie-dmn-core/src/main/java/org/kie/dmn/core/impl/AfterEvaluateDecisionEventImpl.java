package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;

public class AfterEvaluateDecisionEventImpl
        implements AfterEvaluateDecisionEvent {

    private DecisionNode decision;
    private DMNResult result;
    private BeforeEvaluateDecisionEvent before;


    public AfterEvaluateDecisionEventImpl(DecisionNode decision, DMNResult result, BeforeEvaluateDecisionEvent beforeEvent) {
        this.decision = decision;
        this.result = result;
        this.before = beforeEvent;
    }

    @Override
    public DecisionNode getDecision() {
        return this.decision;
    }

    @Override
    public DMNResult getResult() {
        return this.result;
    }

    public BeforeEvaluateDecisionEvent getBeforeEvent() {
        return before;
    }

    @Override
    public String toString() {
        return "AfterEvaluateDecisionEvent{ name='"+decision.getName()+"' id='"+decision.getId()+"' }";
    }

}
