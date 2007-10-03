package org.drools.audit;

import org.drools.WorkingMemory;
import org.drools.audit.event.LogEvent;

public class WorkingMemoryConsoleLogger extends WorkingMemoryLogger {

	public WorkingMemoryConsoleLogger(WorkingMemory workingMemory) {
		super(workingMemory);
	}
	
	public void logEventCreated(LogEvent logEvent) {
		System.out.println(logEvent);
	}

}
