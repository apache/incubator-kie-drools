/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.dtanalysis;

import java.util.Set;

import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.checks.AnalyzerConfigurationMock;
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
import org.junit.Before;
import org.junit.Test;

public class VV_Toni_Test {

    private AnalyzerConfiguration analyzerConfiguration = new AnalyzerConfigurationMock(CheckConfiguration.newDefault());
    private ObjectField tAgeObjectField;
    private ObjectField tKittenObjectField;
    private ObjectField riskObjectField;
    private Column column1;
    private Column column2;
    private Column column3;
    private ObjectType objectType;
    private IndexImpl index1;

    @Before
    public void setUp() throws Exception {
        index1 = new IndexImpl();

        column1 = new Column(1, analyzerConfiguration);
        column2 = new Column(2, analyzerConfiguration);
        column3 = new Column(3, analyzerConfiguration);

        objectType = new ObjectType("mock.Type",
                                    analyzerConfiguration);

        tAgeObjectField = new ObjectField("mock.Type", "Integer", "tAge", analyzerConfiguration);
        tKittenObjectField = new ObjectField("mock.Type", "String", "tKitten", analyzerConfiguration);
        riskObjectField = new ObjectField("mock.Type", "String", "risk", analyzerConfiguration);
        objectType.getFields().add(tAgeObjectField);
        objectType.getFields().add(tKittenObjectField);
        objectType.getFields().add(riskObjectField);
    }

    @Test
    public void name() {

        addRow(2, 12, 15, "supervised");
        addRow(3, 15, "Cat", "ok");
        addRow(4, 18, "Dog", "ok");

        final Analyzer analyzer = new Analyzer(
                                               new Reporter() {

                                                   @Override
                                                   public void sendReport(Set<Issue> issues) {
                                                       for (Issue issue : issues) {
                                                           System.out.println(issue.getCheckType());
                                                           System.out.println(issue.getDebugMessage());
                                                       }
                                                   }

                                                   @Override
                                                   public void sendStatus(Status status) {
                                                       System.out.println(status.getTotalCheckCount());
                                                   }
                                               },
                                               index1,
                                               analyzerConfiguration);

        analyzer.start();

        analyzer.analyze();
    }

    private void addRow(int rowNumber, int a1, int a2, String c) {

        final Pattern pattern = new Pattern("mock.Type",
                                            objectType,
                                            analyzerConfiguration);
        final Field tAge = new Field(tAgeObjectField, "mock.Type", "Integer", "tAge", analyzerConfiguration);
        final Field tKitten = new Field(tKittenObjectField, "mock.Type", "String", "tKitten", analyzerConfiguration);
        final Field risk = new Field(riskObjectField, "mock.Type", "String", "risk", analyzerConfiguration);

        pattern.getFields().add(tAge);
        pattern.getFields().add(tKitten);
        pattern.getFields().add(risk);

        final FieldCondition<Integer> condition1 = new FieldCondition<Integer>(tAge, column1, ">=", new Values(a1), analyzerConfiguration);
        final FieldCondition<Integer> condition2 = new FieldCondition<Integer>(tAge, column1, "<", new Values(a2), analyzerConfiguration);
        final FieldAction riskAction = new FieldAction(risk, column3, DataType.DataTypes.STRING, new Values(c), analyzerConfiguration);

        tAge.getConditions().add(condition1);
        tAge.getConditions().add(condition2);
        risk.getActions().add(riskAction);

        final Rule row = new Rule(rowNumber, analyzerConfiguration);
        row.getConditions().add(condition1);
        row.getConditions().add(condition2);
        row.getPatterns().add(pattern);
        row.getActions().add(riskAction);
        index1.getRules().add(row);
    }

    private void addRow(int rowNumber, int a, String b, String c) {

        final Pattern pattern = new Pattern("mock.Type",
                                            objectType,
                                            analyzerConfiguration);
        final Field tAge = new Field(tAgeObjectField, "mock.Type", "Integer", "tAge", analyzerConfiguration);
        final Field tKitten = new Field(tKittenObjectField, "mock.Type", "String", "tKitten", analyzerConfiguration);
        final Field risk = new Field(riskObjectField, "mock.Type", "String", "risk", analyzerConfiguration);

        pattern.getFields().add(tAge);
        pattern.getFields().add(tKitten);
        pattern.getFields().add(risk);

        final FieldCondition<Integer> condition1 = new FieldCondition<Integer>(tAge, column1, ">=", new Values(a), analyzerConfiguration);
        final FieldCondition<String> condition2 = new FieldCondition<String>(tKitten, column2, "==", new Values(b), analyzerConfiguration);
        final FieldAction riskAction = new FieldAction(risk, column3, DataType.DataTypes.STRING, new Values(c), analyzerConfiguration);

        tAge.getConditions().add(condition1);
        tKitten.getConditions().add(condition2);
        risk.getActions().add(riskAction);

        final Rule row = new Rule(rowNumber, analyzerConfiguration);
        row.getConditions().add(condition1);
        row.getConditions().add(condition2);
        row.getPatterns().add(pattern);
        row.getActions().add(riskAction);
        index1.getRules().add(row);
    }
}