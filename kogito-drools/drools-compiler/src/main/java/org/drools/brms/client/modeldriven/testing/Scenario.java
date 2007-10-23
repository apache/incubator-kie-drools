package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;

/**
 * This represents a test scenario.
 * It also encapsulates the result of a scenario run.
 *
 * @author Michael Neale
 */
public class Scenario implements Serializable {

	public int		maxRuleFirings = 100000;
	public FactData[] facts = new FactData[0];
	public Assertion[] assertions = new Assertion[0];
	public ScenarioRules ruleTrace = new ScenarioRules();


}


