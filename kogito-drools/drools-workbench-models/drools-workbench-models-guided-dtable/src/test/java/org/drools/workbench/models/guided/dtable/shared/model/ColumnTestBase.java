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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ColumnTestBase {

    protected void checkSingleDiff(String fieldName, Object oldValue, Object newValue, DTColumnConfig52 column1, DTColumnConfig52 column2) {
        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        assertSingleDiff(fieldName, oldValue, newValue, diff);
    }

    protected void checkDiffEmpty(DTColumnConfig52 column1, DTColumnConfig52 column2) {
        assertSingleDiffEmpty(column1.diff(column2));
    }

    protected void checkSingleDiff(String fieldName, Object oldValue, Object newValue, Pattern52 column1, Pattern52 column2) {
        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        assertSingleDiff(fieldName, oldValue, newValue, diff);
    }

    protected void checkDiffEmpty(Pattern52 column1, Pattern52 column2) {
        assertSingleDiffEmpty(column1.diff(column2));
    }

    private void assertSingleDiff(String fieldName, Object oldValue, Object newValue, List<BaseColumnFieldDiff> diff) {
        assertNotNull(diff);
        assertEquals(1, diff.size());
        assertEquals(fieldName, diff.get(0).getFieldName());
        assertEquals(oldValue, diff.get(0).getOldValue());
        assertEquals(newValue, diff.get(0).getValue());
    }

    private void assertSingleDiffEmpty(List<BaseColumnFieldDiff> diff) {
        assertNotNull(diff);
        assertEquals(0, diff.size());
    }
}
