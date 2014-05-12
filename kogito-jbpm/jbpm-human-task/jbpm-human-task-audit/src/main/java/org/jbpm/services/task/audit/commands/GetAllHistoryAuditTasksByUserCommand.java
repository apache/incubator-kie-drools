package org.jbpm.services.task.audit.commands;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.jbpm.services.task.audit.impl.model.api.AuditTask;
import org.jbpm.services.task.commands.PaginatedTaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="get-all-audit-tasks-by-user-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllHistoryAuditTasksByUserCommand extends PaginatedTaskCommand<List<AuditTask>> {

        private String owner;
	public GetAllHistoryAuditTasksByUserCommand() {
	}

        public GetAllHistoryAuditTasksByUserCommand(String owner, int offset, int count) {
            this.owner = owner;
            this.offset = offset;
            this.count = count;
        }
        
	@Override
	public List<AuditTask> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getAllAuditTasksByUser", 
                                persistenceContext.addParametersToMap("owner", owner, "firstResult", offset, "maxResults", count),
				ClassUtil.<List<AuditTask>>castClass(List.class));
	}

}
