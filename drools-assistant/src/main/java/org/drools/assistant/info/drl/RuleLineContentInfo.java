package org.drools.assistant.info.drl;

public class RuleLineContentInfo extends RuleBasicContentInfo {

	private RuleDRLContentInfo rule;
	
	public RuleLineContentInfo(Integer offset, String content, DRLContentTypeEnum type) {
		super(offset, content, type);
	}

	public void setRule(RuleDRLContentInfo rule) {
		this.rule = rule;
	}

	public RuleDRLContentInfo getRule() {
		return rule;
	}

}