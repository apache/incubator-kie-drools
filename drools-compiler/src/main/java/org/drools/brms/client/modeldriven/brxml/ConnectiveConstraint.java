package org.drools.brms.client.modeldriven.brxml;


/**
 * This is for a connective constraint that adds more options to a field constraint. 
 * @author Michael Neale
 */
public class ConnectiveConstraint
    implements
    PortableObject {
    
    public static final int UNDEFINED_CONNECTIVE = 0;
    public static final int OR_CONNECTIVE = 1;
    public static final int AND_CONNECTIVE = 2;

    public ConnectiveConstraint() {}
    
    public ConnectiveConstraint(String opr,
                                String val) {
        this.operator = opr;
        this.value = val;
    }
    public int connectiveType;
    public String operator;
    public String value;
    public int constraintType;
    
}
