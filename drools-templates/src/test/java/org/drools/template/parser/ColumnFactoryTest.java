/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
