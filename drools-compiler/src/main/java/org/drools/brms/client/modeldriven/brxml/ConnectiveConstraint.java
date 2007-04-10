package org.drools.brms.client.modeldriven.brxml;


/**
 * This is for a connective constraint that adds more options to a field constraint. 
 * @author Michael Neale
 */
public class ConnectiveConstraint
    extends
    IConstraint {

    public ConnectiveConstraint() {}
    
    public ConnectiveConstraint(String opr,
                                String val) {
        this.operator = opr;
        this.value = val;
    }
    public String operator;
    
    public boolean isORConnective() {
        return this.operator.startsWith( "|" );
    }
    
    public boolean isANDConnective() {
        return this.operator.startsWith( "&" );
    }
    
}
