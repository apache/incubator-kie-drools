package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Attachment;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-all-attachments-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllAttachmentsCommand extends TaskCommand<List<Attachment>> {

	private static final long serialVersionUID = -4566088487597623910L;

	public GetAllAttachmentsCommand() {
	}
	
	public GetAllAttachmentsCommand(Long taskId) {
		this.taskId = taskId;
    }

	public List<Attachment> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskAttachmentService().getAllAttachmentsByTaskId(taskId);
    }

}
