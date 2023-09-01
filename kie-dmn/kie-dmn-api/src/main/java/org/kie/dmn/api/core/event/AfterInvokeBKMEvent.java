package org.kie.dmn.api.core.event;

import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;

public interface AfterInvokeBKMEvent
        extends DMNEvent {

    BusinessKnowledgeModelNode getBusinessKnowledgeModel();

    default Object getInvocationResult() {
        return null;
    }

}
