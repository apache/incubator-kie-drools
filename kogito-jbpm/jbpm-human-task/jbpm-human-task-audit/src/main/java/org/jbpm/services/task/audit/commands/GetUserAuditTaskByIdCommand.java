package org.jbpm.services.task.audit.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.drools.core.command.impl.GenericCommand;

import org.jbpm.services.task.audit.impl.model.api.UserAuditTask;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="get-user-audit-task-by-id-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetUserAuditTaskByIdCommand implements GenericCommand<UserAuditTask> {

       
        private long taskId;
    
	public GetUserAuditTaskByIdCommand() {
		
	}
	
	public GetUserAuditTaskByIdCommand(long taskId) {
		this.taskId = taskId;
                
	}
	
	@Override
	public UserAuditTask execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", 
				persistenceContext.addParametersToMap("taskId", taskId),
				ClassUtil.<UserAuditTask>castClass(List.class));
	}

}
