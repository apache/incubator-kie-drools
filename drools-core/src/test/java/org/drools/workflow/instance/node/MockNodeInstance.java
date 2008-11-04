/**
 * 
 */
package org.drools.workflow.instance.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Node;
import org.drools.process.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

public class MockNodeInstance extends NodeInstanceImpl {
    
    private static final long serialVersionUID = 400L;

    private Map<String, List<NodeInstance>> triggers = new HashMap<String, List<NodeInstance>>();
    private MockNode mockNode;
    
    public MockNodeInstance(MockNode mockNode) {
        this.mockNode = mockNode;
    }
    
    public Node getNode() {
        return mockNode;
    }
    
    public MockNode getMockNode() {
        return mockNode;
    }
    
    public void internalTrigger(NodeInstance from, String type) {
        if (type == null) {
            throw new IllegalArgumentException(
                "Trigger type is null!");
        }
        List<NodeInstance> list = triggers.get(type);
        if (list == null) {
            list = new ArrayList<NodeInstance>();
            triggers.put(type, list);
        }
        list.add(from);
    }
    
    public Map<String, List<NodeInstance>> getTriggers() {
        return triggers;
    }      
    
    public int hashCode() {
        return (int) getNodeId();
    }
    
    public boolean equals(Object object) {
        if ( object == null || (!( object instanceof MockNodeInstance ) )) {
            return false;
        }
        MockNodeInstance other = ( MockNodeInstance ) object;
        return getNodeId() == other.getNodeId();
    }        
    
    public void triggerCompleted() {
        triggerCompleted(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }
}