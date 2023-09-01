package org.kie.api.event.kiebase;

import org.kie.api.definition.process.Process;

public interface AfterProcessRemovedEvent
    extends
    KieBaseEvent {
    Process getProcess();
}
