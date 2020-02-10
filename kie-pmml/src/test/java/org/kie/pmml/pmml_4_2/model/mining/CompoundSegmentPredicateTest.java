/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.model.mining;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.dmg.pmml.pmml_4_2.descr.Array;
import org.dmg.pmml.pmml_4_2.descr.CompoundPredicate;
import org.dmg.pmml.pmml_4_2.descr.SimplePredicate;
import org.dmg.pmml.pmml_4_2.descr.SimpleSetPredicate;
import org.junit.Before;
import org.junit.Test;

public class CompoundSegmentPredicateTest {
	private SimplePredicate simplePredicate[];
	private SimpleSetPredicate simpleSetPredicate[];

	@Before
	public void setUp() throws Exception {
		simplePredicate = new SimplePredicate[3];
		simpleSetPredicate = new SimpleSetPredicate[2];
	}
	
	private void setupSimplePredicate(int index, String fieldName, String operator, String value) {
		if (index < 0 || index >= simplePredicate.length) {
			throw new IndexOutOfBoundsException("Invalid index for setting up SimplePredicate");
		}
		simplePredicate[index] = new SimplePredicate();
		simplePredicate[index].setField(fieldName);
		simplePredicate[index].setOperator(operator);
		simplePredicate[index].setValue(value);
	}
	
	private void setupSimpleSetPredicate(int index, String fieldName, String operator, Array values) {
		if (index < 0 || index >= simpleSetPredicate.length) {
			throw new IndexOutOfBoundsException("Invalid index for setting up SimpleSetPredicate");
		}
		simpleSetPredicate[index] = new SimpleSetPredicate();
		simpleSetPredicate[index].setField(fieldName);
		simpleSetPredicate[index].setBooleanOperator(operator);
		simpleSetPredicate[index].setArray(values);
	}
	
	private Array getNewArray(String setType, BigInteger count, Object ...objects) {
		Array result = new Array();
		result.setType(setType);
		result.setN(count);
		StringBuilder valueBldr = new StringBuilder();
		for (Object o : objects) {
			if (setType.equalsIgnoreCase("string")) valueBldr.append("\'");
			valueBldr.append(o.toString());
			if (setType.equalsIgnoreCase("string")) valueBldr.append("\'");
			valueBldr.append(" ");
		}
		result.setContent(valueBldr.toString());
		return result;
	}

