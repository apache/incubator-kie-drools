package org.drools.verifier.components;

public class VerifierAccessorDescr extends RuleComponent {

    public VerifierAccessorDescr(VerifierRule rule) {
        super( rule );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.ACCESSOR;
    }
}
