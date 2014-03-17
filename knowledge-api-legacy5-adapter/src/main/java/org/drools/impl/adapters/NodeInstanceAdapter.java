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
	
	public NodeInstance getDelegate() {
		return delegate;
	}
	
	public long getId() {
		return delegate.getId();
	}

	public long getNodeId() {
		return delegate.getNodeId();
	}

	public Node getNode() {
		return new NodeAdapter(delegate.getNode());
	}

	public String getNodeName() {
		return delegate.getNodeName();
	}

	public WorkflowProcessInstance getProcessInstance() {
		return new WorkflowProcessInstanceAdapter(delegate.getProcessInstance());
	}

	public NodeInstanceContainer getNodeInstanceContainer() {
		return new NodeInstanceContainerAdapter(delegate.getNodeInstanceContainer());
	}

	public Object getVariable(String variableName) {
		return delegate.getVariable(variableName);
	}

	public void setVariable(String variableName, Object value) {
		delegate.setVariable(variableName, value);
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NodeInstanceAdapter && delegate.equals(((NodeInstanceAdapter)obj).delegate);
    }
}
