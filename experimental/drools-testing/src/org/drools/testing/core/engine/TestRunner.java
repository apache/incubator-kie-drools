package org.drools.testing.core.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.drools.WorkingMemory;
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
	private org.drools.rule.Package pkg;
	
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
	public boolean run (org.drools.rule.Package pkg) throws RuleTestLanguageException {
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
		
		Iterator i  = testSuite.getScenarios().iterator();
		while (i.hasNext())
			parseScenario( (Scenario) i.next());
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
	private void parseFacts (Collection facts, WorkingMemory wm) throws RuleTestLanguageException {
	
		// iterating over the facts
		Iterator i  = facts.iterator();
		while (i.hasNext()) {
			Fact factDefn = (Fact) i.next();
			Class classDefn; 
			Object fact;
			if (factDefn != null) {
				try {
					classDefn = ObjectUtils.getClassDefn(factDefn.getType());
					fact = classDefn.newInstance(); 
				}catch (Exception e) {
					throw new RuleTestServiceUnavailableException("Exception ocurred",e);
				}
			
			
				// get the fields to set from the fact definition
				Iterator j = factDefn.getFields().iterator();
				while (j.hasNext()) {
					Field field = (Field) j.next();
					// set the property on our newly instantiated fact bean
					try {
						Object value = ConvertUtils.convert(field.getValue(), 
								ObjectUtils.getClassDefn(field.getType()));
						PropertyUtils.setProperty(fact, field.getName(), value);
					}catch (Exception e) {
						throw new RuleTestServiceUnavailableException("Exception ocurred",e);
					}
				}
				// assert the fact into working memory
				wm.assertObject(fact);
			}
		}
	}
	
	/**
	 * add the multipleRuleNameAgenda filter to the working memory
	 * 
	 * @param rules
	 * @param wm
	 * @throws RuleTestLanguageException
	 */
	private Collection specifyRulesToFire (Collection rules, WorkingMemory wm) throws RuleTestLanguageException {
		
		Collection items = new ArrayList();
		Iterator i = rules.iterator();
		while (i.hasNext()) {
			Rule rule = (Rule) i.next();
			if (rule.isFire())
				items.add(rule.getName());
		}
		return items;
	}
	
	/**
	 * Grab the list of fact handles from working memory
	 * Match the field values against the expected assertion field values
	 * 
	 * Set the outcome state to PASS if all assertions passed
	 * Set the outcome state to FAIL if all assertions failed
	 * Set the outcome state to PARTIAL PASS at least 1 assertion failed
	 * 
	 * @param scenario
	 * @param wm
	 * @throws RuleTestLanguageException
	 */
	private void setOutcomes (Scenario scenario, WorkingMemory wm) throws RuleTestLanguageException {
		
	}
}
