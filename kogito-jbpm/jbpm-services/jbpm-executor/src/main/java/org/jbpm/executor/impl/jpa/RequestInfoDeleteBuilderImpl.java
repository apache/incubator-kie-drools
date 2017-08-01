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

import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_TIME_LIST;

import java.util.Date;

import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.runtime.manager.audit.query.RequestInfoLogDeleteBuilder;

public class RequestInfoDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<RequestInfoLogDeleteBuilder> implements RequestInfoLogDeleteBuilder {
    
	private static String REQUES_INFO_LOG_DELETE = 
            "DELETE\n"
            + "FROM RequestInfo l\n";
	
    public RequestInfoDeleteBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
        intersect();
    }
  
    public RequestInfoDeleteBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
       intersect();
    }

	@Override
	public RequestInfoLogDeleteBuilder date(Date... date) {
		if (checkIfNull(date)) {
			return this;
		}
		addObjectParameter(EXECUTOR_TIME_LIST, "on date", ensureDateNotTimestamp(date));
		return this;
	}

	@Override
	public RequestInfoLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNull(rangeStart)) {
			return this;
		}
		addRangeParameter(EXECUTOR_TIME_LIST, "date range end", ensureDateNotTimestamp(rangeStart)[0], true);
		return this;
	}

	@Override
	public RequestInfoLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNull(rangeStart)) {
			return this;
		}
		addRangeParameter(EXECUTOR_TIME_LIST, "date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
	}

	@Override
	public RequestInfoLogDeleteBuilder deploymentId(String... deploymentId) {
		if (checkIfNull(deploymentId)) {
			return this;
		}
		addObjectParameter(DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return this;
	}

	@Override
	public RequestInfoLogDeleteBuilder status(STATUS... status) {
		if (checkIfNull(status)) {
			return this;
		}
		
		addObjectParameter(EXECUTOR_STATUS_LIST, "status", status);
        return this;
	}

    @Override
    protected Class getQueryType() {
        return RequestInfo.class;
    }

    @Override
    protected String getQueryBase() {
        return REQUES_INFO_LOG_DELETE;
    }


}
