package org.droolsjbpm.services.impl.event;


public class ProcessInstanceCompletedEvent extends ProcessEvent {

    private static final long serialVersionUID = 6853884184207344822L;

    public ProcessInstanceCompletedEvent(org.kie.api.event.process.ProcessCompletedEvent event) {
        super(event);
    }

}
