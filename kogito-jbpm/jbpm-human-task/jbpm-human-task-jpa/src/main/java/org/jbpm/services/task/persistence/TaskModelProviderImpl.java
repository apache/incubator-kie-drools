package org.jbpm.services.task.persistence;

import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProviderService;

public class TaskModelProviderImpl implements TaskModelProviderService {

	@Override
	public TaskModelFactory getTaskModelFactory() {
		return new JPATaskModelFactory();
	}

}
