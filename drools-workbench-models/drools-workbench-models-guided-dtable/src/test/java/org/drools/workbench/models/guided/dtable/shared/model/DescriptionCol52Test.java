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

public class DescriptionCol52Test {

    @Test
    public void testSameDescriptions() {
        final DescriptionCol52 aDescription = new DescriptionCol52();
        aDescription.setDefaultValue(new DTCellValue52("a description"));
        final DescriptionCol52 sameDescription = new DescriptionCol52();
        sameDescription.setDefaultValue(new DTCellValue52("a description"));

        assertEquals(aDescription, sameDescription);
    }

    @Test
    public void testDifferentDescriptions() {
        final DescriptionCol52 aDescription = new DescriptionCol52();
        aDescription.setDefaultValue(new DTCellValue52("a description"));
        final DescriptionCol52 differentDescription = new DescriptionCol52();
        differentDescription.setDefaultValue(new DTCellValue52("a different description"));

        assertNotEquals(aDescription, differentDescription);
    }
}
