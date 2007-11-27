package org.drools.ruleflow.nodes.split;

import java.util.Iterator;
import java.util.List;

import org.drools.common.RuleFlowGroupNode;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Constraint;
import org.drools.ruleflow.core.Split;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.ruleflow.instance.impl.RuleFlowSplitInstanceImpl;
import org.drools.spi.Activation;
import org.drools.spi.RuleFlowGroup;

public class RuleFlowConstraintEvaluator
    implements
    ConstraintEvaluator {
    public boolean evaluate(RuleFlowSplitInstanceImpl instance,
                            Connection connection,
                            Constraint constraint) {
        RuleFlowProcessInstance processInstance = instance.getProcessInstance();
        RuleFlowGroup systemRuleFlowGroup = processInstance.getAgenda().getRuleFlowGroup( "DROOLS_SYSTEM" );

        String rule = "RuleFlow-Split-" + processInstance.getProcess().getId() + "-" + instance.getNode().getId() + "-" + connection.getTo().getId();
        for ( Iterator activations = systemRuleFlowGroup.iterator(); activations.hasNext(); ) {
            Activation activation = ((RuleFlowGroupNode) activations.next()).getActivation();
            if ( rule.equals( activation.getRule().getName() ) ) {
                return true;
            }
        }
        return false;
    }
}
