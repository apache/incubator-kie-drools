package org.drools.workflow.core.node;

import org.drools.workflow.core.Constraint;

public interface Constrainable {
	
	public void addConstraint(String name, Constraint constraint);


}
