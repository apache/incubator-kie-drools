package org.drools.process.core.context.variable;

import java.util.ArrayList;
import java.util.List;

import org.drools.process.core.Context;
import org.drools.process.core.context.AbstractContext;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class VariableScope extends AbstractContext {

    public static final String VARIABLE_SCOPE = "VariableScope";
    
    private static final long serialVersionUID = 400L;
    
    private List<Variable> variables;
    
    public VariableScope() {
        this.variables = new ArrayList<Variable>();
    }
    
    public String getType() {
        return VariableScope.VARIABLE_SCOPE;
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

    public Variable findVariable(String variableName) {
        for (Variable variable: getVariables()) {
            if (variable.getName().equals(variableName)) {
                return variable;
            }
        }
        return null;
    }

    public Context resolveContext(Object param) {
        if (param instanceof String) {
            return findVariable((String) param) == null ? null : this;
        }
        throw new IllegalArgumentException(
            "VariableScopes can only resolve variable names: " + param);
    }

}
