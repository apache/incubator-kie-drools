package org.drools.core.event;

import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

public class DefaultRuleRuntimeEventListener
    implements
    RuleRuntimeEventListener {
    public DefaultRuleRuntimeEventListener() {
        // intentionally left blank
    }

    public void objectInserted(final ObjectInsertedEvent event) {
        // intentionally left blank
    }

    public void objectUpdated(final ObjectUpdatedEvent event) {
        // intentionally left blank
    }

    public void objectDeleted(final ObjectDeletedEvent event) {
        // intentionally left blank
    }
}
