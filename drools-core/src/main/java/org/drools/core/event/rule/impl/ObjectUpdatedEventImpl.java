/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.event.rule.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.PropagationContext;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;

public class ObjectUpdatedEventImpl  extends RuleRuntimeEventImpl implements ObjectUpdatedEvent {
    private FactHandle factHandle;
    private Object      object;
    private Object      oldObject;
    
    public ObjectUpdatedEventImpl(final KieRuntime kruntime,
                                  final PropagationContext propagationContext,
                                  final FactHandle handle,
                                  final Object oldObject,
                                  final Object object) {
        super( kruntime, propagationContext );
        this.factHandle = handle;
        this.oldObject = oldObject;
        this.object = object;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ObjectUpdatedEventImpl() {
        super();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( factHandle );
        out.writeObject( object );
        out.writeObject( oldObject );
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.factHandle = ( FactHandle ) in.readObject();
        this.object = in.readObject();
        this.oldObject = in.readObject();
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
    public Object getOldObject() {
        return this.oldObject;
    }

    @Override
    public String toString() {
        return "==>[ObjectUpdatedEventImpl: getFactHandle()=" + getFactHandle() + ", getObject()=" + getObject()
                + ", getOldObject()=" + getOldObject() + ", getKnowledgeRuntime()=" + getKieRuntime()
                + ", getPropagationContext()=" + getPropagationContext() + "]";
    }
}
