package org.drools.verifier.report;

import java.io.IOException;
import java.io.OutputStream;

import org.drools.verifier.data.VerifierReport;

public interface VerifierReportWriter {

    public void writeReport(OutputStream out,
                            VerifierReport result) throws IOException;
}
