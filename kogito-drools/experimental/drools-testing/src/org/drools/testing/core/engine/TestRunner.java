package org.drools.testing.core.engine;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.drools.WorkingMemory;
import org.drools.rule.Package;
import org.drools.testing.core.exception.RuleTestLanguageException;
import org.drools.testing.core.exception.RuleTestServiceUnavailableException;
import org.drools.testing.core.filters.MultipleRuleAgendaFilter;
import org.drools.testing.core.model.Fact;
import org.drools.testing.core.model.Field;
import org.drools.testing.core.model.Rule;
import org.drools.testing.core.model.Scenario;
import org.drools.testing.core.utils.ObjectUtils;
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

	private org.drools.testing.core.model.TestSuite testSuite;
	private Package pkg;
	
	public TestRunner (org.drools.testing.core.model.TestSuite otherValue) {
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
		this.pkg = pkg;
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
	 *
	 */
	private void parseTestSuite () throws RuleTestLanguageException {
		
		for (int i=0; i<testSuite.getScenarios().length; i++) 
			parseScenario(testSuite.getScenarios()[i]);
	}
	
	/**
	 * The objects are created asserted. Any agenda filters required are added.
	 *  The tests are executed and the result set is populated.
	 * 
	 * @param scenario
	 * @throws RuleTestLanguageException
	 */
	private void parseScenario (Scenario scenario) throws RuleTestLanguageException {
		
		// create the working memory
		WorkingMemory wm = RuleBaseWrapper.getInstance().getRuleBase().newWorkingMemory(true);
		parseFacts(scenario.getFacts(), wm);
		Collection rules = specifyRulesToFire(scenario.getRules(), wm);
		// fire the rules
		wm.fireAllRules(new MultipleRuleAgendaFilter(rules));
	}
	
	/**
	 * iterator over the array of facts assigning required fields
	 * then assert them into the working memory
	 * 
	 * @param facts
	 * @param wm
	 * @throws RuleTestLanguageException
	 */
	private void parseFacts (Fact[] facts, WorkingMemory wm) throws RuleTestLanguageException {
	
		// iterating over the facts
		for (int i=0; i<facts.length; i++) {
			Fact factDefn = facts[i];
			Class classDefn = ObjectUtils.getClassDefn(factDefn.getType(), pkg.getImports(),null);
			Object fact;
			try {
				fact = classDefn.newInstance(); 
			}catch (Exception e) {
				throw new RuleTestServiceUnavailableException("Exception ocurred",e);
			}
			
			// get the fields to set from the fact definition
			Field[] fields = factDefn.getFields();
			for (int j=0; j<fields.length; j++) {
				Field field = fields[j];
				// set the property on our newly instantiated fact bean
				try {
					PropertyUtils.setProperty(fact, field.getName(), field.getValue());
				}catch (Exception e) {
					throw new RuleTestServiceUnavailableException("Exception ocurred",e);
				}
			}
			// assert the fact into working memory
			wm.assertObject(fact);
		}
	}
	
	/**
	 * add the multipleRuleNameAgenda filter to the working memory
	 * 
	 * @param rules
	 * @param wm
	 * @throws RuleTestLanguageException
	 */
	private Collection specifyRulesToFire (Rule[] rules, WorkingMemory wm) throws RuleTestLanguageException {
		
		Collection items = new ArrayList();
		for (int i=0; i<rules.length; i++) {
			if (rules[i].isFire())
				items.add(rules[i].getName());
		}
		return items;
	}
}
