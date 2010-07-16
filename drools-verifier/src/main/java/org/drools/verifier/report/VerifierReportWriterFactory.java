/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
