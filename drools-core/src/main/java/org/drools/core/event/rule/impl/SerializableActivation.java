package org.drools.core.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.base.rule.Declaration;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.common.PropagationContext;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

public class SerializableActivation
    implements
    Match,
    Externalizable {
    private Rule                              rule;
    private Declaration[]                     declarations;
    private List< ? extends FactHandle>       factHandles;
    private PropagationContext                propgationContext;
    private boolean                           active;

    private int                               salience;

    public SerializableActivation() {
        
    }
    
    public SerializableActivation(Match activation) {
        this.rule = activation.getRule();
        this.factHandles = activation.getFactHandles();
        this.propgationContext = ((InternalMatch)activation).getPropagationContext();
        if ( activation instanceof RuleTerminalNodeLeftTuple) {
            declarations = ((org.drools.core.reteoo.RuleTerminalNode)((RuleTerminalNodeLeftTuple)activation).getTuple().getTupleSink()).getAllDeclarations();
        } else if ( activation instanceof SerializableActivation ) {
            this.declarations = ((SerializableActivation)activation).declarations;
        } else {
            throw new RuntimeException("Unable to get declarations " + activation);
        }
        this.active = ((InternalMatch)activation).isQueued();
        this.salience = activation.getSalience();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        rule = (Rule) in.readObject();
        declarations = (Declaration[]) in.readObject();
        factHandles = (List<? extends FactHandle>) in.readObject();
        propgationContext = (PropagationContext) in.readObject();
        active = in.readBoolean();
        salience = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(rule);
        out.writeObject(declarations);
        out.writeObject(factHandles);
        out.writeObject(propgationContext);
        out.writeBoolean(active);
        out.writeInt(salience);
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
        List<Object> objects = new ArrayList<>( this.factHandles.size() );
        for( FactHandle handle : this.factHandles ) {
            objects.add( ((InternalFactHandle)handle).getObject() );
        }
        return Collections.unmodifiableList( objects );
    }

    public Object getDeclarationValue(String variableName) {
        Declaration decl = ((RuleImpl)this.rule).getDeclaration( variableName );
        return decl.getValue( null, ((InternalFactHandle)factHandles.get(decl.getObjectIndex())).getObject());
    }

    public List<String> getDeclarationIds() {
        List<String> decls = new ArrayList<>();
        for( Declaration decl : this.declarations ) {
            decls.add( decl.getIdentifier() );
        }
        return Collections.unmodifiableList( decls );
    }
    
    public boolean isActive() {
        return active;
    }

    @Override
    public int getSalience() {
        return salience;
    }
}
