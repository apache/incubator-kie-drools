package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class AbortWorkItemCommand
    implements
    GenericCommand<Object> {

    private long workItemId;
    
    public AbortWorkItemCommand() {
        
    }

    public AbortWorkItemCommand(long workItemId) {
        this.workItemId = workItemId;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        ksession.getWorkItemManager().abortWorkItem( workItemId );
        return null;
    }

    public String toString() {
        return "session.getWorkItemManager().abortWorkItem(" + workItemId + ");";
    }

}
