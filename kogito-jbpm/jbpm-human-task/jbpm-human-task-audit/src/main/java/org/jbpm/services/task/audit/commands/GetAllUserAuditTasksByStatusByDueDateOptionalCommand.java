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

@XmlRootElement(name="get-all-user-audit-tasks-bystatusbyduedateoptional-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllUserAuditTasksByStatusByDueDateOptionalCommand extends PaginatedTaskCommand<List<UserAuditTask>> {

        private String status;
        private Date dueDate;
	public GetAllUserAuditTasksByStatusByDueDateOptionalCommand() {
		
	}
	
	public GetAllUserAuditTasksByStatusByDueDateOptionalCommand(String userId, String status, Date dueDate, int offset, int count) {
		this.userId = userId;
                this.status = status;
                this.dueDate = dueDate;
                this.offset = offset;
                this.count = count;
	}
	
	@Override
	public List<UserAuditTask> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getAllUserAuditTasksByStatusByDueDateOptional", 
				persistenceContext.addParametersToMap("userId", userId, "status", status, "dueDate", dueDate,
                                        "firstResult", offset, "maxResults", count),
				ClassUtil.<List<UserAuditTask>>castClass(List.class));
	}

}
