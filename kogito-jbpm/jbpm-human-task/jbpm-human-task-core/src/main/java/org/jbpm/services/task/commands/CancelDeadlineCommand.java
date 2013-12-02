package org.jbpm.services.task.commands;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.InternalTask;

@XmlRootElement(name="cancel-deadline-command")
@XmlAccessorType(XmlAccessType.NONE)
public class CancelDeadlineCommand extends UserGroupCallbackTaskCommand<Void> {

	private static final long serialVersionUID = -1315897796195789680L;

	@XmlElement
	private boolean removeStart;
	@XmlElement
	private boolean removeEnd;
	
    public CancelDeadlineCommand() {
    }
    
    public CancelDeadlineCommand(long taskId, boolean removeStart, boolean removeEnd) {
        this.taskId = taskId;
        this.removeStart = removeStart;
        this.removeEnd = removeEnd;
    }
	@Override
	public Void execute(Context cntxt) {
		
		TaskContext context = (TaskContext) cntxt;
		
		TaskDeadlinesService deadlineService = context.getTaskDeadlinesService();
        TaskQueryService queryService = context.getTaskQueryService();
        TaskPersistenceContext persistenceContext = context.getPersistenceContext();
        
        InternalTask task = (InternalTask) queryService.getTaskInstanceById(taskId);
        if (task == null || task.getDeadlines() == null) {
            return null;
        }

        Iterator<? extends Deadline> it = null;

        if (removeStart) {
        	
            if (task.getDeadlines().getStartDeadlines() != null) {
            	deadlineService.unschedule(taskId, DeadlineType.START);
                it = task.getDeadlines().getStartDeadlines().iterator();
                while (it.hasNext()) {
                    
                    persistenceContext.removeDeadline(it.next());
                    it.remove();
                }
            }
        }

        if (removeEnd) {
            if (task.getDeadlines().getEndDeadlines() != null) {
            	deadlineService.unschedule(taskId, DeadlineType.END);
                it = task.getDeadlines().getEndDeadlines().iterator();
                while (it.hasNext()) {                    
                    persistenceContext.removeDeadline(it.next());
                    it.remove();
                }

            }
        }
		return null;
	}

}
