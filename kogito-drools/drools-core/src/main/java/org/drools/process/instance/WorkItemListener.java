package org.drools.process.instance;

public interface WorkItemListener {

    void workItemCompleted(WorkItem workItem);
    
    void workItemAborted(WorkItem workItem);
    
}
