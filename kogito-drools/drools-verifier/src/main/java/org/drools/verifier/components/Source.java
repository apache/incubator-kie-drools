package org.drools.verifier.components;

import org.drools.verifier.data.VerifierComponent;

public abstract class Source extends VerifierComponent
    implements
    ChildComponent {

    private VerifierComponentType parentType;
    private String                parentGuid;
    private int                   orderNumber = 0;

    public VerifierComponentType getParentType() {
        return parentType;
    }

    public String getParentGuid() {
        return parentGuid;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setParentType(VerifierComponentType parentType) {
        this.parentType = parentType;
    }

    public void setParentGuid(String parentGuid) {
        this.parentGuid = parentGuid;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
