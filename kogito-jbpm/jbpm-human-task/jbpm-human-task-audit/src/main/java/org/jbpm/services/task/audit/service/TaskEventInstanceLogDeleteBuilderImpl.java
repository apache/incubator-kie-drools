/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.services.task.audit.service;

import static org.kie.internal.query.QueryParameterIdentifiers.TASK_EVENT_DATE_ID;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.TaskEventInstanceLogDeleteBuilder;

public class TaskEventInstanceLogDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<TaskEventInstanceLogDeleteBuilder> implements TaskEventInstanceLogDeleteBuilder {

	public static String TASK_EVENT_LOG_DELETE = 
	            "DELETE "
	            + "FROM TaskEventImpl l\n";
	
    public TaskEventInstanceLogDeleteBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
        intersect();
    }
  
    public TaskEventInstanceLogDeleteBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
       intersect();
    }

	@Override
	public TaskEventInstanceLogDeleteBuilder processInstanceId(long... processInstanceId) {
		if (checkIfNotNull(processInstanceId)) {
			return this;
		}
		return super.processInstanceId(processInstanceId);
	}

	@Override
	public TaskEventInstanceLogDeleteBuilder processId(String... processId) {
		if (checkIfNotNull(processId)) {
			return this;
		}
		return super.processId(processId);
	}

	@Override
	public TaskEventInstanceLogDeleteBuilder date(Date... date) {
		if (checkIfNotNull(date)) {
			return this;
		}
		addObjectParameter(TASK_EVENT_DATE_ID, "created on date", ensureDateNotTimestamp(date));
		return this;
	}

	@Override
	public TaskEventInstanceLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(TASK_EVENT_DATE_ID, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], true);
		return this;
	}

	@Override
	public TaskEventInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(TASK_EVENT_DATE_ID, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
	}


	@Override
	public ParametrizedUpdate build() {
		return new ParametrizedUpdate() {
			private QueryData queryData = new QueryData(getQueryData());
			@Override
			public int execute() {
				int result = getJpaAuditLogService().doDelete(TASK_EVENT_LOG_DELETE, queryData, VariableInstanceLog.class);
				return result;
			}
		};
	}


}
