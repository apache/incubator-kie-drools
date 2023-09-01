package org.kie.api.event.kiebase;


public interface AfterFunctionRemovedEvent
    extends
    KieBaseEvent {
    public String getFunction();
}
