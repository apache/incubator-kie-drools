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

package org.jbpm.process.audit.query;

import static org.kie.internal.query.QueryParameterIdentifiers.EXTERNAL_ID_LIST;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.VariableInstanceLog;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogDeleteBuilder;

public class VarInstanceLogDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<VariableInstanceLogDeleteBuilder> implements VariableInstanceLogDeleteBuilder {

    public VarInstanceLogDeleteBuilderImpl(CommandExecutor cmdExecutor ) {
        super(cmdExecutor);
        intersect();
    }
  
    public VarInstanceLogDeleteBuilderImpl(JPAAuditLogService jpaAuditService) { 
       super(jpaAuditService);
       intersect();
    }

	@Override
	public VariableInstanceLogDeleteBuilder processInstanceId(long... processInstanceId) {
		if (checkIfNotNull(processInstanceId)) {
			return this;
		}
		return super.processInstanceId(processInstanceId);
	}

	@Override
	public VariableInstanceLogDeleteBuilder processId(String... processId) {
		if (checkIfNotNull(processId)) {
			return this;
		}
		return super.processId(processId);
	}

	@Override
	public VariableInstanceLogDeleteBuilder date(Date... date) {
		if (checkIfNotNull(date)) {
			return this;
		}
		return super.date(ensureDateNotTimestamp(date));
	}

	@Override
	public VariableInstanceLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		return super.dateRangeStart(ensureDateNotTimestamp(rangeStart)[0]);
	}

	@Override
	public VariableInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		return super.dateRangeEnd(ensureDateNotTimestamp(rangeStart)[0]);
	}

	@Override
	public VariableInstanceLogDeleteBuilder externalId(String... externalId) {
		if (checkIfNotNull(externalId)) {
			return this;
		}
		addObjectParameter(EXTERNAL_ID_LIST, "external id", externalId);
        return this;
	}

	@Override
	public ParametrizedUpdate build() {
		return new ParametrizedUpdate() {
			private QueryData queryData = new QueryData(getQueryData());
			@Override
			public int execute() {
				int result = getJpaAuditLogService().doDelete(queryData, VariableInstanceLog.class);
				return result;
			}
		};
	}


}
