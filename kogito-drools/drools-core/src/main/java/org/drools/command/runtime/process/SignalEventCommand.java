package org.drools.command.runtime.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

@XmlAccessorType(XmlAccessType.NONE)
public class SignalEventCommand implements GenericCommand<Object> {
	
	@XmlAttribute(name="process-instance-id")
	private long processInstanceId = -1;
	@XmlAttribute(name="event-type", required=true)
	private String eventType;
	@XmlElement(name="event")
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
			return "ksession.signalEvent(" + eventType + ", " + event + ");";
		} else {
			return "ksession.signalEvent(" + processInstanceId + ", " + eventType + ", " + event + ");";
		}
	}

}
