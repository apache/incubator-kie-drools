package org.drools.core.event;

import org.drools.core.common.RuleFlowGroup;

public class RuleFlowGroupDeactivatedEvent extends RuleFlowGroupEvent {

    private static final long serialVersionUID = 510l;

    public RuleFlowGroupDeactivatedEvent(final RuleFlowGroup ruleFlowGroup) {
        super( ruleFlowGroup );
    }

    public String toString() {
        return "==>[RuleFlowGroupDeactivated(name=" + getRuleFlowGroup().getName() + "; size=" + getRuleFlowGroup().size() + ")]";
    }
}
