package org.drools.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import org.drools.definition.rule.Rule;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.PropagationContext;

public class SerializableActivation
    implements
    Activation,
    Externalizable {
    private Rule                   rule;
    private Collection<? extends FactHandle> factHandles;
    private PropagationContext     propgationContext;

    public SerializableActivation(Activation activation) {
        this.rule = activation.getRule();
        this.factHandles = activation.getFactHandles();
        this.propgationContext = activation.getPropagationContext();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public Rule getRule() {
        return this.rule;
    }

    public Collection<? extends FactHandle> getFactHandles() {
        return this.factHandles;
    }

    public PropagationContext getPropagationContext() {
        return this.propgationContext;
    }
}
