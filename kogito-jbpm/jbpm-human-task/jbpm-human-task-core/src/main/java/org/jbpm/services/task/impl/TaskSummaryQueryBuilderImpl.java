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

package org.jbpm.services.task.impl;

import static org.kie.internal.query.QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ARCHIVED;
import static org.kie.internal.query.QueryParameterIdentifiers.BUSINESS_ADMIN_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_BY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_ON_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXPIRATION_TIME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.POTENTIAL_OWNER_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_SESSION_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.SKIPPABLE;
import static org.kie.internal.query.QueryParameterIdentifiers.STAKEHOLDER_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.SUB_TASKS_STRATEGY;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_DESCRIPTION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_FORM_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_PARENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_PRIORITY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_SUBJECT_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TYPE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.Date;
import java.util.List;

import org.jbpm.query.jpa.builder.impl.AbstractQueryBuilderImpl;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.services.task.commands.TaskSummaryQueryCommand;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;

/**
 * Main Implementation of the {@link TaskSummaryQueryBuilder}. See the {@link TaskSummaryQueryBuilder} interface
 * for more information.
 * </p>
 * This implementation defaults to an ascending orderby of "Id". It's important to
 * have a default ordering of results so that optional ({@link QueryContext}) offset and count
 * parameters then will actually be useful. Without an ordering, subsequent queries can retrieve
 * different randomly ordered lists.
 */
public class TaskSummaryQueryBuilderImpl extends AbstractQueryBuilderImpl<TaskSummaryQueryBuilder> implements TaskSummaryQueryBuilder {

    private final CommandExecutor executor;
    private final String userId;

    // for buiding QueryWhere instances
    public TaskSummaryQueryBuilderImpl() {
        this.userId = null;
        this.executor = null;
    }

    public TaskSummaryQueryBuilderImpl(String userId, TaskService taskService) {
        this.executor = taskService;
        this.userId = userId;

        this.queryWhere.setAscending(QueryParameterIdentifiers.TASK_ID_LIST);
    }

    // Task query builder methods

