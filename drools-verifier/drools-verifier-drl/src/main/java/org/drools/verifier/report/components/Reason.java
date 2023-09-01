package org.drools.verifier.report.components;

import java.util.Collection;

/**
 * 
 * Reason why something is wrong.
 */
public interface Reason {

    public Collection<Cause> getCauses();

    public ReasonType getReasonType();
}
