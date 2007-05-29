package org.drools.testing.core.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Matt
 *
 * (c) Matt Shaw
 */
public class Scenario {

	private Collection facts = new ArrayList();
	private Collection outcomes = new ArrayList();
	private Collection rules = new ArrayList();
	
	public Scenario () {}

	public Collection getFacts() {
		return facts;
	}

	public void setFacts(Collection facts) {
		this.facts = facts;
	}

	public Collection getOutcomes() {
		return outcomes;
	}

	public void setOutcomes(Collection outcomes) {
		this.outcomes = outcomes;
	}

	public Collection getRules() {
		return rules;
	}

	public void setRules(Collection rules) {
		this.rules = rules;
	}
	
	
}
