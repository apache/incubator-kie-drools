package org.jbpm.services.task.audit.service;

import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_ON_ID;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
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
		addObjectParameter(CREATED_ON_ID, "created on date", date);
		return this;
	}

	@Override
	public AuditTaskInstanceLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(CREATED_ON_ID, "created on date range end", rangeStart, true);
		return this;
	}

	@Override
	public AuditTaskInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(CREATED_ON_ID, "created on date range end", rangeStart, false);
        return this;
	}

	@Override
	public AuditTaskInstanceLogDeleteBuilder deploymentId(String... deploymentId) {
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
				int result = getJpaAuditLogService().doDelete(AUDIT_TASK_LOG_DELETE, queryData, VariableInstanceLog.class);
				return result;
			}
		};
	}


}
