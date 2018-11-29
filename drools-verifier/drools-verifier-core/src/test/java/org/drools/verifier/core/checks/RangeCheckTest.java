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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.gaps.MissingRange;
import org.drools.verifier.api.reporting.gaps.MissingRangeIssue;
import org.drools.verifier.core.checks.base.JavaCheckRunner;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.verifier.core.index.IndexImpl;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.DataType;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldAction;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.FieldRange;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.main.Reporter;
import org.drools.verifier.core.util.DateTimeFormatProviderMock;
import org.drools.verifier.core.util.UUIDKeyProviderMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RangeCheckTest {

    private final static String KITTEN = "kitten";
    private final static String OBJECT_TYPE_NAME = "mock.Type";
    private final static String STRING = "String";
    private final static String RISK = "risk";
    private final static String INTEGER = "Integer";
    private final static String AGE = "age";

    private AnalyzerConfiguration analyzerConfiguration = new AnalyzerConfiguration(
            "UUID",
            new DateTimeFormatProviderMock(),
            new UUIDKeyProviderMock(),
            CheckConfiguration.newDefault(),
            new JavaCheckRunner());

    // In this case everything is mapped under one type
    private ObjectType objectType;

    // Fields that the objectType has
    private ObjectField ageObjectField;
    private ObjectField kittenObjectField;
    private ObjectField riskObjectField;

    // Column that exist in the table
    private Column ageConditionColumn;
    private Column animalTypeConditionColumn;
    private Column actionColumn;

    /*
    Index contains the data about the table content. It doesn't really care about the fact that it is a table that is being analyzed
    the Columns are just used for tracking what changed where.
     */
    private IndexImpl index;

    @Before
    public void setUp() throws Exception {
        index = new IndexImpl();

        ageConditionColumn = new Column(1, analyzerConfiguration);
        animalTypeConditionColumn = new Column(2, analyzerConfiguration);
        actionColumn = new Column(3, analyzerConfiguration);

        objectType = new ObjectType(OBJECT_TYPE_NAME,
                                    analyzerConfiguration);

        kittenObjectField = new ObjectField(OBJECT_TYPE_NAME, STRING, KITTEN, analyzerConfiguration);
        riskObjectField = new ObjectField(OBJECT_TYPE_NAME, STRING, RISK, analyzerConfiguration);

        objectType.getFields().add(kittenObjectField);
        objectType.getFields().add(riskObjectField);
    }

    @Test
    public void runTheCheckWithoutRange() {
        ageObjectField = new ObjectField(OBJECT_TYPE_NAME, INTEGER, AGE, analyzerConfiguration);
        objectType.getFields().add(ageObjectField);

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

    private MissingRangeIssue getIssue(final String type,
                                       final List<Issue> issues) {
        for (final Issue issue : issues) {
            final MissingRangeIssue missingRangeIssue = (MissingRangeIssue) issue;
            if (missingRangeIssue.getPartition().get(0).getValue().equals(type)) {
                return missingRangeIssue;
            }
        }
        return null;
    }

    private boolean contains(int lower, int higher, Collection<MissingRange> uncoveredRanges) {
        for (MissingRange uncoveredRange : uncoveredRanges) {
            if (uncoveredRange.getLower().equals(lower) && uncoveredRange.getUpper().equals(higher)) {
                return true;
            }
        }

        return false;
    }

    private List<Issue> runAnalysis() {
        final List<Issue> issues = new ArrayList<>();

        /**
         * Create table
         * Columns are:
         *  age >= | age < | animal type | action to be taken
         */
        addRow(2, 12, 15, "supervised"); // No animal type set, all the types match
        addRow(3, 15, "Cat", "ok");
        addRow(4, 18, "Dog", "ok");

        final Analyzer analyzer = new Analyzer(
                new Reporter() {
                    @Override
                    public void sendReport(final Set<Issue> foundIssues) {
                        issues.addAll(foundIssues);
                    }

                    @Override
                    public void sendStatus(final Status status) {

                    }
                },
                index,
                analyzerConfiguration);

        analyzer.start();

        return issues;
    }

    private void addRow(final int rowNumber,
                        final int ageGreaterThanOrEqualToColumnValue,
                        final int ageLessThanColumnValue,
                        final String actionColumn) {

        final Pattern typePattern = new Pattern(OBJECT_TYPE_NAME,
                                                objectType,
                                                analyzerConfiguration);
        final Field ageField = new Field(ageObjectField, OBJECT_TYPE_NAME, INTEGER, this.AGE, analyzerConfiguration);
        final Field kittenField = new Field(kittenObjectField, OBJECT_TYPE_NAME, STRING, KITTEN, analyzerConfiguration);
        final Field riskField = new Field(riskObjectField, OBJECT_TYPE_NAME, STRING, RISK, analyzerConfiguration);

        typePattern.getFields().add(ageField);
        typePattern.getFields().add(kittenField);
        typePattern.getFields().add(riskField);

        final FieldCondition<Integer> ageGreaterThanOrEqualCondition = new FieldCondition<Integer>(ageField, ageConditionColumn, ">=", new Values(ageGreaterThanOrEqualToColumnValue), analyzerConfiguration);
        final FieldCondition<Integer> ageLessThanCondition = new FieldCondition<Integer>(ageField, ageConditionColumn, "<", new Values(ageLessThanColumnValue), analyzerConfiguration);
        final FieldAction riskAction = new FieldAction(riskField, this.actionColumn, DataType.DataTypes.STRING, new Values(actionColumn), analyzerConfiguration);

        ageField.getConditions().add(ageGreaterThanOrEqualCondition);
        ageField.getConditions().add(ageLessThanCondition);
        riskField.getActions().add(riskAction);

        final Rule row = new Rule(rowNumber,
                                  analyzerConfiguration);
        row.getConditions().add(ageGreaterThanOrEqualCondition);
        row.getConditions().add(ageLessThanCondition);
        row.getPatterns().add(typePattern);
        row.getActions().add(riskAction);
        index.getRules().add(row);
    }

    private void addRow(final int rowNumber,
                        final int ageGreaterThanOrEqualToColumnValue,
                        final String animalTypeColumn,
                        final String actionColumnValue) {

        final Pattern typePattern = new Pattern(OBJECT_TYPE_NAME,
                                                objectType,
                                                analyzerConfiguration);
        final Field ageField = new Field(ageObjectField, OBJECT_TYPE_NAME, INTEGER, this.AGE, analyzerConfiguration);
        final Field kittenField = new Field(kittenObjectField, OBJECT_TYPE_NAME, STRING, KITTEN, analyzerConfiguration);
        final Field riskField = new Field(riskObjectField, OBJECT_TYPE_NAME, STRING, RISK, analyzerConfiguration);

        typePattern.getFields().add(ageField);
        typePattern.getFields().add(kittenField);
        typePattern.getFields().add(riskField);

        final FieldCondition<Integer> ageFieldCondition = new FieldCondition<Integer>(ageField, ageConditionColumn, ">=", new Values(ageGreaterThanOrEqualToColumnValue), analyzerConfiguration);
        final FieldCondition<String> animalTypeCondition = new FieldCondition<String>(kittenField, animalTypeConditionColumn, "==", new Values(animalTypeColumn), analyzerConfiguration);
        final FieldAction riskAction = new FieldAction(riskField, actionColumn, DataType.DataTypes.STRING, new Values(actionColumnValue), analyzerConfiguration);

        ageField.getConditions().add(ageFieldCondition);
        kittenField.getConditions().add(animalTypeCondition);
        riskField.getActions().add(riskAction);

        final Rule row = new Rule(rowNumber,
                                  analyzerConfiguration);
        row.getConditions().add(ageFieldCondition);
        row.getConditions().add(animalTypeCondition);
        row.getPatterns().add(typePattern);
        row.getActions().add(riskAction);
        index.getRules().add(row);
    }
}
