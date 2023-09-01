package org.kie.api.event.kiebase;

import org.kie.api.definition.rule.Rule;


public interface AfterRuleAddedEvent
    extends
    KieBaseEvent {
    Rule getRule();
}
