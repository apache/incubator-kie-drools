package org.jbpm.services.task.admin.listener.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpm.services.task.commands.TaskCommand;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

public class GetCurrentTxTasksCommand extends TaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 6474368266134150938L;
	private Long processInstanceId;
	
	public GetCurrentTxTasksCommand(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<TaskSummary> execute(Context context) {
		List<TaskSummary> tasks = new ArrayList<TaskSummary>();
		Set<TaskSummary> tasksToRemove = (Set<TaskSummary>) context.get("local:current-tasks");
        if (tasksToRemove != null) {
        	for (TaskSummary task : tasksToRemove) {
        		if (task.getProcessInstanceId() == processInstanceId) {
        			tasks.add(task);
        		}
        	}
        }
        return tasks;
	}
	
}
