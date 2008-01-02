package org.drools.ruleflow.instance.impl.factories;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.RuleSetNode;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.ruleflow.instance.impl.ProcessNodeInstanceFactory;
import org.drools.ruleflow.instance.impl.RuleFlowProcessInstanceImpl;

public class RuleSetNodeFactory implements ProcessNodeInstanceFactory {

	public RuleFlowNodeInstance getNodeInstance(Node node, RuleFlowProcessInstanceImpl processInstance ) {
        final RuleFlowNodeInstance result = (RuleFlowNodeInstance) processInstance.getAgenda().getRuleFlowGroup( ((RuleSetNode) node).getRuleFlowGroup() );
    	result.setNodeId( node.getId() );
    	processInstance.addNodeInstance( result );
    	return result;
	}

}
