package org.drools.ruleflow.instance.impl;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;

public interface ProcessNodeInstanceFactory {
	RuleFlowNodeInstance getNodeInstance(Node node, RuleFlowProcessInstanceImpl processInstance);
}
