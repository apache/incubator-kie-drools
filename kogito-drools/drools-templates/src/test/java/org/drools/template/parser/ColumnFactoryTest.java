package org.drools.template.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

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

    @Test
    public void testGetDollarColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("$column");
        assertTrue(column instanceof StringColumn);
        assertEquals("$column", column.getName());
        assertEquals("StringCell", column.getCellType());
    }

    @Test
    public void testGetDollarArrayColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("$column[]");
        assertTrue(column instanceof ArrayColumn);
        assertEquals("$column", column.getName());
        assertEquals("StringCell", ((ArrayColumn)column).getCellType());
    }

    @Test
    public void testGetDollarTypedColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("$column: Long");
        assertTrue(column instanceof LongColumn);
        assertEquals("$column", column.getName());
        assertEquals("LongCell", column.getCellType());
    }

    @Test
    public void testGetDollarArrayTypedColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("$column: Long[]");
        assertTrue(column instanceof ArrayColumn);
        assertEquals("$column", column.getName());
        assertEquals("LongCell", ((ArrayColumn)column).getCellType());
    }

}
