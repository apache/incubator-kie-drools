package org.jbpm.services.task.audit.commands;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.audit.impl.model.api.UserAuditTask;
import org.jbpm.services.task.commands.PaginatedTaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="get-all-user-audit-tasks-byduedate-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllUserAuditTasksByDueDateCommand extends PaginatedTaskCommand<List<UserAuditTask>> {

        private Date dueDate;
	public GetAllUserAuditTasksByDueDateCommand() {
		
	}
	
	public GetAllUserAuditTasksByDueDateCommand(String userId, Date dueDate, int offset, int count) {
		this.userId = userId;
                this.dueDate = dueDate;
                this.offset = offset;
                this.count = count;
	}
	
	@Override
	public List<UserAuditTask> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getAllUserAuditTasksByDueDate", 
				persistenceContext.addParametersToMap("userId", userId, "dueDate", dueDate,
                                        "firstResult", offset, "maxResults", count),
				ClassUtil.<List<UserAuditTask>>castClass(List.class));
	}

}
