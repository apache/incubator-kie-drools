package org.kie.api.event.rule;

import java.util.EventListener;

public interface RuleRuntimeEventListener extends EventListener {
    void objectInserted(ObjectInsertedEvent event);

    void objectUpdated(ObjectUpdatedEvent event);

    void objectDeleted(ObjectDeletedEvent event);
}
