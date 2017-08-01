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

import static org.kie.internal.query.QueryParameterIdentifiers.TASK_EVENT_DATE_ID_LIST;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.task.query.TaskEventDeleteBuilder;

public class TaskEventDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<TaskEventDeleteBuilder> implements TaskEventDeleteBuilder {

    private static String TASK_EVENT_IMPL_DELETE = 
            "DELETE\n"
            + "FROM TaskEventImpl l\n";
    
    public TaskEventDeleteBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
        intersect();
    }
  
    public TaskEventDeleteBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
       intersect();
    }

	@Override
	public TaskEventDeleteBuilder date(Date... date) {
		if (checkIfNull(date)) {
			return this;
		}
		addObjectParameter(TASK_EVENT_DATE_ID_LIST, "created on date", ensureDateNotTimestamp(date));
		return this;
	}

	@Override
	public TaskEventDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNull(rangeStart)) {
			return this;
		}
		addRangeParameter(TASK_EVENT_DATE_ID_LIST, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], true);
		return this;
	}

	@Override
	public TaskEventDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNull(rangeStart)) {
			return this;
		}
		addRangeParameter(TASK_EVENT_DATE_ID_LIST, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
	}

    @Override
    protected Class getQueryType() {
        return TaskEventImpl.class;
    }

    @Override
    protected String getQueryBase() {
        return TASK_EVENT_IMPL_DELETE;
    }

    @Override
    protected String getSubQuery() {
        return ONLY_COMPLETED_PROCESS_INSTANCES;
    }
}
