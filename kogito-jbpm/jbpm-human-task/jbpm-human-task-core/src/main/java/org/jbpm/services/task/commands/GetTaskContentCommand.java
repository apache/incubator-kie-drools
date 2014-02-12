package org.jbpm.services.task.commands;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskContentService;

@XmlRootElement(name="get-task-content-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskContentCommand extends TaskCommand<Map<String, Object>> {

	private static final long serialVersionUID = 5911387213149078240L;

	
	public GetTaskContentCommand() {
	}
	
	public GetTaskContentCommand(Long taskId) {
		this.taskId = taskId;
    }

	@SuppressWarnings("unchecked")
	public Map<String, Object> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        Task taskById = context.getTaskQueryService().getTaskInstanceById(taskId);
        if (taskById == null) {
        	throw new IllegalStateException("Unable to find task with id " + taskId);
        }
        
        TaskContentService contentService = context.getTaskContentService();
        
        Content contentById = contentService.getContentById(taskById.getTaskData().getDocumentContentId());
        ContentMarshallerContext mContext = contentService.getMarshallerContext(taskById);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(contentById.getContent(), mContext.getEnvironment(), mContext.getClassloader());
        if (!(unmarshalledObject instanceof Map)) {
            throw new IllegalStateException(" The Task Content Needs to be a Map in order to use this method and it was: "+unmarshalledObject.getClass());

        }
        Map<String, Object> content = (Map<String, Object>) unmarshalledObject;
        
        return content;
    }

}
