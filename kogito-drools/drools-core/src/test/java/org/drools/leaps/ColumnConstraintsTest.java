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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;

import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.examples.manners.Context;
import org.drools.rule.Column;
import org.drools.rule.LiteralConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.Field;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.MockField;

import junit.framework.TestCase;

public class ColumnConstraintsTest extends TestCase {

	Evaluator integerEqualEvaluator;

	Evaluator integerNotEqualEvaluator;

	protected void setUp() throws Exception {
		super.setUp();
		this.integerEqualEvaluator = EvaluatorFactory.getInstance()
				.getEvaluator(Evaluator.INTEGER_TYPE, Evaluator.EQUAL);
		this.integerNotEqualEvaluator = EvaluatorFactory.getInstance()
				.getEvaluator(Evaluator.INTEGER_TYPE, Evaluator.NOT_EQUAL);
	}

	/*
	 * Test method for
	 * 'org.drools.leaps.ColumnConstraints.evaluateAlphas(FactHandleImpl, Token,
	 * WorkingMemoryImpl)'
	 */
	public void testEvaluateAlphasSuccess() throws Exception {
		RuleBaseImpl base = new RuleBaseImpl();
		ArrayList alphas = new ArrayList();
		ColumnConstraints columnConstraints;
		FieldConstraint constraint;
		ClassObjectType contextType = new ClassObjectType(Context.class);
		Column testColumn = new Column(0, contextType, "state");

		constraint = getLiteralConstraint(testColumn, "state", new Integer(
				Context.START_UP), this.integerEqualEvaluator);
		alphas.add(constraint);
		testColumn.addConstraint(constraint);
		constraint = getLiteralConstraint(testColumn, "state", new Integer(
				-999999), this.integerNotEqualEvaluator);
		alphas.add(constraint);
		testColumn.addConstraint(constraint);

		columnConstraints = new ColumnConstraints(testColumn, alphas, null);

		assertTrue(columnConstraints.evaluateAlphas(new FactHandleImpl(23,
				new Context(Context.START_UP)), null, (WorkingMemoryImpl) base
				.newWorkingMemory()));
	}

	/*
	 * Test method for
	 * 'org.drools.leaps.ColumnConstraints.evaluateAlphas(FactHandleImpl, Token,
	 * WorkingMemoryImpl)'
	 */
	public void testEvaluateAlphasFalure() throws Exception {
		RuleBaseImpl base = new RuleBaseImpl();
		ArrayList alphas = new ArrayList();
		ColumnConstraints columnConstraints;
		FieldConstraint constraint;
		ClassObjectType contextType = new ClassObjectType(Context.class);
		Column testColumn = new Column(0, contextType, "state");

		constraint = getLiteralConstraint(testColumn, "state", new Integer(
				Context.START_UP), this.integerEqualEvaluator);
		alphas.add(constraint);
		testColumn.addConstraint(constraint);
		constraint = getLiteralConstraint(testColumn, "state", new Integer(
				-999999), this.integerEqualEvaluator);
		alphas.add(constraint);
		testColumn.addConstraint(constraint);

		columnConstraints = new ColumnConstraints(testColumn, alphas, null);

		assertFalse(columnConstraints.evaluateAlphas(new FactHandleImpl(23,
				new Context(Context.START_UP)), null, (WorkingMemoryImpl) base
				.newWorkingMemory()));

	}

	private FieldConstraint getLiteralConstraint(Column column,
			String fieldName, Object fieldValue, Evaluator evaluator)
			throws IntrospectionException {
		Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

		int index = getIndex(clazz, fieldName);

		Field field = new MockField(fieldName, fieldValue, index);

		FieldExtractor extractor = new ClassFieldExtractor(clazz, fieldName);

		return new LiteralConstraint(field, extractor, evaluator);
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
