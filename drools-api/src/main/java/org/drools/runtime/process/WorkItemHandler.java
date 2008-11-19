package org.drools.runtime.process;

public interface WorkItemHandler {

    void executeWorkItem(WorkItem workItem,
                         WorkItemManager manager);

    void abortWorkItem(WorkItem workItem,
                       WorkItemManager manager);

}
