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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.common.AgendaItem;
import org.drools.common.InternalFactHandle;
import org.drools.definition.rule.Rule;
import org.drools.rule.Declaration;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.PropagationContext;

public class SerializableActivation
    implements
    Activation,
    Externalizable {
    private Rule                              rule;
    private Declaration[]                     declarations;
    private List< ? extends FactHandle>       factHandles;
    private PropagationContext                propgationContext;

    public SerializableActivation() {
        
    }
    
    public SerializableActivation(Activation activation) {
        this.rule = activation.getRule();
        this.factHandles = activation.getFactHandles();
        this.propgationContext = activation.getPropagationContext();
        if ( activation instanceof AgendaItem ) {
            declarations = ((org.drools.reteoo.RuleTerminalNode)((AgendaItem)activation).getTuple().getLeftTupleSink()).getDeclarations();
        } else if ( activation instanceof SerializableActivation ) {
            this.declarations = ((SerializableActivation)activation).declarations;
        } else {
            throw new RuntimeException("Unable to get declarations " + activation);
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public Rule getRule() {
        return this.rule;
    }

    public List< ? extends FactHandle> getFactHandles() {
        return this.factHandles;
    }

    public PropagationContext getPropagationContext() {
        return this.propgationContext;
    }

    public List<Object> getObjects() {
        List<Object> objects = new ArrayList<Object>( this.factHandles.size() );
        for( FactHandle handle : this.factHandles ) {
            objects.add( ((InternalFactHandle)handle).getObject() );
        }
        return Collections.unmodifiableList( objects );
    }

    public Object getDeclarationValue(String variableName) {
        Declaration decl = ((org.drools.rule.Rule)this.rule).getDeclaration( variableName );
        return decl.getValue( null, ((InternalFactHandle)factHandles.get(decl.getPattern().getOffset())).getObject() );
    }

    public List<String> getDeclarationIDs() {
        List<String> decls = new ArrayList<String>();
        for( Declaration decl : this.declarations ) {
            decls.add( decl.getIdentifier() );
        }
        return Collections.unmodifiableList( decls );
    }
}
