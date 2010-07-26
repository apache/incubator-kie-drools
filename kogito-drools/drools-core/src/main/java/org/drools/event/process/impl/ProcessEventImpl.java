/**
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

package org.drools.event.process.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.event.process.ProcessEvent;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.ProcessInstance;

public class ProcessEventImpl implements ProcessEvent, Externalizable {

    private ProcessInstance processInstance;
    
    private KnowledgeRuntime kruntime;
    
    public ProcessEventImpl() {
        
    }

    public ProcessEventImpl(org.drools.event.ProcessEvent event, WorkingMemory workingMemory) {
        this.processInstance = event.getProcessInstance();
        this.kruntime =  ((InternalWorkingMemory)workingMemory).getKnowledgeRuntime();
        
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( processInstance );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.processInstance = (ProcessInstance) in.readObject();
        this.kruntime = null; // null because we don't serialise this
    }

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

    public KnowledgeRuntime getKnowledgeRuntime() {
        return this.kruntime;
    }

	@Override
	public String toString() {
		return "==>[ProcessEventImpl: getProcessInstance()=" + getProcessInstance() + ", getKnowledgeRuntime()="
				+ getKnowledgeRuntime() + "]";
	}
}
