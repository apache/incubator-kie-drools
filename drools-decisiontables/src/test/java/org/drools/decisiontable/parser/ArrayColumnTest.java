package org.drools.decisiontable.parser;

import org.antlr.stringtemplate.StringTemplate;

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
		assertEquals(1, value.length);
		assertEquals("value1", value[0]);
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
		StringTemplate t = new StringTemplate();
		ac.addValue(t, value);
		assertEquals("value", t.getAttribute("array0"));
	}

	public void testAddValueMultiple() {
		ArrayColumn ac = new ArrayColumn("array");
		String[] value = new String[] { "value1", "value2" };
		StringTemplate t = new StringTemplate();
		ac.addValue(t, value);
		assertEquals("value1", t.getAttribute("array0"));
		assertEquals("value2", t.getAttribute("array1"));
	}
	
}
