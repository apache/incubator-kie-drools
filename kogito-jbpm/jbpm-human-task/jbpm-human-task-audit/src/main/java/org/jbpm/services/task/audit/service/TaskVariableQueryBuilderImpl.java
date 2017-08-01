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
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TYPE_LIST;

import java.util.Date;
import java.util.List;

import org.jbpm.services.task.audit.commands.TaskVariableQueryCommand;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import org.jbpm.services.task.commands.TaskCommand;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskVariable;
import org.kie.internal.task.api.TaskVariable.VariableType;
import org.kie.internal.task.query.TaskVariableQueryBuilder;

public class TaskVariableQueryBuilderImpl extends AbstractTaskAuditQueryBuilderImpl<TaskVariableQueryBuilder, TaskVariable>  implements TaskVariableQueryBuilder {

    public TaskVariableQueryBuilderImpl(InternalTaskService taskService) {
        super(taskService);
     }

     public TaskVariableQueryBuilderImpl(TaskJPAAuditService jpaService) {
        super(jpaService);
     }

    @Override
	public TaskVariableQueryBuilder taskId(long... taskId) {
		addLongParameter(TASK_ID_LIST, "task id", taskId);
        return this;
	}

	@Override
    public TaskVariableQueryBuilder taskIdRange( Long taskIdMin, Long taskIdMax ) {
	    addRangeParameters(TASK_ID_LIST, "task id range", taskIdMin, taskIdMax);
        return this;
    }

    @Override
    public TaskVariableQueryBuilder id( long... id ) {
		addLongParameter(ID_LIST, "task id", id);
        return this;
    }

    @Override
    public TaskVariableQueryBuilder modificationDate( Date... modDate ) {
		addObjectParameter(DATE_LIST, "log time", modDate);
        return this;
    }

    @Override
    public TaskVariableQueryBuilder modificationDateRange( Date modDateMin, Date modDateMax ) {
	    addRangeParameters(DATE_LIST, "log time range", modDateMin, modDateMax);
        return this;
    }

    @Override
    public TaskVariableQueryBuilder name( String... name ) {
	    addObjectParameter(TASK_VARIABLE_NAME_ID_LIST, "name", name);
        return this;
    }

    @Override
    public TaskVariableQueryBuilder value( String... value ) {
		addObjectParameter(TASK_VARIABLE_VALUE_ID_LIST, "value", value);
        return this;
    }

    @Override
    public TaskVariableQueryBuilder type( VariableType... type ) {
		addObjectParameter(TYPE_LIST, "task variable type", type);
        return this;
    }

	@Override
    public TaskVariableQueryBuilder ascending( org.kie.internal.task.query.TaskVariableQueryBuilder.OrderBy field ) {
		String listId = convertOrderByToListId(field);
		this.queryWhere.setAscending(listId);
        return this;
    }

    @Override
    public TaskVariableQueryBuilder descending( org.kie.internal.task.query.TaskVariableQueryBuilder.OrderBy field ) {
		String listId = convertOrderByToListId(field);
		this.queryWhere.setDescending(listId);
        return this;
    }

    private String convertOrderByToListId(org.kie.internal.task.query.TaskVariableQueryBuilder.OrderBy field) {
        String listId;
        switch( field ) {
        case id:
            listId = QueryParameterIdentifiers.ID_LIST;
            break;
        case taskId:
            listId = QueryParameterIdentifiers.TASK_ID_LIST;
            break;
        case modificationDate:
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
    protected Class<TaskVariableImpl> getQueryType() {
        return TaskVariableImpl.class;
    }

    @Override
    protected Class<TaskVariable> getResultType() {
        return TaskVariable.class;
    }

    @Override
    protected TaskCommand<List<TaskVariable>> getCommand() {
        return new TaskVariableQueryCommand(queryWhere);
    }

}
