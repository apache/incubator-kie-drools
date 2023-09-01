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
package org.drools.verifier.core.cache.inspectors;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.index.model.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PatternInspectorTest {

    private AnalyzerConfigurationMock configurationMock;

    private PatternInspector a;
    private PatternInspector b;

    @BeforeEach
    public void setUp() throws Exception {
        configurationMock = new AnalyzerConfigurationMock();

        a = new PatternInspector(new Pattern("a",
                                             new ObjectType("org.Person",
                                                            configurationMock),
                                             configurationMock),
                                 mock(RuleInspectorUpdater.class),
                                 mock(AnalyzerConfiguration.class));
        b = new PatternInspector(new Pattern("b",
                                             new ObjectType("org.Person",
                                                            configurationMock),
                                             configurationMock),
                                 mock(RuleInspectorUpdater.class),
                                 mock(AnalyzerConfiguration.class));
    }

    @Test
    void testRedundancy01() throws Exception {
        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    void testRedundancy02() throws Exception {
        final PatternInspector x = new PatternInspector(new Pattern("x",
                        new ObjectType("org.Address",
                                configurationMock),
                        configurationMock),
                mock(RuleInspectorUpdater.class),
                mock(AnalyzerConfiguration.class));

        assertThat(x.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(x)).isFalse();
    }

    @Test
    void testSubsumpt01() throws Exception {
        assertThat(a.subsumes(b)).isTrue();
        assertThat(b.subsumes(a)).isTrue();
    }

    @Test
    void testSubsumpt02() throws Exception {
        final PatternInspector x = new PatternInspector(new Pattern("x",
                        new ObjectType("org.Address",
                                configurationMock),
                        configurationMock),
                mock(RuleInspectorUpdater.class),
                mock(AnalyzerConfiguration.class));

        assertThat(x.subsumes(b)).isFalse();
        assertThat(b.subsumes(x)).isFalse();
    }
}