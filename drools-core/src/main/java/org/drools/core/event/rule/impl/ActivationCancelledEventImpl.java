package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.Match;


public class ActivationCancelledEventImpl extends ActivationEventImpl implements MatchCancelledEvent {
    private MatchCancelledCause cause;
    
    public ActivationCancelledEventImpl( Match activation, KieRuntime kruntime, MatchCancelledCause cause ) {
        super( activation, kruntime);
        this.cause = cause;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ActivationCancelledEventImpl() {
        super();
    }

    public MatchCancelledCause getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "==>[ActivationCancelledEvent: getCause()=" + getCause() + ", getActivation()=" + getMatch()
                + ", getKnowledgeRuntime()=" + getKieRuntime() + "]";
    }

}
