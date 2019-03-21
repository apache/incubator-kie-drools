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
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.main.Reporter;
import org.drools.verifier.core.util.DateTimeFormatProviderMock;
import org.drools.verifier.core.util.UUIDKeyProviderMock;
import org.junit.Before;

public abstract class RangeCheckTestBase {

    protected final static String KITTEN = "kitten";
    protected final static String OBJECT_TYPE_NAME = "mock.Type";
    protected final static String STRING = "String";
    protected final static String RISK = "risk";
    protected final static String INTEGER = "Integer";
    protected final static String AGE = "age";

    protected AnalyzerConfiguration analyzerConfiguration = new AnalyzerConfiguration(
            "UUID",
            new DateTimeFormatProviderMock(),
            new UUIDKeyProviderMock(),
            CheckConfiguration.newDefault(),
            new JavaCheckRunner());

    // In this case everything is mapped under one type
    protected ObjectType objectType;

    // Fields that the objectType has
    protected ObjectField ageObjectField;
    protected ObjectField kittenObjectField;
    protected ObjectField riskObjectField;

    // Column that exist in the table
    protected Column ageConditionColumn;
    protected Column animalTypeConditionColumn;
    protected Column actionColumn;

    /*
    Index contains the data about the table content. It doesn't really care about the fact that it is a table that is being analyzed
    the Columns are just used for tracking what changed where.
     */
    protected IndexImpl index;

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

    protected MissingRangeIssue getIssue(final String type,
                                         final List<Issue> issues) {
        for (final Issue issue : issues) {
            if (issue instanceof MissingRangeIssue) {
                final MissingRangeIssue missingRangeIssue = (MissingRangeIssue) issue;
                if (missingRangeIssue.getPartition().get(0).getValue().equals(type)) {
                    return missingRangeIssue;
                }
            }
        }
        return null;
    }

    protected boolean contains(int lower, int higher, Collection<MissingRange> uncoveredRanges) {
        for (MissingRange uncoveredRange : uncoveredRanges) {
            if (uncoveredRange.getLower().equals(lower) && uncoveredRange.getUpper().equals(higher)) {
                return true;
            }
        }

        return false;
    }

    protected List<Issue> runAnalysis() {
        final List<Issue> issues = new ArrayList<>();

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

    protected void addRow(final int rowNumber,
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

    protected void addRow(final int rowNumber,
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
