package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.RuleFlowGroup;

public class RuleFlowGroupDeactivatedEventImpl extends RuleFlowGroupEventImpl  implements RuleFlowGroupDeactivatedEvent {

    private static final long serialVersionUID = 510L;

    public RuleFlowGroupDeactivatedEventImpl(final RuleFlowGroup ruleFlowGroup, KieRuntime kruntime ) {
        super( ruleFlowGroup, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public RuleFlowGroupDeactivatedEventImpl() {
        super();
    }

    @Override
    public String toString() {
        return "==>[RuleFlowGroupDeactivated(name=" + getRuleFlowGroup().getName()  + ")]";
    }
}
