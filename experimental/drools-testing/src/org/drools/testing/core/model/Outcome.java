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

	private String name;
	private String state;
	private Collection rules = new ArrayList();
	private Collection assertions = new ArrayList();
	private static final String STATE_PASS = "PASS";
	private static final String STATE_PARTIAL_PASS = "PARTIAL PASS";
	private static final String STATE_FAIL = "FAIL";
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
}
