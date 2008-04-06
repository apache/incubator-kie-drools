package org.drools.workflow.instance.context;

import org.drools.process.instance.ContextInstance;
import org.drools.workflow.instance.NodeInstanceContainer;

public interface WorkflowContextInstance extends ContextInstance {

    NodeInstanceContainer getNodeInstanceContainer();
    
    void setNodeInstanceContainer(NodeInstanceContainer nodeInstanceContainer);
    
}
