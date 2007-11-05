package org.drools.testframework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.WorkingMemory;
import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.Expectation;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.RetractFact;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;
import org.drools.common.InternalWorkingMemory;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.rule.TimeMachine;

import com.thoughtworks.xstream.XStream;

public class ScenarioRunnerTest extends RuleUnit {

	public void testPopulateFacts() throws Exception {
		Scenario sc = new Scenario();
		FactData[] facts = new FactData[] {
				new FactData("Cheese", "c1", new FieldData[] {
						new FieldData("type", "cheddar", false),
						new FieldData("price", "42", false) },  false),
				new FactData("Person", "p1", new FieldData[] {
						new FieldData("name", "mic", false),
						new FieldData("age", "30 + 3", true) }, false) };

		sc.fixtures.addAll(Arrays.asList(facts));
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

	public void testPopulateNoData() throws Exception {
		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");
		ScenarioRunner run = new ScenarioRunner(new Scenario(), resolver, new MockWorkingMemory());
		run.populatedData.clear();
		Cheese c = new Cheese();
		c.setType("whee");
		c.setPrice(1);
		run.populatedData.put("x", c);

		assertEquals(1, c.getPrice());

		FactData fd = new FactData("Cheese", "x", new FieldData[] {new FieldData("type", "", false), new FieldData("price", "42", false)}, false);

		run.populateFields(fd, run.populatedData, c);
		assertEquals("whee", c.getType());
		assertEquals(42, c.getPrice());
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
				new VerifyField("type", "cheddar", "=="),
				new VerifyField("price", "42", "==") };

		runner.verify(vf);
		for (int i = 0; i < vf.fieldValues.length; i++) {
			assertTrue(vf.fieldValues[i].successResult);
		}

		vf = new VerifyFact();
		vf.factName = "f2";
		vf.fieldValues = new VerifyField[] {
				new VerifyField("name", "michael", "=="),
				new VerifyField("age", "33", "==") };

		runner.verify(vf);
		for (int i = 0; i < vf.fieldValues.length; i++) {
			assertTrue(vf.fieldValues[i].successResult);
		}

		// test one false
		vf = new VerifyFact();
		vf.factName = "f2";
		vf.fieldValues = new VerifyField[] { new VerifyField("name", "mark", "=="),
				new VerifyField("age", "33", "==") };

		runner.verify(vf);
		assertFalse(vf.fieldValues[0].successResult);
		assertTrue(vf.fieldValues[1].successResult);

		assertEquals("michael", vf.fieldValues[0].actualResult);
		assertEquals("mark", vf.fieldValues[0].expected);

		// test 2 false
		vf = new VerifyFact();
		vf.factName = "f2";
		vf.fieldValues = new VerifyField[] { new VerifyField("name", "mark", "=="),
				new VerifyField("age", "32", "==") };

		runner.verify(vf);
		assertFalse(vf.fieldValues[0].successResult);
		assertFalse(vf.fieldValues[1].successResult);

		assertEquals("michael", vf.fieldValues[0].actualResult);
		assertEquals("mark", vf.fieldValues[0].expected);

		assertEquals("33", vf.fieldValues[1].actualResult);
		assertEquals("32", vf.fieldValues[1].expected);

	}

	public void testVerifyFactsWithOperator() throws Exception {
		ScenarioRunner runner = new ScenarioRunner(new Scenario(), null,
				new MockWorkingMemory());
		Cheese f1 = new Cheese("cheddar", 42);
		runner.populatedData.put("f1", f1);

		// test all true
		VerifyFact vf = new VerifyFact();
		vf.factName = "f1";
		vf.fieldValues = new VerifyField[] {
				new VerifyField("type", "cheddar", "=="),
				new VerifyField("price", "4777", "!=") };
		runner.verify(vf);
		for (int i = 0; i < vf.fieldValues.length; i++) {
			assertTrue(vf.fieldValues[i].successResult);
		}

		vf = new VerifyFact();
		vf.factName = "f1";
		vf.fieldValues = new VerifyField[] {
				new VerifyField("type", "cheddar", "!=")};
		runner.verify(vf);
		assertFalse(vf.fieldValues[0].successResult);


	}

