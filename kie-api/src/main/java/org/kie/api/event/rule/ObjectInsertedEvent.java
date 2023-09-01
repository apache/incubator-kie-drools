package org.kie.api.event.rule;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;

public interface ObjectInsertedEvent
    extends
    RuleRuntimeEvent {
    FactHandle getFactHandle();

    Object getObject();

    Rule getRule();
}
