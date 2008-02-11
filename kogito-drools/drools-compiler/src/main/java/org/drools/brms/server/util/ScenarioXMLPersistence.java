package org.drools.brms.server.util;

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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * Persists the scenario model.
 * @author Michael Neale
 */
public class ScenarioXMLPersistence {

    private XStream                     xt;
    private static final ScenarioXMLPersistence INSTANCE = new ScenarioXMLPersistence();

    private ScenarioXMLPersistence() {
    	xt = new XStream(new DomDriver());
    	xt.alias("scenario", Scenario.class);
    	xt.alias("execution-trace", ExecutionTrace.class);
    	xt.alias("expectation", Expectation.class);
    	xt.alias("fact-data", FactData.class);
    	xt.alias("field-data", FieldData.class);
    	xt.alias("fixture", Fixture.class);
    	xt.alias("retract-fact", RetractFact.class);
    	xt.alias("expect-fact", VerifyFact.class);
    	xt.alias("expect-field", VerifyField.class);
    	xt.alias("expect-rule", VerifyRuleFired.class);
    }

    public static ScenarioXMLPersistence getInstance() {
    	return INSTANCE;
    }



    public String marshal(Scenario sc) {
    	if (sc.fixtures.size() > 1  && sc.fixtures.get(sc.fixtures.size() - 1) instanceof ExecutionTrace) {
    		sc.fixtures.remove(sc.fixtures.size() - 1);
    	}
    	return xt.toXML(sc);
    }

    public Scenario unmarshal(String xml) {
    	if (xml == null) return new Scenario();
    	if (xml.trim().equals("")) return new Scenario();
    	return (Scenario) xt.fromXML(xml);
    }

}
