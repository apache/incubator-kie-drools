package org.drools.event;

import java.util.EventListener;

public interface AgendaEventListener
    extends
    EventListener {
    void activationCreated(ActivationCreatedEvent event);

    void activationCancelled(ActivationCancelledEvent event);

    void beforeActivationFired(BeforeActivationFiredEvent event);

    void afterActivationFired(AfterActivationFiredEvent event);
}
