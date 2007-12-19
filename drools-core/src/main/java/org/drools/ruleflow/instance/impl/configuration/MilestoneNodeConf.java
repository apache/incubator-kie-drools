package org.drools.ruleflow.instance.impl.configuration;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.RuleSetNode;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.ruleflow.instance.impl.MilestoneNodeInstanceImpl;
import org.drools.ruleflow.instance.impl.PvmNodeConf;
import org.drools.ruleflow.instance.impl.RuleFlowJoinInstanceImpl;
import org.drools.ruleflow.instance.impl.RuleFlowProcessInstanceImpl;
import org.drools.ruleflow.instance.impl.RuleFlowSplitInstanceImpl;
import org.drools.ruleflow.instance.impl.StartNodeInstanceImpl;

public class MilestoneNodeConf implements PvmNodeConf {

	public RuleFlowNodeInstance getNodeInstance(Node node, RuleFlowProcessInstanceImpl processInstance ) {    	  	
        final RuleFlowNodeInstance result = new MilestoneNodeInstanceImpl();
        result.setNodeId( node.getId() );
        processInstance.addNodeInstance( result );
        return result;
	}

}
