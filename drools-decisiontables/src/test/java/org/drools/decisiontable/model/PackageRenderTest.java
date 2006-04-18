package org.drools.decisiontable.model;
/*
 * Copyright 2005 JBoss Inc
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





import junit.framework.TestCase;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 * Test rendering and running a whole sample ruleset, from the model classes
 * down.
 */
public class PackageRenderTest extends TestCase {

	public Rule buildRule() {
		Rule rule = new Rule("myrule", new Integer(42), 1);
		rule.setComment("rule comments");

		Condition cond = new Condition();
		cond.setComment("cond comment");
		cond.setSnippet("cond snippet");
		rule.addCondition(cond);

		Consequence cons = new Consequence();
		cons.setComment("cons comment");
		cons.setSnippet("cons snippet");
		rule.addConsequence(cons);

		return rule;
	}

	public void testRulesetRender() {
		Package ruleSet = new Package("my ruleset");
		ruleSet.addFunctions("my functions");
		ruleSet.addRule(buildRule());
		
		Rule rule = buildRule();
		rule.setName("other rule");
		ruleSet.addRule(rule);
		
		Import imp = new Import();
		imp.setClassName("clazz name");
		imp.setComment("import comment");
		ruleSet.addImport(imp);
		
		DRLOutput out = new DRLOutput();
		ruleSet.renderDRL(out);
		
		String drl = out.getDRL();
		assertNotNull(drl);
		System.out.println(drl);
		assertTrue(drl.indexOf("rule \"myrule\"") > -1);
		assertTrue(drl.indexOf("salience 42") > -1);
		assertTrue(drl.indexOf("#rule comments") > -1);
		assertTrue(drl.indexOf("my functions") > -1);
		assertTrue(drl.indexOf( "package my_ruleset;" ) > -1);
		assertTrue(drl.indexOf("rule \"other rule\"") > drl.indexOf("rule \"myrule\""));
		
	}

}