    @Override
    public TaskSummaryQueryBuilder activationTime( Date... activationTime ) {
        addObjectParameter(TASK_ACTIVATION_TIME_LIST, "activation time", activationTime);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder activationTimeRange( Date activationTimeMin, Date activationTimeMax ) {
        addRangeParameters(TASK_ACTIVATION_TIME_LIST, "activation time range", activationTimeMin, activationTimeMax);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder actualOwner( String... taskOwnerId ) {
        addObjectParameter(ACTUAL_OWNER_ID_LIST, "task owner id", taskOwnerId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder archived( boolean archived ) {
        Short realValue = archived ? new Short((short) 1) : new Short((short) 0);
        addObjectParameter(ARCHIVED, "archived", realValue);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder businessAdmin( String... businessAdminId ) {
        addObjectParameter(BUSINESS_ADMIN_ID_LIST, "business administrator id", businessAdminId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder createdBy( String... createdById ) {
        addObjectParameter(CREATED_BY_LIST, "created by id", createdById);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder createdOn( Date... createdOnDate ) {
        addObjectParameter(CREATED_ON_LIST, "created on", createdOnDate);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder createdOnRange( Date createdOnMin, Date createdOnMax ) {
        addRangeParameters(CREATED_ON_LIST, "created on range", createdOnMin, createdOnMax);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder deploymentId( String... deploymentId ) {
        addObjectParameter(DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder description( String... description ) {
        addObjectParameter(TASK_DESCRIPTION_LIST, "description", description);
        for( String desc : description ) {
            if( desc.length() > 255 ) {
                throw new IllegalArgumentException("String argument is longer than 255 characters: [" + desc + "]");
            }
        }
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder expirationTime( Date... expirationTime ) {
        addObjectParameter(EXPIRATION_TIME_LIST, "expiration time", expirationTime);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder expirationTimeRange( Date expirationTimeMin, Date expirationTimeMax ) {
        addRangeParameters(EXPIRATION_TIME_LIST, "expiration time range", expirationTimeMin, expirationTimeMax);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder formName( String... formName ) {
        addObjectParameter(TASK_FORM_NAME_LIST, "form name", formName);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder potentialOwner( String... potentialOwnerId ) {
        addObjectParameter(POTENTIAL_OWNER_ID_LIST, "potential owner id", potentialOwnerId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder processInstanceId( long... processInstanceId ) {
        addLongParameter(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder name( String... names ) {
        addObjectParameter(TASK_NAME_LIST, "task name", names);
        for( String name : names ) {
            if( name.length() > 255 ) {
                throw new IllegalArgumentException("String argument is longer than 255 characters: [" + name + "]");
            }
        }
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder priority( int... priority ) {
        addIntParameter(TASK_PRIORITY_LIST, "priority", priority);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder processId( String... processId ) {
        addObjectParameter(PROCESS_ID_LIST, "process id", processId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder processInstanceIdRange( Long processInstanceIdMin, Long processInstanceIdMax ) {
        addRangeParameters(PROCESS_INSTANCE_ID_LIST, "process instance id range", processInstanceIdMin, processInstanceIdMax);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder processSessionId( long... processSessionId ) {
        addLongParameter(PROCESS_SESSION_ID_LIST, "process session id", processSessionId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder skippable( boolean skippable ) {
        addObjectParameter(SKIPPABLE, "skippable", skippable);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder stakeHolder( String... stakeHolderId ) {
        addObjectParameter(STAKEHOLDER_ID_LIST, "stakeholder id", stakeHolderId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder status( Status... status ) {
        addObjectParameter(TASK_STATUS_LIST, "status", status);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder subject( String... subjects ) {
        addObjectParameter(TASK_SUBJECT_LIST, "subject", subjects);
        for( String subject : subjects ) {
            if( subject.length() > 255 ) {
                throw new IllegalArgumentException("String argument is longer than 255 characters: [" + subject + "]");
            }
        }
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder subTaskStrategy( SubTasksStrategy... subTasksStrategy ) {
        addObjectParameter(SUB_TASKS_STRATEGY, "sub tasks strategy", subTasksStrategy);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder taskId( long... taskId ) {
        addLongParameter(TASK_ID_LIST, "task id", taskId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder taskIdRange( Long taskIdMin, Long taskIdMax ) {
        addRangeParameters(TASK_ID_LIST, "task id range", taskIdMin, taskIdMax);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder taskParentId( long... taskParentId ) {
        addLongParameter(TASK_PARENT_ID_LIST, "task parent id", taskParentId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder taskType( String... taskType ) {
        addObjectParameter(TYPE_LIST, "created on", taskType);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder variableName( String... varName ) {
        addObjectParameter(TASK_VARIABLE_NAME_ID_LIST, "task variable name", varName);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder variableValue( String... varValue ) {
        addObjectParameter(TASK_VARIABLE_VALUE_ID_LIST, "task variable value", varValue);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder workItemId( long... workItemId ) {
        addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
    }

    // Other methods

    @Override
    public TaskSummaryQueryBuilder clear() {
        super.clear();
        getQueryWhere().setAscending(QueryParameterIdentifiers.TASK_ID_LIST);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder ascending( OrderBy field ) {
        String listId = getOrderByListId(field);
        this.queryWhere.setAscending(listId);
        return this;
    }

    @Override
    public TaskSummaryQueryBuilder descending( OrderBy field ) {
        String listId = getOrderByListId(field);
        this.queryWhere.setDescending(listId);
        return this;
    }

    private String getOrderByListId( OrderBy field ) {
        if( field == null ) {
            throw new IllegalArgumentException( "A null order by criteria is invalid." );
        }
        String orderByString;
        switch( field ) {
        case taskId:
            orderByString = QueryParameterIdentifiers.TASK_ID_LIST;
            break;
        case processInstanceId:
            orderByString = QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
            break;
        case taskName:
            orderByString = QueryParameterIdentifiers.TASK_NAME_LIST;
            break;
        case taskStatus:
            orderByString = QueryParameterIdentifiers.TASK_STATUS_LIST;
            break;
        case createdOn:
            orderByString = QueryParameterIdentifiers.CREATED_ON_LIST;
            break;
        case createdBy:
            orderByString = QueryParameterIdentifiers.CREATED_BY_LIST;
            break;
        default:
           throw new UnsupportedOperationException("Unsupported order by arqument: " + field.toString() );
        }
        return orderByString;
    }

    @Override
    public ParametrizedQuery<TaskSummary> build() {
        return new ParametrizedQuery<TaskSummary>() {
            private QueryWhere queryWhere = new QueryWhere(getQueryWhere());
            @Override
            public List<TaskSummary> getResultList() {
                TaskSummaryQueryCommand cmd = new TaskSummaryQueryCommand(queryWhere);
                cmd.setUserId(userId);
                return executor.execute(cmd);
            }
        };
    }

}
