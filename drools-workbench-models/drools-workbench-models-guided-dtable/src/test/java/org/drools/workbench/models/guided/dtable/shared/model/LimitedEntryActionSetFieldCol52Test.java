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

public class LimitedEntryActionSetFieldCol52Test {

    @Test
    public void testSameSetFieldActions() {
        final LimitedEntryActionSetFieldCol52 set = new LimitedEntryActionSetFieldCol52();
        set.setDefaultValue(new DTCellValue52(true));
        final LimitedEntryActionSetFieldCol52 sameSet = new LimitedEntryActionSetFieldCol52();
        sameSet.setDefaultValue(new DTCellValue52(true));

        assertEquals(set, sameSet);
    }

    @Test
    public void testDifferentActions() {
        final LimitedEntryActionSetFieldCol52 set = new LimitedEntryActionSetFieldCol52();
        set.setDefaultValue(new DTCellValue52(true));
        final LimitedEntryActionSetFieldCol52 notSet = new LimitedEntryActionSetFieldCol52();
        notSet.setDefaultValue(new DTCellValue52(false));

        assertNotEquals(set, notSet);
    }
}
