package org.drools.testing.core.test;

import java.io.FileWriter;

import junit.framework.TestCase;

import org.drools.lang.descr.PackageDescr;
import org.drools.testing.core.beans.Scenario;
import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.core.main.TestSuiteRunner;
import org.drools.testing.core.main.Testing;
import org.drools.testing.core.main.TransformerService;

public class TestingTest extends TestCase {
	
	public void testGetScenario () throws Exception {
		
		PackageDescr paDescr = TransformerService.parseDrl("/org/drools/testing/core/resources/drl/test.drl");
		Testing testing = new Testing("The Test Test Suite", paDescr);
		Scenario scenario = testing.generateScenario("Scenario One",paDescr.getRules());
		assertTrue(scenario.getFactCount() > 0);
	}
	
	public void testGenerateRTL () throws Exception {
		PackageDescr paDescr = TransformerService.parseDrl("/org/drools/testing/core/resources/drl/test.drl");
		Testing testing = new Testing("The Test Test Suite", paDescr);
		Scenario scenario = testing.generateScenario("Scenario One",paDescr.getRules());
		testing.addScenarioToSuite(scenario);
		TestSuite testSuite = testing.getTestSuite();
		FileWriter out = new FileWriter("rtl/test.rtl");
		testSuite.marshal(out);
	}
	
	public void testRunScenario () throws Exception {
		PackageDescr paDescr = TransformerService.parseDrl("/org/drools/testing/core/resources/drl/test.drl");
		Testing testing = new Testing("The Test Test Suite", paDescr);
		Scenario scenario = testing.generateScenario("Scenario One",paDescr.getRules());
		testing.addScenarioToSuite(scenario);
		TestSuite testSuite = testing.getTestSuite();
		
		TestSuiteRunner testSuiteRunner = new TestSuiteRunner("/org/drools/testing/core/resources/drl/test.drl");
		testSuiteRunner.runTests(scenario);
	}
}
