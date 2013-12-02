package org.jbpm.services.task.audit;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.TaskEvent;

@XmlRootElement(name="get-audit-events-for-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAuditEventsCommand extends TaskCommand<List<TaskEvent>> {

	private static final long serialVersionUID = -7929370526623674312L;

	public GetAuditEventsCommand() {
		
	}
	
	public GetAuditEventsCommand(long taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public List<TaskEvent> execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
		return persistenceContext.queryWithParametersInTransaction("getAllTasksEvents", 
				persistenceContext.addParametersToMap("taskId", taskId),
				ClassUtil.<List<TaskEvent>>castClass(List.class));
	}

}
