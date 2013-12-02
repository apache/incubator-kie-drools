package org.jbpm.services.task.admin.listener;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="get-task-for-proc-inst-id-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksForProcessCommand extends TaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = -3784821014329573243L;
	private Long processInstanceId;
	private List<Status> statuses;
	private String language;
	
	public GetTasksForProcessCommand() {
		
	}
	
	public GetTasksForProcessCommand(Long processInstanceId, List<Status> statuses, String language) {
		this.processInstanceId = processInstanceId;
		this.statuses = statuses;
		this.language = language;
	}

	@Override
	public List<TaskSummary> execute(Context context) {
		TaskContext ctx = (TaskContext) context;
		
		TaskPersistenceContext persistenceContext = ctx.getPersistenceContext();
		
		List<TaskSummary> tasks = (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessId",
                persistenceContext.addParametersToMap("processInstanceId", processInstanceId, 
                                        "status", statuses,
                                        "language", language, "flushMode", "AUTO"),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
    
        return tasks;
	}
	
}
