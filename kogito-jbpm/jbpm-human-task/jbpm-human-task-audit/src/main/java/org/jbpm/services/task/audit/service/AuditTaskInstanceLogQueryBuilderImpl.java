package org.jbpm.services.task.audit.service;

import static org.kie.internal.query.QueryParameterIdentifiers.TASK_AUDIT_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_DESCRIPTION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.List;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditQueryBuilderImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.AuditTaskInstanceLogQueryBuilder;
import org.kie.internal.task.api.AuditTask;

public class AuditTaskInstanceLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<AuditTaskInstanceLogQueryBuilder>  implements AuditTaskInstanceLogQueryBuilder {

	
	public static String TASK_AUDIT_LOG_QUERY = 
            "SELECT l "
            + "FROM AuditTaskImpl l\n";
	
    public AuditTaskInstanceLogQueryBuilderImpl(CommandExecutor cmdService) { 
        super(cmdService);
     }
     
     public AuditTaskInstanceLogQueryBuilderImpl(JPAAuditLogService jpaAuditService) { 
        super(jpaAuditService);
     }		

	@Override
	public AuditTaskInstanceLogQueryBuilder taskId(long... taskId) {
		addLongParameter(TASK_ID_LIST, "task id", taskId);
        return this;
	}

	@Override
	public AuditTaskInstanceLogQueryBuilder taskName(String... name) {
		addObjectParameter(TASK_NAME_LIST, "task name", name);
        return this;
	}

	@Override
	public AuditTaskInstanceLogQueryBuilder description(String... description) {
		addObjectParameter(TASK_DESCRIPTION_LIST, "task description", description);
        return this;
	}
	
	@Override
	public AuditTaskInstanceLogQueryBuilder taskStatus(String... status) {
		addObjectParameter(TASK_AUDIT_STATUS_LIST, "task status", status);
        return this;
	}

	@Override
	public AuditTaskInstanceLogQueryBuilder workItemId(long... workItemId) {
		addLongParameter(WORK_ITEM_ID_LIST, "work item id", workItemId);
        return this;
	}

	@Override
	public AuditTaskInstanceLogQueryBuilder orderBy(OrderBy field) {
		this.queryData.getQueryContext().setOrderBy(field.toString());
        return this;
	}

	@Override
	public ParametrizedQuery<AuditTask> buildQuery() {
		return new ParametrizedQuery<AuditTask>() {
            private QueryData queryData = new QueryData(getQueryData()); 
            @Override
            public List<AuditTask> getResultList() {
                return getJpaAuditLogService().doQuery(TASK_AUDIT_LOG_QUERY, queryData, AuditTask.class);
            }
        };
	}

}
