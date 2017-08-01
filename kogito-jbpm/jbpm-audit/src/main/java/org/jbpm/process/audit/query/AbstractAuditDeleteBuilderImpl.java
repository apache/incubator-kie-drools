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
import org.jbpm.query.jpa.builder.impl.AbstractDeleteBuilderImpl;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Context;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.runtime.manager.audit.query.AuditDeleteBuilder;

import java.sql.Timestamp;
import java.util.Date;

import static org.kie.internal.query.QueryParameterIdentifiers.*;

public abstract class AbstractAuditDeleteBuilderImpl<T> extends AbstractDeleteBuilderImpl<T> implements AuditDeleteBuilder<T> {

    protected static String ONLY_COMPLETED_PROCESS_INSTANCES = 
            " l.processInstanceId in (select spl.processInstanceId \n"
            + "FROM ProcessInstanceLog spl \n"
            + "WHERE spl.status in (2, 3))";
    
    protected final CommandExecutor executor; 
    protected final JPAAuditLogService jpaAuditService; 
    
    protected AbstractAuditDeleteBuilderImpl(JPAAuditLogService jpaService) { 
        this.executor = null;
        this.jpaAuditService = jpaService;
    }
    
    protected AbstractAuditDeleteBuilderImpl(CommandExecutor cmdExecutor) { 
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
    
    @SuppressWarnings("unchecked")
    public T date( Date... date ) {
        if (checkIfNull(date)) {
            return (T) this;
        }
        date = ensureDateNotTimestamp(date);
        addObjectParameter(DATE_LIST, "date", date);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T dateRangeStart( Date rangeStart ) {
        if (checkIfNull(rangeStart)) {
            return (T) this;
        }
        rangeStart = ensureDateNotTimestamp(rangeStart)[0];
        addRangeParameter(DATE_LIST, "date range start", rangeStart, true);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T dateRangeEnd(Date rangeEnd) {
        if (checkIfNull(rangeEnd)) {
            return (T) this;
        }
        rangeEnd = ensureDateNotTimestamp(rangeEnd)[0];
        addRangeParameter(DATE_LIST, "date range end", rangeEnd, false);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T processInstanceId(long... processInstanceId) {
        if (checkIfNull(processInstanceId)) {
            return (T) this;
        }
        addLongParameter(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T processId(String... processId) {
        if (checkIfNull(processId)) {
            return (T) this;
        }
        addObjectParameter(PROCESS_ID_LIST, "process id", processId);
        return (T) this;
    }

    
    protected <T> boolean checkIfNull(T...parameter) {
    	if( parameter == null ) { 
            return true;
        }
        for( int i = 0; i < parameter.length; ++i ) { 
           if( parameter[i] == null ) { 
        	   return true;
           }
        }
        
        return false;
    }
    
    protected Date[] ensureDateNotTimestamp(Date...date) {
		Date[] validated = new Date[date.length];
		for (int i = 0; i < date.length; ++i) {
			if (date[i] instanceof Timestamp) {
				validated[i] = new Date(date[i].getTime());
			} else {
				validated[i] = date[i];
			}
		}
		
		return validated;
    }
 
    abstract protected Class getQueryType();
    
    abstract protected String getQueryBase();
    
    protected String getSubQuery() {
        return null;
    }
    
    public ParametrizedUpdate build() {
        return new ParametrizedUpdate() {
            private QueryWhere queryWhere = new QueryWhere(getQueryWhere());
            @Override
            public int execute() {
                int result = getJpaAuditLogService().doDelete(getQueryBase(), queryWhere, getQueryType(), getSubQuery());
                return result;
            }
        };
    }
}
