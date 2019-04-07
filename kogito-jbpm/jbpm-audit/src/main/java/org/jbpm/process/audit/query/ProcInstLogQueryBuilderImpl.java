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

import static org.kie.internal.query.QueryParameterIdentifiers.CORRELATION_KEY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DURATION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.END_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.IDENTITY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.OUTCOME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_VERSION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.START_DATE_LIST;

import java.util.Date;
import java.util.List;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;

public class ProcInstLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<ProcessInstanceLogQueryBuilder, ProcessInstanceLog> implements ProcessInstanceLogQueryBuilder {

    public ProcInstLogQueryBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
    }

    public ProcInstLogQueryBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
    }
       
    @Override
    public ProcessInstanceLogQueryBuilder status( int... status ) {
        addIntParameter(PROCESS_INSTANCE_STATUS_LIST, "status", status);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder duration( long... duration ) {
        addLongParameter(DURATION_LIST, "duration", duration);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder durationMin( long durationMin ) { 
        addRangeParameter(DURATION_LIST, "duration min", durationMin, true);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder durationMax( long durationMax ) { 
        addRangeParameter(DURATION_LIST, "duration max", durationMax, false);
        return this;
    }
    
    @Override
    public ProcessInstanceLogQueryBuilder identity( String... identity ) {
        addObjectParameter(IDENTITY_LIST, "identity", identity);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder processVersion( String... version ) {
        addObjectParameter(PROCESS_VERSION_LIST, "process version", version);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder processName( String... processName ) {
        addObjectParameter(PROCESS_NAME_LIST, "process name", processName);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDate( Date... date ) {
        addObjectParameter(START_DATE_LIST, "start date", date);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDateRangeStart( Date rangeStart ) {
        addRangeParameter(START_DATE_LIST, "start date range, start", rangeStart, true );
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDateRangeEnd( Date rangeEnd ) {
        addRangeParameter(START_DATE_LIST, "start date range, end", rangeEnd, false );
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDate( Date... date ) {
        addObjectParameter(END_DATE_LIST, "end date", date );
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDateRangeStart( Date rangeStart ) {
        addRangeParameter(END_DATE_LIST, "end date range, start", rangeStart, true);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDateRangeEnd( Date rangeEnd ) {
        addRangeParameter(END_DATE_LIST, "end date range, end", rangeEnd, false);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder outcome( String... outcome ) {
        addObjectParameter(OUTCOME_LIST, "outcome", outcome);
        return this;
    }
    
	@Override
	public ProcessInstanceLogQueryBuilder correlationKey(CorrelationKey... correlationKeys) {
		String[] correlationKeysExternal = new String[correlationKeys.length];
		
		for (int i = 0; i < correlationKeys.length; i++) {
			correlationKeysExternal[i] = correlationKeys[i].toExternalForm();
		}
		
		addObjectParameter(CORRELATION_KEY_LIST, "correlation key", correlationKeysExternal);
		return this;
	}

    @Override
    protected Class<ProcessInstanceLog> getResultType() {
        return ProcessInstanceLog.class;
    }

    @Override
    protected Class getQueryType() {
        return org.jbpm.process.audit.ProcessInstanceLog.class;
    }

}