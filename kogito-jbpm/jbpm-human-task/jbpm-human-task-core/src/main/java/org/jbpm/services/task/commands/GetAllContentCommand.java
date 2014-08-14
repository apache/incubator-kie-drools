package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Content;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-all-content-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllContentCommand extends TaskCommand<List<Content>> {

	private static final long serialVersionUID = 5911387213149078240L;
	
	public GetAllContentCommand() {
	}
	
	public GetAllContentCommand(Long taskId) {
		this.taskId = taskId;
    }

	public List<Content> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskContentService().getAllContentByTaskId(taskId);
    }

}
