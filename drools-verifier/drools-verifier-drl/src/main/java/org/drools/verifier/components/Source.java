package org.drools.verifier.components;

public interface Source
    extends
    ChildComponent {

    public String getPath();

    public VerifierComponentType getVerifierComponentType();
}
