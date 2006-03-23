package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import java.util.ArrayList;

import org.drools.DroolsTestCase;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.EvaluatorFactory;
import org.drools.examples.manners.Context;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.MockField;
import org.drools.spi.Tuple;

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class RuleBaseImplTest extends DroolsTestCase {
	RuleBaseImpl ruleBase;
	RuleBaseImpl ruleBaseAddRule;

	WorkingMemory wm1;

	WorkingMemory wm2;

	WorkingMemory wm3;

	WorkingMemory wm4;

	// leaps add rule objects
	final String handle1Rule1 = "11";

	final String handle1Rule2 = "12";

	final String handle2Rule1 = "21";

	final String handle2Rule2 = "22";

	final ArrayList handlesForRules = new ArrayList();

	WorkingMemory workingMemory;

	Rule rule1;

	Rule rule2;

	final Context context1 = new Context(1);

	final Context context2 = new Context(1);

	public void setUp() throws Exception {
		this.ruleBase = new RuleBaseImpl();

		this.wm1 = this.ruleBase.newWorkingMemory();
		this.wm2 = this.ruleBase.newWorkingMemory();
		this.wm3 = this.ruleBase.newWorkingMemory();
		this.wm4 = this.ruleBase.newWorkingMemory();
		// add rules section
		this.ruleBaseAddRule = new RuleBaseImpl();

		this.workingMemory = this.ruleBaseAddRule.newWorkingMemory();
		// rules
		ClassObjectType contextType = new ClassObjectType(Context.class);
		Evaluator integerEqualEvaluator = EvaluatorFactory
				.getEvaluator(Evaluator.INTEGER_TYPE, Evaluator.EQUAL);
		// rule 1
		// fires on context.state == integer(1)
		this.rule1 = new Rule("rule1");
		Column contextColumnRule1 = new Column(0, contextType, "context1");
		contextColumnRule1.addConstraint(getLiteralConstraint(
				contextColumnRule1, "state", new Integer(1),
				integerEqualEvaluator));
		this.rule1.addPattern(contextColumnRule1);
		final Declaration contextRule1Declaration = this.rule1
				.getDeclaration("context1");
		this.rule1.setConsequence(new Consequence() {
			public void evaluate(Activation activation,
					WorkingMemory workingMemory) throws ConsequenceException {
				try {
					Rule rule = activation.getRule();
					Tuple tuple = activation.getTuple();
					KnowledgeHelper drools = new DefaultKnowledgeHelper(rule,
							tuple, workingMemory);

					Context dummy = (Context) drools
							.get(contextRule1Declaration);
					if (dummy == RuleBaseImplTest.this.context1) {
						RuleBaseImplTest.this.handlesForRules
								.add(RuleBaseImplTest.this.handle1Rule1);
					} else if (dummy == RuleBaseImplTest.this.context2) {
						RuleBaseImplTest.this.handlesForRules
								.add(RuleBaseImplTest.this.handle2Rule1);
					}

				} catch (Exception e) {
					throw new ConsequenceException(e);
				}
			}

		});
		this.rule2 = new Rule("rule2");
		Column contextColumnRule2 = new Column(0, contextType, "context2");
		contextColumnRule2.addConstraint(getLiteralConstraint(
				contextColumnRule2, "state", new Integer(1),
				integerEqualEvaluator));
		this.rule2.addPattern(contextColumnRule2);
		final Declaration contextRule2Declaration = rule2
				.getDeclaration("context2");
		this.rule2.setConsequence(new Consequence() {
			public void evaluate(Activation activation,
					WorkingMemory workingMemory) throws ConsequenceException {
				try {
					Rule rule = activation.getRule();
					Tuple tuple = activation.getTuple();
					KnowledgeHelper drools = new DefaultKnowledgeHelper(rule,
							tuple, workingMemory);

					Context dummy = (Context) drools
							.get(contextRule2Declaration);
					if (dummy == RuleBaseImplTest.this.context1) {
						RuleBaseImplTest.this.handlesForRules
								.add(RuleBaseImplTest.this.handle1Rule2);
					} else if (dummy == RuleBaseImplTest.this.context2) {
						RuleBaseImplTest.this.handlesForRules
								.add(RuleBaseImplTest.this.handle2Rule2);
					}

				} catch (Exception e) {
					throw new ConsequenceException(e);
				}
			}

		});
	}

	public void testKeepReference() throws Exception {
		/* Make sure the RuleBase is referencing all 4 Working Memories */
		assertLength(4, this.ruleBase.getWorkingMemories());
		assertTrue(this.ruleBase.getWorkingMemories().contains(this.wm1));
		assertTrue(this.ruleBase.getWorkingMemories().contains(this.wm2));
		assertTrue(this.ruleBase.getWorkingMemories().contains(this.wm3));
		assertTrue(this.ruleBase.getWorkingMemories().contains(this.wm4));
	}

	public void testWeakReference() throws Exception {
		/* nulling these two so the keys should get garbage collected */
		this.wm2 = null;
		this.wm4 = null;

		/* Run GC */
		System.gc();
		Thread.sleep(200); // Shouldn't need to sleep, but put it in anyway

		/* Check we now only have two keys */
		assertLength(2, this.ruleBase.getWorkingMemories());

		/* Make sure the correct keys were valid */
		assertTrue(this.ruleBase.getWorkingMemories().contains(this.wm1));
		assertFalse(this.ruleBase.getWorkingMemories().contains(this.wm2));
		assertTrue(this.ruleBase.getWorkingMemories().contains(this.wm3));
		assertFalse(this.ruleBase.getWorkingMemories().contains(this.wm4));

	}

	public void testDispose() throws Exception {
		/*
		 * Now lets test the dispose method on the WorkingMemory itself. dispose
		 * doesn't need GC
		 */
		this.wm3.dispose();

		/* Check only wm3 was valid */
		assertLength(3, this.ruleBase.getWorkingMemories());
		assertFalse(this.ruleBase.getWorkingMemories().contains(this.wm3));
	}

	public void testNoKeepReference() throws Exception {
		WorkingMemory wm5 = this.ruleBase.newWorkingMemory(false);
		WorkingMemory wm6 = this.ruleBase.newWorkingMemory(false);
		assertLength(4, this.ruleBase.getWorkingMemories());
		assertFalse(this.ruleBase.getWorkingMemories().contains(wm5));
		assertFalse(this.ruleBase.getWorkingMemories().contains(wm6));
	}

	public void testAddRuleBeforeFacts() throws Exception {

		assertEquals(0, this.handlesForRules.size());

		this.ruleBaseAddRule.addRule(this.rule1);
		this.ruleBaseAddRule.addRule(this.rule2);
		this.workingMemory.assertObject(this.context1);
		this.workingMemory.assertObject(this.context2);
		// firing
		this.workingMemory.fireAllRules();
		// finally everything should be filled
		assertEquals(4, this.handlesForRules.size());
		assertTrue(this.handlesForRules.contains(this.handle1Rule1));
		assertTrue(this.handlesForRules.contains(this.handle2Rule1));
		assertTrue(this.handlesForRules.contains(this.handle1Rule2));
		assertTrue(this.handlesForRules.contains(this.handle2Rule2));
	}

	public void testAddRuleMixedWithFacts() throws Exception {

		assertEquals(0, this.handlesForRules.size());

		this.ruleBaseAddRule.addRule(this.rule1);
		this.workingMemory.assertObject(this.context1);
		this.ruleBaseAddRule.addRule(this.rule2);
		this.workingMemory.assertObject(this.context2);
		// firing
		this.workingMemory.fireAllRules();
		// finally everything should be filled
		assertEquals(4, this.handlesForRules.size());
		assertTrue(this.handlesForRules.contains(this.handle1Rule1));
		assertTrue(this.handlesForRules.contains(this.handle2Rule1));
		assertTrue(this.handlesForRules.contains(this.handle1Rule2));
		assertTrue(this.handlesForRules.contains(this.handle2Rule2));
	}

	public void testAddRuleAfterFacts() throws Exception {

		assertEquals(0, this.handlesForRules.size());
		this.workingMemory.assertObject(this.context1);
		this.workingMemory.assertObject(this.context2);
		this.ruleBaseAddRule.addRule(this.rule1);
		this.ruleBaseAddRule.addRule(this.rule2);
		// firing
		this.workingMemory.fireAllRules();
		// finally everything should be filled
		assertEquals(4, this.handlesForRules.size());
		assertTrue(this.handlesForRules.contains(this.handle1Rule1));
		assertTrue(this.handlesForRules.contains(this.handle2Rule1));
		assertTrue(this.handlesForRules.contains(this.handle1Rule2));
		assertTrue(this.handlesForRules.contains(this.handle2Rule2));
	}


	public void testAddRuleBeforeFactsFiring() throws Exception {

		assertEquals(0, this.handlesForRules.size());

		this.ruleBaseAddRule.addRule(this.rule1);
		// firing
		this.workingMemory.fireAllRules();
		this.ruleBaseAddRule.addRule(this.rule2);
		// firing
		this.workingMemory.fireAllRules();
		this.workingMemory.assertObject(this.context1);
		// firing
		this.workingMemory.fireAllRules();
		this.workingMemory.assertObject(this.context2);
		// firing
		this.workingMemory.fireAllRules();
		// finally everything should be filled
		assertEquals(4, this.handlesForRules.size());
		assertTrue(this.handlesForRules.contains(this.handle1Rule1));
		assertTrue(this.handlesForRules.contains(this.handle2Rule1));
		assertTrue(this.handlesForRules.contains(this.handle1Rule2));
		assertTrue(this.handlesForRules.contains(this.handle2Rule2));
	}

	public void testAddRuleMixedWithFactsFiring1() throws Exception {

		assertEquals(0, this.handlesForRules.size());

		this.ruleBaseAddRule.addRule(this.rule1);
		// firing
		this.workingMemory.fireAllRules();
		this.workingMemory.assertObject(this.context1);
		// firing
		this.workingMemory.fireAllRules();
		this.ruleBaseAddRule.addRule(this.rule2);
		// firing
		this.workingMemory.fireAllRules();
		this.workingMemory.assertObject(this.context2);
		// firing
		this.workingMemory.fireAllRules();
		// finally everything should be filled
		assertEquals(4, this.handlesForRules.size());
		assertTrue(this.handlesForRules.contains(this.handle1Rule1));
		assertTrue(this.handlesForRules.contains(this.handle2Rule1));
		assertTrue(this.handlesForRules.contains(this.handle1Rule2));
		assertTrue(this.handlesForRules.contains(this.handle2Rule2));
	}

	public void testAddRuleMixedWithFactsFiring2() throws Exception {

		assertEquals(0, this.handlesForRules.size());

		this.ruleBaseAddRule.addRule(this.rule2);
		// firing
		this.workingMemory.fireAllRules();
		this.workingMemory.assertObject(this.context1);
		// firing
		this.workingMemory.fireAllRules();
		// firing
		this.workingMemory.fireAllRules();
		this.workingMemory.assertObject(this.context2);
		this.ruleBaseAddRule.addRule(this.rule1);
		// firing
		this.workingMemory.fireAllRules();
		// finally everything should be filled
		assertEquals(4, this.handlesForRules.size());
		assertTrue(this.handlesForRules.contains(this.handle1Rule1));
		assertTrue(this.handlesForRules.contains(this.handle2Rule1));
		assertTrue(this.handlesForRules.contains(this.handle1Rule2));
		assertTrue(this.handlesForRules.contains(this.handle2Rule2));
	}

	public void testAddRuleAfterFactsFiring() throws Exception {

		assertEquals(0, this.handlesForRules.size());
		this.workingMemory.assertObject(this.context1);
		// firing
		this.workingMemory.fireAllRules();
		this.workingMemory.assertObject(this.context2);
		// firing
		this.workingMemory.fireAllRules();
		this.ruleBaseAddRule.addRule(this.rule1);
		// firing
		this.workingMemory.fireAllRules();
		this.ruleBaseAddRule.addRule(this.rule2);
		// firing
		this.workingMemory.fireAllRules();
		// finally everything should be filled
		assertEquals(4, this.handlesForRules.size());
		assertTrue(this.handlesForRules.contains(this.handle1Rule1));
		assertTrue(this.handlesForRules.contains(this.handle2Rule1));
		assertTrue(this.handlesForRules.contains(this.handle1Rule2));
		assertTrue(this.handlesForRules.contains(this.handle2Rule2));
	}

	private FieldConstraint getLiteralConstraint(Column column,
			String fieldName, Object fieldValue, Evaluator evaluator) {
		Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

		FieldExtractor extractor = new ClassFieldExtractor(clazz, fieldName);

		FieldValue field = new MockField(fieldValue);

		return new LiteralConstraint(field, extractor, evaluator);
	}
}
