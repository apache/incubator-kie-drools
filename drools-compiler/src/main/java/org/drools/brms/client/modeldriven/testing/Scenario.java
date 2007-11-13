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
	 * global data which must be setup before hand.
	 * @gwt.typeArgs <org.drools.brms.client.modeldriven.testing.FactData>
	 */
	public List globals = new ArrayList();

	/**
	 * Fixtures are parts of the test. They may be assertions, globals, data, execution runs etc.
	 * Anything really.
	 *
	 * @gwt.typeArgs <org.drools.brms.client.modeldriven.testing.Fixture>
	 */
	public List fixtures = new ArrayList();

	/**
	 * This is the date the last time the scenario was run (and what the results apply to).
	 */
	public Date lastRunResult;

	/**
	 * the rules to include or exclude
	 * @gwt.typeArgs <java.lang.String>
	 */
	public List rules = new ArrayList();

	/**
	 * true if only the rules in the list should be allowed to fire. Otherwise
	 * it is exclusive (ie all rules can fire BUT the ones in the list).
	 */
	public boolean inclusive = false;



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

	/**
	 * Will slip in a fixture after the specified one.
	 */
	public void insertAfter(Fixture fix, Fixture toAdd) {
		if (fix == null) {
			this.fixtures.add(0, toAdd);
		} else {
			fixtures.add( fixtures.indexOf(fix) + 1, toAdd);
		}
	}

	/**
	 * Remove the specified fixture.
	 */
	public void removeFixture(VerifyRuleFired vf2) {
		this.fixtures.remove(vf2);
	}

}


