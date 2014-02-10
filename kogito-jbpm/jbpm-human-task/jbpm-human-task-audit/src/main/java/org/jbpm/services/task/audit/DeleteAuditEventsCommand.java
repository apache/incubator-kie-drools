package org.jbpm.services.task.audit;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.persistence.jpa.JpaPersistenceContext;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.persistence.JPATaskPersistenceContext;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.TaskEvent;

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
		JPATaskPersistenceContext persistenceContextImpl = null;
		if( !(persistenceContext instanceof JPATaskPersistenceContext) ) { 
		   throw new UnsupportedOperationException("This operation is not supported on the " + persistenceContext.getClass() ); 
		}
		persistenceContextImpl = (JPATaskPersistenceContext) persistenceContext;
        if( this.taskId != null ) { 
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("taskId", this.taskId);
            persistenceContextImpl.queryDeleteWithParametersInTransaction("deleteTaskEventsForTask", params);
        } else { 
            persistenceContextImpl.queryDeleteInTransaction("deleteAllTaskEvents");
        }
		return null;
	}

}
