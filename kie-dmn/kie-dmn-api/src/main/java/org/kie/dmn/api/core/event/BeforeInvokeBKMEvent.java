package org.kie.dmn.api.core.event;

import java.util.List;

import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;

public interface BeforeInvokeBKMEvent
        extends DMNEvent {

    BusinessKnowledgeModelNode getBusinessKnowledgeModel();

    default List<Object> getInvocationParameters() {
        return null;
    }

}
