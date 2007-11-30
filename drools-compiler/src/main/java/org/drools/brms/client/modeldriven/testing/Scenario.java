package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.modeldriven.brl.PortableObject;

/**
 * This represents a test scenario.
 * It also encapsulates the result of a scenario run.
 *
 * @author Michael Neale
 */
public class Scenario implements PortableObject {

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
	public void removeFixture(Fixture f) {
		this.fixtures.remove(f);
		this.globals.remove(f);
	}


	/**
	 *
	 * @return A mapping of variable names to their fact type.
	 */
	public Map getVariableTypes() {
		Map m = new HashMap();
		for (Iterator iterator = fixtures.iterator(); iterator.hasNext();) {
			Fixture f = (Fixture) iterator.next();
			if (f instanceof FactData) {
				FactData fd = (FactData)f;
				m.put(fd.name, fd.type);
			}
		}
		for (Iterator iterator = globals.iterator(); iterator.hasNext();) {
			FactData fd = (FactData) iterator.next();
			m.put(fd.name, fd.type);
		}
		return m;
	}

	/**
	 * This will return a list of fact names that are in scope (including globals).
	 * @return List<String>
	 */
	public List getFactNamesInScope(ExecutionTrace ex, boolean includeGlobals) {
		if (ex == null) return new ArrayList();
		List l = new ArrayList();
		int p = this.fixtures.indexOf(ex);
		for (int i = 0; i < p; i++) {
			Fixture f = (Fixture) fixtures.get(i);
			if (f instanceof FactData) {
				FactData fd = (FactData) f;
				l.add(fd.name);
			} else if (f instanceof RetractFact) {
				RetractFact rf = (RetractFact) f;
				l.remove(rf.name);
			}
		}

		if (includeGlobals) {
			for (Iterator iterator = globals.iterator(); iterator.hasNext();) {
				FactData f = (FactData) iterator.next();
				l.add(f.name);
			}
		}
		return l;
	}

	/**
	 * @return true iff a fact name is already in use.
	 */
	public boolean isFactNameExisting(String factName) {
		for (Iterator iterator = globals.iterator(); iterator.hasNext();) {
			FactData fd = (FactData) iterator.next();
			if (fd.name.equals(factName)) {
				return true;
			}
		}
		for (Iterator iterator = fixtures.iterator(); iterator.hasNext();) {
			Fixture f = (Fixture) iterator.next();
			if (f instanceof FactData) {
				FactData fd = (FactData) f;
				if (fd.name.equals(factName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return true iff a fact is actually used (ie if its not, its safe to remove it).
	 */
	public boolean isFactNameUsed(FactData fd) {
		int start = this.fixtures.indexOf(fd);
		for (int i = start + 1; i < fixtures.size(); i++) {
			Fixture f = (Fixture) fixtures.get(i);
			if (f instanceof RetractFact) {
				 if (((RetractFact)f).name.equals(fd.name)) {
					 return true;
				 }
			} else if (f instanceof VerifyFact) {
				 if (((VerifyFact)f).name.equals(fd.name)) {
					 return true;
				 }
			} else if (f instanceof FactData) {
				 if (((FactData)f).name.equals(fd.name)) {
					 return true;
				 }
			}
		}
		return false;
	}


}


