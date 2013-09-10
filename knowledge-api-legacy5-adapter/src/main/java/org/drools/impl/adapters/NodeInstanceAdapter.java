package org.drools.impl.adapters;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.process.NodeInstance;

public class NodeInstanceAdapter implements org.drools.runtime.process.NodeInstance {

	public NodeInstance delegate;
	
	public NodeInstanceAdapter(NodeInstance delegate) {
		this.delegate = delegate;
	}
	
	public long getId() {
		return delegate.getId();
	}

	public long getNodeId() {
		return delegate.getNodeId();
	}

	public Node getNode() {
		throw new UnsupportedOperationException("org.drools.impl.adapters.StatefulKnowledgeSessionAdapter.getChannels -> TODO");
	}

	public String getNodeName() {
		return delegate.getNodeName();
	}

	public WorkflowProcessInstance getProcessInstance() {
		throw new UnsupportedOperationException("org.drools.impl.adapters.StatefulKnowledgeSessionAdapter.getChannels -> TODO");
	}

	public NodeInstanceContainer getNodeInstanceContainer() {
		throw new UnsupportedOperationException("org.drools.impl.adapters.StatefulKnowledgeSessionAdapter.getChannels -> TODO");
	}

	public Object getVariable(String variableName) {
		return delegate.getVariable(variableName);
	}

	public void setVariable(String variableName, Object value) {
		delegate.setVariable(variableName, value);
	}

}
