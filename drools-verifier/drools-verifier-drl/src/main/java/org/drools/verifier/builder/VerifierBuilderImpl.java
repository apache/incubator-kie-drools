package org.drools.verifier.builder;

import java.util.ArrayList;
import java.util.List;

import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierConfigurationImpl;

public class VerifierBuilderImpl
    implements
    VerifierBuilder {

    private List<VerifierBuilderError> errors = new ArrayList<>();

    public VerifierConfiguration newVerifierConfiguration() {
        return new VerifierConfigurationImpl();
    }

    public Verifier newVerifier() {
        return new VerifierImpl();
    }

    public Verifier newVerifier(VerifierConfiguration conf) {
        return new VerifierImpl( conf );
    }

    public List<VerifierBuilderError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

}
