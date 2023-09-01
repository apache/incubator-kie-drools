package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionServiceEvent;

public class BeforeEvaluateDecisionServiceEventImpl implements BeforeEvaluateDecisionServiceEvent {

    private DecisionServiceNode decision;
    private DMNResult     result;

    public BeforeEvaluateDecisionServiceEventImpl(DecisionServiceNode decision, DMNResult result) {
        this.decision = decision;
        this.result = result;
    }

    @Override
    public DecisionServiceNode getDecisionService() {
        return this.decision;
    }

    @Override
    public DMNResult getResult() {
        return this.result;
    }

    @Override
    public String toString() {
        return "BeforeEvaluateDecisionServiceEvent{ name='" + decision.getName() + "' id='" + decision.getId() + "' }";
    }
}
