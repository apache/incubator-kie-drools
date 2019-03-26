/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.assignment.impl;

public class TaskAverageDuration {
	private Double averageDuration;
	private String deploymentId;
	private String processId;
	private String taskName;
	
	public TaskAverageDuration(Double averageDuration, String deploymentId, String processId, String taskName) {
		super();
		this.averageDuration = averageDuration;
		this.deploymentId = deploymentId;
		this.processId = processId;
		this.taskName = taskName;
	}
	public TaskAverageDuration(Long duration, String deploymentId, String processId, String taskName) {
		super();
		this.averageDuration = duration.doubleValue();
		this.deploymentId = deploymentId;
		this.processId = processId;
		this.taskName = taskName;
	}
	public Double getAverageDuration() {
		return averageDuration;
	}
	public void setAverageDuration(Double averageDuration) {
		this.averageDuration = averageDuration;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	@Override
	public String toString() {
		return "TaskAverageDuration{ "+"taskName = "+taskName+", processId = "+processId+", deploymentId = "+deploymentId+", duration = "+averageDuration+"}"; 
	}
}
