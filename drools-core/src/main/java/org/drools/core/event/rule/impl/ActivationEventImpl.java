package org.drools.core.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.api.event.rule.MatchEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.Match;


public class ActivationEventImpl implements MatchEvent, Externalizable {
    private Match activation;
    private KieRuntime kruntime;
    
    public ActivationEventImpl(Match activation, KieRuntime kruntime) {
        this.activation = activation;
        this.kruntime = kruntime;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ActivationEventImpl() {
        super();
    }

    public Match getMatch() {
        return this.activation;
    }

    public KieRuntime getKieRuntime() {
        return this.kruntime;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        new SerializableActivation( this.activation ).writeExternal( out );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.activation = new SerializableActivation();
        ((SerializableActivation)this.activation).readExternal( in );
        // we null this as it isn't serializable
        this.kruntime = null;
    }

    @Override
    public String toString() {
        return "==>[ActivationEventImpl: getActivation()=" + getMatch() + ", getKnowledgeRuntime()="
                + getKieRuntime() + "]";
    }
}
