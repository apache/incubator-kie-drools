package org.drools.testframework;

import static org.mvel.MVEL.eval;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.base.TypeResolver;
import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.Expectation;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Fixture;
import org.drools.brms.client.modeldriven.testing.RetractFact;
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
	final Map<String, FactHandle> factHandles = new HashMap<String, FactHandle>();

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

		TestingEventListener listener = null;

		for (Iterator iterator = scenario.globals.iterator(); iterator.hasNext();) {
			FactData fact = (FactData) iterator.next();
			Object f = eval("new " + resolver.getFullTypeName(fact.type) + "()");
			populateFields(fact, globalData, f);
			globalData.put(fact.name, f);
			wm.setGlobal(fact.name, f);
		}

		for (Iterator<Fixture> iterator = scenario.fixtures.iterator(); iterator.hasNext();) {
			Fixture fx = iterator.next();

			if (fx instanceof FactData) {
				//deal with facts and globals
				FactData fact = (FactData)fx;
				Object f = (fact.isModify)? this.populatedData.get(fact.name) : eval("new " + resolver.getFullTypeName(fact.type) + "()");
				if (fact.isModify) {
					if (!this.factHandles.containsKey(fact.name)) {
						throw new IllegalArgumentException("Was not a previously inserted fact. [" + fact.name  + "]");
					}
					populateFields(fact, populatedData, f);
					this.workingMemory.update(this.factHandles.get(fact.name), f);
				} else /* a new one */ {
					populateFields(fact, populatedData, f);
					populatedData.put(fact.name, f);
					this.factHandles.put(fact.name, wm.insert(f));
				}
			} else if (fx instanceof RetractFact) {
				RetractFact f = (RetractFact)fx;
				this.workingMemory.retract(this.factHandles.get(f.name));
				this.populatedData.remove(f.name);
			} else if (fx instanceof ExecutionTrace) {
				ExecutionTrace executionTrace = (ExecutionTrace)fx;
				//create the listener to trace rules
				HashSet<String> ruleList = new HashSet<String>();
				ruleList.addAll(Arrays.asList(executionTrace.rules));
				listener = new TestingEventListener(ruleList, wm.getRuleBase(), executionTrace.inclusive);
				wm.addEventListener(listener);

				//set up the time machine
				applyTimeMachine(wm, executionTrace);

				//love you
				long time = System.currentTimeMillis();
				wm.fireAllRules(scenario.maxRuleFirings);
				executionTrace.executionTimeResult = System.currentTimeMillis() - time;
				executionTrace.firingCounts = listener.firingCounts;
			} else if (fx instanceof Expectation) {
					Expectation assertion = (Expectation) fx;
					if (assertion instanceof VerifyFact) {
						verify((VerifyFact) assertion);
					} else if (assertion instanceof VerifyRuleFired) {
						verify((VerifyRuleFired) assertion,
								(listener.firingCounts != null) ? listener.firingCounts : new HashMap<String, Integer>());
					}
			} else {
				throw new IllegalArgumentException("Not sure what to do with " + fx);
			}



		}







	}

	private void applyTimeMachine(final InternalWorkingMemory wm,
			ExecutionTrace executionTrace) {
		if (executionTrace.scenarioSimulatedDate != null) {
			final Calendar now = Calendar.getInstance();
			now.setTimeInMillis(executionTrace.scenarioSimulatedDate.getTime());
			wm.setTimeMachine(new TimeMachine() {
				@Override
				public Calendar getNow() {
					return now;
				}
			});
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

	Object populateFields(FactData fact, Map<String, Object> factData, Object factObject) {
		for (int i = 0; i < fact.fieldData.length; i++) {
			FieldData field = fact.fieldData[i];
			Object val;
			if (field.value != null && !field.value.equals("")) {
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
		}
		return factObject;
	}

}
