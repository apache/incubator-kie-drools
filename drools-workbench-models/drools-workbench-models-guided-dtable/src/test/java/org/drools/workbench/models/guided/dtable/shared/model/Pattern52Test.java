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

import static org.drools.workbench.models.guided.dtable.shared.model.Pattern52.FIELD_BOUND_NAME;
import static org.drools.workbench.models.guided.dtable.shared.model.Pattern52.FIELD_ENTRY_POINT_NAME;
import static org.drools.workbench.models.guided.dtable.shared.model.Pattern52.FIELD_FACT_TYPE;
import static org.drools.workbench.models.guided.dtable.shared.model.Pattern52.FIELD_IS_NEGATED;
import static org.drools.workbench.models.guided.dtable.shared.model.Pattern52.FIELD_WINDOW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.drools.workbench.models.datamodel.rule.CEPWindow;
import org.junit.Before;
import org.junit.Test;

public class Pattern52Test extends ColumnTestBase {

    private Pattern52 column1;
    private Pattern52 column2;

    @Before
    public void setup() {
        column1 = new Pattern52();
        column1.setFactType("Fact");
        column1.setBoundName("$var");
        column1.setNegated(false);
        column1.setWindow(new CEPWindow());
        column1.setEntryPointName("entryPoint");

        column2 = new Pattern52();
        column2.setFactType("Fact");
        column2.setBoundName("$var");
        column2.setNegated(false);
        column2.setWindow(new CEPWindow());
        column2.setEntryPointName("entryPoint");
    }

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffFactType() {
        column1.setFactType("Fact1");
        column2.setFactType("Fact2");

        checkSingleDiff(FIELD_FACT_TYPE, "Fact1", "Fact2", column1, column2);
    }

    @Test
    public void testDiffBoundName() {
        column1.setBoundName("$var1");
        column2.setBoundName("$var2");

        checkSingleDiff(FIELD_BOUND_NAME, "$var1", "$var2", column1, column2);
    }

    @Test
    public void testDiffNegated() {
        column1.setNegated(false);
        column2.setNegated(true);

        checkSingleDiff(FIELD_IS_NEGATED, false, true, column1, column2);
    }

    @Test
    public void testDiffWindow() {
        CEPWindow window1 = new CEPWindow();
        window1.setOperator("dummyOp1");
        column1.setWindow(window1);

        CEPWindow window2 = new CEPWindow();
        window2.setOperator("dummyOp2");
        column2.setWindow(window2);

        checkSingleDiff(FIELD_WINDOW, window1, window2, column1, column2);
    }

    @Test
    public void testDiffEntryPoint() {
        column1.setEntryPointName("entryPoint1");
        column2.setEntryPointName("entryPoint2");

        checkSingleDiff(FIELD_ENTRY_POINT_NAME, "entryPoint1", "entryPoint2", column1, column2);
    }

    @Test
    public void testDiffAll() {
        column1.setFactType("Fact1");
        column1.setBoundName("$var1");
        column1.setNegated(false);
        CEPWindow window1 = new CEPWindow();
        window1.setOperator("dummyOp1");
        column1.setWindow(window1);
        column1.setEntryPointName("entryPoint1");

        column2.setFactType("Fact2");
        column2.setBoundName("$var2");
        column2.setNegated(true);
        CEPWindow window2 = new CEPWindow();
        window2.setOperator("dummyOp2");
        column2.setWindow(window2);
        column2.setEntryPointName("entryPoint2");

        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        assertNotNull(diff);
        assertEquals(5, diff.size());
        assertEquals(FIELD_FACT_TYPE, diff.get(0).getFieldName());
        assertEquals("Fact1", diff.get(0).getOldValue());
        assertEquals("Fact2", diff.get(0).getValue());
        assertEquals(FIELD_BOUND_NAME, diff.get(1).getFieldName());
        assertEquals("$var1", diff.get(1).getOldValue());
        assertEquals("$var2", diff.get(1).getValue());
        assertEquals(FIELD_IS_NEGATED, diff.get(2).getFieldName());
        assertEquals(false, diff.get(2).getOldValue());
        assertEquals(true, diff.get(2).getValue());
        assertEquals(FIELD_WINDOW, diff.get(3).getFieldName());
        assertEquals(window1, diff.get(3).getOldValue());
        assertEquals(window2, diff.get(3).getValue());
        assertEquals(FIELD_ENTRY_POINT_NAME, diff.get(4).getFieldName());
        assertEquals("entryPoint1", diff.get(4).getOldValue());
        assertEquals("entryPoint2", diff.get(4).getValue());
    }

    @Test
    public void testCloneColumn() {
        Pattern52 clone = column1.clonePattern();

        assertEquals(column1.getFactType(), clone.getFactType());
        assertEquals(column1.getBoundName(), clone.getBoundName());
        assertEquals(column1.getWindow(), clone.getWindow());
        assertEquals(column1.getEntryPointName(), clone.getEntryPointName());
        assertEquals(column1.isNegated(), clone.isNegated());
    }
}