package org.drools.testing.core.test;

import org.drools.testing.core.model.Rule;
import org.drools.testing.core.model.Scenario;
import org.drools.testing.core.model.TestSuite;
import org.drools.testing.core.utils.OperatorUtils;

import junit.framework.TestCase;

public class APITester extends TestCase {

	TestSuite testSuite;
	
	public void testMockTestSuite () {
		
		testSuite = new TestSuite();
		
	}
	
	private void addScenario () {
	
		Scenario scenario = new Scenario();
		addRule("testrule1");
	}
	
	private Rule addRule (String name) {
		
		Rule rule = new Rule();
		rule.setName(name);
		rule.setFire(true);
		return rule;
	}
}
