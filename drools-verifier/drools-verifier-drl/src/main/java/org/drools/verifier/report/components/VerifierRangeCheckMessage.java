package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class VerifierRangeCheckMessage extends VerifierMessageBase {
    private static final long        serialVersionUID = 510l;

    private Collection<MissingRange> causes;

    public VerifierRangeCheckMessage(Severity severity,
                                     Cause faulty,
                                     String message,
                                     Collection<MissingRange> causes) {
        super( new HashMap<>(),
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
        Collection<Cause> causes = new ArrayList<>();
        causes.addAll(this.causes);
        return causes;
    }

    public void setCauses(Collection<MissingRange> reasons) {
        this.causes = reasons;
    }
}
