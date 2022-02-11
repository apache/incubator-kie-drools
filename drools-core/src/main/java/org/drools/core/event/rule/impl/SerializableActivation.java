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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

public class SerializableActivation implements Match, Externalizable {

    private Rule rule;
    private Map<Declaration, Object> declarations;
    private PropagationContext propgationContext;
    private boolean active;

    public SerializableActivation() {
        
    }
    
    public SerializableActivation(Match activation) {
        this.rule = activation.getRule();
        this.propgationContext = ((Activation)activation).getPropagationContext();
        if ( activation instanceof AgendaItemImpl) {
            Tuple tuple = ((AgendaItem)activation).getTuple();
            this.declarations = extractObjectsFromDeclaration(tuple, ((RuleTerminalNode) tuple.getTupleSink()).getAllDeclarations());
        } else if ( activation instanceof SerializableActivation ) {
            this.declarations = ((SerializableActivation)activation).declarations;
        } else if ( activation instanceof RuleTerminalNodeLeftTuple) {
            RuleTerminalNodeLeftTuple tuple = (RuleTerminalNodeLeftTuple) activation;
            this.declarations = extractObjectsFromDeclaration(tuple, ((RuleTerminalNode) tuple.getTupleSink()).getAllDeclarations());
        } else {
            throw new RuntimeException("Unable to get declarations " + activation);
        }
        this.active = ((Activation)activation).isQueued();
    }

    private Map<Declaration, Object> extractObjectsFromDeclaration(Tuple tuple, Declaration[] declarationsArray) {
        Map<Declaration, Object> declarations = new HashMap<>();
        for (Declaration declaration : declarationsArray) {
            declarations.put(declaration, tuple.get(declaration).getObject());
        }
        return declarations;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.rule = (Rule) in.readObject();
        this.propgationContext = (PropagationContext) in.readObject();
        this.declarations = (Map<Declaration, Object>) in.readObject();
        this.active = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.rule);
        out.writeObject(this.propgationContext);
        out.writeObject(this.declarations);
        out.writeBoolean(active);
    }

    public Rule getRule() {
        return this.rule;
    }

    public List< ? extends FactHandle> getFactHandles() {
        throw new UnsupportedOperationException();
    }

    public PropagationContext getPropagationContext() {
        return this.propgationContext;
    }

    public List<Object> getObjects() {
        throw new UnsupportedOperationException();
    }

    public Object getDeclarationValue(String variableName) {
        throw new UnsupportedOperationException();
    }

    public Collection<Declaration> getDeclarations() {
        return declarations.keySet();
    }

    public Object getObject(Declaration declaration) {
        return declarations.get(declaration);
    }

    public List<String> getDeclarationIds() {
        List<String> decls = new ArrayList<>();
        for( Declaration decl : this.declarations.keySet() ) {
            decls.add( decl.getIdentifier() );
        }
        return Collections.unmodifiableList( decls );
    }
    
    public boolean isActive() {
        return active;
    }        
}
