package org.drools.event.process.impl;

import org.drools.WorkingMemory;
import org.drools.event.ProcessEvent;
import org.drools.event.process.ProcessStartedEvent;

public class ProcessStartedEventImpl extends ProcessEventImpl implements ProcessStartedEvent {

	public ProcessStartedEventImpl(ProcessEvent event, WorkingMemory workingMemory) {
		super(event, workingMemory);
	}

}
