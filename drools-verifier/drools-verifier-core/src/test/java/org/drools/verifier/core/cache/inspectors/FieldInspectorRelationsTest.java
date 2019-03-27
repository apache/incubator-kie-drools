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

package org.drools.verifier.core.cache.inspectors;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FieldInspectorRelationsTest {

    private AnalyzerConfigurationMock configurationMock;

    private FieldInspector a;
    private FieldInspector b;

    @Before
    public void setUp() throws
            Exception {

        configurationMock = new AnalyzerConfigurationMock();

        ObjectField objectField = mock(ObjectField.class);
        a = new FieldInspector(new Field(objectField,
                                         "org.Person",
                                         "String",
                                         "name",
                                         configurationMock),
                               mock(RuleInspectorUpdater.class),
                               mock(AnalyzerConfiguration.class));
        b = new FieldInspector(new Field(objectField,
                                         "org.Person",
                                         "String",
                                         "name",
                                         configurationMock),
                               mock(RuleInspectorUpdater.class),
                               mock(AnalyzerConfiguration.class));
    }

    @Test
    public void testRedundancy01() throws
            Exception {
        assertTrue(a.isRedundant(b));
        assertTrue(b.isRedundant(a));
    }

    @Test
    public void testRedundancy02() throws
            Exception {
        final FieldInspector x = new FieldInspector(new Field(mock(ObjectField.class),
                                                              "org.Address",
                                                              "String",
                                                              "name",
                                                              configurationMock),
                                                    mock(RuleInspectorUpdater.class),
                                                    mock(AnalyzerConfiguration.class));

        assertFalse(x.isRedundant(b));
        assertFalse(b.isRedundant(x));
    }

    @Test
    public void testSubsumpt01() throws
            Exception {
        assertTrue(a.subsumes(b));
        assertTrue(b.subsumes(a));
    }

    @Test
    public void testSubsumpt02() throws
            Exception {
        final FieldInspector x = new FieldInspector(new Field(mock(ObjectField.class),
                                                              "org.Address",
                                                              "String",
                                                              "name",
                                                              configurationMock),
                                                    mock(RuleInspectorUpdater.class),
                                                    mock(AnalyzerConfiguration.class));

        assertFalse(x.subsumes(b));
        assertFalse(b.subsumes(x));
    }
}