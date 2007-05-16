package org.drools.testing.core.engine;

import org.drools.rule.Package;
import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.core.exception.RuleTestLanguageException;
import org.drools.testing.core.exception.RuleTestServiceUnavailableException;
import org.drools.testing.core.wrapper.RuleBaseWrapper;

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
 * Eventually there will be multiple run methods depending on services this API will
 * be used in. for now the run method accepts a drools Package object, implying that the 
 * calling application has it's own method of generating a package.
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
	 * @param pkg
	 * @return boolean
	 * @throws RuleTestLanguageException
	 */
	public boolean run (Package pkg) throws RuleTestLanguageException {
		try {
			RuleBaseWrapper.getInstance().getRuleBase().addPackage(pkg);
		}catch (Exception e) {
			throw new RuleTestServiceUnavailableException("Could not load rule package: "
					+pkg.getName());
		}
		
		parseTestSuite();
		RuleBaseWrapper.getInstance().getRuleBase().removePackage(pkg.getName());
		return true;
	}
	
	/**
	 * This is the method which converts the model into a set of facts to be
	 * inserted into working memory.
	 * 
	 *  The objects are created asserted. Any agenda filters required are added.
	 *  The tests are executed and the result set is populated.
	 *
	 */
	private void parseTestSuite () throws RuleTestLanguageException {
		
	}
}
