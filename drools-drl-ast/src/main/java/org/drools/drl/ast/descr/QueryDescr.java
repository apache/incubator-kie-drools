package org.drools.drl.ast.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryDescr extends RuleDescr {
    private static final long serialVersionUID = 520l;

    private List<String>      parameterTypes   = Collections.emptyList();
    private List<String>      parameterNames   = Collections.emptyList();
    
    public QueryDescr() {
        this( null,
              "" );
    }

    public QueryDescr(final String name) {
        this( name,
              "" );
    }

    public QueryDescr(final String ruleName,
                      final String documentation) {
        super( ruleName,
               documentation );
    }
    
    public void addParameter( String type, String variable ) {
        if( parameterTypes == Collections.EMPTY_LIST ) {
            this.parameterTypes = new ArrayList<>();
            this.parameterNames = new ArrayList<>();
        }
        this.parameterTypes.add( type );
        this.parameterNames.add( variable );
    }
    
    public String[] getParameters() {
        return this.parameterNames.toArray( new String[this.parameterNames.size()] );
    }
    
    public String[] getParameterTypes() {
        return this.parameterTypes.toArray( new String[this.parameterTypes.size()] );
    }

    public boolean isRule() {
        return false;
    }
    
    public boolean isQuery() {
        return true;
    }

    public String toString() {
        return "[Query name='" + getName() + "']";
    }
}