	@Test
	public void testSimplePredUsingAnd() {
		CompoundPredicate predicate = new CompoundPredicate();
		predicate.setBooleanOperator("and");
		setupSimplePredicate(0,"TF1",SimpleSegmentPredicate.EQUAL,"ABC");
		setupSimplePredicate(1,"TF2",SimpleSegmentPredicate.GREATER,"4");
		for (int x = 0; x < 2; x++) {
			predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[x]);
		}
		CompoundSegmentPredicate testPredicate = new CompoundSegmentPredicate(predicate);
		String text = testPredicate.getPredicateRule();
		assertNotNull(text);
		assertEquals(text,"((( mTF1 == false ) && ( vTF1 == \"ABC\" )) && (( mTF2 == false ) && ( vTF2 > 4 )))");
	}
	
	@Test
	public void testMixedPredUsingAnd() {
		CompoundPredicate predicate = new CompoundPredicate();
		predicate.setBooleanOperator("and");
		setupSimplePredicate(0,"TF1",SimpleSegmentPredicate.EQUAL,"ABC");
		Array valueSet = getNewArray("int", new BigInteger("3"), 10, 12, 1);
		setupSimpleSetPredicate(0,"TF2","isIn",valueSet);
		
		predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[0]);
		predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simpleSetPredicate[0]);
		CompoundSegmentPredicate testPredicate = new CompoundSegmentPredicate(predicate);
		String text = testPredicate.getPredicateRule();
		assertNotNull(text);
		assertEquals(text,"((( mTF1 == false ) && ( vTF1 == \"ABC\" )) && (( mTF2 == false ) && ( vTF2 in (  10,  12,  1 ) )))");
	}

	@Test
	public void testSimplePredUsingOr() {
		CompoundPredicate predicate = new CompoundPredicate();
		predicate.setBooleanOperator("or");
		setupSimplePredicate(0,"TF1",SimpleSegmentPredicate.LESSER_EQUAL,"0");
		setupSimplePredicate(1,"TF1",SimpleSegmentPredicate.GREATER,"4");
		for (int x = 0; x < 2; x++) {
			predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[x]);
		}
		CompoundSegmentPredicate testPredicate = new CompoundSegmentPredicate(predicate);
		String text = testPredicate.getPredicateRule();
		assertNotNull(text);
		assertEquals(text,"((( mTF1 == false ) && ( vTF1 <= 0 )) || (( mTF1 == false ) && ( vTF1 > 4 )))");
	}

	@Test
	public void testMixedPredUsingOr() {
		CompoundPredicate predicate = new CompoundPredicate();
		predicate.setBooleanOperator("or");
		setupSimplePredicate(0,"TF1",SimpleSegmentPredicate.GREATER,"100");
		Array valueSet = getNewArray("int", new BigInteger("4"), 1, 8, 16, 21);
		setupSimpleSetPredicate(0,"TF2","isNotIn",valueSet);
		predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[0]);
		predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simpleSetPredicate[0]);
		CompoundSegmentPredicate testPredicate = new CompoundSegmentPredicate(predicate);
		String text = testPredicate.getPredicateRule();
		assertNotNull(text);
		assertEquals(text,"((( mTF1 == false ) && ( vTF1 > 100 )) || (( mTF2 == false ) && ( vTF2 not in (  1,  8,  16,  21 ) )))");
	}
	
	@Test
	public void testSimplePredUsingXor() {
		CompoundPredicate predicate = new CompoundPredicate();
		predicate.setBooleanOperator("xor");
		setupSimplePredicate(0,"TF1",SimpleSegmentPredicate.LESSER_EQUAL,"0");
		setupSimplePredicate(1,"TF1",SimpleSegmentPredicate.GREATER,"4");
		for (int x = 0; x < 2; x++) {
			predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[x]);
		}
		CompoundSegmentPredicate testPredicate = new CompoundSegmentPredicate(predicate);
		String text = testPredicate.getPredicateRule();
		assertNotNull(text);
		assertEquals(text,"((( mTF1 == false ) && ( vTF1 <= 0 )) ^ (( mTF1 == false ) && ( vTF1 > 4 )))");
	}
	
	@Test
	public void testCompoundWithCompound() {
		CompoundPredicate outerPredicate = new CompoundPredicate();
		outerPredicate.setBooleanOperator("or");
		setupSimplePredicate(0,"TF1",SimpleSegmentPredicate.LESSER_EQUAL,"150");
		outerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[0]);
		CompoundPredicate innerPredicate = new CompoundPredicate();
		innerPredicate.setBooleanOperator("and");
		setupSimplePredicate(1, "TF1", SimpleSegmentPredicate.GREATER, "150");
		Array valueSet = getNewArray("string", new BigInteger("3"), "red", "white", "blue");
		setupSimpleSetPredicate(0, "TF2", "isIn", valueSet);
		innerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[1]);
		innerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simpleSetPredicate[0]);
		outerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(innerPredicate);
		CompoundSegmentPredicate testPredicate = new CompoundSegmentPredicate(outerPredicate);
		String text = testPredicate.getPredicateRule();
		assertNotNull(text);
		StringBuilder bldr = new StringBuilder("(");
		bldr.append("(( mTF1 == false ) && ( vTF1 <= 150 )) || ((");
		bldr.append("(( mTF1 == false ) && ( vTF1 > 150 )) && ");
		bldr.append("(( mTF2 == false ) && ( vTF2 in (  \"red\" ,  \"white\" ,  \"blue\"  ) )))");
		bldr.append("))");
		assertEquals(bldr.toString(),text);
	}
	
	@Test
	public void testSimplePredWithSurrogate() {
		CompoundPredicate outerPredicate = new CompoundPredicate();
		outerPredicate.setBooleanOperator("surrogate");
		setupSimplePredicate(0,"TF1",SimpleSegmentPredicate.LESSER_EQUAL,"150");
		outerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[0]);
		CompoundPredicate innerPredicate = new CompoundPredicate();
		innerPredicate.setBooleanOperator("and");
		setupSimplePredicate(1, "TF2", SimpleSegmentPredicate.GREATER, "150");
		Array valueSet = getNewArray("string", new BigInteger("3"), "red", "white", "blue");
		setupSimpleSetPredicate(0, "TF3", "isIn", valueSet);
		setupSimplePredicate(2, "TF4", SimpleSegmentPredicate.GREATER_EQUAL,"10");
		innerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[1]);
		innerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simpleSetPredicate[0]);
		outerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(innerPredicate);
		outerPredicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().add(simplePredicate[2]);
		CompoundSegmentPredicate testPredicate = new CompoundSegmentPredicate(outerPredicate);
		String text = testPredicate.getPrimaryPredicateRule();
		assertNotNull(text);
		assertEquals(text,"( mTF1 == false ) && ( vTF1 <= 150 )");
		assertEquals(2,testPredicate.getSubpredicateCount());
		text = testPredicate.getNextPredicateRule(0);
		assertEquals(text,"( (mTF1 == true) && ( ((( mTF2 == false ) && ( vTF2 > 150 )) && (( mTF3 == false ) && ( vTF3 in (  \"red\" ,  \"white\" ,  \"blue\"  ) ))) ) )");
	}
}
