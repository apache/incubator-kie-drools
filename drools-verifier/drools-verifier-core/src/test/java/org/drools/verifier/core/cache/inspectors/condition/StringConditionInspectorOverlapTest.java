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

package org.drools.verifier.core.cache.inspectors.condition;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.fieldCondition;

@ExtendWith(MockitoExtension.class)
public class StringConditionInspectorOverlapTest {

    @Mock
    private Field field;

    @Test
    void test001() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni"), "==");
        StringConditionInspector b = getCondition(new Values<>("Toni"), "!=");

        assertThat(a.overlaps(b)).isFalse();
        assertThat(b.overlaps(a)).isFalse();
    }

    @Test
    void test002() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni"), "==");
        StringConditionInspector b = getCondition(new Values<>("Eder"), "!=");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test003() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni",
                        "Michael",
                        "Eder"),
                "in");
        StringConditionInspector b = getCondition(new Values<>("Toni"), "!=");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test004() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni",
                        "Michael",
                        "Eder"),
                "in");
        StringConditionInspector b = getCondition(new Values<>("Toni"), "==");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test005() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni",
                        "Michael"),
                "in");
        StringConditionInspector b = getCondition(new Values<>("Eder"), "==");

        assertThat(a.overlaps(b)).isFalse();
        assertThat(b.overlaps(a)).isFalse();
    }

    @Test
    void test006() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni",
                        "Michael"),
                "in");
        StringConditionInspector b = getCondition(new Values<>("Eder"), "!=");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test007() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni",
                        "Michael"),
                "in");
        StringConditionInspector b = getCondition(new Values<>("Eder",
                        "John"),
                "in");

        assertThat(a.overlaps(b)).isFalse();
        assertThat(b.overlaps(a)).isFalse();
    }

    @Test
    void test008() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni",
                        "Michael"),
                "in");
        StringConditionInspector b = getCondition(new Values<>("Toni",
                        "Eder"),
                "in");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test009() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni"),
                "in");
        StringConditionInspector b = getCondition(new Values<>("Eder",
                        "Toni"),
                "in");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test010() throws Exception {
        StringConditionInspector a = getCondition(new Values<>(""), "==");
        StringConditionInspector b = getCondition(new Values<>(""), "==");

        assertThat(a.overlaps(b)).isFalse();
        assertThat(b.overlaps(a)).isFalse();
    }

    @Test
    void test011() throws Exception {
        StringConditionInspector a = getCondition(new Values<>("Toni"), "==");
        StringConditionInspector b = getCondition(new Values<>("Toni"), "==");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    private StringConditionInspector getCondition(final Values values, final String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new StringConditionInspector(fieldCondition(field, values, operator), configurationMock);
    }
}