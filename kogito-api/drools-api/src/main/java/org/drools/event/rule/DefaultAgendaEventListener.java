package org.drools.event.rule;

import org.drools.runtime.rule.WorkingMemory;

public class DefaultAgendaEventListener
    implements
    AgendaEventListener {

    public void activationCancelled(ActivationCancelledEvent event,
                                    WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void activationCreated(ActivationCreatedEvent event,
                                  WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void afterActivationFired(AfterActivationFiredEvent event,
                                     WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event,
                                  WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event,
                                  WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event,
                                      WorkingMemory workingMemory) {
        // intentionally left blank
    }

}
