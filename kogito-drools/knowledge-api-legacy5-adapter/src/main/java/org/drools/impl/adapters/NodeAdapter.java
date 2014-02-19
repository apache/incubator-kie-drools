package org.drools.impl.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.definition.process.NodeContainer;
import org.kie.api.definition.process.Node;

public class NodeAdapter implements org.drools.definition.process.Node {

	public Node delegate;
	
	public NodeAdapter(Node delegate) {
		this.delegate = delegate;
	}

	public Node getDelegate() {
		return delegate;
	}
	
	public long getId() {
		return delegate.getId();
	}

	public String getName() {
		return delegate.getName();
	}

	public Map<String, List<Connection>> getIncomingConnections() {
		return adaptConnectionMap(delegate.getIncomingConnections());
	}

	public Map<String, List<Connection>> getOutgoingConnections() {
		return adaptConnectionMap(delegate.getOutgoingConnections());
	}

	public List<Connection> getIncomingConnections(String type) {
		return adaptConnectionList(delegate.getIncomingConnections(type));
	}

	public List<Connection> getOutgoingConnections(String type) {
		return adaptConnectionList(delegate.getOutgoingConnections(type));
	}

	public NodeContainer getNodeContainer() {
		return new NodeContainerAdapter(delegate.getNodeContainer());
	}

	public Map<String, Object> getMetaData() {
		return delegate.getMetaData();
	}

	public Object getMetaData(String name) {
		return delegate.getMetaData().get(name);
	}
	
	private Map<String, List<Connection>> adaptConnectionMap(Map<String, List<org.kie.api.definition.process.Connection>> connections) {
		if (connections == null) {
			return null;
		}
		Map<String, List<Connection>> result = new HashMap<String, List<Connection>>();
		for (Map.Entry<String, List<org.kie.api.definition.process.Connection>> entry: connections.entrySet()) {
			result.put(entry.getKey(), adaptConnectionList(entry.getValue()));
		}
		return result;
	}
	
	private List<Connection> adaptConnectionList(List<org.kie.api.definition.process.Connection> connections) {
		if (connections == null) {
			return null;
		}
		List<Connection> result = new ArrayList<Connection>(connections.size());
		for (org.kie.api.definition.process.Connection connection: connections) {
			result.add(new ConnectionAdapter(connection));
		}
		return result;
	}
	
}
