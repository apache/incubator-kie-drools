package org.kie.dmn.api.core.event;

import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;

public interface AfterEvaluateBKMEvent
        extends DMNEvent {

    BusinessKnowledgeModelNode getBusinessKnowledgeModel();

}
