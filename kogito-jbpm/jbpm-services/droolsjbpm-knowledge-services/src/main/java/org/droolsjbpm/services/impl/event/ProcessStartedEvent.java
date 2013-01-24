package org.droolsjbpm.services.impl.event;


public class ProcessStartedEvent extends ProcessEvent {

    private static final long serialVersionUID = -4266513388093196681L;

    public ProcessStartedEvent(org.kie.event.process.ProcessStartedEvent event) {
        super(event);
    }
}
