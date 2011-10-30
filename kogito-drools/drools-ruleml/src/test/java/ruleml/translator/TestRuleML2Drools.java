package ruleml.translator;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import reactionruleml.RuleMLType;
import ruleml.translator.TestDataModel.Buy;
import ruleml.translator.TestDataModel.Keep;
import ruleml.translator.Util;
import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator;

//import static org.junit.Assert.fail;

public class TestRuleML2Drools extends TestCase {
	// @Test
	// public void test1() {
	// try {
	// // load up the knowledge base
	// KnowledgeBase kbase = Drools2RuleMLTranslator
	// .readKnowledgeBase("drools/test.drl");
	// StatefulKnowledgeSession ksession = kbase
	// .newStatefulKnowledgeSession();
	// KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
	// .newFileLogger(ksession, "test");
	//
	// Buy buy1 = new Buy("Ti6o", "Dealer", "Objective");
	// Buy buy2 = new Buy("Margo", "Amazon", "USB");
	// Keep keep = new Keep("Ti6o", "Objective");
	//
	// ksession.insert(buy1);
	// ksession.insert(buy2);
	// ksession.insert(keep);
	//
	// System.out.println(ksession.getFactCount());
	//
	// ksession.fireAllRules();
	//
	// System.out.println(ksession.getFactCount());
	//
	// logger.close();
	// } catch (Throwable t) {
	// t.printStackTrace();
	// fail();
	// }
	// }

	@Test
	public void test_assert() {
		System.out
				.println("***********************   RuleML -> Drl: test_assert  **************************");

		try {
			// read the ruleml file
			String input = Util.readFileAsString("src/test/resources/ruleml/test_assert.ruleml");

			RuleML2DroolsTranslator translator = new RuleML2DroolsTranslator();

			String drl = translator.translate(input);
			String expected = "package org.ruleml.translator\n"
					+ "import org.ruleml.translator.TestDataModel.*;\n" + "\n"
					+ "\n" + "rule \"rule1\"\n" + "	when\n"
					+ "		Likes($X:subject,object==\"wine\")\n" + "\n"
					+ "	then\n" + "		insert( new Likes(\"John\",$X));\n" + "\n"
					+ "end\n" + "rule \"rule2\"\n" + "	when\n"
					+ "		eval(true)\n" + "\n" + "	then\n"
					+ "		insert( new Likes(\"Mary\",\"wine\"));\n" + "\n"
					+ "end\n";

			System.out.println(drl);
			assertEquals(expected, drl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test_retract() {
		System.out
				.println("***********************   RuleML -> Drl: test_retract  **************************");

		try {
			// read the ruleml file
			String input = Util.readFileAsString("src/test/resources/ruleml/test_retract.ruleml");

			RuleML2DroolsTranslator translator = new RuleML2DroolsTranslator();

			String drl = translator.translate(input);
			String expected = "package org.ruleml.translator\n"
					+ "import org.ruleml.translator.TestDataModel.*;\n"
					+ "\n"
					+ "\n"
					+ "rule \"rule1\"\n"
					+ "	when\n"
					+ "		Buy(buyer==\"Ti6o\",$Seller:seller,item==\"ThinkPad\")\n"
					+ "		Person(name==\"Ti6o\",$Var1:age)\n"
					+ "		$var: Buy(buyer==\"Ti6o\",seller==$Seller,item==\"ThinkPad\")\n"
					+ "\n"
					+ "	then\n"
					+ "		retract ($var);\n"
					+ "\n"
					+ "end\n"
					+ "rule \"rule2\"\n"
					+ "	when\n"
					+ "		eval(true)\n"
					+ "\n"
					+ "	then\n"
					+ "		insert( new Own(\"Ti6o\",\"laptop\"));\n"
					+ "\n"
					+ "end\n"
					+ "rule \"rule3\"\n"
					+ "	when\n"
					+ "		$var: Buy(buyer==\"Ti6o\",seller==\"Amazon\",item==\"ThinkPad\")\n"
					+ "\n" + "	then\n" + "		retract ($var);\n" + "\n" + "end\n";

			System.out.println(drl);
			assertEquals(expected, drl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
