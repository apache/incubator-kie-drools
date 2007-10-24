package org.drools.testframework;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.brms.client.modeldriven.testing.Assertion;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.TimeMachine;

import junit.framework.TestCase;

public class ScenarioRunnerTest extends RuleUnit {

	public void testPopulateFacts() throws Exception {
		Scenario sc = new Scenario();
		sc.facts = new FactData[] {
				new FactData("Cheese", "c1", new FieldData[] {
						new FieldData("type", "cheddar", false),
						new FieldData("price", "42", false) }, false),
				new FactData("Person", "p1", new FieldData[] {
						new FieldData("name", "mic", false),
						new FieldData("age", "30 + 3", true) }, false) };

		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");
		resolver.addImport("org.drools.Person");
		ScenarioRunner runner = new ScenarioRunner(sc, resolver,
				new MockWorkingMemory());

		assertTrue(runner.populatedData.containsKey("c1"));
		assertTrue(runner.populatedData.containsKey("p1"));

		Cheese c = (Cheese) runner.populatedData.get("c1");
		assertEquals("cheddar", c.getType());
		assertEquals(42, c.getPrice());

		Person p = (Person) runner.populatedData.get("p1");
		assertEquals("mic", p.getName());
		assertEquals(33, p.getAge());

	}

	public void testVerifyFacts() throws Exception {

		ScenarioRunner runner = new ScenarioRunner(new Scenario(), null,
				new MockWorkingMemory());
		Cheese f1 = new Cheese("cheddar", 42);
		runner.populatedData.put("f1", f1);

		Person f2 = new Person("michael", 33);
		runner.populatedData.put("f2", f2);

		// test all true
		VerifyFact vf = new VerifyFact();
		vf.factName = "f1";
		vf.fieldValues = new VerifyField[] {
				new VerifyField("type", "cheddar"),
				new VerifyField("price", "42") };

		runner.verify(vf);
		for (int i = 0; i < vf.fieldValues.length; i++) {
			assertTrue(vf.fieldValues[i].success);
		}

		vf = new VerifyFact();
		vf.factName = "f2";
		vf.fieldValues = new VerifyField[] {
				new VerifyField("name", "michael"),
				new VerifyField("age", "33") };

		runner.verify(vf);
		for (int i = 0; i < vf.fieldValues.length; i++) {
			assertTrue(vf.fieldValues[i].success);
		}

		// test one false
		vf = new VerifyFact();
		vf.factName = "f2";
		vf.fieldValues = new VerifyField[] { new VerifyField("name", "mark"),
				new VerifyField("age", "33") };

		runner.verify(vf);
		assertFalse(vf.fieldValues[0].success);
		assertTrue(vf.fieldValues[1].success);

		assertEquals("michael", vf.fieldValues[0].actual);
		assertEquals("mark", vf.fieldValues[0].expected);

		// test 2 false
		vf = new VerifyFact();
		vf.factName = "f2";
		vf.fieldValues = new VerifyField[] { new VerifyField("name", "mark"),
				new VerifyField("age", "32") };

		runner.verify(vf);
		assertFalse(vf.fieldValues[0].success);
		assertFalse(vf.fieldValues[1].success);

		assertEquals("michael", vf.fieldValues[0].actual);
		assertEquals("mark", vf.fieldValues[0].expected);

		assertEquals("33", vf.fieldValues[1].actual);
		assertEquals("32", vf.fieldValues[1].expected);

	}

	public void testDummyRunNoRules() throws Exception {
		Scenario sc = new Scenario();
		sc.facts = new FactData[] { new FactData("Cheese", "c1",
				new FieldData[] { new FieldData("type", "cheddar", false),
						new FieldData("price", "42", false) }, false) };

		sc.assertions = new VerifyFact[] { new VerifyFact("c1",
				new VerifyField[] { new VerifyField("type", "cheddar"),
						new VerifyField("price", "42") }) };

		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");

		MockWorkingMemory wm = new MockWorkingMemory();
		ScenarioRunner runner = new ScenarioRunner(sc, resolver, wm);
		assertEquals(1, wm.facts.size());
		assertEquals(runner.populatedData.get("c1"), wm.facts.get(0));

		assertTrue(runner.populatedData.containsKey("c1"));
		VerifyFact vf = (VerifyFact) sc.assertions[0];
		for (int i = 0; i < vf.fieldValues.length; i++) {
			assertTrue(vf.fieldValues[i].success);
		}

	}

