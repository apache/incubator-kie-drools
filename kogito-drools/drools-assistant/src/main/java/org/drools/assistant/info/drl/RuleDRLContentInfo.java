package org.drools.assistant.info.drl;

import java.util.ArrayList;
import java.util.List;

public class RuleDRLContentInfo extends RuleBasicContentInfo {
	
	private String ruleName;
	private List<RuleLineContentInfo> lhs;
	private List<RuleLineContentInfo> rhs;

	public RuleDRLContentInfo(Integer offset, String content, DRLContentTypeEnum type, String ruleName, List<RuleLineContentInfo> lhs, List<RuleLineContentInfo> rhs) {
		super(offset, content, type);
		this.setRuleName(ruleName);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleName() {
		return ruleName;
	}
	
	public Integer getRuleNameLength() {
		return ruleName.length();
	}
	
	public void addLHSRuleLine(RuleLineContentInfo ruleLine) {
		this.lhs.add(ruleLine);
	}

	public List<RuleLineContentInfo> getLHSRuleLines() {
		return lhs;
	}
	
	public void addRHSRuleLine(RuleLineContentInfo ruleLine) {
		this.rhs.add(ruleLine);
	}

	public List<RuleLineContentInfo> getRHSRuleLines() {
		return rhs;
	}
	
	public List<RuleLineContentInfo> getAllLines() {
		List<RuleLineContentInfo> all = new ArrayList<RuleLineContentInfo>(lhs);
		all.addAll(rhs);
		return all;
	}

}
