/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
