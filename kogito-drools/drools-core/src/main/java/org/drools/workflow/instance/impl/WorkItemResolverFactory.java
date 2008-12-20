package org.drools.workflow.instance.impl;

import org.drools.process.instance.WorkItem;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class WorkItemResolverFactory extends ImmutableDefaultFactory {

	private static final long serialVersionUID = 4L;
	
	private WorkItem workItem;
	
	public WorkItemResolverFactory(WorkItem workItem) {
		this.workItem = workItem;
	}

	public boolean isResolveable(String name) {
		return workItem.getResult(name) != null;
	}
	
	public VariableResolver getVariableResolver(String name) {
		return new SimpleValueResolver(workItem.getResult(name));
	}
	
}
