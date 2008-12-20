package org.drools.workflow.instance.impl;

import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class VariableScopeResolverFactory extends ImmutableDefaultFactory {

	private static final long serialVersionUID = 4L;
	
	private VariableScopeInstance variableScope;
	
	public VariableScopeResolverFactory(VariableScopeInstance variableScope) {
		this.variableScope = variableScope;
	}

	public boolean isResolveable(String name) {
		return variableScope.getVariable(name) != null;
	}
	
	public VariableResolver getVariableResolver(String name) {
		Object value = variableScope.getVariable(name);
		return new SimpleValueResolver(value);
	}
	
}