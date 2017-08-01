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
import static org.kie.internal.query.QueryParameterIdentifiers.EXPIRATION_TIME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.MESSAGE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.STACK_TRACE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;

import java.util.Date;
import java.util.List;

import org.jbpm.query.jpa.builder.impl.AbstractQueryBuilderImpl;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.query.jpa.data.QueryWhere.QueryCriteriaType;
import org.kie.api.executor.ErrorInfo;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoQueryBuilder;

public class ErrorInfoQueryBuilderImpl extends AbstractQueryBuilderImpl<ErrorInfoQueryBuilder>  implements ErrorInfoQueryBuilder {

    private final ExecutorJPAAuditService jpaAuditService;
    
     public ErrorInfoQueryBuilderImpl(ExecutorJPAAuditService jpaAuditService) { 
        this.jpaAuditService = jpaAuditService;
     }		

    @Override
    public ErrorInfoQueryBuilder message( String... message ) {
        addObjectParameter(MESSAGE_LIST, "message", message);
        return this;
    }

    @Override
    public ErrorInfoQueryBuilder id( long... id ) {
        addLongParameter(ID_LIST, "id", id);
        return this;
    }

    @Override
    public ErrorInfoQueryBuilder time( Date... time ) {
        addObjectParameter(EXECUTOR_TIME_LIST, "time", time);
        return this;
    }

    @Override
    public ErrorInfoQueryBuilder timeRange( Date timeMin, Date timeMax ) {
        addRangeParameters(EXECUTOR_TIME_LIST, "time", timeMin, timeMax);
        return this;
    }

    @Override
    public ErrorInfoQueryBuilder stackTraceRegex( String... stackTraceRegex ) {
        QueryWhere queryWhere = getQueryWhere();
        QueryCriteriaType origCriteriaType = queryWhere.getCriteriaType();
        
        queryWhere.setToLike();
        addObjectParameter(STACK_TRACE_LIST, "stack trace regex", stackTraceRegex);
        
        switch(origCriteriaType) { 
        case NORMAL:
            queryWhere.setToNormal();
            break;
        case RANGE:
            queryWhere.setToRange();
            break;
        case GROUP:
            queryWhere.setToGroup();
            break;
        case REGEXP:
            // already at like
        }
        return this;
    }

    @Override
    public ErrorInfoQueryBuilder ascending( ErrorInfoQueryBuilder.OrderBy field ) {
        String listId = convertOrderByToListId(field);
        this.queryWhere.setAscending(listId);
        return this;
    }
   
    @Override
    public ErrorInfoQueryBuilder descending( ErrorInfoQueryBuilder.OrderBy field ) {
        String listId = convertOrderByToListId(field);
        this.queryWhere.setDescending(listId);
        return this;
    }
   
    private String convertOrderByToListId(ErrorInfoQueryBuilder.OrderBy field) { 
        String listId;
        switch( field ) { 
        case id:
            listId = QueryParameterIdentifiers.ID_LIST;
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
    public ParametrizedQuery<ErrorInfo> build() {
        return new ParametrizedQuery<ErrorInfo>() {
            private QueryWhere queryData = new QueryWhere(getQueryWhere()); 
            @Override
            public List<ErrorInfo> getResultList() {
                return jpaAuditService.queryLogs(queryData, org.jbpm.executor.entities.ErrorInfo.class, ErrorInfo.class);
            }
        };
    }
}
