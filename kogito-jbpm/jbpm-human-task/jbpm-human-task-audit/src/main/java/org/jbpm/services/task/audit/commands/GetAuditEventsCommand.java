package org.jbpm.services.task.audit.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.commands.PaginatedTaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.TaskEvent;

@XmlRootElement(name="get-audit-events-for-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAuditEventsCommand extends PaginatedTaskCommand<List<TaskEvent>> {

	private static final long serialVersionUID = -7929370526623674312L;

	public GetAuditEventsCommand() {
		
	}
	
	public GetAuditEventsCommand(long taskId, int offset, int count) {
		this.taskId = taskId;
                this.offset = offset;
                this.count = count;
	}
        
        
	
	@Override
	public List<TaskEvent> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		if( this.taskId != null ) { 
		    return persistenceContext.queryWithParametersInTransaction("getAllTasksEvents", 
		            persistenceContext.addParametersToMap("taskId", taskId, "firstResult", offset, "maxResults", count),
		            ClassUtil.<List<TaskEvent>>castClass(List.class));
		} else { 
		    return persistenceContext.queryStringWithParametersInTransaction("FROM TaskEventImpl",persistenceContext.addParametersToMap("firstResult", offset, "maxResults", count),
		            ClassUtil.<List<TaskEvent>>castClass(List.class));
		}
	}

}
