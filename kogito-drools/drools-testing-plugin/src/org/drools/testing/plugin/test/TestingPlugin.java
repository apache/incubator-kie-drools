package org.drools.testing.plugin.test;

import java.io.FileWriter;

import junit.framework.TestCase;

import org.drools.lang.descr.PackageDescr;
import org.drools.testing.core.beans.Scenario;
import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.core.main.Testing;
import org.drools.testing.core.main.TransformerService;

public class TestingPlugin extends TestCase {
	
	public void testGenerateRTL () throws Exception {
		PackageDescr paDescr = TransformerService.parseDrl("/org/drools/testing/plugin/test/test.drl");
		Testing testing = new Testing("The Test Test Suite", paDescr);
		Scenario scenario = testing.generateScenario("Scenario One",paDescr.getRules());
		testing.addScenarioToSuite(scenario);
		TestSuite testSuite = testing.getTestSuite();
		FileWriter out = new FileWriter("test.rtl");
		testSuite.marshal(out);
	}
	
	
}
