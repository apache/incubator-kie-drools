/**
 * 
 */
package org.drools.ruleflow.instance.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleflow.core.impl.ConnectionImpl;
import org.drools.ruleflow.core.impl.NodeImpl;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;

public class MockNodeInstance extends RuleFlowNodeInstanceImpl {
    private List list = new ArrayList();
    
    private NodeImpl node;
    
    public MockNodeInstance(NodeImpl node) {
        this.node = node;
    }
    
    public NodeImpl getNode() {
        return this.node;
    }
    
    public void internalTrigger(RuleFlowNodeInstance from) {
        this.list.add( from );    
        triggerCompleted();
    }
    
    public List getList() {
        return this.list;
    }      
    
    public int hashCode() {
        return (int) this.node.getId();
    }
    
    public boolean equals(Object object) {
        if ( object == null || (!( object instanceof MockNodeInstance ) )) {
            return false;
        }
        
        MockNodeInstance other = ( MockNodeInstance ) object;
        return getNode().getId() == other.getNode().getId();
    }        
    
    public void triggerCompleted() {
        
        getProcessInstance().getNodeInstance( ((ConnectionImpl) this.node.getOutgoingConnections().get( 0 )).getTo() ).trigger( this );
        getProcessInstance().removeNodeInstance(this);
    }
}