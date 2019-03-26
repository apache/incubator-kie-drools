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
import static org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52.FIELD_METADATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MetadataCol52Test extends ColumnTestBase {

    private MetadataCol52 column1;
    private MetadataCol52 column2;

    @Before
    public void setup() {
        column1 = new MetadataCol52();
        column1.setMetadata("meta");
        column1.setHeader("header");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default"));

        column2 = new MetadataCol52();
        column2.setMetadata("meta");
        column2.setHeader("header");
        column2.setHideColumn(false);
        column2.setDefaultValue(new DTCellValue52("default"));
    }

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffMetadata() {
        column1.setMetadata("meta1");
        column2.setMetadata("meta2");

        checkSingleDiff(FIELD_METADATA, "meta1", "meta2", column1, column2);
    }

    @Test
    public void testDiffAll() {
        column1.setMetadata("meta1");
        column1.setHeader("header1");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default1"));
        column2.setMetadata("meta2");
        column2.setHeader("header2");
        column2.setHideColumn(true);
        column2.setDefaultValue(new DTCellValue52("default2"));

        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        assertNotNull(diff);
        assertEquals(4, diff.size());
        assertEquals(FIELD_HIDE_COLUMN, diff.get(0).getFieldName());
        assertEquals(false, diff.get(0).getOldValue());
        assertEquals(true, diff.get(0).getValue());
        assertEquals(FIELD_DEFAULT_VALUE, diff.get(1).getFieldName());
        assertEquals("default1", diff.get(1).getOldValue());
        assertEquals("default2", diff.get(1).getValue());
        assertEquals(FIELD_HEADER, diff.get(2).getFieldName());
        assertEquals("header1", diff.get(2).getOldValue());
        assertEquals("header2", diff.get(2).getValue());
        assertEquals(FIELD_METADATA, diff.get(3).getFieldName());
        assertEquals("meta1", diff.get(3).getOldValue());
        assertEquals("meta2", diff.get(3).getValue());
    }

    @Test
    public void testCloneColumn() {
        column1.setWidth(10);
        MetadataCol52 clone = column1.cloneColumn();

        assertEquals(column1.getMetadata(), clone.getMetadata());
        assertEquals(column1.getHeader(), clone.getHeader());
        assertEquals(column1.getWidth(), clone.getWidth());
        assertEquals(column1.isHideColumn(), clone.isHideColumn());
        assertEquals(column1.getDefaultValue(), clone.getDefaultValue());
    }
}