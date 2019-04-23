/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

public class RangeCheckTest
        extends RangeCheckTestBase {

    @Test
    public void runTheCheckWithoutRange() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

        createTable();
        final List<Issue> issues = runAnalysis();

        assertEquals(2, issues.size());
        assertTrue(issues.get(0) instanceof MissingRangeIssue);
        assertTrue(issues.get(1) instanceof MissingRangeIssue);

        final MissingRangeIssue dogIssue = getIssue("Dog", issues);

        assertEquals(2, dogIssue.getUncoveredRanges().size());
        assertTrue(contains(Integer.MIN_VALUE, 11, dogIssue.getUncoveredRanges()));
        assertTrue(contains(15, 17, dogIssue.getUncoveredRanges()));

        final MissingRangeIssue catIssue = getIssue("Cat", issues);

        assertEquals(1, catIssue.getUncoveredRanges().size());
        assertTrue(contains(Integer.MIN_VALUE, 11, catIssue.getUncoveredRanges()));
    }

    @Test
    public void runTheCheckWithRange() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, new FieldRange(0, Integer.MAX_VALUE), analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

        createTable();
        final List<Issue> issues = runAnalysis();

        assertEquals(2, issues.size());
        assertTrue(issues.get(0) instanceof MissingRangeIssue);
        assertTrue(issues.get(1) instanceof MissingRangeIssue);

        final MissingRangeIssue dogIssue = getIssue("Dog", issues);

        assertEquals(2, dogIssue.getUncoveredRanges().size());
        assertTrue(contains(0, 11, dogIssue.getUncoveredRanges()));
        assertTrue(contains(15, 17, dogIssue.getUncoveredRanges()));

        final MissingRangeIssue catIssue = getIssue("Cat", issues);

        assertEquals(1, catIssue.getUncoveredRanges().size());
        assertTrue(contains(0, 11, catIssue.getUncoveredRanges()));
    }

    public void createTable() {
        /**
         * Create table
         * Columns are:
         *  age >= | age < | animal type | action to be taken
         */
        addRow(1, 12, 15, "supervised"); // No animal type set, all the types match
        addRow(2, 15, "Cat", "ok");
        addRow(3, 18, "Dog", "ok");
    }
}
