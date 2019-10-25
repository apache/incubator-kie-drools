/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.guided.dtable.shared.model;

import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_DEFAULT_VALUE;
import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_HEADER;
import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_HIDE_COLUMN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DTColumnConfig52Test extends ColumnTestBase {

    protected DTColumnConfig52 column1;
    protected DTColumnConfig52 column2;

    @Before
    public void setup() {
        column1 = new DTColumnConfig52();
        column1.setHeader("header");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default"));

        column2 = new DTColumnConfig52();
        column2.setHeader("header");
        column2.setHideColumn(false);
        column2.setDefaultValue(new DTCellValue52("default"));
    }

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffHeader() {
        column1.setHeader("header1");
        column2.setHeader("header2");

        checkSingleDiff(FIELD_HEADER, "header1", "header2", column1, column2);
    }

    @Test
    public void testDiffHide() {
        column1.setHideColumn(false);
        column2.setHideColumn(true);

        checkSingleDiff(FIELD_HIDE_COLUMN, false, true, column1, column2);
    }

    @Test
    public void testDiffDefaultValue() {
        column1.setDefaultValue(new DTCellValue52("default1"));
        column2.setDefaultValue(new DTCellValue52(7));

        checkSingleDiff(FIELD_DEFAULT_VALUE, "default1", 7, column1, column2);
    }

    @Test
    public void testDiffAll() {
        column1.setHeader("header1");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default1"));
        column2.setHeader("header2");
        column2.setHideColumn(true);
        column2.setDefaultValue(new DTCellValue52("default2"));

        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        assertNotNull(diff);
        assertEquals(3, diff.size());
        assertEquals(FIELD_HIDE_COLUMN, diff.get(0).getFieldName());
        assertEquals(false, diff.get(0).getOldValue());
        assertEquals(true, diff.get(0).getValue());
        assertEquals(FIELD_DEFAULT_VALUE, diff.get(1).getFieldName());
        assertEquals("default1", diff.get(1).getOldValue());
        assertEquals("default2", diff.get(1).getValue());
        assertEquals(FIELD_HEADER, diff.get(2).getFieldName());
        assertEquals("header1", diff.get(2).getOldValue());
        assertEquals("header2", diff.get(2).getValue());
    }
}
