package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;

public class AfterEvaluateDecisionServiceEventImpl implements AfterEvaluateDecisionServiceEvent {

    private DecisionServiceNode decision;
    private DMNResult result;

    public AfterEvaluateDecisionServiceEventImpl(DecisionServiceNode decision, DMNResult result) {
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
        return "AfterEvaluateDecisionServiceEvent{ name='" + decision.getName() + "' id='" + decision.getId() + "' }";
    }

}
