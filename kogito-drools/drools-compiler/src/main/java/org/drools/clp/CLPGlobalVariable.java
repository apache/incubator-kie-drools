package org.drools.clp;


public class CLPGlobalVariable extends BaseValueHandler implements VariableValueHandler  {
    
    private String identifier;
    private Class knownType;
    private CLPFactory factory;
       
    public CLPGlobalVariable(String identifier,
                                    Class knownType,
                                    CLPFactory factory ) {
        this.identifier = identifier;
        this.factory =  factory;
        this.knownType = knownType;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }

    public Class getKnownType() {
        return this.knownType;
    }

    public Object getValue(ExecutionContext context) {
        return this.factory.getValue( this.identifier );
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getIdentifier() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }       
    
    public String toString() {
        String name = getClass().getName();
        name = name.substring( name.lastIndexOf( "." ) + 1 );
        return "[" + name + " identifier = '" + getIdentifier()  + "']";
    }        

}
