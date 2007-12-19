package org.drools.ruleflow.instance.impl.configuration;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.RuleSetNode;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.ruleflow.instance.impl.PvmNodeConf;
import org.drools.ruleflow.instance.impl.RuleFlowProcessInstanceImpl;

public class RuleSetNodeConf implements PvmNodeConf {

	public RuleFlowNodeInstance getNodeInstance(Node node, RuleFlowProcessInstanceImpl processInstance ) {
        final RuleFlowNodeInstance result = (RuleFlowNodeInstance) processInstance.getAgenda().getRuleFlowGroup( ((RuleSetNode) node).getRuleFlowGroup() );
    	result.setNodeId( node.getId() );
    	processInstance.addNodeInstance( result );
    	return result;
	}

}
