package org.drools.workflow.core.node;

public class DynamicNode extends CompositeContextNode {

	private static final long serialVersionUID = 400L;
	
	private String ruleFlowGroup;
	
	public String getRuleFlowGroup() {
		return ruleFlowGroup;
	}
	
	public void setRuleFlowGroup(String ruleFlowGroup) {
		this.ruleFlowGroup = ruleFlowGroup;
	}
	
}
