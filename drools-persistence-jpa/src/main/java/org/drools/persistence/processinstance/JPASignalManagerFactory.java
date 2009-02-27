package org.drools.persistence.processinstance;

import org.drools.WorkingMemory;
import org.drools.process.instance.event.SignalManager;
import org.drools.process.instance.event.SignalManagerFactory;

public class JPASignalManagerFactory implements SignalManagerFactory {

	public SignalManager createSignalManager(WorkingMemory workingMemory) {
		return new JPASignalManager(workingMemory);
	}

}
