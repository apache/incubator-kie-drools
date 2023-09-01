package org.drools.verifier.report.components;

import java.util.Collection;

/**
 *
 * Cause for a Reason.
 */
public interface Cause {

    public Collection<Cause> getCauses();

}
