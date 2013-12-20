package org.jbpm.runtime.manager.impl.mapper;

import org.kie.api.runtime.Environment;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class EnvironmentAwareProcessInstanceContext extends ProcessInstanceIdContext {

	private Environment environment;
	public EnvironmentAwareProcessInstanceContext(Environment environment, Long processInstanceId) {
		super(processInstanceId);
		this.environment = environment;
	}
	
	public Environment getEnvironment() {
		return this.environment;
	}
}
