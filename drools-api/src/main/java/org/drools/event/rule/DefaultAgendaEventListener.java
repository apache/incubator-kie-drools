package org.drools.event.rule;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.WorkingMemory;

public class DefaultAgendaEventListener
    implements
    AgendaEventListener {

    public void activationCancelled(ActivationCancelledEvent event) {
        // intentionally left blank
    }

    public void activationCreated(ActivationCreatedEvent event) {
        // intentionally left blank
    }

    public void afterActivationFired(AfterActivationFiredEvent event) {
        // intentionally left blank
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        // intentionally left blank
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        // intentionally left blank
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        // intentionally left blank
    }

}
