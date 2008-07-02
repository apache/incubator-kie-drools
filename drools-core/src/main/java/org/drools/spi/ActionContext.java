package org.drools.spi;

import org.drools.workflow.instance.NodeInstance;

public class ActionContext {
    
    private NodeInstance nodeInstance;

    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    public void setNodeInstance(NodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }
    
}
