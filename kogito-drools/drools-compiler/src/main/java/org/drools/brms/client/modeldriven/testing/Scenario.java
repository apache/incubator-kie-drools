package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
	 * Fixtures are parts of the test. They may be assertions, globals, data, execution runs etc.
	 * Anything really.
	 */
	public List fixtures = new ArrayList();

	/**
	 * This is the date the last time the scenario was run (and what the results apply to).
	 */
	public Date lastRunResult;


	/**
	 * Returns true if this was a totally successful scenario, based on the results contained.
	 */
	public boolean wasSuccessful() {
		for (Iterator iterator = fixtures.iterator(); iterator.hasNext();) {
			Fixture f= (Fixture) iterator.next();
			if (f instanceof Expectation) {
				if (! ((Expectation) f ).wasSuccessful()) {
					return false;
				}
			}

		}
		return true;
	}

}


