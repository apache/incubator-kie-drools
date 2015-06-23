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

import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_ON_ID;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.AuditTaskInstanceLogDeleteBuilder;

public class AuditTaskInstanceLogDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<AuditTaskInstanceLogDeleteBuilder> implements AuditTaskInstanceLogDeleteBuilder {
	public static String AUDIT_TASK_LOG_DELETE = 
            "DELETE "
            + "FROM AuditTaskImpl l\n";
	
    public AuditTaskInstanceLogDeleteBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
        intersect();
    }
  
    public AuditTaskInstanceLogDeleteBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
       intersect();
    }

	@Override
	public AuditTaskInstanceLogDeleteBuilder processInstanceId(long... processInstanceId) {
		if (checkIfNotNull(processInstanceId)) {
			return this;
		}
		return super.processInstanceId(processInstanceId);
	}

	@Override
	public AuditTaskInstanceLogDeleteBuilder processId(String... processId) {
		if (checkIfNotNull(processId)) {
			return this;
		}
		return super.processId(processId);
	}

	@Override
	public AuditTaskInstanceLogDeleteBuilder date(Date... date) {
		if (checkIfNotNull(date)) {
			return this;
		}
		addObjectParameter(CREATED_ON_ID, "created on date", ensureDateNotTimestamp(date));
		return this;
	}

	@Override
	public AuditTaskInstanceLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(CREATED_ON_ID, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], true);
		return this;
	}

	@Override
	public AuditTaskInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(CREATED_ON_ID, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
	}

	@Override
	public AuditTaskInstanceLogDeleteBuilder deploymentId(String... deploymentId) {
		if (checkIfNotNull(deploymentId)) {
			return this;
		}
		addObjectParameter(DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return this;
	}

	@Override
	public ParametrizedUpdate build() {
		return new ParametrizedUpdate() {
			private QueryData queryData = new QueryData(getQueryData());
			@Override
			public int execute() {
				int result = getJpaAuditLogService().doDelete(AUDIT_TASK_LOG_DELETE, queryData, AuditTaskImpl.class);
				return result;
			}
		};
	}


}
