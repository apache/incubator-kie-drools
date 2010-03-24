package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public interface Source
    extends
    ChildComponent {

    public String getPath();

    public VerifierComponentType getVerifierComponentType();
}
