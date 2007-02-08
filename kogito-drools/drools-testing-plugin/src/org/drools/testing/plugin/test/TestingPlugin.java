package org.drools.testing.plugin.test;

import java.io.FileReader;
import java.io.FileWriter;

import junit.framework.TestCase;

import org.drools.lang.descr.PackageDescr;
import org.drools.testing.core.beans.Scenario;
import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.core.main.Testing;
import org.drools.testing.core.main.TransformerService;
import org.drools.testing.core.rules.RuleSetTest;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

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
	
	
	public void testUnmarshallXml () throws Exception {
		
		Unmarshaller unmar = new Unmarshaller(TestSuite.class);
        TestSuite testSuite = (TestSuite)unmar.unmarshal(new InputSource(TestingPlugin.class.getResourceAsStream( "test.rtl" )));
    }
	
}
