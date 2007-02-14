package org.drools.testing.core.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.testing.core.beans.Rule;
import org.drools.testing.core.beans.Scenario;
import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.core.configuration.ApplicationProperties;
import org.drools.testing.core.configuration.PropertyNotFoundException;
import org.drools.testing.core.exception.RuleTestLanguageException;
import org.drools.testing.core.utils.CollectionUtils;
import org.drools.testing.core.utils.ObjectUtils;
import org.drools.testing.core.utils.OperatorUtils;
import org.drools.testing.core.wrapper.FactWrapper;
import org.drools.testing.core.wrapper.FieldWrapper;

/**
 * 
 * @author mshaw
 * 
 * This class is the main class for the drools core testing module. It provides
 * functionalilty for generating test scenario's based on an input set of rules.
 *
 * facts - an internal list which contains a unique list of facts and fields
 */
public final class Testing {
	
	private TestSuite testSuite;
	private Scenario scenario;
	private PackageDescr packageDescr;
	private Random rng;
	private List facts = new ArrayList();
	private ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
	private ClassLoader classLoader;
	
	public Testing () {
		
	}
	
	public Testing (String suiteName, PackageDescr packageDescr) {
		this.testSuite = new TestSuite();
		this.testSuite.setName(suiteName);
		this.packageDescr = packageDescr;
	}
	
	/**
	 * Instantiate with a class loader
	 * @param suiteName
	 * @param packageDescr
	 */
	public Testing (String suiteName, PackageDescr packageDescr, ClassLoader classLoader) {
		this.testSuite = new TestSuite();
		this.testSuite.setName(suiteName);
		this.packageDescr = packageDescr;
		this.classLoader = classLoader;
	}
	
	/**
	 * return a testsuite serializable object with the name
	 * 
	 * @param name
	 * @return
	 * @throws RuleTestLanguageException
	 */
	public TestSuite getTestSuite () throws RuleTestLanguageException {
		
		return this.testSuite;
	}
	
	/**
	 * Create a scenario object which will contain all the rules.
	 * We then make a call to processColumnDescriptors which will
	 * aid in generating our fact types.
	 * 
	 * @throws RuleTestLanguageException
	 */
	public Scenario generateScenario (String name, List rules) throws RuleTestLanguageException {
		
		scenario = new Scenario();
		scenario.setName(name);
		rng = new Random();
		
		Iterator i = rules.iterator();
		while (i.hasNext()) {
			RuleDescr rule = (RuleDescr) i.next();
			scenario.addRule(getRule(rule));
			processColumnDescriptors(rule.getLhs().getDescrs());
		}
		
		Iterator j = facts.iterator();
		while (j.hasNext()) {
			FactWrapper factWrapper = (FactWrapper) j.next();
			factWrapper.setId(rng.nextInt());
			scenario.addFact(factWrapper);
		}
		
		return scenario;
	}
	
	/**
	 * 
	 * @param descrs
	 * @return List of facts
	 * @throws RuleTestLanguageException
	 */
	public List processColumnDescriptors (List descrs) throws RuleTestLanguageException {
		
		Iterator i = descrs.iterator();
		while (i.hasNext()) {
			// each columndescr is for a "when" statment in the drl
			ColumnDescr columnDescr = (ColumnDescr) i.next();
			
			Class classDefn = ObjectUtils.getClassDefn(columnDescr.getObjectType(), this.packageDescr.getImports(),this.classLoader);
				
			FactWrapper fact = new FactWrapper();
			fact.setType(classDefn.getName());
			if (facts.contains(fact)) {
				fact = (FactWrapper) facts.get(facts.indexOf(fact));
				facts.remove(fact);
			}	
			
			processLiteralDescriptors(classDefn, fact,columnDescr.getDescrs());
			facts.add(fact);
			
		}
		return facts;
	}
	
	/**
	 * 
	 * @param fact
	 * @param descrs
	 * @throws RuleTestLanguageException
	 */
	public void processLiteralDescriptors (Class classDefn, FactWrapper fact, List descrs) throws RuleTestLanguageException {
		
		List fields = (List) CollectionUtils.arrayToCollection(fact.getField());
		
		Iterator i = descrs.iterator();
		while (i.hasNext()) {
			LiteralDescr literalDescr = (LiteralDescr) i.next();
			FieldWrapper field = new FieldWrapper();
			field.setName(literalDescr.getFieldName());
			//field.setValue(literalDescr.getText());
			field.setValue(getValue(literalDescr.getFieldName(), literalDescr.getText(), literalDescr.getEvaluator()));
			field.setType(getType(classDefn, literalDescr.getFieldName()).getName());
						
			if (!fields.contains(field))
				fact.addField(field);
		}
	}
	
	/**
	 * 
	 * @param ruleDescr
	 * @return Rule
	 * @throws RuleTestLanguageException
	 */
	public Rule getRule (RuleDescr ruleDescr) throws RuleTestLanguageException {
		
		Rule rule = new Rule();
		rule.setName(ruleDescr.getName());
		rule.setFire(true);
		
		
		
		
		return rule;
	}
	
	/**
	 * 
	 * @param scenario
	 * @throws RuleTestLanguageException
	 */
	public void addScenarioToSuite (Scenario scenario) throws RuleTestLanguageException {
		
		this.testSuite.addScenario(scenario);
	}
	
	public Class getType (Class classDefn, String fieldName) {
		
		Class type = null;
		try {
			type = classDefn.getDeclaredField(fieldName).getType();
		}catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return type;
	}
	
	public String getValue (String fieldName, String value, String operator) {
		
		String result = null;
		
		// is it a range?
		if (OperatorUtils.rangeOperators.indexOf(operator) != -1)
			result = getRangeValue(operator, value);
		else
			result = value;
			
			
		return result;
	}
	
	public String getRangeValue (String operator, String value) {
	
		String result = null;
		
		try {
			Integer min = new Integer(applicationProperties.getProperty("range.min"));
			Integer max = new Integer(applicationProperties.getProperty("range.max"));
		
			if (operator.indexOf("<") != -1) 
				max = new Integer(value);
			else if (operator.indexOf(">") != -1)
				min = new Integer(value);
			
			result = min.toString();
			for (int i=min.intValue()+1; i<max.intValue(); i++)
				result = result + "," + i;
			
		}catch (PropertyNotFoundException e) {
			
		}
		
		return result;
	}
}
