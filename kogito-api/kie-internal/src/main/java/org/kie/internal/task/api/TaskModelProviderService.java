package org.kie.internal.task.api;

import org.kie.api.Service;

public interface TaskModelProviderService extends Service {
	
	public TaskModelFactory getTaskModelFactory();
	
}
