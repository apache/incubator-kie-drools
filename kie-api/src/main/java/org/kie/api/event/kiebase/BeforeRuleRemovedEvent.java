package org.kie.api.event.kiebase;

import org.kie.api.definition.rule.Rule;


public interface BeforeRuleRemovedEvent
    extends
    KieBaseEvent {
    Rule getRule();
}
