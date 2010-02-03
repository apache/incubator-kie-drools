package org.drools.guvnor.client.modeldriven.brl;

/**
 * This is for a connective constraint that adds more options to a field constraint. 
 * @author Michael Neale
 */
public class ConnectiveConstraint extends ISingleFieldConstraint {

    public ConnectiveConstraint() {
    }

    public ConnectiveConstraint(final String fieldName,
    		                    final String fieldType,
    							final String opr,
                                final String val) {
    	this.fieldName = fieldName;
    	this.fieldType = fieldType;
        this.operator = opr;
        this.value = val;
    }

    public String operator;
    public String fieldName;
    public String fieldType;

}
