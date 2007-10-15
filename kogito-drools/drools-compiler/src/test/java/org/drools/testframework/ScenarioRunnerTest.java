package org.drools.testframework;

import java.util.HashSet;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.Scenario;

import junit.framework.TestCase;

public class ScenarioRunnerTest extends TestCase {

	public void testPopulateFacts() throws Exception {
		Scenario sc = new Scenario();
		sc.facts = new FactData[] { new FactData("Cheese", "c1", new FieldData[] {
																			new FieldData("type", "cheddar", false),
																			new FieldData("price", "42", false)
																		}),
									new FactData("Person", "p1", new FieldData[] {
																			new FieldData("name", "mic", false),
																			new FieldData("age", "30 + 3", true)
									})};

		TypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
		resolver.addImport("org.drools.Cheese");
		resolver.addImport("org.drools.Person");
		ScenarioRunner runner = new ScenarioRunner(sc, resolver);

		assertTrue(runner.populatedData.containsKey("c1"));
		assertTrue(runner.populatedData.containsKey("p1"));

		Cheese c = (Cheese) runner.populatedData.get("c1");
		assertEquals("cheddar", c.getType());
		assertEquals(42, c.getPrice());

		Person p = (Person) runner.populatedData.get("p1");
		assertEquals("mic", p.getName());
		assertEquals(33, p.getAge());


	}

}
