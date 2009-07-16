package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

public class SignalEventCommand implements GenericCommand<Object> {
	
	private long processInstanceId = -1;
	private String eventType;
	private Object event;	
	
    public SignalEventCommand() {
    }   	
	
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

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
		if (processInstanceId == -1) {
		    ksession.signalEvent(eventType, event);
		} else {
			ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
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
