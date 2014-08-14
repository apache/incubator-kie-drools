package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Comment;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-all-comments-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllCommentsCommand extends TaskCommand<List<Comment>> {

	private static final long serialVersionUID = 5911387213149078240L;
	
	public GetAllCommentsCommand() {
	}
	
	public GetAllCommentsCommand(Long taskId) {
		this.taskId = taskId;
	}

	public List<Comment> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskCommentService().getAllCommentsByTaskId(taskId);
    }

}
