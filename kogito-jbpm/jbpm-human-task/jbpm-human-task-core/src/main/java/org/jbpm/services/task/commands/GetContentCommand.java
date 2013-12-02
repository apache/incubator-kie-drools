package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.Content;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-content-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetContentCommand extends TaskCommand<Content> {

	private static final long serialVersionUID = 5911387213149078240L;

	@XmlElement
    @XmlSchemaType(name="long")
	private Long contentId;
	
	public GetContentCommand() {
	}
	
	public GetContentCommand(Long contentId) {
		this.contentId = contentId;
    }

    public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public Content execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskContentService().getContentById(contentId);
    }

}
