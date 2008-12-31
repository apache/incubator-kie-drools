package org.drools.verifier.components;

import junit.framework.TestCase;

public class LiteralRestrictionTest extends TestCase {

	public void testSetValue() {
		LiteralRestriction booleanRestriction = new LiteralRestriction();
		booleanRestriction.setValue("true");

		assertEquals(Field.FieldType.BOOLEAN, booleanRestriction.getValueType());
		assertEquals(true, booleanRestriction.getBooleanValue());

		LiteralRestriction intRestriction = new LiteralRestriction();
		intRestriction.setValue("1");

		assertEquals(Field.FieldType.INT, intRestriction.getValueType());
		assertEquals(1, intRestriction.getIntValue());

		LiteralRestriction doubleRestriction = new LiteralRestriction();
		doubleRestriction.setValue("1.0");

		assertEquals(Field.FieldType.DOUBLE, doubleRestriction.getValueType());
		assertEquals(1.0, doubleRestriction.getDoubleValue());

		LiteralRestriction dateRestriction = new LiteralRestriction();
		dateRestriction.setValue("11-jan-2008");

		assertEquals(Field.FieldType.DATE, dateRestriction.getValueType());

		LiteralRestriction stringRestriction = new LiteralRestriction();
		stringRestriction.setValue("test test");

		assertEquals(Field.FieldType.STRING, stringRestriction.getValueType());
		assertEquals("test test", stringRestriction.getValueAsString());

		LiteralRestriction nullRestriction = new LiteralRestriction();
		nullRestriction.setValue(null);

		assertEquals(Field.FieldType.UNKNOWN, nullRestriction.getValueType());
		assertEquals(null, nullRestriction.getValueAsString());
		assertEquals(null, nullRestriction.getValueAsObject());
	}
}
