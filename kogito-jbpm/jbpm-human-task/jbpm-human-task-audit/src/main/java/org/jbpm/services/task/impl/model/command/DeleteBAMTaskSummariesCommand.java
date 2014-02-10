package org.jbpm.services.task.impl.model.command;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.persistence.jpa.JpaPersistenceContext;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.persistence.JPATaskPersistenceContext;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name="delete-bam-task-summaries-for-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteBAMTaskSummariesCommand extends TaskCommand<Void> {

    private static final long serialVersionUID = -7929370526623674312L;

    public DeleteBAMTaskSummariesCommand() { 
        // default, delete all
    }
    
    public DeleteBAMTaskSummariesCommand(long taskId) {
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
            persistenceContextImpl.queryDeleteWithParametersInTransaction("deleteBAMTaskSummariesForTask", params);
        } else { 
            persistenceContextImpl.queryDeleteInTransaction("deleteAllBAMTaskSummaries");
        }
        return null;
    }

}
