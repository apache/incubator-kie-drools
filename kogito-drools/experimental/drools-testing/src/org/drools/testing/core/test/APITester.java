package org.drools.testing.core.test;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.beanutils.BeanUtils;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.testing.core.engine.TestRunner;
import org.drools.testing.core.model.Assertion;
import org.drools.testing.core.model.Fact;
import org.drools.testing.core.model.Outcome;
import org.drools.testing.core.model.Rule;
import org.drools.testing.core.model.Scenario;
import org.drools.testing.core.model.TestSuite;
import org.drools.testing.core.rules.model.Account;
import org.drools.testing.core.utils.ObjectUtils;
import org.drools.testing.core.utils.OperatorUtils;

import junit.framework.TestCase;

/**
 * 
 * @author Matt
 *
 * Make sure you have the drools libs on your classpath before running
 * these tests!
 *
 * (c) Matt Shaw
 */
public class APITester extends TestCase {

	public void testMockTestSuite () throws Exception {
		
		// get the package
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(new InputStreamReader( getClass().getResourceAsStream( "/org/drools/testing/core/resources/drl/test.drl" ) ));
		Package pkg = builder.getPackage();
		
		// generate test suite
		TestSuite testSuite = new TestSuite();
		testSuite.setName("Mock Test Suite");
		testSuite.getScenarios().add(addScenario(pkg));
		assertTrue(testSuite.getScenarios().size() > 0);
		
		// run the test
		TestRunner testRunner = new TestRunner(testSuite);
		testRunner.run(pkg);
	}
	
	private Scenario addScenario (Package pkg) throws Exception {
		Scenario scenario = new Scenario();
		scenario.getRules().add(addRule("test1"));
		scenario.getOutcomes().add(addOutcome());
		
		
		Iterator i = pkg.getImports().iterator();
		while (i.hasNext()) {
			String importName = (String) i.next();
			scenario.getFacts().add(addFact(importName));
		}
		return scenario;
	}
	
	private Rule addRule (String name) {
		
		Rule rule = new Rule();
		rule.setName(name);
		rule.setFire(true);
		return rule;
	}
	
	private Outcome addOutcome () {
		Outcome outcome = new Outcome();
		outcome.getAssertions().add(addAssertion());
		
		return outcome;
	}
	
	private Assertion addAssertion () {
		Assertion assertion = new Assertion();
		assertion.setName("assertion1");
		assertion.setBeanName("org.drools.testing.core.rules.model.Account");
		assertion.setPropertyName("balance");
		assertion.setExpectedValue("10");
		return assertion;
	}
	
	private Fact addFact (String importName) {
		Fact fact = new Fact();
		fact.setType(importName);
		Random rand = new Random();
		rand.setSeed(Calendar.getInstance().getTimeInMillis());
		fact.setId(new Integer(rand.nextInt()));
		Class classDefn;
		Object object;
		try {
			classDefn = ObjectUtils.getClassDefn(importName);
		}catch (Exception e) {
			return null;
		}
		Field[] fields = classDefn.getDeclaredFields();
		for (int i=0; i<fields.length; i++) {
			Field fieldDefn = fields[i];
			fact.getFields().add(addField(fieldDefn));
		}
		return fact;
	}
	
	private org.drools.testing.core.model.Field addField (Field fieldDefn) {
		org.drools.testing.core.model.Field field = new org.drools.testing.core.model.Field();
		field.setName(fieldDefn.getName());
		field.setType(fieldDefn.getType().getName());
		if (field.getName().equalsIgnoreCase("status"))
			field.setValue("active");
		if (field.getName().equalsIgnoreCase("balance"))
			field.setValue("0");
		return field;
	}
}
