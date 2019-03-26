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

import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.List;

@XmlRootElement(name="get-tasks-for-process-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksForProcessCommand extends TaskCommand<List<TaskSummary>> implements ProcessInstanceIdCommand {

	private static final long serialVersionUID = -3784821014329573243L;
	
	@XmlElement(name="process-instance-id")
	@XmlSchemaType(name="long")
	private Long processInstanceId;
	
	@XmlElement
	private List<Status> statuses;
	
	@XmlElement(name="language")
    @XmlSchemaType(name="string")
	private String language;
	
	public GetTasksForProcessCommand() {
		
	}
	
	public GetTasksForProcessCommand(Long processInstanceId, List<Status> statuses, String language) {
		this.processInstanceId = processInstanceId;
		this.statuses = statuses;
		this.language = language;
	}

	@Override
	public Long getProcessInstanceId() {
        return processInstanceId;
    }

	@Override
    public void setProcessInstanceId( Long processInstanceId ) {
        this.processInstanceId = processInstanceId;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses( List<Status> statuses ) {
        this.statuses = statuses;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage( String language ) {
        this.language = language;
    }

    @Override
	public List<TaskSummary> execute(Context context) {
		TaskContext ctx = (TaskContext) context;
		
		TaskPersistenceContext persistenceContext = ctx.getPersistenceContext();
		
		List<TaskSummary> tasks = (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessId",
                persistenceContext.addParametersToMap("processInstanceId", processInstanceId, 
                                        "status", statuses),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
    
        return tasks;
	}
	
}
