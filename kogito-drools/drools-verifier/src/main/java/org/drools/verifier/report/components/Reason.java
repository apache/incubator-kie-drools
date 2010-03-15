package org.drools.verifier.report.components;

import java.util.Collection;

/**
 * 
 * Reason why something is wrong.
 * 
 * @author trikkola
 *
 */
public interface Reason {

    public Collection<Cause> getCauses();

    public ReasonType getReasonType();
}
