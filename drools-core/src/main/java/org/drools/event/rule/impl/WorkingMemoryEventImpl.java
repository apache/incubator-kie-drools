/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.event.rule.impl;

import org.kie.event.rule.WorkingMemoryEvent;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.rule.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class WorkingMemoryEventImpl implements WorkingMemoryEvent, Externalizable {
    private KnowledgeRuntime kruntime;
    
    private PropagationContext propagationContext;
    
    public WorkingMemoryEventImpl() {
        
    }
                                      
    public WorkingMemoryEventImpl(KnowledgeRuntime kruntime, PropagationContext propagationContext) {
        this.kruntime = kruntime;
        this.propagationContext = propagationContext;
    }
    
    public KnowledgeRuntime getKieRuntime() {
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
        return "==>[WorkingMemoryEventImpl: getKnowledgeRuntime()=" + getKieRuntime()
                + ", getPropagationContext()=" + getPropagationContext() + "]";
    }
}
