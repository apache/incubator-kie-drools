package org.jbpm.kie.services.impl.event;


public class ProcessInstanceStartedEvent extends ProcessEvent {

    private static final long serialVersionUID = -4266513388093196681L;

    public ProcessInstanceStartedEvent(org.kie.api.event.process.ProcessStartedEvent event) {
        super(event);
    }
}
