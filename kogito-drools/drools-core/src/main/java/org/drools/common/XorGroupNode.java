package org.drools.common;

import org.drools.spi.Activation;
import org.drools.spi.XorGroup;
import org.drools.util.AbstractBaseLinkedListNode;

public class XorGroupNode extends AbstractBaseLinkedListNode {
    
    private Activation activation;
    
    private XorGroup xorGroup;

    public XorGroupNode(Activation activation,
                        XorGroup xorGroup) {
        super();
        this.activation = activation;
        this.xorGroup = xorGroup;
    }

    public Activation getActivation() {
        return this.activation;
    }

    public XorGroup getXorGroup() {
        return this.xorGroup;
    }
    
    

}
