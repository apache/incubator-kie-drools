package org.drools.process.instance;

import org.drools.runtime.process.WorkItem;

public interface WorkItemListener {

    void workItemCompleted(WorkItem workItem);
    
    void workItemAborted(WorkItem workItem);
    
}
