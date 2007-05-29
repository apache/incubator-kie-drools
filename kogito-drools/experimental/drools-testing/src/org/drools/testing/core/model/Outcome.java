package org.drools.testing.core.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Matt
 *
 * (c) Matt Shaw
 */
public class Outcome {

	private Collection rules = new ArrayList();
	public Collection assertions = new ArrayList();
	
	public Outcome () {}

	public Collection getAssertions() {
		return assertions;
	}

	public void setAssertions(Collection assertions) {
		this.assertions = assertions;
	}

	public Collection getRules() {
		return rules;
	}

	public void setRules(Collection rules) {
		this.rules = rules;
	}
	
	
}
