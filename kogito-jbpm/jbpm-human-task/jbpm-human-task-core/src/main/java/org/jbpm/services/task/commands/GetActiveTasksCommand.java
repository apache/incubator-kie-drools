package org.jbpm.services.task.commands;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-active-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetActiveTasksCommand extends TaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 5077599352603072633L;

	@XmlElement
	private Date date;

	public GetActiveTasksCommand() {
	}
	
	public GetActiveTasksCommand(Date date) {
		this.date = date;
	}
	
    public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (date == null) {
        	return context.getTaskAdminService().getActiveTasks();
        } else {
        	return context.getTaskAdminService().getActiveTasks(date);
        }

    }

}
