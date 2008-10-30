package org.drools.event.process.impl;

import org.drools.WorkingMemory;
import org.drools.event.ProcessEvent;
import org.drools.event.process.ProcessCompletedEvent;

public class ProcessCompletedEventImpl extends ProcessEventImpl implements ProcessCompletedEvent {

	public ProcessCompletedEventImpl(ProcessEvent event, WorkingMemory workingMemory) {
		super(event, workingMemory);
	}

}
