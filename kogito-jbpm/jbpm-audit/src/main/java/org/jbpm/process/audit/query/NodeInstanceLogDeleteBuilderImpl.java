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

import static org.kie.internal.query.QueryParameterIdentifiers.NODE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXTERNAL_ID_LIST;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogDeleteBuilder;

public class NodeInstanceLogDeleteBuilderImpl extends
		AbstractAuditDeleteBuilderImpl<NodeInstanceLogDeleteBuilder> implements NodeInstanceLogDeleteBuilder {

	public NodeInstanceLogDeleteBuilderImpl(JPAAuditLogService jpaService) {
		super(jpaService);
		intersect();
	}

	public NodeInstanceLogDeleteBuilderImpl(CommandExecutor cmdExecutor) {
		super(cmdExecutor);
		intersect();
	}


	@Override
	public NodeInstanceLogDeleteBuilder processInstanceId(long... processInstanceId) {
		if (checkIfNotNull(processInstanceId)) {
			return this;
		}
		return super.processInstanceId(processInstanceId);
	}

	@Override
	public NodeInstanceLogDeleteBuilder processId(String... processId) {
		if (checkIfNotNull(processId)) {
			return this;
		}
		return super.processId(processId);
	}

	@Override
	public NodeInstanceLogDeleteBuilder date(Date... date) {
		if (checkIfNotNull(date)) {
			return this;
		}
		return super.date(ensureDateNotTimestamp(date));
	}

	@Override
	public NodeInstanceLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		return super.dateRangeStart(ensureDateNotTimestamp(rangeStart)[0]);
	}

	@Override
	public NodeInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		return super.dateRangeEnd(ensureDateNotTimestamp(rangeStart)[0]);
	}

	@Override
	public NodeInstanceLogDeleteBuilder workItemId(long... workItemId) {
		if (checkIfNotNull(workItemId)) {
			return this;
		}
		addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
	}

	@Override
	public NodeInstanceLogDeleteBuilder nodeInstanceId(String... nodeInstanceId) {
		if (checkIfNotNull(nodeInstanceId)) {
			return this;
		}
		addObjectParameter(NODE_INSTANCE_ID_LIST, "node instance id", nodeInstanceId);
        return this;
	}

	@Override
	public NodeInstanceLogDeleteBuilder nodeId(String... nodeId) {
		if (checkIfNotNull(nodeId)) {
			return this;
		}
		addObjectParameter(NODE_ID_LIST, "node id", nodeId);
        return this;
	}

	@Override
	public NodeInstanceLogDeleteBuilder nodeName(String... name) {
		if (checkIfNotNull(name)) {
			return this;
		}
		addObjectParameter(NODE_NAME_LIST, "node name", name);
        return this;
	}	
	
	@Override
	public NodeInstanceLogDeleteBuilder externalId(String... externalId) {
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
				int result = getJpaAuditLogService().doDelete(queryData, NodeInstanceLog.class);
				return result;
			}
		};
	}


}
