package org.kie.dmn.api.core.event;

import org.kie.dmn.api.core.ast.DecisionNode;

public interface AfterEvaluateDecisionEvent extends DMNEvent {

    DecisionNode getDecision();

}
