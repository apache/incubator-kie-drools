package org.codehaus.jfdi.interpreter;

public interface VariableValueHandler extends ValueHandler {
    
    /** the id of the actual variable in the source. */
    String getIdentifier();

}
