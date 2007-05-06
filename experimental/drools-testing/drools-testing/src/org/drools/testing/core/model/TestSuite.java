package org.drools.testing.core.model;

/**
 * 
 * @author Matt
 *
 * This is the test suite model for representation of rules
 * in a testing environment.
 * 
 * A test suite has many test scenarios.
 */

public class TestSuite {

	private Scenario[] scenarios;

	public Scenario[] getScenarios() {
		return scenarios;
	}

	public void setScenarios(Scenario[] scenarios) {
		this.scenarios = scenarios;
	}
}
