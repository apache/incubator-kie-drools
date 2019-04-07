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

import static org.kie.internal.query.QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_BY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_ON_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_DESCRIPTION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_DUE_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_PARENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_PRIORITY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_PROCESS_SESSION_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditQueryBuilderImpl;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.query.AuditTaskQueryBuilder;

public class AuditTaskQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<AuditTaskQueryBuilder, AuditTask>  implements AuditTaskQueryBuilder {

    public AuditTaskQueryBuilderImpl(CommandExecutor cmdService) { 
        super(cmdService);
     }
     
     public AuditTaskQueryBuilderImpl(JPAAuditLogService jpaAuditService) { 
        super(jpaAuditService);
     }		

	@Override
	public AuditTaskQueryBuilder taskId(long... taskId) {
		addLongParameter(TASK_ID_LIST, "task id", taskId);
        return this;
	}

	@Override
    public AuditTaskQueryBuilder taskIdRange( Long taskIdMin, Long taskIdMax ) {
        long [] params = { taskIdMin, taskIdMax };
        addRangeParameters(TASK_ID_LIST, "task id", taskIdMin, taskIdMax );
        return this;
    }

    @Override
    public AuditTaskQueryBuilder taskStatus(Status... status) {
        String [] stringStatuses = null;
        if( status != null ) { 
            stringStatuses = new String[status.length];
            for( int i = 0; i < status.length; ++i ) { 
                stringStatuses[i] = status[i].toString();
            }
        }
    	addObjectParameter(TASK_STATUS_LIST, "task status", stringStatuses);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder actualOwner( String... actualOwnerUserId ) {
        addObjectParameter(ACTUAL_OWNER_ID_LIST, "actual owner", actualOwnerUserId);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder deploymentId( String... deploymentId ) {
        addObjectParameter(DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder id( long... id ) {
        addLongParameter(ID_LIST, "id", id);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder createdOn( Date... createdOn ) {
        addObjectParameter(CREATED_ON_LIST, "created on", createdOn);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder createdOnRange( Date createdOnMin, Date createdOnMax ) {
        addRangeParameters(CREATED_ON_LIST, "created on", createdOnMin, createdOnMax);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder taskParentId( long... parentId ) {
        addLongParameter(TASK_PARENT_ID_LIST, "parent id", parentId);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder createdBy( String... createdByUserId ) {
        addObjectParameter(CREATED_BY_LIST, "created by", createdByUserId);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder activationTime( Date... activationTime ) {
        addObjectParameter(TASK_ACTIVATION_TIME_LIST, "activation time", activationTime);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder activationTimeRange( Date activationTimeMin, Date activationTimeMax ) {
        addRangeParameters(TASK_ACTIVATION_TIME_LIST, "activation time", activationTimeMin, activationTimeMax);
        return this;
    }

    @Override
	public AuditTaskQueryBuilder taskName(String... name) {
		addObjectParameter(TASK_NAME_LIST, "task name", name);
        return this;
	}

	@Override
	public AuditTaskQueryBuilder description(String... description) {
		addObjectParameter(TASK_DESCRIPTION_LIST, "task description", description);
        return this;
	}
	
	@Override
	public AuditTaskQueryBuilder workItemId(long... workItemId) {
		addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
	}

    @Override
    public AuditTaskQueryBuilder priority( int... priority ) {
        addIntParameter(TASK_PRIORITY_LIST, "priority", priority);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder processSessionId( long... processSessionId ) {
        addLongParameter(TASK_PROCESS_SESSION_ID_LIST, "priority session id", processSessionId);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder dueDate( Date... dueDate ) {
        addObjectParameter(TASK_DUE_DATE_LIST, "due date", dueDate);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder dueDateRange( Date dueDateMin, Date dueDateMax ) {
        addRangeParameters(TASK_DUE_DATE_LIST, "due date", dueDateMin, dueDateMax);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder ascending( org.kie.internal.task.query.AuditTaskQueryBuilder.OrderBy field ) {
    	String listId = convertOrderByToListId(field);
    	this.queryWhere.setAscending(listId);
        return this;
    }

    @Override
    public AuditTaskQueryBuilder descending( org.kie.internal.task.query.AuditTaskQueryBuilder.OrderBy field ) {
    	String listId = convertOrderByToListId(field);
    	this.queryWhere.setDescending(listId);
        return this;
    }

    private String convertOrderByToListId(org.kie.internal.task.query.AuditTaskQueryBuilder.OrderBy field) { 
        String listId;
        switch( field ) { 
        case activationTime:
            listId = TASK_ACTIVATION_TIME_LIST;
            break;
        case taskId:
            listId = TASK_ID_LIST;
            break;
        case createdOn:
            listId = CREATED_ON_LIST;
            break;
        case processId:
            listId = PROCESS_ID_LIST;
            break;
        case processInstanceId:
            listId = PROCESS_INSTANCE_ID_LIST;
            break;
        default:
            throw new IllegalArgumentException("Unknown 'order-by' field: " + field.toString() );
        } 
        return listId;
    }

    @Override
    protected Class<AuditTask> getResultType() {
        return AuditTask.class;
    }

    @Override
    protected Class<AuditTaskImpl> getQueryType() {
        return AuditTaskImpl.class;
    }

}
