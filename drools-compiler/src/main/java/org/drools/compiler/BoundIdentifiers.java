package org.drools.compiler;

import java.util.Collections;
import java.util.Map;

import org.drools.base.EvaluatorWrapper;

public class BoundIdentifiers {
    private Map<String, Class< ? >> declarations;
    private Map<String, Class< ? >> globals;
    private Map<String, EvaluatorWrapper>  operators;
    private Class< ? >              thisClass;

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            Map<String, Class< ? >> globals) {
        this( declarations,
              globals,
              null,
              null );
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            Map<String, Class< ? >> globals,
                            Map<String, EvaluatorWrapper> operators) {
        this( declarations,
              globals,
              operators,
              null );
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            Map<String, Class< ? >> globals,
                            Map<String, EvaluatorWrapper> operators,
                            Class< ? > thisClass) {
        this.declarations = declarations;
        this.globals = globals;
        this.operators = operators;
        this.thisClass = thisClass;
    }

    public Map<String, Class< ? >> getDeclarations() {
        return declarations;
    }

    public Map<String, Class< ? >> getGlobals() {
        return globals;
    }

    @SuppressWarnings("unchecked")
    public Map<String, EvaluatorWrapper> getOperators() {
        return operators != null ? operators : Collections.EMPTY_MAP;
    }

    public Class< ? > getThisClass() {
        return thisClass;
    }

    public String toString() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append( "thisClass: " + thisClass + "\n" );
        sbuilder.append( "declarations:" + declarations + "\n" );
        sbuilder.append( "globals:" + globals + "\n" );
        sbuilder.append( "operators:" + operators + "\n" );
        return sbuilder.toString();
    }

}
