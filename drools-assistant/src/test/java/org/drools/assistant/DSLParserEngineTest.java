package org.drools.assistant;

import org.drools.assistant.engine.DSLParserEngine;

import junit.framework.TestCase;

public class DSLParserEngineTest extends TestCase {

	DSLParserEngine dslParserEngine;
	private String rule;

	@Override
	protected void setUp() throws Exception {
		rule = "#This is a starter DSL to show off some of the features. Make sure you change it to be what you need !.\n" +
		"[when]There is an Instance with field of \"{value}\"=i: Instance(field==\"{value}\")\n" +
		"[when]Instance is at least {number} and field is \"{value}\"=i: Instance(number > {number}, location==\"{value}\")\n" +
		"[then]Log : \"{message}\"=System.out.println(\"{message}\");\n" +
		"[then]Set field of instance to \"{value}\"=i.setField(\"{value}\");\n" +
		"[then]Create instance : \"{value}\"=insert(new Instance(\"{value}\"));\n" +
		"[when]There is no current Instance with field : \"{value}\"=not Instance(field == \"{value}\")\n" +
		"[then]Report error : \"{error}\"=System.err.println(\"{error}\");\n" +
		"[then]Retract the fact : '{variable}'=retract({variable}); //this would retract bound variable {variable}\n";

		dslParserEngine = new DSLParserEngine(rule);
	}

	public void testExecute() {
//		dslParserEngine.parse();
	}
}
