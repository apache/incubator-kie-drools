package org.drools.process.instance;

import org.drools.WorkingMemory;

public interface WorkItemManagerFactory {
	
	WorkItemManager createWorkItemManager(WorkingMemory workingMemory);

}
