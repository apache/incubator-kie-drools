package org.jbpm.services.task.commands;

import org.kie.api.task.model.Content;
import org.kie.internal.command.Context;

public class GetContentCommand extends TaskCommand<Content> {

	private long contentId;
	
	public GetContentCommand() {
	}
	
	public GetContentCommand(long contentId) {
		this.contentId = contentId;
    }

    public long getContentId() {
		return contentId;
	}

	public void setContentId(long contentId) {
		this.contentId = contentId;
	}

	public Content execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	return context.getTaskService().getContentById(contentId);
        }
        return context.getTaskContentService().getContentById(contentId);
    }

}
