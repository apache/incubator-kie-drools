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
package org.drools.core.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.ObjectType;
import org.drools.base.prototype.PrototypeObjectType;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.TypeDeclaration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.kie.api.prototype.Prototype;
import org.kie.api.prototype.PrototypeEventInstance;
import org.kie.api.prototype.PrototypeFactInstance;

public class PrototypeTypeConf
    implements
    ObjectTypeConf,
    Externalizable {

    private static final long serialVersionUID = 510l;

    private transient InternalRuleBase ruleBase;

    private ObjectType objectType;
    private Prototype prototype;
    private ObjectTypeNode concreteObjectTypeNode;
    private ObjectTypeNode[] cache;
    
    private boolean tmsEnabled;
    
    EntryPointId entryPoint;


    public PrototypeTypeConf() {
    }

    public PrototypeTypeConf(final EntryPointId entryPoint,
                             final Prototype prototype,
                             final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.prototype = prototype;
        this.entryPoint = entryPoint;
        this.objectType = new PrototypeObjectType(prototype);
        this.concreteObjectTypeNode = ruleBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        ruleBase = (InternalRuleBase) in.readObject();
        prototype = (Prototype) in.readObject();
        concreteObjectTypeNode = (ObjectTypeNode) in.readObject();
        cache = (ObjectTypeNode[]) in.readObject();
        objectType = (ObjectType) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( ruleBase );
        out.writeObject( prototype );
        out.writeObject( concreteObjectTypeNode );
        out.writeObject( cache );
        out.writeObject( objectType );
    }

    public ObjectTypeNode getConcreteObjectTypeNode() {
        if (concreteObjectTypeNode == null) {
            concreteObjectTypeNode = ruleBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );
        }
        return this.concreteObjectTypeNode;
    }

    public ObjectTypeNode[] getObjectTypeNodes() {
        if ( this.cache == null ) {
            this.cache = this.concreteObjectTypeNode != null ? new ObjectTypeNode[]{this.concreteObjectTypeNode} : new ObjectTypeNode[0];
        }
        return this.cache;
    }

    public boolean isAssignableFrom(Object object) {
        return this.prototype.equals( object );
    }

    public void resetCache() {
        this.cache = null;
    }

    public boolean isActive() {
        return true;
    }

    public boolean isEvent() {
        return false;
    }

    public boolean isTrait() {
        return false;
    }

    public TypeDeclaration getTypeDeclaration() {
        return null;
    }

    public boolean isDynamic() {
        return false;
    }

    public boolean isPrototype() {
        return true;
    }

    public boolean isTMSEnabled() {
        return this.tmsEnabled;
    }

    public void enableTMS() {
        this.tmsEnabled = true;
    }

    public EntryPointId getEntryPoint() {
        return this.entryPoint;
    }

    @Override
    public String getTypeName() {
        return prototype.getFullName();
    }

    @Override
    public InternalFactHandle createFactHandle(FactHandleFactory factHandleFactory, long id, Object object, long recency,
                                               ReteEvaluator reteEvaluator, WorkingMemoryEntryPoint entryPoint) {
        PrototypeFactInstance fact = (PrototypeFactInstance) object;
        if (fact.isEvent()) {
            PrototypeEventInstance event = (PrototypeEventInstance) fact;
            long timestamp = event.getTimestamp() >= 0 ? event.getTimestamp() : reteEvaluator.getTimerService().getCurrentTime();
            return factHandleFactory.createEventFactHandle(id, object, recency, entryPoint, timestamp, 0);
        }
        return factHandleFactory.createDefaultFactHandle(id, object, recency, entryPoint);
    }
}
