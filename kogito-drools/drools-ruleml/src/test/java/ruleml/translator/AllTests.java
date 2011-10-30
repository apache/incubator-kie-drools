package ruleml.translator;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(TestRuleML2Drools.class);
		suite.addTestSuite(TestDrools2RuleML.class);
		// $JUnit-END$
		return suite;
	}

}
