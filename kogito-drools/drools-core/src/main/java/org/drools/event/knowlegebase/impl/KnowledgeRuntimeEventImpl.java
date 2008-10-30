package org.drools.event.knowlegebase.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.event.KnowledgeRuntimeEvent;
import org.drools.runtime.KnowledgeRuntime;

public class KnowledgeRuntimeEventImpl implements KnowledgeRuntimeEvent, Externalizable {
	
    private KnowledgeRuntime knowledgeRuntime;
    
    public KnowledgeRuntimeEventImpl(KnowledgeRuntime knowledgeRuntime) {
        this.knowledgeRuntime = knowledgeRuntime;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
    } 
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {        
        this.knowledgeRuntime = null; // null because we don't serialise this
    }    
    
    public KnowledgeRuntime getKnowledgeRuntime() {
        return knowledgeRuntime;
    }
    
}
