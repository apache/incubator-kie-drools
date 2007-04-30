package org.drools.decisiontable.parser;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class ArrayColumnTest extends TestCase {

	public void testGetValueSingle() {
		ArrayColumn ac = new ArrayColumn("array column");
		String[] value = (String[]) ac.getValue("value1");
		assertEquals(1, value.length);
		assertEquals("value1", value[0]);
	}
	
	public void testGetValueTrailingComma() {
		ArrayColumn ac = new ArrayColumn("array column");
		String[] value = (String[]) ac.getValue("value1,");
		assertEquals(2, value.length);
		assertEquals("value1", value[0]);
		assertEquals("", value[1]);
	}
	
	public void testGetValueLeadingComma() {
		ArrayColumn ac = new ArrayColumn("array column");
		String[] value = (String[]) ac.getValue(",value2");
		assertEquals(2, value.length);
		assertEquals("value2", value[1]);
	}
	
	public void testGetValueMultiple() {
		ArrayColumn ac = new ArrayColumn("array column");
		String[] value = (String[]) ac.getValue("value1,value2,value3");
		assertEquals(3, value.length);
		assertEquals("value1", value[0]);
		assertEquals("value2", value[1]);
		assertEquals("value3", value[2]);
	}
	
	public void testAddValueSingle() {
		ArrayColumn ac = new ArrayColumn("array");
		String[] value = new String[] { "value" };
        Map vars = new HashMap();
		ac.addValue(vars, value);
		assertEquals("value", vars.get("array0"));
	}

	public void testAddValueMultiple() {
		ArrayColumn ac = new ArrayColumn("array");
		String[] value = new String[] { "value1", "value2" };
        Map vars = new HashMap();
		ac.addValue(vars, value);
		assertEquals("value1", vars.get("array0"));
		assertEquals("value2", vars.get("array1"));
	}
	
}
