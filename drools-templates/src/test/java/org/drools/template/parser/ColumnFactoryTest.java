/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.template.parser;

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
        assertEquals("StringCell", ((ArrayColumn) column).getCellType());
    }

    @Test
    public void testGetLongArrayColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("column: Long[]");
        assertTrue(column instanceof ArrayColumn);
        assertEquals("column", column.getName());
        assertEquals("LongCell", ((ArrayColumn) column).getCellType());
    }

    @Test
    public void testGetArrayColumnSimple() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("column[]");
        assertTrue(column instanceof ArrayColumn);
        assertEquals("column", column.getName());
        assertEquals("StringCell", ((ArrayColumn) column).getCellType());
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
        assertEquals("StringCell", ((ArrayColumn) column).getCellType());
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
        assertEquals("LongCell", ((ArrayColumn) column).getCellType());
    }

}
