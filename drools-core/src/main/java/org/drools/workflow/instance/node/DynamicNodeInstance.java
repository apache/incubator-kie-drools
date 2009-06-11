package org.drools.workflow.instance.node;

import java.util.List;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.impl.NodeImpl;

public class DynamicNodeInstance extends CompositeContextNodeInstance {

	private static final long serialVersionUID = 4L;
	
	private transient boolean executing = false;
	
    public void internalTrigger(NodeInstance from, String type) {
    	executing = true;
    	createNodeInstances();
    	executing = false;
    	if (getNodeInstances(false).isEmpty()) {
    		triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
    	}
    }
    
    private void createNodeInstances() {
    	for (Node node: getCompositeNode().getNodes()) {
    		Map<String, List<Connection>> incomingConnections = node.getIncomingConnections();
    		if (incomingConnections.isEmpty()) {
    			NodeInstance nodeInstance = getNodeInstance(node);
                ((org.drools.workflow.instance.NodeInstance) nodeInstance)
                	.trigger(null, NodeImpl.CONNECTION_DEFAULT_TYPE);
    		}
    	}
    }

	public void nodeInstanceCompleted(org.drools.workflow.instance.NodeInstance nodeInstance, String outType) {
		if (!executing && getNodeInstances(false).isEmpty()) {
    		triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
    	}
	}

}
