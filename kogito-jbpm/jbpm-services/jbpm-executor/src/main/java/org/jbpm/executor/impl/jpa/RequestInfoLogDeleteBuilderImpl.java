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

package org.jbpm.executor.impl.jpa;

import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_STATUS_ID;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_TIME_ID;

import java.util.Date;

import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.RequestInfoLogDeleteBuilder;

public class RequestInfoLogDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<RequestInfoLogDeleteBuilder> implements RequestInfoLogDeleteBuilder {
	public static String REQUES_INFO_LOG_DELETE = 
            "DELETE "
            + "FROM RequestInfo l\n";
	
    public RequestInfoLogDeleteBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
        intersect();
    }
  
    public RequestInfoLogDeleteBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
       intersect();
    }

	@Override
	public RequestInfoLogDeleteBuilder date(Date... date) {
		if (checkIfNotNull(date)) {
			return this;
		}
		addObjectParameter(EXECUTOR_TIME_ID, "on date", ensureDateNotTimestamp(date));
		return this;
	}

	@Override
	public RequestInfoLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(EXECUTOR_TIME_ID, "date range end", ensureDateNotTimestamp(rangeStart)[0], true);
		return this;
	}

	@Override
	public RequestInfoLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(EXECUTOR_TIME_ID, "date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
	}

	@Override
	public RequestInfoLogDeleteBuilder deploymentId(String... deploymentId) {
		if (checkIfNotNull(deploymentId)) {
			return this;
		}
		addObjectParameter(DEPLOYMENT_ID, "deployment id", deploymentId);
        return this;
	}

	@Override
	public ParametrizedUpdate build() {		
		return new ParametrizedUpdate() {
			private QueryData queryData = new QueryData(getQueryData());
			@Override
			public int execute() {
				int result = getJpaAuditLogService().doDelete(REQUES_INFO_LOG_DELETE, queryData, RequestInfo.class);
				return result;
			}
		};
	}

	@Override
	public RequestInfoLogDeleteBuilder status(STATUS... status) {
		if (checkIfNotNull(status)) {
			return this;
		}
		
		addObjectParameter(EXECUTOR_STATUS_ID, "status", status);
        return this;
	}


}
