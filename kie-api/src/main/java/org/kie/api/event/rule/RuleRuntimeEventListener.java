package org.kie.api.event.rule;

public interface RuleRuntimeEventListener {
    void objectInserted(ObjectInsertedEvent event);

    void objectUpdated(ObjectUpdatedEvent event);

    void objectDeleted(ObjectDeletedEvent event);
}
