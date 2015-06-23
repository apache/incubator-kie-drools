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

import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_TIME_ID;

import java.util.Date;

import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoLogDeleteBuilder;

public class ErrorInfoLogDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<ErrorInfoLogDeleteBuilder> implements ErrorInfoLogDeleteBuilder {
	public static String ERROR_INFO_LOG_DELETE = 
            "DELETE "
            + "FROM ErrorInfo l\n";
	
    public ErrorInfoLogDeleteBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
        intersect();
    }
  
    public ErrorInfoLogDeleteBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
       intersect();
    }

	@Override
	public ErrorInfoLogDeleteBuilder date(Date... date) {
		if (checkIfNotNull(date)) {
			return this;
		}

		addObjectParameter(EXECUTOR_TIME_ID, "date", ensureDateNotTimestamp(date));
		return this;
	}

	@Override
	public ErrorInfoLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(EXECUTOR_TIME_ID, "date range end", ensureDateNotTimestamp(rangeStart)[0], true);
		return this;
	}

	@Override
	public ErrorInfoLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(EXECUTOR_TIME_ID, "date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
	}

	@Override
	public ParametrizedUpdate build() {
		intersect();
		return new ParametrizedUpdate() {
			private QueryData queryData = new QueryData(getQueryData());
			@Override
			public int execute() {
				int result = getJpaAuditLogService().doDelete(ERROR_INFO_LOG_DELETE, queryData, ErrorInfo.class);
				return result;
			}
		};
	}
}
