package org.drools.process.command;

import org.drools.process.instance.ProcessInstance;
import org.drools.reteoo.ReteooWorkingMemory;

public class SignalEventCommand implements Command<Object> {
	
	private long processInstanceId;
	private String eventType;
	private Object event;	
	
	public SignalEventCommand(String eventType,
                              Object event) {
        this.eventType = eventType;
        this.event = event;
    }	
	
    public SignalEventCommand(long processInstanceId,
                              String eventType,
                              Object event) {
        this.processInstanceId = processInstanceId;
        this.eventType = eventType;
        this.event = event;
    }

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

	public Object execute(ReteooWorkingMemory session) {
		if (processInstanceId == -1) {
			session.getSignalManager().signalEvent(eventType, event);
		} else {
			ProcessInstance processInstance = session.getProcessInstance(processInstanceId);
			if (processInstance != null) {
				processInstance.signalEvent(eventType, event);
			}
		}
		return null;
	}

	public String toString() {
		if (processInstanceId == -1) {
			return "session.getSignalManager().signalEvent(" + eventType + ", " + event + ");";
		} else {
			return "processInstance.signalEvent(" + eventType + ", " + event + ");";
		}
	}

}
