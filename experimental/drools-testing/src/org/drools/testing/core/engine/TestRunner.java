package org.drools.testing.core.engine;

import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.core.exception.RuleTestLanguageException;

/**
 * 
 * @author Matt
 *
 * This is the core API class which takes a TestSuite object and processes
 * the scenarios via the Jboss rules engine.
 * 
 * This class relies on the underlying testing model and will return
 * results reflected in the model by the test scenarios.
 * 
 * (c) Matt Shaw
 */
public class TestRunner {

	private TestSuite testSuite;
	
	public TestRunner (TestSuite otherValue) {
		this.testSuite = otherValue;
	}
	
	/**
	 * The run method is invoked by the client and returns true if test
	 * was successfull and false otherwise.
	 * 
	 * @return boolean
	 * @throws RuleTestLanguageException
	 */
	public boolean run () throws RuleTestLanguageException {
		
		
		
		return true;
	}
}
