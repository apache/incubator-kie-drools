package org.drools.core.marshalling.impl;

import java.util.Map;

import org.drools.core.marshalling.NamedObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;

public class NamedCustomMarshallingStrategy extends IdentityPlaceholderResolverStrategy implements NamedObjectMarshallingStrategy{


	private String name;
	
	public NamedCustomMarshallingStrategy(String name, ObjectMarshallingStrategyAcceptor acceptor, Map<Integer,Object> data ) {
		super(acceptor, data);
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
