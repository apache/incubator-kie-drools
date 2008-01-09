package org.drools.process.instance;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface WorkItemHandler {
    
    void executeWorkItem(WorkItem workItem, WorkItemManager manager);
    
    void abortWorkItem(WorkItem workItem, WorkItemManager manager);

}
