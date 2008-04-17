package org.drools.testframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.RetractFact;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

public class ScenarioTest extends TestCase {

	public void testInsertBetween() {
		Scenario sc = new Scenario();
		VerifyRuleFired vf = new VerifyRuleFired();
		sc.insertBetween(null, vf);
		assertEquals(1, sc.fixtures.size());
		assertEquals(vf, sc.fixtures.get(0));

		VerifyRuleFired vf2 = new VerifyRuleFired();
		sc.fixtures.add(vf2);

		VerifyRuleFired vf3 = new VerifyRuleFired();
		sc.insertBetween(vf, vf3);
		assertEquals(3, sc.fixtures.size());
		assertEquals(vf, sc.fixtures.get(0));
		assertEquals(vf2, sc.fixtures.get(1));
		assertEquals(vf3, sc.fixtures.get(2));

		VerifyRuleFired vf4 = new VerifyRuleFired();
		sc.insertBetween(vf2, vf4);
		assertEquals(4, sc.fixtures.size());
		assertEquals(3, sc.fixtures.indexOf(vf4));
		assertEquals(2, sc.fixtures.indexOf(vf3));
		//assertEquals(vf4, sc.fixtures.get(3));
		assertEquals(1, sc.fixtures.indexOf(vf2));
		//assertEquals(vf2, sc.fixtures.get(2));
		assertEquals(0, sc.fixtures.indexOf(vf));


		VerifyRuleFired vf5 = new VerifyRuleFired();
		sc.insertBetween(null, vf5);
		assertEquals(5, sc.fixtures.size());
		assertEquals(4, sc.fixtures.indexOf(vf5));


		sc = new Scenario();

		sc.fixtures.add(vf);
		ExecutionTrace ex = new ExecutionTrace();
		sc.fixtures.add(ex);
		sc.insertBetween(null, vf2);
		assertEquals(0, sc.fixtures.indexOf(vf));
		assertEquals(1, sc.fixtures.indexOf(vf2));
		assertEquals(2, sc.fixtures.indexOf(ex));
		assertEquals(3, sc.fixtures.size());

		sc.insertBetween(ex, vf3);
		assertEquals(4, sc.fixtures.size());
		assertEquals(0, sc.fixtures.indexOf(vf));
		assertEquals(1, sc.fixtures.indexOf(vf2));
		assertEquals(2, sc.fixtures.indexOf(ex));
		assertEquals(3, sc.fixtures.indexOf(vf3));


		ExecutionTrace ex2 = new ExecutionTrace();
		sc.fixtures.add(ex2);
		sc.insertBetween(ex, vf4);
		assertEquals(6, sc.fixtures.size());
		assertEquals(0, sc.fixtures.indexOf(vf));
		assertEquals(1, sc.fixtures.indexOf(vf2));
		assertEquals(2, sc.fixtures.indexOf(ex));
		assertEquals(3, sc.fixtures.indexOf(vf3));
		assertEquals(4, sc.fixtures.indexOf(vf4));
		assertEquals(5, sc.fixtures.indexOf(ex2));


		sc.insertBetween(ex2, vf5);
		assertEquals(7, sc.fixtures.size());
		assertEquals(0, sc.fixtures.indexOf(vf));
		assertEquals(1, sc.fixtures.indexOf(vf2));
		assertEquals(2, sc.fixtures.indexOf(ex));
		assertEquals(3, sc.fixtures.indexOf(vf3));
		assertEquals(4, sc.fixtures.indexOf(vf4));
		assertEquals(5, sc.fixtures.indexOf(ex2));
		assertEquals(6, sc.fixtures.indexOf(vf5));


		sc = new Scenario();
		sc.fixtures.add(ex);

		sc.insertBetween(null, vf);
		assertEquals(2, sc.fixtures.size());
		assertEquals(0, sc.fixtures.indexOf(vf));
		assertEquals(1, sc.fixtures.indexOf(ex));





	}



	public void testRemoveFixture() {
		Scenario sc = new Scenario();

		VerifyRuleFired vf1 = new VerifyRuleFired();
		VerifyRuleFired vf2 = new VerifyRuleFired();
		VerifyRuleFired vf3 = new VerifyRuleFired();

		FactData fd = new FactData();

		sc.fixtures.add(vf1);
		sc.fixtures.add(vf2);
		sc.fixtures.add(vf3);
		sc.globals.add(fd);


		sc.removeFixture(vf2);
		assertEquals(2, sc.fixtures.size());
		assertEquals(vf1, sc.fixtures.get(0));
		assertEquals(vf3, sc.fixtures.get(1));
		assertEquals(1, sc.globals.size());

		sc.removeFixture(fd);
		assertEquals(0, sc.globals.size());
		assertEquals(2, sc.fixtures.size());




	}

