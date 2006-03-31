package org.drools.decisiontable.model;

/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
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
		Package ruleSet = new Package("myruleset");
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
		assertTrue(drl.indexOf( "package myruleset;" ) > -1);
		assertTrue(drl.indexOf("rule \"other rule\"") > drl.indexOf("rule \"myrule\""));
		
	}

}
