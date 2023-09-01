package org.kie.api.event.rule;

import org.kie.api.event.KieRuntimeEvent;
import org.kie.api.runtime.rule.RuleFlowGroup;

public interface RuleFlowGroupEvent extends KieRuntimeEvent {

    /**
     * The RuleFlowGroup for this event
     *
     * @return the RuleFlowGroup for this event
     */
    RuleFlowGroup getRuleFlowGroup();
}
