package org.drools.rule;

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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import junit.framework.TestCase;

import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.EvaluatorFactory;
import org.drools.examples.waltz.Edge;
import org.drools.examples.waltz.Stage;
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
 * This test demonstrate the following failures:
 * 1. Or constraint to produce matches when Or constraint is specified at the beging. 
 * I suspect it has something to do with regular columns having higher indexes
 * 
 * 2. Resolve an object that has Or condition defined for it in consequence 
 * 
 * 
 * @author Alexander Bagerman
 *
 */
public class OrTest extends TestCase {

	private Evaluator integerEqualEvaluator;
	private Evaluator integerNotEqualEvaluator;

	private ClassObjectType stageType;

	private ClassObjectType edgeType;

	protected Package pkg;

	private Stage markStage;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.stageType = new ClassObjectType(Stage.class);
		this.edgeType = new ClassObjectType(Edge.class);
		this.integerEqualEvaluator = EvaluatorFactory.getInstance()
				.getEvaluator(Evaluator.INTEGER_TYPE, Evaluator.EQUAL);
		this.integerNotEqualEvaluator = EvaluatorFactory.getInstance()
		.getEvaluator(Evaluator.INTEGER_TYPE, Evaluator.NOT_EQUAL);
		this.pkg = new Package("or");
	}

	/*
	 */
	public void testLeapsNeverGetsToConsequenceOrder() throws Exception {

		this.pkg.addRule(this.getNeverGetsToConsequenceRule());

		final org.drools.leaps.RuleBaseImpl ruleBase = new org.drools.leaps.RuleBaseImpl();
        ruleBase.addRuleSet( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new Stage(Stage.LABELING) );
        workingMemory.assertObject( new Edge(1000, 1,false, Edge.NIL, Edge.NIL));
        workingMemory.assertObject( new Edge(1000, 2,false, Edge.NIL, Edge.NIL));
        workingMemory.assertObject( new Edge(5555, 3,false, Edge.NIL, Edge.NIL));
        workingMemory.fireAllRules();
        workingMemory.assertObject( new Stage(Stage.DETECT_JUNCTIONS) );

        workingMemory.fireAllRules();
	}

	/*
	 */
	public void testReteooNeverGetsToConsequenceOrder() throws Exception {

		this.pkg.addRule(this.getNeverGetsToConsequenceRule());

		final org.drools.leaps.RuleBaseImpl ruleBase = new org.drools.leaps.RuleBaseImpl();
        ruleBase.addRuleSet( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new Stage(Stage.LABELING) );
        workingMemory.assertObject( new Edge(1000, 1,false, Edge.NIL, Edge.NIL));
        workingMemory.assertObject( new Edge(1000, 2,false, Edge.NIL, Edge.NIL));
        workingMemory.assertObject( new Edge(5555, 3,false, Edge.NIL, Edge.NIL));
        workingMemory.fireAllRules();
        workingMemory.assertObject( new Stage(Stage.DETECT_JUNCTIONS) );

        workingMemory.fireAllRules();
	}

	/*
	 */
	public void testLeapsWorkingButCanNotResolveOrObjectInConsequenceOrder() throws Exception {

		this.pkg.addRule(this.getWorkingButCanNotResolveOrObjectInConsequenceOrder());

		final org.drools.leaps.RuleBaseImpl ruleBase = new org.drools.leaps.RuleBaseImpl();
        ruleBase.addRuleSet( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new Stage(Stage.LABELING) );
        workingMemory.assertObject( new Edge(1000, 1,false, Edge.NIL, Edge.NIL));
        workingMemory.assertObject( new Edge(1000, 2,false, Edge.NIL, Edge.NIL));
        workingMemory.assertObject( new Edge(5555, 3,false, Edge.NIL, Edge.NIL));
        workingMemory.fireAllRules();
        workingMemory.assertObject( new Stage(Stage.DETECT_JUNCTIONS) );

        workingMemory.fireAllRules();
	}

	/*
	 */
	public void testReteooWorkingButCanNotResolveOrObjectInConsequenceOrder() throws Exception {

		this.pkg.addRule(this.getWorkingButCanNotResolveOrObjectInConsequenceOrder());

		final org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        ruleBase.addRuleSet( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new Stage(Stage.LABELING) );
        workingMemory.assertObject( new Edge(1000, 1,false, Edge.NIL, Edge.NIL));
        workingMemory.assertObject( new Edge(1000, 2,false, Edge.NIL, Edge.NIL));
        workingMemory.assertObject( new Edge(5555, 3,false, Edge.NIL, Edge.NIL));
        workingMemory.fireAllRules();
        workingMemory.assertObject( new Stage(Stage.DETECT_JUNCTIONS) );

        workingMemory.fireAllRules();
	}


	/*
	 * Test method for
	 * 'org.drools.leaps.ColumnConstraints.evaluateAlphas(FactHandleImpl, Token,
	 * WorkingMemoryImpl)'
	 */
	public Rule getNeverGetsToConsequenceRule() throws Exception {
		final Rule rule = new Rule("NeverGetsToConsequence");

		Column stageColumn1 = new Column(0, stageType, "stage1");
		stageColumn1
				.addConstraint(getLiteralConstraint(stageColumn1, "value",
						new Integer(Stage.DETECT_JUNCTIONS),
						this.integerEqualEvaluator));
		final Declaration stage1Declaration = rule.getDeclaration("stage");

		Column stageColumn2 = new Column(1, stageType, "stage");
		stageColumn2.addConstraint(getLiteralConstraint(stageColumn2, "value",
				new Integer(Stage.LABELING), this.integerEqualEvaluator));
		final Declaration stage2Declaration = rule.getDeclaration("stage2");
		Or or = new Or();
		or.addChild(stageColumn1);
		or.addChild(stageColumn2);
		rule.addPattern(or);

		Column edgeColumn1 = new Column(2, edgeType, "edge1");
		setFieldDeclaration(edgeColumn1, "p1", "edge1p1");
		setFieldDeclaration(edgeColumn1, "p2", "edge1p2");
		rule.addPattern(edgeColumn1);
		final Declaration edge1Declaration = rule.getDeclaration("edge1");
		final Declaration edge1P1Declaration = rule.getDeclaration("edge1p1");
		final Declaration edge1P2Declaration = rule.getDeclaration("edge1p2");

		Column edgeColumn2 = new Column(3, edgeType, "edge2");
		rule.addPattern(edgeColumn2);
		final Declaration edge2Declaration = rule.getDeclaration("edge2");
		edgeColumn2.addConstraint(getBoundVariableConstraint(edgeColumn2, "p1",
				edge1P1Declaration, integerEqualEvaluator));
		edgeColumn2.addConstraint(getBoundVariableConstraint(edgeColumn2, "p2",
				edge1P2Declaration, integerNotEqualEvaluator));

		Consequence consequence = new Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) throws ConsequenceException {
				try {
					Rule rule = activation.getRule();
					Tuple tuple = activation.getTuple();
					KnowledgeHelper drools = new DefaultKnowledgeHelper(rule,
							tuple, workingMemory);

					Stage stage = (Stage) drools.get(stage1Declaration);
					if (stage == null) {
						stage = (Stage) drools.get(stage2Declaration);
					}
					markStage = stage;
				} catch (Exception e) {
					e.printStackTrace();
					throw new ConsequenceException(e);
				}
			}

		};
		rule.setConsequence(consequence);

		return rule;
	}

	/*
	 * Test method for
	 * 'org.drools.leaps.ColumnConstraints.evaluateAlphas(FactHandleImpl, Token,
	 * WorkingMemoryImpl)'
	 */
	public Rule getWorkingButCanNotResolveOrObjectInConsequenceOrder() throws Exception {
		final Rule rule = new Rule("WorkingButCanNotResolveOrObjectInConsequence");

		Column edgeColumn1 = new Column(0, edgeType, "edge1");
		setFieldDeclaration(edgeColumn1, "p1", "edge1p1");
		setFieldDeclaration(edgeColumn1, "p2", "edge1p2");
		rule.addPattern(edgeColumn1);
		final Declaration edge1Declaration = rule.getDeclaration("edge1");
		final Declaration edge1P1Declaration = rule.getDeclaration("edge1p1");
		final Declaration edge1P2Declaration = rule.getDeclaration("edge1p2");

		Column edgeColumn2 = new Column(1, edgeType, "edge2");
		rule.addPattern(edgeColumn2);
		final Declaration edge2Declaration = rule.getDeclaration("edge2");
		edgeColumn2.addConstraint(getBoundVariableConstraint(edgeColumn2, "p1",
				edge1P1Declaration, integerEqualEvaluator));
		edgeColumn2.addConstraint(getBoundVariableConstraint(edgeColumn2, "p2",
				edge1P2Declaration, integerNotEqualEvaluator));

		Column stageColumn1 = new Column(2, stageType, "stage1");
		stageColumn1
				.addConstraint(getLiteralConstraint(stageColumn1, "value",
						new Integer(Stage.DETECT_JUNCTIONS),
						this.integerEqualEvaluator));
		final Declaration stage1Declaration = rule.getDeclaration("stage");

		Column stageColumn2 = new Column(3, stageType, "stage");
		stageColumn2.addConstraint(getLiteralConstraint(stageColumn2, "value",
				new Integer(Stage.LABELING), this.integerEqualEvaluator));
		final Declaration stage2Declaration = rule.getDeclaration("stage2");
		Or or = new Or();
		or.addChild(stageColumn1);
		or.addChild(stageColumn2);
		rule.addPattern(or);

		Consequence consequence = new Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) throws ConsequenceException {
				try {
					Rule rule = activation.getRule();
					Tuple tuple = activation.getTuple();
					KnowledgeHelper drools = new DefaultKnowledgeHelper(rule,
							tuple, workingMemory);

					Stage stage = (Stage) drools.get(stage1Declaration);
					if (stage == null) {
						stage = (Stage) drools.get(stage2Declaration);
					}
					markStage = stage;
				} catch (Exception e) {
					e.printStackTrace();
					throw new ConsequenceException(e);
				}
			}

		};
		rule.setConsequence(consequence);

		return rule;
	}

	private void setFieldDeclaration(Column column, String fieldName,
			String identifier) throws IntrospectionException {
		Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

		FieldExtractor extractor = new ClassFieldExtractor(clazz, fieldName);

		column.addDeclaration(identifier, extractor);
	}

	private FieldConstraint getLiteralConstraint(Column column,
			String fieldName, Object fieldValue, Evaluator evaluator)
			throws IntrospectionException {
		Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

		int index = getIndex(clazz, fieldName);

		FieldValue field = new MockField(fieldValue);

		FieldExtractor extractor = new ClassFieldExtractor(clazz, fieldName);

		return new LiteralConstraint(field, extractor, evaluator);
	}

	private FieldConstraint getBoundVariableConstraint(Column column,
			String fieldName, Declaration declaration, Evaluator evaluator)
			throws IntrospectionException {
		Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

		FieldExtractor extractor = new ClassFieldExtractor(clazz, fieldName);

		return new BoundVariableConstraint(extractor, declaration, evaluator);
	}

	public static int getIndex(Class clazz, String name)
			throws IntrospectionException {
		PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz)
				.getPropertyDescriptors();
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

}
