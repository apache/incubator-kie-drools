package org.drools.ruleflow.common.instance;

public interface WorkItemHandler {
    
    void executeWorkItem(WorkItem workItem, WorkItemManager manager);
    
    void abortWorkItem(WorkItem workItem, WorkItemManager manager);

}
