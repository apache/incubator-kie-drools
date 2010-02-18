package org.drools.common;

import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;

public class ActivationGroupNode extends AbstractBaseLinkedListNode {

    private Activation      activation;

    private ActivationGroup activationGroup;

    public ActivationGroupNode(final Activation activation,
                               final ActivationGroup activationGroup) {
        super();
        this.activation = activation;
        this.activationGroup = activationGroup;
    }

    public Activation getActivation() {
        return this.activation;
    }

    public ActivationGroup getActivationGroup() {
        return this.activationGroup;
    }

}
