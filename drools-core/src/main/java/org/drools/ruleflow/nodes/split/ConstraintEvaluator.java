package org.drools.ruleflow.nodes.split;

import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Constraint;
import org.drools.ruleflow.core.Split;
import org.drools.ruleflow.instance.impl.RuleFlowSplitInstanceImpl;

public interface ConstraintEvaluator {
    public boolean evaluate(RuleFlowSplitInstanceImpl instance,
                         Connection connection,
                         Constraint constraint);
}