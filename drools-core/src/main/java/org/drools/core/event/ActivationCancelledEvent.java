package org.drools.core.event;

import org.drools.core.rule.consequence.InternalMatch;
import org.kie.api.event.rule.MatchCancelledCause;

public class ActivationCancelledEvent extends ActivationEvent {
    private MatchCancelledCause cause;
    
    private static final long serialVersionUID = 510l;

    public ActivationCancelledEvent(final InternalMatch internalMatch, MatchCancelledCause cause) {
        super(internalMatch);
        this.cause = cause;
    }
    
    public MatchCancelledCause getCause() {
        return cause;
    }

    public String toString() {
        return "<==[ActivationCancelled(" + getActivation().getActivationNumber() + "): rule=" + getActivation().getRule().getName() + "; tuple=" + getActivation().getTuple() + "]";
        //return "<==[ActivationCancelled: rule=" + getActivation().getRule().getName() + "]";
    }
}
