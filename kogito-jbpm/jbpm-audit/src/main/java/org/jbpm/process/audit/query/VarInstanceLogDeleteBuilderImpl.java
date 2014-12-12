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
		return super.date(date);
	}

	@Override
	public VariableInstanceLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		return super.dateRangeStart(rangeStart);
	}

	@Override
	public VariableInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		return super.dateRangeEnd(rangeStart);
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
