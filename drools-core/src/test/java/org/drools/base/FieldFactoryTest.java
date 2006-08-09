package org.drools.base;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.spi.FieldValue;

import junit.framework.TestCase;

public class FieldFactoryTest extends TestCase {

	public void testBigDecimal() {
		FieldValue val = FieldFactory.getFieldValue("42.42", ValueType.BIG_DECIMAL_TYPE);
		assertEquals(BigDecimal.class, val.getValue().getClass());
		assertTrue(val.getValue().equals(new BigDecimal("42.42")));
	}
	
	public void testBigInteger() {
		FieldValue val = FieldFactory.getFieldValue("424242", ValueType.BIG_INTEGER_TYPE);
		assertEquals(BigInteger.class, val.getValue().getClass());
		assertTrue(val.getValue().equals(new BigInteger("424242")));
	}
	
}
