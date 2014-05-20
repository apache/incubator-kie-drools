package org.jbpm.services.task.audit.commands;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.jbpm.services.task.audit.impl.model.api.AuditTask;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.query.QueryFilterImpl;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.QueryFilter;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="get-all-audit-tasks-by-user-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllHistoryAuditTasksByUserCommand extends TaskCommand<List<AuditTask>> {
        private QueryFilter filter;
        private String owner;
	public GetAllHistoryAuditTasksByUserCommand() {
            this.filter =  new QueryFilterImpl(0,0);
	}

        public GetAllHistoryAuditTasksByUserCommand(String owner, QueryFilter filter) {
            this.owner = owner;
            this.filter = filter;
        }
        
	@Override
	public List<AuditTask> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getAllAuditTasksByUser", 
                                persistenceContext.addParametersToMap("owner", owner, "firstResult", filter.getOffset(), 
                                        "maxResults", filter.getCount()),
				ClassUtil.<List<AuditTask>>castClass(List.class));
	}

}
