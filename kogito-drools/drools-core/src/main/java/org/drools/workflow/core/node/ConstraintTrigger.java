package org.drools.workflow.core.node;

import org.drools.workflow.core.Constraint;

public class ConstraintTrigger extends Trigger implements Constrainable {

	private String constraint;

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

    public void addConstraint(String name, Constraint constraint) {
        this.constraint =  constraint.getConstraint();
    }
	
}
