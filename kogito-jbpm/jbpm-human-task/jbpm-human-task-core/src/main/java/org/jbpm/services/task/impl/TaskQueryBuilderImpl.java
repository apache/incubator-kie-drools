package org.jbpm.services.task.impl;

import static org.kie.internal.query.QueryParameterIdentifiers.*;
import static org.kie.internal.query.QueryParameterIdentifiers.BUSINESS_ADMIN_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.LANGUAGE;
import static org.kie.internal.query.QueryParameterIdentifiers.POTENTIAL_OWNER_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.List;

import org.drools.core.command.CommandService;
import org.jbpm.services.task.commands.TaskQueryDataCommand;
import org.kie.api.query.ParametrizedQuery;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.query.TaskQueryBuilder;
import org.kie.internal.query.AbstractQueryBuilderImpl;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.data.QueryData;

/**
 * Main Implementation of the {@link TaskQueryBuilder}. See the {@link TaskQueryBuilder} interface 
 * for more information.
 * </p>
 * This implementation defaults to an ascending orderby of "Id". It's important to 
 * have a default ordering of results so that optional ({@link QueryContext}) offset and count 
 * parameters then will actually be useful. Without an ordering, subsequent queries can retrieve
 * different randomly ordered lists. 
 */
public class TaskQueryBuilderImpl extends AbstractQueryBuilderImpl<TaskQueryBuilder> implements TaskQueryBuilder {

    private final CommandService executor; 
    private final String userId;
   
    public TaskQueryBuilderImpl(String userId, CommandService taskCmdService) {
        this.executor = taskCmdService;
        this.userId = userId;
        
        this.queryData.getQueryContext().setAscending(true);
        this.queryData.getQueryContext().setOrderBy("Id");
    }
   
    // Task query builder methods
    
    @Override
    public TaskQueryBuilder workItemId( long... workItemId ) {
        addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
    }

    @Override
    public TaskQueryBuilder taskId( long... taskId ) {
        addLongParameter(TASK_ID_LIST, "task id", taskId);
        return this;
    }

    @Override
    public TaskQueryBuilder processInstanceId( long... processInstanceId ) {
        addLongParameter(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return this;
    }

    @Override
    public TaskQueryBuilder initiator( String... createdById ) {
        addObjectParameter(CREATED_BY_LIST, "created by id", createdById);
        return this;
    }

    @Override
    public TaskQueryBuilder stakeHolder( String... stakeHolderId ) {
        addObjectParameter(STAKEHOLDER_ID_LIST, "stakeholder id", stakeHolderId);
        return this;
    }

    @Override
    public TaskQueryBuilder potentialOwner( String... potentialOwnerId ) {
        addObjectParameter(POTENTIAL_OWNER_ID_LIST, "potential owner id", potentialOwnerId);
        return this;
    }

    @Override
    public TaskQueryBuilder taskOwner( String... taskOwnerId ) {
        addObjectParameter(ACTUAL_OWNER_ID_LIST, "task owner id", taskOwnerId);
        return this;
    }

    @Override
    public TaskQueryBuilder businessAdmin( String... businessAdminId ) {
        addObjectParameter(BUSINESS_ADMIN_ID_LIST, "business administrator id", businessAdminId);
        return this;
    }

    @Override
    public TaskQueryBuilder status( Status... status ) {
        addObjectParameter(STATUS_LIST, "status", status);
        return this;
    }

    @Override
    public TaskQueryBuilder deploymentId( String... deploymentId ) {
        addObjectParameter(DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return this;
    }

    @Override
    public TaskQueryBuilder language( String language ) {
        if( language == null || language.isEmpty() ) { 
            StringBuilder msg = new StringBuilder( (language == null ? "A null" : "An empty") );
            throw new IllegalArgumentException( msg.append(" language criteria is invalid.").toString() );
        }
        List<String> languages = this.queryData.getAppropriateParamList(LANGUAGE, language, 1);
        if( languages.isEmpty() ) { 
            languages.add(language);
        } else { 
            languages.set(0, language);
        }
        return this;
    }

    @Override
    public TaskQueryBuilder orderBy( OrderBy orderBy ) {
        if( orderBy == null ) { 
            throw new IllegalArgumentException( "A null order by criteria is invalid." );
        }
        String orderByString;
        switch( orderBy ) { 
        case taskId:
            orderByString = "t.id";
            break;
        case processInstanceId:
            orderByString = "t.taskData.processInstanceId";
            break;
        case taskName:
            orderByString = "t.name";
            break;
        case taskStatus:
            orderByString = "t.taskData.status";
            break;
        case createdOn:
            orderByString = "t.taskData.createdOn";
            break;
        case createdBy:
            orderByString = "t.taskData.createdBy.id";
            break;
        default:
           throw new UnsupportedOperationException("Unsupported order by arqument: " + orderBy.toString() );
        }
        this.queryData.getQueryContext().setOrderBy(orderByString);
        return this;
    }

    @Override
    public TaskQueryBuilder clear() {
        super.clear();
        getQueryData().getQueryContext().setAscending(true);
        getQueryData().getQueryContext().setOrderBy("Id");
        return this;
    }

    @Override
    public ParametrizedQuery<TaskSummary> buildQuery() {
        return new ParametrizedQuery<TaskSummary>() {
            private QueryData queryData = new QueryData(getQueryData());
            @Override
            public List<TaskSummary> getResultList() {
                TaskQueryDataCommand cmd = new TaskQueryDataCommand(queryData);
                cmd.setUserId(userId);
                return executor.execute(cmd);
            }
        };
    }

}
