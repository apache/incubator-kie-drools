/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LimitedEntryActionRetractFactCol52Test {

    @Test
    public void testSameRetractActions() {
        final LimitedEntryActionRetractFactCol52 retract = new LimitedEntryActionRetractFactCol52();
        retract.setDefaultValue(new DTCellValue52(true));
        final LimitedEntryActionRetractFactCol52 sameRetract = new LimitedEntryActionRetractFactCol52();
        sameRetract.setDefaultValue(new DTCellValue52(true));

        assertEquals(retract, sameRetract);
    }

    @Test
    public void testDifferentRetractActions() {
        final LimitedEntryActionRetractFactCol52 retract = new LimitedEntryActionRetractFactCol52();
        retract.setDefaultValue(new DTCellValue52(true));
        final LimitedEntryActionRetractFactCol52 notRetract = new LimitedEntryActionRetractFactCol52();
        notRetract.setDefaultValue(new DTCellValue52(false));

        assertNotEquals(retract, notRetract);
    }
}
