package org.drools.process.instance;

public interface WorkItemHandler {
    
    void executeWorkItem(WorkItem workItem, WorkItemManager manager);
    
    void abortWorkItem(WorkItem workItem, WorkItemManager manager);

}
