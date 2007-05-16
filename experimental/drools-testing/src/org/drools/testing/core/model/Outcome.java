package org.drools.testing.core.model;

/**
 * 
 * @author Matt
 *
 * (c) Matt Shaw
 */
public class Outcome {

	public Rule[] rules;
	public Assertion[] assertions;
	
	public Assertion[] getAssertions() {
		return assertions;
	}
	public void setAssertions(Assertion[] assertions) {
		this.assertions = assertions;
	}
	public Rule[] getRules() {
		return rules;
	}
	public void setRules(Rule[] rules) {
		this.rules = rules;
	}
}
