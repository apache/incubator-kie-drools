package org.kie.api.event.rule;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;

public interface ObjectDeletedEvent
    extends
    RuleRuntimeEvent {

    public FactHandle getFactHandle();

    public Object getOldObject();

    public Rule getRule();
}
