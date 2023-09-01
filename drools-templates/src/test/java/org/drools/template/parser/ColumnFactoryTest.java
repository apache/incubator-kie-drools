package org.drools.template.parser;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ColumnFactoryTest {

    @Test
    public void testGetColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("column");
        assertThat(column instanceof StringColumn).isTrue();
        assertThat(column.getName()).isEqualTo("column");
    }

    @Test
    public void testGetStringArrayColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("column: String[]");
        assertThat(column instanceof ArrayColumn).isTrue();
        assertThat(column.getName()).isEqualTo("column");
        assertThat(column.getCellType()).isEqualTo("StringCell");
    }

    @Test
    public void testGetLongArrayColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("column: Long[]");
        assertThat(column instanceof ArrayColumn).isTrue();
        assertThat(column.getName()).isEqualTo("column");
        assertThat(column.getCellType()).isEqualTo("LongCell");
    }

    @Test
    public void testGetArrayColumnSimple() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("column[]");
        assertThat(column instanceof ArrayColumn).isTrue();
        assertThat(column.getName()).isEqualTo("column");
        assertThat(column.getCellType()).isEqualTo("StringCell");
    }

    @Test
    public void testGetLongColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("column: Long");
        assertThat(column instanceof LongColumn).isTrue();
        assertThat(column.getName()).isEqualTo("column");
    }

    @Test
    public void testGetDoubleColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("column: Double");
        assertThat(column instanceof DoubleColumn).isTrue();
        assertThat(column.getName()).isEqualTo("column");
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
        assertThat(column instanceof StringColumn).isTrue();
        assertThat(column.getName()).isEqualTo("$column");
        assertThat(column.getCellType()).isEqualTo("StringCell");
    }

    @Test
    public void testGetDollarArrayColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("$column[]");
        assertThat(column instanceof ArrayColumn).isTrue();
        assertThat(column.getName()).isEqualTo("$column");
        assertThat(column.getCellType()).isEqualTo("StringCell");
    }

    @Test
    public void testGetDollarTypedColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("$column: Long");
        assertThat(column instanceof LongColumn).isTrue();
        assertThat(column.getName()).isEqualTo("$column");
        assertThat(column.getCellType()).isEqualTo("LongCell");
    }

    @Test
    public void testGetDollarArrayTypedColumn() {
        ColumnFactory f = new ColumnFactory();
        Column column = f.getColumn("$column: Long[]");
        assertThat(column instanceof ArrayColumn).isTrue();
        assertThat(column.getName()).isEqualTo("$column");
        assertThat(column.getCellType()).isEqualTo("LongCell");
    }

}
