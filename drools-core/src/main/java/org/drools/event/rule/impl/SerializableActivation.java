package org.drools.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List< ? extends FactHandle>       factHandles;
    private PropagationContext                propgationContext;

    public SerializableActivation() {
        
    }
    
    public SerializableActivation(Activation activation) {
        this.rule = activation.getRule();
        this.factHandles = activation.getFactHandles();
        this.propgationContext = activation.getPropagationContext();
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
        Declaration[] declArray = ((org.drools.rule.Rule)this.rule).getDeclarations(); 
        List<String> declarations = new ArrayList<String>();
        for( Declaration decl : declArray ) {
            declarations.add( decl.getIdentifier() );
        }
        return Collections.unmodifiableList( declarations );
    }
}
