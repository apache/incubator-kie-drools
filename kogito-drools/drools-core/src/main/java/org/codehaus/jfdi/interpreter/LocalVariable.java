package org.codehaus.jfdi.interpreter;

public class LocalVariable implements VariableValueHandler {

    
    private final Class type;
    private Object value;
    private final boolean isFinal;
    private final String identifier;
    
    
    public LocalVariable(String identifier, 
                         Class type,
                         boolean isFinal) {
        this.type = type;
        this.isFinal = isFinal;
        this.identifier = identifier;
    }    

    public Class getType() {
        return this.type;
    }

    public Object getValue() {
        
        return this.value;
    }

    public boolean isFinal() {

        return isFinal;
    }

    public boolean isLiteral() {
        return false;
    }

    public boolean isLocal() {
        return true;
    }

    public void setValue(Object variable) {
        this.value = variable;
        
    }

    public String getIdentifier() {
        return this.identifier;
    }

}
