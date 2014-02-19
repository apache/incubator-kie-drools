package org.drools.impl.adapters;

import java.util.Map;

import org.kie.api.definition.process.Connection;

public class ConnectionAdapter implements org.drools.definition.process.Connection {

	public Connection delegate;
	
	public ConnectionAdapter(Connection delegate) {
		this.delegate = delegate;
	}

	public org.drools.definition.process.Node getFrom() {
		return new NodeAdapter(delegate.getFrom());
	}

	public org.drools.definition.process.Node getTo() {
		return new NodeAdapter(delegate.getTo());
	}

	public String getFromType() {
		return delegate.getFromType();
	}

	public String getToType() {
		return delegate.getToType();
	}

	public Map<String, Object> getMetaData() {
		return delegate.getMetaData();
	}

	public Object getMetaData(String name) {
		return delegate.getMetaData().get(name);
	}

}
