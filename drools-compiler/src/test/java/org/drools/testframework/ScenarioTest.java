package org.drools.testframework;

import java.util.Map;

import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

import junit.framework.TestCase;

public class ScenarioTest extends TestCase {

	public void testInsertAfter() {
		Scenario sc = new Scenario();
		VerifyRuleFired vf = new VerifyRuleFired();
		sc.insertAfter(null, vf);
		assertEquals(1, sc.fixtures.size());
		assertEquals(vf, sc.fixtures.get(0));

		VerifyRuleFired vf2 = new VerifyRuleFired();
		sc.fixtures.add(vf2);

		VerifyRuleFired vf3 = new VerifyRuleFired();
		sc.insertAfter(vf, vf3);
		assertEquals(3, sc.fixtures.size());
		assertEquals(vf, sc.fixtures.get(0));
		assertEquals(vf3, sc.fixtures.get(1));
		assertEquals(vf2, sc.fixtures.get(2));

		VerifyRuleFired vf4 = new VerifyRuleFired();
		sc.insertAfter(vf2, vf4);
		assertEquals(4, sc.fixtures.size());
		assertEquals(vf4, sc.fixtures.get(3));
		assertEquals(vf2, sc.fixtures.get(2));

		VerifyRuleFired vf5 = new VerifyRuleFired();
		sc.insertAfter(null, vf5);
		assertEquals(5, sc.fixtures.size());
		assertEquals(vf5, sc.fixtures.get(0));
	}

	public void testRemoveFixture() {
		Scenario sc = new Scenario();

		VerifyRuleFired vf1 = new VerifyRuleFired();
		VerifyRuleFired vf2 = new VerifyRuleFired();
		VerifyRuleFired vf3 = new VerifyRuleFired();

		sc.fixtures.add(vf1);
		sc.fixtures.add(vf2);
		sc.fixtures.add(vf3);

		sc.removeFixture(vf2);
		assertEquals(2, sc.fixtures.size());
		assertEquals(vf1, sc.fixtures.get(0));
		assertEquals(vf3, sc.fixtures.get(1));
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

}
