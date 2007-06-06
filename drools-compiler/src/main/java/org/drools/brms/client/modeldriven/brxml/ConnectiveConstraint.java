package org.drools.brms.client.modeldriven.brxml;

/**
 * This is for a connective constraint that adds more options to a field constraint. 
 * @author Michael Neale
 */
public class ConnectiveConstraint extends ISingleFieldConstraint {

    public ConnectiveConstraint() {
    }

    public ConnectiveConstraint(final String opr,
                                final String val) {
        this.operator = opr;
        this.value = val;
    }

    public String operator;

}
