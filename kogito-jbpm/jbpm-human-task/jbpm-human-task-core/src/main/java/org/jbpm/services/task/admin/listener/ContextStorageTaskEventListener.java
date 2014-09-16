/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.admin.listener;

import java.util.HashSet;
import java.util.Set;

import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.jbpm.services.task.query.TaskSummaryImpl;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskContext;

/**
 * Listener responsible for recording tasks that are in the final state 
 * (complete, exit, skip, etc) within given transaction so they can be found and used 
 * as part of the clean up runtine without causing issues with too early flush.
 * Especially important when subprocesses (reusable) are involved.
 *
 */
public class ContextStorageTaskEventListener extends DefaultTaskEventListener {

	private String identifier = "ContextStorageTaskEventListener";
	
	@Override
	public void beforeTaskSkippedEvent(TaskEvent event) {
		store(event);
	}

	@Override
	public void beforeTaskCompletedEvent(TaskEvent event) {
		store(event);
	}

	@Override
	public void beforeTaskFailedEvent(TaskEvent event) {
		store(event);
	}

	@Override
	public void beforeTaskExitedEvent(TaskEvent event) {
		store(event);
	}

	@Override
	public void beforeTaskSuspendedEvent(TaskEvent event) {
		store(event);
	}

	@SuppressWarnings("unchecked")
	private void store(TaskEvent event) {
		TaskContext context = (TaskContext) event.getTaskContext();
		Set<TaskSummary> completedTasks = (Set<TaskSummary>) context.get("local:current-tasks");
		if (completedTasks == null) {
			completedTasks = new HashSet<TaskSummary>();
			context.set("local:current-tasks", completedTasks);
		}
		TaskSummaryImpl ts = new TaskSummaryImpl();
		ts.setId(event.getTask().getId());
		ts.setProcessInstanceId(event.getTask().getTaskData().getProcessInstanceId());
		completedTasks.add(ts);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContextStorageTaskEventListener other = (ContextStorageTaskEventListener) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}
}
