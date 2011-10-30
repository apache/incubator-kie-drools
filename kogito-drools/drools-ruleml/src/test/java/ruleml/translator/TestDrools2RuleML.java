package ruleml.translator;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;

public class TestDrools2RuleML extends TestCase {

	@Test
	public void test() {
		System.out
				.println("------------------------   Drl -> RuleML : TEST ASSERT ------------------------");
		try {			
			final String ruleBase = Util
					.readFileAsString("src/test/resources/drools/test.drl");
			Drools2RuleMLTranslator drools2RuleMLTranslator = new Drools2RuleMLTranslator();

			Object result = drools2RuleMLTranslator.translate(ruleBase);

			System.out.println(result);

		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		}
	}
}
