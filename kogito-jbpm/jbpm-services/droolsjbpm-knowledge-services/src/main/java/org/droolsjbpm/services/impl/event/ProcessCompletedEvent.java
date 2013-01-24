package org.droolsjbpm.services.impl.event;


public class ProcessCompletedEvent extends ProcessEvent {

    private static final long serialVersionUID = 6853884184207344822L;

    public ProcessCompletedEvent(org.kie.event.process.ProcessCompletedEvent event) {
        super(event);
    }

}
