package org.drools.process.instance;

import java.util.Set;

/**
 *
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface WorkItemManager extends org.drools.runtime.process.WorkItemManager {

	void internalExecuteWorkItem(WorkItem workItem);
	
	void internalAddWorkItem(WorkItem workItem);
	
    void internalAbortWorkItem(long id);
    
	Set<WorkItem> getWorkItems();
	
	WorkItem getWorkItem(long id);

}
