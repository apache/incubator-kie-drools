package org.jbpm.services.task.audit.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.drools.core.command.impl.GenericCommand;

import org.jbpm.services.task.audit.impl.model.api.GroupAuditTask;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="get-group-audit-task-by-id-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetGroupAuditTaskByIdCommand implements GenericCommand<GroupAuditTask> {

       
        private long taskId;
    
	public GetGroupAuditTaskByIdCommand() {
		
	}
	
	public GetGroupAuditTaskByIdCommand(long taskId) {
		this.taskId = taskId;
                
	}
	
	@Override
	public GroupAuditTask execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getGroupAuditTaskById", 
				persistenceContext.addParametersToMap("taskId", taskId),
				ClassUtil.<GroupAuditTask>castClass(List.class));
	}

}
