package org.drools.core.process;

import org.kie.api.runtime.process.WorkItem;

public interface WorkItemListener {

    void workItemCompleted(WorkItem workItem);
    
    void workItemAborted(WorkItem workItem);
    
}
