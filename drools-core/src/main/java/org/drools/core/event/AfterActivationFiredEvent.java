package org.drools.core.event;

import org.drools.core.rule.consequence.InternalMatch;

public class AfterActivationFiredEvent extends ActivationEvent {
    private static final long serialVersionUID = 510l;

    public AfterActivationFiredEvent(final InternalMatch internalMatch) {
        super(internalMatch);
    }

    public String toString() {
        return "[AfterActivationFired(" + getActivation().getActivationNumber() + "): rule=" + getActivation().getRule().getName() + "; tuple=" + getActivation().getTuple() + "]";
    }
}
