package org.drools.common;

import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.RuleFlowGroup;
import org.drools.util.AbstractBaseLinkedListNode;

public class RuleFlowGroupNode extends AbstractBaseLinkedListNode {

    private Activation    activation;

    private RuleFlowGroup ruleFlowGroup;

    public RuleFlowGroupNode(final Activation activation,
                             final RuleFlowGroup ruleFlowGroup) {
        super();
        this.activation = activation;
        this.ruleFlowGroup = ruleFlowGroup;
    }

    public Activation getActivation() {
        return this.activation;
    }

    public RuleFlowGroup getRuleFlowGroup() {
        return this.ruleFlowGroup;
    }

}
