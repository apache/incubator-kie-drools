/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
