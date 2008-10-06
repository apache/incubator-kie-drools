package org.drools.process.instance.event;

import org.drools.WorkingMemory;

public interface SignalManagerFactory {
	
	SignalManager createSignalManager(WorkingMemory workingMemory);

}
