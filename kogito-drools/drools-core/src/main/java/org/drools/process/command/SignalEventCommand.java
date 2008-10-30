package org.drools.process.command;

import org.drools.process.instance.InternalProcessInstance;
import org.drools.WorkingMemory;

public class SignalEventCommand implements Command {
	
	private long processInstanceId;
	private String eventType;
	private Object event;
	
	public long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Object getEvent() {
		return event;
	}

	public void setEvent(Object event) {
		this.event = event;
	}

	public Object execute(WorkingMemory workingMemory) {
		InternalProcessInstance processInstance = ( InternalProcessInstance ) workingMemory.getProcessInstance(processInstanceId);
		if (processInstance != null) {
			processInstance.signalEvent(eventType, processInstance);
		}
		return null;
	}

}
