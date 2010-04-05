package org.drools.workflow.core.node;

import org.drools.definition.process.Node;

public class DynamicNode extends CompositeContextNode {

	private static final long serialVersionUID = 400L;
	
	private boolean autoComplete = false;
		
	public boolean isAutoComplete() {
		return autoComplete;
	}

	public void setAutoComplete(boolean autoComplete) {
		this.autoComplete = autoComplete;
	}

	public boolean acceptsEvent(String type, Object event) {
		for (Node node: getNodes()) {
			if (type.equals(node.getName()) && node.getIncomingConnections().isEmpty()) {
				return true;
			}
		}
		return super.acceptsEvent(type, event);
	}
	
    public Node internalGetNode(long id) {
    	try {
    		return getNode(id);
    	} catch (IllegalArgumentException e) {
    		return null;
    	}
    }
}
