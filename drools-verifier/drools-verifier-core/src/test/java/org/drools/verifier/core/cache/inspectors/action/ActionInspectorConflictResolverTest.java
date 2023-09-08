/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ActionInspectorConflictResolverTest {

    private AnalyzerConfiguration configuration;

    @BeforeEach
    public void setUp() throws Exception {
        configuration = new AnalyzerConfigurationMock();
    }

    @Test
    void testRedundancy001() throws Exception {

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");

        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    void testRedundancy002() throws Exception {

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Rambo");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    void testRedundancy003() throws Exception {

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "org.test1.Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "org.test2.Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    void testRedundancy004() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Boolean",
                        "isOldEnough",
                        new AnalyzerConfigurationMock()),
                true);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Boolean",
                        "isOldEnough",
                        new AnalyzerConfigurationMock()),
                "true");

        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    void testRedundancy005() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Boolean",
                        "isOldEnough",
                        new AnalyzerConfigurationMock()),
                true);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Boolean",
                        "isOldEnough",
                        new AnalyzerConfigurationMock()),
                "false");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    void testRedundancy006() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Integer",
                        "age",
                        new AnalyzerConfigurationMock()),
                20);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Integer",
                        "age",
                        new AnalyzerConfigurationMock()),
                "20");

        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    void testRedundancy007() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Integer",
                        "age",
                        new AnalyzerConfigurationMock()),
                20);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Integer",
                        "age",
                        new AnalyzerConfigurationMock()),
                "10");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    void testRedundancy008() throws Exception {
        Date date = new Date();
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Integer",
                        "birthDay",
                        new AnalyzerConfigurationMock()),
                date);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Integer",
                        "birthDay",
                        new AnalyzerConfigurationMock()),
                format(date));

        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    void testRedundancy009() throws Exception {

        Date value = new Date();

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Integer",
                        "birthDay",
                        new AnalyzerConfigurationMock()),
                value);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Integer",
                        "birthDay",
                        new AnalyzerConfigurationMock()),
                "29-Dec-1981");

        assertThat(a.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(a)).isFalse();
    }

    @Test
    void testConflict001() throws Exception {

        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Rambo");

        assertThat(a.conflicts(b)).isTrue();
        assertThat(b.conflicts(a)).isTrue();
    }

    @Test
    void testConflict002() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Boolean",
                        "isOldEnough",
                        new AnalyzerConfigurationMock()),
                true);
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "Boolean",
                        "isOldEnough",
                        new AnalyzerConfigurationMock()),
                "false");

        assertThat(a.conflicts(b)).isTrue();
        assertThat(b.conflicts(a)).isTrue();
    }

    @Test
    void testNoConflict001() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Address",
                        "String",
                        "street",
                        new AnalyzerConfigurationMock()),
                "Rambo");

        assertThat(a.conflicts(b)).isFalse();
        assertThat(b.conflicts(a)).isFalse();
    }

    @Test
    void testNoConflict002() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");
        ActionInspector b = createSetActionInspector(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                "Toni");

        assertThat(a.conflicts(b)).isFalse();
        assertThat(b.conflicts(a)).isFalse();
    }

    @Test
    void testNoConflict003() throws Exception {
        ActionInspector a = createSetActionInspector(new FieldAction(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                mock(Column.class),
                new Values(true),
                new AnalyzerConfigurationMock()));
        ActionInspector b = createSetActionInspector(new FieldAction(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        new AnalyzerConfigurationMock()),
                mock(Column.class),
                new Values(true),
                new AnalyzerConfigurationMock()));

        assertThat(a.conflicts(b)).isFalse();
        assertThat(b.conflicts(a)).isFalse();
    }

    private ActionInspector createSetActionInspector(final Field field,
                                                     final Comparable comparable) {
        return new FieldActionInspector(new FieldAction(field,
                                                        mock(Column.class),
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