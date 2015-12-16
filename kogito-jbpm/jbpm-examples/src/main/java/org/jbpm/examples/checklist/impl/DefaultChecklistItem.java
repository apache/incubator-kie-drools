/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.examples.checklist.impl;

import org.jbpm.examples.checklist.ChecklistItem;

public class DefaultChecklistItem implements ChecklistItem {

	private String name;
	private Status status;
	private Long taskId;
	private String type;
	private String actors;
	private long priority;
	private String processId;
	private Long processInstanceId;
	private String orderingNb;
	
	public DefaultChecklistItem(String name, Status status, Long taskId, String type, String actors, long priority, String processId, Long processInstanceId, String orderingNb) {
		this.name = name;
		this.status = status;
		this.taskId = taskId;
		this.type = type;
		this.actors = actors;
		this.priority = priority;
		this.processId = processId;
		this.processInstanceId = processInstanceId;
		this.orderingNb = orderingNb;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Long getTaskId() {
		return taskId;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getActors() {
		return actors;
	}

	@Override
	public long getPriority() {
		return priority;
	}

	@Override
	public String getProcessId() {
		return processId;
	}

	@Override
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	
	@Override
	public String getOrderingNb() {
		return orderingNb;
	}

}
