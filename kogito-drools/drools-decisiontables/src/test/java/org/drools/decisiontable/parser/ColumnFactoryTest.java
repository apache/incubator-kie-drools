package org.drools.decisiontable.parser;

import org.drools.template.parser.ArrayColumn;
import org.drools.template.parser.Column;
import org.drools.template.parser.ColumnFactory;
import org.drools.template.parser.LongColumn;
import org.drools.template.parser.StringColumn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ColumnFactoryTest {

    @Test
    public void testGetColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column");
		assertTrue(column instanceof StringColumn);
		assertEquals("column", column.getName());
	}

    @Test
    public void testGetStringArrayColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column: String[]");
		assertTrue(column instanceof ArrayColumn);
		assertEquals("column", column.getName());
		assertEquals("StringCell", ((ArrayColumn)column).getCellType());
	}
	
    @Test
    public void testGetLongArrayColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column: Long[]");
		assertTrue(column instanceof ArrayColumn);
		assertEquals("column", column.getName());
		assertEquals("LongCell", ((ArrayColumn)column).getCellType());
	}
	
    @Test
    public void testGetArrayColumnSimple() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column[]");
		assertTrue(column instanceof ArrayColumn);
		assertEquals("column", column.getName());
		assertEquals("StringCell", ((ArrayColumn)column).getCellType());
		
	}

    @Test
    public void testGetLongColumn() {
		ColumnFactory f = new ColumnFactory();
		Column column = f.getColumn("column: Long");
		assertTrue(column instanceof LongColumn);
		assertEquals("column", column.getName());
	}

    @Test
    public void testInvalidGetColumn() {
		try {
			ColumnFactory f = new ColumnFactory();
			f.getColumn("column$");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {

		}
	}

}
