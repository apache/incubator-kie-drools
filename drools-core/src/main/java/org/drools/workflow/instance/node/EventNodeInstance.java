package org.drools.workflow.instance.node;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.instance.impl.ExtendedNodeInstanceImpl;

public abstract class EventNodeInstance extends ExtendedNodeInstanceImpl {

    public EventNode getEventNode() {
        return (EventNode) getNode();
    }
    
    public void triggerCompleted() {
        triggerCompleted(Node.CONNECTION_DEFAULT_TYPE, true);
    }
    
    public void cancel() {
        super.cancel();
        removeEventListeners();
    }
    
    public void addEventListeners() {
    }
    
    public void removeEventListeners() {
    }
    
}
