package org.kie.api.event.kiebase;

import org.kie.api.definition.rule.Rule;

public interface AfterKieBaseLockedEvent
    extends
    KieBaseEvent {
    Rule getRule();
}
