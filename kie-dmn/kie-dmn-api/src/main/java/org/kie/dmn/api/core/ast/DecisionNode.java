package org.kie.dmn.api.core.ast;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.model.api.Decision;

public interface DecisionNode extends DMNNode {

    DMNType getResultType();

    Decision getDecision();

}
