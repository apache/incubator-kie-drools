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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RangeCheckTableCompleteTest
        extends RangeCheckTestBase {

    @Test
    public void runTheCheckWithRange() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, new FieldRange(0, 100), analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

        /**
         * Create table
         * Columns are:
         *  age >= | age < | animal type | action to be taken
         */
        addRow(2, 0, 11, "underage"); // No animal type set, all the types match
        addRow(2, 12, 101, "supervised"); // No animal type set, all the types match

        final List<Issue> issues = runAnalysis();

        assertTrue(issues.isEmpty());
    }

    @Test
    public void runTheCheckWithRangeAndPartitionContent() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, new FieldRange(0, 100), analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

        /**
         * Create table
         * Columns are:
         *  age >= | age < | animal type | action to be taken
         */
        addRow(2, 0, 10, "supervised"); // No animal type set, all the types match
        addRow(3, 11, "Cat", "ok");
        addRow(4, 11, "Dog", "ok");

        final List<Issue> issues = runAnalysis();

        assertTrue(issues.isEmpty());
    }

    @Test
    public void runTheCheckWithRangeAndOnlyDogPartition() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, new FieldRange(0, 100), analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

        /**
         * Create table
         * Columns are:
         *  age >= | age < | animal type | action to be taken
         */
        addRow(2, 12, 15, "supervised"); // No animal type set, all the types match
        addRow(3, 15, "Cat", "ok");
        addRow(4, 18, "Dog", "ok");
        addRow(5, 0, 12, "ok");

        final List<Issue> issues = runAnalysis();

        final MissingRangeIssue dogIssue = getIssue("Dog", issues);

        assertEquals(1, dogIssue.getUncoveredRanges().size());
        assertTrue(contains(15, 17, dogIssue.getUncoveredRanges()));

        final MissingRangeIssue catIssue = getIssue("Cat", issues);
        assertNull(catIssue);
    }

    @Test
    public void runTheCheckWithoutRangeAndOnlyDogPartition() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

        /**
         * Create table
         * Columns are:
         *  age >= | age < | animal type | action to be taken
         */
        addRow(2, 12, 15, "supervised"); // No animal type set, all the types match
        addRow(3, 15, "Cat", "ok");
        addRow(4, 18, "Dog", "ok");
        addRow(5, Integer.MIN_VALUE, 12, "ok");

        final List<Issue> issues = runAnalysis();

        final MissingRangeIssue dogIssue = getIssue("Dog", issues);

        assertEquals(1, dogIssue.getUncoveredRanges().size());
        assertTrue(contains(15, 17, dogIssue.getUncoveredRanges()));

        final MissingRangeIssue catIssue = getIssue("Cat", issues);
        assertNull(catIssue);
    }
}
