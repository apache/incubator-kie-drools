/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.checks;

import java.util.List;

import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.gaps.MissingRangeIssue;
import org.drools.verifier.core.index.model.FieldRange;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RangeCheckSingleLineInTableTest
        extends RangeCheckTestBase {

    @Test
    public void runTheCheckWithoutRange() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

        createTable();

        final List<Issue> issues = runAnalysis();

        final MissingRangeIssue issue = (MissingRangeIssue) issues.get(0);

        assertEquals(2, issue.getUncoveredRanges().size());
        assertTrue(contains(Integer.MIN_VALUE, 4, issue.getUncoveredRanges()));
        assertTrue(contains(10, Integer.MAX_VALUE, issue.getUncoveredRanges()));
    }

    @Test
    public void runTheCheckWithRange() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, new FieldRange(0, 100), analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

        createTable();

        final List<Issue> issues = runAnalysis();

        final MissingRangeIssue issue = (MissingRangeIssue) issues.get(0);

        assertEquals(2, issue.getUncoveredRanges().size());
        assertTrue(contains(0, 4, issue.getUncoveredRanges()));
        assertTrue(contains(10, 100, issue.getUncoveredRanges()));
    }

    public void createTable() {
        /**
         * Create table
         * Columns are:
         *  age >= | age < | animal type | action to be taken
         */
        addRow(5, 5, 10, "ok");
    }
}
