package org.drools.process.instance;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface WorkItemManager {

	void internalExecuteWorkItem(WorkItem workItem);
	
	void internalAddWorkItem(WorkItem workItem);
	
    void internalAbortWorkItem(long id);
    
	Set<WorkItem> getWorkItems();
	
	WorkItem getWorkItem(long id);

    void completeWorkItem(long id, Map<String, Object> results);

    void abortWorkItem(long id);

    void registerWorkItemHandler(String workItemName, WorkItemHandler handler);

}
