package org.kie.dmn.api.core.ast;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.model.api.BusinessKnowledgeModel;

public interface BusinessKnowledgeModelNode extends DMNNode {
    
    DMNType getResultType();
    
    BusinessKnowledgeModel getBusinessKnowledModel();

}
