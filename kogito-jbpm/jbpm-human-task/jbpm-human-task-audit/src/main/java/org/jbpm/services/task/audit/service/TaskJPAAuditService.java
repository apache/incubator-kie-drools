package org.jbpm.services.task.audit.service;

import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_ON_ID;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_EVENT_DATE_ID;

import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.internal.runtime.manager.audit.query.AuditTaskInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.TaskEventInstanceLogDeleteBuilder;

public class TaskJPAAuditService extends JPAAuditLogService {

	static { 

        addCriteria(CREATED_ON_ID, "l.createdOn", Date.class);
        addCriteria(DEPLOYMENT_ID_LIST, "l.deploymentId", String.class);
        addCriteria(TASK_EVENT_DATE_ID, "l.logTime", Date.class);
	}
	
	public TaskJPAAuditService() {
		super();
	}
	
	public TaskJPAAuditService(EntityManagerFactory emf) {
		super(emf);
	}

	public AuditTaskInstanceLogDeleteBuilder auditTaskInstanceLogDelete(){
		return new AuditTaskInstanceLogDeleteBuilderImpl(this);
	}
	
	public TaskEventInstanceLogDeleteBuilder taskEventInstanceLogDelete(){
		return new TaskEventInstanceLogDeleteBuilderImpl(this);
	}
}
