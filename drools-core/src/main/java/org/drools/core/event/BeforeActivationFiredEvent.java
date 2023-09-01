package org.drools.core.event;


import org.drools.core.rule.consequence.InternalMatch;

public class BeforeActivationFiredEvent extends ActivationEvent {
    private static final long serialVersionUID = 510l;

    public BeforeActivationFiredEvent(final InternalMatch internalMatch) {
        super(internalMatch);
    }

    public String toString() {
        return "[BeforeActivationFired(" + getActivation().getActivationNumber() + "): rule=" + getActivation().getRule().getName() + "; tuple=" + getActivation().getTuple() + "]";
    }
}
