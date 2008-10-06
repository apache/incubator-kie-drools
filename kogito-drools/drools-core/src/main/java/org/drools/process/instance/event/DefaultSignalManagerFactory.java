package org.drools.process.instance.event;

import org.drools.WorkingMemory;

public class DefaultSignalManagerFactory implements SignalManagerFactory {

	public SignalManager createSignalManager(WorkingMemory workingMemory) {
		return new DefaultSignalManager();
	}

}
