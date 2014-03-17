package org.drools.impl.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;

public class NodeInstanceContainerAdapter implements org.drools.runtime.process.NodeInstanceContainer {

	public NodeInstanceContainer delegate;
	
	public NodeInstanceContainerAdapter(NodeInstanceContainer delegate) {
		this.delegate = delegate;
	}

	public NodeInstanceContainer getDelegate() {
		return delegate;
	}
	
	public Collection<NodeInstance> getNodeInstances() {
		Collection<org.kie.api.runtime.process.NodeInstance> nodeInstances = delegate.getNodeInstances();
		if (nodeInstances == null) {
			return null;
		}
		List<NodeInstance> result = new ArrayList<NodeInstance>();
		for (org.kie.api.runtime.process.NodeInstance nodeInstance: nodeInstances) {
			result.add(new NodeInstanceAdapter(nodeInstance));
		}
		return result;
	}

	public NodeInstance getNodeInstance(long nodeInstanceId) {
		org.kie.api.runtime.process.NodeInstance nodeInstance = delegate.getNodeInstance(nodeInstanceId);
		if (nodeInstance == null) {
			return null;
		} else {
			return new NodeInstanceAdapter(nodeInstance);
		}
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NodeInstanceContainerAdapter && delegate.equals(((NodeInstanceContainerAdapter)obj).delegate);
    }
}
