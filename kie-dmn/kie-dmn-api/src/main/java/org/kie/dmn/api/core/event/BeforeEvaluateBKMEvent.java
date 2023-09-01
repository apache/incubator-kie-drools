package org.kie.dmn.api.core.event;

import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;

public interface BeforeEvaluateBKMEvent
        extends DMNEvent {

    BusinessKnowledgeModelNode getBusinessKnowledgeModel();

}
