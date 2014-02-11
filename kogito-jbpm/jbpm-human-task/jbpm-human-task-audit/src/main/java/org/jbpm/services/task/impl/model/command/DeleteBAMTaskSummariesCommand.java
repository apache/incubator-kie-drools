package org.jbpm.services.task.impl.model.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.commands.TaskCommand;
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
        if( this.taskId != null ) { 
            persistenceContext.executeUpdateString("delete from BAMTaskSummaryImpl b where b.taskId = "+ this.taskId);
        } else { 
            persistenceContext.executeUpdateString("delete from BAMTaskSummaryImpl b");
        }
        return null;
    }

}
