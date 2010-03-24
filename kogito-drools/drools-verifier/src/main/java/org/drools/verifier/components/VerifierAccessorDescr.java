package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierAccessorDescr extends RuleComponent {

    public VerifierAccessorDescr(VerifierRule rule) {
        super( rule );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.ACCESSOR;
    }
}
