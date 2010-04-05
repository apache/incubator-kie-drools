package org.drools.workflow.instance.node;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.DynamicNode;

public class DynamicNodeInstance extends CompositeContextNodeInstance {

	private static final long serialVersionUID = 4L;
	
	private String getRuleFlowGroupName() {
		return getNodeName();
	}
	
	protected DynamicNode getDynamicNode() {
		return (DynamicNode) getNode();
	}
	
    public void internalTrigger(NodeInstance from, String type) {
    	getProcessInstance().getWorkingMemory().getAgenda().getRuleFlowGroup(getRuleFlowGroupName()).setAutoDeactivate(false);
    	getProcessInstance().getWorkingMemory().getAgenda().activateRuleFlowGroup(
			getRuleFlowGroupName(), getProcessInstance().getId(), getUniqueId());
//    	if (getDynamicNode().isAutoComplete() && getNodeInstances(false).isEmpty()) {
//    		triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
//    	}
    }

	public void nodeInstanceCompleted(org.drools.workflow.instance.NodeInstance nodeInstance, String outType) {
		// TODO what if we reach the end of one branch but others might still need to be created ?
		// TODO are we sure there will always be node instances left if we are not done yet?
		if (getDynamicNode().isAutoComplete() && getNodeInstances(false).isEmpty()) {
    		triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
    	}
	}
	
    public void triggerCompleted(String outType) {
    	getProcessInstance().getWorkingMemory().getAgenda().deactivateRuleFlowGroup(getRuleFlowGroupName());
    	super.triggerCompleted(outType);
    }

	public void signalEvent(String type, Object event) {
		super.signalEvent(type, event);
		for (Node node: getCompositeNode().getNodes()) {
			if (type.equals(node.getName()) && node.getIncomingConnections().isEmpty()) {
    			NodeInstance nodeInstance = getNodeInstance(node);
                ((org.drools.workflow.instance.NodeInstance) nodeInstance)
                	.trigger(null, NodeImpl.CONNECTION_DEFAULT_TYPE);
    		}
		}
	}
	
}
