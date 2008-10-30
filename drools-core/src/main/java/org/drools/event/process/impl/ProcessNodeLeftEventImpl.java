package org.drools.event.process.impl;

import org.drools.WorkingMemory;
import org.drools.event.RuleFlowNodeTriggeredEvent;
import org.drools.event.process.ProcessNodeLeftEvent;

public class ProcessNodeLeftEventImpl extends ProcessNodeEventImpl implements ProcessNodeLeftEvent {

	public ProcessNodeLeftEventImpl(RuleFlowNodeTriggeredEvent event, WorkingMemory workingMemory) {
		super(event, workingMemory);
	}

}
