package org.drools.workflow.core.node;

public class ConstraintTrigger extends Trigger implements Constrainable {

	private String constraint;

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}
	
}
