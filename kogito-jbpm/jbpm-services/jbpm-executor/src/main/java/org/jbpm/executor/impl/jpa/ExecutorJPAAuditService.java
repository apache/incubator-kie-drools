package org.jbpm.executor.impl.jpa;

import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_STATUS_ID;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_TIME_ID;

import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.audit.service.TaskJPAAuditService;
import org.kie.internal.executor.api.STATUS;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.RequestInfoLogDeleteBuilder;

public class ExecutorJPAAuditService extends TaskJPAAuditService {
	
	static { 

        addCriteria(EXECUTOR_TIME_ID, "l.time", Date.class);
        addCriteria(EXECUTOR_STATUS_ID, "l.status", STATUS.class);
	}

	public ExecutorJPAAuditService(EntityManagerFactory emf) {
		super(emf);
	}
	
	public ErrorInfoLogDeleteBuilder errorInfoLogDeleteBuilder() {
		return new ErrorInfoLogDeleteBuilderImpl(this);
	}

	public RequestInfoLogDeleteBuilder requestInfoLogDeleteBuilder() {
		return new RequestInfoLogDeleteBuilderImpl(this);
	}
}
