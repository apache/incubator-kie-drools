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

package org.jbpm.services.task.commands;

import org.kie.api.runtime.Context;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.ProcessInstanceIdCommand;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.Date;
import java.util.List;

@XmlRootElement(name="get-completed-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetCompletedTasksCommand extends TaskCommand<List<TaskSummary>> implements ProcessInstanceIdCommand {

    /** Generated serial version UID */
	private static final long serialVersionUID = 5077599352603072633L;

	@XmlElement
	private Date date;
	
	@XmlElement(name="process-instance-id")
	@XmlSchemaType(name="long")
	private Long processInstanceId;

	public GetCompletedTasksCommand() {
	}
	
	public GetCompletedTasksCommand(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}	
	
	public GetCompletedTasksCommand(Date date) {
		this.date = date;
	}
	
    public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

    @Override
	public Long getProcessInstanceId() {
		return processInstanceId;
	}

    @Override
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (date != null) {
        	return context.getTaskAdminService().getCompletedTasks(date);
        } else if (processInstanceId != null) {
        	return context.getTaskAdminService().getCompletedTasksByProcessId(processInstanceId);
        } else {
        	return context.getTaskAdminService().getCompletedTasks();
        }

    }

}
