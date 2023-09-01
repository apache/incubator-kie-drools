package org.drools.decisiontable.parser;

import org.drools.template.parser.ArrayColumn;
import org.drools.template.parser.Column;
import org.drools.template.parser.ColumnFactory;
import org.drools.template.parser.LongColumn;
import org.drools.template.parser.StringColumn;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class ColumnFactoryTest {
    
    private ColumnFactory f;

    @Before
    public void setUp() {
        f = new ColumnFactory();
    }

    @Test
    public void testGetColumn() {
        Column column = f.getColumn("column");

        assertThat(column).isInstanceOf(StringColumn.class);
        assertThat(column.getName()).isEqualTo("column");
    }

    @Test
    public void testGetStringArrayColumn() {
        Column column = f.getColumn("column: String[]");

        assertThat(column).isInstanceOf(ArrayColumn.class);
        assertThat(column.getName()).isEqualTo("column");
        assertThat(column.getCellType()).isEqualTo("StringCell");
    }

    @Test
    public void testGetLongArrayColumn() {
        Column column = f.getColumn("column: Long[]");

        assertThat(column).isInstanceOf(ArrayColumn.class);
        assertThat(column.getName()).isEqualTo("column");
        assertThat(column.getCellType()).isEqualTo("LongCell");
    }

    @Test
    public void testGetArrayColumnSimple() {
        Column column = f.getColumn("column[]");

        assertThat(column).isInstanceOf(ArrayColumn.class);
        assertThat(column.getName()).isEqualTo("column");
        assertThat(column.getCellType()).isEqualTo("StringCell");

    }

    @Test
    public void testGetLongColumn() {
        Column column = f.getColumn("column: Long");

        assertThat(column).isInstanceOf(LongColumn.class);
        assertThat(column.getName()).isEqualTo("column");
    }

    @Test
    public void testInvalidGetColumn() {
        assertThatIllegalArgumentException().isThrownBy(() ->  f.getColumn("column$"));
    }

}
