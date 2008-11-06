package org.drools.process.instance;

import org.drools.WorkingMemory;
import org.drools.runtime.process.WorkItemManager;

public interface WorkItemManagerFactory {
	
	WorkItemManager createWorkItemManager(WorkingMemory workingMemory);

}
