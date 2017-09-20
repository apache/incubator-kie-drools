/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.datamodel.oracle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OperatorsOracleTest {

    @Test
    public void stringConnectives() {
        assertContainsAll(OperatorsOracle.STRING_CONNECTIVES, "|| ==", "|| !=", "&& !=", "&& >", "&& <", "|| >", "|| <", "&& >=", "&& <=", "|| <=", "|| >=", "&& matches", "|| matches");
    }

    @Test
    public void STANDARD_OPERATORS() {
        assertContainsAll(OperatorsOracle.STANDARD_OPERATORS, "==", "!=", "== null", "!= null");
    }

    @Test
    public void standardConnectives() {
        assertContainsAll(OperatorsOracle.STANDARD_CONNECTIVES, "|| ==", "|| !=", "&& !=");
    }

    @Test
    public void comparableOperators() {
        assertContainsAll(OperatorsOracle.COMPARABLE_OPERATORS, "==", "!=", "<", ">", "<=", ">=", "== null", "!= null");
    }

    @Test
    public void simpleCEPConnectives() {
        assertContainsAll(OperatorsOracle.SIMPLE_CEP_CONNECTIVES, "|| after", "|| before", "|| coincides", "&& after", "&& before", "&& coincides");
    }

    @Test
    public void windowCEPOperators() {
        assertContainsAll(OperatorsOracle.WINDOW_CEP_OPERATORS, "over window:time", "over window:length");
    }

    @Test
    public void complexCEPOperators() {
        assertContainsAll(OperatorsOracle.COMPLEX_CEP_OPERATORS, "during", "finishes", "finishedby", "includes", "meets", "metby", "overlaps", "overlappedby", "starts", "startedby");
    }

    @Test
    public void simpleCEPOrators() {
        assertContainsAll(OperatorsOracle.SIMPLE_CEP_OPERATORS, "after", "before", "coincides");
    }

    @Test
    public void conditionalElements() {
        assertContainsAll(OperatorsOracle.CONDITIONAL_ELEMENTS, "not", "exists", "or");
    }

    @Test
    public void complexCEPConnectives() {
        assertContainsAll(OperatorsOracle.COMPLEX_CEP_CONNECTIVES, "|| during", "|| finishes", "|| finishedby", "|| includes", "|| meets", "|| metby", "|| overlaps", "|| overlappedby", "|| starts", "|| startedby",
                          "&& during", "&& finishes", "&& finishedby", "&& includes", "&& meets", "&& metby", "&& overlaps", "&& overlappedby", "&& starts", "&& startedby");
    }

    @Test
    public void comparableConnectives() {
        assertContainsAll(OperatorsOracle.COMPARABLE_CONNECTIVES, "|| ==", "|| !=", "&& !=", "&& >", "&& <", "|| >", "|| <", "&& >=", "&& <=", "|| <=", "|| >=");
    }

    @Test
    public void explicitListOperators() {
        assertContainsAll(OperatorsOracle.EXPLICIT_LIST_OPERATORS, "in", "not in");
    }

    @Test
    public void collectionOperators() {
        assertContainsAll(OperatorsOracle.COLLECTION_OPERATORS, "contains", "excludes", "==", "!=", "== null", "!= null");
    }

    @Test
    public void collectionConnectives() {
        assertContainsAll(OperatorsOracle.COLLECTION_CONNECTIVES, "|| ==", "|| !=", "&& !=", "|| contains", "&& contains", "|| excludes", "&& excludes");
    }

    @Test
    public void stringOperators() {
        assertContainsAll(OperatorsOracle.STRING_OPERATORS, "==", "!=", "<", ">", "<=", ">=", "matches", "not matches", "soundslike", "not soundslike", "== null", "!= null");
    }

    public void assertContainsAll(final String[] list, String... items) {

        assertEquals(items.length,
                     list.length);

        for (final String item : items) {

            assertContains(list,
                           item);
        }
    }

    private void assertContains(final String[] strings, final String s) {
        for (final String string : strings) {
            if (string.equals(s)) {
                return;
            }
        }

        fail("Could not find " + s);
    }
}