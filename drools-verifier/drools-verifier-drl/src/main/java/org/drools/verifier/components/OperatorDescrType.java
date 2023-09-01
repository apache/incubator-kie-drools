package org.drools.verifier.components;

public class OperatorDescrType {
    public static final OperatorDescrType AND = new OperatorDescrType( "AND" );
    public static final OperatorDescrType OR  = new OperatorDescrType( "OR" );

    protected final String                type;

    private OperatorDescrType(String t) {
        type = t;
    }

}
