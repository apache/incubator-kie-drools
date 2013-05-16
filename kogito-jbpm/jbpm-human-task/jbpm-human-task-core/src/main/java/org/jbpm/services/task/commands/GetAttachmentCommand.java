package org.jbpm.services.task.commands;

import org.kie.api.task.model.Attachment;
import org.kie.internal.command.Context;

public class GetAttachmentCommand extends TaskCommand<Attachment> {

	private long attachmentId;
	
	public GetAttachmentCommand() {
	}
	
	public GetAttachmentCommand(long attachmentId) {
		this.attachmentId = attachmentId;
    }

    public Attachment execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	context.getTaskService().getAttachmentById(attachmentId);
        	return null;
        }
        return context.getTaskAttachmentService().getAttachmentById(attachmentId);
    }

}
