/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.event.rule.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.rule.FactHandle;

public class ObjectInsertedEventImpl extends RuleRuntimeEventImpl
    implements
    ObjectInsertedEvent {
    private FactHandle  factHandle;
    private Object      object;

    public ObjectInsertedEventImpl(final WorkingMemory workingMemory,
                               final PropagationContext propagationContext,
                               final FactHandle handle,
                               final Object object) {
        super( ((InternalWorkingMemory) workingMemory ).getKnowledgeRuntime(), propagationContext );
        this.factHandle = handle;
        this.object = object;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ObjectInsertedEventImpl() {
        super();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( factHandle );
        out.writeObject( object );
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.factHandle = ( FactHandle ) in.readObject();
        this.object = in.readObject();
    }

    @Override
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    @Override
    public Object getObject() {
        return this.object;
    }

    @Override
    public String toString() {
        return "==>[ObjectInsertedEventImpl: getFactHandle()=" + getFactHandle() + ", getObject()=" + getObject()
                + ", getKnowledgeRuntime()=" + getKieRuntime() + ", getPropagationContext()="
                + getPropagationContext() + "]";
    }
}