	public void testDummyRunNoRules() throws Exception {
		Scenario sc = new Scenario();
		FactData[] facts = new FactData[] { new FactData("Cheese", "c1",
				new FieldData[] { new FieldData("type", "cheddar", false),
						new FieldData("price", "42", false) }, false) };

		VerifyFact[] assertions = new VerifyFact[] { new VerifyFact("c1",
				new VerifyField[] { new VerifyField("type", "cheddar", "=="),
						new VerifyField("price", "42", "==") }) };

		sc.fixtures.addAll(Arrays.asList(facts));
		sc.fixtures.addAll(Arrays.asList(assertions));

		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");

		MockWorkingMemory wm = new MockWorkingMemory();
		ScenarioRunner runner = new ScenarioRunner(sc, resolver, wm);
		assertEquals(1, wm.facts.size());
		assertEquals(runner.populatedData.get("c1"), wm.facts.get(0));

		assertTrue(runner.populatedData.containsKey("c1"));
		VerifyFact vf = (VerifyFact) assertions[0];
		for (int i = 0; i < vf.fieldValues.length; i++) {
			assertTrue(vf.fieldValues[i].successResult);
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
		assertTrue(v.successResult);
		assertEquals(2, v.actualResult.intValue());

		v = new VerifyRuleFired();
		v.ruleName = "foo";
		v.expectedFire = false;
		runner.verify(v, firingCounts);
		assertFalse(v.successResult);
		assertEquals(2, v.actualResult.intValue());

		v = new VerifyRuleFired();
		v.ruleName = "foo";
		v.expectedCount = 2;

		runner.verify(v, firingCounts);
		assertTrue(v.successResult);
		assertEquals(2, v.actualResult.intValue());

	}

	public void testTestingEventListener() throws Exception {
		Scenario sc = new Scenario();
		sc.rules.add("foo"); sc.rules.add("bar");
		ExecutionTrace ext = new ExecutionTrace();




		sc.fixtures.add(ext);

		MockWorkingMemory wm = new MockWorkingMemory();
		ScenarioRunner run = new ScenarioRunner(sc, null, wm);
		assertEquals(wm, run.workingMemory);
		assertNotNull(wm.agendaEventListener);
		assertTrue(wm.agendaEventListener instanceof TestingEventListener);
		TestingEventListener lnr = (TestingEventListener) wm.agendaEventListener;
		assertEquals(2, sc.rules.size());
		assertTrue(sc.rules.contains("foo"));
		assertTrue(sc.rules.contains("bar"));
	}

	public void testWithGlobals() throws Exception {
		Scenario sc = new Scenario();
		FactData[] facts = new FactData[] {
				new FactData("Cheese", "c2", new FieldData[] { new FieldData(
						"type", "stilton", false) }, false) };
		sc.globals.add(new FactData("Cheese", "c", new FieldData[] { new FieldData(
				"type", "cheddar", false) }, false));
		sc.fixtures.addAll(Arrays.asList(facts));

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

		ExecutionTrace ext = new ExecutionTrace();
		ext.scenarioSimulatedDate = new Date("10-Jul-1974");
		sc.fixtures.add(ext);
		run = new ScenarioRunner(sc, null, wm);
		tm = run.workingMemory.getTimeMachine();

		long expected = ext.scenarioSimulatedDate.getTime();
		assertEquals(expected, tm.getNow().getTimeInMillis());
		Thread.sleep(50);
		assertEquals(expected, tm.getNow().getTimeInMillis());

	}

	/**
	 * Do a kind of end to end test with some real rules.
	 */
	public void testIntegrationWithSuccess() throws Exception {

		Scenario sc = new Scenario();
		FactData[] facts = new FactData[] {
				new FactData("Cheese", "c1", new FieldData[] {
						new FieldData("type", "cheddar", false),
						new FieldData("price", "42", false) },  false)

				};
		sc.globals.add(new FactData("Person", "p", new FieldData[0] , false));
		sc.fixtures.addAll(Arrays.asList(facts));

		ExecutionTrace executionTrace = new ExecutionTrace();


		sc.rules.add("rule1");
		sc.rules.add("rule2");
		sc.inclusive = true;
		sc.fixtures.add(executionTrace);

		Expectation[] assertions = new Expectation[5];

		assertions[0] =	new VerifyFact("c1", new VerifyField[] {
					new VerifyField("type", "cheddar", "==")

		});

		assertions[1] = new VerifyFact("p", new VerifyField[] {
					new VerifyField("name", "rule1", "=="),
					new VerifyField("status", "rule2", "==")

		});

		assertions[2] = new VerifyRuleFired("rule1", 1, null);
		assertions[3] = new VerifyRuleFired("rule2", 1, null);
		assertions[4] = new VerifyRuleFired("rule3", 1, null);

		sc.fixtures.addAll(Arrays.asList(assertions));

		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");
		resolver.addImport("org.drools.Person");

        WorkingMemory wm = getWorkingMemory("test_rules2.drl");

        ScenarioRunner run = new ScenarioRunner(sc, resolver, (InternalWorkingMemory) wm);

        assertEquals(3, executionTrace.numberOfRulesFired);

        assertSame(run.scenario, sc);

        assertTrue(sc.wasSuccessful());

        Person p = (Person) run.globalData.get("p");
        assertEquals("rule1", p.getName());
        assertEquals("rule2", p.getStatus());
        assertEquals(0, p.getAge());


        assertTrue((new Date()).after(sc.lastRunResult));
        assertTrue(executionTrace.executionTimeResult != -1);

	}

	public void testIntgerationStateful() throws Exception {
		Scenario sc = new Scenario();
		sc.fixtures.add(new FactData("Cheese", "c1", new FieldData[] {new FieldData("price", "1", false)}, false));
		ExecutionTrace ex = new ExecutionTrace();
		sc.fixtures.add(ex);
		sc.fixtures.add(new FactData("Cheese", "c2", new FieldData[] {new FieldData("price", "2", false)}, false));
		sc.fixtures.add(new VerifyFact("c1", new VerifyField[] {new VerifyField("type", "rule1", "==")}));
		ex = new ExecutionTrace();
		sc.fixtures.add(ex);
		sc.fixtures.add(new VerifyFact("c1", new VerifyField[] {new VerifyField("type", "rule2", "==")}));

		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");


        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
        ScenarioRunner run = new ScenarioRunner(sc, resolver, (InternalWorkingMemory) wm);

        Cheese c1 = (Cheese) run.populatedData.get("c1");
        Cheese c2 = (Cheese) run.populatedData.get("c2");

        assertEquals("rule2", c1.getType());
        assertEquals("rule2", c2.getType());

        assertTrue(sc.wasSuccessful());


	}

	public void testIntegrationWithModify() throws Exception {
		Scenario sc = new Scenario();
		sc.fixtures.add(new FactData("Cheese", "c1", new FieldData[] {new FieldData("price", "1", false)}, false));

		sc.fixtures.add(new ExecutionTrace());

		sc.fixtures.add(new VerifyFact("c1", new VerifyField[] {new VerifyField("type", "rule1", "==")}));

		sc.fixtures.add(new FactData("Cheese", "c1", new FieldData[] {new FieldData("price", "42", false)}, true));
		sc.fixtures.add(new ExecutionTrace());

		sc.fixtures.add(new VerifyFact("c1", new VerifyField[] {new VerifyField("type", "rule3", "==")}));



		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");


        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
        ScenarioRunner run = new ScenarioRunner(sc, resolver, (InternalWorkingMemory) wm);

        Cheese c1 = (Cheese) run.populatedData.get("c1");


        assertEquals("rule3", c1.getType());


        assertTrue(sc.wasSuccessful());
	}

	public void testIntegrationWithRetract() throws Exception {
		Scenario sc = new Scenario();
		sc.fixtures.add(new FactData("Cheese", "c1", new FieldData[] {new FieldData("price", "46", false), new FieldData("type", "XXX", false)}, false));
		sc.fixtures.add(new FactData("Cheese", "c2", new FieldData[] {new FieldData("price", "42", false)}, false));
		sc.fixtures.add(new ExecutionTrace());

		sc.fixtures.add(new VerifyFact("c1", new VerifyField[] {new VerifyField("type", "XXX", "==")}));

		sc.fixtures.add(new RetractFact("c2"));
		sc.fixtures.add(new ExecutionTrace());

		sc.fixtures.add(new VerifyFact("c1", new VerifyField[] {new VerifyField("type", "rule4", "==")}));



		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");


        WorkingMemory wm = getWorkingMemory("test_stateful.drl");
        ScenarioRunner run = new ScenarioRunner(sc, resolver, (InternalWorkingMemory) wm);

        Cheese c1 = (Cheese) run.populatedData.get("c1");

        assertEquals("rule4", c1.getType());
        assertFalse(run.populatedData.containsKey("c2"));

        assertTrue(sc.wasSuccessful());
	}

	public void testIntegrationWithFailure() throws Exception {
		Scenario sc = new Scenario();
		FactData[] facts = new FactData[] {
				new FactData("Cheese", "c1", new FieldData[] {
						new FieldData("type", "cheddar", false),
						new FieldData("price", "42", false) }, false)

				};
		sc.fixtures.addAll(Arrays.asList(facts));
		sc.globals.add(new FactData("Person", "p", new FieldData[0] , false));

		ExecutionTrace executionTrace = new ExecutionTrace();
		sc.rules.add("rule1");
		sc.rules.add("rule2");
		sc.inclusive = true;
		sc.fixtures.add(executionTrace);

		Expectation[] assertions = new Expectation[5];

		assertions[0] =	new VerifyFact("c1", new VerifyField[] {
					new VerifyField("type", "cheddar", "==")

		});

		assertions[1] = new VerifyFact("p", new VerifyField[] {
					new VerifyField("name", "XXX", "=="),
					new VerifyField("status", "rule2", "==")

		});

		assertions[2] = new VerifyRuleFired("rule1", 1, null);
		assertions[3] = new VerifyRuleFired("rule2", 1, null);
		assertions[4] = new VerifyRuleFired("rule3", 2, null);

		sc.fixtures.addAll(Arrays.asList(assertions));

		TypeResolver resolver = new ClassTypeResolver(new HashSet<Object>(),
				Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");
		resolver.addImport("org.drools.Person");

        WorkingMemory wm = getWorkingMemory("test_rules2.drl");

        ScenarioRunner run = new ScenarioRunner(sc, resolver, (InternalWorkingMemory) wm);

        assertSame(run.scenario, sc);

        assertFalse(sc.wasSuccessful());

        VerifyFact vf = (VerifyFact) assertions[1];
        assertFalse(vf.fieldValues[0].successResult);
        assertEquals("XXX", vf.fieldValues[0].expected);
        assertEquals("rule1", vf.fieldValues[0].actualResult);

        VerifyRuleFired vr = (VerifyRuleFired) assertions[4];
        assertFalse(vr.successResult);

        assertEquals(2, vr.expectedCount.intValue());
        assertEquals(1, vr.actualResult.intValue());


	}






}
