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
import org.dmg.pmml.pmml_4_2.descr.SimpleSetPredicate;
import org.junit.Before;
import org.junit.Test;

public class SimpleSetSegmentPredicateTest {
	private SimpleSetPredicate predicate;
	@Before
	public void setUp() throws Exception {
		predicate = new SimpleSetPredicate();
		predicate.setField("TF1");
		predicate.setBooleanOperator("isIn");
	}
	
	private void doPredicateRetrieve(String expected) {
		SimpleSetSegmentPredicate sssp = new SimpleSetSegmentPredicate(predicate);
		String text = sssp.getPredicateRule();
		assertNotNull(text);
		assertEquals(expected,text);
	}
	

	@Test
	public void testSimpleIntSet() {
		Array array = new Array();
		array.setType("int");
		array.setContent("1 33 45");
		array.setN(new BigInteger("3"));
		predicate.setArray(array);
		String expected = "vTF1 in (  1,  33,  45 )";
		doPredicateRetrieve(expected);
	}
	
	@Test
	public void testSimpleRealSet() {
		Array array = new Array();
		array.setType("real");
		array.setContent("3.14 29.5   17 71");
		array.setN(new BigInteger("4"));
		predicate.setArray(array);
		String expected = "vTF1 in (  3.14,  29.5,  17.0,  71.0 )";
		doPredicateRetrieve(expected);
	}
	
	@Test
	public void testSimpleStringSet() {
		Array array = new Array();
		array.setType("string");
		StringBuilder bldr = new StringBuilder();
		for (int x = 0; x < 4; x++) {
			bldr.append("\"").append("testValue ").append(x).append("\" ");
		}
		array.setContent(bldr.toString());
		array.setN(new BigInteger("4"));
		predicate.setArray(array);
		String expected = "vTF1 in (  \"testValue 0\" ,  \"testValue 1\" ,  \"testValue 2\" ,  \"testValue 3\"  )";
		doPredicateRetrieve(expected);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testInvalidItemCount() {
		Array array = new Array();
		array.setType("int");
		array.setContent("1 33 45");
		array.setN(new BigInteger("2"));
		predicate.setArray(array);
		// Note: Since we expect an exception it doesn't matter
		// what is passed as the expected value
		doPredicateRetrieve(null);
	}
	
	@Test
	public void testNegativeMembership() {
		Array array = new Array();
		array.setType("real");
		array.setContent("3.14 29.5   17 71");
		array.setN(new BigInteger("4"));
		predicate.setArray(array);
		predicate.setBooleanOperator("isNotIn");
		String expected = "vTF1 not in (  3.14,  29.5,  17.0,  71.0 )";
		doPredicateRetrieve(expected);
	}

}
