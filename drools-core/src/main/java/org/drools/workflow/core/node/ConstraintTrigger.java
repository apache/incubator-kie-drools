package org.drools.workflow.core.node;

import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConnectionRef;

public class ConstraintTrigger extends Trigger implements Constrainable {

	private static final long serialVersionUID = 4L;

	private String constraint;

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

    public void addConstraint(ConnectionRef connection, Constraint constraint) {
    	if (connection != null) {
    		throw new IllegalArgumentException(
				"A constraint trigger only accepts one simple constraint");
    	}
        this.constraint =  constraint.getConstraint();
    }
	
}
