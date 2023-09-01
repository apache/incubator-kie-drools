package org.drools.core.common;

import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.AbstractBaseLinkedListNode;

public class ActivationGroupNode extends AbstractBaseLinkedListNode<ActivationGroupNode> {

    private final InternalMatch internalMatch;

    private final InternalActivationGroup activationGroup;

    public ActivationGroupNode(final InternalMatch internalMatch,
                               final InternalActivationGroup activationGroup) {
        super();
        this.internalMatch = internalMatch;
        this.activationGroup = activationGroup;
    }

    public InternalMatch getActivation() {
        return this.internalMatch;
    }

    public InternalActivationGroup getActivationGroup() {
        return this.activationGroup;
    }

    @Override
    public String toString() {
        return "activation: " + internalMatch + " in " + activationGroup;
    }
}
