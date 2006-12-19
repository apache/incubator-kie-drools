package org.drools.decisiontable.parser;

import junit.framework.TestCase;

public class ColumnFactoryTest extends TestCase {

	public void testGetColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column");
		assertTrue(!(column instanceof ArrayColumn));
		assertEquals("column", column.getName());
	}
	
	public void testGetArrayColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column[]");
		assertTrue(column instanceof ArrayColumn);
		assertEquals("column", column.getName());
	}

}
