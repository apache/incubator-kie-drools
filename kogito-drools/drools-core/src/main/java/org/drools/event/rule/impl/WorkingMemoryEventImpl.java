package org.drools.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.event.rule.WorkingMemoryEvent;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.PropagationContext;

public class WorkingMemoryEventImpl implements WorkingMemoryEvent, Externalizable {
    private KnowledgeRuntime kruntime;
    
    private PropagationContext propagationContext;     
    
    public WorkingMemoryEventImpl() {
        
    }
                                      
    public WorkingMemoryEventImpl(KnowledgeRuntime kruntime, PropagationContext propagationContext) {
        this.kruntime = kruntime;
        this.propagationContext = propagationContext;
    }
    
    public KnowledgeRuntime getKnowledgeRuntime() {
        return this.kruntime;
    }
    
    public PropagationContext getPropagationContext() {
        return this.propagationContext;
    }   
    
    public void writeExternal(ObjectOutput out) throws IOException {
        new SerializablePropagationContext( propagationContext ).writeExternal( out );
    } 
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {        
        this.kruntime = null; // null because we don't serialise this
        this.propagationContext = new SerializablePropagationContext();
        ((SerializablePropagationContext)this.propagationContext).readExternal( in );
    }

	@Override
	public String toString() {
		return "==>[WorkingMemoryEventImpl: getKnowledgeRuntime()=" + getKnowledgeRuntime()
				+ ", getPropagationContext()=" + getPropagationContext() + "]";
	}
}
