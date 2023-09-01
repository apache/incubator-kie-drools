package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.Match;


public class BeforeActivationFiredEventImpl extends ActivationEventImpl implements BeforeMatchFiredEvent {

    private long timestamp;

    public BeforeActivationFiredEventImpl(Match activation, KieRuntime kruntime ) {
        super( activation, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public BeforeActivationFiredEventImpl() {
        super();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "==>[BeforeActivationFiredEvent:  getActivation()=" + getMatch()
                + ", getKnowledgeRuntime()=" + getKieRuntime() + "]";
    }    
}
