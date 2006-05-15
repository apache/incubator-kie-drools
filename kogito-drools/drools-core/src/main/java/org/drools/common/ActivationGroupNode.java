package org.drools.common;

import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.util.AbstractBaseLinkedListNode;

public class ActivationGroupNode extends AbstractBaseLinkedListNode {
    
    private Activation activation;
    
    private ActivationGroup activationGroup;

    public ActivationGroupNode(Activation activation,
                        ActivationGroup activationGroup) {
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
