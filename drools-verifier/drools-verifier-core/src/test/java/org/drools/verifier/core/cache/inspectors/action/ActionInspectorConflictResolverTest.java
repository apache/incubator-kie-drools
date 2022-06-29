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

package org.drools.verifier.core.cache.inspectors.action;

import java.util.Date;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldAction;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ActionInspectorConflictResolverTest {

    private AnalyzerConfiguration configuration;

    @Before
    public void setUp() throws
            Exception {
        configuration = new AnalyzerConfigurationMock();
    }

    @Test
    public void testRedundancy001() throws
            Exception {

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");

        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    public void testRedundancy002() throws
            Exception {

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Rambo");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    public void testRedundancy003() throws
            Exception {

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "org.test1.Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "org.test2.Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    public void testRedundancy004() throws
            Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Boolean",
                                                               "isOldEnough",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.BOOLEAN,
                                                     true);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Boolean",
                                                               "isOldEnough",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "true");

        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    public void testRedundancy005() throws
            Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Boolean",
                                                               "isOldEnough",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.BOOLEAN,
                                                     true);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Boolean",
                                                               "isOldEnough",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "false");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    public void testRedundancy006() throws
            Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Integer",
                                                               "age",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.NUMERIC_INTEGER,
                                                     20);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Integer",
                                                               "age",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "20");

        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    public void testRedundancy007() throws
            Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Integer",
                                                               "age",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.NUMERIC_INTEGER,
                                                     20);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Integer",
                                                               "age",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "10");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    public void testRedundancy008() throws
            Exception {
        Date date = new Date();
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Integer",
                                                               "birthDay",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.DATE,
                                                     date);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Integer",
                                                               "birthDay",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     format(date));

        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    public void testRedundancy009() throws
            Exception {

        Date value = new Date();

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Integer",
                                                               "birthDay",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.DATE,
                                                     value);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Integer",
                                                               "birthDay",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "29-Dec-1981");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    public void testConflict001() throws
            Exception {

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Rambo");

        assertThat(a.conflicts(b)).isTrue();
        assertThat(b.conflicts(a)).isTrue();
    }

    @Test
    public void testConflict002() throws
            Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Boolean",
                                                               "isOldEnough",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.BOOLEAN,
                                                     true);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "Boolean",
                                                               "isOldEnough",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "false");

        assertThat(a.conflicts(b)).isTrue();
        assertThat(b.conflicts(a)).isTrue();
    }

    @Test
    public void testNoConflict001() throws
            Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Address",
                                                               "String",
                                                               "street",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Rambo");

        assertThat(a.conflicts(b)).isFalse();
        assertThat(b.conflicts(a)).isFalse();
    }

    @Test
    public void testNoConflict002() throws
            Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                                                               "Person",
                                                               "String",
                                                               "name",
                                                               new AnalyzerConfigurationMock()),
                                                     DataType.DataTypes.STRING,
                                                     "Toni");

        assertThat(a.conflicts(b)).isFalse();
        assertThat(b.conflicts(a)).isFalse();
    }

    @Test
    public void testNoConflict003() throws
            Exception {
        ActionInspector a = createSetActionInspector(new FieldAction(new Field(mock(ObjectField.class),
                                                                               "Person",
                                                                               "String",
                                                                               "name",
                                                                               new AnalyzerConfigurationMock()),
                                                                     mock(Column.class),
                                                                     DataType.DataTypes.BOOLEAN,
                                                                     new Values(true),
                                                                     new AnalyzerConfigurationMock()));
        ActionInspector b = createSetActionInspector(new FieldAction(new Field(mock(ObjectField.class),
                                                                               "Person",
                                                                               "String",
                                                                               "name",
                                                                               new AnalyzerConfigurationMock()),
                                                                     mock(Column.class),
                                                                     DataType.DataTypes.STRING,
                                                                     new Values(true),
                                                                     new AnalyzerConfigurationMock()));

        assertThat(a.conflicts(b)).isFalse();
        assertThat(b.conflicts(a)).isFalse();
    }

    private ActionInspector createSetActionInspector(final Field field,
                                                     final DataType.DataTypes dataType,
                                                     final Comparable comparable) {
        return new FieldActionInspector(new FieldAction(field,
                                                        mock(Column.class),
                                                        dataType,
                                                        new Values(comparable),
                                                        new AnalyzerConfigurationMock()),
                                        configuration);
    }

    private ActionInspector createSetActionInspector(final Action action) {
        return new ActionInspector(action,
                                   new AnalyzerConfigurationMock()) {
        };
    }

    private String format(final Date dateValue) {
        return configuration.formatDate(dateValue);
    }
}