package org.drools.verifier.report.components;

import java.util.Arrays;
import java.util.Collection;

/**
 * Pattern, rule or similar that is always satisfied.
 * 
 * @author trikkola
 * 
 */
public class AlwaysTrue
    implements
    Reason,
    Cause {

    private static int              index = 0;

    private final String            path  = String.valueOf( index++ );

    private final Cause             impactedComponent;

    private final Collection<Cause> causes;

    /**
     * 
     * @param cause
     *            Component that is always satisfied.
     */
    public AlwaysTrue(Cause cause,
                      Collection<Cause> causes) {
        this.impactedComponent = cause;
        this.causes = causes;
    }

    public AlwaysTrue(Cause cause,
                      Cause... causes) {
        this.impactedComponent = cause;
        this.causes = Arrays.asList( causes );
    }

    public ReasonType getReasonType() {
        return ReasonType.ALWAYS_TRUE;
    }

    public String getPath() {
        return path;
    }

    public Cause getCause() {
        return impactedComponent;
    }

    public Collection<Cause> getCauses() {
        return causes;
    }

}
