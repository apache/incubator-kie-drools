package org.drools.workflow.instance.impl;

import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.instance.node.SplitInstance;

public interface ConstraintEvaluator extends Constraint {
    
    // TODO: make this work for more than only splits
    public boolean evaluate(SplitInstance instance,
                            Connection connection,
                            Constraint constraint);
}