package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.RuleFlowGroup;

public class RuleFlowGroupActivatedEventImpl extends RuleFlowGroupEventImpl implements RuleFlowGroupActivatedEvent {

    private static final long serialVersionUID = 510L;

    public RuleFlowGroupActivatedEventImpl(final RuleFlowGroup ruleFlowGroup, KieRuntime kruntime ) {
        super( ruleFlowGroup, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public RuleFlowGroupActivatedEventImpl() {
        super();
    }

    @Override
    public String toString() {
        return "==>[RuleFlowGroupActivated(name=" + getRuleFlowGroup().getName() + ")]";
    }
}
