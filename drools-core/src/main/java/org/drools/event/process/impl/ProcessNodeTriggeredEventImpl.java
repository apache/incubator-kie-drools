package org.drools.event.process.impl;

import org.drools.WorkingMemory;
import org.drools.event.RuleFlowNodeTriggeredEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;

public class ProcessNodeTriggeredEventImpl extends ProcessNodeEventImpl implements ProcessNodeTriggeredEvent {

	public ProcessNodeTriggeredEventImpl(RuleFlowNodeTriggeredEvent event, WorkingMemory workingMemory) {
		super(event, workingMemory);
	}

}
