package org.drools.testframework;

import static org.mvel.MVEL.eval;

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



			//stub out any rules we don't want to have the consequences firing of.
			HashSet<String> ruleList = new HashSet<String>();
			ruleList.addAll(scenario.rules);
			//TestingEventListener.stubOutRules(ruleList, wm.getRuleBase(), scenario.inclusive);

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

					if (listener != null) wm.removeEventListener(listener); //remove the old
					listener = new TestingEventListener();

					wm.addEventListener(listener);

					//set up the time machine
					applyTimeMachine(wm, executionTrace);

					//love you
					long time = System.currentTimeMillis();
					wm.fireAllRules(listener.getAgendaFilter(ruleList, scenario.inclusive),scenario.maxRuleFirings);
					executionTrace.executionTimeResult = System.currentTimeMillis() - time;
					executionTrace.numberOfRulesFired = listener.totalFires;
					executionTrace.rulesFired = listener.getRulesFiredSummary();


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
		} else {
			//normal time.
			wm.setTimeMachine(new TimeMachine());
		}
	}

	void verify(VerifyRuleFired assertion, Map<String, Integer> firingCounts) {

		assertion.actualResult = firingCounts.containsKey(assertion.ruleName) ? firingCounts
				.get(assertion.ruleName)
				: 0;
		if (assertion.expectedFire != null) {
			if (assertion.expectedFire) {
				if (assertion.actualResult > 0) {
					assertion.successResult = true;
					assertion.explanation = "Rule [" + assertion.ruleName + "] was actived " + assertion.actualResult + " times.";
				} else {
					assertion.successResult = false;
					assertion.explanation = "Rule [" + assertion.ruleName + "] was not activated. Expected it to be activated.";
				}
			} else {
				if (assertion.actualResult == 0) {
					assertion.successResult = true;
					assertion.explanation = "Rule [" + assertion.ruleName + "] was not activated.";
				} else {
					assertion.successResult = false;
					assertion.explanation = "Rule [" + assertion.ruleName + "] was activated " + assertion.actualResult + " times, but expected none.";
				}
			}
		}

		if (assertion.expectedCount != null) {
			if (assertion.actualResult.equals(assertion.expectedCount)) {
				assertion.successResult = true;
				assertion.explanation = "Rule [" + assertion.ruleName + "] activated " + assertion.actualResult + " times.";
			} else {
				assertion.successResult = false;
				assertion.explanation = "Rule [" + assertion.ruleName + "] activated " + assertion.actualResult + " times. Expected " + assertion.expectedCount + " times.";
			}
		}
	}


	void verify(VerifyFact value) {


		if (!value.anonymous) {
			Object fact = this.populatedData.get(value.name);
			if (fact == null) fact = this.globalData.get(value.name);
			checkFact(value, fact);
		} else {
			Iterator obs = this.workingMemory.iterateObjects();
			while(obs.hasNext()) {
				Object fact = obs.next();
				if (fact.getClass().getSimpleName().equals(value.name)) {
					checkFact(value, fact);
					if (value.wasSuccessful()) return;
				}
			}
			for (Iterator iterator = value.fieldValues.iterator(); iterator.hasNext();) {
				VerifyField vfl = (VerifyField) iterator.next();
				vfl.successResult = Boolean.FALSE;
				vfl.actualResult = "Not found";
			}
 		}
	}

	private void checkFact(VerifyFact value, Object fact) {
		for (int i = 0; i < value.fieldValues.size(); i++) {
			VerifyField fld = (VerifyField) value.fieldValues.get(i);
			Map<String, Object> st = new HashMap<String, Object>();
			st.put("__fact__", fact);
			if (fld.expected != null) {
				Object expectedVal = fld.expected.trim();
				if (fld.expected.startsWith("=")) {
					expectedVal = eval(fld.expected.substring(1), this.populatedData);
				}
				st.put("__expected__", expectedVal);

				fld.successResult = (Boolean) eval("__fact__." + fld.fieldName
						+ " " + fld.operator  + " __expected__", st);


				if (!fld.successResult) {
					Object actual = eval("__fact__." + fld.fieldName, st);
					fld.actualResult = (actual != null) ? actual.toString() : "";

					if (fld.operator.equals("==")) {
						fld.explanation = "[" + value.name + "] field [" + fld.fieldName + "] was [" + fld.actualResult
											+ "] expected [" + fld.expected + "].";
					} else {
						fld.explanation = "[" + value.name + "] field [" + fld.fieldName + "] was not expected to be [" + fld.actualResult
						+ "].";
					}
				} else {
					if (fld.operator.equals("==")) {
						fld.explanation = "[" + value.name + "] field [" + fld.fieldName + "] was [" + fld.expected + "].";
					} else if (fld.operator.equals("!=")){
						fld.explanation = "[" + value.name + "] field [" + fld.fieldName + "] was not [" + fld.expected + "].";
					}
				}
			}

		}
	}



	Object populateFields(FactData fact, Map<String, Object> factData, Object factObject) {
		for (int i = 0; i < fact.fieldData.size(); i++) {
			FieldData field = (FieldData) fact.fieldData.get(i);
			Object val;
			if (field.value != null && !field.value.equals("")) {
				if (field.value.startsWith("=")) {
					// eval the val into existence
					val = eval(field.value.substring(1), factData);
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

