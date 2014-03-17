package org.drools.impl.adapters;

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;

public class NodeContainerAdapter implements org.drools.definition.process.NodeContainer {

	public NodeContainer delegate;
	
	public NodeContainerAdapter(NodeContainer delegate) {
		this.delegate = delegate;
	}

	public NodeContainer getDelegate() {
		return delegate;
	}
	
	public Node[] getNodes() {
		List<Node> result = new ArrayList<Node>();
		for (org.kie.api.definition.process.Node node: delegate.getNodes()) {
			result.add(new NodeAdapter(node));
		}
		return result.toArray(new Node[result.size()]);
	}

	public Node getNode(long id) {
		return new NodeAdapter(delegate.getNode(id));
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NodeContainerAdapter && delegate.equals(((NodeContainerAdapter)obj).delegate);
    }
}
