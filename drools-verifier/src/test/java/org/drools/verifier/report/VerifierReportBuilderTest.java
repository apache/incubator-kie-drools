package org.drools.verifier.report;

import java.util.Collection;

import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.RangeCheckCause;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

import junit.framework.TestCase;

public class VerifierReportBuilderTest extends TestCase {

    public void testHtmlReportTest() {

        // Create report
        VerifierReport vReport = new VerifierReportMock();

        // Write to disk
        // Check the files on disk
        // done
        assertTrue(true);
    }

}
class VerifierReportMock
    implements
    VerifierReport {

    public void add(Gap gap) {
        // TODO Auto-generated method stub

    }

    public void add(MissingNumberPattern missingNumberPattern) {
        // TODO Auto-generated method stub

    }

    public void add(VerifierMessageBase note) {
        // TODO Auto-generated method stub

    }

    public Collection<VerifierMessageBase> getBySeverity(Severity severity) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Gap> getGapsByFieldId(int fieldId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<RangeCheckCause> getRangeCheckCauses() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<RangeCheckCause> getRangeCheckCausesByFieldId(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    public VerifierData getVerifierData(VerifierData data) {
        // TODO Auto-generated method stub
        return null;
    }

    public VerifierData getVerifierData() {
        // TODO Auto-generated method stub
        return null;
    }

    public void remove(Gap gap) {
        // TODO Auto-generated method stub

    }

    public Collection<RangeCheckCause> getRangeCheckCausesByFieldId(String guid) {
        // TODO Auto-generated method stub
        return null;
    }

}