package org.jbpm.services.task.events;

import java.util.EventObject;

import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskEvent;

public class TaskEventImpl extends EventObject implements TaskEvent {

	private static final long serialVersionUID = -3579310906511209132L;

	private Task task;
	private transient TaskContext taskContext;
	
	public TaskEventImpl(Task task, TaskContext context) {
		super(task);
		this.task = task;
		this.taskContext = context;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(TaskContext context) {
		this.taskContext = context;
	}
	
}
