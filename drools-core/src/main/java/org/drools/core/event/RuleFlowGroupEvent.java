package org.drools.core.event;

import java.util.EventObject;

import org.drools.core.common.RuleFlowGroup;

public class RuleFlowGroupEvent extends EventObject {

    private static final long serialVersionUID = 510l;

    public RuleFlowGroupEvent(final RuleFlowGroup ruleFlowGroup) {
        super( ruleFlowGroup );
    }

    public RuleFlowGroup getRuleFlowGroup() {
        return (RuleFlowGroup) getSource();
    }

}
