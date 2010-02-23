package org.drools.verifier.builder;

import java.util.ArrayList;
import java.util.List;

import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierConfigurationImpl;
import org.drools.verifier.report.VerifierReportConfiguration;
import org.drools.verifier.report.VerifierReportConfigurationImpl;

public class VerifierBuilderImpl
    implements
    VerifierBuilder {

    private List<VerifierBuilderError> errors = new ArrayList<VerifierBuilderError>();

    public VerifierConfiguration newVerifierConfiguration() {
        return new VerifierConfigurationImpl();
    }

    public VerifierReportConfiguration newVerifierReportConfiguration() {
        return new VerifierReportConfigurationImpl();
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
