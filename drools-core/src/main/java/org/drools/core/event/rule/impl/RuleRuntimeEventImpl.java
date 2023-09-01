package org.drools.core.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.PropagationContext;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.RuleRuntimeEvent;
import org.kie.api.runtime.KieRuntime;

public class RuleRuntimeEventImpl implements RuleRuntimeEvent, Externalizable {
    private KieRuntime kruntime;
    
    private PropagationContext propagationContext;
    
    public RuleRuntimeEventImpl() {
        
    }
                                      
    public RuleRuntimeEventImpl(KieRuntime kruntime, PropagationContext propagationContext) {
        this.kruntime = kruntime;
        this.propagationContext = propagationContext;
    }
    
    public KieRuntime getKieRuntime() {
        return this.kruntime;
    }
    
    public PropagationContext getPropagationContext() {
        return this.propagationContext;
    }

    public Rule getRule() {
        return this.propagationContext != null ? this.getPropagationContext().getRuleOrigin() : null;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( propagationContext );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.propagationContext = (PropagationContext) in.readObject();
    }

    @Override
    public String toString() {
        return "==>[WorkingMemoryEventImpl: getKnowledgeRuntime()=" + getKieRuntime()
                + ", getPropagationContext()=" + getPropagationContext() + "]";
    }
}
