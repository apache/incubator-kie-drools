package org.drools.assistant;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.assistant.engine.DRLParserEngine;
import org.drools.assistant.info.drl.DRLContentTypeEnum;
import org.drools.assistant.info.drl.DRLRuleRefactorInfo;
import org.drools.assistant.info.drl.RuleBasicContentInfo;
import org.drools.assistant.info.drl.RuleLineContentInfo;

public class DRLParserEngineTest extends TestCase {

	private String rule;
	private DRLParserEngine engine;
	private DRLRuleRefactorInfo info;

	@Override
	protected void setUp() throws Exception {
		rule = 	"package org.drools.assistant.test;\n\n" +
		"import org.drools.assistant.test.model.Company;\n" +
		"import org.drools.assistant.test.model.Employee;\n\n" +
		"import function org.drools.assistant.model.Class1.anotherFunction \n" +
		"import		function org.drools.assistant.model.Class1.mathFunction \n" +
		"global     org.drools.assistant.test.model.Class2    results \n"+
		"global org.drools.assistant.test.model.Class3 current\n"+ 
		"expander help-expander.dsl\n" +
		"query \"all clients\"\n" +
		"	result : Clients()\n" +
		"end\n" +
		"query \"new query\"\n" +
		"	objects : Clients()\n" +
		"end\n" +
		"function String hello(String name) {\n"+
		"    return \"Hello \"+name+\"!\";\n"+
		"}\n" +
		"function String helloWithAge(String name, Integer age) {\n"+
		"    return \"Hello2 \"+name+\"! \" + age;\n"+
		"}\n" +
		"\trule   \"My Test Rule\"\n" +
		"when\n"+ 
		"	$employee : Employee($company : company, $age : age > 80, salary > 400)\n" +
		"	$result : Company(company == $company, retireAge <= $age)\n" + 
		"then\n"+ 
		"	System.out.println(\"can retire\")\n" +
		"end\n";

		engine = new DRLParserEngine(rule);

	}

	public void testExecuteEngine() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(123);
		Assert.assertEquals(true, content!=null);
	}

	public void testImport() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(9);
		Assert.assertEquals(true, content!=null);
	}

	public void testNothingInteresting() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(199);
		Assert.assertEquals(true, content==null);
	}

	public void testInsideTheRuleName() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(670);
		Assert.assertEquals(true, content==null);
	}

	public void testInsideLHSRule() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(790);
		Assert.assertEquals(true, content!=null);
	}

	public void testInsideRHSRule() {
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(830);
		Assert.assertEquals(true, content!=null);
	}

	public void testSampleDRL() {
		rule = "package com.sample\n\n" +
		"import com.sample.DroolsTest.Message;\n" +
		"import com.sample.Prueba;\n\n" +
		"\trule \"Hello World\"\n" +
		"\twhen\n" +
		"\t\tm : Message( status == Message.HELLO, myMessage : message )\n" +
		"\t\tPrueba()\n" +
		"\tthen\n" +
		"\t\tSystem.out.println( myMessage );\n" +
		"\t\tm.setMessage( \"Goodbye cruel world\" );\n" +
		"\t\tm.setStatus( Message.GOODBYE );\n" +
		"\t\tupdate( m );\n" +
		"end\n"+
		"rule \"GoodBye World\"\n" +
		"\twhen\n" +
		"\t\tm : Message( status == Message.GOODBYE, myMessage : message )\n" +
		"\t\tPrueba()\n" +
		"\tthen\n" +
		"\t\tSystem.out.println( myMessage );\n" +
		"\t\tm.setMessage( \"Bon Giorno\" );\n" +
		"end";
		engine = new DRLParserEngine(rule);
		info = (DRLRuleRefactorInfo) engine.parse();
		RuleBasicContentInfo content = info.getContentAt(173);

		Assert.assertEquals(true, content!=null);
		Assert.assertEquals(DRLContentTypeEnum.RULE_LHS_LINE, content.getType());
		Assert.assertEquals("rule \"Hello World\"", ((RuleLineContentInfo)content).getRule().getRuleName());
		Assert.assertEquals("\t\tPrueba()", content.getContent());

		content = info.getContentAt(343);
		Assert.assertEquals(true, content!=null);
		Assert.assertEquals(DRLContentTypeEnum.RULE_LHS_LINE, content.getType());
		Assert.assertEquals("rule \"GoodBye World\"", ((RuleLineContentInfo)content).getRule().getRuleName());
		Assert.assertEquals("\twhen", content.getContent());
	}

}
