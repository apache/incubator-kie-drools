package org.kie.internal.task.api;

import org.kie.api.task.model.Task;

public interface TaskEvent {

	Task getTask();
	
	TaskContext getTaskContext();
}
