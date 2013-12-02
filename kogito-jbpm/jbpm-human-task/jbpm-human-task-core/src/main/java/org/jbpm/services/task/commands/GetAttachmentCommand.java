package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.Attachment;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-attachment-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAttachmentCommand extends TaskCommand<Attachment> {

	private static final long serialVersionUID = -4566088487597623910L;

	@XmlElement(name="attachment-id")
    @XmlSchemaType(name="long")
	private Long attachmentId;
	
	public GetAttachmentCommand() {
	}
	
	public GetAttachmentCommand(Long attachmentId) {
		this.attachmentId = attachmentId;
    }

    public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public Attachment execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskAttachmentService().getAttachmentById(attachmentId);
    }

}
