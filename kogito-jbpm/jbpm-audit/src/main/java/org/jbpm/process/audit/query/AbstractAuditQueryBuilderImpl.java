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

package org.jbpm.process.audit.query;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.command.AuditCommand;
import org.jbpm.query.jpa.builder.impl.AbstractQueryBuilderImpl;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Context;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.internal.runtime.manager.audit.query.AuditLogQueryBuilder;

import java.util.Date;
import java.util.List;

import static org.kie.internal.query.QueryParameterIdentifiers.*;

@SuppressWarnings("unchecked")
public abstract class AbstractAuditQueryBuilderImpl<T,R> extends AbstractQueryBuilderImpl<T> implements AuditLogQueryBuilder<T,R> {

    protected final CommandExecutor executor; 
    protected final JPAAuditLogService jpaAuditService; 
    
    protected AbstractAuditQueryBuilderImpl(JPAAuditLogService jpaService) { 
        this.executor = null;
        this.jpaAuditService = jpaService;
    }
    
    protected AbstractAuditQueryBuilderImpl(CommandExecutor cmdExecutor) { 
        this.executor = cmdExecutor;
        this.jpaAuditService = null;
    }
   
    // service methods
    
    protected JPAAuditLogService getJpaAuditLogService() { 
        JPAAuditLogService jpaAuditLogService = this.jpaAuditService;
        if( jpaAuditLogService == null ) { 
           jpaAuditLogService = this.executor.execute(getJpaAuditLogServiceCommand);
        }
        return jpaAuditLogService;
    }
    
    private AuditCommand<JPAAuditLogService> getJpaAuditLogServiceCommand = new AuditCommand<JPAAuditLogService>() {
        private static final long serialVersionUID = 101L;
        @Override
        public JPAAuditLogService execute( Context context ) {
            setLogEnvironment(context);
            return (JPAAuditLogService) this.auditLogService;
        }
    };

    // query builder methods
    
    @Override
    public T processInstanceId( long... processInstanceId ) {
        addLongParameter(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return (T) this;
    }

    @Override
    public T processInstanceIdRange( Long processInstanceIdMin, Long processInstanceIdMax ) {
        addRangeParameters(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceIdMin, processInstanceIdMax);
        return (T) this;
    }
    
    @Override
    public T processId( String... processId ) {
        addObjectParameter(PROCESS_ID_LIST, "process id", processId);
        return (T) this;
    }

    public T date( Date... date ) {
        addObjectParameter(DATE_LIST, "date", date);
        return (T) this;
    }

    public T dateRangeStart( Date rangeStart ) {
        addRangeParameter(DATE_LIST, "date range start", rangeStart, true);
        return (T) this;
    }

    public T dateRangeEnd( Date rangeStart ) {
        addRangeParameter(DATE_LIST, "date range end", rangeStart, false);
        return (T) this;
    }

    @Override
    public T ascending( OrderBy field ) {
        String listId = convertOrderByToListId(field);
        this.queryWhere.setAscending(listId);
        return (T) this;
    }
   
    @Override
    public T descending( OrderBy field ) {
        String listId = convertOrderByToListId(field);
        this.queryWhere.setDescending(listId);
        return (T) this;
    }
   
    private String convertOrderByToListId(OrderBy field) { 
        String listId;
        switch( field ) { 
        case processId:
            listId = QueryParameterIdentifiers.PROCESS_ID_LIST;
            break;
        case processInstanceId:
            listId = QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
            break;
        default:
            throw new IllegalArgumentException("Unknown 'order-by' field: " + field.toString() );
        } 
        return listId;
    }
    
    // query builder result methods

    protected abstract Class<R> getResultType();
    protected abstract Class getQueryType();
    
    @Override
    public ParametrizedQuery<R> build() {
        return new ParametrizedQuery<R>() {
            private QueryWhere queryData = new QueryWhere(getQueryWhere()); 
            @Override
            public List<R> getResultList() {
                return getJpaAuditLogService().queryLogs(queryData, getQueryType(), getResultType());
            }
        };
    }

}
