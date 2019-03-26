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

package org.jbpm.executor.impl.jpa;

import static org.kie.internal.query.QueryParameterIdentifiers.*;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_EXECUTIONS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_KEY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_OWNER_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_RETRIES_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXPIRATION_TIME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.MESSAGE_LIST;

import java.util.Date;
import java.util.List;

import org.jbpm.query.jpa.builder.impl.AbstractQueryBuilderImpl;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.internal.runtime.manager.audit.query.RequestInfoQueryBuilder;

public class RequestInfoQueryBuilderImpl extends AbstractQueryBuilderImpl<RequestInfoQueryBuilder>  implements RequestInfoQueryBuilder {

    private final ExecutorJPAAuditService jpaAuditService;
    
     public RequestInfoQueryBuilderImpl(ExecutorJPAAuditService jpaAuditService) { 
        this.jpaAuditService = jpaAuditService;
     }		

    @Override
    public RequestInfoQueryBuilder commandName( String... commandName ) {
        addObjectParameter(COMMAND_NAME_LIST, "command name", commandName);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder deploymentId( String... deploymentId ) {
        addObjectParameter(DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder executions( int... executions ) {
        addIntParameter(EXECUTOR_EXECUTIONS_LIST, "executions", executions);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder id( long... id ) {
        addLongParameter(ID_LIST, "id", id);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder key( String... key ) {
        addObjectParameter(EXECUTOR_KEY_LIST, "key", key);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder message( String... message ) {
        addObjectParameter(MESSAGE_LIST, "message", message);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder owner( String... owner ) {
        addObjectParameter(EXECUTOR_OWNER_LIST, "owner", owner);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder retries( int... retries ) {
        addIntParameter(EXECUTOR_RETRIES_LIST, "retries", retries);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder status( STATUS... status ) {
        addObjectParameter(EXECUTOR_STATUS_LIST, "status", status);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder time( Date... time ) {
        addObjectParameter(EXECUTOR_TIME_LIST, "time", time);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder timeRange( Date timeMin, Date timeMax ) {
        addRangeParameters(EXECUTOR_TIME_LIST, "time", timeMin, timeMax);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder ascending( RequestInfoQueryBuilder.OrderBy field ) {
        String listId = convertOrderByToListId(field);
        this.queryWhere.setAscending(listId);
        return this;
    }

    @Override
    public RequestInfoQueryBuilder descending( RequestInfoQueryBuilder.OrderBy field ) {
        String listId = convertOrderByToListId(field);
        this.queryWhere.setDescending(listId);
        return this;
    }

    private String convertOrderByToListId(RequestInfoQueryBuilder.OrderBy field) { 
        String listId;
        switch( field ) { 
        case deploymentId:
            listId = QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
            break;
        case executions:
            listId = QueryParameterIdentifiers.EXECUTOR_EXECUTIONS_LIST;
            break;
        case id:
            listId = QueryParameterIdentifiers.ID_LIST;
            break;
        case retries:
            listId = QueryParameterIdentifiers.EXECUTOR_RETRIES_LIST;
            break;
        case status:
            listId = QueryParameterIdentifiers.EXECUTOR_STATUS_LIST;
            break;
        case time:
            listId = QueryParameterIdentifiers.EXECUTOR_TIME_LIST;
            break;
        default:
            throw new IllegalArgumentException("Unknown 'order-by' field: " + field.toString() );
        } 
        return listId;
    }

    @Override
    public ParametrizedQuery<RequestInfo> build() {
        return new ParametrizedQuery<RequestInfo>() {
            private QueryWhere queryData = new QueryWhere(getQueryWhere()); 
            @Override
            public List<RequestInfo> getResultList() {
                return jpaAuditService.queryLogs(queryData, org.jbpm.executor.entities.RequestInfo.class, RequestInfo.class);
            }
        };
    }

}
