package org.drools.process.instance;

import org.drools.WorkingMemory;

public interface ProcessInstanceManagerFactory {
	
	ProcessInstanceManager createProcessInstanceManager(WorkingMemory workingMemory);

}
