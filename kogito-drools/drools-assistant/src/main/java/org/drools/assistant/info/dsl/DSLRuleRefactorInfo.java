package org.drools.assistant.info.dsl;

import java.util.List;

import org.drools.assistant.info.RuleRefactorInfo;

public class DSLRuleRefactorInfo implements RuleRefactorInfo {

	private List<String> when;
	private List<String> then;
	
	public List<String> getWhen() {
		return when;
	}
	public void setWhen(List<String> when) {
		this.when = when;
	}
	public List<String> getThen() {
		return then;
	}
	public void setThen(List<String> then) {
		this.then = then;
	}
	
}
