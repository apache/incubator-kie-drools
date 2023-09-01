package org.kie.dmn.api.core.event;

import org.kie.dmn.api.core.ast.DecisionServiceNode;

public interface BeforeEvaluateDecisionServiceEvent extends DMNEvent {

    DecisionServiceNode getDecisionService();

}
