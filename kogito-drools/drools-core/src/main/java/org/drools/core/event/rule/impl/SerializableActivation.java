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

package org.drools.core.event.rule.impl;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SerializableActivation
    implements
    Match,
    Externalizable {
    private Rule                              rule;
    private Declaration[]                     declarations;
    private List< ? extends FactHandle>       factHandles;
    private PropagationContext                propgationContext;
    private boolean                           active;

    public SerializableActivation() {
        
    }
    
    public SerializableActivation(Match activation) {
        this.rule = activation.getRule();
        this.factHandles = activation.getFactHandles();
        this.propgationContext = ((Activation)activation).getPropagationContext();
        if ( activation instanceof AgendaItemImpl) {
            declarations = ((org.drools.core.reteoo.RuleTerminalNode)((AgendaItem)activation).getTuple().getTupleSink()).getAllDeclarations();
        } else if ( activation instanceof SerializableActivation ) {
            this.declarations = ((SerializableActivation)activation).declarations;
        } else {
            throw new RuntimeException("Unable to get declarations " + activation);
        }
        this.active = ((Activation)activation).isQueued();
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
        Declaration decl = ((RuleImpl)this.rule).getDeclaration( variableName );
        return decl.getValue( null, ((InternalFactHandle)factHandles.get(decl.getPattern().getOffset())).getObject() );
    }

    public List<String> getDeclarationIds() {
        List<String> decls = new ArrayList<String>();
        for( Declaration decl : this.declarations ) {
            decls.add( decl.getIdentifier() );
        }
        return Collections.unmodifiableList( decls );
    }
    
    public boolean isActive() {
        return active;
    }        
}
