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

package org.jbpm.services.task.audit.service;

import static org.kie.internal.query.QueryParameterIdentifiers.DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.MESSAGE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TYPE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.USER_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditQueryBuilderImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.internal.task.api.model.TaskEvent.TaskEventType;
import org.kie.internal.task.query.TaskEventQueryBuilder;

public class TaskEventQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<TaskEventQueryBuilder, TaskEvent>  implements TaskEventQueryBuilder {

    public TaskEventQueryBuilderImpl(CommandExecutor cmdService) { 
        super(cmdService);
     }
     
     public TaskEventQueryBuilderImpl(JPAAuditLogService jpaAuditService) { 
        super(jpaAuditService);
     }		

	@Override
    public TaskEventQueryBuilder message(String... name) {
    	addObjectParameter(MESSAGE_LIST, "message", name);
        return this;
    }

    @Override
	public TaskEventQueryBuilder taskId(long... taskId) {
		addLongParameter(TASK_ID_LIST, "task id", taskId);
        return this;
	}

	@Override
    public TaskEventQueryBuilder taskIdRange( Long taskIdMin, Long taskIdMax ) {
	    addRangeParameters(TASK_ID_LIST, "task id range", taskIdMin, taskIdMax);
        return this;
    }

    @Override
    public TaskEventQueryBuilder id( long... id ) {
		addLongParameter(ID_LIST, "task id", id);
        return this;
    }

    @Override
    public TaskEventQueryBuilder logTime( Date... logTime ) {
		addObjectParameter(DATE_LIST, "log time", logTime);
        return this;
    }

    @Override
    public TaskEventQueryBuilder logTimeRange( Date logTimeMin, Date logTimeMax ) {
	    addRangeParameters(DATE_LIST, "log time range", logTimeMin, logTimeMax);
        return this;
    }

    @Override
    public TaskEventQueryBuilder userId( String... userId ) {
		addObjectParameter(USER_ID_LIST, "user id", userId);
        return this;
    }

    @Override
    public TaskEventQueryBuilder workItemId(long... workItemId) {
    	addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
    }

    @Override
    public TaskEventQueryBuilder type( TaskEventType... taskEventType ) {
		addObjectParameter(TYPE_LIST, "task event type", taskEventType);
        return this;
    }

	@Override
    public TaskEventQueryBuilder ascending( org.kie.internal.task.query.TaskEventQueryBuilder.OrderBy field ) {
		String listId = convertOrderByToListId(field);
		this.queryWhere.setAscending(listId);
        return this;
    }

    @Override
    public TaskEventQueryBuilder descending( org.kie.internal.task.query.TaskEventQueryBuilder.OrderBy field ) {
		String listId = convertOrderByToListId(field);
		this.queryWhere.setDescending(listId);
        return this;
    }
    
    private String convertOrderByToListId(org.kie.internal.task.query.TaskEventQueryBuilder.OrderBy field) { 
        String listId;
        switch( field ) { 
        case taskId:
            listId = QueryParameterIdentifiers.TASK_ID_LIST;
            break;
        case logTime:
            listId = QueryParameterIdentifiers.DATE_LIST;
            break;
        case processInstanceId:
            listId = QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
            break;
        default:
            throw new IllegalArgumentException("Unknown 'order-by' field: " + field.toString() );
        } 
        return listId;
    }

    @Override
    protected Class<TaskEvent> getResultType() {
        return TaskEvent.class;
    }

    @Override
    protected Class<TaskEventImpl> getQueryType() {
        return TaskEventImpl.class;
    }

}
