package org.drools.process.core.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.process.core.Variable;
import org.drools.process.core.VariableScope;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class VariableScopeImpl implements VariableScope, Serializable {

    private static final long serialVersionUID = 400L;
    
    private List<Variable> variables;
    
    public VariableScopeImpl() {
        this.variables = new ArrayList<Variable>();
    }

    public List<Variable> getVariables() {
        return this.variables;
    }

    public void setVariables(final List<Variable> variables) {
        if ( variables == null ) {
            throw new IllegalArgumentException( "Variables is null" );
        }
        this.variables = variables;
    }

    public String[] getVariableNames() {
        final String[] result = new String[this.variables.size()];
        if (this.variables != null) {
            for ( int i = 0; i < this.variables.size(); i++ ) {
                result[i] = ((Variable) this.variables.get( i )).getName();
            }
        }
        return result;
    }

}
