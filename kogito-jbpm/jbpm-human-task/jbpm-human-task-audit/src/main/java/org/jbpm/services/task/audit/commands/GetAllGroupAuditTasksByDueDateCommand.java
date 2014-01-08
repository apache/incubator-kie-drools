package org.jbpm.services.task.audit.commands;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.jbpm.services.task.audit.impl.model.api.GroupAuditTask;

import org.jbpm.services.task.commands.PaginatedTaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="get-all-group-audit-tasks-byduedate-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllGroupAuditTasksByDueDateCommand extends PaginatedTaskCommand<List<GroupAuditTask>> {

        private Date dueDate;
        private String groupIds;
	public GetAllGroupAuditTasksByDueDateCommand() {
		
	}
	
	public GetAllGroupAuditTasksByDueDateCommand(String groupIds, Date dueDate, int offset, int count) {
		this.groupIds = groupIds;
                this.dueDate = dueDate;
                this.count = count;
                this.offset = offset;
	}
	
	@Override
	public List<GroupAuditTask> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getAllGroupAuditTasksByDueDate", 
				persistenceContext.addParametersToMap("groupIds", groupIds, "dueDate", dueDate, 
                                        "firstResult", offset, "maxResults", count),
				ClassUtil.<List<GroupAuditTask>>castClass(List.class));
	}

}
