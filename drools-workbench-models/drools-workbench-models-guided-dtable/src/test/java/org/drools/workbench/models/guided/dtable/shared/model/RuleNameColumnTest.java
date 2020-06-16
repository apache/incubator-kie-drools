/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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

public class RuleNameColumnTest {

    @Test
    public void testEquals() {
        final RuleNameColumn one = new RuleNameColumn();
        one.setHeader("h1");
        one.setDefaultValue(new DTCellValue52("rule name"));

        final RuleNameColumn two = new RuleNameColumn();
        two.setHeader("h1");
        two.setDefaultValue(new DTCellValue52("rule name"));

        assertEquals(one, two);
    }

    @Test
    public void testNotEqualsHeaders() {
        final RuleNameColumn one = new RuleNameColumn();
        one.setHeader("h1");
        one.setDefaultValue(new DTCellValue52("rule name"));

        final RuleNameColumn two = new RuleNameColumn();
        two.setHeader("h2");
        two.setDefaultValue(new DTCellValue52("rule name"));

        assertNotEquals(one, two);
    }

    @Test
    public void testNotEqualsDefaultValues() {
        final RuleNameColumn one = new RuleNameColumn();
        one.setHeader("h1");
        one.setDefaultValue(new DTCellValue52("rule name 1"));

        final RuleNameColumn two = new RuleNameColumn();
        two.setHeader("h1");
        two.setDefaultValue(new DTCellValue52("rule name 2"));

        assertNotEquals(one, two);
    }
}