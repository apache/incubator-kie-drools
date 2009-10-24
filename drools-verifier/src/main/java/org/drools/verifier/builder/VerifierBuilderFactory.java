package org.drools.verifier.builder;

public class VerifierBuilderFactory {

    public static VerifierBuilder newVerifierBuilder() {
        return new VerifierBuilderImpl();
    }
}
