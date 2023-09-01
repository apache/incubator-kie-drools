package org.drools.verifier.components;

public class ReturnValueFieldDescr extends PatternComponent {

    public ReturnValueFieldDescr(Pattern pattern) {
        super( pattern );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.RETURN_VALUE_FIELD_DESCR;
    }

}
