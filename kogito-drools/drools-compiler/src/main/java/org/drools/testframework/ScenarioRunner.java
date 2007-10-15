package org.drools.testframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.base.TypeResolver;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;

import static org.mvel.MVEL.*;

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
	 */
	public ScenarioRunner(Scenario scenario, TypeResolver resolver) throws ClassNotFoundException {
		Map<String, Object> factData = new HashMap<String, Object>();
		this.scenario = scenario;

		//have to go and instansiate all the facts
		for (int i = 0; i < scenario.facts.length; i++) {
			FactData fact = scenario.facts[i];
			Object f = eval("new " + resolver.getFullTypeName(fact.type) + "()");
			factData.put(fact.name, f);
			populate(fact, factData);
		}

		this.populatedData = factData;
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
			Map vars = new HashMap();
			vars.putAll(factData);
			vars.put("__val__", val);
			eval(fact.name + "." + field.name + " = __val__", vars);
		}
	}





}
