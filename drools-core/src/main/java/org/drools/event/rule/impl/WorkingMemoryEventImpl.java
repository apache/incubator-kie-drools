package org.drools.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.event.rule.WorkingMemoryEvent;
import org.drools.runtime.rule.PropagationContext;
import org.drools.runtime.rule.WorkingMemory;

public class WorkingMemoryEventImpl implements WorkingMemoryEvent, Externalizable {
    private WorkingMemory ruleRuntime;
    
    private PropagationContext propagationContext;
    
    public void writeExternal(ObjectOutput out) throws IOException {
        new SerializablePropagationContext( propagationContext ).writeExternal( out );
    } 
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {        
        this.ruleRuntime = null; // null because we don't serialise this
        this.propagationContext = new SerializablePropagationContext();
        ((SerializablePropagationContext)this.propagationContext).readExternal( in );
    }    
    
    public WorkingMemoryEventImpl(WorkingMemory ruleRuntime, PropagationContext propagationContext) {
        this.ruleRuntime = ruleRuntime;
        this.propagationContext = propagationContext;
    }
    
    public WorkingMemory getWorkingMemory() {
        return this.ruleRuntime;
    }
    
    public PropagationContext getPropagationContext() {
        return this.propagationContext;
    }   
}
