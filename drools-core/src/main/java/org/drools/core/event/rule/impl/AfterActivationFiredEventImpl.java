package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.Match;

public class AfterActivationFiredEventImpl  extends ActivationEventImpl implements AfterMatchFiredEvent {

    BeforeMatchFiredEvent beforeMatchFiredEvent;

    public AfterActivationFiredEventImpl(Match activation, KieRuntime kruntime, BeforeMatchFiredEvent beforeMatchFiredEvent) {
        super( activation, kruntime );
        this.beforeMatchFiredEvent = beforeMatchFiredEvent;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public AfterActivationFiredEventImpl() {
        super();
    }

    public BeforeMatchFiredEvent getBeforeMatchFiredEvent() {
        return beforeMatchFiredEvent;
    }

    @Override
    public String toString() {
        return "==>[AfterActivationFiredEvent: getActivation()=" + getMatch()
                + ", getKnowledgeRuntime()=" + getKieRuntime() + "]";
    }        
    
}
