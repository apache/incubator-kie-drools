package org.drools.verifier.report.components;

import java.util.Collection;

/**
 *
 * Cause for a Reason.
 * 
 * @author Toni Rikkola
 */
public interface Cause {

    public Collection<Cause> getCauses();

}
