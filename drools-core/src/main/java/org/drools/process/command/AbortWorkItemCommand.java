package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;

public class AbortWorkItemCommand
    implements
    Command<Object> {

    private long workItemId;

    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    public Object execute(ReteooWorkingMemory session) {
        session.getWorkItemManager().abortWorkItem( workItemId );
        return null;
    }

    public String toString() {
        return "session.getWorkItemManager().abortWorkItem(" + workItemId + ");";
    }

}