	public void testCountVerification() throws Exception {

		Map<String, Integer> firingCounts = new HashMap<String, Integer>();
		firingCounts.put("foo", 2);
		firingCounts.put("bar", 1);
		// and baz, we leave out

		ScenarioRunner runner = new ScenarioRunner(new Scenario(), null,
				new MockWorkingMemory());
		VerifyRuleFired v = new VerifyRuleFired();
		v.ruleName = "foo";
		v.expectedFire = true;
		runner.verify(v, firingCounts);
		assertTrue(v.success);
		assertEquals(2, v.actual.intValue());

		v = new VerifyRuleFired();
		v.ruleName = "foo";
		v.expectedFire = false;
		runner.verify(v, firingCounts);
		assertFalse(v.success);
		assertEquals(2, v.actual.intValue());

		v = new VerifyRuleFired();
		v.ruleName = "foo";
		v.expectedCount = 2;

		runner.verify(v, firingCounts);
		assertTrue(v.success);
		assertEquals(2, v.actual.intValue());

	}

	public void testTestingEventListener() throws Exception {
		Scenario sc = new Scenario();
		sc.ruleTrace.rules = new String[] { "foo", "bar" };
		MockWorkingMemory wm = new MockWorkingMemory();
		ScenarioRunner run = new ScenarioRunner(sc, null, wm);
		assertEquals(wm, run.workingMemory);
		assertNotNull(wm.agendaEventListener);
		assertTrue(wm.agendaEventListener instanceof TestingEventListener);
		TestingEventListener lnr = (TestingEventListener) wm.agendaEventListener;
		assertEquals(2, lnr.ruleNames.size());
		assertTrue(lnr.ruleNames.contains("foo"));
		assertTrue(lnr.ruleNames.contains("bar"));
	}

	public void testWithGlobals() throws Exception {
		Scenario sc = new Scenario();
		sc.facts = new FactData[] {
				new FactData("Cheese", "c", new FieldData[] { new FieldData(
						"type", "cheddar", false) }, true),
				new FactData("Cheese", "c2", new FieldData[] { new FieldData(
						"type", "stilton", false) }, false) };
		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");

		MockWorkingMemory wm = new MockWorkingMemory();
		ScenarioRunner run = new ScenarioRunner(sc, resolver, wm);
		assertEquals(1, wm.globals.size());
		assertEquals(1, run.globalData.size());
		assertEquals(1, run.populatedData.size());
		assertEquals(1, wm.facts.size());

		Cheese c = (Cheese) wm.globals.get("c");
		assertEquals("cheddar", c.getType());
		Cheese c2 = (Cheese) wm.facts.get(0);
		assertEquals("stilton", c2.getType());

	}

	@SuppressWarnings("deprecation")
	// F**** dates in java. What a mess. Someone should die.
	public void testSimulatedDate() throws Exception {
		Scenario sc = new Scenario();
		MockWorkingMemory wm = new MockWorkingMemory();
		ScenarioRunner run = new ScenarioRunner(sc, null, wm);
		TimeMachine tm = run.workingMemory.getTimeMachine();

		// love you
		long time = tm.getNow().getTimeInMillis();

		Thread.sleep(100);
		long future = tm.getNow().getTimeInMillis();
		assertTrue(future > time);

		sc.scenarioSimulatedDate = new Date("10-Jul-1974");
		run = new ScenarioRunner(sc, null, wm);
		tm = run.workingMemory.getTimeMachine();

		long expected = sc.scenarioSimulatedDate.getTime();
		assertEquals(expected, tm.getNow().getTimeInMillis());
		Thread.sleep(50);
		assertEquals(expected, tm.getNow().getTimeInMillis());

	}

	/**
	 * Do a kind of end to end test with some real rules.
	 */
	public void testIntegration() throws Exception {
		Scenario sc = new Scenario();
		sc.facts = new FactData[] {
				new FactData("Cheese", "c1", new FieldData[] {
						new FieldData("type", "cheddar", false),
						new FieldData("price", "42", false) }, false),
						new FactData("Person", "p", new FieldData[0] , true)
				};


		sc.ruleTrace.rules = new String[] {"rule1", "rule2" };
		sc.ruleTrace.inclusive = true;

		sc.assertions = new Assertion[5];

		sc.assertions[0] =	new VerifyFact("c1", new VerifyField[] {
					new VerifyField("type", "cheddar")

		});

		sc.assertions[1] = new VerifyFact("p", new VerifyField[] {
					new VerifyField("name", "rule1"),
					new VerifyField("status", "rule2")

		});

		sc.assertions[2] = new VerifyRuleFired("rule1", 1, null);
		sc.assertions[3] = new VerifyRuleFired("rule2", 1, null);
		sc.assertions[4] = new VerifyRuleFired("rule3", 1, null);

		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");
		resolver.addImport("org.drools.Person");

        WorkingMemory wm = getWorkingMemory("test_rules2.drl");

        ScenarioRunner run = new ScenarioRunner(sc, resolver, (InternalWorkingMemory) wm);

        assertSame(run.scenario, sc);

        assertTrue(sc.wasSuccessful());

        Person p = (Person) run.globalData.get("p");
        assertEquals("rule1", p.getName());
        assertEquals("rule2", p.getStatus());
        assertEquals(0, p.getAge());


	}




}
