package org.kie.api.event.kiebase;

import org.kie.api.definition.process.Process;

public interface BeforeProcessAddedEvent
    extends
    KieBaseEvent {
    Process getProcess();
}
