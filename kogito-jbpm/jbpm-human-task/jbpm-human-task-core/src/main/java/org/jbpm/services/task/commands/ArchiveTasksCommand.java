/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;


@XmlRootElement(name="archive-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ArchiveTasksCommand extends TaskCommand<Integer> {

	private static final long serialVersionUID = -2218880638839442379L;

	@XmlAnyElement
	private List<TaskSummary> tasks;
	
	public ArchiveTasksCommand() {
	}
	
    public ArchiveTasksCommand(List<TaskSummary> tasks) {
        this.tasks = tasks;
    }

    public Integer execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskAdminService().archiveTasks(tasks);

    }

	public List<TaskSummary> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskSummary> tasks) {
		this.tasks = tasks;
	}
    
    
}
