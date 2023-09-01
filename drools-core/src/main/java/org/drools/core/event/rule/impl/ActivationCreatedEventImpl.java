package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.Match;

public class ActivationCreatedEventImpl extends ActivationEventImpl implements MatchCreatedEvent {

    public ActivationCreatedEventImpl(Match activation, KieRuntime kruntime ) {
        super( activation, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ActivationCreatedEventImpl() {
        super();
    }

    @Override
    public String toString() {
        return "==>[ActivationCreatedEvent: getActivation()=" + getMatch()
                + ", getKnowledgeRuntime()=" + getKieRuntime() + "]";
    }    
}
