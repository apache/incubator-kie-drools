package org.drools.factmodel.traits;


public class LogicalTypeInconsistencyException extends Exception {

    private Class type1;

    private Class type2;

    public LogicalTypeInconsistencyException( String message, Class type1, Class type2 ) {
        super( message );
        this.type1 = type1;
        this.type2 = type2;
    }

}
