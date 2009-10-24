package org.drools.verifier.components;

public interface ChildComponent {

    public VerifierComponentType getParentType();

    public String getParentGuid();

    public int getOrderNumber();

}
