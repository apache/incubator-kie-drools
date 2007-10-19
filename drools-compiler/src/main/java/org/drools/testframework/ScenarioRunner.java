package org.drools.testframework;

import static org.mvel.MVEL.eval;

import java.util.HashMap;
import java.util.Map;

import org.drools.base.TypeResolver;
import org.drools.brms.client.modeldriven.testing.AssertFactValue;
import org.drools.brms.client.modeldriven.testing.AssertFieldValue;
import org.drools.brms.client.modeldriven.testing.Assertion;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;

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
	 * This requires that the classloader for the context be set appropraitely. The PackageBuilder
	 * can provide a suitable TypeResolver for a given package header, and the Package config can provide
	 * a classloader.
	 *
	 */
	public ScenarioRunner(Scenario scenario, TypeResolver resolver) throws ClassNotFoundException {
		Map<String, Object> factData = new HashMap<String, Object>();
		this.scenario = scenario;

		//have to go and instanciate all the facts
		for (int i = 0; i < scenario.facts.length; i++) {
			FactData fact = scenario.facts[i];
			Object f = eval("new " + resolver.getFullTypeName(fact.type) + "()");
			factData.put(fact.name, f);
			populate(fact, factData);
		}

		this.populatedData = factData;

		//now run the rules...

		//now check the results...
		for (int i = 0; i < scenario.assertions.length; i++) {
			Assertion assertion = scenario.assertions[i];
			if (assertion instanceof AssertFactValue) {
				verify((AssertFactValue)assertion);
			}
		}
	}



	private void verify(AssertFactValue value) {
		for (int i = 0; i < value.fieldValues.length; i++) {
			AssertFieldValue verify = value.fieldValues[i];
			verify.isChecked = true;
			//hmmm... need a ruleset that we can use to take data
			//from the WM under test - perhaps ALL the data
			//if checking a named fact, may not be worth the hassle
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

	public Map<String, Object> getFacts() {
		return this.populatedData;
	}





}
