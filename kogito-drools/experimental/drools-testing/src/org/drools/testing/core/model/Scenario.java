package org.drools.testing.core.model;

/**
 * 
 * @author Matt
 *
 * (c) Matt Shaw
 */
public class Scenario {

	public Fact[] facts;
	public Outcome[] outcomes;
	public Rule[] rules;
	
	public Scenario () {}
	
	public Fact[] getFacts() {
		return facts;
	}
	public void setFacts(Fact[] facts) {
		this.facts = facts;
	}
	public Outcome[] getOutcomes() {
		return outcomes;
	}
	public void setOutcomes(Outcome[] outcomes) {
		this.outcomes = outcomes;
	}
	public Rule[] getRules() {
		return rules;
	}
	public void setRules(Rule[] rules) {
		this.rules = rules;
	}
}
