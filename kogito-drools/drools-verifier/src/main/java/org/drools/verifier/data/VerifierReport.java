package org.drools.verifier.data;

import java.util.Collection;

import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.MissingRange;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 */
public interface VerifierReport {

    public void add(Gap gap);

    public void remove(Gap gap);

    public void add(MissingNumberPattern missingNumberPattern);

    public VerifierData getVerifierData(VerifierData data);

    public VerifierData getVerifierData();

    public Collection<MissingRange> getRangeCheckCauses();

    public Collection<Gap> getGapsByFieldId(String fieldId);

    public void add(VerifierMessageBase note);

    /**
     * Return all the items that have given severity value.
     * 
     * @param severity
     *            Severity level of item.
     * @return Collection of items or an empty list if none was found.
     */
    public Collection<VerifierMessageBase> getBySeverity(Severity severity);

    public Collection<MissingRange> getRangeCheckCausesByFieldPath(String path);

}
