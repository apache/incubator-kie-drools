package org.drools.compiler;

import java.util.ArrayList;

import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;


public class PackageBuilderErrors extends ArrayList<KnowledgeBuilderError> implements KnowledgeBuilderErrors {
    private DroolsError[] errors;

    public PackageBuilderErrors(DroolsError[] errors) {
        super( errors.length );
        this.errors = errors;
        for ( DroolsError error : errors ) {
            add( error );
        }
    }

    public DroolsError[]  getErrors() {
        return errors;
    }
    
    public boolean isEmpty() {
        return this.errors.length == 0;
    }
    
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        for ( int i = 0, length = this.errors.length; i < length; i++) {
            buf.append( errors[i] );
        }
        return buf.toString();        
    }
    
    
}
