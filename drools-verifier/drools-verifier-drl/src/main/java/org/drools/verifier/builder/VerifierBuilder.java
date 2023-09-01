package org.drools.verifier.builder;

import java.util.List;

import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;

public interface VerifierBuilder {

    VerifierConfiguration newVerifierConfiguration();

    Verifier newVerifier();

    Verifier newVerifier(VerifierConfiguration conf);

    boolean hasErrors();

    List<VerifierBuilderError> getErrors();

}
