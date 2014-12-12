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
		addObjectParameter(TASK_EVENT_DATE_ID, "created on date", date);
		return this;
	}

	@Override
	public TaskEventInstanceLogDeleteBuilder dateRangeStart(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(TASK_EVENT_DATE_ID, "created on date range end", rangeStart, true);
		return this;
	}

	@Override
	public TaskEventInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart) {
		if (checkIfNotNull(rangeStart)) {
			return this;
		}
		addRangeParameter(TASK_EVENT_DATE_ID, "created on date range end", rangeStart, false);
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
