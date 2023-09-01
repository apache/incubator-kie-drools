package org.drools.core.event;

import org.drools.core.rule.consequence.InternalMatch;

public class ActivationCreatedEvent extends ActivationEvent {
    private static final long serialVersionUID = 510L;

    public ActivationCreatedEvent(final InternalMatch internalMatch) {
        super(internalMatch);
    }

    @Override
    public String toString() {
        return "==>[ActivationCreated(" + getActivation().getActivationNumber() + "): rule=" + getActivation().getRule().getName() + "; tuple=" + getActivation().getTuple() + "]";
    }
}
