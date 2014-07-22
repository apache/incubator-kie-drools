package org.jbpm.services.task.audit.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.query.QueryFilterImpl;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.TaskEvent;

@XmlRootElement(name="get-audit-events-for-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAuditEventsCommand extends TaskCommand<List<TaskEvent>> {

	private static final long serialVersionUID = -7929370526623674312L;
        private QueryFilter filter;
	public GetAuditEventsCommand() {
            this.filter = new QueryFilterImpl(0,0);
	}
	
	public GetAuditEventsCommand(long taskId, QueryFilter filter) {
		this.taskId = taskId;
                this.filter = filter;
	}
       
	@Override
	public List<TaskEvent> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		if( this.taskId != null ) { 
		    return persistenceContext.queryWithParametersInTransaction("getAllTasksEvents", 
		            persistenceContext.addParametersToMap("taskId", taskId, "firstResult", filter.getOffset(), 
                                    "maxResults", filter.getCount()),
		            ClassUtil.<List<TaskEvent>>castClass(List.class));
		} else { 
		    return persistenceContext.queryStringWithParametersInTransaction("FROM TaskEventImpl",persistenceContext.addParametersToMap("firstResult", filter.getOffset(),
                                                                                                "maxResults", filter.getCount()),
		            ClassUtil.<List<TaskEvent>>castClass(List.class));
		}
	}

}
