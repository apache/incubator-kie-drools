package org.drools.testing.core.model;

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

	private Scenario[] scenarios;
	private String name;
	
	public TestSuite () {}

	public Scenario[] getScenarios() {
		return scenarios;
	}

	public void setScenarios(Scenario[] scenarios) {
		this.scenarios = scenarios;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
