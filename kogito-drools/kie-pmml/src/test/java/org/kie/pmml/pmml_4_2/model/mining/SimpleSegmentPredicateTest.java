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

import org.dmg.pmml.pmml_4_2.descr.SimplePredicate;
import org.junit.Before;
import org.junit.Test;

public class SimpleSegmentPredicateTest {
	private SimplePredicate predicate;
	private static final String BAD_OP = "invalidOp";
	
	@Before
	public void setUp() throws Exception {
		predicate = new SimplePredicate();
		predicate.setField("TF2");
		predicate.setValue("123");
	}
	
	

	@Test
	public void testEquals() {
		predicate.setOperator(SimpleSegmentPredicate.EQUAL);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 == 123",text);
	}
	
	@Test
	public void testNotEquals() {
		predicate.setOperator(SimpleSegmentPredicate.NOT_EQUAL);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 != 123",text);
	}
	
	@Test
	public void testGreaterThan() {
		predicate.setOperator(SimpleSegmentPredicate.GREATER);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 > 123",text);
	}
	
	@Test
	public void testLessThan() {
		predicate.setOperator(SimpleSegmentPredicate.LESSER);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 < 123",text);
	}
	
	@Test
	public void testMissing() {
		predicate.setOperator(SimpleSegmentPredicate.MISSING);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("mTF2 == true",text);
	}
	
	@Test
	public void testGreaterEqual() {
		predicate.setOperator(SimpleSegmentPredicate.GREATER_EQUAL);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 >= 123",text);
	}
	
	@Test
	public void testLesserEqual() {
		predicate.setOperator(SimpleSegmentPredicate.LESSER_EQUAL);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 <= 123",text);
	}
	
	@Test
	public void testNotMissing() {
		predicate.setOperator(SimpleSegmentPredicate.NOT_MISSING);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("mTF2 == false",text);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testBadOperator() {
		predicate.setOperator(BAD_OP);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNull(text);
	}

}
