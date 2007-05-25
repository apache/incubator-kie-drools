package org.drools.testing.core.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Matt
 *
 * This is the test suite model for representation of rules
 * in a testing environment.
 * 
 * A test suite has many test scenarios.
 * 
 * (c) Matt Shaw
 */

public class TestSuite {

	private Collection scenarios = new ArrayList();
	private String name;
	
	public TestSuite () {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection getScenarios() {
		return scenarios;
	}

	public void setScenarios(Collection scenarios) {
		this.scenarios = scenarios;
	}
}
