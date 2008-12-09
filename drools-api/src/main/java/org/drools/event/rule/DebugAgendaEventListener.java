package org.drools.event.rule;

public class DebugAgendaEventListener
    implements
    AgendaEventListener {

    public void activationCancelled(ActivationCancelledEvent event) {
        System.err.println( event );
    }

    public void activationCreated(ActivationCreatedEvent event) {
        System.err.println( event );
    }

    public void afterActivationFired(AfterActivationFiredEvent event) {
        System.err.println( event );
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        System.err.println( event );
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        System.err.println( event );
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        System.err.println( event );
    }

}
