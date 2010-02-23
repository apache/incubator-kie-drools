package org.drools.verifier.builder;

import java.util.List;

import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.report.VerifierReportConfiguration;

public interface VerifierBuilder {

    public VerifierConfiguration newVerifierConfiguration();

    public VerifierReportConfiguration newVerifierReportConfiguration();

    public Verifier newVerifier();

    public Verifier newVerifier(VerifierConfiguration conf);

    boolean hasErrors();

    List<VerifierBuilderError> getErrors();

}
