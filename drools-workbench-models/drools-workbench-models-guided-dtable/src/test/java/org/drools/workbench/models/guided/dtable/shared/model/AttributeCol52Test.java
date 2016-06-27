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

import static org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52.FIELD_ATTRIBUTE;
import static org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52.FIELD_REVERSE_ORDER;
import static org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52.FIELD_USE_ROW_NUMBER;
import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_DEFAULT_VALUE;
import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_HEADER;
import static org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52.FIELD_HIDE_COLUMN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class AttributeCol52Test extends ColumnTestBase {

    private AttributeCol52 column1;
    private AttributeCol52 column2;

    @Before
    public void setup() {
        column1 = new AttributeCol52();
        column1.setAttribute("attr");
        column1.setReverseOrder(false);
        column1.setUseRowNumber(false);
        column1.setHeader("header");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default"));

        column2 = new AttributeCol52();
        column2.setAttribute("attr");
        column2.setReverseOrder(false);
        column2.setUseRowNumber(false);
        column2.setHeader("header");
        column2.setHideColumn(false);
        column2.setDefaultValue(new DTCellValue52("default"));
    }

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffAttribute() {
        column1.setAttribute("attr1");
        column2.setAttribute("attr2");

        checkSingleDiff(FIELD_ATTRIBUTE, "attr1", "attr2", column1, column2);
    }

    @Test
    public void testDiffRevesreOrder() {
        column1.setReverseOrder(false);
        column2.setReverseOrder(true);

        checkSingleDiff(FIELD_REVERSE_ORDER, false, true, column1, column2);
    }

    @Test
    public void testDiffUseRowNumber() {
        column1.setUseRowNumber(false);
        column2.setUseRowNumber(true);

        checkSingleDiff(FIELD_USE_ROW_NUMBER, false, true, column1, column2);
    }

    @Test
    public void testDiffAll() {
        column1.setAttribute("attr1");
        column1.setReverseOrder(false);
        column1.setUseRowNumber(false);
        column1.setHeader("header1");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default1"));
        column2.setAttribute("attr2");
        column2.setReverseOrder(true);
        column2.setUseRowNumber(true);
        column2.setHeader("header2");
        column2.setHideColumn(true);
        column2.setDefaultValue(new DTCellValue52("default2"));

        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        assertNotNull(diff);
        assertEquals(6, diff.size());
        assertEquals(FIELD_HIDE_COLUMN, diff.get(0).getFieldName());
        assertEquals(false, diff.get(0).getOldValue());
        assertEquals(true, diff.get(0).getValue());
        assertEquals(FIELD_DEFAULT_VALUE, diff.get(1).getFieldName());
        assertEquals("default1", diff.get(1).getOldValue());
        assertEquals("default2", diff.get(1).getValue());
        assertEquals(FIELD_HEADER, diff.get(2).getFieldName());
        assertEquals("header1", diff.get(2).getOldValue());
        assertEquals("header2", diff.get(2).getValue());
        assertEquals(FIELD_ATTRIBUTE, diff.get(3).getFieldName());
        assertEquals("attr1", diff.get(3).getOldValue());
        assertEquals("attr2", diff.get(3).getValue());
        assertEquals(FIELD_REVERSE_ORDER, diff.get(4).getFieldName());
        assertEquals(false, diff.get(4).getOldValue());
        assertEquals(true, diff.get(4).getValue());
        assertEquals(FIELD_USE_ROW_NUMBER, diff.get(5).getFieldName());
        assertEquals(false, diff.get(5).getOldValue());
        assertEquals(true, diff.get(5).getValue());
    }

    @Test
    public void testCloneColumn() {
        column1.setWidth(10);
        AttributeCol52 clone = column1.cloneColumn();

        assertEquals(column1.getAttribute(), clone.getAttribute());
        assertEquals(column1.isReverseOrder(), clone.isReverseOrder());
        assertEquals(column1.isUseRowNumber(), clone.isUseRowNumber());
        assertEquals(column1.getHeader(), clone.getHeader());
        assertEquals(column1.getWidth(), clone.getWidth());
        assertEquals(column1.isHideColumn(), clone.isHideColumn());
        assertEquals(column1.getDefaultValue(), clone.getDefaultValue());
    }
}
