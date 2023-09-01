package org.drools.mvel.workitem;

import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class CustomWorkItemHandler implements WorkItemHandler {
    
    @SuppressWarnings("unused")
    private StatefulKnowledgeSession session;
    
    public CustomWorkItemHandler(StatefulKnowledgeSession session) {
        this.session = session;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // dummy work item handler implementation

    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // dummy work item handler implementation

    }

}
