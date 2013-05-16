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

    public Content execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	context.getTaskService().getContentById(contentId);
        	return null;
        }
        return context.getTaskContentService().getContentById(contentId);
    }

}
