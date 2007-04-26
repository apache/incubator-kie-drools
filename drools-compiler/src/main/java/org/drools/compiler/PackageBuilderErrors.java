package org.drools.compiler;


public class PackageBuilderErrors {
    private DroolsError[] errors;

    public PackageBuilderErrors(DroolsError[] errors) {
        this.errors = errors;
    }

    public DroolsError[]  getErrors() {
        return errors;
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        for ( int i = 0, length = this.errors.length; i < length; i++) {
            buf.append( errors[i] );
        }
        return buf.toString();        
    }
    
    
}
