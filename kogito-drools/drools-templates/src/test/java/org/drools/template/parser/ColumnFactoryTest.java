package org.drools.template.parser;

import junit.framework.TestCase;

public class ColumnFactoryTest extends TestCase {

	public void testGetColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column");
		assertTrue(column instanceof StringColumn);
		assertEquals("column", column.getName());
	}

	public void testGetStringArrayColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column: String[]");
		assertTrue(column instanceof ArrayColumn);
		assertEquals("column", column.getName());
		assertEquals("StringCell", ((ArrayColumn)column).getCellType());
	}
	
	public void testGetLongArrayColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column: Long[]");
		assertTrue(column instanceof ArrayColumn);
		assertEquals("column", column.getName());
		assertEquals("LongCell", ((ArrayColumn)column).getCellType());
	}
	
	public void testGetArrayColumnSimple() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column[]");
		assertTrue(column instanceof ArrayColumn);
		assertEquals("column", column.getName());
		assertEquals("StringCell", ((ArrayColumn)column).getCellType());
		
	}

	public void testGetLongColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column: Long");
		assertTrue(column instanceof LongColumn);
		assertEquals("column", column.getName());
	}

	public void testInvalidGetColumn() {
		try {
			ColumnFactory f = new ColumnFactory();
			f.getColumn("column$");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {

		}
	}

}
