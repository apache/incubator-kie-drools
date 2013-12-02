package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.Comment;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-comment-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetCommentCommand extends TaskCommand<Comment> {

	private static final long serialVersionUID = 5911387213149078240L;

	@XmlElement
    @XmlSchemaType(name="long")
	private Long commentId;
	
	public GetCommentCommand() {
	}
	
	public GetCommentCommand(Long commentId) {
		this.commentId = commentId;
    }

    public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Comment execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskCommentService().getCommentById(commentId);
    }

}
