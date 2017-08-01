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

import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_ON_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DURATION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.END_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.START_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.USER_ID_LIST;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditQueryBuilderImpl;
import org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.model.Status;
import org.kie.internal.query.QueryParameterIdentifiers;

public class BAMTaskSummaryQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<BAMTaskSummaryQueryBuilder, BAMTaskSummaryImpl>  implements BAMTaskSummaryQueryBuilder {

    public BAMTaskSummaryQueryBuilderImpl(CommandExecutor cmdService) { 
        super(cmdService);
     }
     
     public BAMTaskSummaryQueryBuilderImpl(JPAAuditLogService jpaAuditService) { 
        super(jpaAuditService);
     }		

	@Override
	public BAMTaskSummaryQueryBuilder taskId(long... taskId) {
		addLongParameter(TASK_ID_LIST, "task id", taskId);
        return this;
	}

	@Override
    public BAMTaskSummaryQueryBuilder taskIdRange( Long taskIdMin, Long taskIdMax ) {
		addRangeParameters(TASK_ID_LIST, "task id", taskIdMin, taskIdMax);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder startDate( Date... startDate ) {
        addObjectParameter(START_DATE_LIST, "start date", startDate);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder startDateRange( Date startDateMin, Date startDateMax ) {
		addRangeParameters(START_DATE_LIST, "start date", startDateMin, startDateMax);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder duration( long... duration ) {
        addLongParameter(DURATION_LIST, "duration", duration);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder taskStatus( Status... status ) {
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
    public BAMTaskSummaryQueryBuilder userId( String... userId ) {
        addObjectParameter(USER_ID_LIST, "user id", userId);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder endDate( Date... endDate ) {
        addObjectParameter(END_DATE_LIST, "end date", endDate);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder endDateRange( Date endDateMin, Date endDateMax ) {
		addRangeParameters(END_DATE_LIST, "end date", endDateMin, endDateMax);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder createdOn( Date... createdOn ) {
        addObjectParameter(CREATED_ON_LIST, "created on", createdOn);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder createdOnRange( Date createdOnMin, Date createdOnMax ) {
		addRangeParameters(CREATED_ON_LIST, "created on", createdOnMin, createdOnMax);
        return this;
    }

    @Override
	public BAMTaskSummaryQueryBuilder taskName(String... name) {
		addObjectParameter(TASK_NAME_LIST, "task name", name);
        return this;
	}

    @Override
    public BAMTaskSummaryQueryBuilder id( long... id ) {
		addLongParameter(ID_LIST, "id", id);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder ascending( org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder.OrderBy field ) {
		String listId = convertOrderByToListId(field);
		this.queryWhere.setAscending(listId);
        return this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder descending( org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder.OrderBy field ) {
		String listId = convertOrderByToListId(field);
		this.queryWhere.setDescending(listId);
        return this;
    }
    
    private String convertOrderByToListId(org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder.OrderBy field) { 
        String listId;
        switch( field ) { 
        case taskId:
            listId = QueryParameterIdentifiers.TASK_ID_LIST;
            break;
        case startDate:
            listId = QueryParameterIdentifiers.START_DATE_LIST;
            break;
        case endDate:
            listId = QueryParameterIdentifiers.END_DATE_LIST;
            break;
        case createdDate:
            listId = QueryParameterIdentifiers.CREATED_ON_LIST;
            break;
        case taskName:
            listId = QueryParameterIdentifiers.TASK_NAME_LIST;
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
    protected Class<BAMTaskSummaryImpl> getResultType() {
        return BAMTaskSummaryImpl.class;
    }

    @Override
    protected Class<BAMTaskSummaryImpl> getQueryType() {
        return BAMTaskSummaryImpl.class;
    }

}
