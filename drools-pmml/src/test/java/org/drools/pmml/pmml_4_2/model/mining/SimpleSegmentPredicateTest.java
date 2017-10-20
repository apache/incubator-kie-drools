package org.drools.pmml.pmml_4_2.model.mining;

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
		System.out.println(predicate.getOperator()+" gives "+text);
	}
	
	@Test
	public void testNotEquals() {
		predicate.setOperator(SimpleSegmentPredicate.NOT_EQUAL);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 != 123",text);
		System.out.println(predicate.getOperator()+" gives "+text);
	}
	
	@Test
	public void testGreaterThan() {
		predicate.setOperator(SimpleSegmentPredicate.GREATER);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 > 123",text);
		System.out.println(predicate.getOperator()+" gives "+text);
	}
	
	@Test
	public void testLessThan() {
		predicate.setOperator(SimpleSegmentPredicate.LESSER);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 < 123",text);
		System.out.println(predicate.getOperator()+" gives "+text);
	}
	
	@Test
	public void testMissing() {
		predicate.setOperator(SimpleSegmentPredicate.MISSING);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("mTF2 == true",text);
		System.out.println(predicate.getOperator()+" gives "+text);
	}
	
	@Test
	public void testGreaterEqual() {
		predicate.setOperator(SimpleSegmentPredicate.GREATER_EQUAL);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 >= 123",text);
		System.out.println(predicate.getOperator()+" gives "+text);
	}
	
	@Test
	public void testLesserEqual() {
		predicate.setOperator(SimpleSegmentPredicate.LESSER_EQUAL);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("vTF2 <= 123",text);
		System.out.println(predicate.getOperator()+" gives "+text);
	}
	
	@Test
	public void testNotMissing() {
		predicate.setOperator(SimpleSegmentPredicate.NOT_MISSING);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNotNull(text);
		assertEquals("mTF2 == false",text);
		System.out.println(predicate.getOperator()+" gives "+text);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testBadOperator() {
		predicate.setOperator(BAD_OP);
		SimpleSegmentPredicate ssp = new SimpleSegmentPredicate(predicate);
		String text = ssp.getPredicateRule();
		assertNull(text);
	}

}
