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

package org.drools.core.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.ObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.facttemplates.Event;
import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.impl.RuleBase;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.accessor.FactHandleFactory;

public class FactTemplateTypeConf
    implements
    ObjectTypeConf,
    Externalizable {

    private static final long serialVersionUID = 510l;

    private transient RuleBase ruleBase;

    private ObjectType        objectType;
    private FactTemplate      factTemplate;
    private ObjectTypeNode    concreteObjectTypeNode;
    private ObjectTypeNode[]  cache;
    
    private boolean          tmsEnabled;
    
    EntryPointId entryPoint;


    public FactTemplateTypeConf() {
    }

    public FactTemplateTypeConf(final EntryPointId entryPoint,
                                final FactTemplate factTemplate,
                                final RuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.factTemplate = factTemplate;
        this.entryPoint = entryPoint;
        this.objectType = new FactTemplateObjectType( factTemplate );
        this.concreteObjectTypeNode = ruleBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        ruleBase = (RuleBase) in.readObject();
        factTemplate = (FactTemplate) in.readObject();
        concreteObjectTypeNode = (ObjectTypeNode) in.readObject();
        cache = (ObjectTypeNode[]) in.readObject();
        objectType = (ObjectType) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( ruleBase );
        out.writeObject( factTemplate );
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
        return this.factTemplate.equals( object );
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
    	return factTemplate.getName();
    }

    @Override
    public InternalFactHandle createFactHandle(FactHandleFactory factHandleFactory, long id, Object object, long recency,
                                               ReteEvaluator reteEvaluator, WorkingMemoryEntryPoint entryPoint) {
        Fact fact = (Fact) object;
        if (fact.isEvent()) {
            Event event = (Event) fact;
            long timestamp = event.getTimestamp() >= 0 ? event.getTimestamp() : reteEvaluator.getTimerService().getCurrentTime();
            return factHandleFactory.createEventFactHandle(id, object, recency, entryPoint, timestamp, 0);
        }
        return factHandleFactory.createDefaultFactHandle(id, object, recency, entryPoint);
    }
}
