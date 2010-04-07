package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QueryElement extends ConditionalElement
    implements
    Externalizable {
    private String        queryName;
    private Object[]      arguments;
    private int[]         declIndexes;
    private int[]         variables;

    private Declaration[] requiredDeclarations;

    public QueryElement(String queryName,
                        Object[] arguments,
                        Declaration[] requiredDeclarations,
                        int[] declIndexes,
                        int[] variables) {
        super();
        this.queryName = queryName;
        this.arguments = arguments;
        this.requiredDeclarations = requiredDeclarations;
        this.declIndexes = declIndexes;
        this.variables = variables;
    }

    public String getQueryName() {
        return queryName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public int[] getDeclIndexes() {
        return declIndexes;
    }

    public int[] getVariables() {
        return variables;
    }

    public Map getInnerDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public Map getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public List getNestedElements() {
        return Collections.EMPTY_LIST;
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }
    
    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return null;
    }

    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        for ( int i = 0; i < this.requiredDeclarations.length; i++ ) {
            if ( this.requiredDeclarations[i].equals( declaration ) ) {
                this.requiredDeclarations[i] = resolved;
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( arguments );
        result = prime * result + Arrays.hashCode( declIndexes );
        result = prime * result + ((queryName == null) ? 0 : queryName.hashCode());
        result = prime * result + Arrays.hashCode( variables );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        QueryElement other = (QueryElement) obj;
        if ( !Arrays.equals( arguments,
                             other.arguments ) ) return false;
        if ( !Arrays.equals( declIndexes,
                             other.declIndexes ) ) return false;
        if ( queryName == null ) {
            if ( other.queryName != null ) return false;
        } else if ( !queryName.equals( other.queryName ) ) return false;
        if ( !Arrays.equals( variables,
                             other.variables ) ) return false;
        return true;
    }

    @Override
    public Object clone() {
        return new QueryElement( queryName, arguments, requiredDeclarations, declIndexes, variables );
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
       out.writeObject( this.queryName );
       out.writeObject( this.arguments );
       out.writeObject( this.requiredDeclarations );
       out.writeObject( this.declIndexes );
       out.writeObject( this.variables );
    }    

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.queryName = (String) in.readObject();
        this.arguments = (Object[]) in.readObject();
        this.requiredDeclarations = ( Declaration[] ) in.readObject();
        this.declIndexes = ( int[] ) in.readObject();
        this.variables = ( int[] ) in.readObject();
    }



}
