package org.kie.dmn.api.core.ast;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.model.api.DecisionService;

public interface DecisionServiceNode extends DMNNode {
    
    DMNType getResultType();
    
    DecisionService getDecisionService();

}
