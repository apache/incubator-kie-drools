/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.hitpolicy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RowPriorityResolverTest {

    private RowPriorityResolver rowPriorityResolver;
    private RowPriorities priorities;

    @Before
    public void setUp() throws
            Exception {
        rowPriorityResolver = new RowPriorityResolver();
    }

    @After
    public void tearDown() throws
            Exception {
        rowPriorityResolver = null;
        priorities = null;
    }

    @Test
    public void noPrioritiesSet() throws
            Exception {

        // Note that the row numbers are mixed here on purpose.
        // Normally the UI keeps them at the correct order, but better to be safe and test the unexpected.
        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(3,
                                0);
        rowPriorityResolver.set(2,
                                0);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(2,
                       new RowNumber(1));
        assertSalience(1,
                       new RowNumber(2));
        assertSalience(0,
                       new RowNumber(3));
    }

    @Test
    public void simple2Row() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(2,
                                1);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(0,
                       new RowNumber(1));
        assertSalience(1,
                       new RowNumber(2));
    }

    @Test
    public void simple3Row() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(2,
                                0);
        rowPriorityResolver.set(3,
                                2);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(2,
                       new RowNumber(1));
        assertSalience(0,
                       new RowNumber(2));
        assertSalience(1,
                       new RowNumber(3));
    }

    @Test
    public void when2rowsHavePriorityOverTheSameRow() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(2,
                                1);
        rowPriorityResolver.set(3,
                                1);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(0,
                       new RowNumber(1));
        assertSalience(2,
                       new RowNumber(2));
        assertSalience(1,
                       new RowNumber(3));
    }

    @Test
    public void complex() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(2,
                                0);
        rowPriorityResolver.set(3,
                                1);
        rowPriorityResolver.set(4,
                                2);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(2,
                       new RowNumber(1));
        assertSalience(0,
                       new RowNumber(2));
        assertSalience(3,
                       new RowNumber(3));
        assertSalience(1,
                       new RowNumber(4));
    }

    @Test
    public void complex2() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(2,
                                0);
        rowPriorityResolver.set(3,
                                2);
        rowPriorityResolver.set(4,
                                1);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(2,
                       new RowNumber(1));
        assertSalience(0,
                       new RowNumber(2));
        assertSalience(1,
                       new RowNumber(3));
        assertSalience(3,
                       new RowNumber(4));
    }

    @Test
    public void reverseOrder() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(2,
                                1);
        rowPriorityResolver.set(3,
                                2);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(0,
                       new RowNumber(1));
        assertSalience(1,
                       new RowNumber(2));
        assertSalience(2,
                       new RowNumber(3));
    }

    @Test
    public void complex3() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(2,
                                1);
        rowPriorityResolver.set(3,
                                2);
        rowPriorityResolver.set(4,
                                1);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(0,
                       new RowNumber(1));
        assertSalience(2,
                       new RowNumber(2));
        assertSalience(3,
                       new RowNumber(3));
        assertSalience(1,
                       new RowNumber(4));
    }

    @Test
    public void complex4() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);
        rowPriorityResolver.set(2,
                                0);
        rowPriorityResolver.set(3,
                                0);
        rowPriorityResolver.set(4,
                                3);
        rowPriorityResolver.set(5,
                                2);
        rowPriorityResolver.set(6,
                                1);

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(4,
                       new RowNumber(1));
        assertSalience(2,
                       new RowNumber(2));
        assertSalience(0,
                       new RowNumber(3));
        assertSalience(1,
                       new RowNumber(4));
        assertSalience(3,
                       new RowNumber(5));
        assertSalience(5,
                       new RowNumber(6));
    }

    @Test
    public void twoSeparatePriorityGroups() throws
            Exception {

        rowPriorityResolver.set(1,
                                0);

        // First group. Rows 2 & 3
        rowPriorityResolver.set(2,
                                0);
        rowPriorityResolver.set(3,
                                2);

        // Second group. Rows 4, 5, 6
        rowPriorityResolver.set(4,
                                0);
        rowPriorityResolver.set(5,
                                4);
        rowPriorityResolver.set(6,
                                5);

        /*
         Due to the priorities the engine sets ( rule order, a row on top of another has a priority on each row below it )
         The Resolver should make sure first group keeps priority over the second.
         (You might even say the first group is Row 1 on top and the two after that are 2 and 3.)
          */

        priorities = rowPriorityResolver.getPriorityRelations();

        assertSalience(5,
                       new RowNumber(1));

        assertSalience(3,
                       new RowNumber(2));
        assertSalience(4,
                       new RowNumber(3));

        assertSalience(0,
                       new RowNumber(4));
        assertSalience(1,
                       new RowNumber(5));
        assertSalience(2,
                       new RowNumber(6));
    }

    @Test(expected = IllegalArgumentException.class)
    public void priorityOverLowerRow() throws
            Exception {

        rowPriorityResolver.set(1,
                                4);
    }

    private void assertSalience(final Integer expected,
                                final RowNumber rowNumber) {
        assertEquals(expected,
                     priorities.getSalience(rowNumber)
                             .getSalience());
    }
}