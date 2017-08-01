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

package org.jbpm.services.task.audit.commands;

import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.runtime.Context;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.TaskEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.List;

@XmlRootElement(name="get-task-audit-events-by-processinstanceid-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAuditEventsByProcessInstanceIdCommand extends TaskCommand<List<TaskEvent>> implements ProcessInstanceIdCommand {

	private static final long serialVersionUID = -7929370526623674312L;

	@XmlElement
	private QueryFilter filter;

	@XmlElement
	@XmlSchemaType(name="long")
	private Long processInstanceId;
        
	public GetAuditEventsByProcessInstanceIdCommand() {
	    this.filter = new QueryFilter(0,0);
	}
	
	public GetAuditEventsByProcessInstanceIdCommand(long processInstanceId, QueryFilter filter) {
	    this.processInstanceId = processInstanceId;
	    this.filter = filter;
	}
       
	public QueryFilter getFilter() {
        return filter;
    }

    public void setFilter( QueryFilter filter ) {
        this.filter = filter;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId( Long processInstanceId ) {
        this.processInstanceId = processInstanceId;
    }

    @Override
	public List<TaskEvent> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		// @formatter:off
		return persistenceContext.queryWithParametersInTransaction(
		        "getAllTasksEventsByProcessInstanceId", 
		        persistenceContext.addParametersToMap(
		                "processInstanceId", 
		                processInstanceId, 
		                "firstResult", 
		                filter.getOffset(), 
		                "maxResults", 
		                filter.getCount()),
		        ClassUtil.<List<TaskEvent>>castClass(List.class));
		// @formatter:on
	}

}
