package org.drools.verifier.report;

import org.drools.verifier.report.html.HTMLReportWriter;

public class VerifierReportWriterFactory {

    public static VerifierReportWriter newPDFReportWriter() {
        // TODO Auto-generated method stub
        return null;
    }

    public static VerifierReportWriter newHTMLReportWriter() {
        return new HTMLReportWriter();
    }

    /**
     * Returns the verifier results as plain text.
     * 
     * @return Analysis results as plain text.
     */
    public static VerifierReportWriter newPlainTextReportWriter() {
        return new PlainTextReportWriter();
    }

    /**
     * Returns the verifier results as XML.
     * 
     * @return Analysis results as XML
     */
    public static VerifierReportWriter newXMLReportWriter() {
        return new XMLReportWriter();
    }
}
