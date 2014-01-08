package org.jbpm.services.task.audit.commands;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.jbpm.services.task.audit.impl.model.api.HistoryAuditTask;
import org.jbpm.services.task.commands.PaginatedTaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="get-all-history-audit-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllHistoryAuditTasksCommand extends PaginatedTaskCommand<List<HistoryAuditTask>> {
     
	public GetAllHistoryAuditTasksCommand() {
	}

        public GetAllHistoryAuditTasksCommand(int offset, int count) {
            this.offset = offset;
            this.count = count;
        }

	@Override
	public List<HistoryAuditTask> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getAllHistoryAuditTasks", 
                                persistenceContext.addParametersToMap("firstResult", offset, "maxResults", count),
				ClassUtil.<List<HistoryAuditTask>>castClass(List.class));
	}

}
