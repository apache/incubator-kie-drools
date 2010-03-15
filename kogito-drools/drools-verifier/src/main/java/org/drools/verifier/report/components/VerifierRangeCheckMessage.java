package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class VerifierRangeCheckMessage extends VerifierMessageBase {
    private static final long        serialVersionUID = -2403507929285633672L;

    private Collection<MissingRange> causes;

    public VerifierRangeCheckMessage(Severity severity,
                                     Cause faulty,
                                     String message,
                                     Collection<MissingRange> causes) {
        super( new HashMap<String, String>(),
               severity,
               MessageType.RANGE_CHECK,
               faulty,
               message );

        this.causes = causes;
    }

    public Collection<MissingRange> getMissingRanges() {
        return causes;
    }

    public Collection<Cause> getCauses() {
        Collection<Cause> causes = new ArrayList<Cause>();
        for ( Cause cause : this.causes ) {
            causes.add( cause );
        }
        return causes;
    }

    public void setCauses(Collection<MissingRange> reasons) {
        this.causes = reasons;
    }
}
