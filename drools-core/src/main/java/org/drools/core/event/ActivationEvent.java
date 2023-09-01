package org.drools.core.event;

import java.util.EventObject;

import org.drools.core.rule.consequence.InternalMatch;

public class ActivationEvent extends EventObject {

    private static final long serialVersionUID = 510l;

    public ActivationEvent(final InternalMatch internalMatch) {
        super(internalMatch);
    }

    public InternalMatch getActivation() {
        return (InternalMatch) getSource();
    }

}
