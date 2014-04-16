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

package org.drools.core.reteoo;

import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.PatternBuilder;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FactTemplateTypeConf
    implements
    ObjectTypeConf,
    Externalizable {

    private static final long serialVersionUID = 510l;

    private FactTemplate      factTemplate;
    private ObjectTypeNode    concreteObjectTypeNode;
    private ObjectTypeNode[]  cache;
    
    private boolean          tmsEnabled;
    
    EntryPointId entryPoint;


    public FactTemplateTypeConf() {
    }

    public FactTemplateTypeConf(final EntryPointId entryPoint,
                                final FactTemplate factTemplate,
                                final InternalKnowledgeBase kBase) {
        this.factTemplate = factTemplate;
        this.entryPoint = entryPoint;
        ObjectType objectType = new FactTemplateObjectType( factTemplate );
        this.concreteObjectTypeNode = (ObjectTypeNode) kBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );
        if ( this.concreteObjectTypeNode == null ) {
            BuildContext context = new BuildContext( kBase,
                                                     kBase.getReteooBuilder().getIdGenerator() );
            if ( context.getKnowledgeBase().getConfiguration().isSequential() ) {
                // We are in sequential mode, so no nodes should have memory
                context.setTupleMemoryEnabled( false );
                context.setObjectTypeNodeMemoryEnabled( false );
            } else {
                context.setTupleMemoryEnabled( true );
                context.setObjectTypeNodeMemoryEnabled( true );
            }
            // there must exist an ObjectTypeNode for this concrete class
            this.concreteObjectTypeNode = PatternBuilder.attachObjectTypeNode( context,
                                                                               objectType );
        }
        this.cache = new ObjectTypeNode[]{this.concreteObjectTypeNode};
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        factTemplate = (FactTemplate) in.readObject();
        concreteObjectTypeNode = (ObjectTypeNode) in.readObject();
        cache = (ObjectTypeNode[]) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( factTemplate );
        out.writeObject( concreteObjectTypeNode );
        out.writeObject( cache );
    }

    public ObjectTypeNode getConcreteObjectTypeNode() {
        return this.concreteObjectTypeNode;
    }

    public ObjectTypeNode[] getObjectTypeNodes() {
        if ( this.cache == null ) {
            this.cache = new ObjectTypeNode[]{this.concreteObjectTypeNode};
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
    
    public boolean isTMSEnabled() {
        return this.tmsEnabled;
    }

    public boolean isTraitTMSEnabled() {
        return false;
    }

    public void enableTMS() {
        this.tmsEnabled = true;
    }

    public EntryPointId getEntryPoint() {
        return this.entryPoint;
    }

    public boolean isSupportsPropertyChangeListeners() {
        return false;
    }
    
    @Override
    public String getTypeName() {
    	return factTemplate.getName();
    }

}
