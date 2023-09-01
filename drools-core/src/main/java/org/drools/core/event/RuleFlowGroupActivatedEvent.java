package org.drools.core.event;

import org.drools.core.common.RuleFlowGroup;

public class RuleFlowGroupActivatedEvent extends RuleFlowGroupEvent {

    private static final long serialVersionUID = 510l;

    public RuleFlowGroupActivatedEvent(final RuleFlowGroup ruleFlowGroup) {
        super( ruleFlowGroup );
    }

    public String toString() {
        return "==>[RuleFlowGroupActivated(name=" + getRuleFlowGroup().getName() + "; size=" + getRuleFlowGroup().size() + ")]";
    }
}
