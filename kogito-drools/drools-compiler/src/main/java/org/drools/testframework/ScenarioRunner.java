package org.drools.testframework;

import static org.mvel.MVEL.eval;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.base.TypeResolver;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.Assertion;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

/**
 * This actually runs the test scenarios.
 *
 * @author Michael Neale
 *
 */
public class ScenarioRunner {

	final Scenario scenario;
	final Map<String, Object> populatedData;


	/**
	 * @param scenario The scenario to run.
	 * @param resolver A populated type resolved to be used to resolve the types in the scenario.
	 *
	 * For info on how to invoke this, see ContentPackageAssemblerTest.testPackageWithRuleflow in drools-jbrms
	 * This requires that the classloader for the thread context be set appropriately. The PackageBuilder
	 * can provide a suitable TypeResolver for a given package header, and the Package config can provide
	 * a classloader.
	 *
	 */
	public ScenarioRunner(Scenario scenario, TypeResolver resolver, WorkingMemory wm) throws ClassNotFoundException {
		Map<String, Object> factData = new HashMap<String, Object>();
		this.scenario = scenario;

		//have to go and create all the facts
		for (int i = 0; i < scenario.facts.length; i++) {
			FactData fact = scenario.facts[i];
			Object f = eval("new " + resolver.getFullTypeName(fact.type) + "()");
			factData.put(fact.name, f);
			populate(fact, factData);
		}
		this.populatedData = factData;

		HashSet<String> ruleList = new HashSet<String>();
		ruleList.addAll(Arrays.asList(scenario.ruleTrace.rules));
		TestingEventListener listener = new TestingEventListener(ruleList, wm.getRuleBase(), scenario.ruleTrace.inclusive);
		wm.addEventListener(listener);

		//now run the rules...
		insertData(wm, this.populatedData);
		wm.fireAllRules(scenario.maxRuleFirings);
		scenario.ruleTrace.firingCounts = listener.firingCounts;

		//now check the results...
		for (int i = 0; i < scenario.assertions.length; i++) {
			Assertion assertion = scenario.assertions[i];
			if (assertion instanceof VerifyFact) {
				verify((VerifyFact)assertion);
			} else if (assertion instanceof VerifyRuleFired) {
				verify((VerifyRuleFired) assertion, scenario.ruleTrace.firingCounts);
			}
 		}

	}



	private void verify(VerifyRuleFired assertion, Map<String, Integer> firingCounts) {
		assertion.actual = firingCounts.containsKey(assertion.ruleName) ?  firingCounts.get(assertion.ruleName) : 0;
		if (assertion.expectNotFire) {
			assertion.success = assertion.actual == 0;
		} else if (assertion.expectFire) {
			assertion.success = assertion.actual > 0;
		} else {
			assertion.success = assertion.actual == assertion.expectedCount;
		}
	}



	private void insertData(WorkingMemory wm, Map<String, Object> data) {
		for (Iterator<Object> iterator = data.values().iterator(); iterator.hasNext();) {
			wm.insert(iterator.next());
		}
	}



	void verify(VerifyFact value) {
		Object fact = this.populatedData.get(value.factName);
		for (int i = 0; i < value.fieldValues.length; i++) {
			VerifyField fld = value.fieldValues[i];
			Map<String, Object> st = new HashMap<String, Object>();
			st.put("__fact__", fact);
			st.put("__expected__", fld.expected);
			fld.success =  (Boolean) eval( "__fact__." + fld.fieldName + " == __expected__", st);
 			if (!fld.success) {
 				fld.actual = eval("__fact__." + fld.fieldName, st).toString();
 			}
		}
	}



	private void populate(FactData fact, Map<String, Object> factData) {
		for (int i = 0; i < fact.fieldData.length; i++) {
			FieldData field = fact.fieldData[i];
			Object val;
			if (field.isExpression) {
				//eval the val into existence
				val = eval(field.value, factData);
			} else {
				val = field.value;
			}
			Map<String, Object> vars = new HashMap<String, Object>();
			vars.putAll(factData);
			vars.put("__val__", val);
			eval(fact.name + "." + field.name + " = __val__", vars);
		}
	}






}
