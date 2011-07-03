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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.RuntimeDroolsException;
import org.drools.common.InternalRuleBase;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateObjectType;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.builder.PatternBuilder;
import org.drools.rule.EntryPoint;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.ObjectType;

public class FactTemplateTypeConf
    implements
    ObjectTypeConf,
    Externalizable {

    private static final long serialVersionUID = 510l;

    private FactTemplate      factTemplate;
    private ObjectTypeNode    concreteObjectTypeNode;
    private ObjectTypeNode[]  cache;
    
    private boolean          tmsEnabled;


    public FactTemplateTypeConf() {
    }

    public FactTemplateTypeConf(final EntryPoint entryPoint,
                                final FactTemplate factTemplate,
                                final InternalRuleBase ruleBase) {
        this.factTemplate = factTemplate;

        ObjectType objectType = new FactTemplateObjectType( factTemplate );
        this.concreteObjectTypeNode = (ObjectTypeNode) ruleBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );
        if ( this.concreteObjectTypeNode == null ) {
            BuildContext context = new BuildContext( ruleBase,
                                                     ((ReteooRuleBase) ruleBase.getRete().getRuleBase()).getReteooBuilder().getIdGenerator() );
            if ( context.getRuleBase().getConfiguration().isSequential() ) {
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

    public Object getShadow(Object fact) throws RuntimeDroolsException {
        return null;
    }

    public boolean isShadowEnabled() {
        return false;
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

    public TypeDeclaration getTypeDeclaration() {
        return null;
    }

    public boolean isDynamic() {
        return false;
    }
    
    public boolean isTMSEnabled() {
        return this.tmsEnabled;
    }

    public void enableTMS() {
        this.tmsEnabled = true;
    }

}
