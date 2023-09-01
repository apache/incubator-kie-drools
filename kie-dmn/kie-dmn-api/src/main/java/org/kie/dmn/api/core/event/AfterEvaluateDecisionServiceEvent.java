package org.kie.dmn.api.core.event;

import org.kie.dmn.api.core.ast.DecisionServiceNode;

public interface AfterEvaluateDecisionServiceEvent extends DMNEvent {

    DecisionServiceNode getDecisionService();

}
