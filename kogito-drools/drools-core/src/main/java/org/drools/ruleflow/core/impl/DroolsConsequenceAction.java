package org.drools.ruleflow.core.impl;

public class DroolsConsequenceAction {
	
	private String consequence;
	
	public DroolsConsequenceAction(String consequence) {
		this.consequence = consequence;
	}
	
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	
	public String getConsequence() {
		return consequence;
	}

	public String toString() {
		return consequence;
	}
}
