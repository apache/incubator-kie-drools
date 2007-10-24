package org.drools.testframework;

import static org.mvel.MVEL.eval;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.base.TypeResolver;
import org.drools.brms.client.modeldriven.testing.Assertion;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.TimeMachine;

/**
 * This actually runs the test scenarios.
 *
 * @author Michael Neale
 *
 */
public class ScenarioRunner {

	final Scenario scenario;
	final Map<String, Object> populatedData = new HashMap<String, Object>();
	final Map<String, Object> globalData = new HashMap<String, Object>();
	final InternalWorkingMemory workingMemory;

	/**
	 * @param scenario
	 *            The scenario to run.
	 * @param resolver
	 *            A populated type resolved to be used to resolve the types in
	 *            the scenario.
	 *
	 * For info on how to invoke this, see
	 * ContentPackageAssemblerTest.testPackageWithRuleflow in drools-jbrms This
	 * requires that the classloader for the thread context be set
	 * appropriately. The PackageBuilder can provide a suitable TypeResolver for
	 * a given package header, and the Package config can provide a classloader.
	 *
	 */
	public ScenarioRunner(final Scenario scenario, final TypeResolver resolver,
			final InternalWorkingMemory wm) throws ClassNotFoundException {
		this.scenario = scenario;
		this.workingMemory = wm;
		scenario.lastRunResult = new Date();



		// have to go and create all the facts
		for (int i = 0; i < scenario.facts.length; i++) {
			FactData fact = scenario.facts[i];
			Object f = eval("new " + resolver.getFullTypeName(fact.type) + "()");
			if (fact.isGlobal) {
				populateFields(fact, globalData, f);
				globalData.put(fact.name, f);
			} else {
				populateFields(fact, populatedData, f);
				populatedData.put(fact.name, f);
			}
		}

		//create the listener to trace rules
		HashSet<String> ruleList = new HashSet<String>();
		ruleList.addAll(Arrays.asList(scenario.ruleTrace.rules));
		TestingEventListener listener = new TestingEventListener(ruleList, wm
				.getRuleBase(), scenario.ruleTrace.inclusive);
		wm.addEventListener(listener);

		//set up the time machine
		if (scenario.scenarioSimulatedDate != null) {
			final Calendar now = Calendar.getInstance();
			now.setTimeInMillis(scenario.scenarioSimulatedDate.getTime());
			wm.setTimeMachine(new TimeMachine() {
				@Override
				public Calendar getNow() {
					return now;
				}
			});
		}

		// now run the rules...
		applyData(wm, this.populatedData, this.globalData);
		//love you
		long time = System.currentTimeMillis();
		wm.fireAllRules(scenario.maxRuleFirings);
		scenario.executionTimeResult = System.currentTimeMillis() - time;
		scenario.ruleTrace.firingCounts = listener.firingCounts;

		// now check the results...
		for (int i = 0; i < scenario.assertions.length; i++) {
			Assertion assertion = scenario.assertions[i];
			if (assertion instanceof VerifyFact) {
				verify((VerifyFact) assertion);
			} else if (assertion instanceof VerifyRuleFired) {
				verify((VerifyRuleFired) assertion, listener.firingCounts);
			}
		}

	}

	void verify(VerifyRuleFired assertion, Map<String, Integer> firingCounts) {
		assertion.actualResult = firingCounts.containsKey(assertion.ruleName) ? firingCounts
				.get(assertion.ruleName)
				: 0;
		if (assertion.expectedFire != null) {
			assertion.successResult = assertion.expectedFire ? assertion.actualResult > 0
					: assertion.actualResult == 0;
		}
		if (assertion.expectedCount != null) {
			assertion.successResult = assertion.actualResult
					.equals(assertion.expectedCount);
		}
	}

	private void applyData(WorkingMemory wm, Map<String, Object> facts, Map<String, Object> globals) {
		for (Map.Entry<String, Object> e : globals.entrySet()) {
			wm.setGlobal(e.getKey(), e.getValue());
		}
		for (Object f : facts.values()) {
			wm.insert(f);
		}
	}

	void verify(VerifyFact value) {
		Object fact = this.populatedData.get(value.factName);
		if (fact == null) fact = this.globalData.get(value.factName);
		for (int i = 0; i < value.fieldValues.length; i++) {
			VerifyField fld = value.fieldValues[i];
			Map<String, Object> st = new HashMap<String, Object>();
			st.put("__fact__", fact);
			st.put("__expected__", fld.expected);
			fld.successResult = (Boolean) eval("__fact__." + fld.fieldName
					+ " == __expected__", st);
			if (!fld.successResult) {
				fld.actualResult = eval("__fact__." + fld.fieldName, st).toString();
			}
		}
	}

	private Object populateFields(FactData fact, Map<String, Object> factData, Object factObject) {
		for (int i = 0; i < fact.fieldData.length; i++) {
			FieldData field = fact.fieldData[i];
			Object val;
			if (field.isExpression) {
				// eval the val into existence
				val = eval(field.value, factData);
			} else {
				val = field.value;
			}
			Map<String, Object> vars = new HashMap<String, Object>();
			vars.putAll(factData);
			vars.put("__val__", val);
			vars.put("__fact__", factObject);
			eval("__fact__." + field.name + " = __val__", vars);
		}
		return factObject;
	}

}
