package org.drools.workflow.instance.impl;

import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.workflow.instance.NodeInstance;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class NodeInstanceResolverFactory extends ImmutableDefaultFactory {

	private static final long serialVersionUID = 4L;
	
	private NodeInstance nodeInstance;
	
	public NodeInstanceResolverFactory(NodeInstance nodeInstance) {
		this.nodeInstance = nodeInstance;
	}

	public boolean isResolveable(String name) {
		return nodeInstance.resolveContextInstance(VariableScope.VARIABLE_SCOPE, name) != null;
	}
	
	public VariableResolver getVariableResolver(String name) {
		Object value = ((VariableScopeInstance)
			nodeInstance.resolveContextInstance(
					VariableScope.VARIABLE_SCOPE, name)).getVariable(name);
		return new SimpleValueResolver(value);
	}
	
}