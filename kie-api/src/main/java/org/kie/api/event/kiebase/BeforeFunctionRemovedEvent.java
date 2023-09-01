package org.kie.api.event.kiebase;


public interface BeforeFunctionRemovedEvent
    extends
    KieBaseEvent {
    String getFunction();
}
