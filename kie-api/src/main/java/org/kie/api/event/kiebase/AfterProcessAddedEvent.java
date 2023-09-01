package org.kie.api.event.kiebase;

import org.kie.api.definition.process.Process;

public interface AfterProcessAddedEvent
    extends
    KieBaseEvent {
    Process getProcess();
}
