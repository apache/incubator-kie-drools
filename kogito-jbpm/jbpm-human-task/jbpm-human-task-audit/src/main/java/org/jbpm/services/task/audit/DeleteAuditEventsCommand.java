package org.jbpm.services.task.audit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.commands.TaskCommand;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="delete-audit-events-for-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteAuditEventsCommand extends TaskCommand<Void> {

	private static final long serialVersionUID = -7929370526623674312L;

	public DeleteAuditEventsCommand() {
	}
	
	public DeleteAuditEventsCommand(long taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public Void execute(Context context) {
		TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
        if( this.taskId != null ) { 
            persistenceContext.executeUpdateString("delete from TaskEventImpl t where t.taskId = " + this.taskId);
        } else { 
            persistenceContext.executeUpdateString("delete from TaskEventImpl");
        }
		return null;
	}

}
