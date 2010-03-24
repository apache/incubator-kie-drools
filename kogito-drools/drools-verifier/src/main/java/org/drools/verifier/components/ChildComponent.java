package org.drools.verifier.components;

public interface ChildComponent {

    public VerifierComponentType getParentType();

    public String getParentPath();

    public int getOrderNumber();

}
