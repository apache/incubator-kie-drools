package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-archived-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetArchivedTasksCommand extends TaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 5077599352603072633L;

	public GetArchivedTasksCommand() {
	}

	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
    	return context.getTaskAdminService().getArchivedTasks();

    }

}