	public void testMapFactTypes() {
		Scenario sc = new Scenario();
		sc.fixtures.add(new FactData("X", "q", null, false));
		sc.globals.add(new FactData("Q", "x", null, false));

		Map r = sc.getVariableTypes();
		assertEquals(2, r.size());

		assertEquals("X", r.get("q"));
		assertEquals("Q", r.get("x"));

	}

	public void testVariablesInScope() {
		Scenario sc = new Scenario();
		sc.globals.add(new FactData("X", "x", new ArrayList(), false));

		sc.fixtures.add(new FactData("Q", "q", new ArrayList(), true));
		sc.fixtures.add(new FactData("Z", "z", new ArrayList(), false));
		ExecutionTrace ex1 = new ExecutionTrace();

		sc.fixtures.add(ex1);
		sc.fixtures.add(new RetractFact("z"));
		sc.fixtures.add(new FactData("Y", "y", new ArrayList(), false));

		ExecutionTrace ex2 = new ExecutionTrace();
		sc.fixtures.add(ex2);

		List l = sc.getFactNamesInScope(ex1, true);

		assertEquals(3, l.size());
		assertEquals("q", l.get(0));
		assertEquals("z", l.get(1));
		assertEquals("x", l.get(2));


		l = sc.getFactNamesInScope(ex1, false);
		assertEquals(2, l.size());
		assertFalse(l.contains(sc.globals.get(0)));

		l = sc.getFactNamesInScope(ex2, true);
		assertEquals(3, l.size());
		assertEquals("q", l.get(0));
		assertEquals("y", l.get(1));
		assertEquals("x", l.get(2));

		l= sc.getFactNamesInScope(null, true);
		assertEquals(0, l.size());

	}

	public void testAllowRemoveFact() {
		Scenario sc = new Scenario();

		FactData fd1 = new FactData("X", "x", new ArrayList(), false);
		sc.fixtures.add(fd1);
		FactData fd2 = new FactData("Q", "q", new ArrayList(), false);
		sc.fixtures.add(fd2);
		FactData fd3 = new FactData("Z", "z", new ArrayList(), false);
		sc.fixtures.add(fd3);
		ExecutionTrace ex1 = new ExecutionTrace();
		FactData fd4 = new FactData("I", "i", new ArrayList(), false);
		sc.globals.add(fd4);

		sc.fixtures.add(ex1);
		sc.fixtures.add(new RetractFact("z"));
		sc.fixtures.add(new FactData("Z", "z", new ArrayList(), true));
		sc.fixtures.add(new VerifyFact("q", new ArrayList()));

		assertFalse(sc.isFactNameUsed(fd1));
		assertTrue(sc.isFactNameUsed(fd2));
		assertTrue(sc.isFactNameUsed(fd3));
		assertFalse(sc.isFactNameUsed(fd4));
	}

	public void testIsFactNameUsed() {
		Scenario sc = new Scenario();
		sc.globals.add(new FactData("X", "x", null, false));
		sc.fixtures.add(new FactData("Q", "q", null, false));
		sc.fixtures.add(new ExecutionTrace());

		assertTrue(sc.isFactNameExisting("x"));
		assertTrue(sc.isFactNameExisting("q"));
		assertFalse(sc.isFactNameExisting("w"));


		sc = new Scenario();
		assertFalse(sc.isFactNameExisting("w"));
	}

	public void testCountSuccessFailures() {
		Scenario sc = new Scenario();
		sc.fixtures.add(new FactData());
		sc.fixtures.add(new ExecutionTrace());
		VerifyRuleFired vr = new VerifyRuleFired();
		vr.successResult = false;
		sc.fixtures.add(vr);

		VerifyField vf = new VerifyField();
		vf.successResult = true;
		VerifyField vf2 = new VerifyField();
		vf2.successResult = false;
		VerifyFact vfact = new VerifyFact();
		vfact.fieldValues.add(vf);
		vfact.fieldValues.add(vf2);
		sc.fixtures.add(vfact);


		int[] totals = sc.countFailuresTotal();
		assertEquals(2, totals[0]);
		assertEquals(3, totals[1]);



	}

}
