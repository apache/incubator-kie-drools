package org.drools.workflow.instance.node;

import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

public abstract class EventNodeInstance extends NodeInstanceImpl {

    public EventNode getEventNode() {
        return (EventNode) getNode();
    }
    
    public void triggerCompleted() {
        getNodeInstanceContainer().removeNodeInstance(this);
        getNodeInstanceContainer().getNodeInstance(getEventNode().getTo().getTo())
            .trigger(this, getEventNode().getTo().getToType());
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
