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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.FactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.event.rule.ObjectInsertedEvent;

public class ObjectInsertedEventImpl extends WorkingMemoryEventImpl
    implements
    ObjectInsertedEvent {
    private FactHandle  factHandle;
    private Object      object;

    public ObjectInsertedEventImpl(org.drools.event.ObjectInsertedEvent event) {
        super( ((InternalWorkingMemory) event.getWorkingMemory() ).getKnowledgeRuntime(), event.getPropagationContext() );
        factHandle = event.getFactHandle();
        object = event.getObject();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( factHandle );
        out.writeObject( object );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.factHandle = ( FactHandle ) in.readObject();
        this.object = in.readObject();
    }

    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public Object getObject() {
        return this.object;
    }

	@Override
	public String toString() {
		return "==>[ObjectInsertedEventImpl: getFactHandle()=" + getFactHandle() + ", getObject()=" + getObject()
				+ ", getKnowledgeRuntime()=" + getKnowledgeRuntime() + ", getPropagationContext()="
				+ getPropagationContext() + "]";
	}
}
