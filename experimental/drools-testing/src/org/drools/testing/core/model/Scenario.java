package org.drools.testing.core.model;

public class Scenario {

	public Fact[] facts;
	public Outcome[] outcomes;
	
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
}
