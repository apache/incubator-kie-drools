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

public class LimitedEntryActionInsertFactCol52Test {
    @Test
    public void testSameInsertActions() {
        final LimitedEntryActionInsertFactCol52 insert = new LimitedEntryActionInsertFactCol52();
        insert.setDefaultValue(new DTCellValue52(true));
        final LimitedEntryActionInsertFactCol52 sameInsert = new LimitedEntryActionInsertFactCol52();
        sameInsert.setDefaultValue(new DTCellValue52(true));

        assertEquals(insert, sameInsert);
    }

    @Test
    public void testDifferentActions() {
        final LimitedEntryActionInsertFactCol52 insert = new LimitedEntryActionInsertFactCol52();
        insert.setDefaultValue(new DTCellValue52(true));
        final LimitedEntryActionInsertFactCol52 notInsert = new LimitedEntryActionInsertFactCol52();
        notInsert.setDefaultValue(new DTCellValue52(false));

        assertNotEquals(insert, notInsert);
    }
}
