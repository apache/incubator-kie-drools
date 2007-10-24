package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;
import java.util.Date;

/**
 * This represents a test scenario.
 * It also encapsulates the result of a scenario run.
 *
 * @author Michael Neale
 */
public class Scenario implements Serializable {

	/**
	 * The maximum number of rules to fire so we don't recurse for ever.
	 */
	public int		maxRuleFirings = 100000;

	/**
	 * This is the simulated date - leaving it as null means it will use
	 * the current time.
	 */
	public Date		scenarioSimulatedDate = null;

	/**
	 * the fact data (and globals) to use
	 */
	public FactData[] facts = new FactData[0];

	/**
	 * Result assertions/expectations.
	 */
	public Assertion[] assertions = new Assertion[0];

	/**
	 * The rules to trace in this scenario.
	 */
	public ScenarioRules ruleTrace = new ScenarioRules();

	/**
	 * Returns true if this was a totally successful scenario, based on the results contained.
	 */
	public boolean wasSuccessful() {
		for (int i = 0; i < this.assertions.length; i++) {
			Assertion as = this.assertions[i];
			if (!as.wasSuccessful()) {
				return false;
			}
		}
		return true;
	}


}


